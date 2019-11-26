package model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 *
 * @author krish
 */
public class Log {
    
 //Tracks User activity, records Timestamp from Log-in to Log-out including inserting and updating records.
    
    public static void log(String log) throws IOException{
        
        String filename = "src/Log/log.txt";
        FileWriter fwrite = new FileWriter (filename, true);
        PrintWriter outFile = new PrintWriter(fwrite);
        outFile.println(log);
        outFile.close();
        System.out.println("Log Updated!");
        
    }
    
}
