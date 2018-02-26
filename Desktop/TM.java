import java.io.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class TM {
	public static void main(String [] args) throws IOException {
		TM tm = new TM();
		tm.appMain(args);
	}
	
	void appMain(String [] args) throws IOException {
		
		String Command;
		String taskName;
		String Data;
      String Entry = null;
		
		TaskLog log = new TaskLog();
		
		LocalDateTime currentTime = LocalDateTime.now();
		
		try {		
			Command = args[0];
			if (args.length < 2)
				taskName = null;
			else 
				taskName = args[1];
			if (args[0].equals("describe") || args[0].equals("size")) {
				Data = args[2];
				if (args.length == 4)
					Entry = args[3];				
			}
			else 
				Data = null;		
		}
		catch (ArrayIndexOutOfBoundsException ex) {
			System.out.println("Usage:");
			System.out.println("\tTM start <task name>");
			System.out.println("\tTM stop <task name>");
			System.out.println("\tTM describe <task name> <task description in quotes> <(optional)task size XS-XXL>");
			System.out.println("\tTM size <task name> <task size XS-XXL>");
			System.out.println("\tTM summary <task name>");
			System.out.println("\tTM summary");
			return;
		}
		
		switch (Command) {
			case "stop": CommandStop(taskName, log, Command, currentTime);
						break;
			case "start": CommandStart(taskName, log, Command, currentTime);
						break;
			case "describe": CommandDescribe(taskName, log, Command, currentTime, Data, Entry);
						break;
			case "size": CommandDescribe(taskName, log, Command, currentTime, Data, Entry);
						break;
         case "delete": CommandDelete(taskName);
						break;
			case "summary": if (taskName == null)
								CommandSummary(log);
							else
								CommandSummary(log, taskName);
						break;
		}
		
	}
	
	void CommandStart(String taskName, TaskLog log, String Command, LocalDateTime currentTime) throws IOException {
		String line = (currentTime + "_" + taskName + "_" + Command);
		log.writeLine(line);
	}
	
	void CommandStop(String taskName, TaskLog log, String Command, LocalDateTime currentTime) throws IOException {
		String line = (currentTime + "_" + taskName + "_" + Command);
		log.writeLine(line);
	}
	
	void CommandDescribe(String taskName, TaskLog log, String Command, LocalDateTime currentTime, String Data, String Entry) throws IOException {
		String line;
		if(Entry != null)
			line = (currentTime + "_" + taskName + "_" + Command + "_" + Data + "_" + Entry);
		else
			line = (currentTime + "_" + taskName + "_" + Command + "_" + Data);
		log.writeLine(line);
	}
   static void CommandDelete(String taskName)
    {
        File fileToBeModified = new File("TM.txt");
        String newString = "<REDACTED>";
        String oldContent = "";
        BufferedReader reader = null;       
        FileWriter writer = null;
        try
        {
            reader = new BufferedReader(new FileReader(fileToBeModified));
            //Reading all the lines of input text file into oldContent
            String line = reader.readLine();
            while (line != null) 
            {
                oldContent = oldContent + line + System.lineSeparator();
                line = reader.readLine();
            }
            //Replacing taskName with newString in the oldContent
            String newContent = oldContent.replaceAll(taskName, newString);
            //Rewriting the input text file with newContent
            writer = new FileWriter(fileToBeModified);
            writer.write(newContent);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                //Closing the resources
                reader.close();
                writer.close();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
         }   
}//end delete
   /*void CommandDelete(String taskName, TaskLog log, String Command, LocalDateTime currentTime, String Data, String Entry) throws IOException {
		String line;
	   line = (currentTime + "_" + taskName + "_" + Command + "_" + Data + "_" + Entry);
		log.writeLine(line);
	}*/
	
	void CommandSummary(TaskLog log) throws FileNotFoundException {

		LinkedList<TaskLogEntry> allLines = log.readFile();
		TreeSet<String> names = new TreeSet<String>();
		long totalTime = 0;
		for (TaskLogEntry entry : allLines){
			names.add(entry.taskName);	
		}
		System.out.println("\n###########[TASK LOG]##############");
		for (String name : names) {
			totalTime += CommandSummary(log, name);
		}
		System.out.println("\n\nTotal time: \t\t|" + TimeUtilities.secondsFormatter(totalTime));
	}
	
	long CommandSummary(TaskLog log, String task) throws FileNotFoundException {

		LinkedList<TaskLogEntry> allLines = log.readFile();
		Task taskToSummarize = new Task(task, allLines);
		System.out.println(taskToSummarize.toString());
      
		return taskToSummarize.totalTime;
      /*
      #####NOT SCALEABLE... WELL IT WORKED OK BUT WAS REALLY CLUNKY
      +   void CommandSummary(String taskName, TaskLog log, LocalDateTime currentTime) throws IOException {
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
         
	}*/
		
	}
	
	
	
	public class TaskLog {
		public FileWriter fwriter;
		public PrintWriter outputFile;
		
		public TaskLog() throws IOException{
			fwriter = new FileWriter("TM.txt", true);
			outputFile = new PrintWriter(fwriter);
		}
		
		void writeLine(String line) throws IOException{
			outputFile.println(line);
			outputFile.close();
		}		
		
		LinkedList<TaskLogEntry> readFile() throws FileNotFoundException {

			LinkedList<TaskLogEntry> lineList = new LinkedList<TaskLogEntry>();
			
			// Open file for reading
			File logFile = new File("TM.txt");
			Scanner inputFile = new Scanner(logFile);
			
			String inLine;
			while(inputFile.hasNextLine()) {
				TaskLogEntry entry = new TaskLogEntry();
				inLine = inputFile.nextLine();
				StringTokenizer st = new StringTokenizer(inLine, "_");
				entry.timeStamp = LocalDateTime.parse(st.nextToken());
				entry.taskName = st.nextToken();
				entry.Command = st.nextToken();
				// If Command is describe, data is description, if Command is size, data is size.
				if (st.hasMoreTokens())
					entry.Data = st.nextToken();
				if (st.hasMoreTokens())
					entry.Entry = st.nextToken();
				lineList.add(entry);
			}
         //close the file
			inputFile.close();
			return lineList;
		}
	}
	
	class TaskLogEntry {
		String Command;
		String taskName;
		String Data;
		String Entry;
		LocalDateTime timeStamp;
		
	}
	
	static class TimeUtilities {
		
		 static String secondsFormatter(long secondsToFormat) {
			
			long hours = (secondsToFormat / 3600);
			long minutes = ((secondsToFormat % 3600) / 60);
			long seconds = (secondsToFormat % 60);
			String formattedTime = (String.format("%02d:%02d:%02d", hours, minutes, seconds));
			return formattedTime;
			
		}
	}
	
	class Task {
		private String name;
		private StringBuilder description = new StringBuilder("");
		private String taskSize;
		private String formattedTime = null;
		private long totalTime = 0;
		
		public Task(String name, LinkedList<TaskLogEntry> entries) {
			this.name = name;
			LocalDateTime lastStart = null;
			long timeElapsed = 0;
			
			
			for (TaskLogEntry entry : entries){
				if (entry.taskName.equals(name)) {
					switch (entry.Command) {
					case "start":
						lastStart = entry.timeStamp;
						break;
					case "stop":
						if (lastStart != null) 
							timeElapsed += calculateDuration(lastStart, entry.timeStamp);
						lastStart = null;
						break;
					case "describe": 
						if (description.toString().equals(""))
							description.append(entry.Data);
						else
							description.append("\n\t\t\t| " + entry.Data);
						
						if (entry.Entry != null)
							taskSize = entry.Entry;
						break;
					case "size": 
						taskSize = entry.Data;
					}
				}
			}
			// Format the elapsed time, parse to String, store long time for CommandSummary
			this.formattedTime = TimeUtilities.secondsFormatter(timeElapsed);
			this.totalTime = timeElapsed;
		}
		
		public String toString() { 
			String str = ("\nSummary for task:\t| " + this.name + "\nDescription of task:\t| " + this.description + "\nSize:\t\t\t| " + this.taskSize + "\nDuration\t\t| " + this.formattedTime);
			return str;
		}
		
		long calculateDuration(LocalDateTime startTime, LocalDateTime stopTime) {
			long duration = ChronoUnit.SECONDS.between(startTime, stopTime);
			return duration;
		}
	}
}
