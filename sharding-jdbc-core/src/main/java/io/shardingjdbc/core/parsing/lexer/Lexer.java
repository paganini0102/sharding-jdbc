/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingjdbc.core.parsing.lexer;

import io.shardingjdbc.core.parsing.lexer.analyzer.CharType;
import io.shardingjdbc.core.parsing.lexer.analyzer.Dictionary;
import io.shardingjdbc.core.parsing.lexer.analyzer.Tokenizer;
import io.shardingjdbc.core.parsing.lexer.token.Assist;
import io.shardingjdbc.core.parsing.lexer.token.Token;
import io.shardingjdbc.core.parsing.parser.exception.SQLParsingException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Lexical analysis.
 * 
 * @author zhangliang 
 */
@RequiredArgsConstructor
public class Lexer {
    
	/** 输出字符串 */
    @Getter
    private final String input;
    /** 词法标记字典 */
    private final Dictionary dictionary;
    /** 解析到 SQL的offset */
    private int offset;
    /** 当前词法标记 */
    @Getter
    private Token currentToken;
    
    /**
     * 分析下一个词法标记
     * Analyse next token.
     */
    public final void nextToken() {
        skipIgnoredToken();
        if (isVariableBegin()) { // 变量
            currentToken = new Tokenizer(input, dictionary, offset).scanVariable();
        } else if (isNCharBegin()) { // N\
            currentToken = new Tokenizer(input, dictionary, ++offset).scanChars();
        } else if (isIdentifierBegin()) { // Keyword + Literals.IDENTIFIER
            currentToken = new Tokenizer(input, dictionary, offset).scanIdentifier();
        } else if (isHexDecimalBegin()) { // 十六进制
            currentToken = new Tokenizer(input, dictionary, offset).scanHexDecimal();
        } else if (isNumberBegin()) { // 数字（整数 + 浮点数）
            currentToken = new Tokenizer(input, dictionary, offset).scanNumber();
        } else if (isSymbolBegin()) { // 符号
            currentToken = new Tokenizer(input, dictionary, offset).scanSymbol();
        } else if (isCharsBegin()) { // 字符串，例如："abc"
            currentToken = new Tokenizer(input, dictionary, offset).scanChars();
        } else if (isEnd()) { // 结束
            currentToken = new Token(Assist.END, "", offset);
        } else { // 分析错误，无符合条件的词法标记
            throw new SQLParsingException(this, Assist.ERROR);
        }
        offset = currentToken.getEndPosition();
    }
    
    /**
     * 跳过忽略的词法标记
     * 1. 空格
     * 2. SQL Hint
     * 3. SQL 注释
     */
    private void skipIgnoredToken() {
    	// 空格
        offset = new Tokenizer(input, dictionary, offset).skipWhitespace();
        // SQL Hint
        while (isHintBegin()) {
            offset = new Tokenizer(input, dictionary, offset).skipHint();
            offset = new Tokenizer(input, dictionary, offset).skipWhitespace();
        }
        // SQL 注释
        while (isCommentBegin()) {
            offset = new Tokenizer(input, dictionary, offset).skipComment();
            offset = new Tokenizer(input, dictionary, offset).skipWhitespace();
        }
    }
    
    protected boolean isHintBegin() {
        return false;
    }
    
    protected boolean isCommentBegin() {
        char current = getCurrentChar(0);
        char next = getCurrentChar(1);
        return '/' == current && '/' == next || '-' == current && '-' == next || '/' == current && '*' == next;
    }
    
    /**
     * 是否是变量
     * MySQL与 SQL Server支持
     * 
     * @see Tokenizer#scanVariable()
     * @return
     */
    protected boolean isVariableBegin() {
        return false;
    }
    
    protected boolean isSupportNChars() {
        return false;
    }
    
    private boolean isNCharBegin() {
        return isSupportNChars() && 'N' == getCurrentChar(0) && '\'' == getCurrentChar(1);
    }
    
    private boolean isIdentifierBegin() {
        return isIdentifierBegin(getCurrentChar(0));
    }
    
    private boolean isIdentifierBegin(final char ch) {
        return CharType.isAlphabet(ch) || '`' == ch || '_' == ch || '$' == ch;
    }
    
    private boolean isHexDecimalBegin() {
        return '0' == getCurrentChar(0) && 'x' == getCurrentChar(1);
    }
    
    /**
     * 是否是数字
     * 
     * @return
     */
    private boolean isNumberBegin() {
        return CharType.isDigital(getCurrentChar(0)) || ('.' == getCurrentChar(0) && CharType.isDigital(getCurrentChar(1)) && !isIdentifierBegin(getCurrentChar(-1))
                || ('-' == getCurrentChar(0) && ('.' == getCurrentChar(0) || CharType.isDigital(getCurrentChar(1)))));
    }
    
    /**
     * 是否是符号
     * @see Tokenizer#scanSymbol()
     * @return
     */
    private boolean isSymbolBegin() {
        return CharType.isSymbol(getCurrentChar(0));
    }
    
    private boolean isCharsBegin() {
        return '\'' == getCurrentChar(0) || '\"' == getCurrentChar(0);
    }
    
    private boolean isEnd() {
        return offset >= input.length();
    }
    
    protected final char getCurrentChar(final int offset) {
        return this.offset + offset >= input.length() ? (char) CharType.EOI : input.charAt(this.offset + offset);
    }
}
