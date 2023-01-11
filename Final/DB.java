import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;


public class DB {
  public int NUM_RECORDS = 10;
  public static final int RECORD_SIZE = 123;
  public static boolean isOpen = false;

  private RandomAccessFile Overflow;
  private RandomAccessFile Din;
  private RandomAccessFile Database;
  private RandomAccessFile Config;
  private RandomAccessFile report;
  private FileWriter dout;
  private int num_records;
  private int numOverflowRecords;
  private String readFile = "Data";


  public DB() {
    	this.Din = null;
	this.Overflow = null;
	this.Database = null;
	this.Config = null;
	this.report = null;
    	this.num_records = 0;
	this.numOverflowRecords = 0;
  }

  /**
   * Opens the file in read/write mode
   * Should also be able to be used to create the files
   * @param filename (e.g., input.txt)
   * @return status true if operation successful
   */
  public boolean open(String filename) {
    
	// Set the number of records
	//this.num_records = NUM_RECORDS;

	// Open file in read/write mode
	if(!isOpen)
	{		
		try {
			this.Din = new RandomAccessFile(filename + ".csv", "rw");
			this.Overflow = new RandomAccessFile(filename + ".overflow", "rw");
			this.Database = new RandomAccessFile(filename + ".data", "rw");
			this.Config = new RandomAccessFile(filename + ".config", "rw");
			isOpen = true;
			try{
				BufferedReader reader = new BufferedReader(new FileReader(filename + ".csv"));
				int lines = 0;
				while(reader.readLine() != null){
					lines = lines + 1;
				}
				this.NUM_RECORDS = lines;
				BufferedReader overflowReader = new BufferedReader(new FileReader(filename + ".overflow"));
				lines = 0;
				while(overflowReader.readLine() != null){
					lines = lines + 1;
				}
				this.numOverflowRecords = lines;
			} catch(IOException e){
				e.printStackTrace();
			}
			try{
				Config.seek(0);
				Config.writeBytes(String.valueOf(this.NUM_RECORDS));
			} catch(IOException e){
				e.printStackTrace();
			}
			return isOpen;
		} catch (FileNotFoundException e) {
			System.out.println("Could not open file\n");
			e.printStackTrace();
			return isOpen;
		}
	}
	else
		System.out.println("CLOSE THE DATABASE FIRST");
		return isOpen;
  }

  /**
   * Close the database file
   */
  public boolean close() {
	  if (isOpen)
	  {
		  try {
		    	Din.close();
			Overflow.close();
			Database.close();
			this.num_records = 0;
			this.numOverflowRecords = 0;
			this.Overflow = null;
			this.Din = null;
			this.Database = null;
			this.Config = null;
			isOpen = false;
			return isOpen;
		} catch (IOException e) {
			System.out.println("There was an error while attempting to close the database file.\n");
			e.printStackTrace();
			return isOpen;
		}
	  }
	  else
	  {
		  System.out.println("Database is not open");
		  return isOpen;
	  }
  }

  /**
   * Get record number n (Records numbered from 0 to NUM_RECORDS-1)
   * 
   * @param record_num
   * @return values of the fields with the name of the field and
   *         the values read from the record
   */
  public Record readRecord(int record_num) {
    Record record = new Record();
    String[] fields;

    if ((record_num >= 0) && (record_num < this.NUM_RECORDS)) {
      try {
		if(readFile == "Data"){
			Database.seek(0); // return to the top of the file
			Database.skipBytes(record_num * RECORD_SIZE);
		
			// parse record and update fields
			fields = Database.readLine().split(",", 0);
		} else{
			Overflow.seek(0);
			Overflow.skipBytes(record_num * RECORD_SIZE);
			fields = Overflow.readLine().split(",", 0);
		}
		record.updateFields(fields);
		
      } catch (IOException e) {
      	System.out.println("There was an error while attempting to read a record from the database file.\n");
        e.printStackTrace();
      }
    } 

    return record;
  }
  
  // Writes a record to the database
  //  @param record form of string, already formatted.
  // @return true if file is open, false if not.
  public boolean writeRecord(String toWrite){
   	 Record record = new Record();
	String transfer = toWrite;
	try{
		Database.writeBytes(transfer);
	} catch(IOException e){
		System.out.println("There was an error while attempting to write the database file.\n");
		System.out.println(toWrite);
		e.printStackTrace();
	}
			
    return isOpen;
  }
  
    /**
   * appends a record to the overflow file. Does the same as addRecord
   * @return true if the file is open, false if not
   */  
  
  public boolean appendRecord(){
	  Record record = new Record();
	  String fields;
	  Scanner input = new Scanner(System.in);
	  System.out.println("Please enter the ID to append: ");
	  fields = String.format("%1$10s", input.nextLine());
	  System.out.println("Please enter the state to append: ");
	  fields = fields + "," + String.format("%1$20s", input.nextLine());
	  System.out.println("Please enter the city to append: ");
	  fields = fields + "," + String.format("%1$20s", input.nextLine());
	  System.out.println("Please enter the name to append (Use '_' in place of spaces: ");
	  fields = fields + "," + String.format("%1$70s", input.nextLine());
	  try{
		Overflow.writeBytes(fields);
	  } catch(IOException e) {
		System.out.println("There was an error while attempting to write the overflow file.\n");
		e.printStackTrace();
	  }
	  
	  return isOpen;
  }
  
    /**
   * prompts user for a record ID, then displays a record
   */
  public void display(){
	  Scanner in = new Scanner(System.in);
	 // RandomAccessFile din = new RandomAccessFile( filename + ".data" , "r");
	  System.out.print("Enter the Id you want to find: ");
	  String find = in.nextLine();
	  int record = binarySearch(String.valueOf(find));
	  record = findRecord(String.valueOf(find));
	  //if(binarySearch(String.valueOf(find)) == -1){
	  if(record == -1){
		  System.out.println("Record does not exist");
	  } else{
		System.out.println("Record found: "  + readRecord(record));
	  }
	  this.readFile = "Data";
  }
  
  
    /**
   * Updates a value in a record
   */
  
  public void update(){
	Record record ;
	Scanner in = new Scanner(System.in);
	System.out.println("Enter the ID you want to update");
	String id  = in.nextLine();
	int update = findRecord(id);
	if (update == -1)
	{
		System.out.println("Record Not Found");
		System.out.println("Enter the valid ID");
		update();
	}
	else{		
		record =  readRecord(update);
		System.out.println("Record found: "  + record);
		overwriteRecord(record, update);
	}		
  }
  
    /**
   * Changes the values in the record
   *
   * @param record, the record to change
   * @return true if file is open, false if not
   */
  public boolean overwriteRecord(Record record, int id){
	
		Scanner in = new Scanner(System.in);
		System.out.println(" Which of the following list you want to update");
		System.out.println(" 1. state ");
		System.out.println("2. city ");
		System.out.println("3. name ");
		System.out.println("4. quit ");
		int num =  in.nextInt();
		String notused =  in.nextLine();
		while(num != 4){
			if ( num == 1){
				System.out.println(" Enter new state ");
				String state  = in.nextLine();
				state = String.format("%1$10s" , state.substring(0, Math.min(state.length(), 19)));
				record.State = record.State.replace(record.State, state);
				String transfer = String.format("%1$10s", record.Id) + "," + String.format("%1$20s", record.State) + "," +  String.format("%1$20s", record.City) + "," +  String.format("%1$70s", record.Name + "\n");
				try{
					if(readFile == "Data"){
						Database.seek(0);
						Database.skipBytes(num_records * RECORD_SIZE );
						Database.write(transfer.getBytes());
					} else{
						Overflow.seek(0);
						Overflow.skipBytes(id * RECORD_SIZE);
						Overflow.write(transfer.getBytes());
						this.readFile = "Data";
					}
					System.out.println(" State Updated Successfully");
				} catch(IOException e){
					e.printStackTrace();
				}
				num = 4;
			}
			else if(num == 2)
				{
					System.out.println(" Enter the new city ");
					String city = in.nextLine();
					city = String.format("%1$10s" , city.substring(0, Math.min(city.length(), 19)));
					record.City = record.City.replace(record.City, city);
					String transfer = String.format("%1$10s", record.Id) + "," + String.format("%1$20s", record.State) + "," +  String.format("%1$20s", record.City) + "," +  String.format("%1$70s", record.Name + "\n");
					try{
						if(readFile == "Data"){
							Database.seek(0);
							Database.skipBytes(num_records * RECORD_SIZE );
							Database.write(transfer.getBytes());
						} else{
							Overflow.seek(0);
							Overflow.skipBytes(id * RECORD_SIZE);
							Overflow.write(transfer.getBytes());
							this.readFile = "Data";
						}
						System.out.println(" City Updated Successfully");
					} catch(IOException e){
						e.printStackTrace();
					}
					  num = 4;
				  }
			else if(num == 3)
				{
					System.out.println(" Enter the new  School name ");
					String name = in.nextLine();
					name = name.replace(" ", "_");
					name = String.format("%1$10s" , name.substring(0, Math.min(name.length(), 69)));
					record.Name = record.Name.replace(record.Name, name);
					String transfer = String.format("%1$10s", record.Id) + "," + String.format("%1$20s", record.State) + "," +  String.format("%1$20s", record.City) + "," +  String.format("%1$70s", record.Name + "\n");
					try{
						if(readFile == "Data"){
							Database.seek(0);
							Database.skipBytes(num_records * RECORD_SIZE );
							Database.write(transfer.getBytes());
						} else{
							Overflow.seek(0);
							Overflow.skipBytes(id * RECORD_SIZE);
							Overflow.write(transfer.getBytes());
							this.readFile = "Data";
						}
						System.out.println(" School Name Updated Successfully");
					} catch(IOException e){
						e.printStackTrace();
					}
					  num = 4;
				  }
			else
				{
					System.out.println("Enter valid num");
					num = in.nextInt();
				}
		}
	return isOpen;
  }
    /**
   * finds a record based off of id
   * 
   * @param id
   * @return Record number or -1 if id not found
   */
  
  public int findRecord(String id){
	  int recNumber;
	  this.readFile = "Data";
	  if(binarySearch(id) != -1){
		  this.readFile = "Data";
		  recNumber = binarySearch(id);
	  }else if(linearSearch(id) != -1){
		  this.readFile = "Overflow";
		  recNumber = linearSearch(id);
	  } else{
		  this.readFile = "Data";
		  recNumber = -1;
	  }
	  return recNumber;
  }
  
  
     /**
   * Deletes a record
   */ 
  public void deleteRecord()
	{

		Record record = new Record();
		Scanner in = new Scanner(System.in);
		System.out.println("Enter the Id you want to delete");
		String search = in.nextLine();
		
		int num = findRecord(search);				
		if ( num == -1){
			return;
		}
		record = readRecord(num);
		String state = "NA";
		record.State = record.State.replace(record.State , state);
		//System.out.println(record.State);
		String city = "NA";
		record.City = record.City.replace(record.City , city);
		//System.out.println(record.State);
		String name = "NA";
		record.Name = record.Name.replace(record.Name, name);	
		//System.out.println(record.State);	
		String transfer = String.format("%1$10s", record.Id) + "," + String.format("%1$20s", record.State) + "," +  String.format("%1$20s", record.City) + "," +  String.format("%1$70s", record.Name + "\n");
		try{
			if(readFile == "Data"){
				Database.seek(0);
				Database.skipBytes(num_records * RECORD_SIZE );
				Database.write(transfer.getBytes());
			} else{
				Overflow.seek(0);
				Overflow.skipBytes(num * RECORD_SIZE);
				Overflow.write(transfer.getBytes());
				this.readFile = "Data";
			 }
			 System.out.println(" Record Deleted Successfully");
		}catch (IOException e)
		{
			e.printStackTrace();
		}		
	}
	
    /**
   * Adds a record to the overflow file
   */
	
public void addRecord()
  {	  
	  try
	  {
		 String[] fields = new String[4];
		 Scanner in = new Scanner(System.in);
		 System.out.print(" Enter the ID you want to add: ");
		 fields[0] = in.nextLine();
		 System.out.print(" Enter the state you want to add: ");
		 fields[1] = in.nextLine();
		 System.out.print(" Enter the city you want to add: ");
		 fields[2] = in.nextLine();
		 System.out.print(" Enter the name you want to add: ");
		 fields[3] = in.nextLine();
		 fields[3] = fields[3].replace(" ", "_");
		 String transfer = String.format("%1$10s", fields[0].substring(0, Math.min(fields[0].length(), 10))) + "," + String.format("%1$20s", fields[1].substring(0, Math.min(fields[1].length(), 19))) + "," +  String.format("%1$20s", fields[2].substring(0, Math.min(fields[2].length(), 19))) + "," +  String.format("%1$70s", fields[3].substring(0, Math.min(fields[3].length(), 69)) + "\n");
		 Overflow.seek(0);
		 Overflow.skipBytes(numOverflowRecords * RECORD_SIZE );
		 Overflow.write(transfer.getBytes());
		 numOverflowRecords++;
		 System.out.println(" Record Added Successfully");
		 System.out.println(" There are now " + numOverflowRecords + " record in Overflow");
	  }catch (IOException e)
	  {
		e.printStackTrace();
	  }	  
  }
	
	/**
   * Creates and displays the report
   * 
   * @param filename
   */
  public void createReport(String filename)
  {
		Record record = new Record();
		String transfer;
		try
		{	  
			this.report = new RandomAccessFile(filename + ".report.txt", "rw");
			System.out.println("Top 10 list are as follows: ");
			for (int i =  0; i < 10; i++)		  
			{
				record = readRecord(i);
				System.out.println(record);
				transfer = String.format("%1$10s", record.Id) + "," + String.format("%1$20s", record.State) + "," +  String.format("%1$20s", record.City) + "," +  String.format("%1$70s", record.Name + "\n");
				report.writeBytes(transfer);
		 
			}
		}catch (IOException e)
		{
			e.printStackTrace();
		}
  }
		  
	  
	  

  /**
   * Binary Search by record id
   * 
   * @param id
   * @return Record number (which can then be used by read to
   *         get the fields) or -1 if id not found
   */
  public int binarySearch(String id) {
    id = String.format("%1$10s",id);
    int Low = 0;
    int High = NUM_RECORDS	- 1;
    int Middle = 0;
    boolean Found = false;
    Record record;

    while (!Found && (High >= Low)) {
      Middle = (Low + High) / 2;
      record = readRecord(Middle);
      String MiddleId = record.Id;
      int result = MiddleId.compareTo(id); 
     // int result = Integer.parseInt(MiddleId) - Integer.parseInt(id); // DOES INT COMPARE of MiddleId[0] and id
      if (result == 0){
	num_records = Middle;
        Found = true;
      }
      else if (result < 0)
        Low = Middle + 1;
      else
        High = Middle - 1;
    }
    if (Found) {
      return Middle; // the record number of the record
    } else
      return -1;
  }
  
    /**
   * Linear Search by record id
   * 
   * @param id
   * @return Record number or -1 if id not found
   */
  public int linearSearch(String id){
	id = String.format("%1$10s",id);
	int counter = 0;
	int result = -1;
	String Fields[];

	try{		
		while(counter < numOverflowRecords ){
				Overflow.seek(0); // return to the top of the file
				Overflow.skipBytes(counter * RECORD_SIZE);
				// parse record and update fields
				Fields = Overflow.readLine().split(",", 0);
				result = Fields[0].compareTo(id); 
				if(result == 0){
					result = counter;
					return counter;
					//counter = numOverflowRecords;
					
					
				}else {
					counter = counter + 1;
				}
		}

	} catch(IOException e){
		e.printStackTrace();
	}
	return -1;
  }
	
	/**
   * Create the DB file.
   * 
   * @param entryFile
   *
   */
  public void createDB(String entryFile){
	  DB db = new DB();
	  String[]fields;
	  Record record = new Record();
	  String transfer;
	  int counter = 0;
	  if(!isOpen){
		open(entryFile);	  
		while(counter < NUM_RECORDS){
			try{
				fields = Din.readLine().split(",", 0);
				counter = counter + 1;
				// sets min length through String.format. Sets max length through .substring
				transfer = String.format("%1$10s", fields[0].substring(0, Math.min(fields[0].length(), 10))) + "," + String.format("%1$20s", fields[1].substring(0, Math.min(fields[1].length(), 19))) + "," +  String.format("%1$20s", fields[2].substring(0, Math.min(fields[2].length(), 19))) + "," +  String.format("%1$70s", fields[3].substring(0, Math.min(fields[3].length(), 69)) + "\n");
				writeRecord(transfer);
				record.updateFields(fields);
				isOpen = false;
			} catch(IOException e){
				System.out.println("There was an error while attempting to write the database file.\n");
				e.printStackTrace();
				counter = counter + 1;
			}
		}
	  }
	  else{
			System.out.println("Error, Please close the database before creating a new one.");
	  }
  }
  
    /**
   * Displays the menue
   */
  public static void Menu() 
	{
		//Menu choices
		System.out.println("Choose one of the following menu ");
		System.out.println("1. Create a New Database");
		System.out.println("2. Open Database");
		System.out.println("3. Close Database");
		System.out.println("4. Display Record");
		System.out.println("5. Update Record");
		System.out.println("6. Create Report");
		System.out.println("7. Add Record");
		System.out.println("8. Delete Record");
		System.out.println("9. Quit/n");
	}
  
  
	// Sends file to writeRecord to build the initial database
	// Calls readRecord to display the desired records.
	// Written on windows, seems to work on Turing.
    public static void main(String[] args){
	Scanner in = new Scanner(System.in);
	Scanner secondScanner = new Scanner(System.in);
	String fileName;

	DB db = new DB();
	db.Menu();
	int choice = in.nextInt();
	int innerChoice;
	String entryFile = "";
	while (choice != 9)
		{
			if (choice == 1) {
				entryFile = in.nextLine();
				System.out.println("Create a Database");
				System.out.println( "Please enter the name of the file (No extention): ");
				System.out.println("\n");
				entryFile = in.nextLine();
				db.createDB(entryFile);
				db.Menu();
				choice = in.nextInt();
			}
			else if (choice == 2) {
				entryFile = in.nextLine();
				System.out.println("Open a Database");
				System.out.println( "Please enter the name of the file (No extention): ");
				System.out.println("\n");
				entryFile = in.nextLine();				
				db.open(entryFile);
				db.Menu();
				choice = in.nextInt();
			}
			else if (choice == 3) {
				System.out.println("Close a Database");
				db.close();
				db.Menu();
				choice = in.nextInt();
			}
			else if (choice == 4) {
				System.out.println("Display a Record");
				if(!isOpen){
					System.out.println("No database open.");
					db.Menu();
					choice = in.nextInt();
				}else{
					db.display();
				
				//innerChoice =in.nextInt();
				//System.out.println(db.readRecord(innerChoice));
					db.Menu();
					choice = in.nextInt();
				}
			}
			else if (choice == 5) {
				System.out.println("Update a Record");
				if(!isOpen){
					System.out.println("No database open.");
					db.Menu();
					choice = in.nextInt();
				}else{
					db.update();
					db.Menu();
					choice = in.nextInt();
				}
			}
			else if (choice == 6) {
				System.out.println("Creating report");
				if(!isOpen){
					System.out.println("No database open.");
					db.Menu();
					choice = in.nextInt();
				}else{
					db.createReport(entryFile);
					db.Menu();
					choice = in.nextInt();
				}
			}
			else if (choice == 7) {
				System.out.println("Add a new Record");
				if(!isOpen){
					System.out.println("No database open.");
					db.Menu();
					choice = in.nextInt();
				}else{
					db.addRecord();
					//db.appendRecord();
					db.Menu();
					choice = in.nextInt();
				}
			}
			else if (choice == 8) {
				System.out.println("Delete a Record");
				if(!isOpen){
					System.out.println("No database open.");
					db.Menu();
					choice = in.nextInt();
				}else{
					db.deleteRecord();
					db.Menu();
					choice = in.nextInt();
				}
			}
			else
			{
				System.out.print("Invalid input, enter choice from the list: ");
				choice = in.nextInt();
			}
		}
		System.out.println("Quitting");
		System.exit(0);
  }
  
}
