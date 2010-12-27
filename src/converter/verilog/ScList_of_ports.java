package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  list_of_ports  <br>
 *     ::= (  port  {, port } ) 
 */
class ScList_of_ports extends ScVerilog {
    public ScList_of_ports(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLIST_OF_PORTS);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
