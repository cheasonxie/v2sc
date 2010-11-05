package parser.vhdl;


import common.MyDebug;

import parser.ISymbol;

public class LibSymbolTable extends SymbolTable
{
    public LibSymbolTable(SymbolTable p, String name) {
        super(p, name);
        if(!libMgr.isTableExist(tabName)) {
            MyDebug.printFileLine("symbol table not exist:" + tabName);
        }
    }
    
    @Override
    public boolean addSymbol(ISymbol sym) {
        return false;    // forbid add symbol to library symbol table
    }
    
    @Override
    public ISymbol getSymbol(String name) {
        Symbol[] ret = libMgr.getLibSymbol(tabName, name);
        if(ret != null)
            return ret[0];
        if(parent != null)
            return parent.getSymbol(name);
        return null;
    }

    @Override
    public ISymbol[] getAllSymbols() {
        return libMgr.getLibSymbol(tabName, null); 
    }
    
    public SymbolTable getTableOfSymbol(String name) {
        if(libMgr.getLibSymbol(tabName, name) != null)
            return this;
        if(parent != null)
            return parent.getTableOfSymbol(name);
        return null;
    }
}
