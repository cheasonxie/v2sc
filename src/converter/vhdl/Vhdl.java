package converter.vhdl;

import java.io.IOException;

import parser.ParserException;
import parser.vhdl.ASTNode;
import parser.vhdl.LibraryManager;
import parser.vhdl.VhdlParser;
import converter.hdlConverter;

public class Vhdl extends hdlConverter {
    
    @Override
    public void convertFile(String srcPath, String dstPath)
            throws ParserException, IOException
    {
        VhdlParser parser = new VhdlParser(false);
        createFile(dstPath, true);
        parser.parse(srcPath);
        ScVhdl root = new ScDesign_file(parser);
        m_targetFileBuff.print(root.toString());
    }
    
    @Override
    public void convertDir(String srcDir)
    {
        
    }

    @Override
    public void addLibary(String srcDir, String libName)
    {
        LibraryManager.getInstance().add(srcDir, libName);
    }
}
