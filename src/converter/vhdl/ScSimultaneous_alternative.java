package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> simultaneous_alternative ::=
 *   <dd> <b>when</b> choices =>
 *   <ul> simultaneous_statement_part </ul>
 */
class ScSimultaneous_alternative extends ScVhdl {
    public ScSimultaneous_alternative(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIMULTANEOUS_ALTERNATIVE);
    }

    public String scString() {
        warning("simultaneous_alternative not support");
        return "";
    }
}
