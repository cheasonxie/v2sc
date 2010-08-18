
import java.io.FileNotFoundException;
import java.io.IOException;

import converter.hdlConverter;
import converter.verilog.Verilog;
import converter.vhdl.Vhdl;

import parser.ParserException;

public class hdl2SystemC
{
    public static void main(String[] args)
    {
        try
        {
            hdlConverter conv = null;
            String path = "ahbctrl.vhd";
            //String path = "apbuart.vhd";
            //String path = "ac97_top.v";
            if(args.length > 1)
                path = args[1];
            switch(getFileType(path))
            {
            case hdlConverter.T_VERILOG:
                conv = new Verilog();
                break;
                
            case hdlConverter.T_VHDL:
                conv = new Vhdl();
                break;
                
            default:
            case hdlConverter.T_NONE:
                System.err.println("file type not support! : " + path);
                return;
            }
            
            if(conv != null)
            {
                conv.parseLibSymbols("grlib-gpl-1.0.21-b3848\\lib\\grlib");
                conv.convertFile(path, "a.cpp");
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (ParserException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public static int getFileType(String path)
    {
        int index = path.lastIndexOf('.');
        if(index > 0)
        {
            String ext = path.substring(index + 1);
            if(ext.equalsIgnoreCase(hdlConverter.EXT_VERILOG))
                return hdlConverter.T_VERILOG;
            else if(ext.equalsIgnoreCase(hdlConverter.EXT_VHDL))
                return hdlConverter.T_VHDL;
            else
                return hdlConverter.T_NONE;
        }
        else
        {
            return hdlConverter.T_NONE;
        }
    }
}
