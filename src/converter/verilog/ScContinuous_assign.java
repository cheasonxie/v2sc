package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  continuous_assign  <br>
 *     ::= <b>assign</b> [ drive_strength ] [ delay ]  list_of_assignments  ; <br>
 *     ||=  nettype  [ drive_strength ] [ expandrange ] [ delay ]  list_of_assignments  ; 
 */
class ScContinuous_assign extends ScVerilog {
    public ScContinuous_assign(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONTINUOUS_ASSIGN);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
