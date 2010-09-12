package parser.vhdl;

import java.io.File;

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
    
    public PackageEntry(String name)
    {
        this.name = name;
        table = new SymbolTable();
    }
    
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
        return (Symbol[])table.toArray(new Symbol[table.size()]);
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
        if(libMgr == null) {
            libMgr = new LibraryManager();
            libMgr.addPredefinedPackage();
        }
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
                        System.arraycopy(ret, 0, ret0, 0, ret.length);
                        System.arraycopy(pkgNode, 0, ret0, ret.length, pkgNode.length);
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
    
    /**
     * add one library
     * @param dir: library directory
     * @param libName: library name
     * @return
     */
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
                VhdlParser parser = new VhdlParser(true);
                ASTNode designFile = (ASTNode)parser.parse(path);
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
    
    protected boolean addPredefinedPackage() {
        PrePkg[] prePkg = PredefinedPackage.predefined_pkgs;
        for(int i = 0; i < prePkg.length; i++) {
            LibraryEntry lib = get(prePkg[i].libName);
            if(lib == null) {
                lib = new LibraryEntry(prePkg[i].libName);
                add(lib);
            }
            
            PackageEntry pkg = lib.get(prePkg[i].pkgName);
            if(pkg == null) {
                pkg = new PackageEntry(prePkg[i].pkgName);
                lib.add(pkg);
            }
            for(int j = 0; j < prePkg[i].syms.length; j++) {
                pkg.table.add(prePkg[i].syms[j]);
            }
        }
        return true;
    }
    
    public Symbol getSymbol(String symbolName) {
        String libName = "", pkgName = "";
        
        for(int i = 0; i < size(); i++) {
            libName = get(i).getName();
            for(int j = 0; j < get(i).size(); j++) {
                pkgName = get(i).get(j).getName();
                Symbol[] syms = getSymbols(libName, pkgName, symbolName);
                if(syms != null) {
                    return syms[0];
                }
            }
        }
        return null;
    }
    
    private String[] getSelectedNames(ASTNode node) {
        String[] ret = new String[3];
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
        ret[0] = libName;
        ret[1] = pkgName;
        ret[2] = symbolName;
        return ret;
    }
   
    public Symbol[] getSymbols(ASTNode node) {
        String[] names = getSelectedNames(node);
        if(names == null) {
            return null;
        }
        
        return getSymbols(names[0], names[1], names[2]);
    }
    
    protected Symbol[] getSymbols(String libName, String pkgName, String symbolName)
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
                        Symbol tmpSym = pkg.getSymbol(symbolName);
                        if(tmpSym != null) {
                            Symbol[] syms = new Symbol[1];
                            syms[0] = tmpSym;
                            ret = syms;
                        }
                    }
                    break;
                }
            }
        }
        return ret;
    }
    
    public SymbolTable getSymbolTable(ASTNode node) {
        String[] names = getSelectedNames(node);
        if(names == null) {
            return null;
        }
        
        return getSymbolTable(names[0], names[1], names[2]);
    }
    
    protected SymbolTable getSymbolTable(String libName, String pkgName, String symbolName)
    {
        SymbolTable ret = null;

        for(int i = 0; i < size(); i++) {
            LibraryEntry lib = get(i);
            if(lib.getName().equalsIgnoreCase(libName)) {
                PackageEntry pkg = lib.get(pkgName);
                if(pkg != null) {
                    if(symbolName.equalsIgnoreCase("all")) {
                        ret = pkg.table;
                    }else {
                        Symbol tmpSym = pkg.getSymbol(symbolName);
                        if(tmpSym != null) {
                            ret = pkg.table;    //TODO: create an symboltable
                        }
                    }
                    break;
                }
            }
        }
        return ret;
    }

}
