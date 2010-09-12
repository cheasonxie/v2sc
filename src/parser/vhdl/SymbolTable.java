/**
 * 
 * This file is based on the VHDL parser originally developed by
 * (c) 1997 Christoph Grimm,
 * J.W. Goethe-University Frankfurt
 * Department for computer engineering
 *
 **/
package parser.vhdl;

import java.util.ArrayList;
import java.util.HashMap;

import parser.ISymbol;
import parser.ISymbolTable;

public class SymbolTable extends VhdlArrayList<Symbol> implements ISymbolTable
{
    private static final long serialVersionUID = 2819894900029453741L;
    
    SymbolTable parent = null;
    HashMap<String, SymbolTable> subTable = null;
    
    public SymbolTable() {}
    public SymbolTable(SymbolTable p) {
        parent = p;
    }
    
    /**
     * add one table to subtable <br>
     * 1. package need subtable(such as component's port_list/generic_list)
     * 2. record, physical, enum need subtable
     */
    public void addSubtable(String name, SymbolTable table) {
        if(subTable == null) {
            subTable = new HashMap<String, SymbolTable>();
        }
        subTable.put(name, table);
    }
    
    /**
     * copy another's subtable to my subtable<br>
     * 1. package need subtable(such as component's port_list/generic_list)
     * 2. record, physical, enum need subtable
     */
    public void copySubtable(SymbolTable other, Symbol[] symbols) {
        if(other == null || symbols == null) {
            return;
        }
        if(subTable == null) {
            subTable = new HashMap<String, SymbolTable>();
        }
        
        for(int i = 0; i < symbols.length; i++) {
            String name = symbols[i].getName();
            SymbolTable child = other.getSubtable(name);
            if(child != null) {
                subTable.put(name, child);
            }
        }
    }
    
    /**
     * get one subtable<br>
     * 1. package need subtable(such as component's port_list/generic_list)
     * 2. record, physical, enum need subtable
     */
    public SymbolTable getSubtable(String name) {
        if(name == null || name.isEmpty() || subTable == null) {
            return null;
        }
        return subTable.get(name);
    }
    
    /**
     * get symbol from subtable
     */
    private Symbol getSubtableSymbol(String name) {
        Symbol ret = null;
        if(subTable == null) {
            return null;
        }
        for(int i = 0; i < size(); i++) {
            SymbolTable table = subTable.get(get(i).name);
            if(table != null) {
                if((ret = table.get(name)) != null) {
                    return ret;
                }
                if((ret = table.getSubtableSymbol(name)) != null) {
                    return ret;
                }
            }
        }
        return null;
    }

    /**
     * get a symbol from the symbol table of specified name
     */
    public Symbol getSymbol(String name) {
        Symbol ret = get(name);
        if(ret != null)
            return ret;
        
        if((ret = getSubtableSymbol(name)) != null) {
            return ret;
        }
        
        if(parent != null) {
            return parent.getSymbol(name);
        }
      
        return null;
    }
    
    /**
     * get all symbols of specified kind
     */
    public ArrayList<Symbol> getKindSymbols(int kind) {
        ArrayList<Symbol> syms = new ArrayList<Symbol>();
        for(int i = 0; i < size(); i++) {
            if(get(i).kind == kind) {
                syms.add(get(i));
            }
        }
        
        if(parent != null) {
            syms.addAll(parent.getKindSymbols(kind));
        }
        return syms;
    }
    
    /**
     * get symbol table which actually contains the symbol of specified name
     */
    public SymbolTable getTableOfSymbol(String name) {
        if(get(name) != null)
            return this;
        
        if(subTable != null) {    // component's port/generic symbol
            for(int i = 0; i < size(); i++) {
                SymbolTable table = subTable.get(get(i).name);
                if(table != null && table.get(name) != null) {
                    return table;
                }
            }
        }
        
        if(parent != null) {
            return parent.getTableOfSymbol(name);
        } 
        return null;
    }
    
    public SymbolTable getParent() {
        return parent;
    }
    
    public void setParent(SymbolTable sym) {
        parent = sym;
    }
    
    @Override
    public int getSize()
    {
        return size();
    }
    
    @Override
    public Symbol getSymbol(int i)
    {
        return get(i);
    }
}

