package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  specparam_declaration  <br>
 *     ::= <b>specparam</b>  list_of_param_assignments  ; 
 */
class ScSpecparam_declaration extends ScVerilog {
    public ScSpecparam_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSPECPARAM_DECLARATION);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
