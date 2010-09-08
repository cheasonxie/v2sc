package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> architecture_body ::=
 *   <dd> <b>architecture</b> identifier <b>of</b> <i>entity_</i>name <b>is</b>
 *   <ul> architecture_declarative_part </ul>
 *   <b>begin</b>
 *   <ul> architecture_statement_part </ul>
 *   <b>end</b> [ <b>architecture</b> ] [ <i>architecture_</i>simple_name ] ;
 */
class ScArchitecture_body extends ScCommonIdentifier {
    ScVhdl entity_name = null;
    ScVhdl declarative_part = null;
    ScVhdl statement_part = null;
    public ScArchitecture_body(ASTNode node) {
        super(node);
        assert(node.getId() == ASTARCHITECTURE_BODY);
        int i;
        for(i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            int id = c.getId();
            ScVhdl tmp = null;
            switch(id)
            {
            case ASTIDENTIFIER:
                tmp = new ScIdentifier(c);
                identifier = tmp.scString();
                break;
            case ASTNAME:
                entity_name = new ScName(c);
                break;
            case ASTARCHITECTURE_DECLARATIVE_PART:
                declarative_part = new ScArchitecture_declarative_part(c);
                break;
            case ASTARCHITECTURE_STATEMENT_PART:
                statement_part = new ScArchitecture_statement_part(c);
                break;
            default:
                break;
            }
        }
        assert(entity_name != null);
        for(i = 0; i < units.size(); i++) {
            ScCommonIdentifier ident = units.get(i);
            if(ident instanceof ScEntity_declaration
                && ident.identifier.equalsIgnoreCase(entity_name.scString())) {
                ((ScEntity_declaration)ident).setArchitectureBody(this);
                break;
            }
        }
        if(i == units.size()) {
            System.err.println("architecture boty no corresponding entity");
        }
    }

    public String scString() {
        String ret = declarative_part.toString() + "\r\n";
        ret += statement_part.scString();
        return ret;
    }
}
