package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  register_variable  <br>
 *     ::=  name_of_register  <br>
 *     ||=  name_of_memory  <b>[</b>  constant_expression  :  constant_expression  <b>]</b> 
 */
class ScRegister_variable extends ScVerilog {
    ScVerilog name = null;
    ScConstant_expression exp1 = null, exp2 = null;
    public ScRegister_variable(ASTNode node) {
        super(node);
        assert(node.getId() == ASTREGISTER_VARIABLE);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTNAME_OF_REGISTER:
                name = new ScName_of_register(c);
                break;
            case ASTNAME_OF_MEMORY:
                name = new ScName_of_memory(c);
                break;
            case ASTCONSTANT_EXPRESSION:
                if(exp1 == null) {
                    exp1 = new ScConstant_expression(c);
                }else {
                    exp2 = new ScConstant_expression(c);
                }
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
