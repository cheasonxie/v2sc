package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  list_of_assignments  <br>
 *     ::=  assignment  {, assignment } 
 */
class ScList_of_assignments extends ScVerilog {
    public ScList_of_assignments(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLIST_OF_ASSIGNMENTS);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
