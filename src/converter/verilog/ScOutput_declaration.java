package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  output_declaration  <br>
 *     ::= <b>output</b> [ range ]  list_of_variables  ; 
 */
class ScOutput_declaration extends ScVerilog {
    public ScOutput_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTOUTPUT_DECLARATION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
