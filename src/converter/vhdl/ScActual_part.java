package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> actual_part ::=
 *   <dd> actual_designator
 *   <br> | <i>function_</i>name ( actual_designator )
 *   <br> | type_mark ( actual_designator )
 */
class ScActual_part extends ScVhdl {
    ScVhdl item = null;
    ScActual_designator designator = null;
    boolean isFirstBracket = false;
    public ScActual_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTACTUAL_PART);
        isFirstBracket = (node.firstTokenImage().charAt(0) == '(');
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTFUNCTION_CALL:
                item = new ScFunction_call(c);
                break;
            case ASTAGGREGATE:
                item = new ScAggregate(c);
                break;
            case ASTACTUAL_DESIGNATOR:
                designator = new ScActual_designator(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        if(item != null) {
            return item.scString();
        }
        
        if(designator == null) {
            System.out.println();
        }
        if(((ScActual_designator)designator).isOpen) {
            warning("token open ignored");
        }
        if(item != null) {
            ret += item.scString();
        }else {
            String tmp = designator.scString();
            if(isFirstBracket)
                ret += encloseBracket(tmp);
            else
                ret += tmp;
        }
        return ret;
    }
}
