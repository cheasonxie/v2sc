package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  list_of_module_connections  <br>
 *     ::=  module_port_connection  {, module_port_connection } <br>
 *     ||=  named_port_connection  {, named_port_connection } 
 */
class ScList_of_module_connections extends ScVerilog {
    public ScList_of_module_connections(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLIST_OF_MODULE_CONNECTIONS);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
