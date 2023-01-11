// This is a changed version of the example DB file. It is untested and will probably not work right now
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.PrintWriter;

public class DB {
  public static final int NUM_RECORDS = 10;
  public static final int RECORD_SIZE = 97;
  public int numSortedRecords;
  public int numOverflowRecords;
  
  private RandomAccessFile Database;
  private RandomAccessFile Overflow;
  private RandomAccessFile Din;
  private int num_records;
  private FileWriter dout;
  private FileWriter cout;
  private BufferedReader cin;
  private String name;
  
  //public PrintWriter writer = new PrintWriter("Database.csv");

  public DB() {
    this.Din = null;
    this.overflow = null;
    this.database = null;
    this.num_records = 0;
    this.numSortedRecords = 0;
    this.numOverflowRecords = 0;
  }
  public void create(){
	   Scanner in = new Scanner(System.in);
	   System.out.print("Enter the name of a .csv file(e.g., colleges_lf.csv");
   	   name = in.nextLine();
	   String namecsv = name + ".csv";
	   String nameconfig = name + ".config"; 
	   String namedata = name + ".data";
	   String sameoverflow = name + ".overflow";
	  
	  try{
		  din = new BufferedReader(new FileREader(namecsv));
		  cout = new FileWirter(new File(nameconfig));
		  dout = new FileWriter(new File(namedata));
		  String line = din.readLine();
		  while(record != null)
		  {
			String[] fields = line.split(",");
			writeRecord();
			num_records++;
			line = din.readLine();
		  }
	  }
	  catch (IOException e) 
	  {
		  e.printStackTrace();
	  }
		 
		
			  
			  
			  
			  
			
		  
		
  /**
   * Opens the file in read/write mode
   * 
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
        Din.seek(0); // return to the top of the file
        Din.skipBytes(record_num * RECORD_SIZE);
        // parse record and update fields
        fields = Din.readLine().split(",", 0);
        record.updateFields(fields);
      } catch (IOException e) {
        System.out.println("There was an error while attempting to read a record from the database file.\n");
        e.printStackTrace();
      }
    }

    return record;
  }
  
    
public Record writeRecord(){
   // This sets up the database. The main function for part 1 still needs made so that the readRecord can read the database file.
   // Builds the initial database.
    Record record = new Record();
    String[] fields;
    String transfer;
    int counter = 0;
    while(counter < NUM_RECORDS){
        try{
            fields = Din.readLine().split(",", 0);
            counter = counter + 1;
            transfer = String.format("%1$7s", fields[0]) + "," + String.format("%1$20s", fields[1]) + "," +  String.format("%1$20s", fields[2]) + "," +  String.format("%1$50s", fields[3] + "\n");
            Database.writeChars(transfer);
        } catch(IOException e){
            System.out.println("There was an error while attempting to write the database file.\n");
            e.printStackTrace();
            counter = counter + 1;
        }

    }

    return record;
  }
  
  public Record appendRecord(int id, String state, String city, String name){
    
    
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
  
  
  public Record findRecord(int id, int recordNum, String fileId, String state, String city, String name){
    Scanner in = new Scanner(System.in);
    try
    {
      System.out.print("Enter the ID to search for: ");
      String id = in.nextLine();
      recordNum = binarysearch(search);
      System.out.println( search + "Found in:" + recordNum);
    } catch(IOException e){
      System.out.println("There was an error while attempting to find a record in the database.\n");
      e.printStackTrace();
	}
    
  }
  
  public Record addRecord(int id, String state, String city, String name){
    try
    {
      string[] fields = new String[4];
      //opening overflow file
      Scanner in = new Scanner(System.in);
      System.out.print("Enter the ID");
      fields[0] = in.nextLine();
      System.out.print("Enter the name of the State");
      fields[1] = in.nextLine();
      System.out.print("Enter the city");
      fields[2] = in.nextLine();
      System.out.print("Enter the name");
      fields[3] = in.nextLine();
    }
  }
    
  
  public Record updateRecord(int id, String state, String city, String name){
    
  }
  
  public Record deleteRecord(int id){
    
  }
    
  public static void main(String[] args){
    // needs to read and parse a line fron csv file
     	Scanner in = new Scanner(System.in);
	String fileName;
	System.out.println( "Please enter the name of the file (including extention): ");
     	System.out.println("\n");
	String entryFile = in.nextLine();
	System.out.println( "What do you wanna do ");
	System.out.println("1.) Create a New Database");
	System.out.println("2.) Open Database");
	System.out.println("3.) Close Database");
	System.out.println("4.) Display Record");
	System.out.println("5.) Update Record");
	System.out.println("6.) Create Report");
	System.out.println("7.) Add Record");
	System.out.println("8.) Delete Record");
	System.out.println("9.) Quit");
	int num  = in.nextInt();
	while(num != 9)
	{
		if(num == 1)
		{
			create();
		}
		if(num == 2)
		{
			open( entryFile);
		}
	}
	System.exit(0);																 
  }
  
}
	
