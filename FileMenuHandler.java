import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

//This class will handle the action that it should take when an event takes place in MainGUI (in this case, the user clicking a File JMenu item)
public class FileMenuHandler implements ActionListener {
   MainGUI mGUI;


   // Constructor used to pass the MainGUI object in to allow access to it's methods.
   public FileMenuHandler (MainGUI m) {
      mGUI = m;
   }
   
   //Invoked (called) automatically when an action occurs in the File menu.
   public void actionPerformed(ActionEvent event) {
      
      String menuName = event.getActionCommand(); //get String associated with action 
      //if quit is clicked, write data to output file and terminate the program and also produce the logreconstruct file for future use
      if (menuName.equals("Quit")){
          try {
            HashMapQuery.saveImages();
            HashMapQuery.produceOutputFile();
            HashMapQuery.produceLogReconstructFile();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
          System.exit(0);
      }
      // if input is called, then a JFileChooser will be displayed to select an input file and the item numbers in the input file will be added to the GUI
      else if (menuName.equals("Input")){
          String inputFile = chooseFile(); 
          HashMapQuery hmq = new HashMapQuery();
          try {
              Map<Integer,TreeMap<String,String>> map = HashMapQuery.getMap();
              hmq.addInputToGUIFromGUI(inputFile, mGUI,map);
          }   catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          }
      }
      //reconstruct log works similarly to Input. Just load all of the item numbers from the result of the most recent file generated from produceLogReconstructFile()
      else if (menuName.equals("Reconstruct Log")){
          String inputFile = chooseFile(); 
          HashMapQuery hmq = new HashMapQuery();
          try {
              hmq.addInputToGUI(inputFile, mGUI);
          }   catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          }
      }
      else if (menuName.equals("View Inventory")){
          HashMapQuery.getCurrentInventory(mGUI);
      }
   }
   
   //inspired by https://www.mkyong.com/swing/java-swing-jfilechooser-example/
   //method that calls the JFileChooser
   private static String chooseFile() {
       String fname = null;
       JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
       jfc.setDialogTitle("Select a text file");
       jfc.setAcceptAllFileFilterUsed(false);
      
       //only allows .txt files to be selected
       FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT files", "txt");
       jfc.addChoosableFileFilter(filter);

       int returnValue = jfc.showOpenDialog(null);
       if (returnValue == JFileChooser.APPROVE_OPTION) {
           fname = jfc.getSelectedFile().toString();
       }
       return fname;
   }
}