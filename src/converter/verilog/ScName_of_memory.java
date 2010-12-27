package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  name_of_memory  <br>
 *     ::=  IDENTIFIER  
 */
class ScName_of_memory extends ScVerilog {
    public ScName_of_memory(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNAME_OF_MEMORY);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
