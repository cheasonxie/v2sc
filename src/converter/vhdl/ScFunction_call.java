package converter.vhdl;

import parser.vhdl.ASTNode;
import parser.vhdl.Symbol;


/**
 * <dl> function_call ::=
 *   <dd> <i>function_</i>name [ ( actual_parameter_part ) ]
 */
class ScFunction_call extends ScVhdl {
    ScName name = null;
    ScActual_parameter_part param_part= null;
    public ScFunction_call(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFUNCTION_CALL);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                break;
            case ASTASSOCIATION_LIST:
                param_part = new ScActual_parameter_part(c);
                break;
            default:
                break;
            }
        }
    }
    
    public int getBitWidth() {
        Symbol sym = (Symbol)parser.getSymbol(curNode, name.getNameSegments());
        int v1 = getIntValue(sym.typeRange[0]);
        int v2 = getIntValue(sym.typeRange[2]);
        return (v1 > v2) ? (v1-v2+1) : (v2-v1+1);
    }

    public String scString() {
        String ret = "";
        ret += name + "(";
        ret += param_part.scString();
        ret += ")";
        return ret;
    }
}
