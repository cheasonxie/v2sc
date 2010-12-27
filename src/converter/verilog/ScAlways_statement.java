package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  always_statement  <br>
 *     ::= <b>always</b>  statement  
 */
class ScAlways_statement extends ScVerilog {
    public ScAlways_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTALWAYS_STATEMENT);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
