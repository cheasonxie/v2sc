package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  register_variable  <br>
 *     ::=  name_of_register  <br>
 *     ||=  name_of_memory  <b>[</b>  constant_expression  :  constant_expression  <b>]</b> 
 */
class ScRegister_variable extends ScVerilog {
    public ScRegister_variable(ASTNode node) {
        super(node);
        assert(node.getId() == ASTREGISTER_VARIABLE);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
