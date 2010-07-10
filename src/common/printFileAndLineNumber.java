package common;

public class printFileAndLineNumber
{
     public printFileAndLineNumber()
     {
         StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
         String fileName = stacks[2].getFileName();
         String lineNum = String.format("%d", stacks[2].getLineNumber());
         System.out.println(fileName + "--" + lineNum);
     }
     
     public printFileAndLineNumber(String msg)
     {
         StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
         String fileName = stacks[2].getFileName();
         String lineNum = String.format("%d", stacks[2].getLineNumber());
         System.out.println(fileName + "--" + lineNum + ": " + msg);
     }
}
