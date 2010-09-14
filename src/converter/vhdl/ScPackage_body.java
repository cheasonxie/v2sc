package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> package_body ::=
 *   <dd> <b>package body</b> <i>package_</i>simple_name <b>is</b>
 *   <ul> package_body_declarative_part
 *   </ul> <b>end</b> [ <b>package body</b> ] [ <i>package_</i>simple_name ] ;
 */
class ScPackage_body extends ScCommonIdentifier implements IStatement {
    ScName package_name = null;
    ScPackage_body_declarative_part declarative_part = null;
    public ScPackage_body(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPACKAGE_BODY);
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
            case ASTPACKAGE_BODY_DECLARATIVE_PART:
                declarative_part = new ScPackage_body_declarative_part(c);
                break;
            default:
                break;
            }
        }
        assert(identifier != null);
        for(i = 0; i < units.size(); i++) {
            ScCommonIdentifier ident = units.get(i);
            if(ident instanceof ScPackage_declaration
                && ident.identifier.equalsIgnoreCase(identifier)) {
                ((ScPackage_declaration)ident).setPackageBody(this);
                break;
            }
        }
        if(i == units.size()) {
            System.err.println("package boty no corresponding package");
        }
    }

    public String scString() {
        String ret = declarative_part.toString() + "\r\n";
        return ret;
    }

    @Override
    public String getDeclaration() {
        String ret = "";
        return ret;
    }

    @Override
    public String getImplements() {
        return toString();
    }

    @Override
    public String getInitCode() {
        String ret = "";
        return ret;
    }
}
