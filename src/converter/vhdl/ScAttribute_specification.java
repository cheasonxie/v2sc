package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> attribute_specification ::=
 *   <dd> <b>attribute</b> attribute_designator <b>of</b> entity_specification <b>is</b> expression ;
 */
class ScAttribute_specification extends ScVhdl {
    ScVhdl designator = null;
    ScVhdl entity = null;
    ScVhdl expression = null;
    public ScAttribute_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTATTRIBUTE_SPECIFICATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTATTRIBUTE_DESIGNATOR:
                designator = new ScAttribute_designator(c);
                break;
            case ASTENTITY_SPECIFICATION:
                entity = new ScEntity_specification(c);
                break;
            case ASTEXPRESSION:
                expression  = new ScExpression(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        error("user define attribute not support");
        return "";
    }
}
