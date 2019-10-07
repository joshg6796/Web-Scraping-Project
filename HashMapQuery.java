import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

// this class contains all of the GUI operations that require the use of querying the HashMap and GUI operations for online searching
public class HashMapQuery {
    
    static Map<Integer, TreeMap<String,String>> map = new HashMap<>();
    
    static //date format for filename
    DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH_mm_ss");
    static Date date = new Date();
    
    //file name is a concatenation of log, current date and time of requested log, and .txt
    static String logFileName = "log"+dateFormat.format(date)+".txt";
    
    static PrintStream log;
    static String output;
    
    //static initialization block. Inspired by https://stackoverflow.com/questions/1028661/unhandled-exceptions-in-field-initializations
    static {
        try {
          log = new PrintStream(new FileOutputStream(logFileName)); 
        } catch (Exception e) {
          e.printStackTrace();
        } 
      } 
    
    // constructor used to instantiate class
    public HashMapQuery(){
        
    }
    
    // this method is called when reproducing a log file
    public void addInputToGUI(String inputFile, MainGUI mGUI) throws MalformedURLException, IOException {
        addInputToGUI(inputFile,mGUI,output);
    }
    
    //This method will take all of the item numbers in the input file and obtain item information from the url's html code.
    //Some of this information includes the title, number of bids, and the price of the item.
    public void addInputToGUI(String inputFile, MainGUI mGUI, String outputFile) throws MalformedURLException, IOException {
        map.clear(); // clear map in the event of reconstructing a log file
        
        Scanner sc = null;
        
        try {
            sc = new Scanner(new File(inputFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        output = outputFile;
        
        //inspired by https://www.mkyong.com/java/java-how-to-get-current-date-time-date-and-calender/
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String dateStr = dateFormat.format(date);
        
        
        //loop through each item number
        while (sc.hasNext()) {
            Integer itemNumber = Integer.parseInt(sc.nextLine());
            
            //this is the url that will be used to get the html code
            String url = "https://www.shopgoodwill.com/Item/"+itemNumber;
            
            //inspired by https://stackoverflow.com/questions/8616781/how-to-get-a-web-pages-source-code-from-java
            URL urlObject = new URL(url);
            URLConnection urlConnection = urlObject.openConnection();
            InputStream is = urlConnection.getInputStream();
            Scanner sc2 = new Scanner(is);
            
            // variables to be placed into the hashmap
            String itemTitle = null;
            String numberOfBids = null;
            String currPrice = null;
            String auctionEndDate = null;
            String sellerName = null;
            String sellerLocation = null;
            String seller = null;
            ArrayList<String> imgArr = new ArrayList<>();
            
            // boolean methods to indicate whether certain lines of html have been read since these line occur more than one in the html code
            boolean sellerNameRead = false;
            boolean sellerLocationRead = false;
            
            //loop through each item attribute
            while (sc2.hasNext()) {
                String html_line = sc2.nextLine();
                //search for title location in html line if the html line contains the title.
                if (html_line.contains("<div class='col-xs-12 col-sm-6'>")){ // the title is in the html line that starts with this tag
                    itemTitle = html_line.substring(82); // removes the beginning of the html tag
                    itemTitle = itemTitle.replace(itemTitle.substring(itemTitle.indexOf("<")),""); // replaces the ending html tag with the empty string which will remove the end html tag
                }
                //search for number of bids location in html line if the html line contains the number of bids.
                else if (html_line.contains("<b>Number of Bids:</b><span class=\"num-bids\">")){
                    numberOfBids = html_line.substring(65); // removes the beginning of the html tag
                    numberOfBids = numberOfBids.replace(numberOfBids.substring(numberOfBids.indexOf("<")),""); // replaces the ending html tag with the empty string which will remove the end html tag
                }
                //search for current price location in html line if the html line contains the current price.
                else if (html_line.contains("<li><b>Current Price:</b><span class=\"current-price\">")){
                    currPrice = html_line.substring(69); // removes the beginning of the html tag
                    currPrice = currPrice.replace(currPrice.substring(currPrice.indexOf("<")),""); // replaces the ending html tag with the empty string which will remove the end html tag
                }
                //search for auction end date location in html line if the html line contains the auction end date.
                else if (html_line.contains("<li><b>Ends On: </b>")){
                    auctionEndDate = html_line.substring(36); // removes the beginning of the html tag
                    auctionEndDate = auctionEndDate.replace(auctionEndDate.substring(auctionEndDate.indexOf("<")),""); // replaces the ending html tag with the empty string which will remove the end html tag
                }
                //search for the seller name which is the line after "<h4><b>Seller:</b></h4>"
                else if (html_line.contains("<h4><b>Seller:</b></h4>") && !sellerNameRead){
                    html_line = sc2.nextLine();
                    sellerNameRead = true;
                    sellerName = html_line.substring(3); // removes the beginning of the html tag
                    sellerName = sellerName.replace(sellerName.substring(sellerName.indexOf("<")),""); // replaces the ending html tag with the empty string which will remove the end html tag
                }
                //search for the seller location which is the line after "<h4><b>Location:</b></h4>"
                else if (html_line.contains("<h4><b>Location:</b></h4>") && !sellerLocationRead){
                    html_line = sc2.nextLine();
                    sellerLocationRead = true;
                    sellerLocation = html_line.substring(3); // removes the beginning of the html tag
                    sellerLocation = sellerLocation.replace(sellerLocation.substring(sellerLocation.indexOf("<")),""); // replaces the ending html tag with the empty string which will remove the end html tag
                    sellerLocation = sellerLocation.substring(sellerLocation.indexOf(", "));
                    sellerLocation = sellerLocation.replace(", ", "");
                }
                //search for the images which is the line after "<div class="carousel-inner" data-chocolat-title="Product Images">"
                else if (html_line.contains("<div class=\"carousel-inner\" data-chocolat-title=\"Product Images\">")){
                    while (!html_line.contains("<div class=\"magnify\">")){
                        if (html_line.contains("<a href=\"https://sgws3productimages.azureedge.net/sgwproductimages/images")){
                            String temp = html_line.substring(49); // removes the beginning of the html tag
                            temp = temp.replace(temp.substring(temp.indexOf("\"")),""); // replaces the ending " with the empty string which will remove the end " in html code
                            imgArr.add(temp);
                        }
                        html_line = sc2.nextLine();
                    }
                }
            }
            //create the seller string which is a concatenation of state and name
            if (sellerNameRead && sellerLocationRead){
                seller = sellerLocation+ "-" + sellerName;
            }
            
            //Convert imgArr to string
            //inspired by https://stackoverflow.com/questions/599161/best-way-to-convert-an-arraylist-to-a-string
            StringBuilder sb = new StringBuilder();
            for (String img : imgArr){
                sb.append(img+";"); // the ; will be a delimiter used for retrieving each image in the string
            }

            String imgStr = sb.toString();
            
            //place data retrieved previously and store data into hash map
            map.put(itemNumber, new TreeMap<String, String>());
            map.get(itemNumber).put("1", itemTitle);
            map.get(itemNumber).put("2", currPrice);
            map.get(itemNumber).put("3", auctionEndDate);
            map.get(itemNumber).put("4", seller);
            map.get(itemNumber).put("5", numberOfBids);
            map.get(itemNumber).put("6", imgStr);
            map.get(itemNumber).put("7", dateStr);
        }
        mGUI.clear(); // clear if in the event we are reconstructing a log file
        mGUI.appendToGUI("Welcome to the shopgoodwill.com inventory organizer! This is what you output file looks like right now.\n\n");
        mGUI.appendToGUI("Item Number|Name|Price|Auction End Date|Seller|Number of Bids|Image(s)|Time of Query\n");
        
        //prints map to the GUI
        //inspired by https://stackoverflow.com/questions/26188532/iterate-through-nested-hashmap    
        for (Map.Entry<Integer, TreeMap<String, String>> outerEntry : map.entrySet()) {
            Integer itemNo = outerEntry.getKey();
            mGUI.appendToGUI(String.valueOf(itemNo));
            for (Map.Entry<String, String> innerEntry :outerEntry.getValue().entrySet()) {
                String value = innerEntry.getValue();
                mGUI.appendToGUI("|"+value);
            }
            mGUI.appendToGUI("\n");
        } 
        
    }
    

    // same functionality as addInputToGUI except the parameter used is an item number instead of an input file of numbers.
    public static void addItemToGUI(Integer itemNumber, MainGUI mGUI, Map<Integer, TreeMap<String, String>> map2) throws MalformedURLException, IOException {
        //this is the url that will be used to get the html code
        String url = "https://www.shopgoodwill.com/Item/"+itemNumber;
        
        //inspired by https://stackoverflow.com/questions/8616781/how-to-get-a-web-pages-source-code-from-java
        URL urlObject = new URL(url);
        URLConnection urlConnection = urlObject.openConnection();
        InputStream is = urlConnection.getInputStream();
        Scanner sc = new Scanner(is);
        
        // variables to be placed into the hashmap
        String itemTitle = null;
        String numberOfBids = null;
        String currPrice = null;
        String auctionEndDate = null;
        String sellerName = null;
        String sellerLocation = null;
        String seller = null;
        ArrayList<String> imgArr = new ArrayList<>();
        
        //Get timestamp of query
        //inspired by https://www.mkyong.com/java/java-how-to-get-current-date-time-date-and-calender/
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String dateStr = dateFormat.format(date);
        
        // boolean methods to indicate whether certain lines of html have been read since these line occur more than one in the html code
        boolean sellerNameRead = false;
        boolean sellerLocationRead = false;
        
        //loop through each item attribute
        while (sc.hasNext()) {
            String html_line = sc.nextLine();
            //search for title location in html line if the html line contains the title.
            if (html_line.contains("<div class='col-xs-12 col-sm-6'>")){ // the title is in the html line that starts with this tag
                itemTitle = html_line.substring(82); // removes the beginning of the html tag
                itemTitle = itemTitle.replace(itemTitle.substring(itemTitle.indexOf("<")),""); // replaces the ending html tag with the empty string which will remove the end html tag
            }
            //search for number of bids location in html line if the html line contains the number of bids.
            else if (html_line.contains("<b>Number of Bids:</b><span class=\"num-bids\">")){
                numberOfBids = html_line.substring(65); // removes the beginning of the html tag
                numberOfBids = numberOfBids.replace(numberOfBids.substring(numberOfBids.indexOf("<")),""); // replaces the ending html tag with the empty string which will remove the end html tag
            }
            //search for current price location in html line if the html line contains the current price.
            else if (html_line.contains("<li><b>Current Price:</b><span class=\"current-price\">")){
                currPrice = html_line.substring(69); // removes the beginning of the html tag
                currPrice = currPrice.replace(currPrice.substring(currPrice.indexOf("<")),""); // replaces the ending html tag with the empty string which will remove the end html tag
            }
            //search for auction end date location in html line if the html line contains the auction end date.
            else if (html_line.contains("<li><b>Ends On: </b>")){
                auctionEndDate = html_line.substring(36); // removes the beginning of the html tag
                auctionEndDate = auctionEndDate.replace(auctionEndDate.substring(auctionEndDate.indexOf("<")),""); // replaces the ending html tag with the empty string which will remove the end html tag
            }
            //search for the seller name which is the line after "<h4><b>Seller:</b></h4>"
            else if (html_line.contains("<h4><b>Seller:</b></h4>") && !sellerNameRead){
                html_line = sc.nextLine();
                sellerNameRead = true;
                sellerName = html_line.substring(3); // removes the beginning of the html tag
                sellerName = sellerName.replace(sellerName.substring(sellerName.indexOf("<")),""); // replaces the ending html tag with the empty string which will remove the end html tag
            }
            //search for the seller location which is the line after "<h4><b>Location:</b></h4>"
            else if (html_line.contains("<h4><b>Location:</b></h4>") && !sellerLocationRead){
                html_line = sc.nextLine();
                sellerLocationRead = true;
                sellerLocation = html_line.substring(3); // removes the beginning of the html tag
                sellerLocation = sellerLocation.replace(sellerLocation.substring(sellerLocation.indexOf("<")),""); // replaces the ending html tag with the empty string which will remove the end html tag
                sellerLocation = sellerLocation.substring(sellerLocation.indexOf(", "));
                sellerLocation = sellerLocation.replace(", ", "");
            }
            //search for the images which is the line after "<div class="carousel-inner" data-chocolat-title="Product Images">"
            else if (html_line.contains("<div class=\"carousel-inner\" data-chocolat-title=\"Product Images\">")){
                while (!html_line.contains("<div class=\"magnify\">")){
                    if (html_line.contains("<a href=\"https://sgws3productimages.azureedge.net/sgwproductimages/images")){
                        String temp = html_line.substring(49); // removes the beginning of the html tag
                        temp = temp.replace(temp.substring(temp.indexOf("\"")),""); // replaces the ending " with the empty string which will remove the end " in html code
                        imgArr.add(temp);
                    }
                    html_line = sc.nextLine();
                }
            }
            
        }
        //create the seller string which is a concatenation of state and name
        if (sellerNameRead && sellerLocationRead){
            seller = sellerLocation+ "-" + sellerName;
        }
        
        //Convert imgArr to string
        //inspired by https://stackoverflow.com/questions/599161/best-way-to-convert-an-arraylist-to-a-string
        StringBuilder sb = new StringBuilder();
        for (String img : imgArr){
            sb.append(img+";"); // the ; will be a delimiter used for retrieving each image in teh string
        }

        String imgStr = sb.toString();
        
        //place data retrieved previously and store data into hash map
        map.put(itemNumber, new TreeMap<String, String>());
        map.get(itemNumber).put("1", itemTitle);
        map.get(itemNumber).put("2", currPrice);
        map.get(itemNumber).put("3", auctionEndDate);
        map.get(itemNumber).put("4", seller);
        map.get(itemNumber).put("5", numberOfBids);
        map.get(itemNumber).put("6", imgStr);
        map.get(itemNumber).put("7", dateStr);
        
        // log the insert transaction performed
        System.setOut(log);
        System.out.println("INSERT");
        System.out.println(itemNumber+"|"+itemTitle+"|"+currPrice+"|"+auctionEndDate+"|"+seller+"|"+numberOfBids+"|"+imgStr+"|"+dateStr);
        
        mGUI.clear();
        mGUI.appendToGUI("Welcome to the shopgoodwill.com inventory organizer! This is what you output file looks like right now.\n\n");
        mGUI.appendToGUI("Item Number|Name|Price|Auction End Date|Seller|Number of Bids|Image(s)|Time of Query\n");
        
        //prints map to the GUI
        //inspired by https://stackoverflow.com/questions/26188532/iterate-through-nested-hashmap    
        for (Map.Entry<Integer, TreeMap<String, String>> outerEntry : map.entrySet()) {
            Integer itemNo = outerEntry.getKey();
            mGUI.appendToGUI(String.valueOf(itemNo));
            for (Map.Entry<String, String> innerEntry :outerEntry.getValue().entrySet()) {
                String value = innerEntry.getValue();
                mGUI.appendToGUI("|"+value);
            }
            mGUI.appendToGUI("\n");
        } 
    }
    
    // getter method for returning the current inventory of the map
    public static Map<Integer,TreeMap<String,String>> getMap(){
        return map;
    }

    // removes an item in its entirety from the GUI. This method is called when the user wants to delete an item
    public static void removeItemFromGUI(Integer itemNumber, MainGUI mGUI, Map<Integer, TreeMap<String, String>> map2) {
        //get other info about the item that is being deleted for logging
        String itemTitle = map.get(itemNumber).get("1"); 
        String currPrice = map.get(itemNumber).get("2"); 
        String auctionEndDate = map.get(itemNumber).get("3"); 
        String seller = map.get(itemNumber).get("4"); 
        String numberOfBids = map.get(itemNumber).get("5"); 
        String imgStr = map.get(itemNumber).get("6"); 
        String dateStr = map.get(itemNumber).get("7"); 
        
        //if the item number exists, delete it's entry
        if (map.containsKey(itemNumber))
            map.remove(itemNumber); 
        
        //log the deleted entry
        System.setOut(log);
        System.out.println("DELETE");
        System.out.println(itemNumber+"|"+itemTitle+"|"+currPrice+"|"+auctionEndDate+"|"+seller+"|"+numberOfBids+"|"+imgStr+"|"+dateStr);
        
        mGUI.clear();
        mGUI.appendToGUI("Welcome to the shopgoodwill.com inventory organizer! This is what you output file looks like right now.\n\n");
        mGUI.appendToGUI("Item Number|Name|Price|Auction End Date|Seller|Number of Bids|Image(s)|Time of Query\n");
        
        //prints map to the GUI
        //inspired by https://stackoverflow.com/questions/26188532/iterate-through-nested-hashmap    
        for (Map.Entry<Integer, TreeMap<String, String>> outerEntry : map.entrySet()) {
            Integer itemNo = outerEntry.getKey();
            mGUI.appendToGUI(String.valueOf(itemNo));
            for (Map.Entry<String, String> innerEntry :outerEntry.getValue().entrySet()) {
                String value = innerEntry.getValue();
                mGUI.appendToGUI("|"+value);
            }
            mGUI.appendToGUI("\n");
        }  
    }

    // method for modifying a specific item number in the HashMap
    public static void modifyItemsFromGUI(Integer itemNumber, MainGUI mGUI, Map<Integer, TreeMap<String, String>> map2,
            Map<String, String> modifyMap) {
        // only perform modification if the item number is in the HashMap. otherwise a JOptionPane will pop up with an error message
        if (map.containsKey(itemNumber)){
            
            for (Map.Entry<String, String> entry : modifyMap.entrySet()) {
                map.get(itemNumber).put(entry.getKey(), entry.getValue()); 
            }
           
            String name = null;
            if (modifyMap.get("name")!= null){
                name = modifyMap.get("name");
            }
            else name = map.get(itemNumber).get("1");
            
            //get other info about the item that was modified for logging
            String itemTitle = map.get(itemNumber).get("1"); 
            String currPrice = map.get(itemNumber).get("2"); 
            String auctionEndDate = map.get(itemNumber).get("3"); 
            String seller = map.get(itemNumber).get("4"); 
            String numberOfBids = map.get(itemNumber).get("5"); 
            String imgStr = map.get(itemNumber).get("6"); 
            String dateStr = map.get(itemNumber).get("7"); 

            //log the modified entry
            System.setOut(log);
            System.out.println("MODIFY");
            System.out.println(itemNumber+"|"+itemTitle+"|"+currPrice+"|"+auctionEndDate+"|"+seller+"|"+numberOfBids+"|"+imgStr+"|"+dateStr);
            
            
            mGUI.clear();
            mGUI.appendToGUI("Welcome to the shopgoodwill.com inventory organizer! This is what you output file looks like right now.\n\n");
            mGUI.appendToGUI("Item Number|Name|Price|Auction End Date|Seller|Number of Bids|Image(s)|Time of Query\n");
            
            //prints map to the GUI
            //inspired by https://stackoverflow.com/questions/26188532/iterate-through-nested-hashmap    
            for (Map.Entry<Integer, TreeMap<String, String>> outerEntry : map.entrySet()) {
                Integer itemNo = outerEntry.getKey();
                mGUI.appendToGUI(String.valueOf(itemNo));
                for (Map.Entry<String, String> innerEntry :outerEntry.getValue().entrySet()) {
                    String value = innerEntry.getValue();
                    mGUI.appendToGUI("|"+value);
                }
                mGUI.appendToGUI("\n");
            } 
            
        }
        else JOptionPane.showMessageDialog(null, "Item number entered does not exist."); 
        
    }

    // adds input to GUI via an input file chosen by the user using a JFileChooser
    public void addInputToGUIFromGUI(String inputFile, MainGUI mGUI, Map<Integer, TreeMap<String, String>> map) throws MalformedURLException, IOException{
        Scanner sc = null;
        
        try {
            sc = new Scanner(new File(inputFile));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //inspired by https://www.mkyong.com/java/java-how-to-get-current-date-time-date-and-calender/
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String dateStr = dateFormat.format(date);
        
    
        //loop through each item number
        while (sc.hasNext()) {
            Integer itemNumber = Integer.parseInt(sc.nextLine());
            
            if (!map.containsKey(itemNumber)){
                //this is the url that will be used to get the html code
                String url = "https://www.shopgoodwill.com/Item/"+itemNumber;
                
                //inspired by https://stackoverflow.com/questions/8616781/how-to-get-a-web-pages-source-code-from-java
                URL urlObject = new URL(url);
                URLConnection urlConnection = urlObject.openConnection();
                InputStream is = urlConnection.getInputStream();
                Scanner sc2 = new Scanner(is);
                
                // variables to be placed into the hashmap
                String itemTitle = null;
                String numberOfBids = null;
                String currPrice = null;
                String auctionEndDate = null;
                String sellerName = null;
                String sellerLocation = null;
                String seller = null;
                ArrayList<String> imgArr = new ArrayList<>();
                
                // boolean methods to indicate whether certain lines of html have been read since these line occur more than one in the html code
                boolean sellerNameRead = false;
                boolean sellerLocationRead = false;
                
                //loop through each item attribute
                while (sc2.hasNext()) {
                    String html_line = sc2.nextLine();
                    //search for title location in html line if the html line contains the title.
                    if (html_line.contains("<div class='col-xs-12 col-sm-6'>")){ // the title is in the html line that starts with this tag
                        itemTitle = html_line.substring(82); // removes the beginning of the html tag
                        itemTitle = itemTitle.replace(itemTitle.substring(itemTitle.indexOf("<")),""); // replaces the ending html tag with the empty string which will remove the end html tag
                    }
                    //search for number of bids location in html line if the html line contains the number of bids.
                    else if (html_line.contains("<b>Number of Bids:</b><span class=\"num-bids\">")){
                        numberOfBids = html_line.substring(65); // removes the beginning of the html tag
                        numberOfBids = numberOfBids.replace(numberOfBids.substring(numberOfBids.indexOf("<")),""); // replaces the ending html tag with the empty string which will remove the end html tag
                    }
                    //search for current price location in html line if the html line contains the current price.
                    else if (html_line.contains("<li><b>Current Price:</b><span class=\"current-price\">")){
                        currPrice = html_line.substring(69); // removes the beginning of the html tag
                        currPrice = currPrice.replace(currPrice.substring(currPrice.indexOf("<")),""); // replaces the ending html tag with the empty string which will remove the end html tag
                    }
                    //search for auction end date location in html line if the html line contains the auction end date.
                    else if (html_line.contains("<li><b>Ends On: </b>")){
                        auctionEndDate = html_line.substring(36); // removes the beginning of the html tag
                        auctionEndDate = auctionEndDate.replace(auctionEndDate.substring(auctionEndDate.indexOf("<")),""); // replaces the ending html tag with the empty string which will remove the end html tag
                    }
                    //search for the seller name which is the line after "<h4><b>Seller:</b></h4>"
                    else if (html_line.contains("<h4><b>Seller:</b></h4>") && !sellerNameRead){
                        html_line = sc2.nextLine();
                        sellerNameRead = true;
                        sellerName = html_line.substring(3); // removes the beginning of the html tag
                        sellerName = sellerName.replace(sellerName.substring(sellerName.indexOf("<")),""); // replaces the ending html tag with the empty string which will remove the end html tag
                    }
                    //search for the seller location which is the line after "<h4><b>Location:</b></h4>"
                    else if (html_line.contains("<h4><b>Location:</b></h4>") && !sellerLocationRead){
                        html_line = sc2.nextLine();
                        sellerLocationRead = true;
                        sellerLocation = html_line.substring(3); // removes the beginning of the html tag
                        sellerLocation = sellerLocation.replace(sellerLocation.substring(sellerLocation.indexOf("<")),""); // replaces the ending html tag with the empty string which will remove the end html tag
                        sellerLocation = sellerLocation.substring(sellerLocation.indexOf(", "));
                        sellerLocation = sellerLocation.replace(", ", "");
                    }
                    //search for the images which is the line after "<div class="carousel-inner" data-chocolat-title="Product Images">"
                    else if (html_line.contains("<div class=\"carousel-inner\" data-chocolat-title=\"Product Images\">")){
                        while (!html_line.contains("<div class=\"magnify\">")){
                            if (html_line.contains("<a href=\"https://sgws3productimages.azureedge.net/sgwproductimages/images")){
                                String temp = html_line.substring(49); // removes the beginning of the html tag
                                temp = temp.replace(temp.substring(temp.indexOf("\"")),""); // replaces the ending " with the empty string which will remove the end " in html code
                                imgArr.add(temp);
                            }
                            html_line = sc2.nextLine();
                        }
                    }
                }
                //create the seller string which is a concatenation of state and name
                if (sellerNameRead && sellerLocationRead){
                    seller = sellerLocation+ "-" + sellerName;
                }
                
                //Convert imgArr to string
                //inspired by https://stackoverflow.com/questions/599161/best-way-to-convert-an-arraylist-to-a-string
                StringBuilder sb = new StringBuilder();
                for (String img : imgArr){
                    sb.append(img+";"); // the ; will be a delimiter used for retrieving each image in the string
                }

                String imgStr = sb.toString();
                
                //place data retrieved previously and store data into hash map
                map.put(itemNumber, new TreeMap<String, String>());
                map.get(itemNumber).put("1", itemTitle);
                map.get(itemNumber).put("2", currPrice);
                map.get(itemNumber).put("3", auctionEndDate);
                map.get(itemNumber).put("4", seller);
                map.get(itemNumber).put("5", numberOfBids);
                map.get(itemNumber).put("6", imgStr);
                map.get(itemNumber).put("7", dateStr);
            }
           
        }
        mGUI.clear();
        mGUI.appendToGUI("Welcome to the shopgoodwill.com inventory organizer! This is what you output file looks like right now.\n\n");
        mGUI.appendToGUI("Item Number|Name|Price|Auction End Date|Seller|Number of Bids|Image(s)|Time of Query\n");
        //prints map to the GUI
        //inspired by https://stackoverflow.com/questions/26188532/iterate-through-nested-hashmap    
        for (Map.Entry<Integer, TreeMap<String, String>> outerEntry : map.entrySet()) {
            Integer itemNo = outerEntry.getKey();
            mGUI.appendToGUI(String.valueOf(itemNo));
            for (Map.Entry<String, String> innerEntry :outerEntry.getValue().entrySet()) {
                String value = innerEntry.getValue();
                mGUI.appendToGUI("|"+value);
            }
            mGUI.appendToGUI("\n");
        } 
        
        
    }
    
    // get the search results of the online search requested by the user
    public static void getOnlineSearch(String itemChosen, String minPriceChosen, String maxPriceChosen, 
                                        String status, String orderBy1, String orderBy2, String buyNow, 
                                        String pickupOnly, String noPickup, String cShipping, String canada, 
                                        String outsideUSandCanada, MainGUI mGUI, String categoryNo, String sellerNo) throws MalformedURLException, IOException{
        
        //this is the url that will be used to get the html code
        String url = "https://www.shopgoodwill.com/Listings?st="+ itemChosen +
        "&sg=&c="+categoryNo+"&s="+sellerNo+"&lp="+minPriceChosen+"&hp="+maxPriceChosen+
        "&sbn="+buyNow+"&spo="+pickupOnly+"&snpo="+noPickup+"&socs="+cShipping+"&sd=false&sca="+status+
        "&caed=11/29/2017&cadb=7&scs="+canada+"&sis="+outsideUSandCanada+"&col="+orderBy1+
        "&p=1&ps=40&desc="+orderBy2+
        "&ss=0&UseBuyerPrefs=true";
        
        mGUI.clear(); // clear MainGUI to display search results
 
        // variables to be placed into the main GUI (the search results)
        String itemTitle = null;
        String productNumber = null;
        String numberOfBids = null;
        String currPrice = null;
        String auctionEndDate = null;
        String sellerName = null;
        String imageUrl = null;
        String seller = null;
        String sellerLocation = null;
        
        //inspired by https://stackoverflow.com/questions/8616781/how-to-get-a-web-pages-source-code-from-java
        URL urlObject = new URL(url);
        URLConnection urlConnection = urlObject.openConnection();
        InputStream is = urlConnection.getInputStream();
        Scanner sc = new Scanner(is);
        mGUI.appendToGUI("Here are your search results.\n\n");
        while (sc.hasNext()){
                    String html_line = sc.nextLine();
                    // this html code will find the image associated with the search result
                    if (html_line.contains("<img class=\"lazy-load\" src=")){
                        imageUrl = html_line.substring(40); // removes the beginning of the html tag
                        imageUrl = imageUrl.replace(imageUrl.substring(imageUrl.indexOf("\"")),""); // replaces the ending html tag with the empty string which will remove the end html tag
                        mGUI.appendToGUI("Image URL: " + imageUrl + "\n");
                    }
                    // this html code will get the item number of a search result
                    else if (html_line.contains("<div class=\"product-number\"><span>Product #: </span>")){
                        productNumber = html_line.substring(68); // removes the beginning of the html tag
                        productNumber = productNumber.replace(productNumber.substring(productNumber.indexOf("<")),""); // replaces the ending html tag with the empty string which will remove the end html tag
                        mGUI.appendToGUI("Item Number: " + productNumber + "\n");
                    }
                    // this html code will find the title of the search result
                    else if (html_line.contains("<div class=\"title\">")){
                        html_line = sc.nextLine();
                        itemTitle = html_line.substring(20); // removes the beginning of the html tag
                        mGUI.appendToGUI("Title: " + itemTitle + "\n");
                    }
                    // this html code will find the number of bids of an item
                    else if (html_line.contains("<br>Bids:")){
                        numberOfBids = html_line.substring(30); // removes the beginning of the html tag
                        mGUI.appendToGUI("Number of Bids: " + numberOfBids + "\n");
                    }
                    // this html code will find the price of an item
                    else if (html_line.contains("<div class=\"product-price\">")){
                        html_line = sc.nextLine();
                        currPrice = html_line.substring(35); // removes the beginning of the html tag
                        currPrice = currPrice.replace(currPrice.substring(currPrice.indexOf("<")),""); // replaces the ending html tag with the empty string which will remove the end html tag
                        mGUI.appendToGUI("Price: " + currPrice + "\n");
                    }
                    //this html code will find the auction end date
                    else if (html_line.contains("<div class=\"timer countdown product-countdown\" data-countdown=")){
                        html_line = sc.nextLine();
                        auctionEndDate = html_line.substring(91); // removes the beginning of the html tag
                        auctionEndDate = auctionEndDate.replace(auctionEndDate.substring(auctionEndDate.indexOf("\"")),""); // replaces the ending html tag with the empty string which will remove the end html tag
                        mGUI.appendToGUI("Auction End Date: " + auctionEndDate + "\n\n");
                    }
            
        }
      
    }

    // method called when View Inventory is clicked which will display the current HashMap inventory to the GUI
    public static void getCurrentInventory(MainGUI mGUI) {
        mGUI.clear();
        mGUI.appendToGUI("Welcome to the shopgoodwill.com inventory organizer! This is what you output file looks like right now.\n\n");
        mGUI.appendToGUI("Item Number|Name|Price|Auction End Date|Seller|Number of Bids|Image(s)|Time of Query\n");
        //prints map to the GUI
        //inspired by https://stackoverflow.com/questions/26188532/iterate-through-nested-hashmap    
        for (Map.Entry<Integer, TreeMap<String, String>> outerEntry : map.entrySet()) {
            Integer itemNo = outerEntry.getKey();
            mGUI.appendToGUI(String.valueOf(itemNo));
            for (Map.Entry<String, String> innerEntry :outerEntry.getValue().entrySet()) {
                String value = innerEntry.getValue();
                mGUI.appendToGUI("|"+value);
            }
            mGUI.appendToGUI("\n");
        } 
    }

    // this method is called when the user selects Quit from the File menu and this is where the final output file is produced
    public static void produceOutputFile() throws FileNotFoundException {
        // changes the system.out setting to print to the text file instead of the console
        PrintStream out = new PrintStream(new FileOutputStream(output)); 
        System.setOut(out);
        
        System.out.println("Item Number|Name|Price|Auction End Date|Seller|Number of Bids|Image(s)|Time of Query\n");
        
        //prints map to the output file
        //inspired by https://stackoverflow.com/questions/26188532/iterate-through-nested-hashmap
        for (Map.Entry<Integer, TreeMap<String, String>> outerEntry : map.entrySet()) {
            Integer itemNo = outerEntry.getKey();
            System.out.print(itemNo);
            for (Map.Entry<String, String> innerEntry :outerEntry.getValue().entrySet()) {
                String value = innerEntry.getValue();
                System.out.print("|"+value);
            }
            System.out.println();
        } 
        
    }
    

    // this is where the offline search of queries is performed
    public static void searchItemsFromGUI(String itemNumberStr, MainGUI mGUI, Map<Integer, TreeMap<String, String>> map,
        ArrayList<String> searchArr) {
        
        mGUI.clear();
        mGUI.appendToGUI("Welcome to the shopgoodwill.com inventory organizer! This is what you output file looks like right now.\n\n");
        mGUI.appendToGUI("Item Number|Name|Price|Auction End Date|Seller|Number of Bids|Image(s)|Time of Query\n");
            
        int innerKey = 2; // key used to put items in the candidate HashMap
        boolean isItemNumber = true; // boolean method used for proper printing of the Map

        for (Map.Entry<Integer, TreeMap<String, String>> outerEntry : map.entrySet()) {
            Integer itemNo = outerEntry.getKey();
            Map<String,String> candidate = new TreeMap<>(); // a TreeMap representing the possible candidate entries for the offline search
            candidate.put("1", String.valueOf(itemNo));
                
            for (Map.Entry<String, String> innerEntry :outerEntry.getValue().entrySet()) {
                String value = innerEntry.getValue();
                candidate.put(String.valueOf(innerKey), value);
                innerKey++;
                    
            }
                
            // process for filtering through possible candidates for search result    
            if (candidate.get("1").contains(itemNumberStr) && candidate.get("2").toLowerCase().contains(searchArr.get(0).toLowerCase()) && candidate.get("3").toLowerCase().contains(searchArr.get(1).toLowerCase()) &&
                    candidate.get("4").toLowerCase().contains(searchArr.get(2).toLowerCase()) && candidate.get("5").toLowerCase().contains(searchArr.get(3).toLowerCase()) && candidate.get("6").toLowerCase().contains(searchArr.get(4).toLowerCase()) &&
                    candidate.get("7").toLowerCase().contains(searchArr.get(5).toLowerCase())){
                for (Map.Entry<String,String> c : candidate.entrySet()){
                     if (isItemNumber) {
                        mGUI.appendToGUI(c.getValue());
                        isItemNumber = false;
                     }
                     else mGUI.appendToGUI("|"+c.getValue());
                }
                mGUI.appendToGUI("\n");
            }
            isItemNumber = true;
            innerKey = 2;
                
        } 
    }

    // this method is called when the user selects Quit from the File menu and this is where the file used to reproduce logs is created
    public static void produceLogReconstructFile() throws FileNotFoundException {
        //date format for filename
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH_mm_ss");
        Date date = new Date();
        
        //file name is a concatenation of logreconstruct, current date and time of requested log, and .txt
        String reproduceLogFileName = "logreconstruct"+dateFormat.format(date)+".txt";
        
        // changes the system.out setting to print to the text file instead of the console
        PrintStream out = new PrintStream(new FileOutputStream(reproduceLogFileName)); 
        System.setOut(out);
        
        // prints only the item numbers to the text file
        for (Map.Entry<Integer, TreeMap<String, String>> outerEntry : map.entrySet()) {
            System.out.println(outerEntry.getKey());
        }
    }

    // go through each entry in the inventory and save all the images in it to output files
    public static void saveImages() {
        //inspired by https://stackoverflow.com/questions/26188532/iterate-through-nested-hashmap
        for (Map.Entry<Integer, TreeMap<String, String>> outerEntry : map.entrySet()) {
            Integer itemNo = outerEntry.getKey();
            for (Map.Entry<String, String> innerEntry :outerEntry.getValue().entrySet()) {
                String value = innerEntry.getValue();
                if (innerEntry.getKey().equals("6")) readAndWriteImages(value); // read and write the images for each entry in the inventory
            }
        } 
        
    }

    // method for reading and writing images - based on Phase 2 code
    public static void readAndWriteImages(String imgStr) {
        String[] imgArr = imgStr.split(";"); // convert the string concatenation of image urls to an array using the : delimeter
        for (String url_str : imgArr) System.out.println(url_str);
        
        for (String url_str : imgArr){
            String tempFileName = url_str.substring(url_str.lastIndexOf("/"));//filename is determined by whatever is after and including the last / in the url string
            
            String fileName = tempFileName.substring(1, tempFileName.length());//remove the / in tempFileName
            String extension = fileName.substring(fileName.indexOf(".")).substring(1);
            
            BufferedImage img = null;
            try {
                //inspired by https://docs.oracle.com/javase/tutorial/2d/images/loadimage.html
                URL url = new URL(url_str);
                img = ImageIO.read(url);
                
                //inspired by https://docs.oracle.com/javase/tutorial/2d/images/saveimage.html
                File outputFile = new File(fileName);
                ImageIO.write(img, extension, outputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    

}