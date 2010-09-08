package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> design_unit ::=
 *   <dd> context_clause library_unit
 */
class ScDesign_unit extends ScVhdl {
    ScVhdl context_clause = null;
    ScVhdl library_unit = null;
    public ScDesign_unit(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDESIGN_UNIT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTCONTEXT_CLAUSE:
                context_clause = new ScContext_clause(c);
                break;
            case ASTLIBRARY_UNIT:
                library_unit = new ScLibrary_unit(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += context_clause.toString();
        ret += "\r\n";
        ret += library_unit.toString();
        return ret;
    }
}
