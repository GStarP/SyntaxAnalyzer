package utils;

import entity.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LexAnalyzer {

    // 保留字列表
    private static String[] reservedWords = {
            "import",
            "public",
            "private",
            "class",
            "int",
            "char",
            "String",
            "void",
            "return",
            "if",
            "else",
            "for",
            "break",
            "while",
            "static"
    };
    private static ArrayList<String> reservedWordList = new ArrayList<>(Arrays.asList(reservedWords));
    // 类别码总数
    private static final int codeSum = 46;

    public static List<Token> getTokenList(String filePath) {
        String content = FileUtil.readFile(filePath);
        return scan(content);
    }

    private static List<Token> scan(String content) {
        List<Token> res = new ArrayList<>();
        int row = 1;  // 当前行数
        int col = 1;  // 当前列数
        int p = 0;    // 当前位指针

        while (p < content.length()) {
            char c = content.charAt(p);

            if (c == '\n') {
                // 换行
                row++;
                col = 1;
                p++;
            } else if (c == ' ' || c == '\t') {
                // 无意义的空格和 Tab
                col++;
                p++;
            } else {
                int tmpP = p;
                int code = -1;
                String s = c + "";
                col++;
                /* 首先确定那些可以通过一位字符唯一确定的符号 */
                if (c == '*') {
                    code = 17;
                } else if (c == '/') {
                    code = 18;
                } else if (c == '(') {
                    code = 32;
                } else if (c == ')') {
                    code = 33;
                } else if (c == '[') {
                    code = 34;
                } else if (c == ']') {
                    code = 35;
                } else if (c == '{') {
                    code = 36;
                } else if (c == '}') {
                    code = 37;
                } else if (c == '.') {
                    code = 39;
                } else if (c == ':') {
                    code = 40;
                } else if (c == ';') {
                    code = 41;
                } else if (c == '\'') {
                    code = 42;
                } else if (c == '"') {
                    code = 43;
                } else if (c == '+') {
                    if (tmpP == content.length() - 1) {
                        code = 15;
                        Token token = new Token(code, s);
                        res.add(token);
                        return res;
                    } else {
                        char nextChar = content.charAt(tmpP + 1);
                        if (nextChar == '+') {
                            // ++
                            code = 21;
                            s += nextChar;
                            col++;
                            tmpP++;
                        } else if (nextChar == '=') {
                            // +=
                            code = 19;
                            s += nextChar;
                            col++;
                            tmpP++;
                        } else {
                            // +
                            code = 15;
                        }
                    }
                } else if (c == '-') {
                    if (tmpP == content.length() - 1) {
                        code = 16;
                        Token token = new Token(code, s);
                        res.add(token);
                        return res;
                    } else {
                        char nextChar = content.charAt(tmpP + 1);
                        if (nextChar == '-') {
                            // --
                            code = 22;
                            s += nextChar;
                            col++;
                            tmpP++;
                        } else if (nextChar == '=') {
                            // -=
                            code = 20;
                            s += nextChar;
                            col++;
                            tmpP++;
                        } else {
                            // -
                            code = 16;
                        }
                    }
                } else if (c == '!') {
                    if (tmpP == content.length() - 1) {
                        code = 38;
                        Token token = new Token(code, s);
                        res.add(token);
                        return res;
                    } else {
                        char nextChar = content.charAt(tmpP + 1);
                        if (nextChar == '=') {
                            // !=
                            code = 24;
                            s += nextChar;
                            col++;
                            tmpP++;
                        } else {
                            // !
                            code = 38;
                        }
                    }
                } else if (c == '=') {
                    if (tmpP == content.length() - 1) {
                        code = 29;
                        Token token = new Token(code, s);
                        res.add(token);
                        return res;
                    } else {
                        char nextChar = content.charAt(tmpP + 1);
                        if (nextChar == '=') {
                            // ==
                            code = 23;
                            s += nextChar;
                            col++;
                            tmpP++;
                        } else {
                            // =
                            code = 29;
                        }
                    }
                } else if (c == '>') {
                    if (tmpP == content.length() - 1) {
                        code = 25;
                        Token token = new Token(code, s);
                        res.add(token);
                        return res;
                    } else {
                        char nextChar = content.charAt(tmpP + 1);
                        if (nextChar == '=') {
                            // >=
                            code = 27;
                            s += nextChar;
                            col++;
                            tmpP++;
                        } else {
                            // >
                            code = 25;
                        }
                    }
                } else if (c == '<') {
                    if (tmpP == content.length() - 1) {
                        code = 26;
                        Token token = new Token(code, s);
                        res.add(token);
                        return res;
                    } else {
                        char nextChar = content.charAt(tmpP + 1);
                        if (nextChar == '=') {
                            // <=
                            code = 28;
                            s += nextChar;
                            col++;
                            tmpP++;
                        } else {
                            // <
                            code = 26;
                        }
                    }
                } else if (isNum(c)) {
                    // Num
                    code = 44;
                    while (tmpP + 1 < content.length() && isNum(content.charAt(tmpP + 1))) {
                        s += content.charAt(tmpP + 1);
                        col++;
                        tmpP++;
                    }
                } else if (isAlpha(c)) {
                    while (tmpP + 1 < content.length() && isAlpha(content.charAt(tmpP + 1))) {
                        s += content.charAt(tmpP + 1);
                        col++;
                        tmpP++;
                    }
                    int pos = findStr(s, reservedWordList);
                    if (pos == -1) {
                        code = 45;
                    } else {
                        code = pos;
                    }
                }

                if (code != -1) {
                    Token token = new Token(code, s);
                    res.add(token);
                } else {
                    System.out.println("parse error: row " + row + ", col " + col + ", invalid syntax " + s + '\n');
                }

                tmpP++;
                p = tmpP;
            }
        }

        return res;
    }

    private static boolean isNum(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' &&  c <= 'Z');
    }

    private static int findStr(String s, ArrayList<String> list) {
        for (int i = 0; i < list.size(); i++) {
            if (s.equals(list.get(i))) {
                return i;
            }
        }
        return -1;
    }
}
