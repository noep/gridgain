/*
 * Copyright 2019 GridGain Systems, Inc. and Contributors.
 *
 * Licensed under the GridGain Community Edition License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.yardstick.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.yardstick.cache.model.Organization;
import org.apache.ignite.yardstick.cache.model.Person;
import org.yardstickframework.BenchmarkConfiguration;

import static org.yardstickframework.BenchmarkUtils.println;

/**
 * Ignite benchmark that performs query operations with joins.
 */
public class IgniteSqlQueryJoinBenchmark extends IgniteCacheAbstractBenchmark<Integer, Object> {
    /** {@inheritDoc} */
    @Override public void setUp(BenchmarkConfiguration cfg) throws Exception {
        super.setUp(cfg);

        println(cfg, "Populating query data...");

        loadCachesData();
    }

    /** {@inheritDoc} */
    @Override protected void loadCacheData(String cacheName) {
        if (args.range() < 100)
            throw new IllegalArgumentException("Invalid range: " + args.range());

        try (IgniteDataStreamer<Object, Object> dataLdr = ignite().dataStreamer(cacheName)) {
            final int orgRange = args.range() / 10;

            // Populate organizations.
            for (int i = 0; i < orgRange && !Thread.currentThread().isInterrupted(); i++)
                dataLdr.addData(i, new Organization(i, "org" + i));

            dataLdr.flush();

            // Populate persons.
            for (int i = orgRange; i < orgRange + args.range() && !Thread.currentThread().isInterrupted(); i++) {
                Person p = new Person(i, nextRandom(orgRange), "firstName" + i, "lastName" + i, (i - orgRange) * 1000);

                dataLdr.addData(i, p);

                if (i % 100000 == 0)
                    println(cfg, "Populated persons: " + i);
            }
        }
    }

    /** {@inheritDoc} */
    @Override public boolean test(Map<Object, Object> ctx) throws Exception {
        double salary = ThreadLocalRandom.current().nextDouble() * args.range() * 1000;

        double maxSalary = salary + 1000;

        Collection<List<?>> lists = executeQueryJoin(salary, maxSalary);

        for (List<?> l : lists) {
            double sal = (Double)l.get(4);

            if (sal < salary || sal > maxSalary) {
                Person p = new Person();

                p.setId((Integer)l.get(0));
                p.setOrganizationId((Integer)l.get(1));
                p.setFirstName((String)l.get(2));
                p.setLastName((String)l.get(3));
                p.setSalary(sal);

                throw new Exception("Invalid person retrieved [min=" + salary + ", max=" + maxSalary +
                    ", person=" + p + ']');
            }
        }

        return true;
    }

    /**
     * @param minSalary Min salary.
     * @param maxSalary Max salary.
     * @return Query results.
     * @throws Exception If failed.
     */
    private Collection<List<?>> executeQueryJoin(double minSalary, double maxSalary) throws Exception {
        IgniteCache<Integer, Object> cache = cacheForOperation(true);

        SqlFieldsQuery qry = new SqlFieldsQuery(
            "select p.id, p.orgId, p.firstName, p.lastName, p.salary, o.name " +
            "from Person p " +
            "left join Organization o " +
            "on p.id = o.id " +
            "where salary >= ? and salary <= ?");

        qry.setArgs(minSalary, maxSalary);

        return cache.query(qry).getAll();
    }

    /** {@inheritDoc} */
    @Override protected IgniteCache<Integer, Object> cache() {
        return ignite().cache("query");
    }
}
