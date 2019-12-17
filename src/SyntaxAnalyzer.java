import entity.Token;
import utils.FileUtil;
import utils.LexAnalyzer;

import java.util.List;
import java.util.Stack;

public class SyntaxAnalyzer {

    private static String inFilePath = "test.txt";
    private static String lexOutFilePath = "resources/lex_result.txt";
    private static String synOutFilePath = "resources/syn_result.txt";

    // 产生式列表
    private static final String [] reductionList = {
            "S`-> S",
            "S -> if (C) S else S",
            "S -> if (C) S",
            "S -> w + w",
            "C -> C && C",
            "C -> w > w"
    };
    // 产生式右侧符号数
    private static final int[] reductionPopNum = {
            1,
            7,
            5,
            3,
            3,
            3
    };

    // ACTION 表
    private static final String [][] actionTable = {
            { "S2" , ""   , ""   , ""   , ""   , ""   , ""   , "S3" , ""   },
            { ""   , ""   , ""   , ""   , ""   , ""   , ""   , ""   , "r0" },
            { ""   , ""   , "S4" , ""   , ""   , ""   , ""   , ""   , ""   },
            { ""   , ""   , ""   , ""   , "S5" , ""   , ""   , ""   , ""   },
            { ""   , ""   , ""   , ""   , ""   , ""   , ""   , "S7" , ""   },
            { ""   , ""   , ""   , ""   , ""   , ""   , ""   , "S8" , ""   },
            { ""   , ""   , ""   , "S9" , ""   , ""   , "S10", ""   , ""   },
            { ""   , ""   , ""   , ""   , ""   , "S11", ""   , ""   , ""   },
            { ""   , ""   , ""   , ""   , ""   , ""   , ""   , ""   , "r3" },
            { "S13", ""   , ""   , ""   , ""   , ""   , ""   , "S14", ""   },
            { ""   , ""   , ""   , ""   , ""   , ""   , ""   , "S7" , ""   },
            { ""   , ""   , ""   , "r5" , ""   , ""   , "r5" , "S16", ""   },
            { ""   , "S17", ""   , ""   , ""   , ""   , ""   , ""   , "r2" },
            { ""   , ""   , ""   , "S18", ""   , ""   , ""   , ""   , ""   },
            { ""   , ""   , ""   , ""   , "S19", ""   , ""   , ""   , ""   },
            { ""   , ""   , ""   , "r4" , ""   , ""   , "r4" , ""   , ""   },
            { ""   , ""   , ""   , "r5" , ""   , ""   , "r5" , ""   , ""   },
            { "S2" , ""   , ""   , ""   , ""   , ""   , ""   , "S3" , ""   },
            { ""   , ""   , ""   , ""   , ""   , ""   , ""   , "S7" , ""   },
            { ""   , ""   , ""   , ""   , ""   , ""   , ""   , "S22", ""   },
            { ""   , ""   , ""   , ""   , ""   , ""   , ""   , ""   , "r1" },
            { ""   , ""   , ""   , "S23", ""   , ""   , "S10", ""   , ""   },
            { ""   , "r3" , ""   , ""   , ""   , ""   , ""   , ""   , "r3" },
            { "S13", ""   , ""   , ""   , ""   , ""   , ""   , "S14", ""   },
            { ""   , "S25", ""   , ""   , ""   , ""   , ""   , ""   , "r2" },
            { "S13", ""   , ""   , ""   , ""   , ""   , ""   , "S14", ""   },
            { ""   , "r1" , ""   , ""   , ""   , ""   , ""   , ""   , "r1" }
    };
    // GOTO 表
    private static final int [][] gotoTable = {
            { 1 , 0  },
            { 0 , 0  },
            { 0 , 0  },
            { 0 , 0  },
            { 0 , 6  },
            { 0 , 0  },
            { 0 , 0  },
            { 0 , 0  },
            { 0 , 0  },
            { 12, 0  },
            { 0 , 15 },
            { 0 , 0  },
            { 0 , 0  },
            { 0 , 0  },
            { 0 , 0  },
            { 0 , 0  },
            { 0 , 0  },
            { 20, 0  },
            { 0 , 21 },
            { 0 , 0  },
            { 0 , 0  },
            { 0 , 0  },
            { 0 , 0  },
            { 24, 0  },
            { 0 , 0  },
            { 26, 0  },
            { 0 , 0  },
    };

    // Token 栈
    private static Stack<Token> tokenStack = new Stack<>();
    // 状态栈
    private static Stack<Integer> stateStack = new Stack<>();

    public static void main(String[] args) {
        List<Token> tokens = LexAnalyzer.getTokenList(inFilePath);

        String lexRes = "";
        for (Token token : tokens) {
            lexRes += token.toString() + '\n';
        }
        FileUtil.outputAsFile(lexOutFilePath, lexRes);

        // 要记得在 Token 序列末尾加上 $
        tokens.add(new Token(8, "$"));
        stateStack.push(0);
        String synRes = parse(tokens);
        FileUtil.outputAsFile(synOutFilePath, synRes);
    }

    private static String parse(List<Token> tokens) {
        String res = "";
        int curIndex = 0;
        while (curIndex < tokens.size()) {
            Token curToken = tokens.get(curIndex);
            int curState = stateStack.peek();
            // 根据当前状态栈和符号栈的栈顶从 ACTION 表中得到表项
            String tableItem = actionTable[curState][curToken.getType()];
            // 分析表无对应表项表示错误
            if (tableItem.equals("")) {
                res += "parse error: Token(" + curToken.getContent() + ") cant match state(" + curState + ")";
                break;
            }
            char type = tableItem.charAt(0);
            int number = Integer.parseInt(tableItem.substring(1));
            // Sx 进行状态转移
            if (type == 'S') {
                tokenStack.push(curToken);
                stateStack.push(number);
            }
            // rx 进行规约
            else if (type == 'r') {
                // 0 号产生式为 accept, 表示解析完毕
                if (number == 0) {
                    break;
                }
                // 将规约式加入规约序列
                res += reductionList[number] + '\n';
                // 将产生式右部弹出
                for (int i = 0; i < reductionPopNum[number]; i++) {
                    tokenStack.pop();
                    stateStack.pop();
                }
                // 将产生式左部压栈
                char left = reductionList[number].charAt(0);
                // 为避免重复, 非终结符的状态码使用负数
                int code;
                if (left == 'S') {
                    code = -2;
                } else if (left == 'C') {
                    code = -1;
                } else {
                    res += "no such non-terminal: " + left;
                    break;
                }
                // 非终结符压栈
                tokenStack.push(new Token(code,left + ""));
                // 根据当前状态栈和符号栈的栈顶从 GOTO 表中得到下一状态
                int nextState = gotoTable[stateStack.peek()][code + 2];
                stateStack.push(nextState);
                // 指针位置保持不动
                continue;
            }
            curIndex++;
        }
        return res;
    }
}
