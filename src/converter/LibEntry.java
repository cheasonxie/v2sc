package converter;

import java.util.ArrayList;

public class LibEntry extends ArrayList<SCSymbol>
{
    private static final long serialVersionUID = 1L;
    
    String filePath = "";
    String name = "";
    
    public LibEntry(String path, String name)
    {
        filePath = path;
        this.name = name;
    }
    
    public String getFilePath()
    {
        return filePath;
    }
    
    public String getName()
    {
        return name;
    }
    
    public SCSymbol get(String name)
    {
        for(int i = 0; i < size(); i++)
        {
            if(get(i).name.equalsIgnoreCase(name))
                return get(i);
        }
        return null;
    }
}
