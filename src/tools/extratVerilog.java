package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;

import parser.IASTNode;
import parser.IParser;
import parser.Token;
import parser.vhdl.VhdlASTConstants;
import parser.vhdl.VhdlTokenConstants;

public class extratVerilog {
    public static void main(String[] args)
    {
        //test_astconstants(args);
        test_html(args);
    }
    
    static class AstNode
    {
        String name;
        ArrayList<String> content = new ArrayList<String>();
    }
    
    static class SortByName implements Comparator<AstNode>
    {
        public int compare(AstNode n1, AstNode n2){
            return n1.name.compareToIgnoreCase(n2.name);
        }
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
            
            Collections.sort(astArray, new SortByName());
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
                    String tmp = lowerName(astArray, astArray.get(i).content.get(j));
                    str += "     * " + addBold(tmp);
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
    
    
    static void test_html(String[] args)
    {
        /**
         * <A name="REF1"></A><B><A href="#REF1">&lt;source_text&gt;</A></B>
         *     ::= &lt;<A href="#REF2">description</A>&gt;*
         *
         * <A name="REF2"></A><B><A href="#REF2">&lt;description&gt;</A></B>
         *     ::= &lt;<A href="#REF3">module</A>&gt;
         *     ||= &lt;<A href="#REF12">UDP</A>&gt;
         */
        String validLine = "<A name=\"REF[a-zA-Z0-9</>_= \"]+<A href=\"#REF[a-zA-Z0-9&;.</>_= \"]+";
        
        String str = "<A name=\"REF1\"></A><B><A href=\"#REF1\">&lt;source_text&gt;</A></B>";
        if(str.matches(validLine))
            System.out.println("match!");
        else
            System.out.println("not match!");
        
        String str1 = "<H2><A name=\"REF0\"></A>1. Source Text</H2>";
        if(str1.matches(validLine))
            System.out.println("match!");
        else
            System.out.println("not match!");
        String dir = System.getProperty("user.dir");
        ArrayList<AstNode> astArray = new ArrayList<AstNode>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(dir + "\\src\\b.htm"));
            String line = reader.readLine();
            while(line != null) {
                if(line.matches(validLine)) {
                    String name = getAstName(line);
                    if(getIndex(astArray, name) >= 0 || name.indexOf("comment") >= 0) {
                        line = reader.readLine();
                        continue;
                    }
                    AstNode newNode = new AstNode();
                    newNode.name = name;
                    newNode.content.add(name);
                    while(true) {
                        line = reader.readLine();
                        if(line == null || line.matches(validLine) || line.isEmpty()) {
                            break;
                        }
                        String tmp = removeUnused(line);
                        newNode.content.add(tmp);
                    }
                    astArray.add(newNode);
                }else {
                    line = reader.readLine();
                }
            }
            
            Collections.sort(astArray, new SortByName());
            writeInterface(dir, "VerilogASTConstants", astArray);
            writeParserClass(dir, "VerilogParser", astArray);
                    
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static int getIndex(ArrayList<AstNode> astArray, String name)
    {
        int ret = -1;
        for(int i = 0; i < astArray.size(); i++) {
            if(astArray.get(i).name.equals(name)) {
                ret = i;
                break;
            }
        }
        return ret;
    }
    
    static String lowerName(ArrayList<AstNode> astArray, String str)
    {
        String ret = str;
        str = str.toUpperCase();
        for(int i = 0; i < astArray.size(); i++) {
            if(!astArray.get(i).name.equalsIgnoreCase("identifier")
                    && !astArray.get(i).name.equalsIgnoreCase("null"))
            {
                String tmp = "<" + astArray.get(i).name.toUpperCase() + ">";
                String tmp1 = "<" + astArray.get(i).name.toLowerCase() + ">";
                int index = str.indexOf(tmp);
                if(index >= 0) {
                    String tmp2 = ret.substring(index, index+tmp.length());
                    ret = ret.replaceAll(tmp2, tmp1);
                }
            }
        }
        return ret;
    }
    
    static String getAstName(String str)
    {
        int index1 = str.indexOf("&lt;");
        int index2 = str.indexOf("&gt;");
        if(index1 < 0 || index2 < 0)
            return "";
        
        String ret = str.substring(index1+4, index2).trim();
        ret = ret.replace('-', '_');
        ret = ret.replace(' ', '_');
        if(!ret.equalsIgnoreCase("identifier")
                && !ret.equalsIgnoreCase("null")) {
            ret = ret.toLowerCase();
        }
        return ret;
    }
    
    static String removeLink(String str, String token)
    {
        String ret = str;
        int idx1 = ret.indexOf("<" + token);
        while(idx1 >= 0) {
            String tmp = ret.substring(0, idx1);
            idx1 = ret.indexOf('>', idx1);
            int idx2 = -1;
            if(idx1 >= 0) {
                idx2 = ret.indexOf("</" + token + ">", idx1);
            }
            if(idx1 >= 0 && idx2 >= 0) {
                tmp += ret.substring(idx1+1, idx2) + ret.substring(idx2+4);
            }
            ret = tmp;
            idx1 = ret.indexOf("<" + token);
        }

        return ret;        
    }
    
    static String removeUnused(String str)
    {
        String amp = "&amp;";
        String gt = "&gt;";
        String lt = "&lt;";
        
        String ret = removeLink(str, "A");
        ret = removeLink(ret, "B");
        if(ret.isEmpty())
            return ret;
        
        ret = ret.replaceAll("<p>", "");
        ret = ret.replaceAll("</p>", "");
        ret = ret.replaceAll("</dt>", "");
        ret = ret.replaceAll("</dd>", "");
        ret = ret.replaceAll("<dt>", "<dl>");
        ret = ret.replace(amp, "&");
        ret = ret.replace(gt, ">");
        ret = ret.replace(lt, "<");
        ret = ret.replace("\t", "    ");
        return ret;        
    }
}
