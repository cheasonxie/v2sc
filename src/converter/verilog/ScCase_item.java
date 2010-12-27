package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  case_item  <br>
 *     ::=  expression  {, expression } :  statement_or_null  <br>
 *     ||= <b>default</b> :  statement_or_null  <br>
 *     ||= <b>default</b>  statement_or_null  
 */
class ScCase_item extends ScVerilog {
    public ScCase_item(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCASE_ITEM);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
