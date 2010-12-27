package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  reg_declaration  <br>
 *     ::= <b>reg</b> [ range ]  list_of_register_variables  ; 
 */
class ScReg_declaration extends ScVerilog {
    public ScReg_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTREG_DECLARATION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
