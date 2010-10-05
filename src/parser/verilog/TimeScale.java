package parser.verilog;

public class TimeScale
{
    int unitNum;
    String unit;
    int precisionNum;
    String precision;
    
    public TimeScale(int unitNum, String unit, int precisionNum,
            String precision)
    {
        this.unitNum = unitNum;
        this.unit = unit;
        this.precisionNum = precisionNum;
        this.precision = precision;
    }
}
