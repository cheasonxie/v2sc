package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  concatenation  <br>
 *     ::= <b>{</b>  expression  {, expression } <b>}</b> 
 */
class ScConcatenation extends ScVerilog {
    public ScConcatenation(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONCATENATION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
