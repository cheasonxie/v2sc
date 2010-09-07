package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> concurrent_assertion_statement ::=
 *   <dd> [ label : ] [ <b>postponed</b> ] assertion ;
 */
class ScConcurrent_assertion_statement extends ScVhdl {
    ScAssertion assertion = null;
    public ScConcurrent_assertion_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONCURRENT_ASSERTION_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTASSERTION:
                assertion = new ScAssertion(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return assertion.scString() + ";";
    }
}
