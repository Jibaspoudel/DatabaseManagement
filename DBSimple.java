import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class DB {
  public static final int NUM_RECORDS = 10;
  public static final int RECORD_SIZE = 103;

  private RandomAccessFile Overflow;
  private RandomAccessFile Din;
  private RandomAccessFile Database;
  private RandomAccessFile Config;
  private int num_records;
  private int numOverflowRecords;


  public DB() {
    	this.Din = null;
	this.Overflow = null;
	this.Database = null;
	this.Config = null;
    	this.num_records = 0;
	this.numOverflowRecords = 0;
  }

  /**
   * Opens the file in read/write mode
   * Should also be able to be used to create the files
   * @param filename (e.g., input.txt)
   * @return status true if operation successful
   */
  public void open(String filename) {
    // Set the number of records
    this.num_records = NUM_RECORDS;

    // Open file in read/write mode
    try {
      	this.Din = new RandomAccessFile(filename + ".csv", "rw");
	this.Overflow = new RandomAccessFile(filename + ".overflow", "rw");
	this.Database = new RandomAccessFile(filename + ".data", "rw");
	this.Config = new RandomAccessFile(filename + ".config", "rw");
	  
    } catch (FileNotFoundException e) {
      	System.out.println("Could not open file\n");
      	e.printStackTrace();
    }
  }

  /**
   * Close the database file
   */
  public void close() {
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
    } catch (IOException e) {
      	System.out.println("There was an error while attempting to close the database file.\n");
      	e.printStackTrace();
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

    if ((record_num >= 0) && (record_num < this.num_records)) {
      try {
        Database.seek(0); // return to the top of the file
        Database.skipBytes(record_num * RECORD_SIZE);
		
	System.out.println("Record number: " + record_num);
        // parse record and update fields
        fields = Database.readLine().split(",", 0);
        record.updateFields(fields);
      } catch (IOException e) {
      	System.out.println("There was an error while attempting to read a record from the database file.\n");
        e.printStackTrace();
      }
    }

    return record;
  }
  
  // Builds the database.
  // 
  public boolean writeRecord(){
    Record record = new Record();
    String[] fields;
	String transfer;
	int counter = 0;
	while(counter < NUM_RECORDS){
		try{
			fields = Din.readLine().split(",", 0);
			counter = counter + 1;
			transfer = String.format("%1$10s", fields[0]) + "," + String.format("%1$20s", fields[1]) + "," +  String.format("%1$20s", fields[2]) + "," +  String.format("%1$50s", fields[3] + "\n");
			Database.writeBytes(transfer);
		} catch(IOException e){
			System.out.println("There was an error while attempting to write the database file.\n");
			e.printStackTrace();
			counter = counter + 1;
		}
		
	}

    return true;
  }

  /**
   * Binary Search by record id
   * 
   * @param id
   * @return Record number (which can then be used by read to
   *         get the fields) or -1 if id not found
   */
  public int binarySearch(String id) {
    int Low = 0;
    int High = NUM_RECORDS - 1;
    int Middle = 0;
    boolean Found = false;
    Record record;

    while (!Found && (High >= Low)) {
      Middle = (Low + High) / 2;
      record = readRecord(Middle);
      String MiddleId = record.Id;

      // int result = MiddleId[0].compareTo(id); // DOES STRING COMPARE
      int result = Integer.parseInt(MiddleId) - Integer.parseInt(id); // DOES INT COMPARE of MiddleId[0] and id
      if (result == 0)
        Found = true;
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
  
	// Sends file to writeRecord to build the initial database
	// Calls readRecord to display the desired records.
	// Written on windows, seems to work on Turing.
    public static void main(String[] args){
	Scanner in = new Scanner(System.in);
	String fileName;
	System.out.println( "Please enter the name of the file (No extention): ");
     	System.out.println("\n");
	String entryFile = in.nextLine();
	DB db = new DB();
	db.open(entryFile);
	db.writeRecord();
	System.out.println(db.readRecord(0));
	System.out.println(db.readRecord(5));
	System.out.println(db.readRecord(9));
	db.close();
	
	 
  }
  
}
