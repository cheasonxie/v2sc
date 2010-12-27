package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  input_declaration  <br>
 *     ::= <b>input</b> [ range ]  list_of_variables  ; 
 */
class ScInput_declaration extends ScVerilog {
    public ScInput_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINPUT_DECLARATION);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
