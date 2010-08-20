package converter;

import java.util.ArrayList;

/*
 * root node for current file
 */
public abstract class SCFileNode extends SCTreeNode
{
    private static final long serialVersionUID = 5271941380644784303L;
    
    public ArrayList<String> useLibNameArray = new ArrayList<String>();
    protected String curFilePath = "";
    protected ArrayList<CommentBlock> comments = new ArrayList<CommentBlock>();
    int nextCommentBlock = 0;   // to be sure all comment has been printed
    
    public SCFileNode(String srcPath)
    {
        super(null);
        curFilePath = srcPath;
        curFileNode = this;
        curBlockSymbol = new ArrayList<SCSymbol>();
        parseComment();
    }
    
    protected abstract void parseComment();
    
    public ArrayList<String> getComment(int line)
    {
        ArrayList<String> ret = null;
        if(nextCommentBlock < comments.size())
        {
            CommentBlock cb = comments.get(nextCommentBlock);
            if(cb.startLine <= line) {
                ret = cb.commentLines;
            }
            nextCommentBlock ++;
        }
        return ret;
    }
    
    public int getNextComment()
    {
        if(nextCommentBlock < comments.size())
            return comments.get(nextCommentBlock).endLine;
        return 0x7fffffff;
    }
    
    public String getFilePath()
    {
        return curFilePath;
    }
}
