package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  strength1  is one of the following keywords: <br>
 *     <b>supply1</b> <b>strong1</b> <b>pull1</b> <b>weak1</b> <b>highz1</b> 
 */
class ScStrength1 extends ScVerilog {
    public ScStrength1(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSTRENGTH1);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
