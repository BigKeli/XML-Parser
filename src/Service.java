import java.io.*;
import java.util.*;

public class Service {
    private Stack<String> pathStack; // path stack
    private static final Map<String, String> escapeMap = Map.of(
            "&quot", "\"",   // "
            "&amp", "&",     // &
            "&apos", "'",    // '
            "&lt", "<",      // <
            "&gt", ">"       // >
    ); // mapping escape characters as final
    public Service() {
        this.pathStack = new Stack<>();
    }

    public void parseXml(String xml, Callback callback) {
        try (BufferedReader reader = new BufferedReader(new StringReader(xml))) {
            StringBuilder buffer = new StringBuilder(); //
            int currentChar;

            while ((currentChar = reader.read()) != -1) {
//                if (currentChar == 32) {
//                    continue;  // Skip the rest of the loop when encountering a space
//                }

                char c = (char) currentChar;
                processChar(c, buffer, callback);   // dependency injection with buffer to processChar()
            }

        } catch (IOException e) {
            callback.onError("Error reading XML: " + e.getMessage());
        }
    }

    private void processChar(char c, StringBuilder buffer, Callback callback) {
        // Simple state machine logic start tag
        if (c == '<') { // Starting tag
            if (buffer.length() > 0) { // Handle content inside tag before continuing with other tag
                callback.onText(pathStackToString(), buffer.toString().trim());
                buffer.setLength(0);
            }
            buffer.append(c);
        }
        else if (c == '>') { // End of a tag
            buffer.append(c);
            String tag = buffer.toString();
            buffer.setLength(0);

            if (tag.startsWith("</")) { // Closing tag
                String element = tag.substring(2, tag.length() - 1);
                pathStack.pop(); // Pop the element from the stack
                callback.onElementEnd(element, pathStackToString());
            } else { // Opening tag
                String element = parseElementName(tag);
                pathStack.push(element);
                Map<String, String> attributes = parseAttributes(tag);
                callback.onElementStart(element, pathStackToString(), attributes);
            }
        }
        else if (c == ';') {// Dealing with escape characters

            try {
                int startIndex = buffer.indexOf("&");
                if (startIndex != -1) {
                    // Save from '&' to the end of the buffer
                    String escapedSequence = buffer.substring(startIndex, buffer.length());
                    System.out.println(escapedSequence);

                    // Check the escape map for a match
                    String replacement = escapeMap.get(escapedSequence);
                        if (replacement != null) {
                            buffer.replace(startIndex,buffer.length(),replacement);

                        } else {
                            System.out.println("Unknown escape sequence: " + escapedSequence);
                        }

                } else {
                    // If '&' is not found, appends the character to the buffer
                    buffer.append(c);


                }
            } catch (Exception e) {
                System.out.println("Error processing escape sequence: " + e.getMessage());
            }



        }
        else {
            buffer.append(c); // Collect content
        }
    }

    private String parseElementName(String tag) {
        int spaceIndex = tag.indexOf(' ');
        if (spaceIndex == -1) {
            return tag.substring(1, tag.length() - 1); // no attributes
        }
        return tag.substring(1, spaceIndex);
    }

    private Map<String, String> parseAttributes(String tag) {
        Map<String, String> attributes = new HashMap<>();
        int spaceIndex = tag.indexOf(' ');
        if (spaceIndex == -1) return attributes; // no attributes

         String[] parts = tag.substring(spaceIndex + 1, tag.length() - 1).split(" ");
        for (String part : parts) {
            String[] keyValue = part.split("=");
            if (keyValue.length == 2) {
                attributes.put(keyValue[0], keyValue[1].replace("\"", ""));
            }
        }
        return attributes;
    }

    private String pathStackToString() {
        return String.join("/", pathStack);
    }
}
