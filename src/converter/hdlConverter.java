package converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Stack;

import parser.IASTNode;
import parser.ParserException;
import parser.vhdl.ASTNode;

public abstract class hdlConverter implements SCTreeConstants
{
    static public final int T_NONE = 0;
    static public final int T_VHDL = 1;
    static public final int T_VERILOG = 2;
    
    String[] m_hdlFileContents = null;
    protected PrintStream m_targetFileBuff = null;
    
    static protected ArrayList<LibEntry> libSymbols = new ArrayList<LibEntry>();
    public static boolean symbolExist(String name)
    {
        for(int i = 0; i < libSymbols.size(); i++)
        {
            if(libSymbols.get(i).get(name) != null)
                return true; 
        }
        return false;
    }
    
    public static int getGlobalSymbolType(String name)
    {
        int ret = SC_INVALID_TYPE;
        for(int i = 0; i < libSymbols.size(); i++)
        {
            SCSymbol sym = libSymbols.get(i).get(name);
            if(sym != null)
            {
                ret = sym.type;
                break;
            }
        }
        return ret;
    }
    
    public static SCSymbol getGlobalSymbol(String symName)
    {
        SCSymbol ret = null;
        int i;
        
        for(i = 0; i < libSymbols.size(); i++)
        {
            ret = libSymbols.get(i).get(symName);
            if(ret != null)
                return ret;
        }

        return ret;
    }
    
    public static SCSymbol getGlobalSymbol(String libName, String symName)
    {
        SCSymbol ret = null;
        int i;
        
        // find libentry
        for(i = 0; i < libSymbols.size(); i++)
        {
            if(libSymbols.get(i).name.equalsIgnoreCase(libName))
                break;
        }
        
        // if libentry found, find symbol
        if(i < libSymbols.size())
        {
            return libSymbols.get(i).get(symName);
        }
        return ret;
    }
    
    protected String readFile(String path) throws FileNotFoundException, IOException
    {
        File file = new File(path);
        Reader reader;
        
        reader = new FileReader(file);
        BufferedReader breader = new BufferedReader(reader);
        String s, s2 = new String();

        while((s = breader.readLine())!= null)
        {
            s2 += s + "\n";
        }
        m_hdlFileContents = s2.split("\n");
        breader.close();

        return s2;
    }
    
    protected void createFile(String path, boolean replace) throws IOException
    {
        File file = new File(path);
        if(file.exists() && !replace)
            return;
        m_targetFileBuff = new PrintStream(file);
    }
    
    public abstract void convertFile(String srcPath, String dstPath) 
                throws ParserException, FileNotFoundException, IOException;    
    public abstract void convertDir(String srcDir);    
    public abstract void addLibary(String srcDir, String libName);
}
