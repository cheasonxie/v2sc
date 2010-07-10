/**
 * 
 * This file is based on the VHDL parser originally developed by
 * (c) 1997 Christoph Grimm,
 * J.W. Goethe-University Frankfurt
 * Department for computer engineering
 *
 **/
package parser.vhdl;

/**
 * A symbol - entry in symbol-table
 */
public class Symbol implements VhdlTokenConstants
{
    public String name;
    public int kind;
    public Symbol(String id, int k)
    {
        name = id;
        kind = k;
    }
}

