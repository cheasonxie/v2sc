package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> process_statement ::=
 *   <dd> [ <i>process_</i>label : ]
 *   <ul> [ <b>postponed</b> ] <b>process</b> [ ( sensitivity_list ) ] [ <b>is</b> ]
 *   <ul> process_declarative_part
 *   </ul> <b>begin</b>
 *   <ul> process_statement_part
 *   </ul> <b>end</b> [ <b>postponed</b> ] <b>process</b> [ <i>process_</i>label ] ; </ul>
 */
class ScProcess_statement extends ScCommonIdentifier {
    ScSensitivity_list sensitivity_list = null;
    ScProcess_declarative_part declarative_part = null;
    ScProcess_statement_part statement_part = null;
    public ScProcess_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPROCESS_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                identifier = c.firstTokenImage();
                break;
            case ASTSENSITIVITY_LIST:
                sensitivity_list = new ScSensitivity_list(c);
                break;
            case ASTPROCESS_DECLARATIVE_PART:
                declarative_part = new ScProcess_declarative_part(c);
                break;
            case ASTPROCESS_STATEMENT_PART:
                statement_part = new ScProcess_statement_part(c);
                break;
            default:
                break;
            }
        }
        if(identifier.isEmpty()) {
            identifier = String.format("line%d", node.getFirstToken().beginLine);
        }
    }

    public String scString() {
        String ret = "\r\n" + intent();
        ret += "process_" + identifier + "()\r\n";
        ret += intent() + "{\r\n";
        startIntentBlock();
        ret += declarative_part.scString() + "\r\n\r\n";
        ret += statement_part.scString() + "\r\n";
        endIntentBlock();
        ret += intent() + "}\r\n";
        return ret;
    }
}
