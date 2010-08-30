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
        m_targetFileBuff.println("\r\n#include <systemc.h>");

        ASTNode designFile = (ASTNode)parser.parse(srcPath);
        ScVhdl root = new ScDesign_file(parser);
        m_targetFileBuff.println(root.scString());
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
