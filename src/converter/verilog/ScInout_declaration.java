package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  inout_declaration  <br>
 *     ::= <b>inout</b> [ range ]  list_of_variables  ; 
 */
class ScInout_declaration extends ScVerilog {
    public ScInout_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINOUT_DECLARATION);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
