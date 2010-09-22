package converter.vhdl;

import java.io.IOException;
import java.io.PrintStream;

import common.FileList;

import parser.IParser;
import parser.ParserException;
import parser.vhdl.LibraryManager;
import parser.vhdl.VhdlParser;
import converter.hdlConverter;

public class Vhdl extends hdlConverter {
    
    @Override
    public void convertFile(String srcPath, String dstPath)
            throws ParserException, IOException
    {
        System.out.println("convertFile:" + srcPath);
        VhdlParser parser = new VhdlParser(false);
        parser.parse(srcPath);
        ScDesign_file root = new ScDesign_file(parser);
        
        saveHeader(root, dstPath);
        saveSource(root, dstPath);
    }
    
    private String preHeaderGuard(String name)
    {
        String ret = "";
        name = name.toUpperCase();
        name = "__" + name.replace('.', '_') + "__";
        ret += "#ifndef " + name + "\r\n";
        ret += "#define " + name;
        return ret;
    }
    
    private String postHeaderGuard(String name)
    {
        String ret = "";
        name = name.toUpperCase();
        name = "__" + name.replace('.', '_') + "__";
        ret += "#endif  // " + "#ifndef " + name;
        return ret;
    }
    
    private void saveHeader(ScDesign_file root, String dstPath)
    {
        String path = dstPath+".h";
        path = path.replace('\\', '/');
        int index = path.lastIndexOf('/');
        String name = "";
        if(index > 0)
            name = path.substring(index+1);
        else
            name = path;
        try {
            createFile(path, true);
            PrintStream fileBuff = new PrintStream(path);
            fileBuff.println(preHeaderGuard(name));
            fileBuff.println(root.getInclude());
            fileBuff.print(root.getDeclaration());
            fileBuff.println(postHeaderGuard(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void saveSource(ScDesign_file root, String dstPath)
    {
        try {
            String buff = root.getImplements();
            if(buff.isEmpty())
                return;
            createFile(dstPath+".cpp", true);
            PrintStream fileBuff = new PrintStream(dstPath+".cpp");
            String path = dstPath+".h";
            path = path.replace('\\', '/');
            int index = path.lastIndexOf('/');
            String name = "";
            if(index > 0)
                name = path.substring(index+1);
            else
                name = path;
            fileBuff.println("#include \"" + name + "\"");
            fileBuff.print(buff);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void convertDir(String srcDir)
    {
        FileList list = new FileList(srcDir, IParser.EXT_VHDL);
        for(int i = 0; i < list.getFileNum(); i++) {
            String path = list.getFile(i);
            path = path.toLowerCase();
            int index = path.lastIndexOf(IParser.EXT_VHDL);
            String dstPath = path.substring(0, index-1);
            try {
                convertFile(path, dstPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void addLibary(String srcDir, String libName)
    {
        LibraryManager.getInstance().add(srcDir, libName);
    }
}
