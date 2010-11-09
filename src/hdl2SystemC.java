
import java.io.FileNotFoundException;
import java.io.IOException;

import common.MyDebug;

import converter.hdlConverter;
import converter.verilog.Verilog;
import converter.vhdl.Vhdl;

import parser.IParser;
import parser.ParserException;
import parser.vhdl.LibraryManager;

public class hdl2SystemC
{
    public static final boolean testDir = true;
    public static final boolean addLib = true;
    public static void main(String[] args)
    {
        MyDebug.init("vhdllog.txt");
        if(testDir) {
            hdlConverter conv = new Vhdl();
            String baseDir = "D:\\xzs\\mygrlib";
            if(addLib) {
                conv.addLibary(baseDir+"\\lib\\contrib", "contrib");
                conv.addLibary(baseDir+"\\lib\\cypress", "cypress");
                conv.addLibary(baseDir+"\\lib\\esa", "esa");
                conv.addLibary(baseDir+"\\lib\\eth", "eth");
                conv.addLibary(baseDir+"\\lib\\fmf", "fmf");
                conv.addLibary(baseDir+"\\lib\\gaisler", "gaisler");
                conv.addLibary(baseDir+"\\lib\\gleichmann", "gleichmann");
                conv.addLibary(baseDir+"\\lib\\grlib", "grlib");
                conv.addLibary(baseDir+"\\lib\\gsi", "gsi");
                conv.addLibary(baseDir+"\\lib\\hynix", "hynix");
                conv.addLibary(baseDir+"\\lib\\micron", "micron");
                conv.addLibary(baseDir+"\\lib\\openchip", "openchip");
                conv.addLibary(baseDir+"\\lib\\opencores", "opencores");
                conv.addLibary(baseDir+"\\lib\\spansion", "spansion");
                conv.addLibary(baseDir+"\\lib\\spw", "spw");
                conv.addLibary(baseDir+"\\lib\\techmap", "techmap");
                conv.addLibary(baseDir+"\\designs\\leon3mp", "dare");
                conv.addLibary(baseDir+"\\designs\\leon3-asic", "leon3-asic");
            }
            
            conv.convertDir(baseDir, baseDir + "\\SystemC");
        }else {
            try
            {
                hdlConverter conv = null;
                //String name = "amba";
                String name = "ahbctrl";
                //String name = "ahbtbp";
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
        MyDebug.exit();
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
