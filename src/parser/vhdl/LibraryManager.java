package parser.vhdl;

import java.io.File;
import java.io.FileReader;

import parser.IASTNode;
import parser.INameObject;
import parser.IParser;
import parser.Token;

import common.FileList;

/**
 * package symbols entry
 */
class PackageEntry implements INameObject
{
    String name = "";
    SymbolTable table = null;
    
    public PackageEntry(String name, SymbolTable table)
    {
        this.name = name;
        this.table = table;
    }
    
    public PackageEntry(ASTNode pkgNode)
    {
        assert(pkgNode.getId() == VhdlASTConstants.ASTPACKAGE_DECLARATION);
        name = pkgNode.getName();
        table = pkgNode.getSymbolTable();
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Symbol getSymbol(String symbolName)
    {
        if(table == null)
            return null;
        return table.getSymbol(symbolName);
    }
    
    public Symbol[] getAllSymbols()
    {
        if(table == null || table.size() == 0)
            return null;
        return (Symbol[])table.toArray();
    }

    @Override
    public boolean equals(INameObject other) {
        if(!(other instanceof PackageEntry)) {
            return false;
        }
        return name.equalsIgnoreCase(((PackageEntry)other).getName());
    }
}

/**
 * library symbol entry
 */
class LibraryEntry extends VhdlArrayList<PackageEntry> implements INameObject
{
    private static final long serialVersionUID = 4599069745863337895L;
    String name = "";
    
    public LibraryEntry(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public boolean equals(INameObject other) {
        if(!(other instanceof LibraryEntry)) {
            return false;
        }
        return name.equalsIgnoreCase(((LibraryEntry)other).getName());
    }
}

/**
 * all libraries
 */
public class LibraryManager extends VhdlArrayList<LibraryEntry>
{
    private static final long serialVersionUID = -6123017095587268106L;
    static protected  LibraryManager libMgr = null;
    
    public static LibraryManager getInstance() {
        if(libMgr == null)
            libMgr = new LibraryManager();
        return libMgr;
    }
    
    protected IASTNode[] getPackageNode(IASTNode node, int level)
    {
        IASTNode[] ret = null;
        
        if(node == null || level >= 5)  //TODO modify the maximum level
            return null;
        
        if(node.getId() == VhdlASTConstants.ASTPACKAGE_DECLARATION) {
            ret = new IASTNode[1];
            ret[0] = node;
        }else {
            for(int i = 0; i < node.getChildrenNum(); i++)
            {
                IASTNode[] pkgNode = getPackageNode(node.getChild(i), level+1);
                if(pkgNode != null) {
                    if(ret == null) {
                        ret = pkgNode;
                    }else {
                        IASTNode[] ret0 = new IASTNode[ret.length+pkgNode.length];
                        System.arraycopy(ret0, 0, ret, 0, ret.length);
                        System.arraycopy(ret0, ret.length, pkgNode, 0, pkgNode.length);
                        ret = ret0;
                    }
                }
            }
        }
        return ret;
    }
    
    private String getFileName(String path)
    {
        File file = new File(path);
        String ret = file.getName();
        file = null;
        return ret;
    }
    
    public boolean add(String dir, String libName)
    {
        FileList list = new FileList(dir, IParser.EXT_VHDL);
        System.out.println("======file num:" + list.getFileNum() + "========");
        
        if(libName == null || libName.isEmpty())
            libName = getFileName(dir);    // use dir name as library name
        LibraryEntry lib = new LibraryEntry(libName);
        
        for(int i = 0; i < list.getFileNum(); i++)
        {
            String path = list.getFile(i);
            try {
                System.out.println("index:" + i + ", file:" + path);
                VhdlParser parser = new VhdlParser(new FileReader(path), true);
                ASTNode designFile = parser.design_file();
                IASTNode[] pkgNodes = getPackageNode(designFile, 0);
                if(pkgNodes != null) {
                    for(int j = 0; j < pkgNodes.length; j++) {
                        PackageEntry pkg = new PackageEntry((ASTNode)pkgNodes[j]);
                        lib.add(pkg);
                    }
                }
                designFile = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        add(lib);
        return true;
    }
    
    public Symbol[] getSymbol(ASTNode node) {
        if(node.getId() != VhdlASTConstants.ASTSELECTED_NAME) {
            return null;
        }
        
        String libName = "", pkgName = "", symbolName = "";
        Token token = node.first_token;
        while(!token.image.equals(".")) {
            libName += token.image;
            token = token.next;
        }
        
        token = token.next;
        while(!token.image.equals(".")) {
            pkgName += token.image;
            token = token.next;
        }
        
        token = token.next;
        while(token != node.last_token) {
            symbolName += token.image;
            token = token.next;
        }
        symbolName += token.image;
        
        return getSymbol(libName, pkgName, symbolName);
    }
    
    public Symbol[] getSymbol(String libName, String pkgName, String symbolName)
    {
        Symbol[] ret = null;

        for(int i = 0; i < size(); i++) {
            LibraryEntry lib = get(i);
            if(lib.getName().equalsIgnoreCase(libName)) {
                PackageEntry pkg = lib.get(pkgName);
                if(pkg != null) {
                    if(symbolName.equalsIgnoreCase("all")) {
                        ret = pkg.getAllSymbols();
                    }else {
                        Symbol[] syms = new Symbol[1];
                        syms[0] = pkg.getSymbol(symbolName);
                        ret = syms;
                    }
                    break;
                }
            }
        }
        return ret;
    }
}
