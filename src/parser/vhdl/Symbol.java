/**
 * 
 * This file is based on the VHDL parser originally developed by
 * (c) 1997 Christoph Grimm,
 * J.W. Goethe-University Frankfurt
 * Department for computer engineering
 *
 **/
package parser.vhdl;

import parser.INameObject;

/**
 * A symbol - entry in symbol-table
 */
public class Symbol implements INameObject
{
    public static final int KIND_INVALID = -1;
   
    /**
     * symbol name
     */
    public String name;
    
    /**
     * symbol kind, available kinds: <br>
     * <b>function</b>, <b>procedure</b>, <b>variable</b>, <b>constant</b>, <b>type</b><br>
     * <b>attribute</b>, <b>alias</b>, <b>subtype</b>, <b>file</b>, <b>group</b><br>
     * <b>signal</b>, <b>component</b>, <b>disconnect</b>, <b>nature</b>, <b>terminal</b><br>
     * <b>subnature</b>
     * @see VhdlTokenConstants
     */
    public int kind;
    
    /**
     * constant/variable type, function return type, 
     */
    public String type;
    
    /**
     * value range
     */
    public String[] range;
    
    public Symbol()
    {
        this("name", KIND_INVALID);
    }
    
    public Symbol(String name, int kind)
    {
        this(name, kind, "");
    }
    
    public Symbol(String name, int kind, String type)
    {
        this(name, kind, type, null);
    }
    
    public Symbol(String name, int kind, String type, String[] range)
    {
        this.name = name;
        this.kind = kind;
        this.type = type;
        this.range = range;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
       this.name = name;
    }
}

