package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  conditional_port_expression  <br>
 *     ::=  port_reference  <br>
 *     ||=  unary_operator  port_reference  <br>
 *     ||=  port_reference  binary_operator  port_reference  
 */
class ScConditional_port_expression extends ScVerilog {
    public ScConditional_port_expression(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONDITIONAL_PORT_EXPRESSION);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
