package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  module_instantiation  <br>
 *     ::=  name_of_module  [ parameter_value_assignment ] <br>
 *          module_instance  {, module_instance } ; 
 */
class ScModule_instantiation extends ScVerilog {
    public ScModule_instantiation(ASTNode node) {
        super(node);
        assert(node.getId() == ASTMODULE_INSTANTIATION);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
