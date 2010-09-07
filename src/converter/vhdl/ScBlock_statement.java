package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> block_statement ::=
 *   <dd> <i>block_</i>label :
 *   <ul> <b>block</b> [ ( <i>guard_</i>expression ) ] [ <b>is</b> ]
 *   <ul> block_header
 *   <br> block_declarative_part
 *   </ul> <b>begin</b>
 *   <ul> block_statement_part
 *   </ul> <b>end</b> <b>block</b> [ <i>block_</i>label ] ; </ul>
 */
class ScBlock_statement extends ScVhdl {
    ScVhdl header = null;
    ScVhdl declarative_part = null;
    ScVhdl statement_part = null;
    public ScBlock_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBLOCK_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTBLOCK_HEADER:
                header = new ScBlock_header(c);
                break;
            case ASTBLOCK_DECLARATIVE_PART:
                declarative_part = new ScBlock_declarative_part(c);
                break;
            case ASTBLOCK_STATEMENT_PART:
                statement_part = new ScBlock_statement_part(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += intent() + "{";
        startIntentBlock();
        ret += intent() + header.scString() + "\r\n";
        ret += intent() + declarative_part.scString() + "\r\n";
        ret += intent() + statement_part.scString() + "\r\n";
        endIntentBlock();
        ret += intent() + "}";
        return ret;
    }
}
