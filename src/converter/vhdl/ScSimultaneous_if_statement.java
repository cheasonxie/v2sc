package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> simultaneous_if_statement ::=
 *   <dd> [ <i>if_</i>label : ]
 *   <ul> <b>if</b> condition <b>use</b>
 *   <ul>  simultaneous_statement_part
 *   </ul> { <b>elsif</b> condition <b>then</b>
 *   <ul> simultaneous_statement_part }
 *   </ul> [ <b>else</b>
 *   <ul> simultaneous_statement_part ]
 *   </ul> <b>end</b> <b>use</b> [ <i>if_</i>label ] ; </ul>
 */
class ScSimultaneous_if_statement extends ScVhdl {
    public ScSimultaneous_if_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIMULTANEOUS_IF_STATEMENT);
    }

    public String scString() {
        warning("simultaneous_if_statement not support");
        return "";
    }
}
