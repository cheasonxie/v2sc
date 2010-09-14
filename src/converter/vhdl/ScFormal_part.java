package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> formal_part ::=
 *   <dd> formal_designator
 *   <br> | <i>function_</i>name ( formal_designator )
 *   <br> | type_mark  ( formal_designator )
 */
class ScFormal_part extends ScVhdl {
    ScName name = null;
    ScFormal_designator designator = null;
    public ScFormal_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFORMAL_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                name = new ScName(c);
                break;
            case ASTFORMAL_DESIGNATOR:
                designator = new ScFormal_designator(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        if(name != null) {
            ret += name.scString();
            ret += encloseBracket(designator.scString());
        }else {
            ret += designator.scString();
        }
        return ret;
    }
}
