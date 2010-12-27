package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  net_declaration  <br>
 *     ::=  nettype  [ expandrange ] [ delay ]  list_of_variables  ; <br>
 *     ||= <b>trireg</b> [ charge_strength ] [ expandrange ] [ delay ] list_of_variables  ; <br>
 *     ||=  nettype  [ drive_strength ] [ expandrange ] [ delay ]  list_of_assignments  ; 
 */
class ScNet_declaration extends ScVerilog {
    public ScNet_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNET_DECLARATION);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
