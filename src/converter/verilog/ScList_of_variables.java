package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  list_of_variables  <br>
 *     ::=  name_of_variable  {, name_of_variable } 
 */
class ScList_of_variables extends ScVerilog {
    public ScList_of_variables(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLIST_OF_VARIABLES);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
