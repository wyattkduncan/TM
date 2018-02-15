import java.io.*;
import java.time.*;
import java.util.*;
import java.time.temporal.ChronoUnit;

public class TM {
	public static void main(String [] args) throws IOException {
		TM tm = new TM();
		tm.appDriver(args);
	}
	
//where the program actually runs
	void appDriver(String [] args) throws IOException {		
		String cmd;
		String taskName;
		String description;		
		TaskLog log = new TaskLog();		
		LocalDateTime currentTime = LocalDateTime.now();
		
		try {		
			cmd = args[0];
			if (args.length < 2 && cmd.equals("summary"))
				taskName = null;
			else 
				taskName = args[1];
			if (args[0].equals("describe")) {
					description = args[2];
			}
			else 
				description = null;		
		}//end Try
		catch (ArrayIndexOutOfBoundsException ex) {
			System.out.println("Usage:");
			System.out.println("\tTM start <task name>");
			System.out.println("\tTM stop <task name>");
			System.out.println("\tTM describe <task name> <task description in quotes>");
			System.out.println("\tTM summary <task name>");
			System.out.println("\tTM summary");
			return;
		}//end catch 
		
// switch for different operations
		switch (cmd) {
			case "stop": cmdStop(taskName, log, cmd, currentTime);
						break;
			case "start": cmdStart(taskName, log, cmd, currentTime);
						break;
			case "describe": cmdDescribe(taskName, log, cmd, currentTime, description);
						break;
			case "summary": if (taskName == null)
								cmdSummary(log);
							else
								cmdSummary(log, taskName);
						break;
		}//end switch
		
	}//end appDriver
	
	

	void cmdStart(String taskName, TaskLog log, String cmd, LocalDateTime currentTime) throws IOException {
		String line = (currentTime + "_" + taskName + "_" + cmd);
		log.writeLine(line);
	}//end cmdStart
	

	void cmdStop(String taskName, TaskLog log, String cmd, LocalDateTime currentTime) throws IOException {
		String line = (currentTime + "_" + taskName + "_" + cmd);
		log.writeLine(line);
	}//end cmdStop
	
	void cmdDescribe(String taskName, TaskLog log, String cmd, LocalDateTime currentTime, String description) throws IOException {
		String line = (currentTime + "_" + taskName + "_" + cmd + "_" + description);
		log.writeLine(line);
	}//end cmdDescribe


//Summary for all tasks.
	void cmdSummary(TaskLog log) throws FileNotFoundException {
		
		// Read log file, gather entries
		LinkedList<TaskLogEntry> allLines = log.readFile();
		
		// Gather all task names from log entries, store in Tree Set. Tree set will sort tasks and prevent duplicate summaries for a single task.
		TreeSet<String> names = new TreeSet<String>();
		long totalTime = 0;
		for (TaskLogEntry entry : allLines){
			names.add(entry.taskName);	
		}
		
		// Display each summary individually
		System.out.println("\n--------------------| TASK LOG |--------------------");
		for (String name : names) {
			totalTime += cmdSummary(log, name);
		}
		System.out.println("\n---------------------------------------------------- \nTotal time\t\t|" + TimeUtilities.secondsFormatter(totalTime));
	}//end cmdSummary
   
   
//summary for a single task
	long cmdSummary(TaskLog log, String task) throws FileNotFoundException {

		// Read log file, gather entries
		LinkedList<TaskLogEntry> allLines = log.readFile();
		
		// Create Task object based on task name, with entries from log
		Task taskToSummarize = new Task(task, allLines);
		
		// Display 
		System.out.println(taskToSummarize.toString());
		return taskToSummarize.totalTime;
		
	}//end cmdSummary
	
	
	
	public class TaskLog {
		
		// Create FileWriter and PrintWriter to write to log file
		public FileWriter fwriter;
		public PrintWriter outputFile;
		
		
		/**
		 * Constructor, creates file I/O objects
		 */
		public TaskLog() throws IOException{
			fwriter = new FileWriter("TM.txt", true);
			outputFile = new PrintWriter(fwriter);
		}
		

		void writeLine(String line) throws IOException{
			outputFile.println(line);
			outputFile.close();
		}//end writeLine		
		
      
		LinkedList<TaskLogEntry> readFile() throws FileNotFoundException {
			
			// Create LinkedList of TaskLogEntry objects to hold each entry in log file
			LinkedList<TaskLogEntry> lineList = new LinkedList<TaskLogEntry>();
			
			// Open file for reading
			File logFile = new File("TM.txt");
			Scanner inputFile = new Scanner(logFile);
			
			// Process each line in the string
			String inLine;
			while(inputFile.hasNextLine()) {
				TaskLogEntry entry = new TaskLogEntry();
				inLine = inputFile.nextLine();
				StringTokenizer st = new StringTokenizer(inLine, "_");
				entry.timeStamp = LocalDateTime.parse(st.nextToken());
				entry.taskName = st.nextToken();
				entry.cmd = st.nextToken();
				// If there are more than 3 tokens, 4th token is description
				if (st.hasMoreTokens())
					entry.description = st.nextToken();
				
				// Add entry to LinkedList
				lineList.add(entry);
			}
			
			// Close Scanner and return LinkedList
			inputFile.close();
			return lineList;
		}//end LinkedList<TaskLogEntry>
	}//end taskLog
	
//simple data container class used to hold the values necesary for a log entry
	class TaskLogEntry {
		String cmd;
		String taskName;
		String description;
		LocalDateTime timeStamp;
		
	}
	
	// The TimeUtilities class contains utilities for working with time related data, borrowed from Lecture Help
	static class TimeUtilities {
		
		 static String secondsFormatter(long secondsToFormat) {
			
			long hours = (secondsToFormat / 3600);
			long minutes = ((secondsToFormat % 3600) / 60);
			long seconds = (secondsToFormat % 60);
			String formattedTime = (String.format("%02d:%02d:%02d", hours, minutes, seconds));
			return formattedTime;
			
		}
	}//end TimeUtilities
	
	class Task {
		// Each task can be identified by name
		private String name;
		private String description;
		private String formattedTime = null;
		private long totalTime = 0;
		
//collects all data from a given task using our linked list
		public Task(String name, LinkedList<TaskLogEntry> entries) {
			this.name = name;
			this.description = null;
			LocalDateTime lastStart = null;
			long timeElapsed = 0;
			
			
			for (TaskLogEntry entry : entries){
				if (entry.taskName.equals(name)) {
					switch (entry.cmd) {
					case "start":
						lastStart = entry.timeStamp;
						break;
					case "stop":
						if (lastStart != null) 
							timeElapsed += calculateDuration(lastStart, entry.timeStamp);
						lastStart = null;
						break;
					case "describe": 
						description = entry.description;
					}
				}
			}
			// Format the elapsed time, parse to String, store long time for cmdSummary
			this.formattedTime = TimeUtilities.secondsFormatter(timeElapsed);
			this.totalTime = timeElapsed;
		}//end Task Constructor
		
		public String toString() {
			String str = ("\nSummary for task:\t| " + this.name + "\nDescription:\t\t| " + this.description + "\nDuration\t\t| " + this.formattedTime);
			return str;
		}
		
		long calculateDuration(LocalDateTime startTime, LocalDateTime stopTime) {
			long duration = ChronoUnit.SECONDS.between(startTime, stopTime);
			return duration;
		}
	}//end Task Class
}//end TM

//-If multiple descriptions are entered, only most recent is used




