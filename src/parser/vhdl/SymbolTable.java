package parser.vhdl;

import java.util.ArrayList;
import java.util.StringTokenizer;

import common.MyDebug;

import parser.INameObject;
import parser.ISymbol;
import parser.ISymbolTable;

public class SymbolTable implements ISymbolTable, INameObject
{
    private static final long serialVersionUID = -5606826108996410370L;
    
    String name = "";
    SymbolTable parent = null;
    String tabName = "tabName";
    int symNum = 0;
    
    /**
     * whether this table is library, stand for where to get symbol
     * @value true: gets symbol from database
     * <br>false: get symbol from local ArrayList
     * @note set true if create instance from use_clause
     */
    boolean isLibraryTable = false;
    VhdlArrayList<Symbol> mysyms = null;
    VhdlArrayList<SymbolTable> chidren = null;
    
    private static VhdlDataBase db = LibraryManager.getInstance().getDb();
    
    public SymbolTable(SymbolTable p, String name) {
        this(p, name, false);
    }
    
    public SymbolTable(SymbolTable p, String name, boolean isLib) {
        parent = p;
        this.name = name;
        tabName = getTableName();
        isLibraryTable = isLib;
        if(!isLib) {
            mysyms = new VhdlArrayList<Symbol>();
            chidren = new VhdlArrayList<SymbolTable>();
            if(p != null) {
                p.chidren.add(this);
            }
        }else {
            if(!db.isTableExist(tabName)) {
                MyDebug.printFileLine("symbol table not exist:" + tabName);
            }
            //db.newTable(tabName, true);
        }
    }
    

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String n) {
        name = n;
    }
    
    @Override
    public boolean equals(INameObject other) {
        if(other == null || !(other instanceof SymbolTable))
            return false;
        return name.equals(other.getName());
    }
    
    public void setParent(SymbolTable p) {
        parent = p;
        if(!isLibraryTable && p != null) {
            p.chidren.add(this);
        }
    }
    
    public SymbolTable getParent() {
        return parent;
    }
    
    public String getTableName() {
        String ret = name;
        SymbolTable p = parent;
        while(p != null) {
            ret = p.name + "#" + ret;
            p = p.parent;
        }
        return ret;
    }
    
    public boolean addAll(SymbolTable other) {
        if(other == null)
            return false;
        ISymbol[] syms = other.getAllSymbols();
        if(syms == null)
            return false;
        for(int i = 0; i < syms.length; i++) {
            addSymbol(syms[i]);
        }
        return true;
    }
    
    @Override
    public boolean addSymbol(ISymbol sym) {
        boolean ret = true;
        if(!isLibraryTable)
            ret = mysyms.add((Symbol)sym);
        else
            ret = db.insert(tabName, (Symbol)sym);
        symNum ++;
        return ret;
    }
    
    /**
     * static method, get symbol by specified table name,
     * @param curTab current table
     * @param tableName table name, must be format like: a#b#c
     * @param name symbol name
     */
    public static Symbol getSymbol(SymbolTable curTab, String tableName, String name) {
        Symbol[] ret = db.retrive(tableName, name);
        if(ret != null)
            return ret[0];
        
        if(curTab == null || curTab.isLibraryTable)
            return null;
        
        SymbolTable p = curTab;
        SymbolTable p1 = p;
        while(p1 != null) { // forward to root
            p = p1;
            p1 = p1.parent;
        }
        
        StringTokenizer tkn = new StringTokenizer(tableName, "#");
        while(tkn.hasMoreTokens()) {
            String n = tkn.nextToken();
            SymbolTable[] temp = p.chidren.get(n);
            if(temp == null) {
                return null;
            }
            p = temp[0];
        }
        return (Symbol)p.getSymbol(name);
    }
    
    /**
     * static method, check whether symbol table exists
     */
    public static boolean isTableExist(SymbolTable curTab, String tabName) {
        if(db.isTableExist(tabName))
            return true;
        
        if(curTab == null || curTab.isLibraryTable)
            return false;
        
        return (curTab.tabName.indexOf(tabName) >= 0);
    }
    
    @Override
    public ISymbol getSymbol(String name) {
        Symbol[] ret = null;
        if(!isLibraryTable)
            ret = mysyms.get(name);
        else
            ret = db.retrive(tabName, name);
        if(ret != null)
            return ret[0];
        if(parent != null)
            return parent.getSymbol(name);
        return null;
    }

    @Override
    public ISymbol[] getAllSymbols() {
        if(!isLibraryTable)
            return mysyms.toArray(new Symbol[mysyms.size()]); 
        else
            return db.retrive(tabName);
    }
    
    public Symbol[] getKindSymbols(int kind) {
        ArrayList<Symbol> symArray = new ArrayList<Symbol>();
        if(!isLibraryTable) {
            for(int i = 0; i < mysyms.size(); i++) {
                if(mysyms.get(i).kind == kind) {
                    symArray.add(mysyms.get(i));
                }
            }
        }else {
            Symbol[] syms = db.retrive(tabName);
            if(syms == null) {
                return null;
            }
            for(int i = 0; i < syms.length; i++) {
                if(syms[i].kind == kind) {
                    symArray.add(syms[i]);
                }
            }
        }

        if(symArray.size() == 0) {
            return null;
        }else {
            return symArray.toArray(new Symbol[symArray.size()]);
        }
    }
    
    public SymbolTable getTableOfSymbol(String name) {
        Symbol[] ret = null;
        if(!isLibraryTable) {
            ret = mysyms.get(name);
        }else {
            ret = db.retrive(tabName, name);
        }
        if(ret != null)
            return this;
        if(parent != null)
            return parent.getTableOfSymbol(name);
        return null;
    }
}
