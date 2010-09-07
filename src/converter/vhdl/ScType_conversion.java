package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> type_conversion ::=
 *   <dd> type_mark ( expression )
 */
class ScType_conversion extends ScVhdl {
    public ScType_conversion(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTYPE_CONVERSION);
    }

    public String scString() {
        return "";
    }
}
