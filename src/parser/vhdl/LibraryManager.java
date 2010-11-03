package parser.vhdl;

import java.io.File;
import java.util.ArrayList;

import parser.IASTNode;
import parser.IParser;

import common.FileList;
import common.MyDebug;

/**
 * all libraries
 */
public class LibraryManager
{
    private static final long serialVersionUID = 5288877947661058435L;
    
    static protected LibraryManager libMgr = null;
    static protected VhdlDataBase dataBase = null;
    
    public static LibraryManager getInstance() {
        if(libMgr == null) {
            libMgr = new LibraryManager();
            libMgr.addPredefinedPackage();
        }
        return libMgr;
    }
    
    protected VhdlDataBase getDb() {
        return dataBase;
    }
    
    private LibraryManager() {
        dataBase = new VhdlDataBase();
        dataBase.init();
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
        if(tabName == null || tabName.isEmpty() || tab == null || tab.children == null) {
            return false;
        }
        
        for(int i = 0; i < tab.children.size(); i++) {
            SymbolTable tab1 = tab.children.get(i);
            String tabName1 = tabName + "#" + tab1.getName(); 
            dataBase.newTable(tabName1, true);
            dataBase.insert(tabName1, (Symbol[])tab1.getAllSymbols());
            addChildren(tabName1, tab1);
        }
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
        System.out.println("======file num:" + list.getFileNum() + "========");
        
        if(libName == null || libName.isEmpty())
            libName = getFileName(dir);    // use dir name as library name
        libName = libName.toLowerCase();
        dataBase.newTable(libName, true);
        
        ArrayList<String> tabNames = new ArrayList<String>();
        ArrayList<ASTNode> localEntities = new ArrayList<ASTNode>();
        tabNames.add(libName);
        for(int i = 0; i < list.getFileNum(); i++) {
            String path = list.getFile(i);
            try {
                System.out.println("index:" + i + ", file:" + path);
                VhdlParser parser = new VhdlParser(true);
                ASTNode designFile = (ASTNode)parser.parse(path);
                ASTNode[] pkgNodes = getPackageNode(designFile, 0);
                if(pkgNodes != null) {
                    for(int j = 0; j < pkgNodes.length; j++) {
                        String tabName = libName + "#" + pkgNodes[j].getName().toLowerCase();
                        tabNames.add(tabName);
                        dataBase.insert(libName, new Symbol(pkgNodes[j].getName(), 
                                            VhdlTokenConstants.PACKAGE));
                        
                        dataBase.newTable(tabName, true);
                        SymbolTable tab = ((ASTNode)pkgNodes[j].getChild(0)).getSymbolTable();
                        dataBase.insert(tabName, (Symbol[])tab.getAllSymbols());
                        addChildren(tabName, tab);
                    }
                }
                
                localEntities.addAll(parser.getLocalUnits());
                parser.getLocalUnits().clear();
                designFile = null;
                System.gc();
            } catch (Exception e) {
                StackTraceElement[] stackEle = e.getStackTrace();
                MyDebug.printFileLine("stackEle.length:" + stackEle.length);
                if(stackEle.length > 7) {
                    e.printStackTrace();
                }
            }
        }
        
        for(int i = 0; i < localEntities.size(); i++) {
            String name = localEntities.get(i).getName().toLowerCase();
            int j = 0;
            for(j = 0; j < tabNames.size(); j++) {
                if(dataBase.retrive(tabNames.get(j), name) != null) {
                    break;
                }
            }
            
            // not found in package
            if(j >= tabNames.size()) {
                if(localEntities.get(i).id != VhdlASTConstants.ASTENTITY_DECLARATION) {
                    MyDebug.printFileLine("not an entity error");
                }
                dataBase.insert(libName, new Symbol(name, VhdlTokenConstants.COMPONENT));
                
                String tabName = libName + "#" + name;
                tabNames.add(tabName);
                
                dataBase.newTable(tabName, false);
                SymbolTable node = ((ASTNode)localEntities.get(i).getChild(0)).getSymbolTable();
                dataBase.insert(tabName, (Symbol[])node.getAllSymbols());
                addChildren(tabName, localEntities.get(i).getSymbolTable());
            }
        }
        localEntities.clear();
        System.gc();
        return true;
    }
    
    /**
     * find user defined library which includes specified symbol
     * @param symName name of symbol, may be package, component.
     * @return library name if found, elsewhere null; 
     */
    public String findWorkLibrary(String symName) {
        String ret = null;
        String[] tables = dataBase.getAllTables();
        if(tables == null || tables.length == 0) {
            return null;
        }
        for(int i = 0; i < tables.length; i++) {
            String tabName = tables[i];
            if(tabName.indexOf('#') >= 0)
                continue;   // not library table
            if(dataBase.retrive(tabName, symName) != null)
                return tabName;
        }
        return ret;
    }
    
    protected boolean addPredefinedPackage() {
        PrePkg[] prePkg = PredefinedPackage.predefined_pkgs;
        for(int i = 0; i < prePkg.length; i++) {
            String libName = prePkg[i].libName.toLowerCase();
            String pkgName = prePkg[i].pkgName.toLowerCase();
            dataBase.newTable(libName, true);
            
            dataBase.insert(libName, new Symbol(pkgName, VhdlTokenConstants.PACKAGE));
            String tabName = libName + "#" + pkgName;
            
            dataBase.newTable(tabName, true);
            for(int j = 0; j < prePkg[i].syms.length; j++) {
                dataBase.insert(tabName, prePkg[i].syms[j]);
            }
            //Symbol[] tmpSyms = dataBase.retrive(tabName);
            //MyDebug.printFileLine("tabName:" + tabName + ", count:" + tmpSyms.length);
        }
        return true;
    }
}
