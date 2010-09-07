package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> simple_simultaneous_statement ::=
 *   <dd> [ label : ] simple_expression == simple_expression [ tolerance_aspect ] ;
 */
class ScSimple_simultaneous_statement extends ScVhdl {
    public ScSimple_simultaneous_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIMPLE_SIMULTANEOUS_STATEMENT);
    }

    public String scString() {
        warning("simple_simultaneous_statement not support");
        return "";
    }
}
