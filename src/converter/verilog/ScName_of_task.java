package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  name_of_task  <br>
 *     ::=  IDENTIFIER  
 */
class ScName_of_task extends ScVerilog {
    public ScName_of_task(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNAME_OF_TASK);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
