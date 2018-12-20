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

package io.shardingjdbc.core.parsing.parser.dialect.mysql.sql;

import io.shardingjdbc.core.rule.ShardingRule;
import io.shardingjdbc.core.parsing.lexer.LexerEngine;
import io.shardingjdbc.core.parsing.parser.dialect.mysql.clause.MySQLLimitClauseParser;
import io.shardingjdbc.core.parsing.parser.dialect.mysql.clause.facade.MySQLSelectClauseParserFacade;
import io.shardingjdbc.core.parsing.parser.dialect.mysql.clause.MySQLSelectOptionClauseParser;
import io.shardingjdbc.core.parsing.parser.sql.dql.select.AbstractSelectParser;
import io.shardingjdbc.core.parsing.parser.sql.dql.select.SelectStatement;

/**
 * Select parser for MySQL.
 *
 * @author zhangliang
 */
public final class MySQLSelectParser extends AbstractSelectParser {
    
    private final MySQLSelectOptionClauseParser selectOptionClauseParser;
    
    private final MySQLLimitClauseParser limitClauseParser;
    
    public MySQLSelectParser(final ShardingRule shardingRule, final LexerEngine lexerEngine) {
        super(shardingRule, lexerEngine, new MySQLSelectClauseParserFacade(shardingRule, lexerEngine));
        selectOptionClauseParser = new MySQLSelectOptionClauseParser(lexerEngine);
        limitClauseParser = new MySQLLimitClauseParser(lexerEngine);
    }
    
    @Override
    protected void parseInternal(final SelectStatement selectStatement) {
        parseDistinct(); // 解析DISTINCT、DISTINCTROW、UNION谓语
        parseSelectOption();
        parseSelectList(selectStatement, getItems()); // 解析查询字段
        parseFrom(selectStatement); // 解析表（JOIN ON/FROM单&多表）
        parseWhere(getShardingRule(), selectStatement, getItems()); // 解析WHERE条件
        parseGroupBy(selectStatement); // 解析Group By条件
        parseHaving(); // 解析Having条件
        parseOrderBy(selectStatement); // 解析Order By条件
        parseLimit(selectStatement); // 解析分页Limit条件
        parseSelectRest();
    }
    
    private void parseSelectOption() {
        selectOptionClauseParser.parse();
    }
    
    private void parseLimit(final SelectStatement selectStatement) {
        limitClauseParser.parse(selectStatement);
    }
}
