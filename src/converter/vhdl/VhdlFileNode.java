package converter.vhdl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import converter.CommentBlock;
import converter.SCFileNode;

public class VhdlFileNode extends SCFileNode
{
    public VhdlFileNode(String srcPath)
    {
        super(srcPath);
        parseComment();
    }
    
    CommentBlock cb = null;
    int lineIndex = 1;
    int emptyLineNum = 0;
    private void lineComment(String strLine, int pos)
    {
        if(pos >= 0) {
            strLine = strLine.replaceFirst("--", "//");
            if(cb == null){
                cb = new CommentBlock(lineIndex, lineIndex);
            }else if(pos == 0) {
                cb.endLine ++;
            }else{
                comments.add(cb);    // string line don't start with "--", end this block
                cb = new CommentBlock(lineIndex, lineIndex);
            }
            for(int i = 0; i < emptyLineNum; i++)
                cb.commentLines.add("");
            cb.commentLines.add(strLine.substring(pos));
        }else if(cb != null) {
            comments.add(cb);
            cb = null;
        }
        if(strLine.isEmpty())
            emptyLineNum ++;
        else
            emptyLineNum = 0;
    }

    protected void parseComment()
    {
        cb = null;
        lineIndex = 1;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(curFilePath));
            while(true)
            {
                String strLine = reader.readLine();
                if(strLine == null)
                {
                    if(cb != null)
                        comments.add(cb);
                    break;
                }
                strLine = strLine.trim();
                int pos = strLine.indexOf("--");
                lineComment(strLine, pos);
                lineIndex ++;
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
