import java.io.IOException;
import java.util.Set;

/*delete this later
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
         }*/

public class TM {
	public static void main(String[] args) throws IOException {
		
		ITMModel tmModel = new TMModel();
		String cmd, Entry, taskName;
		
		try {
			cmd = args[0];
			if (args.length < 2)
				taskName = null;
			else 
				taskName = args[1];
			if (args[0].equals("describe") || args[0].equals("size") || args[0].equals("rename")) {
				Entry = args[2];				
			}
			else 
				Entry = null;		
		}
		catch (ArrayIndexOutOfBoundsException ex) {
		   System.out.println("Usage:");
			System.out.println("Usage:");
			System.out.println("\tTM start <task name>");
			System.out.println("\tTM stop <task name>");
			System.out.println("\tTM describe <task name> <task description in quotes>");
			System.out.println("\tTM size <task name> <task size XS-XXL>");
			System.out.println("\tTM delete <task name>");
			System.out.println("\tTM rename <task name> <new name>");
			System.out.println("\tTM summary <task name>");
			System.out.println("\tTM summary");
			return;
		}

		switch (cmd) {
			case "stop": tmModel.stopTask(taskName);
						break;
			case "start": tmModel.startTask(taskName);
						break;
			case "describe": tmModel.describeTask(taskName, Entry);
						break;
			case "size": tmModel.sizeTask(taskName, Entry);
						break;
			case "delete": tmModel.deleteTask(taskName);
						break;
			case "rename": tmModel.renameTask(taskName, Entry);
						break;			
			case "summary": if (taskName == null)
								summary(tmModel);
							else {
								summary(tmModel, taskName);
								break;
							}	
		}
	}
	
	public static void summary(ITMModel tmModel, String taskName) {
		String str = ("\nSummary for task:\t| " + taskName +
				"\nSize:\t\t\t| " + tmModel.taskSize(taskName) +
				"\nDuration\t\t| " + tmModel.taskElapsedTime(taskName) +
				"\nDescription:\n" + tmModel.taskDescription(taskName) + "\n");
		System.out.println(str);
	}
	
	public static void summary(ITMModel tmModel) {
		Set<String> taskNames = tmModel.taskNames();
		Set<String> taskSizes = tmModel.taskSizes();
		System.out.println("\n###############################| TASK LOG |#################################");
		for (String name : taskNames) {
			// Ignores deleted tasks
			if (name.contains("<REDACTED>"))
				continue;
			
			summary(tmModel, name);
		}
		System.out.println("-----------------------------------");
		String str = ("Total Time:\t\t| " + tmModel.elapsedTimeForAllTasks() +
					"\n");
		System.out.println(str);
		System.out.println("\n SUMMARY FOR TASKS WITH SIZES");
		for (String size : taskSizes) {
			Set<String> taskNamesForSize = tmModel.taskNamesForSize(size);
			if (taskNamesForSize.size() > 1) {
				System.out.println("Size:\t\t\t| " + size);
				System.out.println("Task Names:\t\t| " + taskNamesForSize);
				System.out.println("Min time:\t\t| " + tmModel.minTimeForSize(size));
				System.out.println("Avg time:\t\t| " + tmModel.avgTimeForSize(size));
				System.out.println("Max time:\t\t| " + tmModel.maxTimeForSize(size) + "\n");
			}
		}
	}
}

/*import java.io.IOException;
import java.util.Set;


public class TM {
	public static void main(String[] args) throws IOException {
		
		ITMModel tmModel = new TMModel();
		String cmd;
		String taskName;
		String Entry;
		
		try {
			cmd = args[0];
			if (args.length < 2)
				taskName = null;
			else 
				taskName = args[1];
			if (args[0].equals("describe") || args[0].equals("size")) {
				Entry = args[2];				
			}
			else 
				Entry = null;		
		}
		catch (ArrayIndexOutOfBoundsException ex) {
		
			System.out.println("Usage:");
			System.out.println("\tTM start <task name>");
			System.out.println("\tTM stop <task name>");
			System.out.println("\tTM describe <task name> <task description in quotes>");
			System.out.println("\tTM size <task name> <task size XS-XXL>");
			System.out.println("\tTM delete <task name>");
			System.out.println("\tTM rename <task name> <new name>");
			System.out.println("\tTM summary <task name>");
			System.out.println("\tTM summary");
			return;
		}
		
		// switch to direct operation
		switch (cmd) {
			case "stop": tmModel.stopTask(taskName);
						break;
			case "start": tmModel.startTask(taskName);
						break;
			case "describe": tmModel.describeTask(taskName, Entry);
						break;
			case "size": tmModel.sizeTask(taskName, Entry);
						break;
			case "delete": tmModel.deleteTask(taskName);
						break;
			case "rename": tmModel.renameTask(taskName, Entry);
						break;			
			case "summary": if (taskName == null)
								summary(tmModel);
							else {
								summary(tmModel, taskName);
								break;
							}	
		}
	}
	
	public static void summary(ITMModel tmModel, String taskName) {
		String str = ("\nSummary for task:\t| " + taskName +
				"\nSize:\t\t\t| " + tmModel.taskSize(taskName) +
				"\nDuration\t\t| " + tmModel.taskElapsedTime(taskName) +
				"\nDescription:\n" + tmModel.taskDescription(taskName) );
		System.out.println(str);
	}
	
	public static void summary(ITMModel tmModel) {
		Set<String> taskNames = tmModel.taskNames();
		Set<String> taskSizes = tmModel.taskSizes();
		//Set<String> namesForSizes = tmModel.taskNamesForSize(size); 
		for (String name : taskNames) {
			summary(tmModel, name);
		}
		String str = ("\nTotal Time:\t\t|" + tmModel.elapsedTimeForAllTasks() +
					"\n");
		System.out.println(str);
	}
}

/*static void CommandDelete(String taskName)
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
         }//end rename
*/   
 /*
      
       }
      log.writeLine(line);
         
	}*/
