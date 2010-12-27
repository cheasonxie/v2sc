package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  blocking_assignment  <br>
 *     ::=  lvalue  =  expression  <br>
 *     ||=  lvalue  =  delay_or_event_control   expression  
 */
class ScBlocking_assignment extends ScVerilog {
    public ScBlocking_assignment(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBLOCKING_ASSIGNMENT);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
