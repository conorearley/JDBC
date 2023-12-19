import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;


public class JDBCMainWindowContent extends JInternalFrame implements ActionListener
{	
	String cmd = null;

	// DB Connectivity Attributes
	private Connection con = null;
	private Statement stmt = null;
	private ResultSet rs = null;

	private Container content;

	private JPanel detailsPanel;
	private JPanel exportButtonPanel;
	private JPanel exportConceptDataPanel;
	private JScrollPane dbContentsPanel;

	private Border lineBorder;

	private JLabel ReadingID=new JLabel("Reading ID:                 ");
	private JLabel DeviceIDLabel=new JLabel("Device ID:                 ");
	private JLabel ReadingTypeLabel=new JLabel("Reading Type:               ");
	private JLabel ValueLabel=new JLabel("Value:      ");
	private JLabel ReadingTime=new JLabel("Reading Time:        ");

	private JTextField ReadingIDTF= new JTextField(10);
	private JTextField DeviceIDTF= new JTextField(10);
	private JTextField ReadingTypeTF=new JTextField(10);
	private JTextField ValueTF=new JTextField(10);
	private JTextField TimeTF=new JTextField(10);


	private static QueryTableModel TableModel = new QueryTableModel();
	//Add the models to JTabels
	private JTable TableofDBContents=new JTable(TableModel);
	//Buttons for inserting, and updating members
	//also a clear button to clear details panel
	private JButton updateButton = new JButton("Update");
	private JButton insertButton = new JButton("Insert");
	private JButton exportButton  = new JButton("Export");
	private JButton deleteButton  = new JButton("Delete");
	private JButton clearButton  = new JButton("Clear");

	private JButton  ReadingsByRoom = new JButton("ReadingsByRoom:");
	private JTextField ReadingsRoomTF  = new JTextField(12);
	private JButton ReadinsByDevic  = new JButton("ReadingsByDevice");
	private JTextField ReadingsbyDeviceTF  = new JTextField(12);
	private JButton ListAllDevices  = new JButton("ListAllDevices");
	private JButton ListAllRooms  = new JButton("ListAllDevicesInRooms");



	public JDBCMainWindowContent( String aTitle)
	{	
		//setting up the GUI
		super(aTitle, false,false,false,false);
		setEnabled(true);

		initiate_db_conn();
		//add the 'main' panel to the Internal Frame
		content=getContentPane();
		content.setLayout(null);
		content.setBackground(Color.lightGray);
		lineBorder = BorderFactory.createEtchedBorder(15, Color.red, Color.black);

		//setup details panel and add the components to it
		detailsPanel=new JPanel();
		detailsPanel.setLayout(new GridLayout(11,2));
		detailsPanel.setBackground(Color.lightGray);
		detailsPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "CRUD Actions"));

		detailsPanel.add(ReadingID);
		detailsPanel.add(ReadingIDTF);
		detailsPanel.add(DeviceIDLabel);
		detailsPanel.add(DeviceIDTF);
		detailsPanel.add(ReadingTypeLabel);
		detailsPanel.add(ReadingTypeTF);
		detailsPanel.add(ValueLabel);
		detailsPanel.add(ValueTF);
		detailsPanel.add(ReadingTime);
		detailsPanel.add(TimeTF);

		//setup details panel and add the components to it
		exportButtonPanel=new JPanel();
		exportButtonPanel.setLayout(new GridLayout(3,2));
		exportButtonPanel.setBackground(Color.lightGray);
		exportButtonPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Export Data"));
		exportButtonPanel.add(ReadingsByRoom);
		exportButtonPanel.add(ReadingsRoomTF);
		exportButtonPanel.add(ReadinsByDevic);
		exportButtonPanel.add(ReadingsbyDeviceTF);
		exportButtonPanel.add(ListAllDevices);
		exportButtonPanel.add(ListAllRooms);
		exportButtonPanel.setSize(500, 200);
		exportButtonPanel.setLocation(3, 300);
		content.add(exportButtonPanel);

		insertButton.setSize(100, 30);
		updateButton.setSize(100, 30);
		exportButton.setSize (100, 30);
		deleteButton.setSize (100, 30);
		clearButton.setSize (100, 30);

		insertButton.setLocation(370, 10);
		updateButton.setLocation(370, 110);
		exportButton.setLocation (370, 160);
		deleteButton.setLocation (370, 60);
		clearButton.setLocation (370, 210);

		insertButton.addActionListener(this);
		updateButton.addActionListener(this);
		exportButton.addActionListener(this);
		deleteButton.addActionListener(this);
		clearButton.addActionListener(this);

		this.ListAllDevices.addActionListener(this);
		this.ListAllRooms.addActionListener(this);
		this.ReadingsByRoom.addActionListener(this);
		this.ReadinsByDevic.addActionListener(this);

		content.add(insertButton);
		content.add(updateButton);
		content.add(exportButton);
		content.add(deleteButton);
		content.add(clearButton);


		TableofDBContents.setPreferredScrollableViewportSize(new Dimension(900, 300));

		dbContentsPanel=new JScrollPane(TableofDBContents,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		dbContentsPanel.setBackground(Color.lightGray);
		dbContentsPanel.setBorder(BorderFactory.createTitledBorder(lineBorder,"Database Content"));

		detailsPanel.setSize(360, 300);
		detailsPanel.setLocation(3,0);
		dbContentsPanel.setSize(700, 300);
		dbContentsPanel.setLocation(477, 0);

		content.add(detailsPanel);
		content.add(dbContentsPanel);

		setSize(982,645);
		setVisible(true);

		TableModel.refreshFromDB(stmt);
	}

	public void initiate_db_conn()
	{
		try
		{
			// Load the JConnector Driver
			Class.forName("com.mysql.jdbc.Driver");
			// Specify the DB Name
			String url="jdbc:mysql://localhost:3306/smart?serverTimezone=UTC";
			// Connect to DB using DB URL, Username and password
			con = DriverManager.getConnection(url, "root", "root");
			//Create a generic statement which is passed to the TestInternalFrame1
			stmt = con.createStatement();
		}
		catch(Exception e)
		{
			System.out.println("Error: Failed to connect to database\n"+e.getMessage());
		}
	}

	//event handling 
	public void actionPerformed(ActionEvent e)
	{
		Object target=e.getSource();
		if (target == clearButton)
		{
			ReadingIDTF.setText("");
			DeviceIDTF.setText("");
			ReadingTypeTF.setText("");
			ValueTF.setText("");
			TimeTF.setText("");

		}

		if (target == insertButton) {
			try {
				String updateTemp = "INSERT INTO readings VALUES(" +
						Integer.parseInt(ReadingIDTF.getText()) + ",'" +
						DeviceIDTF.getText() + "','" +
						ReadingTypeTF.getText() + "'," +
						Double.parseDouble(ValueTF.getText()) + ",'" +
						TimeTF.getText() + "')";

				stmt.executeUpdate(updateTemp);
			} catch (SQLException sqle) {
				System.err.println("Error with insert:\n" + sqle.toString());
			} finally {
				TableModel.refreshFromDB(stmt);
			}
		}
		if (target == deleteButton)
		{

			try
			{
				String updateTemp ="DELETE FROM readings WHERE ReadingID = "+ReadingIDTF.getText()+";";
				stmt.executeUpdate(updateTemp);

			}
			catch (SQLException sqle)
			{
				System.err.println("Error with delete:\n"+sqle.toString());
			}
			finally
			{
				TableModel.refreshFromDB(stmt);
			}
		}
		if (target == exportButton) {
			try {

				String exportQuery = "SELECT * FROM readings";

				rs = stmt.executeQuery(exportQuery);
				writeToFile(rs);
			} catch (SQLException sqle) {
				System.err.println("Error exporting data:\n" + sqle.toString());
			}
		}
		if (target == updateButton) {
			try {
				String updateTemp = "UPDATE readings SET " +
						"DeviceID = '" + DeviceIDTF.getText() + "', " +
						"ReadingType = '" + ReadingTypeTF.getText() + "', " +
						"Value = " + Double.parseDouble(ValueTF.getText()) + ", " +
						"ReadingTime = '" + TimeTF.getText() + "' " +
						"WHERE ReadingID = " + Integer.parseInt(ReadingIDTF.getText());

				stmt.executeUpdate(updateTemp);

				rs = stmt.executeQuery("SELECT * FROM readings");
				rs.next();
				rs.close();
			} catch (SQLException sqle) {
				System.err.println("Error with update:\n" + sqle.toString());
			} finally {
				TableModel.refreshFromDB(stmt);
			}
		}

		/////////////////////////////////////////////////////////////////////////////////////
		//I have only added functionality of 2 of the button on the lower right of the template
		///////////////////////////////////////////////////////////////////////////////////
		if(target == this.ListAllRooms){

			cmd = "SELECT r.RoomID,r.RoomName,COUNT(d.DeviceID) AS DeviceCount FROM Rooms r LEFT JOIN Devices d ON r.RoomID = d.RoomID GROUP BY r.RoomID, r.RoomName;";

			try {
				rs = stmt.executeQuery(cmd);
				if (rs.next()) {
					writeToFile(rs);
				//} else {
				//	System.out.println("No data to export.");
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}

		if(target == this.ListAllDevices){

			cmd = "SELECT DeviceID, DeviceName FROM Devices;";

			try{					
				rs= stmt.executeQuery(cmd); 	
				writeToFile(rs);
			}
			catch(Exception e1){e1.printStackTrace();}

		}
		if(target == this.ReadingsByRoom){
			String roomName = this.ReadingsRoomTF.getText();

			try{
				// Use a prepared statement
				String query = "CALL GetReadingsByRoom(?)";
				try (PreparedStatement pstmt = con.prepareStatement(query)) {
					pstmt.setString(1, roomName);
					rs = pstmt.executeQuery();
					writeToFile(rs);
				}
			}
			catch(Exception e1){e1.printStackTrace();}
		}
		if (target == this.ReadinsByDevic) {
			String deviceId = this.ReadingsbyDeviceTF.getText();

			try {
				// Use a prepared statement
				String query = "CALL GetReadingsForDevice(?)";
				try (PreparedStatement pstmt = con.prepareStatement(query)) {
					pstmt.setString(1, deviceId);
					rs = pstmt.executeQuery();
					if (rs.next()) {
						writeToFile(rs);
					}
				}
			}  catch (Exception e1) {
				e1.printStackTrace();
			}
		}

	}
	///////////////////////////////////////////////////////////////////////////

	private void writeToFile(ResultSet rs){
		try{
			System.out.println("In writeToFile");
			FileWriter outputFile = new FileWriter("Data.csv");
			PrintWriter printWriter = new PrintWriter(outputFile);
			ResultSetMetaData rsmd = rs.getMetaData();
			int numColumns = rsmd.getColumnCount();

			for(int i=0;i<numColumns;i++){
				printWriter.print(rsmd.getColumnLabel(i+1)+",");
			}
			printWriter.print("\n");
			while(rs.next()){
				for(int i=0;i<numColumns;i++){
					printWriter.print(rs.getString(i+1)+",");
				}
				printWriter.print("\n");
				printWriter.flush();
			}
			printWriter.close();
		}
		catch(Exception e){e.printStackTrace();}
	}
}
