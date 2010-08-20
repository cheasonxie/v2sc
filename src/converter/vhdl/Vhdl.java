package converter.vhdl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import common.FileList;

import parser.IASTNode;
import parser.ParserException;
import parser.vhdl.ASTNode;
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
        VhdlParser parser = new VhdlParser(new FileReader(srcPath));
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

   
    /*
     * parse item in package: entity, procedure, function, constants, type
     */
    protected LibEntry parserPackageTree(IASTNode node, int level, String path)
    {
        LibEntry entry = null;
        int i = 0;
        
        if(node == null || level >= 5)  //TODO modify the maximum level
            return null;
        
        if(node.getId() == ASTPACKAGE_DECLARATION) {
            entry = new LibEntry(path, node.toString());
            SCVhdlNode vhdlNode = new SCVhdlNode(null, (ASTNode)node);
            ArrayList<SCSymbol> symbols = vhdlNode.curBlockSymbol;
            if(symbols != null) {
                entry.addAll(symbols);
            }
        }else {
            for(i = 0; i < node.getChildrenNum(); i++)
            {
                entry = parserPackageTree(node.getChild(i), level+1, path);
                if(entry != null) {
                    break;
                }
            }
        }
        return entry;
    }
    
    @Override
    public void parseLibSymbols(String srcDir)
    {
        FileList list = new FileList(srcDir, EXT_VHDL);
        
        System.out.println("======file num:" + list.getFileNum() + "========");
        
        for(int i = 0; i < list.getFileNum(); i++)
        {
            System.out.println("index:" + i);
            String path = list.getFile(i);
            try {
                System.out.println("file:" + path);
                VhdlParser parser = new VhdlParser(new FileReader(path));
                ASTNode designFile;
                designFile = parser.design_file();
                LibEntry entry = parserPackageTree(designFile, 0, path);
                if(entry != null)
                    libSymbols.add(entry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
