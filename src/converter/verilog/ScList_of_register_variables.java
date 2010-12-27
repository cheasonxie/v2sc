package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  list_of_register_variables  <br>
 *     ::=  register_variable  {, register_variable } 
 */
class ScList_of_register_variables extends ScVerilog {
    public ScList_of_register_variables(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLIST_OF_REGISTER_VARIABLES);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
