package utils;

import entity.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LexAnalyzer {

    // 保留字列表
    private static String[] reservedWords = {
            "if",
            "else"
    };
    private static ArrayList<String> reservedWordList = new ArrayList<>(Arrays.asList(reservedWords));

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
                if (c == '(') {
                    code = 2;
                } else if (c == ')') {
                    code = 3;
                } else if (c == '+') {
                    code = 4;
                } else if (c == '>') {
                    code = 5;
                } else if (c == '&') {
                    if (tmpP + 1 < content.length() && content.charAt(tmpP + 1) == '&') {
                        code = 6;
                        s += content.charAt(tmpP + 1);
                        col++;
                        tmpP++;
                    }
                } else if (isAlNum(c)) {
                    code = 7;
                    while (tmpP + 1 < content.length() && isAlNum(content.charAt(tmpP + 1))) {
                        s += content.charAt(tmpP + 1);
                        col++;
                        tmpP++;
                    }
                    int pos = findStr(s, reservedWordList);
                    if (pos != -1) {
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

    private static boolean isAlNum(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' &&  c <= 'Z') || (c >= '0' && c <= '9');
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
