
import java.io.FileNotFoundException;
import java.io.IOException;

import converter.hdlConverter;
import converter.verilog.Verilog;
import converter.vhdl.Vhdl;

import parser.IParser;
import parser.ParserException;

public class hdl2SystemC
{
    public static boolean testDir = true;
    public static void main(String[] args)
    {
        if(testDir) {
            hdlConverter conv = new Vhdl();
            conv.addLibary("grlib-gpl-1.0.21-b3848\\lib\\grlib", "grlib");
            conv.convertDir("grlib-gpl-1.0.21-b3848\\lib\\grlib");
        }else {
            try
            {
                hdlConverter conv = null;
                String name = "leaves";
                //String name = "ahbctrl";
                String path = name + ".vhd";
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
                    conv.addLibary("grlib-gpl-1.0.21-b3848\\lib\\grlib", "grlib");
                    conv.convertFile(path, name);
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
    }
    
    public static int getFileType(String path)
    {
        int index = path.lastIndexOf('.');
        if(index > 0)
        {
            String ext = path.substring(index + 1);
            if(ext.equalsIgnoreCase(IParser.EXT_VERILOG))
                return hdlConverter.T_VERILOG;
            else if(ext.equalsIgnoreCase(IParser.EXT_VHDL))
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
