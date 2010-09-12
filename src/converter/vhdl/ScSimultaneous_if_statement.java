package converter.vhdl;

import java.util.ArrayList;

import parser.vhdl.ASTNode;


/**
 * <dl> simultaneous_if_statement ::=
 *   <dd> [ <i>if_</i>label : ]
 *   <ul> <b>if</b> condition <b>use</b>
 *   <ul>  simultaneous_statement_part
 *   </ul> { <b>elsif</b> condition <b>then</b>
 *   <ul> simultaneous_statement_part }
 *   </ul> [ <b>else</b>
 *   <ul> simultaneous_statement_part ]
 *   </ul> <b>end</b> <b>use</b> [ <i>if_</i>label ] ; </ul>
 */
class ScSimultaneous_if_statement extends ScCommonIdentifier implements IStatement {
    class ConPair {
        ScCondition condition = null;
        ScSimultaneous_statement_part statements = null;
    }
    
    ConPair if_pair = new ConPair();
    ArrayList<ConPair> elsif_pair = new ArrayList<ConPair>();
    ConPair else_pair = null;
    
    public ScSimultaneous_if_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIMULTANEOUS_IF_STATEMENT);
        int i = 0;
        while(i < node.getChildrenNum()) {
            ASTNode c = (ASTNode)node.getChild(i);
            String image = "";
            switch(c.getId())
            {
            case ASTVOID:
                image = c.firstTokenImage();
                c = (ASTNode)node.getChild(i+1);
                if(image.equalsIgnoreCase(tokenImage[IF])) {
                    if_pair.condition = new ScCondition(c);
                    c = (ASTNode)node.getChild(i+2);
                    if_pair.statements = new ScSimultaneous_statement_part(c);
                    i += 3;
                }else if(image.equalsIgnoreCase(tokenImage[ELSIF])) {
                    ConPair pair = new ConPair();
                    pair.condition = new ScCondition(c);
                    c = (ASTNode)node.getChild(i+2);
                    pair.statements = new ScSimultaneous_statement_part(c);
                    elsif_pair.add(pair);
                    i += 3;
                }else if(image.equalsIgnoreCase(tokenImage[ELSE])) {
                    else_pair = new ConPair();
                    else_pair.statements = new ScSimultaneous_statement_part(c);
                    i += 2;
                }
                break;
            case ASTIDENTIFIER:
                identifier = c.firstTokenImage();
                i++;
                break;
            default:
                i++;
                break;
            }
        }
        if(identifier.isEmpty())
            identifier = String.format("line%d", node.getFirstToken().beginLine);
    }

    public String scString() {
        String ret = "";
        ret += intent() + "if(" + if_pair.condition.scString() + ")\r\n";
        ret += intent() + "{\r\n";
        startIntentBlock();
        ret += if_pair.statements.scString();
        if(elsif_pair.size() > 0) {
            endIntentBlock();
            ret += intent() + "}\r\n";
            for(int i = 0; i < elsif_pair.size(); i++) {
                ConPair pair = elsif_pair.get(i);
                ret += intent() + "else if(" + pair.condition.scString() + ")\r\n";
                ret += intent() + "{\r\n";
                startIntentBlock();
                ret += pair.statements.scString();
            }
        }
        
        if(else_pair != null) {
            endIntentBlock();
            ret += intent() + "}\r\n";
            ret += intent() + "else\r\n";
            ret += intent() + "{\r\n";
            startIntentBlock();
            ret += else_pair.statements.scString();
        }
        endIntentBlock();
        ret += intent() + "}";
        return ret;
    }

    @Override
    public String getDeclaration()
    {
        return "";
    }

    @Override
    public String getImplements()
    {
        return "";
    }

    @Override
    public String getInitCode()
    {
        return scString();
    }
}
