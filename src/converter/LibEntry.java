package converter;

import java.util.ArrayList;

public class LibEntry
{
    String filePath = "";
    ArrayList<SCSymbol> symbols = new ArrayList<SCSymbol>();
    String packageName = "";
    
    public LibEntry(String path, String name)
    {
        filePath = path;
        packageName = name;
    }
    
    public String getFilePath()
    {
        return filePath;
    }
    
    public String getPackageName()
    {
        return packageName;
    }
    
    public void addSymbol(SCSymbol s)
    {
        symbols.add(s);
    }
    
    public SCSymbol getSymbol(String name)
    {
        for(int i = 0; i < symbols.size(); i++)
        {
            if(symbols.get(i).name.equalsIgnoreCase(name))
                return symbols.get(i);
        }
        return null;
    }
}
