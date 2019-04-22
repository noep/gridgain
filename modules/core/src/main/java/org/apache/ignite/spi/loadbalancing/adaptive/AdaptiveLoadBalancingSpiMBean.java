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

package org.apache.ignite.spi.loadbalancing.adaptive;

import org.apache.ignite.mxbean.MXBeanDescription;
import org.apache.ignite.spi.IgniteSpiManagementMBean;

/**
 * Management MBean for {@link AdaptiveLoadBalancingSpi} SPI.
 */
@MXBeanDescription("MBean that provides access to adaptive load balancing SPI configuration.")
public interface AdaptiveLoadBalancingSpiMBean extends IgniteSpiManagementMBean {
    /**
     * Gets text description of current load probing implementation used.
     *
     * @return Text description of current load probing implementation used.
     */
    @MXBeanDescription("Text description of current load probing implementation used.")
    public String getLoadProbeFormatted();
}