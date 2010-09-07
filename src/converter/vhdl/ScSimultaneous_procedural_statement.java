package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> simultaneous_procedural_statement ::=
 *   <dd> [ <i>procedural_</i>label : ]
 *   <ul> <b>procedural</b> [ <b>is</b> ]
 *   <ul> procedural_declarative_part
 *   </ul> <b>begin</b>
 *   <ul> procedural_statement_part
 *   </ul> <b>end</b> <b>procedural</b> [ <i>procedural_</i>label ] ; </ul>
 */
class ScSimultaneous_procedural_statement extends ScVhdl {
    public ScSimultaneous_procedural_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIMULTANEOUS_PROCEDURAL_STATEMENT);
    }

    public String scString() {
        warning("simultaneous_procedural_statement not support");
        return "";
    }
}
