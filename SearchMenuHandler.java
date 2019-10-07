import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

//This class will handle the action that it should take when an event takes place in MainGUI (in this case, the user clicking a View JMenu item)
public class SearchMenuHandler implements ActionListener {
   MainGUI mGUI;
  
   //Constructor used to pass the MainGUI object in to allow access to it's methods.
   public SearchMenuHandler (MainGUI m) {
      mGUI = m;
   }
   
   //This method will handle the action of each event, "search" and "images"
   public void actionPerformed(ActionEvent event) {
      String menuName = event.getActionCommand(); //get String associated with action 
      //If search is clicked, a search GUI will be displayed 
      //inspired by https://stackoverflow.com/questions/6555040/multiple-input-in-joptionpane-showinputdialog
      if (menuName.equals("Online Search")){
          //JTextFields which will contain the input parameters
          JTextField itemField = new JTextField(10);
          JTextField minPriceField = new JTextField(10);
          JTextField maxPriceField = new JTextField(10);
          
          //The JPanel creates a GUI for the text field with JLabels to denote the text not in a text box
          JPanel myPanel = new JPanel();
          myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.PAGE_AXIS)); // enables input lines to be displayed line after line
          
          // adds a label on GUI asking the user which columns the user would like to choose
          myPanel.add(new JLabel("Please enter the columns you would like to search:"));
          myPanel.add(new JLabel(" "));
          
          // adds a label on GUI for the search bar by item number
          myPanel.add(new JLabel("Search by Item Number or Keyword(s):"));
          myPanel.add(itemField);
          
          // adds a label on GUI for the category menu
          myPanel.add(new JLabel("Category:"));
          
          //gets the categories from a text file stored in a working directory and stores all of the category names and numbers in arrays
          File categoryFile = new File("/Users/JoshGoldstein/Desktop/Phase3Final/categories.txt"); // replace with your path
          String[] category = null;
          String[] categoryNumbers = null;
          try {
            category = new String[30]; // size of array = number of lines in file + 1
            categoryNumbers = new String[30];
            Scanner sc= new Scanner(categoryFile);
            categoryNumbers[0] = "";
            category[0] = "";
            int idx = 1; //index for category array
            while (sc.hasNextLine()){
                String categoryLine = sc.nextLine();
                String[] categoryArr = categoryLine.split(":");
                categoryNumbers[idx] = categoryArr[0];
                category[idx++] = categoryArr[1];
            }
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          }
          // add category names to category drop down menu
          JComboBox<String> categoriesComboBox=new JComboBox<String>(category); // provides a drop down menu for the GUI
          myPanel.add(categoriesComboBox, BorderLayout.AFTER_LAST_LINE); 
          
          // create labels with text boxes to enter min price and max price
          myPanel.add(new JLabel("Min Price:"));
          myPanel.add(minPriceField);
          myPanel.add(new JLabel("Max Price:"));
          myPanel.add(maxPriceField);
          
          //create a drop down menu for auction status
          myPanel.add(new JLabel("Auction Status:"));
          final String[] status = {"Open", "Closed"};
          JComboBox<String> statusComboBox=new JComboBox<String>(status); // provides a drop down menu for the GUI
          myPanel.add(statusComboBox, BorderLayout.AFTER_LAST_LINE);
          myPanel.add(new JLabel("Seller:"));
          
          //gets the seller from a text file stored in a working directory and stores all of the seller names and numbers in arrays
          File sellerFile = new File("/Users/JoshGoldstein/Desktop/Phase3Final/sellers.txt"); // replace with your path
          String[] sellerLocation = null;
          String[] sellerNumbers = null;
          try {
            sellerLocation = new String[127]; // size of array = number of lines in file + 1
            sellerNumbers = new String[127];
            Scanner sc= new Scanner(sellerFile);
            sellerNumbers[0] = "";
            sellerLocation[0] = "";
            int idx = 1; //index for seller array
            while (sc.hasNextLine()){
                String sellerLine = sc.nextLine();
                String[] sellerArr = sellerLine.split(" ");
                sellerNumbers[idx] = sellerArr[0];
                sellerLocation[idx++] = sellerArr[1];
            }
          } catch (FileNotFoundException e) {
                e.printStackTrace();
          }

          //add seller names to seller drop down menu
          JComboBox<String> sellerComboBox=new JComboBox<String>(sellerLocation); // provides a drop down menu for the GUI
          myPanel.add(sellerComboBox, BorderLayout.AFTER_LAST_LINE);
          
          //add drop down menu for order by
          myPanel.add(new JLabel("Order By:"));
          final String[] orderOption1 = {"Ending Date", "Title", "Number of Bids", "Bid Price"};
          final String[] orderOption2 = {"Ascending", "Descending"};
          JComboBox<String> orderComboBox1=new JComboBox<String>(orderOption1); // provides a drop down menu for the GUI
          myPanel.add(orderComboBox1, BorderLayout.AFTER_LAST_LINE);
          JComboBox<String> orderComboBox2=new JComboBox<String>(orderOption2); // provides a drop down menu for the GUI
          myPanel.add(orderComboBox2, BorderLayout.AFTER_LAST_LINE);
          
          // creates a checklist for filter by attributes
          myPanel.add(new JLabel("Filter By Attribute:"));
          JCheckBox buyNowBox = new JCheckBox("Search for Buy Now items only?");
          myPanel.add(buyNowBox);
          JCheckBox pickupOnlyBox = new JCheckBox("Search for Pickup Only items only?");
          myPanel.add(pickupOnlyBox);
          JCheckBox noPickupBox = new JCheckBox("Do not include Pickup Only Items?");
          myPanel.add(noPickupBox);
          JCheckBox shippingBox = new JCheckBox("Search for 1 cent shipping only?");
          myPanel.add(shippingBox);
          
          //creates a checklist for international shipping
          myPanel.add(new JLabel("International Shipping:"));
          JCheckBox canadaBox = new JCheckBox("Canada");
          myPanel.add(canadaBox);
          JCheckBox outsideUSandCanadaBox = new JCheckBox("Outside US & Canada");
          myPanel.add(outsideUSandCanadaBox);
          
          int result = JOptionPane.showConfirmDialog(null, myPanel,
              "Search", JOptionPane.OK_CANCEL_OPTION);
          if (result == JOptionPane.OK_OPTION) { //If OK is selected on the JOptionPane, then process the input parameters given
              String itemChosen = itemField.getText();
              // if the item chosen is an item number, then insert that item number into the GUI
              if (isItemNumber(itemChosen)){
                  Map<Integer,TreeMap<String,String>> map = HashMapQuery.getMap();
                  try {
                    HashMapQuery.addItemToGUI(Integer.parseInt(itemChosen), mGUI, map);
                  } catch (NumberFormatException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                  } catch (MalformedURLException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                  } catch (IOException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                  }
              }
              else{
                  String sellerNo = getSellerNumber((String) sellerComboBox.getSelectedItem(),sellerNumbers,sellerLocation); // get seller number base on the seller location chosen
                  
                  String categoryNo = getCategoryNumber((String) categoriesComboBox.getSelectedItem(),categoryNumbers,category); // get category number based on the category name chosen
                  
                  // determine what values of the search are passed on to the URL in HashMapQuery
                  String minPriceChosen = minPriceField.getText();
                  if (minPriceChosen.equals("")) minPriceChosen = "0";
                  String maxPriceChosen = maxPriceField.getText();
                  if (maxPriceChosen.equals("")) maxPriceChosen = "999999";
                  String statusStr = (String) statusComboBox.getSelectedItem();
                  if (statusStr.equals("Open")) statusStr = "false";
                  else statusStr = "true";
                  String orderBy1 = (String) orderComboBox1.getSelectedItem();
                  if (orderBy1.equals("Title")) orderBy1 = "2";
                  else if (orderBy1.equals("Number of Bids")) orderBy1 = "3";
                  else if (orderBy1.equals("Bid Price")) orderBy1 = "4";
                  else orderBy1 = "1";
                  String orderBy2 = (String) orderComboBox2.getSelectedItem();
                  if (orderBy2.equals("Descending")) orderBy2 = "true";
                  else orderBy2 = "false";
                  String buyNow = "false";
                  if (buyNowBox.isSelected()) buyNow = "true";
                  String pickupOnly = "false";
                  if (pickupOnlyBox.isSelected()) pickupOnly = "true";
                  String noPickup = "false";
                  if (noPickupBox.isSelected()) noPickup = "true";
                  String cShipping = "false";
                  if (shippingBox.isSelected()) cShipping = "true";
                  String canada = "false";
                  if (canadaBox.isSelected()) canada = "true";
                  String outsideUSandCanada = "false";
                  if (outsideUSandCanadaBox.isSelected()) outsideUSandCanada = "true";
                  
                  try {
                      HashMapQuery.getOnlineSearch(itemChosen,minPriceChosen,maxPriceChosen,statusStr,orderBy1,orderBy2,buyNow,pickupOnly,noPickup,cShipping,canada,outsideUSandCanada,mGUI,categoryNo,sellerNo);
                  } catch (MalformedURLException e) {
                      e.printStackTrace();
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
              } 
             
          }
      }
      
      //If images is clicked, display images for each product in inventory
      else if (menuName.equals("Offline Search")){
          //JTextFields which will contain the input parameters
          JTextField itemField = new JTextField(10);
          JTextField nameField = new JTextField(10);
          JTextField priceField = new JTextField(10);
          JTextField aucEndField = new JTextField(10);
          JTextField sellerField = new JTextField(10);
          JTextField numBidField = new JTextField(10);
          JTextField timeField = new JTextField(10);
          
          //The JPanel creates a GUI for the text field with JLabels to denote the text not in a text box
          JPanel myPanel = new JPanel();
          myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.PAGE_AXIS)); // enables input lines to be displayed line after line
          
          myPanel.add(new JLabel("Please enter the columns you would like to search:"));
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
          myPanel.add(new JLabel("Time of Query:"));
          myPanel.add(timeField);

          int result = JOptionPane.showConfirmDialog(null, myPanel,
              "Search", JOptionPane.OK_CANCEL_OPTION);
          if (result == JOptionPane.OK_OPTION) { //If OK is selected on the JOptionPane, then process the input parameters given
              String itemNumber = itemField.getText();
              String name = nameField.getText(); 
              String price = priceField.getText(); 
              String aucEnd = aucEndField.getText(); 
              String seller = sellerField.getText(); 
              String numBid = numBidField.getText(); 
              String time = timeField.getText(); 
              ArrayList<String> searchArr = new ArrayList<>(); // array that contains all the columns that need to be modified

              searchArr.add(name);
              searchArr.add(price);
              searchArr.add(aucEnd);
              searchArr.add(seller);
              searchArr.add(numBid);
              searchArr.add(time);
              
              Map<Integer,TreeMap<String,String>> map = HashMapQuery.getMap();
              HashMapQuery.searchItemsFromGUI(itemNumber,mGUI,map,searchArr);
          }
       }
    }
   
    // method that checks validity of item numbers by a regex
    private boolean isItemNumber(String itemChosen) {
        return itemChosen.matches("\\d{8}");
    }

    //method that gets the category number associated with a category name
    private String getCategoryNumber(String categoryChosen, String[] categoryNumbers, String[] category) {
        String result = "";
        for(int i = 0 ; i < category.length; i++){
            if (category[i].equals(categoryChosen)) result = categoryNumbers[i];
        }
        return result;
    }

    // check which seller the sellerChosen string matches and return the corresponding seller number
    public String getSellerNumber(String sellerChosen, String[] sellerNumbers, String[] sellerLocation) {
        String result = "";
        for(int i = 0 ; i < sellerLocation.length; i++){
            if (sellerLocation[i].equals(sellerChosen)) result = sellerNumbers[i];
        }
        return result;
    }
}
