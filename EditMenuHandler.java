import java.awt.event.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

//This class will handle the action that it should take when an event takes place in MainGUI (in this case, the user clicking an Edit JMenu item) 
public class EditMenuHandler implements ActionListener {
   MainGUI mGUI;
   //Constructor used to pass the MainGUI object in to allow access to it's methods.
   public EditMenuHandler (MainGUI m) {
       mGUI = m;
   }
   
   //This method will handle the action of the events, insert, modify, and delete
   public void actionPerformed(ActionEvent event) {
      String menuName = event.getActionCommand(); 
      //if insert is chosen
      if (menuName.equals("Insert")){
          String itemNumberStr = JOptionPane.showInputDialog("Enter the item number you would like to insert:");
          try{
              Integer itemNumber;
              if(itemNumberStr != null) {
                  itemNumber = Integer.parseInt(itemNumberStr);
                  try {
                      //HashMapQuery hmq = new HashMapQuery();
                      Map<Integer,TreeMap<String,String>> map = HashMapQuery.getMap();
                      HashMapQuery.addItemToGUI(itemNumber,mGUI,map);
                } catch (IOException e) {
                      e.printStackTrace();
                }
              }
          } catch (NumberFormatException nfe){
              JOptionPane.showMessageDialog(null, "Invalid item number.");
          }
      }
      //inspired by https://stackoverflow.com/questions/6555040/multiple-input-in-joptionpane-showinputdialog
      // a JPanel GUI with JTextFields GUI will be displayed so the user can select which columns are to be modified in the MainGUI.
      else if (menuName.equals("Modify")){
          //JTextFields which will contain the input parameters
          JTextField itemField = new JTextField(10);
          JTextField nameField = new JTextField(10);
          JTextField priceField = new JTextField(10);
          JTextField aucEndField = new JTextField(10);
          JTextField sellerField = new JTextField(10);
          JTextField numBidField = new JTextField(10);
          
          //The JPanel creates a GUI for the text field with JLabels to denote the text not in a text box
          JPanel myPanel = new JPanel();
          myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.PAGE_AXIS)); // enables input lines to be displayed line after line
          
          myPanel.add(new JLabel("Please enter the columns you would like to modify: (Item number must be specified)"));
          myPanel.add(new JLabel(" "));
          myPanel.add(new JLabel("Item Number:"));
          myPanel.add(itemField);
          myPanel.add(new JLabel("Name:"));
          myPanel.add(nameField);
          myPanel.add(new JLabel("Price:"));
          myPanel.add(priceField);
          myPanel.add(new JLabel("Auction End Date:"));
          myPanel.add(aucEndField);
          myPanel.add(new JLabel("Seller:"));
          myPanel.add(sellerField);
          myPanel.add(new JLabel("Number of Bids:"));
          myPanel.add(numBidField);

          int result = JOptionPane.showConfirmDialog(null, myPanel,
              "Modify", JOptionPane.OK_CANCEL_OPTION);
          if (result == JOptionPane.OK_OPTION) { //If OK is selected on the JOptionPane, then process the input parameters given
              Integer itemNumber = Integer.parseInt(itemField.getText());
              String name = nameField.getText(); 
              String price = priceField.getText(); 
              String aucEnd = aucEndField.getText(); 
              String seller = sellerField.getText(); 
              String numBid = numBidField.getText(); 
              Map<String,String> modifyMap = new HashMap<String,String>(); // map that contains all the columns that need to be modified
              if (!name.equals("")) modifyMap.put("1", name);
              if (!price.equals("")) modifyMap.put("2", price);
              if (!aucEnd.equals("")) modifyMap.put("3", aucEnd);
              if (!seller.equals("")) modifyMap.put("4", seller);
              if (!numBid.equals("")) modifyMap.put("5", numBid);
              Map<Integer,TreeMap<String,String>> map = HashMapQuery.getMap();
              HashMapQuery.modifyItemsFromGUI(itemNumber,mGUI,map,modifyMap);
          }
      }
      // if delete is selected, a JOptionPane will pop up asking the user for the item number they would like to delete
      else if (menuName.equals("Delete")){
          String itemNumberStr = JOptionPane.showInputDialog("Enter the item number you would like to delete:");
          try{
              Integer itemNumber;
              if(itemNumberStr != null) {
                  itemNumber = Integer.parseInt(itemNumberStr);
                  Map<Integer,TreeMap<String,String>> map = HashMapQuery.getMap();
                  HashMapQuery.removeItemFromGUI(itemNumber,mGUI,map);
              }
          } catch (NumberFormatException nfe){
              JOptionPane.showMessageDialog(null, "Invalid item number.");
          }
      }
   } 
}