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
class ScBlock_statement extends ScCommonIdentifier implements IStatement {
    ScBlock_header header = null;
    ScBlock_declarative_part declarative_part = null;
    ScBlock_statement_part statement_part = null;
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
            case ASTIDENTIFIER:
                identifier = c.firstTokenImage();
                break;
            default:
                break;
            }
        }
        if(identifier.isEmpty())
            identifier = String.format("line%d", node.getFirstToken().beginLine);
    }
    
    private String getName() {
        return "process_block_" + identifier;
    }
    private String getSpec() {
        return intent() + "void " + getName() + "(void)";
    }

    public String scString() {
        String ret = getSpec() + "\r\n";
        ret += intent() + "{";
        startIntentBlock();
        ret += intent() + header.toString() + "\r\n";
        ret += intent() + declarative_part.toString() + "\r\n";
        ret += intent() + statement_part.toString() + "\r\n";
        endIntentBlock();
        ret += intent() + "}";
        return ret;
    }

    @Override
    public String getDeclaration() {
        String ret = getSpec() + ";";
        return ret;
    }

    @Override
    public String getImplements() {
        return toString();
    }

    @Override
    public String getInitCode()
    {
        // just call it
        return getName() + "();";
    }
}
