package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  always_statement  <br>
 *     ::= <b>always</b>  statement  
 */
class ScAlways_statement extends ScVerilog {
    ScStatement statement = null;
    public ScAlways_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTALWAYS_STATEMENT);
        ASTNode c = (ASTNode)curNode.getChild(0);
        statement = new ScStatement(c);
    }

    public String scString() {
        return statement.scString();
    }
}
