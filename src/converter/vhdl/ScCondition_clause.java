package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> condition_clause ::=
 *   <dd> <b>until</b> condition
 */
class ScCondition_clause extends ScVhdl {
    public ScCondition_clause(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONDITION_CLAUSE);
    }

    public String scString() {
        return "";
    }
}
