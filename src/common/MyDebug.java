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
            stream.flush();
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
    
    public static void printStackTrace()
    {
        stream.println("**********start************");
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        for (int i=0; i < stacks.length; i++)
            stream.println("\tat " + stacks[i]);
        stream.println("**********end************");
    }
    
    public static void printStackTrace(Exception e)
    {
        if(e == null)
            return;
        stream.println("**********start************");
        stream.println(e);
        StackTraceElement[] stacks = e.getStackTrace();
        for (int i=0; i < stacks.length; i++)
            stream.println("\tat " + stacks[i]);

        Throwable ourCause = e.getCause();
        if (ourCause != null)
            stream.println("Caused by: " + ourCause);
        stream.println("**********end************");
    }
}
