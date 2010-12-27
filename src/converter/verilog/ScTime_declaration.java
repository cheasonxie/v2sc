package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  time_declaration  <br>
 *     ::= <b>time</b>  list_of_register_variables  ; 
 */
class ScTime_declaration extends ScVerilog {
    public ScTime_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTIME_DECLARATION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
