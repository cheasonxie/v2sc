package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  path_declaration  <br>
 *     ::=  path_description  =  path_delay_value  ; 
 */
class ScPath_declaration extends ScVerilog {
    public ScPath_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPATH_DECLARATION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
