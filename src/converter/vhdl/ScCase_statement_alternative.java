package converter.vhdl;

import parser.vhdl.ASTNode;
import java.util.ArrayList;


/**
 * <dl> case_statement_alternative ::=
 *   <dd> <b>when</b> choices =>
 *   <ul> sequence_of_statements </ul>
 */
class ScCase_statement_alternative extends ScVhdl {
    ScChoices choices = null;
    ScVhdl seq_statements = null;
    public ScCase_statement_alternative(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCASE_STATEMENT_ALTERNATIVE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTCHOICES:
                choices = new ScChoices(c);
                break;
            case ASTSEQUENCE_OF_STATEMENTS:
                seq_statements = new ScSequence_of_statements(c);
                break;
            default:
                break;
            }
        }
    }
    
    public ScChoices getChoices() {
        return choices;
    }
    
    public String statementsString() {
        return seq_statements.scString();
    }

    public String scString() {
        String ret = "";
        ArrayList<ScChoice> items = choices.getItems();
        for(int i = 0; i < items.size(); i++) {
            ScChoice item = items.get(i);
            if(item.isOthers()){
                ret += intent() + "default:\r\n";
            }else {
                ret += intent() + "case " + item.scString();
                ret += ":\r\n";
            }
        }
        startIntentBlock();
        ret += statementsString();
        ret += intent() + "break;\r\n";
        endIntentBlock();
        return ret;
    }
}
