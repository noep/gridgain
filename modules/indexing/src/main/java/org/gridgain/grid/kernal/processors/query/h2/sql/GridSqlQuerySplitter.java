/* @java.file.header */

/*  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.gridgain.grid.kernal.processors.query.h2.sql;

import org.apache.ignite.*;
import org.gridgain.grid.kernal.processors.cache.query.*;
import org.gridgain.grid.util.typedef.*;

import java.sql.*;
import java.util.*;

import static org.gridgain.grid.kernal.processors.query.h2.sql.GridSqlFunctionType.*;

/**
 * Splits a single SQL query into two step map-reduce query.
 */
public class GridSqlQuerySplitter {
    /** */
    private static final String TABLE_PREFIX = "__T";

    /** */
    private static final String COLUMN_PREFIX = "__C";

    /**
     * @param idx Index of table.
     * @return Table name.
     */
    private static String table(int idx) {
        return TABLE_PREFIX + idx;
    }

    /**
     * @param idx Index of column.
     * @return Generated by index column alias.
     */
    private static String columnName(int idx) {
        return COLUMN_PREFIX + idx;
    }

    /**
     * @param conn Connection.
     * @param query Query.
     * @param params Parameters.
     * @return Two step query.
     */
    public static GridCacheTwoStepQuery split(Connection conn, String query, Object[] params) {
        GridSqlSelect srcQry = GridSqlQueryParser.parse(conn, query);

        if (srcQry.groups().isEmpty()) { // Simple case.
            String tbl0 = table(0);

            GridCacheTwoStepQuery res = new GridCacheTwoStepQuery("select * from " + tbl0);

            res.addMapQuery(tbl0, srcQry.getSQL(), params);

            return res;
        }

        // Split all select expressions into map-reduce parts.
        List<GridSqlElement> mapExps = new ArrayList<>(srcQry.allExpressions());

        GridSqlElement[] rdcExps = new GridSqlElement[srcQry.select().size()];

        for (int i = 0, len = mapExps.size(); i < len; i++)
            splitSelectExpression(mapExps, rdcExps, i);

        // Build map query.
        GridSqlSelect mapQry = srcQry.clone();

        mapQry.clearSelect();

        for (GridSqlElement exp : mapExps)
            mapQry.addSelectExpression(exp);

        mapQry.clearGroups();

        for (int col : srcQry.groupColumns())
            mapQry.addGroupExpression(column(((GridSqlAlias)mapExps.get(col)).alias()));

        // TODO sort support

        // Reduce query.
        GridSqlSelect rdcQry = new GridSqlSelect();

        for (GridSqlElement rdcExp : rdcExps)
            rdcQry.addSelectExpression(rdcExp);

        rdcQry.from(new GridSqlTable(null, table(0)));

        for (int col : srcQry.groupColumns())
            rdcQry.addGroupExpression(column(((GridSqlAlias)mapExps.get(col)).alias()));

        GridCacheTwoStepQuery res = new GridCacheTwoStepQuery(rdcQry.getSQL());

        res.addMapQuery(table(0), mapQry.getSQL(), params);

        return res;
    }

    /**
     * @param mapSelect Selects for map query.
     * @param rdcSelect Selects for reduce query.
     * @param idx Index.
     */
    private static void splitSelectExpression(List<GridSqlElement> mapSelect, GridSqlElement[] rdcSelect, int idx) {
        GridSqlElement el = mapSelect.get(idx);

        GridSqlAlias alias = null;

        if (el instanceof GridSqlAlias) { // Unwrap from alias.
            alias = (GridSqlAlias)el;
            el = alias.child();
        }

        if (el instanceof GridSqlAggregateFunction) {
            GridSqlAggregateFunction agg = (GridSqlAggregateFunction)el;

            switch (agg.type()) {
                case AVG: // Split AVG(x) into distributed SUM( AVG(x)*COUNT(x) )/SUM( COUNT(x) ).
                    //-- COUNT(x) map
                    GridSqlElement cntMap = aggregate(agg.distinct(), COUNT).addChild(agg.child()); // Add function argument.

                    // Add generated alias to COUNT(x).
                    // Using size as index since COUNT will be added as the last select element to the map query.
                    cntMap = alias(columnName(mapSelect.size()), cntMap);

                    mapSelect.add(cntMap);

                    //-- AVG(x) map
                    GridSqlElement avgMap = aggregate(agg.distinct(), AVG).addChild(agg.child()); // Add function argument.

                    // Add generated alias to AVG(x).
                    avgMap = alias(columnName(idx), avgMap);

                    mapSelect.set(idx, avgMap);

                    //-- SUM( AVG(x)*COUNT(x) )/SUM( COUNT(x) ) reduce
                    GridSqlElement sumUpRdc = aggregate(false, SUM).addChild(
                        op(GridSqlOperationType.MULTIPLY,
                            column(((GridSqlAlias)avgMap).alias()),
                            column(((GridSqlAlias)cntMap).alias())));

                    GridSqlElement sumDownRdc = aggregate(false, SUM).addChild(
                        column(((GridSqlAlias)cntMap).alias()));

                    GridSqlElement rdc = op(GridSqlOperationType.DIVIDE, sumUpRdc, sumDownRdc);

                    if (alias != null) // Add initial alias if it was set.
                        rdc = alias(alias.alias(), rdc);

                    rdcSelect[idx] = rdc;

                    break;

                case COUNT_ALL:
                case COUNT:
                case MAX:
                case MIN:
                case SUM:
                default:
                    throw new IgniteException("Unsupported aggregate: " + agg.type());
            }
        }
        else {
            if (alias == null) { // Generate alias if none.
                alias = alias(columnName(idx), mapSelect.get(idx));

                mapSelect.set(idx, alias);
            }

            if (idx < rdcSelect.length)
                rdcSelect[idx] = column(alias.alias());
        }
    }

    /**
     * @param distinct Distinct.
     * @param type Type.
     * @return Aggregate function.
     */
    private static GridSqlAggregateFunction aggregate(boolean distinct, GridSqlFunctionType type) {
        return new GridSqlAggregateFunction(distinct, type);
    }

    /**
     * @param name Column name.
     * @return Column.
     */
    private static GridSqlColumn column(String name) {
        return new GridSqlColumn(null, name, name);
    }

    /**
     * @param alias Alias.
     * @param child Child.
     * @return Alias.
     */
    private static GridSqlAlias alias(String alias, GridSqlElement child) {
        return new GridSqlAlias(alias, child);
    }

    /**
     * @param type Type.
     * @param left Left expression.
     * @param right Right expression.
     * @return Binary operator.
     */
    private static GridSqlOperation op(GridSqlOperationType type, GridSqlElement left, GridSqlElement right) {
        return new GridSqlOperation(type, left, right);
    }
}
