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
    
    static public final String EXT_VHDL = "vhd";
    static public final String EXT_VERILOG = "v";    
    
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
    public abstract void parseLibSymbols(String srcDir);
    
    
    
    /////////////////////////////////////////////////////////
    ////////////////// for debug ////////////////////////////
    /////////////////////////////////////////////////////////
    protected final boolean debugOut = true;
    protected PrintStream debugStreamOut = null;
    protected void initDebugStream(String path) throws IOException
    {
        if(!debugOut)
            return;
        
        int index1;
        path.replace('\\', '/');
        index1 = path.lastIndexOf('.');
        if(index1 < 0)
            return;

        File file = new File(path.substring(0, index1) + ".txt");
        if(file.exists())
            file.delete();
        file.createNewFile();
        debugStreamOut = new PrintStream(file);
    }
    
    protected void printTree(ASTNode node, int level, String srcPath)
    {
        if(!debugOut || node == null) {
            return;
        }
        
        if(debugStreamOut == null) {
            try {
                initDebugStream(srcPath);
            }catch(IOException e) {
                return;
            }
        }

        printTreeNode(node);           
        debugStreamOut.println(level + "=" + node.toString() + ": "
                    + "<<" + node.getFirstToken().image + ">>");
        
        for(int i = 0; i < node.getChildrenNum(); i++)
        {
            printTree((ASTNode)node.getChild(i), level+1, srcPath);
        }
    }
    
    protected void printTreeNode(IASTNode node)
    {
        int i;
        Stack<Character> stack = new Stack<Character>();
        
        IASTNode parent = node.getParent();
        IASTNode grandParent = null;
        if(parent != null)
        {
            stack.push('©¤');
            stack.push('©¤');
            for(i = 0; i < parent.getChildrenNum(); i++)
            {
                if(node.equals(parent.getChild(i)))
                    break;
            }
            if(i >= parent.getChildrenNum() - 1)
                stack.push('©¸');
            else
                stack.push('©À');
        }
        
        while(parent != null)
        {
            grandParent = parent.getParent();
            if(grandParent == null)
                break;
            for(i = 0; i < grandParent.getChildrenNum(); i++)
            {
                if(parent.equals(grandParent.getChild(i)))
                    break;
            }
            stack.push(' ');
            stack.push(' ');
            if(i >= grandParent.getChildrenNum() - 1)
            {
                stack.push(' ');
            }
            else
            {
                stack.push('©¦');
            }
            parent = grandParent;
        }
        
        while(stack.size() > 0)
        {
            debugStreamOut.print(stack.pop());
        }
    }
}
