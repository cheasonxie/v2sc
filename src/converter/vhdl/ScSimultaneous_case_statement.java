package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> simultaneous_case_statement ::=
 *   <dd> [ <i>case_</i>label : ]
 *   <ul> <b>case</b> expression <b>use</b>
 *   <ul> simultaneous_alternative
 *   <br> { simultaneous_alternative }
 *   </ul> <b>end</b> <b>case</b> [ <i>case_</i>label ] ; </ul>
 */
class ScSimultaneous_case_statement extends ScVhdl {
    public ScSimultaneous_case_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIMULTANEOUS_CASE_STATEMENT);
    }

    public String scString() {
        warning("simultaneous_case_statement not support");
        return "";
    }
}
