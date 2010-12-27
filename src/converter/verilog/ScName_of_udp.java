package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  name_of_udp  <br>
 *     ::=  IDENTIFIER  
 */
class ScName_of_udp extends ScVerilog {
    public ScName_of_udp(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNAME_OF_UDP);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
