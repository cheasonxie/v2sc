package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> subprogram_body ::=
 *   <dd> subprogram_specification <b>is</b>
 *   <ul> subprogram_declarative_part
 *   </ul> <b>begin</b>
 *   <ul> subprogram_statement_part
 *   </ul> <b>end</b> [ subprogram_kind ] [ designator ] ;
 */
class ScSubprogram_body extends ScVhdl {
    ScSubprogram_specification spec = null;
    ScSubprogram_declarative_part declarative_part = null;
    ScSubprogram_statement_part statement_part = null;
    public ScSubprogram_body(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSUBPROGRAM_BODY);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTSUBPROGRAM_SPECIFICATION:
                spec = new ScSubprogram_specification(c);
                break;
            case ASTSUBPROGRAM_DECLARATIVE_PART:
                declarative_part = new ScSubprogram_declarative_part(c);
                break;
            case ASTSUBPROGRAM_STATEMENT_PART:
                statement_part = new ScSubprogram_statement_part(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        String tmp = "";
        ret += spec.specString(true) + "\r\n";
        ret += intent() + "{\r\n";
        startIntentBlock();
        tmp = declarative_part.toString();
        if(!tmp.isEmpty())
            ret += tmp + "\r\n";
        String[] lvars = getLoopVar();
        if(lvars != null && lvars.length > 0) {
            ret += intent() + "int ";
            for(int i = 0; i < lvars.length; i++) {
                ret += lvars[i];
                if(i < lvars.length - 1) {
                    ret += ", ";
                }
            }
            ret += ";\r\n";
        }
        if(!tmp.isEmpty())
            ret += "\r\n";
        ret += statement_part.toString() + "\r\n";
        endIntentBlock();
        ret += intent() + "}\r\n";
        return ret;
    }
}
