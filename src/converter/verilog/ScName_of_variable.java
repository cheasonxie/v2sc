package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  name_of_variable  <br>
 *     ::=  IDENTIFIER  
 */
class ScName_of_variable extends ScVerilog {
    public ScName_of_variable(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNAME_OF_VARIABLE);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
