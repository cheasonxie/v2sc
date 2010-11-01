package parser.vhdl;

import java.io.File;
import java.util.ArrayList;

import parser.IASTNode;
import parser.IParser;

import common.FileList;

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
            //libMgr.addPredefinedPackage();
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
                        
                        dataBase.newTable(tabName, true);
                        SymbolTable node = pkgNodes[j].getSymbolTable();
                        dataBase.insert(tabName, (Symbol[])node.getAllSymbols());
                        dataBase.insert(libName, new Symbol(pkgNodes[j].getName(), 
                                            VhdlTokenConstants.PACKAGE));
                    }
                }
                
                localEntities.addAll(parser.getLocalUnits());
                parser.getLocalUnits().clear();
                designFile = null;
                System.gc();
            } catch (Exception e) {
                StackTraceElement[] stackEle = e.getStackTrace();
                System.err.println("stackEle.length:" + stackEle.length);
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
                    System.err.println("not an entity error");
                }
                dataBase.insert(libName, new Symbol(name, VhdlTokenConstants.COMPONENT));
                
                String tabName = libName + "#" + name;
                tabNames.add(tabName);
                
                dataBase.newTable(tabName, false);
                SymbolTable node = localEntities.get(i).getSymbolTable();
                dataBase.insert(tabName, (Symbol[])node.getAllSymbols());
            }
        }
        localEntities.clear();
        System.gc();
        return true;
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
        }
        return true;
    }
    
}
