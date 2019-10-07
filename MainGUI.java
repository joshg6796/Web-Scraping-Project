import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.TextArea;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

// this class contains the settings of the MainGUI along with the setting of the JMenu
public class MainGUI extends JFrame{
    private Container cont;
    private TextArea textArea = new TextArea();
    
    //constructor used to create objects for the class MainGUI and to set up the GUI
    public MainGUI(String title){
        setTitle(title);
        setSize(1000, 600);
        setLocation(100, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cont = getContentPane();
        cont.add(textArea, BorderLayout.CENTER);
        //pack();
        setVisible(true);
        textArea.setEditable(false);
        textArea.setBackground(Color.CYAN);
        createMenu();
    }
    
    //appends text into GUI
    public void appendToGUI(String s){
        textArea.append(s);
    }
    
    //clears data in GUI
    public void clear(){
        textArea.setText("");
    }
    
    //creates JMenu for GUI
    private void createMenu() {
        JMenuItem item;
        JMenuBar menuBar  = new JMenuBar(); //the menu bar which will appear in the GUI
        menuBar.setBackground(Color.blue);
        JMenu fileMenu = new JMenu("File"); //File menu button
        FileMenuHandler fmh  = new FileMenuHandler(this); //handler for File menu actions
      
        item = new JMenuItem("Input"); //Input button
        item.addActionListener(fmh); //assign listener to input button
        fileMenu.add(item); //put input button in File menu
        
        fileMenu.addSeparator(); //add a horizontal separator line
        
        item = new JMenuItem("View Inventory"); //View Inventory button
        item.addActionListener(fmh); //assign listener to View Inventory button
        fileMenu.add(item); //put View Inventory button in File menu
        
        fileMenu.addSeparator(); //add a horizontal separator line
        
        item = new JMenuItem("Reconstruct Log"); //Reconstruct Log button
        item.addActionListener(fmh); //assign listener to Reconstruct Log button
        fileMenu.add(item); //put Reconstruct Log button in File menu
        
        fileMenu.addSeparator(); //add a horizontal separator line
        
        item = new JMenuItem("Quit"); //Quit button
        item.addActionListener(fmh); //assign listener to Quit button
        fileMenu.add(item); //put Quit button in File menu
        
        JMenu searchMenu = new JMenu("Search"); //Search Menu button
        SearchMenuHandler smh  = new SearchMenuHandler(this); //handler for Search menu actions
        
        item = new JMenuItem("Online Search"); //online search button when Search is clicked
        item.addActionListener(smh);  //assign listener to online search button
        searchMenu.add(item); //put online search button in View menu
        
        searchMenu.addSeparator(); //add a horizontal separator line
        
        item = new JMenuItem("Offline Search"); //images button when Search is clicked
        item.addActionListener(smh);  //assign listener to offline search button
        searchMenu.add(item); //put offline search button in Search menu
        
        JMenu editMenu = new JMenu("Edit"); //Edit Menu button
        EditMenuHandler emh  = new EditMenuHandler(this); //handler for Edit menu actions
        
        item = new JMenuItem("Insert"); //insert button when Edit is clicked
        item.addActionListener(emh); //assign listener to insert button
        editMenu.add(item); //put insert button in Edit menu
        
        editMenu.addSeparator(); //add a horizontal separator line
        
        item = new JMenuItem("Modify"); //Modify button when Edit is clicked
        item.addActionListener(emh); //assign listener to modify button
        editMenu.add(item); //put modify button in Edit menu
        
        editMenu.addSeparator(); //add a horizontal separator line
        
        item = new JMenuItem("Delete"); //Delete button when Edit is clicked
        item.addActionListener(emh); //assign listener to delete button
        editMenu.add(item); //put delete button in Edit menu

        setJMenuBar(menuBar); //set menuBar to be this GUI's menu bar
        menuBar.add(fileMenu); //add the File menu to the menu bar
        menuBar.add(searchMenu); //add the View menu to the menu bar
        menuBar.add(editMenu); //add the Edit menu to the menu bar
    } 
}
