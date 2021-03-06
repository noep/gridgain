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
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import javax.cache.Cache;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.yardstick.cache.model.Person;

import static org.yardstickframework.BenchmarkUtils.println;

/**
 * Ignite benchmark that performs put and query operations.
 */
public class IgniteSqlQueryPutBenchmark extends IgniteCacheAbstractBenchmark<Integer, Object> {
    /** */
    private AtomicInteger putCnt = new AtomicInteger();

    /** */
    private AtomicInteger qryCnt = new AtomicInteger();

    /** {@inheritDoc} */
    @Override public boolean test(Map<Object, Object> ctx) throws Exception {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        IgniteCache<Integer, Object> cache = cacheForOperation(true);

        if (rnd.nextBoolean()) {
            double salary = rnd.nextDouble() * args.range() * 1000;

            double maxSalary = salary + 1000;

            Collection<Cache.Entry<Integer, Object>> entries = executeQuery(salary, maxSalary);

            for (Cache.Entry<Integer, Object> entry : entries) {
                Person p = (Person)entry.getValue();

                if (p.getSalary() < salary || p.getSalary() > maxSalary)
                    throw new Exception("Invalid person retrieved [min=" + salary + ", max=" + maxSalary +
                            ", person=" + p + ']');
            }

            qryCnt.getAndIncrement();
        }
        else {
            int i = rnd.nextInt(args.range());

            cache.put(i, new Person(i, "firstName" + i, "lastName" + i, i * 1000));

            putCnt.getAndIncrement();
        }

        return true;
    }

    /** {@inheritDoc} */
    @Override public void onWarmupFinished() {
        super.onWarmupFinished();
    }

    /**
     * @param minSalary Min salary.
     * @param maxSalary Max salary.
     * @return Query result.
     * @throws Exception If failed.
     */
    private Collection<Cache.Entry<Integer, Object>> executeQuery(double minSalary, double maxSalary) throws Exception {
        SqlQuery qry = new SqlQuery(Person.class, "salary >= ? and salary <= ?");

        qry.setArgs(minSalary, maxSalary);

        return cache.query(qry).getAll();
    }

    /** {@inheritDoc} */
    @Override protected IgniteCache<Integer, Object> cache() {
        return ignite().cache("query");
    }

    /** {@inheritDoc} */
    @Override public void tearDown() throws Exception {
        println(cfg, "Finished sql query put benchmark [putCnt=" + putCnt.get() + ", qryCnt=" + qryCnt.get() + ']');

        super.tearDown();
    }
}
