package converter;

public class SCSymbol implements SCTreeConstants
{
    public int type;
    public String name;
    public String[] range;
    public String value = "1";
    
    public SCSymbol(int type, String name, String[] range)
    {
        this.type = type;
        this.name = name;
        this.range = range;
    }
    
    public void setValue(String val)
    {
        value = val;
    }
}

