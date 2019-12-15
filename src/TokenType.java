public enum TokenType {
    End_of_input(false, false, false, -1, NodeType.nd_None),
    Op_multiply(false, true, false, 13, NodeType.nd_Mul),
    Op_divide(false, true, false, 13, NodeType.nd_Div),
    Op_mod(false, true, false, 13, NodeType.nd_Mod),
    Op_add(false, true, false, 12, NodeType.nd_Add),
    Op_subtract(false, true, false, 12, NodeType.nd_Sub),
    Op_negate(false, false, true, 14, NodeType.nd_Negate),
    Op_not(false, false, true, 14, NodeType.nd_Not),
    Op_less(false, true, false, 10, NodeType.nd_Lss),
    Op_lessequal(false, true, false, 10, NodeType.nd_Leq),
    Op_greater(false, true, false, 10, NodeType.nd_Gtr),
    Op_greaterequal(false, true, false, 10, NodeType.nd_Geq),
    Op_equal(false, true, true, 9, NodeType.nd_Eql),
    Op_notequal(false, true, false, 9, NodeType.nd_Neq),
    Op_assign(false, false, false, -1, NodeType.nd_Assign),
    Op_and(false, true, false, 5, NodeType.nd_And),
    Op_or(false, true, false, 4, NodeType.nd_Or),
    Keyword_if(false, false, false, -1, NodeType.nd_If),
    Keyword_else(false, false, false, -1, NodeType.nd_None),
    Keyword_while(false, false, false, -1, NodeType.nd_While),
    Keyword_print(false, false, false, -1, NodeType.nd_None),
    Keyword_endif(false, false, false, -1, NodeType.nd_Endif),
    Keyword_endwhile(false, false, false, -1, NodeType.nd_Endwhile),
    LeftParen(false, false, false, -1, NodeType.nd_None),
    RightParen(false, false, false, -1, NodeType.nd_None),
    LeftBrace(false, false, false, -1, NodeType.nd_None),
    RightBrace(false, false, false, -1, NodeType.nd_None),
    Newline(false, false, false, -1, NodeType.nd_None),
    Comma(false, false, false, -1, NodeType.nd_None),
    Identifier(false, false, false, -1, NodeType.nd_Ident),
    Integer(false, false, false, -1, NodeType.nd_Integer),
    String(false, false, false, -1, NodeType.nd_String);

    private final int precedence;
    private final boolean right_assoc;
    private final boolean is_binary;
    private final boolean is_unary;
    private final NodeType node_type;

    TokenType(boolean right_assoc, boolean is_binary, boolean is_unary, int precedence, NodeType node) {
        this.right_assoc = right_assoc;
        this.is_binary = is_binary;
        this.is_unary = is_unary;
        this.precedence = precedence;
        this.node_type = node;
    }
    boolean isRightAssoc() { return this.right_assoc; }
    boolean isBinary() { return this.is_binary; }
    boolean isUnary() { return this.is_unary; }
    int getPrecedence() { return this.precedence; }
    NodeType getNodeType() { return this.node_type; }
}