package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  expandrange  <br>
 *     ::=  range  <br>
 *     ||= <b>scalared</b>  range  <br>
 *     ||= <b>vectored</b>  range  
 */
class ScExpandrange extends ScVerilog {
    public ScExpandrange(ASTNode node) {
        super(node);
        assert(node.getId() == ASTEXPANDRANGE);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
