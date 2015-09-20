package LexicalAnalyzer;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

public class JavaLexicalAnalyzer {
    //----------------------------------------------------------------------------------------------------
    public enum TokenEnum {
        ABSTRACT("abstract"), ASSERT("assert"), BOOLEAN("boolean"), BREAK("break"), BYTE("byte"),
        CASE("case"), CATCH("catch"), CHAR("char"), CLASS("class"), CONST("const"),
        CONTINUE("continue"), DEFAULT("default"), DO("do"), DOUBLE("double"), ELSE("else"),
        ENUM("enum"), EXTENDS("extends"), FINAL("final"), FINALLY("finally"), FLOAT("float"),
        FOR("for"), GOTO("goto"), IF("if"), IMPLEMENTS("implements"), IMPORT("import"),
        INSTANCEOF("instanceof"), INT("int"), INTERFACE("interface"), LONG("long"), NATIVE("native"),
        NEW("new"), PACKAGE("package"), PRIVATE("private"), PROTECTED("protected"), PUBLIC("public"),
        RETURN("return"), STRICTFP("strictfp"), SHORT("short"), STATIC("static"), SUPER("super"),
        SWITCH("switch"), SYNCHRONIZED("synchronized"), THIS("this"), THROW("throw"), THROWS("throws"),
        TRANSIENT("transient"), TRY("try"), VOID("void"), VOLATILE("volatile"), WHILE("while");

        private String name;

        TokenEnum(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    //----------------------------------------------------------------------------------------------------
    private static String s_punctuations = "+-*/()[]{}!@#$%^&<>=|:;?~.,\'`\"";
    private Editable m_editable;
    private String m_data;
    private int m_len;
    private int m_pos;

    public JavaLexicalAnalyzer() {
    }

    public boolean analyze(Editable s) {
        m_editable = s;
        m_data = m_editable.toString() + '\0';
        m_len = s.length();
        m_pos = 0;

        while (!isEnd()) {
            if (!handleNonsense()) {
                return true;
            } else if (isDecimalNumber(m_data.charAt(m_pos))) {
                handleNumber();
            } else if (isLetterOrUnderline(m_data.charAt(m_pos))) {
                handleKeywordOrIdentifier();
            } else if (isPunctuation(m_data.charAt(m_pos))) {
                handlePunctuation();
            }
        }

        return true;
    }

    // handlers
    //----------------------------------------------------------------------------------------------------
    private boolean handleNonsense() {
        while (isNonsense(m_data.charAt(m_pos))) {
            ++m_pos;
        }

        // deal with the comment

        return true;
    }

    private boolean handleNumber() {
        int begin = m_pos;

        // Octal(begins with "0") or Hexadecimal(begins with "0x" or "0X") or float or double number
        if (m_data.charAt(m_pos) == '0') {
            if (m_data.charAt(m_pos + 1)  == 'x' || m_data.charAt(m_pos + 1)  == 'X') {
                m_pos += 2;
                while (isHexadecimalNumber(m_data.charAt(m_pos))) {
                    ++m_pos;
                }
            } else if (!isDot(m_data.charAt(m_pos + 1))) {
                ++m_pos;
                while (isOctalNumber(m_data.charAt(m_pos))) {
                    ++m_pos;
                }
            } else {
                // Float or Double number
                m_pos += 2;
                while (isDecimalNumber(m_data.charAt(m_pos))) {
                    ++m_pos;
                }
                if (m_data.charAt(m_pos) == 'f' || m_data.charAt(m_pos) == 'F') {
                    ++m_pos;
                }
            }
        } else {
            // Decimal number
            while (isDecimalNumber(m_data.charAt(m_pos))) {
                ++m_pos;
            }

            // Float or Double number
            if (isDot(m_data.charAt(m_pos))) {
                ++m_pos;
                while (isDecimalNumber(m_data.charAt(m_pos))) {
                    ++m_pos;
                }
                if (m_data.charAt(m_pos) == 'f' || m_data.charAt(m_pos) == 'F') {
                    ++m_pos;
                }
            }
        }

        renderNumber(begin, m_pos);
        return true;
    }

    private boolean handleKeywordOrIdentifier() {
        int begin = m_pos;

        // Get word
        while (isLetterOrUnderline(m_data.charAt(m_pos)))  {
            ++m_pos;
        }
        while (isLetterOrNumberOrUnderline(m_data.charAt(m_pos))) {
            ++m_pos;
        }

        // Match word
        String word = m_data.substring(begin, m_pos);
        for (TokenEnum token : TokenEnum.values()) {
            if (word.equals(token.toString())) {
                // Keyword
                renderKeyword(begin, m_pos);
                return true;
            }
        }

        // Identifier
        renderIdentifier(begin, m_pos);
        return true;
    }

    private boolean handlePunctuation() {
        int begin = m_pos;
        ++m_pos;

        switch (m_data.charAt(m_pos - 1)) {
            case '+':
                // "++" "+="
                if (m_data.charAt(m_pos) == '+' || m_data.charAt(m_pos) == '=') {
                    ++m_pos;
                }
                break;

            case '-':
                //"--" "-="
                if (m_data.charAt(m_pos) == '-' || m_data.charAt(m_pos) == '=') {
                    ++m_pos;
                }
                break;

            case '*':
                // "*="
                if (m_data.charAt(m_pos) == '=') {
                    ++m_pos;
                }
                break;

            case '/':
                // "/="
                if (m_data.charAt(m_pos) == '=') {
                    ++m_pos;
                }
                break;

            case '!':
                // "!="
                if (m_data.charAt(m_pos) == '=') {
                    ++m_pos;
                }
                break;

            case '@':
                break;

            case '&':
                // "&&" "&="
                if (m_data.charAt(m_pos) == '&' || m_data.charAt(m_pos) == '=') {
                    ++m_pos;
                }
                break;

            case '<':
                // "<=" "<<"
                if (m_data.charAt(m_pos) == '=' || m_data.charAt(m_pos) == '<') {
                    ++m_pos;
                }
                break;

            case '>':
                // ">=" ">>"
                if (m_data.charAt(m_pos) == '=' || m_data.charAt(m_pos) == '>') {
                    ++m_pos;
                }
                break;

            case '=':
                //"=="
                if (m_data.charAt(m_pos) == '=') {
                    ++m_pos;
                }
                break;

            case '|':
                // "||" "|="
                if (m_data.charAt(m_pos) == '|' || m_data.charAt(m_pos) == '=') {
                    ++m_pos;
                }
                break;

            case ':':
                break;

            case '\'':
                while (m_data.charAt(m_pos) != '\'' && m_data.charAt(m_pos) != '\0') {
                    if (m_data.charAt(m_pos) == '\\') {
                        ++m_pos;
                        if (m_data.charAt(m_pos) == '\'' || m_data.charAt(m_pos) == '\\') {
                            ++m_pos;
                        }
                    } else {
                        ++m_pos;
                    }
                }
                if (m_data.charAt(m_pos) == '\'') {
                    ++m_pos;
                }

                // Literal
                renderLiteral(begin, m_pos);
                return true;

            case '"':
                while (m_data.charAt(m_pos) != '"' && m_data.charAt(m_pos) != '\0') {
                    if (m_data.charAt(m_pos) == '\\') {
                        ++m_pos;
                        if (m_data.charAt(m_pos) == '"' || m_data.charAt(m_pos) == '\\') {
                            ++m_pos;
                        }
                    } else {
                        ++m_pos;
                    }
                }
                if (m_data.charAt(m_pos) == '"') {
                    ++m_pos;
                }

                // Literal
                renderLiteral(begin, m_pos);
                return true;

            default:
                break;
        }

        // Punctuation
        renderPunctuation(begin, m_pos);
        return true;
    }

    // Judgement functions
    //----------------------------------------------------------------------------------------------------
    private boolean isDecimalNumber(char input) {
        return '0' <= input && input <= '9';
    }

    private boolean isOctalNumber(char input) {
        return '0' <= input && input <= '7';
    }

    private boolean isHexadecimalNumber(char input) {
        return ('0' <= input && input <= '9') || ('a' <= input && input <= 'f') || ('A' <= input && input <= 'F');
    }

    private boolean isDot(char input)  {
        return input == '.';
    }

    private boolean isLetterOrUnderline(char input) {
        return (('a' <= input && input <= 'z') || ('A' <= input && input <= 'Z')) || input == '_';
    }

    private boolean isLetterOrNumberOrUnderline(char input) {
        return isDecimalNumber(input) || isLetterOrUnderline(input);
    }

    private boolean isNonsense(char input) {
        return input == ' ' || input == '\t' || input == '\n' || input == '\r';
    }

    private boolean isPunctuation(char input) {
        for (int i = 0; i < s_punctuations.length(); ++i) {
            if (s_punctuations.charAt(i) == input) {
                return true;
            }
        }
        return false;
    }

    private boolean isEnd() {
        return m_len <= m_pos;
    }

    // Render
    //----------------------------------------------------------------------------------------------------
    private void renderNumber(int begin, int end) {
        m_editable.setSpan(new ForegroundColorSpan(Color.BLUE), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void renderKeyword(int begin, int end) {
        m_editable.setSpan(new ForegroundColorSpan(Color.MAGENTA), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void renderIdentifier(int begin, int end) {
        m_editable.setSpan(new ForegroundColorSpan(Color.CYAN), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void renderLiteral(int begin, int end) {
        m_editable.setSpan(new ForegroundColorSpan(Color.BLACK), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void renderPunctuation(int begin, int end) {
        m_editable.setSpan(new ForegroundColorSpan(Color.DKGRAY), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
