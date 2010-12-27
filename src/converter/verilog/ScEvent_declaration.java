package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  event_declaration  <br>
 *     ::= <b>event</b>  name_of_event  {, name_of_event } ; 
 */
class ScEvent_declaration extends ScVerilog {
    public ScEvent_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTEVENT_DECLARATION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
