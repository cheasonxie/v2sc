package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  charge_strength  <br>
 *     ::= ( <b>small</b> ) <br>
 *     ||= ( <b>medium</b> ) <br>
 *     ||= ( <b>large</b> ) 
 */
class ScCharge_strength extends ScVerilog {
    public ScCharge_strength(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCHARGE_STRENGTH);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
