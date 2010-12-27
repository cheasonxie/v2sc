package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  module_instance  <br>
 *     ::=  name_of_instance  ( [ list_of_module_connections ] ) 
 */
class ScModule_instance extends ScVerilog {
    public ScModule_instance(ASTNode node) {
        super(node);
        assert(node.getId() == ASTMODULE_INSTANCE);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
