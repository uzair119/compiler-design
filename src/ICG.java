import java.io.File;
import java.util.*;

public class ICG {
    ICG()
    {
        map.put(NodeType.nd_Add, "+");
        map.put(NodeType.nd_Sub, "-");
        map.put(NodeType.nd_Mul, "*");
        map.put(NodeType.nd_Div, "/");
        map.put(NodeType.nd_Mod, "%");
        map.put(NodeType.nd_Eql, "==");
        map.put(NodeType.nd_Assign, "=");
    }
    HashMap<NodeType, String> map = new HashMap<>();



    int register = 1;

    public void generateIntermediateCode(Node root) {
        if(root == null)
            return;
        if(root.nodeType == NodeType.nd_Integer || root.nodeType == NodeType.nd_String || root.nodeType == NodeType.nd_Ident)
        {
            root.interimCode = root.value;
            return;
        }
        generateIntermediateCode(root.left);
        generateIntermediateCode(root.right);
        if(root.nodeType == NodeType.nd_Sequence)
            return;
        if(root.nodeType == NodeType.nd_Assign)
        {
            System.out.println(root.left.interimCode + " = " + root.right.interimCode);
            return;
        }
        root.interimCode = "r" + register++;
        System.out.println(root.interimCode + " = " + root.left.interimCode + " " + map.get(root.nodeType) + " " + root.right.interimCode);
//        if((root.left.nodeType == NodeType.nd_Ident || root.left.nodeType == NodeType.nd_Integer || root.left.nodeType == NodeType.nd_String) &&
//                (root.right.nodeType == NodeType.nd_Ident || root.right.nodeType == NodeType.nd_Integer || root.right.nodeType == NodeType.nd_String))
//        {
//            root.interimCode = "r" + register++;
//            System.out.println(root.interimCode + " = " + root.left.value + " " + map.get(root.nodeType) + " " + root.right.value);
//        }
//        else if(root.left.interimCode != null)
    }


    public static void main(String args[])
    {
        try{
            String value, token;
            int line, pos;
            Parser.Token t;
            boolean found;
            List<Parser.Token> list = new ArrayList<>();
            Map<String, TokenType> str_to_tokens = new HashMap<>();

            str_to_tokens.put("End_of_input", TokenType.End_of_input);
            str_to_tokens.put("Op_multiply", TokenType.Op_multiply);
            str_to_tokens.put("Op_divide", TokenType.Op_divide);
            str_to_tokens.put("Op_mod", TokenType.Op_mod);
            str_to_tokens.put("Op_add", TokenType.Op_add);
            str_to_tokens.put("Op_subtract", TokenType.Op_subtract);
            str_to_tokens.put("Op_negate", TokenType.Op_negate);
            str_to_tokens.put("Op_not", TokenType.Op_not);
            str_to_tokens.put("Op_less", TokenType.Op_less);
            str_to_tokens.put("Op_lessequal", TokenType.Op_lessequal);
            str_to_tokens.put("Op_greater", TokenType.Op_greater);
            str_to_tokens.put("Op_greaterequal", TokenType.Op_greaterequal);
            str_to_tokens.put("Op_equal", TokenType.Op_equal);
            str_to_tokens.put("Op_notequal", TokenType.Op_notequal);
            str_to_tokens.put("Op_assign", TokenType.Op_assign);
            str_to_tokens.put("Op_and", TokenType.Op_and);
            str_to_tokens.put("Op_or", TokenType.Op_or);
            str_to_tokens.put("Keyword_if", TokenType.Keyword_if);
            str_to_tokens.put("Keyword_else", TokenType.Keyword_else);
            str_to_tokens.put("Keyword_while", TokenType.Keyword_while);
            str_to_tokens.put("Keyword_print", TokenType.Keyword_print);
            str_to_tokens.put("Keyword_endif", TokenType.Keyword_endif);
            str_to_tokens.put("Keyword_endwhile", TokenType.Keyword_endwhile);
            str_to_tokens.put("LeftParen", TokenType.LeftParen);
            str_to_tokens.put("RightParen", TokenType.RightParen);
            str_to_tokens.put("LeftBrace", TokenType.LeftBrace);
            str_to_tokens.put("RightBrace", TokenType.RightBrace);
            str_to_tokens.put("Newline", TokenType.Newline);
            str_to_tokens.put("Comma", TokenType.Comma);
            str_to_tokens.put("Identifier", TokenType.Identifier);
            str_to_tokens.put("Integer", TokenType.Integer);
            str_to_tokens.put("String", TokenType.String);

            Scanner s = new Scanner(new File("parser.txt"));
            String source = " ";
            while (s.hasNext()) {
                String str = s.nextLine();
                StringTokenizer st = new StringTokenizer(str);
                token = st.nextToken();
                value = "";
                while (st.hasMoreTokens()) {
                    value += st.nextToken() + " ";
                }
                found = false;
                if (str_to_tokens.containsKey(token)) {
                    found = true;
                    list.add(new Parser.Token(str_to_tokens.get(token), value));
                }
                if (found == false) {
                    throw new Exception("Token not found: '" + token + "'");
                }
            }
            Parser p = new Parser(list);
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
            Node root = p.parse();
            p.printAST(root);
            semanticAnalyzer.analyze(root);
            ICG icg = new ICG();
            icg.generateIntermediateCode(root);
        }
        catch(Exception e)
        {
            System.out.println(e + "errror");
        }
    }

}
