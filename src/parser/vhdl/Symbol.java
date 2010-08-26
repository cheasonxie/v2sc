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

import parser.INameObject;

/**
 * A symbol - entry in symbol-table
 */
public class Symbol implements INameObject
{
    /** invalid symbol kind */
    public static final int KIND_INVALID = -1;
   
    /**
     * symbol name
     */
    public String name;
    
    /**
     * symbol kind<br>
     * available kinds: <br>
     * <b>function</b>, <b>procedure</b>, <b>variable</b>, <b>constant</b>, <b>type</b><br>
     * <b>attribute</b>, <b>alias</b>, <b>subtype</b>, <b>file</b>, <b>group</b><br>
     * <b>signal</b>, <b>component</b>, <b>disconnect</b>, <b>nature</b>, <b>terminal</b><br>
     * <b>subnature</b>, <b>generic</b>, <b>port</b>
     * @see VhdlTokenConstants
     */
    public int kind;
    
    /**
     * data type(constant,variable, function return<br>
    */
    public String type;
    
    /**
     * value range(value limitation)
     */
    public String[] range = null;
    
    /**
     * type range(array index)
     */
    public String[] typeRange = null;
    
    /**
     * port mode(in/out/...)
     */
    public String mode = "";
    
    /**
     * param list(only used in function or procedure)
     */
    public ArrayList<String> paramTypeList = null;
    
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
    
    public boolean equals(INameObject other)
    {
        if(!(other instanceof Symbol)) {
            return false;
        }
        
        Symbol oth = (Symbol)other;
        if(kind != VhdlTokenConstants.FUNCTION 
                && kind != VhdlTokenConstants.PROCEDURE) {
            return name.equalsIgnoreCase(oth.name);
        }else {
            // function or procedure may overload
            return false;    //TODO: check function/procedure parameter type list
            /*
            boolean ret = (name.equalsIgnoreCase(oth.name)
                    && kind == oth.kind
                    && type.equalsIgnoreCase(oth.type));
            if(!ret)
                return false;
            
            // check parameter list
            if(paramTypeList != null && oth.paramTypeList != null) {
                if(paramTypeList.size() != oth.paramTypeList.size()) {
                    return false;
                }
                for(int i = 0; i < paramTypeList.size(); i++) {
                    if(!paramTypeList.get(i).equalsIgnoreCase(oth.paramTypeList.get(i))) {
                        return false;
                    }
                }
            }else if (paramTypeList == oth.paramTypeList) {
                ret = true;
            }
            return ret;
            */
        }
    }
    
    public void setParamList(ArrayList<String> paramList)
    {
        paramTypeList = paramList;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
       this.name = name;
    }
}

