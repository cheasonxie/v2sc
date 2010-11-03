package converter.vhdl;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> architecture_body ::=
 *   <dd> <b>architecture</b> identifier <b>of</b> <i>entity_</i>name <b>is</b>
 *   <ul> architecture_declarative_part </ul>
 *   <b>begin</b>
 *   <ul> architecture_statement_part </ul>
 *   <b>end</b> [ <b>architecture</b> ] [ <i>architecture_</i>simple_name ] ;
 */
class ScArchitecture_body extends ScCommonIdentifier implements IScStatementBlock {
    ScName entity_name = null;
    ScArchitecture_declarative_part declarative_part = null;
    ScArchitecture_statement_part statement_part = null;
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
            System.err.println("architecture boty has no corresponding entity");
        }
    }
    
    public String scString() {
        String ret = "";
        ret += addLF(declarative_part.toString());
        ret += addLF(statement_part.scString());
        return ret;
    }

    @Override
    public String getDeclaration()
    {
        String ret = "";
        ret += addLF(declarative_part.getDeclaration());
        ret += addLF(statement_part.getDeclaration());
        return ret;
    }

    @Override
    public String getImplements()
    {
        String ret = "";
        ret += addLF(declarative_part.getImplements());
        ret += addLF(statement_part.getImplements());
        return ret;
    }

    @Override
    public String getInitCode()
    {
        String ret = "";
        ret += addLF(declarative_part.getInitCode());
        ret += addLF(statement_part.getInitCode());
        return ret;
    }
}
