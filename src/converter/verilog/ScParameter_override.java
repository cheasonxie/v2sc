package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  parameter_override  <br>
 *     ::= <b>defparam</b>  list_of_param_assignments  ; 
 */
class ScParameter_override extends ScVerilog {
    public ScParameter_override(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPARAMETER_OVERRIDE);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
