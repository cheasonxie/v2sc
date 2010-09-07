package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> discrete_range ::=
 *   <dd> <i>discrete_</i>subtype_indication | range
 */
class ScDiscrete_range extends ScVhdl {
    ScRange range = null;
    ScSubtype_indication subtype = null;
    public ScDiscrete_range(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDISCRETE_RANGE);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTRANGE:
            range = new ScRange(c);
            break;
        case ASTSUBTYPE_INDICATION:
            subtype = new ScSubtype_indication(c);
            break;
        default:
            break;
        }
    }
    
    public int getBitWidth() {
        if(range != null)
            return range.getBitWidth();
        else
            return subtype.getBitWidth();
    }
    
    public String getMin() {
        String ret = "0";
        if(range != null) {
            ret = range.getMin();
        }else if(subtype.constraint != null){
            ret = subtype.constraint.getMin();
        }
        return ret;
    }
    public String getMax() {
        String ret = "0";
        if(range != null) {
            ret = range.getMax();
        }else if(subtype.constraint != null){
            ret = subtype.constraint.getMax();
        }
        return ret;
    }
    public boolean isDownto() {
        boolean ret = false;
        if(range != null) {
            ret = range.isDownto();
        }else if(subtype.constraint != null){
            ret = subtype.constraint.isDownto();
        }
        return ret;
    }
    
    public String[] getRange() {
        String[] ret = null;
        if(range != null) {
            ret = range.getRange();
        }else if(subtype.constraint != null){
            ret = subtype.constraint.getRange();
        }
        return ret;
    }

    public String scString() {
        String ret = "";
        if(range != null) {
            ret = range.scString();
        }else {
            ret = subtype.scString();
        }
        return ret;
    }
}
