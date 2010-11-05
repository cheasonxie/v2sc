package parser.vhdl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import parser.IASTNode;
import parser.IParser;

import common.FileList;
import common.MyDebug;

/**
 * all libraries
 */
public class LibraryManager
{
    static LibraryManager libMgr = null;
    static HashMap<String, ArrayList<Symbol>> symbolMap = new HashMap<String, ArrayList<Symbol>>();
    
    public static LibraryManager getInstance() {
        if(libMgr == null) {
            libMgr = new LibraryManager();
            libMgr.loadAll();
            libMgr.addPredefinedPackage();
        }
        return libMgr;
    }
    
    protected void finalize() {
        if(libMgr != null) {
            libMgr.writeAll();
        }
    }
    
    protected ASTNode[] getPackageNode(IASTNode node, int level) {
        ASTNode[] ret = null;
        
        if(node == null || level >= 5)  //TODO modify the maximum level
            return null;
        
        if(node.getId() == VhdlASTConstants.ASTPACKAGE_DECLARATION) {
            ret = new ASTNode[1];
            ret[0] = (ASTNode)node;
        }else {
            for(int i = 0; i < node.getChildrenNum(); i++)
            {
                ASTNode[] pkgNode = getPackageNode(node.getChild(i), level+1);
                if(pkgNode != null) {
                    if(ret == null) {
                        ret = pkgNode;
                    }else {
                        ASTNode[] ret0 = new ASTNode[ret.length+pkgNode.length];
                        System.arraycopy(ret, 0, ret0, 0, ret.length);
                        System.arraycopy(pkgNode, 0, ret0, ret.length, pkgNode.length);
                        ret = ret0;
                    }
                }
            }
        }
        return ret;
    }
    
    private String getFileName(String path) {
        File file = new File(path);
        String ret = file.getName();
        file = null;
        return ret;
    }
    
    private boolean addChildren(String tabName, SymbolTable tab) {
        if(tabName == null || tabName.isEmpty() || tab == null 
                || tab.children == null) {
            return false;
        }
        
        for(int i = 0; i < tab.children.size(); i++) {
            SymbolTable tab1 = tab.children.get(i);
            String tabName1 = tabName + "#" + tab1.getName();
            addTable(tabName1, tab);
        }
        return true;
    }
    
    private boolean addTable(String tabName, SymbolTable tab) {
        ArrayList<Symbol> symsArray = symbolMap.get(tabName);
        if(symsArray == null) {
            symsArray = new ArrayList<Symbol>();
            symbolMap.put(tabName, symsArray);
        }
        
        Symbol[] syms = (Symbol[])tab.getAllSymbols();
        if(syms != null) {
            for(int i = 0; i < syms.length; i++) {
                symsArray.add(syms[i]);
            }
        }
        addChildren(tabName, tab);
        return true;
    }
    
    private boolean addPackageOrEntity(String tabName, ASTNode pkgNode) {
        SymbolTable tab = ((ASTNode)pkgNode.getChild(0)).getSymbolTable();
        addTable(tabName, tab);
        return true;
    }
    
    /**
     * load all symbols in database to memory
     */
    public boolean loadAll() {
        VhdlDataBase db = new VhdlDataBase();
        db.init();
        
        String[] tabs = db.getAllTables(null);
        if(tabs == null || tabs.length == 0) {
            return false;
        }
        
        for(int i = 0; i < tabs.length; i++) {
            Symbol[] syms = db.retrive(tabs[i]);
            if(syms == null || syms.length == 0) {
                continue;
            }
            ArrayList<Symbol> symsArray = new ArrayList<Symbol>();
            for(int j = 0; j < syms.length; j++) {
                symsArray.add(syms[i]);
            }
            symbolMap.put(tabs[i], symsArray);
        }
        db.exit();
        return true;
    }
    
    /**
     * write back to database from memory
     */
    public boolean writeAll() {
        VhdlDataBase db = new VhdlDataBase();
        db.init();
        db.beginBatch();
        
        Set<String> keys = symbolMap.keySet();
        Iterator<String> keyIter = keys.iterator();
        while (keyIter.hasNext()) {
           String tabName = keyIter.next();
           ArrayList<Symbol> syms = symbolMap.get(tabName);
           if(syms == null) {
               continue;
           }
           db.newTable(tabName, true);
           for(int i = 0; i < syms.size(); i++) {
               db.insert(tabName, syms.get(i));
           }
        }
        db.endBatch();
        db.exit();
        return true;
    }
    
    /**
     * add one library
     * @param dir: library directory
     * @param libName: library name
     * @return
     */
    public boolean add(String dir, String libName) {
        FileList list = new FileList(dir, IParser.EXT_VHDL);
        MyDebug.printFileLine("======file num:" + list.getFileNum() + "========");
        
        if(libName == null || libName.isEmpty())
            libName = getFileName(dir);    // use dir name as library name
        libName = libName.toLowerCase();
        ArrayList<Symbol> libSyms = symbolMap.get(libName);
        if(libSyms == null) {
            libSyms = new ArrayList<Symbol>();
            symbolMap.put(libName, libSyms);
        }
        
        ArrayList<String> tabNames = new ArrayList<String>();
        ArrayList<ASTNode> localEntities = new ArrayList<ASTNode>();
        tabNames.add(libName);
        for(int i = 0; i < list.getFileNum(); i++) {
            String path = list.getFile(i);
            try {
                MyDebug.printFileLine("index:" + i + ", file:" + path);
                VhdlParser parser = new VhdlParser(true);
                ASTNode designFile = (ASTNode)parser.parse(path);
                ASTNode[] pkgNodes = getPackageNode(designFile, 0);
                if(pkgNodes != null) {
                    for(int j = 0; j < pkgNodes.length; j++) {
                        String tabName = libName + "#" + pkgNodes[j].getName().toLowerCase();
                        tabNames.add(tabName);
                        libSyms.add(new Symbol(pkgNodes[j].getName(), VhdlTokenConstants.PACKAGE));
                        
                        addPackageOrEntity(tabName, pkgNodes[i]);
                    }
                }
                
                localEntities.addAll(parser.getLocalUnits());
                parser.getLocalUnits().clear();
                designFile = null;
                parser = null;
                System.gc();
            } catch (Exception e) {
                StackTraceElement[] stackEle = e.getStackTrace();
                MyDebug.printFileLine("stackEle.length:" + stackEle.length);
                if(stackEle.length > 7) {
                    MyDebug.printStackTrace(e);
                }
            }
        }
        
        for(int i = 0; i < localEntities.size(); i++) {
            String name = localEntities.get(i).getName().toLowerCase();
            int j = 0;
            for(j = 0; j < tabNames.size(); j++) {
                if(getLibSymbol(tabNames.get(j), name) != null) {
                    break;
                }
            }
            
            // not found in package
            if(j >= tabNames.size()) {
                if(localEntities.get(i).id != VhdlASTConstants.ASTENTITY_DECLARATION) {
                    MyDebug.printFileLine("not an entity error");
                }
                libSyms.add(new Symbol(name, VhdlTokenConstants.COMPONENT));
                
                String tabName = libName + "#" + name;
                tabNames.add(tabName);
                
                addPackageOrEntity(tabName, localEntities.get(i));
            }
        }
        localEntities.clear();
        tabNames.clear();
        System.gc();
        return true;
    }
    
    /**
     * find user defined library which contains specified symbol
     * @param symName name of symbol, may be package, component.
     * @return library name if found, elsewhere null; 
     */
    public String findWorkLibrary(String symName) {
        Iterator<String> nameIter = symbolMap.keySet().iterator();
        while (nameIter.hasNext()) {
           String tabName = nameIter.next();
           if(getLibSymbol(tabName, symName) != null) {
               return tabName;
           }
        }
        
        return null;
    }
    
    protected boolean addPredefinedPackage() {
        PrePkg[] prePkg = PredefinedPackage.predefined_pkgs;
        for(int i = 0; i < prePkg.length; i++) {
            String libName = prePkg[i].libName.toLowerCase();
            String pkgName = prePkg[i].pkgName.toLowerCase();
            
            ArrayList<Symbol> libSyms = symbolMap.get(libName);
            if(libSyms == null) {
                libSyms = new ArrayList<Symbol>();
                symbolMap.put(libName, libSyms);
            }
            
            libSyms.add(new Symbol(pkgName, VhdlTokenConstants.PACKAGE));
            String tabName = libName + "#" + pkgName;
            
            ArrayList<Symbol> pkgSyms = symbolMap.get(tabName);
            if(pkgSyms == null) {
                pkgSyms = new ArrayList<Symbol>();
                symbolMap.put(tabName, pkgSyms);
            }
            
            for(int j = 0; j < prePkg[i].syms.length; j++) {
                pkgSyms.add(prePkg[i].syms[j]);
            }
            //Symbol[] tmpSyms = dataBase.retrive(tabName);
            //MyDebug.printFileLine("tabName:" + tabName + ", count:" + tmpSyms.length);
        }
        return true;
    }
    
    public Symbol[] getLibSymbol(String tabName, String symName) {
        ArrayList<Symbol> syms = symbolMap.get(tabName);
        if(syms == null) {
            return null;
        }
        
        if(symName == null || symName.isEmpty()) {
            if(syms.size() > 0)
                return syms.toArray(new Symbol[syms.size()]);
            else
                return null;
        }
        
        ArrayList<Symbol> ret = new ArrayList<Symbol>();
        for(int i = 0; i < syms.size(); i++) {
            if(syms.get(i).name.equalsIgnoreCase(symName)) {
                ret.add(syms.get(i));
            }
        }
        
        if(ret.size() > 0)
            return ret.toArray(new Symbol[ret.size()]);
        else
            return null;
    }
    
    public boolean isTableExist(String tabName) {
        return (symbolMap.get(tabName) != null);
    }
}
