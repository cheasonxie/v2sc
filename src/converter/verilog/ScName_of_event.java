package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  name_of_event  <br>
 *     ::=  IDENTIFIER  
 */
class ScName_of_event extends ScVerilog {
    public ScName_of_event(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNAME_OF_EVENT);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
