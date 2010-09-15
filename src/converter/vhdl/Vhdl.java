package converter.vhdl;

import java.io.IOException;
import java.io.PrintStream;

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
    
    String preHeaderGuard(String name)
    {
        String ret = "";
        name = name.toUpperCase();
        name = "__" + name.replace('.', '_') + "__";
        ret += "#ifndef " + name + "\r\n";
        ret += "#define " + name;
        return ret;
    }
    
    String postHeaderGuard(String name)
    {
        String ret = "";
        name = name.toUpperCase();
        name = "__" + name.replace('.', '_') + "__";
        ret += "#endif  // " + "#ifndef " + name;
        return ret;
    }
    
    void saveHeader(ScDesign_file root, String dstPath)
    {
        String path = dstPath+".h";
        path.replace('\\', '/');
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
    
    void saveSource(ScDesign_file root, String dstPath)
    {
        try {
            createFile(dstPath+".cpp", true);
            PrintStream fileBuff = new PrintStream(dstPath+".cpp");
            fileBuff.println("#include \"" + dstPath + ".h\"");
            fileBuff.print(root.getImplements());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void convertDir(String srcDir)
    {
        
    }

    @Override
    public void addLibary(String srcDir, String libName)
    {
        LibraryManager.getInstance().add(srcDir, libName);
    }
}
