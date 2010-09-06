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

public class SymbolTable extends VhdlArrayList<Symbol>
{
    private static final long serialVersionUID = 2819894900029453741L;
    
    SymbolTable parent = null;
    HashMap<String, SymbolTable> children = null;
    
    public SymbolTable() {}
    public SymbolTable(SymbolTable p) {
        parent = p;
    }
    
    /**
     * add one child to children table<br>
     * 1. package need child table(such as component's port_list/generic_list)
     * 2. record, physical, enum need child table
     */
    public void addChild(String name, SymbolTable child) {
        if(children == null) {
            children = new HashMap<String, SymbolTable>();
        }
        children.put(name, child);
    }
    
    /**
     * copy another's children to my children table<br>
     * 1. package need child table(such as component's port_list/generic_list)
     * 2. record, physical, enum need child table
     */
    public void copyChild(SymbolTable other, Symbol[] symbols) {
        if(other == null || symbols == null) {
            return;
        }
        if(children == null) {
            children = new HashMap<String, SymbolTable>();
        }
        
        for(int i = 0; i < symbols.length; i++) {
            String name = symbols[i].getName();
            SymbolTable child = other.getChild(name);
            if(child != null) {
                children.put(name, child);
            }
        }
    }
    
    /**
     * get one child table<br>
     * 1. package need child table(such as component's port_list/generic_list)
     * 2. record, physical, enum need child table
     */
    public SymbolTable getChild(String name) {
        if(children == null) {
            return null;
        }
        return children.get(name);
    }

    /**
     * Get a symbol from the symbol table by name
     */
    public Symbol getSymbol(String name) {
        Symbol ret = get(name);
        if(ret != null)
            return ret;
        
        if(parent != null) {
            ret = parent.getSymbol(name);
        }else if(children != null) {    // component's port/generic symbol
            for(int i = 0; i < children.size(); i++) {
                SymbolTable table = children.get(i);
                if((ret = table.get(name)) != null) {
                    break;
                }
            }
        }
        return ret;
    }
    
    /**
     * Get all symbols of specified kind
     */
    public ArrayList<Symbol> getSymbol(int kind) {
        ArrayList<Symbol> syms = new ArrayList<Symbol>();
        for(int i = 0; i < size(); i++) {
            if(get(i).kind == kind) {
                syms.add(get(i));
            }
        }
        
        if(parent != null) {
            syms.addAll(parent.getSymbol(kind));
        }else if(children != null) {    // component's port/generic symbol
            for(int i = 0; i < children.size(); i++) {
                syms.addAll(children.get(i));
            }
        }
        return syms;
    }
    
    /**
     * Get symbol table which actually contains the symbol by name
     */
    public SymbolTable getTableOfSymbol(String name) {
        SymbolTable ret = null;
        if(get(name) != null)
            return this;
        
        if(parent != null) {
            ret = parent.getTableOfSymbol(name);
        }else if(children != null) {    // component's port/generic symbol
            for(int i = 0; i < children.size(); i++) {
                SymbolTable table = children.get(i);
                if(table.get(name) != null) {
                    ret = table;
                    break;
                }
            }
        }
        return ret;
    }
    
    public SymbolTable getParent() {
        return parent;
    }
    
    public void setParent(SymbolTable sym) {
        parent = sym;
    }
}

