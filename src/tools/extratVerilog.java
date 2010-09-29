package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import parser.IASTNode;
import parser.IParser;
import parser.Token;
import parser.vhdl.VhdlASTConstants;
import parser.vhdl.VhdlTokenConstants;

public class extratVerilog {
    public static void main(String[] args)
    {
        test_astconstants(args);
    }
    
    static class AstNode
    {
        String name;
        ArrayList<String> content = new ArrayList<String>();
    }
    
    static void test_astconstants(String[] args)
    {
        String dir = System.getProperty("user.dir");
        
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader(dir + "\\verilog_syntax.txt"));
            
            ArrayList<AstNode> astArray = new ArrayList<AstNode>();
            String line = reader.readLine();
            while(line != null) {
                int index = line.indexOf("::=");
                if(index > 0) {
                    String name = line.substring(0, index - 1).trim();
                    name = name.replace('-', '_');
                    name = name.replace(' ', '_');
                    AstNode newNode = new AstNode();
                    newNode.name = name;
                    while(!line.isEmpty()) {
                        newNode.content.add(line);
                        line = reader.readLine();
                        if(line == null || line.indexOf("::=") > 0) {
                            break;
                        }
                    }
                    astArray.add(newNode);
                }else {
                    line = reader.readLine();
                }
            }
            
            writeInterface(dir, "VerilogASTConstants", astArray);
            writeParserClass(dir, "VerilogParser", astArray);
            
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    static void writeInterface(String dir, String className, ArrayList<AstNode> astArray)
    {
        if(astArray == null)
            return;
        
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(
                    new FileWriter(dir + "\\" + className + ".java"));
            String str = "";
            str += "package parser.verilog;\r\n\r\n";
            str += "public interface " + className + "\r\n{\r\n";
            for(int i = 0; i < astArray.size(); i++) {
                str += "    static final int ";
                str += "AST";
                if(!astArray.get(i).name.equalsIgnoreCase("identifier"))
                    str += astArray.get(i).name.toUpperCase();
                else
                    str += astArray.get(i).name;
                str += " = " + i + ";\r\n";
            }
            
            str += "\r\n    static final String[] ASTNodeName =\r\n";
            str += "    {\r\n";
            for(int i = 0; i < astArray.size(); i++) {
                str += "        \"" + astArray.get(i).name  + "\"";
                str += ",\r\n";
            }
            str += "    };\r\n";
            str += "}\r\n";
            writer.write(str);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static void writeParserClass(String dir, String className, ArrayList<AstNode> astArray)
    {
        if(astArray == null)
            return;
        
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(
                    new FileWriter(dir + "\\" + className + ".java"));
            String str = "";
            str += "package parser.verilog;\r\n\r\n";
            str += "public class " + className;
            str += " implements IParser, VerilogTokenConstants, VerilogASTConstants\r\n{\r\n";
            for(int i = 0; i < astArray.size(); i++) {
                
                str += "    /**\r\n";
                for(int j = 0; j < astArray.get(i).content.size(); j++) {
                    str += "     * " + addBold(astArray.get(i).content.get(j));
                    if(j < astArray.get(i).content.size() - 1)
                        str += "<br>";
                    str += "\r\n";
                }
                str += "     */\r\n";
                
                String name = astArray.get(i).name;
                str += "    void " + name + "(IASTNode p, Token endToken) throws ParserException {\r\n";
                if(!astArray.get(i).name.equalsIgnoreCase("identifier"))
                    str += "        ASTNode node = new ASTNode(p, AST" + name.toUpperCase() + ");\r\n";
                else
                    str += "        ASTNode node = new ASTNode(p, AST" + name + ");\r\n";
                str += "        openNodeScope(node);\r\n";
                str += "        closeNodeScope(node);\r\n";
                str += "    }\r\n\r\n";
            }
            str += "}\r\n";
            writer.write(str);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static String addBold(String str) {
        String ret = "";
        int i = 0;
        if(str.isEmpty())
            return "";
        
        while(i < str.length() && str.charAt(i) == ' ') {
            ret += ' ';
            i ++;
        }
        
        StringTokenizer tkn = new StringTokenizer(str);
        while(tkn.hasMoreTokens()) {
            String temp = tkn.nextToken();
            for(i = 0; i < tokenImage.length; i++) {
                if(temp.equals(tokenImage[i])) {
                    ret += "<b>" + temp + "</b>";
                    break;
                }
            }
            
            if(i >= tokenImage.length)
                ret += temp;
            ret += " ";
        }

        return ret;
    }
    
    static final String[] tokenImage =
    {
        "always",
        "and",
        "assign",
        "begin",
        "buf",
        "bufif0",
        "bufif1",
        "case",
        "casex",
        "casez",
        "cmos",
        "deassign",
        "default",
        "defparam",
        "disable",
        "edge",
        "else",
        "end",
        "endcase",
        "endmodule",
        "endfunction",
        "endprimitive",
        "endspecify",
        "endtable",
        "endtask",
        "event",
        "for",
        "force",
        "forever",
        "fork",
        "function",
        "highz0",
        "highz1",
        "if",
        "initial",
        "inout",
        "input",
        "integer",
        "join",
        "large",
        "macromodule",
        "medium",
        "module",
        "nand",
        "negedge",
        "nmos",
        "nor",
        "not",
        "notif0",
        "notif1",
        "or",
        "output",
        "parameter",
        "pmos",
        "posedge",
        "primitive",
        "pull0",
        "pull1",
        "pullup",
        "pulldown",
        "rcmos",
        "reg",
        "release",
        "repeat",
        "rnmos",
        "rpmos",
        "rtran",
        "rtranif0",
        "rtranif1",
        "scalared",
        "small",
        "specify",
        "specparam",
        "strength",
        "strong0",
        "strong1",
        "supply0",
        "supply1",
        "table",
        "task",
        "time",
        "tran",
        "tranif0",
        "tranif1",
        "tri",
        "tri0",
        "tri1",
        "triand",
        "trior",
        "trireg",
        "vectored",
        "wait",
        "wand",
        "weak0",
        "weak1",
        "while",
        "wire",
        "wor",
        "xnor",
        "xor",
    };
}
