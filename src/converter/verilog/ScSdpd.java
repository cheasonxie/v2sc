package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  sdpd  <br>
 *     ::= <b>if</b> (  sdpd_conditional_expression  )  path_description  =  path_delay_value ; 
 */
class ScSdpd extends ScVerilog {
    public ScSdpd(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSDPD);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
