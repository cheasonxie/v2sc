package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  udp_instantiation  <br>
 *     ::=  name_of_udp  [ drive_strength ] [ delay ] <br>
 *      udp_instance  {, udp_instance } ; 
 */
class ScUdp_instantiation extends ScVerilog {
    public ScUdp_instantiation(ASTNode node) {
        super(node);
        assert(node.getId() == ASTUDP_INSTANTIATION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
