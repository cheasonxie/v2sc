package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> formal_part ::=
 *   <dd> formal_designator
 *   <br> | <i>function_</i>name ( formal_designator )
 *   <br> | type_mark  ( formal_designator )
 */
class ScFormal_part extends ScVhdl {
    public ScFormal_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFORMAL_PART);
    }

    public String scString() {
        return "";
    }
}
