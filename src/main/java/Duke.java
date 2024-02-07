import helperpackage.Deadline;
import helperpackage.DukeException;
import helperpackage.Event;
import helperpackage.Task;
import helperpackage.ToDo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Duke {
    public static void main(String[] args) {
        String logo = " ____        _        \n"
                + "|  _ \\ _   _| | _____ \n"
                + "| | | | | | | |/ / _ \\\n"
                + "| |_| | |_| |   <  __/\n"
                + "|____/ \\__,_|_|\\_\\___|\n";
        System.out.println("Hello from\n" + logo);
        System.out.println("________________________________________");

        System.out.println("Hello! I'm NextGenerationJarvis.");
        System.out.println("What can I do for you?");
        System.out.println("________________________________________\n");

        /** Used to store Tasks */
        LinkedList<Task> taskList = new LinkedList<>();

        // Read file
        File file = new File("./data/duke.txt");
        int currentAttempt = 0;
        int maxAttempt = 2;

        while (++currentAttempt <= maxAttempt) {
            try {
                System.out.println("Startup Attempt #" + currentAttempt + "/" + maxAttempt + ":");
                if (file.createNewFile()) {
                    System.out.println("duke.txt does not exist.");
                    System.out.println("duke.txt successfully created.");
                } else {
                    System.out.println("duke.txt exist.");
                    System.out.println("duke.txt successfully loaded.");
                }
                break;
            } catch (IOException e) {
                System.out.println("IOException occured: " + e.getMessage());
                
                // creating directory 
                File dir = new File("./data");
                boolean isDirectoryCreated = dir.mkdir();
                if (isDirectoryCreated) {
                    System.out.println("Directory ./data created.");
                }
            } finally {
                System.out.println("\n________________________________________\n");
            }
        }

        // Reading inputs from the file
        // e.g. E | 1 | resiDANCE | 6th Feb 8-10pm
        try {
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNext()) {
                String[] tasks = fileScanner.nextLine().split(" \\| ");
                String type = tasks[0];
                boolean isDone =  Integer.parseInt(tasks[1]) == 1 ? true : false;
                if (type.equals("T")) {
                    ToDo td = new ToDo(isDone, tasks[2]);
                    taskList.add(td);
                } else if (type.equals("D")) {
                    Deadline d = new Deadline(isDone, tasks[2], tasks[3]);
                    taskList.add(d);
                } else if (type.equals("E")) {
                    Event e = new Event(isDone, tasks[2], tasks[3]);
                    taskList.add(e);
                }
            }
            fileScanner.close();

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }


        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();


        // loop only exits if input is "bye"
        while (!userInput.toLowerCase().equals("bye")) {
            System.out.println("\n________________________________________");

            // Level-2: if the input is "list"
            if (userInput.toLowerCase().equals("list")) {
                System.out.println("Here are the tasks in your list:");

                for (int i = 1; i <= taskList.size(); i++) {
                    Task t = taskList.get(i - 1);
                    System.out.println(i + ". " + t.toString());
                }

            } else {
                StringTokenizer st = new StringTokenizer(userInput);
                String cmd = st.nextToken().toLowerCase();

                // Level-3: mark & unmark
                if (cmd.equals("mark") || (cmd.equals("unmark"))) {
                    try {
                        changeStatus(taskList, cmd, st);
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Task not found. :(");
    
                    } catch (NumberFormatException e) {
                        System.out.println("Input is not an integer. :(");
    
                    } catch (NoSuchElementException e) {
                        System.out.println("Missing task number. :(");
                    }
                    
                // Level-4: ToDo, Deadline, Event
                } else if (cmd.equals("todo") || cmd.equals("event") || cmd.equals("deadline")) {
                    try {
                        addTask(taskList, cmd, userInput);

                    } catch (DukeException e) {
                        System.out.println(e.getMessage());

                    } catch (StringIndexOutOfBoundsException e) {
                        System.out.println("Invalid input. :(");
                    }

                // Level-6: Delete
                } else if (cmd.equals("delete")) {
                    try {
                        delete(taskList, cmd, st);
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Task not found. :(");
    
                    } catch (NumberFormatException e) {
                        System.out.println("Input is not an integer. :(");
    
                    } catch (NoSuchElementException e) {
                        System.out.println("Missing task number. :(");
                    }

                // Level-5: Throw exception for other inputs
                } else {
                    try {
                        throw new DukeException("OOPS!! Pls try again. :)");
                    } catch (DukeException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
            System.out.println("________________________________________\n");
            userInput = scanner.nextLine();
        }
        
        // writing to file
        try {
            FileWriter fw = new FileWriter(file, false);
            for (Task t : taskList) {
                fw.write(t.toData());
                fw.write(System.lineSeparator());
            }
            fw.close();

        } catch (IOException e){
            System.out.println(e.getMessage());
        }

        System.out.println("\n________________________________________");
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("________________________________________\n");

        scanner.close();
    }

    // Level-3: mark & unmark
    public static void changeStatus(LinkedList<Task> taskList, String cmd, 
            StringTokenizer st) throws IndexOutOfBoundsException, 
            NumberFormatException, NoSuchElementException {
        
        int index = Integer.parseInt(st.nextToken());
        Task t = taskList.get(index - 1);

        if (cmd.equals("mark")) {
            t.markAsDone();
            System.out.println("Nice! I've marked this task as done:");
        
        } else if (cmd.equals("unmark")) {
            t.unmark();
            System.out.println("OK, I've marked this task as not done yet:");
        }

        System.out.println(" " + t.toString());
    }

    // Level-4: ToDo, Deadline, Event
    public static void addTask(LinkedList<Task> taskList, String cmd, 
            String userInput) throws DukeException {

        int firstSpaceIndex = userInput.indexOf(" ");
        String description = userInput.substring(firstSpaceIndex + 1);
        Task t = new Task(" ");

        if (cmd.equals("todo")) {
            description = description.strip();
            if (description.equals("") || firstSpaceIndex == -1) {
                throw new DukeException("Invalid todo input. :(");
            } else {
                t = new ToDo(description);
            }

        } else if (cmd.equals("deadline")) {
            t = new Deadline(description);

        } else if (cmd.equals("event")) {
            t = new Event(description);
        }

        taskList.add(t);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + t.toString());
        System.out.println("Now you have " + taskList.size() + " tasks in the list.");
    }

    // Level-6: Delete
    public static void delete (LinkedList<Task> taskList, String cmd, 
            StringTokenizer st) throws IndexOutOfBoundsException,
            NumberFormatException, NoSuchElementException {

        int index = Integer.parseInt(st.nextToken());
        Task t = taskList.remove(index - 1);

        System.out.println("Noted, I've removed this task:");
        System.out.println(" " + t.toString());
        System.out.println("Now you have " + taskList.size() + " tasks in the list.");
    }
}
