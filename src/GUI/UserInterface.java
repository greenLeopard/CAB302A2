/**
 * 
 */
package GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import Store.*;
import Stock.*;
import Delivery.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * 
 * User Interface for a supermarket inventory system
 * 
 * @author Chris and Lucas
 *
 */
public class UserInterface {

	public static Store store = Store.getInstance();
	
	/**
	 * Constructor
	 */
	public UserInterface() {
	}
	
	
	
	/**
	 * Create the show the graphical user interface.
	 */
	private static void createAndShowGUI() {
		store.setName("SuperMart");
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		//Create and set up the window. 
        JFrame frame = new JFrame(store.getName() + " Inventory Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        
        // create top level panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // create and add buttons to panel
        JPanel topPanel = new JPanel(new FlowLayout());
        
        // add capital label
        JLabel capitalLabel = new JLabel("Capital: " + store.displayCapital());
        topPanel.add(capitalLabel);

        //add buttons
        JButton loadItemPropertiesButton = new JButton("Load Item Properties");
        JButton exportManifestButton = new JButton("Export Manifest");
        JButton loadManifestButton = new JButton("Load Manifest");
        JButton loadSalesLogButton = new JButton("Load Sales Log");

        // add ActionListeners
        
        loadItemPropertiesButton.addActionListener(new ActionListener() {
        	
        	/**
        	 * Initialises item properties.
        	 * 
        	 * All item quantities must be zero.
        	 * 
        	 */
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Load Item Properties
				System.out.println("Item properties button clicked");
				FileDialog fd = new FileDialog(frame, "Load item properties", FileDialog.LOAD);
				fd.setVisible(true);
				
				try {
					FileReader reader = new FileReader(fd.getFile());
					BufferedReader bufferedReader = new BufferedReader(reader);
					String line;
					String[] propertiesList;
					while((line = bufferedReader.readLine()) != null) {
						propertiesList = line.split(",");
						switch (propertiesList.length) {
						case 5: // item without temperature
							store.setItemProperties(new Item(propertiesList[0], Double.parseDouble(propertiesList[1]), Double.parseDouble(propertiesList[2]), Integer.parseInt(propertiesList[3]), Integer.parseInt(propertiesList[4])));
							break;
						case 6: // item with temperature
							store.setItemProperties(new Item(propertiesList[0], Double.parseDouble(propertiesList[1]), Double.parseDouble(propertiesList[2]), Integer.parseInt(propertiesList[3]), Integer.parseInt(propertiesList[4]), Double.parseDouble(propertiesList[5])));
							break;
						default:
							break;
						}
					}
					bufferedReader.close();
					exportManifestButton.setEnabled(true);
			        loadManifestButton.setEnabled(true);
			        loadSalesLogButton.setEnabled(true);
			        loadItemPropertiesButton.setEnabled(false);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
        
        
        exportManifestButton.addActionListener(new ActionListener() {
        	/**
        	 * Export a manifest based on current inventory.
        	 */
			@Override
			public void actionPerformed(ActionEvent e) {
				//find all items where amount in stock is less than reorder point
				//create manifest to order the the reorder quantity of each item which minimizes cost
				//save as a CSV to a location of the user's choice
				
				// TODO Create an optimised manifest object from current inventory.
					// TODO get
				
				FileDialog fd = new FileDialog(frame, "Export Manifest", FileDialog.SAVE);
				fd.setVisible(true);
				String path = fd.getFile();
				try {
					Manifest manifest = new Manifest(store);
					System.out.println(manifest.toString());
					manifest.writeToCSV(path);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(frame, "Error writing manifest to CSV", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (DeliveryException e2) {
					System.out.println(e2.getMessage());
					JOptionPane.showMessageDialog(frame, "Error exporting manifest. Are item properties properly loaded?", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
        
        
        loadManifestButton.addActionListener(new ActionListener() {
        	/**
        	 * Load in a manifest; decreases capital and increases inventory.
        	 */
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Load manifest button clicked");
				// present user with file selection dialog
				FileDialog fd = new FileDialog(frame, "Load Manifest", FileDialog.LOAD);
				fd.setVisible(true);
				String path = fd.getFile();
				
				try {
					Manifest manifest = new Manifest(path);
					// update capital shown in GUI
					System.out.println(manifest.getCost());
					store.addCapital(-manifest.getCost());
					capitalLabel.setText("Capital: " + store.displayCapital());
					// update item quantities in GUI
					
					frame.repaint();
					JOptionPane.showMessageDialog(frame, "Manifest Successfully Loaded.\nTotal Price: $" + manifest.getCost());
				} catch (IOException e3) {
					JOptionPane.showMessageDialog(frame, "Error reading file", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (CSVFormatException e3) {
					JOptionPane.showMessageDialog(frame, "CSV in wrong format", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (StockException e3) {
					JOptionPane.showMessageDialog(frame, "Stock Exception, have item properties been loaded?", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (DeliveryException e1) {
					System.out.println(e1.getMessage());
					JOptionPane.showMessageDialog(frame, "A delivery error occured.", "Error", JOptionPane.ERROR_MESSAGE);
				}
				System.out.println("manifest should've been loaded!");
			}//end action performed
		});
        
        
        loadSalesLogButton.addActionListener(new ActionListener() {
        	/**
        	 * Load in a sales log; increases capital and  inventory.
        	 */
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Load Sales
				System.out.println("Load Sales Log button clicked");
			}
		});
        
        
        topPanel.add(loadItemPropertiesButton);
        topPanel.add(exportManifestButton);
        topPanel.add(loadManifestButton);
        topPanel.add(loadSalesLogButton);
        exportManifestButton.setEnabled(false);
        loadManifestButton.setEnabled(false);
        loadSalesLogButton.setEnabled(false);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // create and add table to centre
        String[] columnNames = {"Name", "Quantity", "Manufactuing Cost", "Sell Price", "Reorder Point", "Reorder Amount", "Temperature"};
        JTable table = new JTable(new DefaultTableModel(columnNames, 3)); // initialize table with columns and empty rows
        JScrollPane pane = new JScrollPane(table);
        mainPanel.add(pane, BorderLayout.CENTER);
        
        //add panel to gui
        frame.getContentPane().add(mainPanel);
        frame.pack();
        
        //Display the window. 
        frame.setPreferredSize(new Dimension(800, 800)); //TODO find better dimensions
        frame.pack(); 
        frame.setVisible(true);
	}

	/**
	 * The entry point of the program. This launches the GUI.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Main Started");
		createAndShowGUI();
	}

}
