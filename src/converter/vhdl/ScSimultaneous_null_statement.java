package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> simultaneous_null_statement ::=
 *   <dd> [ label : ] <b>null</b> ;
 */
class ScSimultaneous_null_statement extends ScVhdl {
    public ScSimultaneous_null_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIMULTANEOUS_NULL_STATEMENT);
    }

    public String scString() {
        warning("simultaneous_null_statement not support");
        return "";
    }
}
