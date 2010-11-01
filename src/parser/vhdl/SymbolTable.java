package parser.vhdl;

import java.util.ArrayList;

import parser.ISymbol;
import parser.ISymbolTable;

public class SymbolTable implements ISymbolTable
{
    private static final long serialVersionUID = -5606826108996410370L;
    
    String name = "";
    SymbolTable parent = null;
    String tabName = "tabName";
    int symNum = 0;
    
    static VhdlDataBase db = LibraryManager.getInstance().getDb();
    
    public SymbolTable(SymbolTable p, String name) {
        parent = p;
        this.name = name;
        tabName = getTableName();
        db.newTable(tabName, false);
    }

    public String getName() {
        return name;
    }
    
    public void setParent(SymbolTable p) {
        parent = p;
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
        boolean ret = db.insert(tabName, (Symbol)sym);
        symNum ++;
        return ret;
    }
    
    @Override
    public ISymbol getSymbol(String name) {
        Symbol[] ret = db.retrive(tabName, name);
        if(ret != null)
            return ret[0];
        if(parent != null)
            return parent.getSymbol(name);
        return null;
    }

    @Override
    public ISymbol[] getAllSymbols() {
        return db.retrive(tabName);
    }
    
    public Symbol[] getKindSymbols(int kind) {
        Symbol[] syms = db.retrive(tabName);
        if(syms == null) {
            return null;
        }
        
        ArrayList<Symbol> symArray = new ArrayList<Symbol>();
        for(int i = 0; i < syms.length; i++) {
            if(syms[i].kind == kind) {
                symArray.add(syms[i]);
            }
        }
        
        if(symArray.size() == 0) {
            return null;
        }else {
            return symArray.toArray(new Symbol[symArray.size()]);
        }
    }
    
    public SymbolTable getTableOfSymbol(String name) {
        Symbol[] ret = db.retrive(tabName, name);
        if(ret != null)
            return this;
        if(parent != null)
            return parent.getTableOfSymbol(name);
        return null;
    }
}
