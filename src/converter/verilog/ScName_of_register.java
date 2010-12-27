package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  name_of_register  <br>
 *     ::=  IDENTIFIER  
 */
class ScName_of_register extends ScVerilog {
    public ScName_of_register(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNAME_OF_REGISTER);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
