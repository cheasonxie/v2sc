package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> subtype_indication ::=
 *   <dd> [ <i>resolution_function_</i>name ] type_mark [ constraint ] [ tolerance_aspect ]
 */
class ScSubtype_indication extends ScVhdl {
    ScName name = null;
    ScType_mark type_mark = null;
    ScConstraint constraint = null;
    ScTolerance_aspect tolerance = null;
    public ScSubtype_indication(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSUBTYPE_INDICATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                name = new ScName(c);
                break;
            case ASTTYPE_MARK:
                type_mark = new ScType_mark(c);
                break;
            case ASTCONSTRAINT:
                constraint = new ScConstraint(c);
                break;
            case ASTTOLERANCE_ASPECT:
                tolerance = new ScTolerance_aspect(c);
                break;
            default:
                break;
            }
        }
    }
    
    public int getBitWidth() {
        return type_mark.getBitWidth();
    }
    
    public String scString() {
        String ret = "";
        ret += type_mark.scString();
        if(constraint != null) {
            String[] range = constraint.getRange();
            ret += "/* ";
            ret += range[0] + " " + range[1] + " " + range[2];
            ret += " */";
            //warning("constraint ignored");
        }
        if(tolerance != null) {
            warning("tolerance ignored");
        }
        return ret;
    }
}
