package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  named_port_connection  <br>
 *     ::= . IDENTIFIER  ( [ expression ] ) 
 */
class ScNamed_port_connection extends ScVerilog {
    public ScNamed_port_connection(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNAMED_PORT_CONNECTION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
