package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  real_declaration  <br>
 *     ::= <b>real</b>  list_of_variables  ; 
 */
class ScReal_declaration extends ScVerilog {
    public ScReal_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTREAL_DECLARATION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
