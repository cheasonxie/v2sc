package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  description  <br>
 *     ::=  module  <br>
 *     ||=  udp  
 */
class ScDescription extends ScVerilog {
    public ScDescription(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDESCRIPTION);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
