package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> architecture_statement ::=
 *   <dd> simultaneous_statement
 *   <br> | concurrent_statement
 */
class ScArchitecture_statement extends ScVhdl {
    ScVhdl item = null;
    public ScArchitecture_statement(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTARCHITECTURE_STATEMENT);
        switch(node.getId())
        {
        case ASTSIMULTANEOUS_STATEMENT:
            item = new ScSimultaneous_statement(node);
            break;
        case ASTCONCURRENT_STATEMENT:
            item = new ScConcurrent_statement(node);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}
