package parser.vhdl;

import java.util.ArrayList;

import parser.IASTNode;
import parser.INameObject;
import parser.Token;

public class ASTNode implements IASTNode
{
    protected IASTNode parent;
    protected ArrayList<IASTNode> children = new ArrayList<IASTNode>(1);
    protected int id;
    protected Token first_token = null;
    protected Token last_token = null;
    protected SymbolTable symTab = null;
    
    boolean isBoolean = false;  // used only for expression
    
    public ASTNode(IASTNode p, int id) {
        parent = p;
        this.id = id;
        if(p != null) {
            if(p instanceof ASTNode) {
                isBoolean = ((ASTNode)p).isBoolean;
            }
            p.addChild(this);
        }
    }
    
    public void addChild(IASTNode n) {
        children.add(n);
    }

    public IASTNode getChild(int i) {
        if(i < children.size())
            return children.get(i);
        return null;
    }

    public int getChildrenNum() {
        return children.size();
    }

    public int getId() {
        return id;
    }

    public IASTNode getParent() {
        return parent;
    }

    public void setParent(IASTNode p) {
        parent = p;
    }
    
    public void setFirstToken(Token token) {
        first_token = token;
    }
    
    public void setLastToken(Token token) {
        last_token = token;  
    }
    
    public Token getFirstToken() {
        return first_token;
    }
    
    public Token getLastToken() {
        return last_token;
    }
    
    public String toString() {
        return VhdlASTConstants.ASTNodeName[id];
    }
    
    public String firstTokenImage() {
        if(first_token != null) {
            return first_token.image;
        }else {
            return "invalid_image";
        }
    }
   
    public boolean isLogic() {
        return isBoolean;
    }

    public IASTNode getChildById(int id) {
        for(int i = 0; i < children.size(); i++) {
            IASTNode child = children.get(i);
            if(child.getId() == id) {
                return child; 
            }
        }
        return null;
    }
    
    /**
     * search descendant recursive to find specified ASTNode
     */
    public IASTNode getFirstDescendant(int id) {
        IASTNode ret = null;
        for(int i = 0; i < children.size(); i++) {
            ASTNode child = (ASTNode)children.get(i);
            if(child.getId() == id) {
                ret = child;
                break;
            }
            ret = child.getFirstDescendant(id);
            if(ret != null) { break; }
        }
        return ret;
    }
    
    public void setSymbolTable(SymbolTable table) {
        symTab = table;
    }
    
    public SymbolTable getSymbolTable() {
        return symTab;
    }
}

class ASTtoken extends ASTNode {
    String image = "";
    public ASTtoken(IASTNode p, String image) {
        super(p, VhdlASTConstants.ASTVOID);
        this.image = image;
    }
    
    public String toString() {
        return image;
    }
    public String firstTokenImage() {
        return image;
    }
}

