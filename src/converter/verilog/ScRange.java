package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  range  <br>
 *     ::= <b>[</b>  constant_expression  :  constant_expression  <b>]</b> 
 */
class ScRange extends ScVerilog {
    public ScRange(ASTNode node) {
        super(node);
        assert(node.getId() == ASTRANGE);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
