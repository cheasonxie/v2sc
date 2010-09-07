package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> element_declaration ::=
 *   <dd> identifier_list : element_subtype_definition ;
 */
class ScElement_declaration extends ScVhdl {
    ScIdentifier_list idList = null;
    ScElement_subtype_definition type = null;
    public ScElement_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTELEMENT_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER_LIST:
                idList = new ScIdentifier_list(c);
                break;
            case ASTELEMENT_SUBTYPE_DEFINITION:
                type = new ScElement_subtype_definition(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += type.scString() + " ";
        ret += idList.scString();
        ret += ";";
        return ret;
    }
}
