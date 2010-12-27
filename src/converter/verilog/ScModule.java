package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  module  <br>
 *     ::= <b>module</b>  name_of_module  [ list_of_ports ] ; <br>
 *         { module_item } <br>
 *         <b>endmodule</b> <br>
 *     ||= <b>macromodule</b>  name_of_module  [ list_of_ports ] ; <br>
 *         { module_item } <br>
 *         <b>endmodule</b> 
 */
class ScModule extends ScVerilog {
    public ScModule(ASTNode node) {
        super(node);
        assert(node.getId() == ASTMODULE);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
