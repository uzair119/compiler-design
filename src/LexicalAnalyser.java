import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LexicalAnalyser {

    private int position;
    private char chr;
    private String s;

    Map<String, TokenType> keywords = new HashMap<>();
    PrintWriter printWriter = new PrintWriter(new File("parser.txt"));
    static class Token {
        public TokenType tokentype;
        public String value;

        Token(TokenType token, String value) {
            this.tokentype = token;
            this.value = value;
        }
        @Override
        public String toString() {
            String result =  this.tokentype.toString();
            result += "   " + value;
            return result;
        }
    }

    static enum TokenType {
        End_of_input, Op_multiply,  Op_divide, Op_mod, Op_add, Op_subtract,
        Op_negate, Op_not, Op_less, Op_lessequal, Op_greater, Op_greaterequal,
        Op_equal, Op_notequal, Op_assign, Op_and, Op_or, Keyword_if,
        Keyword_else, Keyword_while, Keyword_print, Keyword_endif, Keyword_endwhile, Newline, LeftParen, RightParen,
        LeftBrace, RightBrace, Comma, Identifier, Integer, String
    }

    LexicalAnalyser(String source) throws FileNotFoundException {
        this.s = source;
        this.chr = this.s.charAt(0);
        this.keywords.put("if", TokenType.Keyword_if);
        this.keywords.put("else", TokenType.Keyword_else);
        this.keywords.put("print", TokenType.Keyword_print);
        this.keywords.put("while", TokenType.Keyword_while);
        this.keywords.put("endif", TokenType.Keyword_endif);
        this.keywords.put("endwhile", TokenType.Keyword_endwhile);

    }
    Token follow(char expect, TokenType ifyes, TokenType ifno) {
        if (getNextChar() == expect) {
            getNextChar();
            return new Token(ifyes, "");
        }
        if (ifno == TokenType.End_of_input) {
            System.out.println("Unrecognized token");
        }
        return new Token(ifno, "");
    }
    Token char_lit() {
        char c = getNextChar(); // skip opening quote
        int n = (int)c;
        if (c == '\'') {
            System.out.println("Empty char constant");
        } else if (c == '\\') {
            c = getNextChar();
            if (c == 'n') {
                n = 10;
            } else if (c == '\\') {
                n = '\\';
            } else {
                System.out.println("Unknown escape sequence");
            }
        }
        if (getNextChar() != '\'') {
            System.out.println("multi-character constant");
        }
        getNextChar();
        return new Token(TokenType.Integer, "" + n);
    }
    Token string_lit(char start) {
        String result = "";
        while (getNextChar() != start) {
            if (this.chr == '\u0000') {
                System.out.println("End of file while scanning string literal");
            }
            if (this.chr == '\n') {
                System.out.println("End of line while scanning string literal");
            }
            result += this.chr;
        }
        getNextChar();
        return new Token(TokenType.String, result);
    }
    Token div_or_comment() {
        if (getNextChar() != '*') {
            return new Token(TokenType.Op_divide, "");
        }
        getNextChar();
        while (true) {
            if (this.chr == '\u0000') {
                System.out.println("End of file while reading comment");
            } else if (this.chr == '*') {
                if (getNextChar() == '/') {
                    getNextChar();
                    return getToken();
                }
            } else {
                getNextChar();
            }
        }
    }
    Token identifier_or_integer() {
        boolean is_number = true;
        String text = "";

        while (Character.isAlphabetic(this.chr) || Character.isDigit(this.chr) || this.chr == '_') {
            text += this.chr;
            if (!Character.isDigit(this.chr)) {
                is_number = false;
            }
            getNextChar();
        }

        if (text.equals("")) {
            System.out.println("Error");
        }

        if (Character.isDigit(text.charAt(0))) {
            if (!is_number) {
                System.out.printf("invalid number: %s", text);
            }
            return new Token(TokenType.Integer, text);
        }

        if (this.keywords.containsKey(text)) {
            return new Token(this.keywords.get(text), "");
        }
        return new Token(TokenType.Identifier, text);
    }
    Token getToken() {
        while (Character.isWhitespace(this.chr) && this.chr != '\n') {
            getNextChar();
        }


        switch (this.chr) {
            case '\u0000': return new Token(TokenType.End_of_input, "");
            case '/': return div_or_comment();
            case '\'': return char_lit();
            case '<': return follow('=', TokenType.Op_lessequal, TokenType.Op_less);
            case '>': return follow('=', TokenType.Op_greaterequal, TokenType.Op_greater);
            case '=': return follow('=', TokenType.Op_equal, TokenType.Op_assign);
            case '!': return follow('=', TokenType.Op_notequal, TokenType.Op_not);
            case '&': return follow('&', TokenType.Op_and, TokenType.End_of_input);
            case '|': return follow('|', TokenType.Op_or, TokenType.End_of_input);
            case '"': return string_lit(this.chr);
            case '\n': getNextChar(); return new Token(TokenType.Newline,"");
            case '(': getNextChar(); return new Token(TokenType.LeftParen, "");
            case ')': getNextChar(); return new Token(TokenType.RightParen, "");
            case '+': getNextChar(); return new Token(TokenType.Op_add, "");
            case '-': getNextChar(); return new Token(TokenType.Op_subtract, "");
            case '*': getNextChar(); return new Token(TokenType.Op_multiply, "");
            case '%': getNextChar(); return new Token(TokenType.Op_mod, "");
            case ',': getNextChar(); return new Token(TokenType.Comma, "");

            default: return identifier_or_integer();
        }
    }

    char getNextChar() {

        this.position++;
        if (this.position >= this.s.length()) {
            this.chr = '\u0000';
            return this.chr;
        }
        this.chr = this.s.charAt(this.position);
//        if (this.chr == '\n') {
//            this.line++;
//            this.pos = 0;
//            System.out.println("new line");
//        }
        return this.chr;
    }

    void printTokens() {
        Token t;
        while ((t = getToken()).tokentype != TokenType.End_of_input) {
            System.out.println(t);
            printWriter.println(t.toString());
        }
        printWriter.println(t.toString());
        printWriter.close();
        System.out.println(t);
    }
    public static void main(String[] args) throws FileNotFoundException {
                File f = new File("file.txt");
                Scanner s = new Scanner(f);
                String source = " ";
                while (s.hasNext()) {
                    source += s.nextLine() + "\n";
                }
                System.out.println(source);
                LexicalAnalyser l = new LexicalAnalyser(source);
                l.printTokens();

    }
}
 