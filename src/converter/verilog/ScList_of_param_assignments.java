package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  list_of_param_assignments  <br>
 *     ::= param_assignment <,{ param_assignment } 
 */
class ScList_of_param_assignments extends ScVerilog {
    public ScList_of_param_assignments(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLIST_OF_PARAM_ASSIGNMENTS);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
