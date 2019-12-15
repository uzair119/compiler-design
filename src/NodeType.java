public enum NodeType {
    nd_None(""), nd_Ident("Identifier"), nd_String("String"), nd_Integer("Integer"), nd_Sequence("Sequence"), nd_If("If"),
    nd_Prtc("Prtc"), nd_Prts("Prts"), nd_Prti("Prti"), nd_While("While"), nd_Endif("Endif"), nd_Endwhile("Endwhile"),
    nd_Assign("Assign"), nd_Negate("Negate"), nd_Not("Not"), nd_Mul("Multiply"), nd_Div("Divide"), nd_Mod("Mod"), nd_Add("Add"),
    nd_Sub("Subtract"), nd_Lss("Less"), nd_Leq("LessEqual"),
    nd_Gtr("Greater"), nd_Geq("GreaterEqual"), nd_Eql("Equal"), nd_Neq("NotEqual"), nd_And("And"), nd_Or("Or");

    private final String name;

    NodeType(String name) {
        this.name = name;
    }

    @Override
    public String toString() { return this.name; }
}