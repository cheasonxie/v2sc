package parser.vhdl;

import java.util.ArrayList;
import parser.INameObject;

/**
 * no reduplicated elements ArrayList
 */
public class VhdlArrayList<E extends INameObject> extends ArrayList<E>
{
    private static final long serialVersionUID = 6099154330118133178L;
    
    @Override
    public boolean add(E e)
    {
        if(e == null) {
            return false;
        }
        for(int i = 0; i < size(); i++) {
            if(e.getName().equalsIgnoreCase(get(i).getName())) {
                return false;
            }
        }
        return super.add(e);
    }
    
    public E get(String name)
    {
        if(name == null || name.isEmpty()) {
            return null;
        }
        for(int i = 0; i < size(); i++) {
            if(name.equalsIgnoreCase(get(i).getName())) {
                return get(i);
            }
        }
        return null;
    }
}
