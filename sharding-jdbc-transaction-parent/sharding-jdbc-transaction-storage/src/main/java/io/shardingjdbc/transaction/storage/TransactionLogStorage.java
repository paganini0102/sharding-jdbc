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

package io.shardingjdbc.transaction.storage;

import java.sql.Connection;
import java.util.List;

/**
 * Transaction log storage interface.
 * 
 * @author zhangliang
 */
public interface TransactionLogStorage {
    
    /**
     * 存储事务日志
     * Save transaction log.
     * 
     * @param transactionLog transaction log
     */
    void add(TransactionLog transactionLog);
    
    /**
     * 根据主键删除事务日志
     * Remove transaction log.
     * 
     * @param id transaction log id
     */
    void remove(String id);
    
    /**
     * 读取需要处理的事务日志
     * 
     * <p>需要处理的事务日志为: </p>
     * <p>1. 异步处理次数小于最大处理次数.</p>
     * <p>2. 异步处理的事务日志早于异步处理的间隔时间.</p>
     * 
     * Find eligible transaction logs.
     * 
     * <p>To be processed transaction logs: </p>
     * <p>1. retry times less than max retry times.</p>
     * <p>2. transaction log last retry timestamp interval early than last retry timestamp.</p>
     * 
     * @param size size of fetch transaction log
     * @param maxDeliveryTryTimes max delivery try times
     * @param maxDeliveryTryDelayMillis max delivery try delay millis
     * @return eligible transaction logs
     */
    List<TransactionLog> findEligibleTransactionLogs(int size, int maxDeliveryTryTimes, long maxDeliveryTryDelayMillis);
    
    /**
     * 增加事务日志异步重试次数
     * Increase asynchronized delivery try times.
     * 
     * @param id transaction log id 事务主键
     */
    void increaseAsyncDeliveryTryTimes(String id);
    
    /**
     * 处理事务数据
     * Process transaction logs.
     *
     * @param connection connection for business app     业务数据库连接
     * @param transactionLog transaction log             事务日志
     * @param maxDeliveryTryTimes max delivery try times 事务送达的最大尝试次数
     * @return process success or not
     */
    boolean processData(Connection connection, TransactionLog transactionLog, int maxDeliveryTryTimes);
}
