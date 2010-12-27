package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  module_port_connection  <br>
 *     ::=  expression  <br>
 *     ||=  NULL  
 */
class ScModule_port_connection extends ScVerilog {
    public ScModule_port_connection(ASTNode node) {
        super(node);
        assert(node.getId() == ASTMODULE_PORT_CONNECTION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
