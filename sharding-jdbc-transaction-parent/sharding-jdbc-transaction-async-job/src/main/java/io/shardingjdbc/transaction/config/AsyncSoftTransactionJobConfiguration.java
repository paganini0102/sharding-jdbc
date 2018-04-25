/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingjdbc.transaction.config;

import lombok.Getter;
import lombok.Setter;

/**
 * Asynchronized B.A.S.E transaction job configuration.
 *
 * @author caohao
 */
@Getter
@Setter
public class AsyncSoftTransactionJobConfiguration {
    
    /**
     * 作业名称
     * Job name.
     */
    private String name = "bestEffortsDeliveryJob";
    
    /**
     * 触发作业的cron表达式
     * Cron expression for trigger job.
     */
    private String cron = "0/5 * * * * ?";
    
    /**
     * 每次作业获取的事务日志最大数量
     * Transaction logs fetch data count.
     */
    private int transactionLogFetchDataCount = 100;
    
    /**
     * 事务送达的最大尝试次数
     * Max delivery try times.
     */
    private int maxDeliveryTryTimes = 3;
    
    /**
     * 执行事务的延迟毫秒数
     * Delay millis for asynchronized delivery.
     */
    private long maxDeliveryTryDelayMillis = 60  * 1000L;
}
