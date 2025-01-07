import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // ask the user for the file path example.xml included here for ease
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the full path of the XML file:");
        String filePath = scanner.nextLine();

        try {
            // Read the XML content
            String xml = Files.readString(Paths.get(filePath));

            //  start the parsing process
            Controller controller = new Controller();
            controller.processXml(xml);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}

