/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.rocketmq.client.latency;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.rocketmq.client.common.ThreadLocalIndex;

/**
 * 延迟故障容错实现。维护每个对象的信息
 */
public class LatencyFaultToleranceImpl implements LatencyFaultTolerance<String> {
    /**
     * key : brokerName
     * 对象故障信息Table
     */
    private final ConcurrentHashMap<String, FaultItem> faultItemTable = new ConcurrentHashMap<String, FaultItem>(16);

    /**
     * 对象选择Index
     */
    private final ThreadLocalIndex whichItemWorst = new ThreadLocalIndex();

    /**
     * 更新维护的brokerName 项信息
     *
     * 当 Producer 发送消息时间过长，则逻辑认为N秒内不可用。
     * 按照latencyMax，notAvailableDuration的配置
     * @param name
     * @param currentLatency
     * @param notAvailableDuration
     */
    @Override
    public void updateFaultItem(final String name, final long currentLatency, final long notAvailableDuration) {
        FaultItem old = this.faultItemTable.get(name);
        if (null == old) {
            // 创建对象
            final FaultItem faultItem = new FaultItem(name);
            faultItem.setCurrentLatency(currentLatency);
            faultItem.setStartTimestamp(System.currentTimeMillis() + notAvailableDuration);
            // 更新对象
            old = this.faultItemTable.putIfAbsent(name, faultItem);
            if (old != null) {
                old.setCurrentLatency(currentLatency);
                old.setStartTimestamp(System.currentTimeMillis() + notAvailableDuration);
            }
        } else {
            // 更新对象
            old.setCurrentLatency(currentLatency);
            old.setStartTimestamp(System.currentTimeMillis() + notAvailableDuration);
        }
    }

    @Override
    public boolean isAvailable(final String name) {
        final FaultItem faultItem = this.faultItemTable.get(name);
        if (faultItem != null) {
            return faultItem.isAvailable();
        }
        return true;
    }

    @Override
    public void remove(final String name) {
        this.faultItemTable.remove(name);
    }

    @Override
    public String pickOneAtLeast() {
        // 创建可用broker数组
        final Enumeration<FaultItem> elements = this.faultItemTable.elements();
        List<FaultItem> tmpList = new LinkedList<FaultItem>();
        while (elements.hasMoreElements()) {
            final FaultItem faultItem = elements.nextElement();
            tmpList.add(faultItem);
        }

        if (!tmpList.isEmpty()) {
            // 打乱 并 排序
            Collections.shuffle(tmpList);
            // 根据Fault升序排序
            Collections.sort(tmpList);

            // 选择前一半对象
            final int half = tmpList.size() / 2;
            if (half <= 0) {
                return tmpList.get(0).getName();
            } else {
                final int i = this.whichItemWorst.getAndIncrement() % half;
                return tmpList.get(i).getName();
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "LatencyFaultToleranceImpl{" +
            "faultItemTable=" + faultItemTable +
            ", whichItemWorst=" + whichItemWorst +
            '}';
    }

    /**
     * 对象故障信息。维护对象的名字、延迟、开始可用的时间。
     */
    class FaultItem implements Comparable<FaultItem> {
        /**
         * brokerName
         */
        private final String name;
        /**
         * 当前延时
         */
        private volatile long currentLatency;
        /**
         * 开始可用时间
         */
        private volatile long startTimestamp;

        public FaultItem(final String name) {
            this.name = name;
        }

        /**
         * 比较优先级：可用性 > 延迟 > 开始可用时间
         * @param other
         * @return
         */
        @Override
        public int compareTo(final FaultItem other) {
            /**
             * 可用性
             *
             */
            if (this.isAvailable() != other.isAvailable()) {
                if (this.isAvailable())
                    return -1;

                if (other.isAvailable())
                    return 1;
            }

            /**
             * 延迟
             * 延迟小的排在前面
             */
            if (this.currentLatency < other.currentLatency)
                return -1;
            else if (this.currentLatency > other.currentLatency) {
                return 1;
            }

            /**
             * 开始可用性
             * 开始可用时间小的排在前面
             */
            if (this.startTimestamp < other.startTimestamp)
                return -1;
            else if (this.startTimestamp > other.startTimestamp) {
                return 1;
            }

            return 0;
        }

        /**
         * 是否可用：当前时间 大于可用时间
         * @return
         */
        public boolean isAvailable() {
            return (System.currentTimeMillis() - startTimestamp) >= 0;
        }

        @Override
        public int hashCode() {
            int result = getName() != null ? getName().hashCode() : 0;
            result = 31 * result + (int) (getCurrentLatency() ^ (getCurrentLatency() >>> 32));
            result = 31 * result + (int) (getStartTimestamp() ^ (getStartTimestamp() >>> 32));
            return result;
        }

        /**
         * 满足三个条件才相等
         * @param o
         * @return
         */
        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (!(o instanceof FaultItem))
                return false;

            final FaultItem faultItem = (FaultItem) o;
            /**
             * 当前延迟时间一样
             */
            if (getCurrentLatency() != faultItem.getCurrentLatency())
                return false;
            /**
             * 开始可用时间一样
             */
            if (getStartTimestamp() != faultItem.getStartTimestamp())
                return false;
            /**
             * brokerName 一样
             */
            return getName() != null ? getName().equals(faultItem.getName()) : faultItem.getName() == null;

        }

        @Override
        public String toString() {
            return "FaultItem{" +
                "name='" + name + '\'' +
                ", currentLatency=" + currentLatency +
                ", startTimestamp=" + startTimestamp +
                '}';
        }

        public String getName() {
            return name;
        }

        public long getCurrentLatency() {
            return currentLatency;
        }

        public void setCurrentLatency(final long currentLatency) {
            this.currentLatency = currentLatency;
        }

        public long getStartTimestamp() {
            return startTimestamp;
        }

        public void setStartTimestamp(final long startTimestamp) {
            this.startTimestamp = startTimestamp;
        }

    }
}
