package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  assignment  <br>
 *     ::=  lvalue  =  expression  
 */
class ScAssignment extends ScVerilog {
    public ScAssignment(ASTNode node) {
        super(node);
        assert(node.getId() == ASTASSIGNMENT);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
