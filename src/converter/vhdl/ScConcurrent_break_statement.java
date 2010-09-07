package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> concurrent_break_statement ::=
 *   <dd> [ label : ] <b>break</b> [ break_list ] [ sensitivity_clause ] [ <b>when</b> condition ] ;
 */
class ScConcurrent_break_statement extends ScVhdl {
    ScBreak_list break_list = null;
    ScSensitivity_clause sensitivity_clause = null;
    ScCondition condition = null;
    public ScConcurrent_break_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONCURRENT_BREAK_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTBREAK_LIST:
                break_list = new ScBreak_list(c);
                break;
            case ASTSENSITIVITY_CLAUSE:
                sensitivity_clause = new ScSensitivity_clause(c);
                break;
            case ASTEXPRESSION:
                condition = new ScCondition(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        warning("break statement not support");
        return "";
    }
}
