package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  param_assignment  <br>
 *     ::= identifier  = <constant_expression> 
 */
class ScParam_assignment extends ScVerilog {
    public ScParam_assignment(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPARAM_ASSIGNMENT);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
