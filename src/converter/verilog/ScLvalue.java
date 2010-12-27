package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  lvalue  <br>
 *     ::=  identifier  <br>
 *     ||=  identifier  <b>[</b> <expression> <b>]</b> <br>
 *     ||=  identifier  <b>[</b> <constant_expression> : <constant_expression> <b>]</b> <br>
 *     ||=  concatenation  
 */
class ScLvalue extends ScVerilog {
    public ScLvalue(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLVALUE);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
