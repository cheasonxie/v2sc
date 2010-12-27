package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  non_blocking_assignment  <br>
 *     ::=  lvalue  <=  expression  <br>
 *     ||=  lvalue  <=  delay_or_event_control   expression  
 */
class ScNon_blocking_assignment extends ScVerilog {
    public ScNon_blocking_assignment(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNON_BLOCKING_ASSIGNMENT);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
