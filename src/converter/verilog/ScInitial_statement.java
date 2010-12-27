package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  initial_statement  <br>
 *     ::= <b>initial</b>  statement  
 */
class ScInitial_statement extends ScVerilog {
    public ScInitial_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINITIAL_STATEMENT);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
