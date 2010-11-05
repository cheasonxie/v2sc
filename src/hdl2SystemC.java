
import java.io.FileNotFoundException;
import java.io.IOException;

import common.MyDebug;

import converter.hdlConverter;
import converter.verilog.Verilog;
import converter.vhdl.Vhdl;

import parser.IParser;
import parser.ParserException;

public class hdl2SystemC
{
    public static final boolean testDir = true;
    public static final boolean addLib = false;
    public static void main(String[] args)
    {
        MyDebug.init("vhdllog.txt");
        if(testDir) {
            hdlConverter conv = new Vhdl();
            String baseDir = "E:\\grlib-gpl-1.0.22-b4095\\lib";
            if(addLib) {
                conv.addLibary(baseDir+"\\tech", "tech");
                conv.addLibary(baseDir+"\\tech\\unisim", "unisim");
                conv.addLibary(baseDir+"\\tech\\altera", "altera");
                conv.addLibary(baseDir+"\\tech\\altera_mf", "altera_mf");
                conv.addLibary(baseDir+"\\tech\\apa", "apa");
                conv.addLibary(baseDir+"\\tech\\atc18", "atc18");
                conv.addLibary(baseDir+"\\tech\\axcelerator", "axcelerator");
                conv.addLibary(baseDir+"\\tech\\cycloneiii", "cycloneiii");
                conv.addLibary(baseDir+"\\tech\\dw02", "dw02");
                conv.addLibary(baseDir+"\\tech\\ec", "ec");
                conv.addLibary(baseDir+"\\tech\\eclipsee", "eclipsee");
                conv.addLibary(baseDir+"\\tech\\proasic3", "proasic3");
                conv.addLibary(baseDir+"\\tech\\simprim", "simprim");
                conv.addLibary(baseDir+"\\tech\\snps", "snps");
                conv.addLibary(baseDir+"\\tech\\stratixii", "stratixii");
                conv.addLibary(baseDir+"\\tech\\stratixiii", "stratixiii");
                conv.addLibary(baseDir+"\\tech\\umc18", "umc18");
                conv.addLibary(baseDir+"\\tech\\virage", "virage");            
                
                conv.addLibary(baseDir+"\\contrib", "contrib");
                conv.addLibary(baseDir+"\\cypress", "cypress");
                conv.addLibary(baseDir+"\\esa", "esa");
                conv.addLibary(baseDir+"\\eth", "eth");
                conv.addLibary(baseDir+"\\fmf", "fmf");
                conv.addLibary(baseDir+"\\gaisler", "gaisler");
                conv.addLibary(baseDir+"\\gleichmann", "gleichmann");
                conv.addLibary(baseDir+"\\grlib", "grlib");
                conv.addLibary(baseDir+"\\gsi", "gsi");
                conv.addLibary(baseDir+"\\hynix", "hynix");
                conv.addLibary(baseDir+"\\micron", "micron");
                conv.addLibary(baseDir+"\\openchip", "openchip");
                conv.addLibary(baseDir+"\\opencores", "opencores");
                conv.addLibary(baseDir+"\\spansion", "spansion");
                conv.addLibary(baseDir+"\\spw", "spw");
                conv.addLibary(baseDir+"\\synplify", "synplify");
                conv.addLibary(baseDir+"\\techmap", "techmap");
    
                conv.addLibary(baseDir+"\\techmap\\altera_mf", "altera_mf");
                conv.addLibary(baseDir+"\\techmap\\apa", "apa");
                conv.addLibary(baseDir+"\\techmap\\atc18", "atc18");
                conv.addLibary(baseDir+"\\techmap\\axcelerator", "axcelerator");
                conv.addLibary(baseDir+"\\techmap\\clocks", "clocks");
                conv.addLibary(baseDir+"\\techmap\\cycloneiii", "cycloneiii");
                conv.addLibary(baseDir+"\\techmap\\dw02", "dw02");
                conv.addLibary(baseDir+"\\techmap\\ec", "ec");
                conv.addLibary(baseDir+"\\techmap\\eclipsee", "eclipsee");
                conv.addLibary(baseDir+"\\techmap\\gencomp", "gencomp");
                conv.addLibary(baseDir+"\\techmap\\inferred", "inferred");
                conv.addLibary(baseDir+"\\techmap\\maps", "maps");
                conv.addLibary(baseDir+"\\techmap\\proasic3", "proasic3");
                conv.addLibary(baseDir+"\\techmap\\stratixii", "stratixii");
                conv.addLibary(baseDir+"\\techmap\\stratixiii", "stratixiii");
                conv.addLibary(baseDir+"\\techmap\\umc18", "umc18");
                conv.addLibary(baseDir+"\\techmap\\unisim", "unisim");
                conv.addLibary(baseDir+"\\techmap\\virage", "virage");
            }
            
            conv.convertDir(baseDir, baseDir + "\\SystemC");
        }else {
            try
            {
                hdlConverter conv = null;
                //String name = "amba";
                String name = "ahbctrl";
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
