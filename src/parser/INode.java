package parser;

public interface INode
{
    public void setParent(INode n);
    public INode getParent();
    public void addChild(INode n);
    public INode getChild(int i);
    int getChildrenNum();
    public int getId();
}
