package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  specify_block  <br>
 *     ::= <b>specify</b> { specify_item } <b>endspecify</b> 
 */
class ScSpecify_block extends ScVerilog {
    public ScSpecify_block(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSPECIFY_BLOCK);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
