package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> library_unit ::=
 *   <dd> primary_unit
 *   <br> | secondary_unit
 */
class ScLibrary_unit extends ScVhdl {
    ScVhdl unit = null;
    public ScLibrary_unit(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLIBRARY_UNIT);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTPRIMARY_UNIT:
            unit = new ScPrimary_unit(c);
            break;
        case ASTSECONDARY_UNIT:
            unit = new ScSecondary_unit(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return unit.scString();
    }
}
