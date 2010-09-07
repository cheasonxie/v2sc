package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> concurrent_procedure_call_statement ::=
 *   <dd> [ label : ] [ <b>postponed</b> ] procedure_call ;
 */
class ScConcurrent_procedure_call_statement extends ScVhdl {
    ScProcedure_call procedure_call = null;
    public ScConcurrent_procedure_call_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONCURRENT_PROCEDURE_CALL_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTPROCEDURE_CALL:
                procedure_call = new ScProcedure_call(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return procedure_call.scString();
    }
}
