package converter.vhdl;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> entity_declaration ::=
 *   <dd> <b>entity</b> identifier <b>is</b>
 *   <ul> entity_header
 *   <br> entity_declarative_part
 *   </ul> [ <b>begin</b>
 *   <ul> entity_statement_part ]
 *   </ul> <b>end</b> [ <b>entity</b> ] [ <i>entity_</i>simple_name ] ;
 */
class ScEntity_declaration extends ScCommonIdentifier implements IScStatementBlock {
    ScEntity_header header = null;
    ScEntity_declarative_part declarative_part = null;
    ScEntity_statement_part statement_part = null;
    ScArchitecture_body body = null;
    ScConfiguration_declaration config = null;
    
    public ScEntity_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_DECLARATION);
        curLevel = 0;
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl tmp = null;
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                tmp = new ScIdentifier(c);
                identifier = tmp.scString();
                break;
            case ASTENTITY_HEADER:
                header = new ScEntity_header(c);
                break;
            case ASTENTITY_DECLARATIVE_PART:
                declarative_part = new ScEntity_declarative_part(c);
                break;
            case ASTENTITY_STATEMENT_PART:
                statement_part = new ScEntity_statement_part(c);
                break;
            default:
                break;
            }
        }
        units.add(this);
    }

    public String scString() {
        String ret = "\r\n";
        if(body == null) {
            return "";  //TODO no entity body, ignore
        }
        if(header.generic != null) {
            ret += "template<\r\n";
            startIntentBlock();
            ret += header.generic.toString();
            endIntentBlock();
            ret += "\r\n>\r\n";
        }
        ret += "SC_MODULE(" + getName() + ")\r\n{\r\n";
        startIntentBlock();
        ret += addLF(header.toString());
        ret += addLF(declarative_part.toString());
        ret += addLF(body.getDeclaration());
        
        ret += getInitCode();
        ret += getImplements();
        endIntentBlock();
        ret += "};\r\n";
        return ret;
    }
    
    public void setArchitectureBody(ScArchitecture_body body) {
        this.body = body;
    }
    
    public void setConfigurationDeclaration(ScConfiguration_declaration config) {
        this.config = config;
    }
    
    public String getName() {
        return identifier;
    }

    @Override
    public String getInitCode()
    {
        if(body == null) {
            return "";  //TODO no entity body, ignore
        }
        String ret = "";
        className = getName();
        ret += intent() + "SC_CTOR(" + getName() + ")\r\n";
        ret += startIntentBraceBlock();
        ret += body.getInitCode();
        ret += endIntentBraceBlock();
        return ret;
    }

    @Override
    public String getDeclaration()
    {
        String ret = "";
        if(body == null) {
            return "";  //TODO no entity body, ignore
        }
        className = getName();
        if(header.generic != null) {
            ret += "template<\r\n";
            startIntentBlock();
            ret += header.generic.toString();
            endIntentBlock();
            ret += "\r\n>\r\n";
        }
        ret += "SC_MODULE(" + getName() + ")\r\n{\r\n";
        startIntentBlock();
        ret += addLF(header.toString());
        ret += addLF(declarative_part.getDeclaration());
        ret += addLF(body.getDeclaration());
        ret += "\r\n" + addLF(getInitCode());   // add sc_ctor
        endIntentBlock();
        ret += "};\r\n";
        return ret;
    }

    @Override
    public String getImplements()
    {
        String ret = "";
        if(body == null) {
            return "";  //TODO no entity body, ignore
        }
        className = getName();
        ret += addLF(declarative_part.getImplements());
        if(statement_part != null) {
            ret += addLF(statement_part.toString());
        }
        ret += addLF(body.getImplements());
        return ret;
    }
}
