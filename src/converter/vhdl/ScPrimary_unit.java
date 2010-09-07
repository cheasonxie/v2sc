package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> primary_unit ::=
 *   <dd> entity_declaration
 *   <br> | configuration_declaration
 *   <br> | package_declaration
 */
class ScPrimary_unit extends ScVhdl {
    ScVhdl declaration = null;
    public ScPrimary_unit(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPRIMARY_UNIT);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTENTITY_DECLARATION:
            declaration = new ScEntity_declaration(c);
            break;
        case ASTCONFIGURATION_DECLARATION:
            declaration = new ScConfiguration_declaration(c);
            break;
        case ASTPACKAGE_DECLARATION:
            declaration = new ScPackage_declaration(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return declaration.scString();
    }
}
