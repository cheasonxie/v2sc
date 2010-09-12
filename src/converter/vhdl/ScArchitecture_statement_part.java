package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> architecture_statement_part ::=
 *   <dd> { architecture_statement }
 */
class ScArchitecture_statement_part extends ScVhdl implements IStatement {
    ArrayList<ScArchitecture_statement> itemList = new ArrayList<ScArchitecture_statement>();
    public ScArchitecture_statement_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTARCHITECTURE_STATEMENT_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScArchitecture_statement newNode = new ScArchitecture_statement(c);
            itemList.add(newNode);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < itemList.size(); i++) {
            ret += intent() + itemList.get(i).scString();
            if(i < itemList.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }

    @Override
    public String getDeclaration() {
        String ret = "";
        for(int i = 0; i < itemList.size(); i++) {
            ret += itemList.get(i).getDeclaration() + "\r\n";
        }
        return ret;
    }

    @Override
    public String getImplements() {
        String ret = "";
        for(int i = 0; i < itemList.size(); i++) {
            ret += itemList.get(i).getImplements() + "\r\n";
        }
        return ret;
    }

    @Override
    public String getInitCode()
    {
        String ret = "";
        for(int i = 0; i < itemList.size(); i++) {
            ret += itemList.get(i).getInitCode() + "\r\n";
        }
        return ret;
    }
}
