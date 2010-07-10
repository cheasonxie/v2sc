package parser.vhdl;

import java.util.ArrayList;

import parser.INode;
import parser.Token;

public class ASTNode implements INode, VhdlTokenConstants, VhdlASTConstants
{
    protected INode parent;
    protected ArrayList<INode> children = new ArrayList<INode>();
    protected int id;
    protected Token first_token = null;
    protected Token last_token = null;
    
    boolean isBoolean = false;  // used only for expression
    
    public ASTNode(INode p, int id) {
        parent = p;
        this.id = id;
        if(p != null) {
            p.addChild(this);
        }
    }
    
    public void addChild(INode n) {
        children.add(n);
    }

    public INode getChild(int i) {
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

    public INode getParent() {
        return parent;
    }

    public void setParent(INode p) {
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
        return ASTNodeName[id];
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
}

class ASTtoken extends ASTNode {
    String image = "";
    public ASTtoken(INode p, String image) {
        super(p, ASTVOID);
        this.image = image;
    }
    
    public String toString() {
        return image;
    }
    public String firstTokenImage() {
        return image;
    }
}

