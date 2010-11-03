package common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * print file and line number
 */
public class MyDebug
{
    static PrintStream stream = System.out;
    public static void init(String path)
    {
        try {
            stream = new PrintStream(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static void exit()
    {
        if(stream != System.out)
            stream.close();
    }
    
    public static void printFileLine()
    {
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        String fileName = stacks[2].getFileName();
        String lineNum = String.format("%d", stacks[2].getLineNumber());
        stream.println(fileName + "--" + lineNum);
    }
     
    public static void printFileLine(String msg)
    {
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        String fileName = stacks[2].getFileName();
        String lineNum = String.format("%d", stacks[2].getLineNumber());
        stream.println(fileName + "--" + lineNum + ": " + msg);
    }
}
