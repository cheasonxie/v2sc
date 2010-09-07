package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> simple_expression ::=
 *   <dd> [ sign ] term { adding_operator term }
 */
class ScSimple_expression extends ScVhdl {
    ScSign sign = null;
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScSimple_expression(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIMPLE_EXPRESSION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl newNode = null;
            switch(c.getId())
            {
            case ASTSIGN:
                sign = new ScSign(c);
                break;
            case ASTTERM:
                newNode = new ScTerm(c);
                items.add(newNode);
                break;
            case ASTVOID:
                newNode = new ScAdding_operator(c);
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
        if(items.size() < 2)
            items.get(0).setLogic(logic);
    }

    public String scString() {
        String ret = "";
        if(sign != null) {
            ret += sign.scString();
        }
        ScTerm term = (ScTerm)items.get(0);
        String tmp = term.scString();
        //if(term.items.size() > 2) {
        //    ret += "(" + tmp + ")";
        //}else {
            ret += tmp;
        //}
        
        for(int i = 1; i < items.size() - 1; i += 2) {
            if(!(items.get(i) instanceof ScAdding_operator 
                    && ((ScAdding_operator)items.get(i)).isConcat))
                ret += " ";
            ret += getReplaceOperator(items.get(i).scString());
            ret += " ";
            term = (ScTerm)items.get(i+1);
            tmp = term.scString();
            //if(term.items.size() > 2) {
            //    ret += "(" + tmp + ")";
            //}else {
                ret += tmp;
            //}
        }
        return ret;
    }
}
