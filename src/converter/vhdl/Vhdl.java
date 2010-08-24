package converter.vhdl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import common.FileList;

import parser.IASTNode;
import parser.IParser;
import parser.ParserException;
import parser.vhdl.ASTNode;
import parser.vhdl.LibraryManager;
import parser.vhdl.VhdlASTConstants;
import parser.vhdl.VhdlParser;
import converter.LibEntry;
import converter.SCSymbol;
import converter.hdlConverter;

public class Vhdl extends hdlConverter implements VhdlASTConstants {
    
    @Override
    public void convertFile(String srcPath, String dstPath)
            throws ParserException, FileNotFoundException, IOException
    {
        VhdlParser parser = new VhdlParser(new FileReader(srcPath), false);
        createFile(dstPath, true);        
        m_targetFileBuff.println("\r\n#include <systemc.h>");

        ASTNode designFile = parser.design_file();
        SCVhdlNode rootNode = new SCVhdlNode(null, designFile);
        m_targetFileBuff.println(rootNode);
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
