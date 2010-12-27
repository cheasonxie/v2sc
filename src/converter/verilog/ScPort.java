package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  port  <br>
 *     ::= [ port_expression ] <br>
 *     ||= .  name_of_port  ( [ port_expression ] ) 
 */
class ScPort extends ScVerilog {
    public ScPort(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPORT);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
