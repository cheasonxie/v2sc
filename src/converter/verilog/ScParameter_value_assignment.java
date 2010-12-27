package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  parameter_value_assignment  <br>
 *     ::= # (  expression  {, expression } ) 
 */
class ScParameter_value_assignment extends ScVerilog {
    public ScParameter_value_assignment(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPARAMETER_VALUE_ASSIGNMENT);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
