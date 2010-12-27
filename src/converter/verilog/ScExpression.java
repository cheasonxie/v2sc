package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  expression  <br>
 *     ::=  primary  <br>
 *     ||=  unary_operator   primary  <br>
 *     ||=  expression   binary_operator   expression  <br>
 *     ||=  expression  <QUESTION_MARK>  expression  :  expression  <br>
 *     ||=  string  
 */
class ScExpression extends ScVerilog {
    public ScExpression(ASTNode node) {
        super(node);
        assert(node.getId() == ASTEXPRESSION);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
