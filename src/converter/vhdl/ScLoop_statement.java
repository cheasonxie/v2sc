package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> loop_statement ::=
 *   <dd> [ <i>loop_</i>label : ]
 *   <ul> [ iteration_scheme ] <b>loop</b>
 *   <ul> sequence_of_statements
 *   </ul> <b>end</b> <b>loop</b> [ <i>loop_</i>label ] ; </ul>
 */
class ScLoop_statement extends ScVhdl {
    ScIteration_scheme iteration = null;
    ScVhdl seq_statements = null;
    public ScLoop_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLOOP_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTITERATION_SCHEME:
                iteration = new ScIteration_scheme(c);
                break;
            case ASTSEQUENCE_OF_STATEMENTS:
                seq_statements = new ScSequence_of_statements(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += intent() + iteration.scString() + "\r\n";
        ret += intent() + "{\r\n";
        startIntentBlock();
        ret += seq_statements.scString();
        endIntentBlock();
        ret += intent() + "}";
        return ret;
    }
}
