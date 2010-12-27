package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  name_of_instance  <br>
 *     ::=  IDENTIFIER [ range ] 
 */
class ScName_of_instance extends ScVerilog {
    public ScName_of_instance(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNAME_OF_INSTANCE);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
