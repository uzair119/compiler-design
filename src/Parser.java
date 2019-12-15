import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

class Parser {
    private List<Token> source;
    private Token token;
    private int position;

    static class Token {
        public TokenType tokentype;
        public String value;

        Token(TokenType token, String value) {
            this.tokentype = token; this.value = value;

        }
        @Override
        public String toString() {
            return this.tokentype + "   " + this.value;
        }
    }


    Parser(List<Token> source) {
        this.source = source;
        this.token = null;
    }
    Token getNextToken() {
        this.token = this.source.get(this.position++);
        return this.token;
    }
    Node expr(int p) {
        Node result = null, node;
        TokenType op;
        int q;

        if (this.token.tokentype == TokenType.LeftParen) {
            result = paren_expr();
        } else if (this.token.tokentype == TokenType.Op_add || this.token.tokentype == TokenType.Op_subtract) {
            op = (this.token.tokentype == TokenType.Op_subtract) ? TokenType.Op_negate : TokenType.Op_add;
            getNextToken();
            node = expr(TokenType.Op_negate.getPrecedence());
            result = (op == TokenType.Op_negate) ? Node.make_node(NodeType.nd_Negate, node) : node;
        } else if (this.token.tokentype == TokenType.Op_not) {
            getNextToken();
            result = Node.make_node(NodeType.nd_Not, expr(TokenType.Op_not.getPrecedence()));
        } else if (this.token.tokentype == TokenType.Identifier) {
            result = Node.make_leaf(NodeType.nd_Ident, this.token.value);
            getNextToken();
        } else if (this.token.tokentype == TokenType.Integer) {
            result = Node.make_leaf(NodeType.nd_Integer, this.token.value);
            getNextToken();
        }
        else if (this.token.tokentype == TokenType.String) {
            result = Node.make_leaf(NodeType.nd_String, this.token.value);
            getNextToken();
        }
        else {
            System.out.println("Error in expression");
        }

        while (this.token.tokentype.isBinary() && this.token.tokentype.getPrecedence() >= p) {
            op = this.token.tokentype;
            getNextToken();
            q = op.getPrecedence();
            if (!op.isRightAssoc()) {
                q++;
            }
            node = expr(q);
            result = Node.make_node(op.getNodeType(), result, node);
        }
        return result;
    }
    Node paren_expr() {
        expect(TokenType.LeftParen);
        Node node = expr(0);
        expect(TokenType.RightParen);
        return node;
    }
    void expect(TokenType s) {
        if (this.token.tokentype == s) {
            getNextToken();
            return;
        }
        System.out.println("Expecting" + s + " but found "+ this.token.tokentype);
    }
    Node stmt() {
        Node s, s2, t = null, e, v;
        if (this.token.tokentype == TokenType.Keyword_if) {
            getNextToken();
            e = expr(0);
            //s = stmt();

            s = null;
            getNextToken();
            while (this.token.tokentype != TokenType.Keyword_endif && this.token.tokentype != TokenType.Keyword_else && this.token.tokentype != TokenType.End_of_input) {
                s = Node.make_node(NodeType.nd_Sequence, s, stmt());
            }
            if(!(this.token.tokentype == TokenType.Keyword_endif || this.token.tokentype == TokenType.Keyword_else))
                System.out.println("error: expected 'else' or 'endif', found: " + this.token.tokentype);
//            expect("LBrace", TokenType.Keyword_endwhile);

            s2 = null;
            if (this.token.tokentype == TokenType.Keyword_else) {
                getNextToken();
//                s2 = stmt();

                s2 = null;
                getNextToken();
                while (this.token.tokentype != TokenType.Keyword_endif  && this.token.tokentype != TokenType.End_of_input) {
                    s2 = Node.make_node(NodeType.nd_Sequence, s2, stmt());
                }
            }
            expect(TokenType.Keyword_endif);
            getNextToken();
            t = Node.make_node(NodeType.nd_If, e, Node.make_node(NodeType.nd_If, s, s2));
        }  else if (this.token.tokentype == TokenType.Keyword_print) {
            getNextToken();
            expect(TokenType.LeftParen);
            while (true) {
                if (this.token.tokentype == TokenType.String) {
                    e = Node.make_node(NodeType.nd_Prts, Node.make_leaf(NodeType.nd_String, this.token.value));
                    getNextToken();
                } else {
                    e = Node.make_node(NodeType.nd_Prti, expr(0), null);
                }
                t = Node.make_node(NodeType.nd_Sequence, t, e);
                if (this.token.tokentype != TokenType.Comma) {
                    break;
                }
                getNextToken();
            }
            expect(TokenType.RightParen);
            expect(TokenType.Newline);
        } else if (this.token.tokentype == TokenType.Newline) {
            getNextToken();
        } else if (this.token.tokentype == TokenType.Identifier) {
            v = Node.make_leaf(NodeType.nd_Ident, this.token.value);
            getNextToken();
            expect(TokenType.Op_assign);
            e = expr(0);
            t = Node.make_node(NodeType.nd_Assign, v, e);
            expect(TokenType.Newline);
        } else if (this.token.tokentype == TokenType.Keyword_while) {
            getNextToken();
            e = expr(0);

            s = null;
            getNextToken();
            while (this.token.tokentype != TokenType.Keyword_endwhile && this.token.tokentype != TokenType.End_of_input) {
                s = Node.make_node(NodeType.nd_Sequence, s, stmt());
            }
            expect(TokenType.Keyword_endwhile);
            getNextToken();
            t = Node.make_node(NodeType.nd_While, e, s);
        }
//        else if (this.token.tokentype == TokenType.LeftBrace) {
//
//        }
        else if (this.token.tokentype == TokenType.End_of_input) {
        } else {
            System.out.println("No statement found");
        }
        return t;
    }
    Node parse() {
        Node t = null;
        this.position = 0;
        getNextToken();
        while (this.token.tokentype != TokenType.End_of_input) {
            t = Node.make_node(NodeType.nd_Sequence, t, stmt());
        }
        return t;
    }
    void printAST(Node t) {
        int i = 0;
        if (t == null) {
            System.out.println(";");
        } else {
            System.out.printf("%-14s", t.nodeType);
            if (t.nodeType == NodeType.nd_Ident || t.nodeType == NodeType.nd_Integer || t.nodeType == NodeType.nd_String) {
                System.out.println(" " + t.value);
            } else {
                System.out.println();
                printAST(t.left);
                printAST(t.right);
            }
        }
    }
    public static void main(String[] args) {
        try {
                String value, token;
                int line, pos;
                Token t;
                boolean found;
                List<Token> list = new ArrayList<>();
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
                        list.add(new Token(str_to_tokens.get(token), value));
                    }
                    if (found == false) {
                        throw new Exception("Token not found: '" + token + "'");
                    }
                }
                Parser p = new Parser(list);
                p.printAST(p.parse());
            } catch (FileNotFoundException e) {
            System.out.println(e);
            } catch (Exception e) {
            System.out.println(e);
            }

    }
}