package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  parameter_declaration  <br>
 *     ::= <b>parameter</b>  list_of_param_assignments  ; 
 */
class ScParameter_declaration extends ScVerilog {
    public ScParameter_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPARAMETER_DECLARATION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
