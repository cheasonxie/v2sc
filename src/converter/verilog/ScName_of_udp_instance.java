package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  name_of_udp_instance  <br>
 *     ::=  IDENTIFIER [ range ] 
 */
class ScName_of_udp_instance extends ScVerilog {
    public ScName_of_udp_instance(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNAME_OF_UDP_INSTANCE);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
