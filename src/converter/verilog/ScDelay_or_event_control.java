package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  delay_or_event_control  <br>
 *     ::=  delay_control  <br>
 *     ||=  event_control  <br>
 *     ||= <b>repeat</b> (  expression  )  event_control  
 */
class ScDelay_or_event_control extends ScVerilog {
    public ScDelay_or_event_control(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDELAY_OR_EVENT_CONTROL);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
