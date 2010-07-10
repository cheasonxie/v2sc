package converter;

/*
 * systemc syntax tree node
 */

import java.util.ArrayList;

public class SCTreeNode implements SCTreeConstants
{
    protected SCFileNode curFileNode = null;
    protected SCTreeNode parent = null;
    protected ArrayList<SCTreeNode> children = new ArrayList<SCTreeNode>();
    
    // symbols in this block
    public ArrayList<SCSymbol> curBlockSymbol = null; 
    public ArrayList<String> curBlockloopVar = null; 
    protected int level = 0;
    protected int intent_level = 0;

    public SCTreeNode(SCTreeNode p)
    {
        parent = p;
        if (p != null)
        {
            level = p.level + 1;
            intent_level = level;
            curFileNode = p.curFileNode;
            curBlockSymbol = p.curBlockSymbol;
            curBlockloopVar = p.curBlockloopVar;
            p.AddChild(this);
        }
    }
    
    public SCTreeNode getParent()
    {
        return parent;
    }
    
    public void setParent(SCTreeNode p)
    {
        parent = p;
    }
    
    public SCFileNode getFileNode()
    {
        return curFileNode;
    }
    
    public void AddChild(SCTreeNode node)
    {
        if(node != null)
            children.add(node);
    }

    public int getChildNum()
    {
        return children.size();
    }
    
    public SCTreeNode getChild(int index)
    {
        if(index < children.size())
            return children.get(index);
        else
            return null;
    }
    
    @Override
    public String toString()
    {
        return "";
    }
    
    // intent
    protected static String intent(int lv)
    {
        String ret = "";
        for (int i = 0; i <= lv; i++)
            for (int j = 0; j < tabSize; j++)
                ret += " ";
        return ret;
    }
    
    protected String intent()
    {
        if(intent_level >= 2)
            return intent(intent_level - 2);
        else
            return "";
    }
    
    // intent one tabsize spaces to left
    public void intentLeft()
    {
        if(intent_level > 0)
        {
            intent_level --;
            for(int i = 0; i < children.size(); i++)
                children.get(i).intentLeft();
        }
    }
    
    // intent one tabsize spaces to right
    public void intentRight()
    {
        intent_level ++;
        for(int i = 0; i < children.size(); i++)
            children.get(i).intentRight();
    }
    
    public void setIntentLevel(int lv)
    {
        intent_level = lv;
        for(int i = 0; i < children.size(); i++)
            children.get(i).setIntentLevel(lv+1);
    }
    
    public int getIntentLevel()
    {
        return intent_level;
    }
    
    protected SCSymbol getSymbol(String name)
    {
        SCSymbol ret = null;
        int i;

        // 1. find symbol in lib
        if(curFileNode != null) {
            for (i = 0; i < curFileNode.useLibNameArray.size(); i++) {
                ret = hdlConverter.getGlobalSymbol(curFileNode.useLibNameArray.get(i), name);
                if (ret != null)
                    return ret;
            }
        } else {
            ret = hdlConverter.getGlobalSymbol(name);
            if (ret != null)
                return ret;
        }

        // 2. if not found, find symbol in this block
        for (i = 0; i < curBlockSymbol.size(); i++) {
            SCSymbol s = curBlockSymbol.get(i);
            if (name.equalsIgnoreCase(s.name)) {
                return s;
            }
        }

        // 3. if not found, find symbol in parent node
        SCTreeNode scNode = parent;
        while ((scNode != null) && (scNode.curBlockSymbol == curBlockSymbol)) {
            scNode = scNode.parent;
        }
        if(scNode != null)
            return scNode.getSymbol(name);
        return null;
    }
    
    protected int getSymbolType(String name)
    {
        SCSymbol sym = getSymbol(name);
        if(sym != null)
            return sym.type;

        return SC_INVALID_TYPE;
    }
    
    protected ArrayList<String> getLoopVars()
    {
        int i;
        ArrayList<String> ret = new ArrayList<String>();
        if(curBlockloopVar == null)
            return ret;
        
        for(i = 0; i < curBlockloopVar.size(); i++)
            ret.add(curBlockloopVar.get(i));
        
        return ret;
    }
}
