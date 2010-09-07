package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> term ::=
 *   <dd> factor { multiplying_operator factor }
 */
class ScTerm extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScTerm(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTERM);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl newNode = null;
            switch(c.getId())
            {
            case ASTFACTOR:
                newNode = new ScFactor(c);
                items.add(newNode);
                break;
            case ASTVOID:
                newNode = new ScMultiplying_operator(c);
                items.add(newNode);
                break;
            default:
                break;
            }
        }
    }
    
    public int getBitWidth() {
        return items.get(0).getBitWidth();
    }
    
    protected void setLogic(boolean logic) {
        super.setLogic(logic);
        if(items.size() <2)
            items.get(0).setLogic(logic);
    }

    public String scString() {
        String ret = "";
        ScFactor factor = (ScFactor)items.get(0);
        ret += factor.scString();
        
        for(int i = 1; i < items.size() - 1; i += 2) {
            ret += " " + getReplaceOperator(items.get(i).scString()) + " ";
            factor = (ScFactor)items.get(i+1);
            ret += factor.scString();
        }
        return ret;
    }
}
