package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  statement_or_null  <br>
 *     ::=  statement  <br>
 *     ||= ; 
 */
class ScStatement_or_null extends ScVerilog {
    public ScStatement_or_null(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSTATEMENT_OR_NULL);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
