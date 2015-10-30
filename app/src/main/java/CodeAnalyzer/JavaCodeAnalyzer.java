package CodeAnalyzer;

import java.util.HashMap;
import java.util.Set;

import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;


public class JavaCodeAnalyzer {

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
    // Lexical Analysis
    static private HashMap<String, Boolean> s_keywords = new HashMap<>();
    static private HashMap<Character, Boolean> s_punctuations = new HashMap<>();
    // Syntax Analysis
    // int m_state = 0;
    // private HashMap<String, Boolean> m_functions = new HashMap<>();
    // private HashMap<String, Boolean> m_identifiers = new HashMap<>();
    // Code Data
    private Editable m_editable;
    private String m_data;
    private int m_len;
    private int m_pos;
    private HashMap<String, Integer> m_colors = new HashMap<>();

    //----------------------------------------------------------------------------------------------------
    public JavaCodeAnalyzer() {
        initialize();
    }

    //----------------------------------------------------------------------------------------------------
    private void initialize() {
        if (s_keywords.isEmpty()) {
            for (TokenEnum token : TokenEnum.values()) {
                s_keywords.put(token.toString(), true);
            }
        }
        if (s_punctuations.isEmpty()) {
            String punctuations = "+-*/()[]{}!@#$%^&<>=|:;?~.,\'`\"";
            for (int i = 0; i < punctuations.length(); ++i) {
                s_punctuations.put(punctuations.charAt(i), true);
            }
        }

        m_colors.put("numberColor", 0xFF6897BB);
        m_colors.put("keywordColor", 0xFFCC7832);
        m_colors.put("identifierColor", 0xFFD0D0FF);
        m_colors.put("literalColor", 0xFF6A8759);
        m_colors.put("operatorColor", 0xFFD0D0FF);
        m_colors.put("punctuationColor", 0xFFCC7832);
        m_colors.put("commentColor", 0xFF808080);
        m_colors.put("annotationColor", 0xFFBBB529);
    }

    //----------------------------------------------------------------------------------------------------
    public boolean analyze(Editable s) {
        m_editable = s;
        m_data = m_editable.toString() + '\0';
        m_len = m_editable.length();
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

    public boolean analyze() {
        if (m_editable != null) {
            m_data = m_editable.toString() + '\0';
            m_len = m_editable.length();
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

        return false;
    }

    //----------------------------------------------------------------------------------------------------
    public boolean setColors(Bundle colors) {
        Set<String> keySet = colors.keySet();
        for (String key : keySet) {
            m_colors.put(key, colors.getInt(key));
        }

        return true;
    }

    public Bundle getColors() {
        Bundle colors = new Bundle();
        Set<String> keySet = m_colors.keySet();
        for (String key : keySet) {
            colors.putInt(key, m_colors.get(key));
        }

        return colors;
    }

    // handlers
    //----------------------------------------------------------------------------------------------------
    private boolean handleNonsense() {
        while (isNonsense(m_data.charAt(m_pos))) {
            ++m_pos;
        }

        // Deal with comment
        int begin = m_pos;

        if (m_data.charAt(m_pos) == '/') {
            if (m_data.charAt(m_pos + 1) == '/') {
                m_pos += 2;
                while (m_data.charAt(m_pos) != '\n' && !isEnd()) {
                    ++m_pos;
                }
                if (m_data.charAt(m_pos) == '\n') {
                    ++m_pos;
                }
                handleNonsense();
            } else if (m_data.charAt(m_pos + 1) == '*') {
                m_pos += 2;
                while(!isEnd()) {
                    if (m_data.charAt(m_pos) == '*') {
                        ++m_pos;
                        if (m_data.charAt(m_pos) == '/') {
                            ++m_pos;
                            break;
                        } else if (isEnd()) {
                            break;
                        }
                    }
                    ++m_pos;
                }
                handleNonsense();
            }
        }

        if (begin != m_pos) {
            renderComment(begin, m_pos);
        }
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
        if (s_keywords.containsKey(m_data.substring(begin, m_pos))) {
            renderKeyword(begin, m_pos);
            return true;
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
                if (!handleNonsense()) {
                    return true;
                }
                while (isLetterOrUnderline(m_data.charAt(m_pos)))  {
                    ++m_pos;
                }
                while (isLetterOrNumberOrUnderline(m_data.charAt(m_pos))) {
                    ++m_pos;
                }
                renderAnnotation(begin, m_pos);
                return true;

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
                while (m_data.charAt(m_pos) != '\'' && !isEnd()) {
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
                while (m_data.charAt(m_pos) != '"' && !isEnd()) {
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

            case '.':
            case '{':
            case '}':
            case '(':
            case ')':
                break;

            default:
                renderPunctuation(begin, m_pos);
                return true;
        }

        // Punctuation
        renderOperator(begin, m_pos);
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
        return s_punctuations.containsKey(input);
    }

    private boolean isEnd() {
        return m_len <= m_pos;
    }

    // Render
    //----------------------------------------------------------------------------------------------------
    private void renderNumber(int begin, int end) {
        m_editable.setSpan(new ForegroundColorSpan(m_colors.get("numberColor")), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void renderKeyword(int begin, int end) {
        m_editable.setSpan(new ForegroundColorSpan(m_colors.get("keywordColor")), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void renderIdentifier(int begin, int end) {
        m_editable.setSpan(new ForegroundColorSpan(m_colors.get("identifierColor")), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void renderLiteral(int begin, int end) {
        m_editable.setSpan(new ForegroundColorSpan(m_colors.get("literalColor")), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void renderOperator(int begin, int end) {
        m_editable.setSpan(new ForegroundColorSpan(m_colors.get("operatorColor")), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void renderPunctuation(int begin, int end) {
        m_editable.setSpan(new ForegroundColorSpan(m_colors.get("punctuationColor")), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void renderComment(int begin, int end) {
        m_editable.setSpan(new ForegroundColorSpan(m_colors.get("commentColor")), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void renderAnnotation(int begin, int end) {
        m_editable.setSpan(new ForegroundColorSpan(m_colors.get("annotationColor")), begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
