package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  constant_expression  <br>
 *     ::= expression  
 */
class ScConstant_expression extends ScVerilog {
    public ScConstant_expression(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONSTANT_EXPRESSION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
