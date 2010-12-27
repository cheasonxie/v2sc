package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  udp_instance  <br>
 *     ::= [ name_of_udp_instance ] (  terminal  {, terminal } ) 
 */
class ScUdp_instance extends ScVerilog {
    public ScUdp_instance(ASTNode node) {
        super(node);
        assert(node.getId() == ASTUDP_INSTANCE);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
