import entity.Token;
import utils.LexAnalyzer;

import java.util.List;

public class SyntaxAnalyzer {

    private static String inFilePath = "test.txt";

    public static void main(String[] args) {
        List<Token> tokens = LexAnalyzer.getTokenList(inFilePath);
        // TODO
    }
}
