package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  integer_declaration  <br>
 *     ::= <b>integer</b>  list_of_register_variables  ; 
 */
class ScInteger_declaration extends ScVerilog {
    public ScInteger_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINTEGER_DECLARATION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
