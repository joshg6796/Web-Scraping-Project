import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

// This is the main class that the project will start with upon proper execution of code. If execution of code is successful, then the item numbers in the input file will be added to the main GUI
public class P3Main {
    public static void main(String args[]) throws IOException{
        String inputFile = "";
        String outputFile = "";
        
        //inspired by http://journals.ecs.soton.ac.uk/java/tutorial/java/cmdLineArgs/parsing.html
        //control for flags when executing code
        int i = 0; 
        while (i < args.length && args[i].startsWith("-")){
            if (args[i].equals("-i")){
                //check if there is an input file in the args array
                if (++i < args.length){
                    inputFile = args[i++];
                    // handles the case where java P3Main -i -o output.txt is entered on the command line
                    if (inputFile.equals("-o")){
                        System.err.println("Invalid arguments. Make sure your arguments are in the following format:\njava P3Main -i inputfile.txt -o outputfile.txt \n");
                        System.exit(0);
                    }
                }
                else{
                    System.err.println("Invalid arguments. Make sure your arguments are in the following format:\njava P3Main -i inputfile.txt -o outputfile.txt \n");
                    System.exit(0);
                }
            }
            else if (args[i].equals("-o")){
                //check if there is an output file in the args array
                if (++i < args.length){
                    outputFile = args[i++];
                } else{
                    outputFile = "output.txt";
                }
            }
            else{
                System.err.println("Invalid arguments. Make sure your arguments are in the following format:\njava P3Main -i inputfile.txt -o outputfile.txt \n");
                System.exit(0);
            }
        }
        if (args.length==0){
            System.err.println("Invalid arguments. Make sure your arguments are in the following format:\njava P3Main -i inputfile.txt -o outputfile.txt \n");
            System.exit(0);
        }
        
        MainGUI mGUI = new MainGUI("Goodwill Media Evaluation Project"); // instantiation the Main GUI display
        
        // if an output file name is not specified, call it "output.txt"
        if (outputFile.equals("")) outputFile = "output.txt";
        
        // add item numbers in input file to GUI
        HashMapQuery hmq = new HashMapQuery();
        hmq.addInputToGUI(inputFile, mGUI, outputFile);
    }
}
