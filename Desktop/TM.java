//Reference help used, Student: Joseph Donati. Big thanks.

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.*;
import java.io.*;

public class TM {
	// main only used to pass args to appMain, gets rid of static issues
	public static void main(String [] args) throws IOException {
		TM tm = new TM();
		tm.appMain(args);
	}
	
	// appMain is where program actually runs, not static
	void appMain(String [] args) throws IOException {
		
		// Declare variables
		String cmd;
		String taskName;
		String description;
		
		// Create TaskLog to interact with persistant log
		TaskLog log = new TaskLog();
		
		// Create object to pull time codes
		LocalDateTime currentTime = LocalDateTime.now();
		
		// If two args aren't provided, or describe is called with no description, print usage instructions 
 
		try {		
			cmd = args[0];
			taskName = args[1];
			if (args[0].equals("describe")) {
					description = args[2];
			}
			else 
				description = null;
		
		}
		catch (ArrayIndexOutOfBoundsException ex) {
			// Usage instructions
			System.out.println("Usage:");
			System.out.println("\tTM start <task name>");
			System.out.println("\tTM stop <task name>");
			System.out.println("\tTM describe <task name> <task description in quotes>");
			System.out.println("\tTM summary <task name>");
			System.out.println("\tTM summary");
			return;
		}
      
		
		// switch to direct operation
		switch (cmd) {
			case "stop": cmdStop(taskName, log, cmd, currentTime);
						break;
			case "start": cmdStart(taskName, log, cmd, currentTime);
						break;
			case "describe": cmdDescribe(taskName, log, cmd, currentTime, description);
						break;
         case "summary": cmdSummary(taskName, log, currentTime);
                  break;
			
		}
		
	}
	
	/**
	 * cmdStart method prints start entry to log
	 * @param taskName The name of the task
	 * @param log The TaskLog object wrapping the persistent log
	 * @param cmd The command supplied by the user
	 * @param currentTime The current time at log entry
	 * @throws IOException
	 */
	void cmdStart(String taskName, TaskLog log, String cmd, LocalDateTime currentTime) throws IOException {
		String line = (currentTime + " " + taskName + " " + cmd);
		log.writeLine(line);
	}
	
	/**
	 * cmdStop method prints stop entry to log
	 * @param taskName The name of the task
	 * @param log The TaskLog object wrapping the persistent log
	 * @param cmd The command supplied by the user
	 * @param currentTime The current time at log entry
	 * @throws IOException
	 */
	void cmdStop(String taskName, TaskLog log, String cmd, LocalDateTime currentTime) throws IOException {
		String line = (currentTime + " " + taskName + " " + cmd);
		log.writeLine(line);
	}
	
	/**
	 * cmdDescribe method prints describe entry to log
	 * @param taskName The name of the task
	 * @param log The TaskLog object wrapping the persistent log
	 * @param cmd The command supplied by the user
	 * @param currentTime The current time at log entry
	 * @param description The description of the task provided by the user
	 * @throws IOException
	 */
	void cmdDescribe(String taskName, TaskLog log, String cmd, LocalDateTime currentTime, String description) throws IOException {
      String line = (currentTime + " " + taskName + " " + cmd + " " + description);
      log.writeLine(line);
	}
   
   /**
	 * cmdSummary method prints describe entry to log
	 * @param taskName The name of the task
	 * @param log The TaskLog object wrapping the persistent log
	 * @param currentTime The current time at log entry
	 * @throws IOException
	 */
   //file and scanner referenced from stack overflow "How to print lines from a file that contains a specific word in java"
   void cmdSummary(String taskName, TaskLog log, LocalDateTime currentTime) throws IOException {
		String line = (currentTime + " " + taskName + " Summary Taken");
      
		Scanner in = null;
      File file = new File("TMlog.txt");
      in = new Scanner(file);
         while(in.hasNext())
         {
         String FileLine = in.nextLine();
         if(FileLine.contains(taskName)){
               if(FileLine.contains(" describe ")){
               System.out.println(FileLine);
               }
         }
        }
      log.writeLine(line);
         
	}
	
	
	
	
	/**
	 * The TaskLog class has methods for writing and reading from a persistent log file
	 */
	public class TaskLog {
		
		/*Reference note* Needed a refresher on a file I/O, used method from: Starting out with Java, 2nd Edition, Gaddis + Muganda, print*/
		
		// Create FileWriter and PrintWriter to write to log file
		public FileWriter fwriter;
		public PrintWriter outputFile;
		
		
		/**
		 * Constructor, creates file I/O objects
		 */
		public TaskLog() throws IOException{
			fwriter = new FileWriter("TMlog.txt", true);
			outputFile = new PrintWriter(fwriter);
		}
		
		/**
		 * The writeLine method writes a line to the persistent log file
		 * @param line The line to write to the log file
		 * @throws IOException
		 */
		void writeLine(String line) throws IOException{
			outputFile.println(line);
						outputFile.close();
		}		
	}
}

