import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SemanticAnalyzer {

    private HashMap<String,NodeType> symbolTable = new HashMap<>();
    public void analyze (Node root)
    {
        if(root == null)
            return;
        if(root.nodeType == NodeType.nd_Ident)
        {
            if(!symbolTable.containsKey(root.value)) {
                System.out.println("Error: Uninitialized variable: " + root.value);
                System.exit(0);
            }
        }
        else if(root.nodeType == NodeType.nd_Assign)
        {
            Node left = root.left;
            Node right = root.right;

            //check for consistent type
            if(symbolTable.containsKey(left.value))
            {
                if(symbolTable.get(left.value) == NodeType.nd_Integer && !isIntegerOp(right.nodeType) && (!(right.nodeType == NodeType.nd_Integer)))
                    {
                        System.out.println("Error: trying to assign type non-integer to " + left.value + " which is of type " + symbolTable.get(left.value));
                        System.exit(-1);
                    }
                else if(symbolTable.get(left.value) == NodeType.nd_String && isIntegerOp(right.nodeType))
                {
                    System.out.println("Error: trying to assign type Integer to " + left.value + " which is of type " + symbolTable.get(left.value));
                    System.exit(-1);
                }
            }
            else if(right.nodeType == NodeType.nd_Integer || right.nodeType == NodeType.nd_String)
                symbolTable.put(left.value, right.nodeType);
            else if(right.nodeType == NodeType.nd_Ident)
            {
                if(symbolTable.containsKey(right.value))
                {
                    symbolTable.put(left.value, right.nodeType);
                }
                else
                {
                    System.out.println("Error: Uninitialized variable: " + right.value);
                    System.exit(0);
                }
            }
            else if(isIntegerOp(right.nodeType))
            {
                analyze(right);
                symbolTable.put(left.value,NodeType.nd_Integer);
            }

        }
        else if(isIntegerOp(root.nodeType))
        {
            analyze(root.left);
            analyze(root.right);
            if(root.left.nodeType == NodeType.nd_Ident)
            {
                if(symbolTable.get(root.left.value) != NodeType.nd_Integer)
                    System.out.println("Invalid operand of type " + symbolTable.get(root.left.value) + " on " + root.nodeType);
            }
            if(root.right.nodeType == NodeType.nd_Ident)
            {
                if(symbolTable.get(root.right.value) != NodeType.nd_Integer)
                    System.out.println("Invalid operand of type " + symbolTable.get(root.right.value) + " on " + root.nodeType);
            }
            if(root.left.nodeType == NodeType.nd_String|| root.right.nodeType == NodeType.nd_String)
            {
                System.out.println("Invalid operand(s) on " + root.nodeType);
                System.exit(-1);
            }
        }
        else
        {
            analyze(root.left);
            analyze(root.right);
        }

    }
    
    public boolean isIntegerOp(NodeType nodeType)
    {
        if( nodeType == NodeType.nd_Add || nodeType == NodeType.nd_Div || nodeType == NodeType.nd_Eql
                || nodeType == NodeType.nd_Mul || nodeType == NodeType.nd_Sub || nodeType == NodeType.nd_Geq
                || nodeType == NodeType.nd_Gtr || nodeType == NodeType.nd_Lss || nodeType == NodeType.nd_Mod || nodeType == NodeType.nd_Leq
                || nodeType == NodeType.nd_Neq)
            return true;
        return false;
    }

    public static void main(String[] args)
    {
    try

    {
        String value, token;
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
        //p.printAST();
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        Node root = p.parse();
        semanticAnalyzer.analyze(root);
    }







    catch(
    Exception e)

    {
        System.out.println(e + "errror");
    }
    }
}
