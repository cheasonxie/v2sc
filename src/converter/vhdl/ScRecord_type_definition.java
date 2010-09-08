package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> record_type_definition ::=
 *   <dd> <b>record</b>
 *   <ul> element_declaration
 *   <br> { element_declaration }
 *   </ul><b>end</b> <b>record</b> [ <i>record_type_</i>simple_name ]
 */
class ScRecord_type_definition extends ScCommonIdentifier {
    ArrayList<ScElement_declaration> elements = new ArrayList<ScElement_declaration>();
    public ScRecord_type_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTRECORD_TYPE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScElement_declaration ele = null;
            switch(c.getId())
            {
            case ASTELEMENT_DECLARATION:
                ele = new ScElement_declaration(c);
                elements.add(ele);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += intent() + "typedef struct " + identifier + "\r\n";
        ret += intent() +"{\r\n";
        startIntentBlock();
        for(int i = 0; i < elements.size(); i++) {
            ret += intent() + elements.get(i).toString() + "\r\n";
        }
        endIntentBlock();
        ret += intent() +"}";
        return ret;
    }
}
