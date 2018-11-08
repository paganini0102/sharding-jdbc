package io.shardingjdbc.core.parsing.parser.clause;

import io.shardingjdbc.core.parsing.lexer.token.DefaultKeyword;
import io.shardingjdbc.core.parsing.lexer.token.Literals;
import io.shardingjdbc.core.parsing.lexer.token.Symbol;
import io.shardingjdbc.core.parsing.lexer.LexerEngine;
import io.shardingjdbc.core.util.SQLUtil;
import com.google.common.base.Optional;
import lombok.RequiredArgsConstructor;

/**
 * Alias clause parser.
 *
 * @author zhangliang
 */
@RequiredArgsConstructor
public final class AliasClauseParser implements SQLClauseParser {
    
    private final LexerEngine lexerEngine;
    
    /**
     * 解析别名（字段、表）
     * Parse alias.
     *
     * @return alias
     */
    public Optional<String> parse() {
    	// 解析带AS情况
        if (lexerEngine.skipIfEqual(DefaultKeyword.AS)) {
            if (lexerEngine.equalAny(Symbol.values())) {
                return Optional.absent();
            }
            String result = SQLUtil.getExactlyValue(lexerEngine.getCurrentToken().getLiterals());
            lexerEngine.nextToken();
            return Optional.of(result);
        }
        // 解析别名
        if (lexerEngine.equalAny(
                Literals.IDENTIFIER, Literals.CHARS, DefaultKeyword.USER, DefaultKeyword.END, DefaultKeyword.CASE, DefaultKeyword.KEY, DefaultKeyword.INTERVAL, DefaultKeyword.CONSTRAINT)) {
            String result = SQLUtil.getExactlyValue(lexerEngine.getCurrentToken().getLiterals());
            lexerEngine.nextToken();
            return Optional.of(result);
        }
        return Optional.absent();
    }
}
