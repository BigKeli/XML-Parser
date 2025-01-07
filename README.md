# **XML Forward-Only Parser Documentation**

## **Project Overview**
This project implements a **forward-only XML parser** that reads an XML document **character by character** and reports XML structure and content as it is parsed. The parser does not allow backward traversal and processes data efficiently by using a **state machine** to track the current parsing state.

Key components of the project include:
- **Main Class:** The entry point for the application.
- **Controller Class:** Handles user logic and receives events from the service.
- **Service Class:** Contains the core logic for parsing the XML and invokes callbacks for events.
- **Callback Interface:** Facilitates communication between the service and controller by notifying the controller of parsing events (e.g., element start, text content, escape characters, errors).

---

## **Classes and Responsibilities**

### **1. Main Class (`Main.java`)**
The `Main` class initializes the application and provides an XML input for parsing. It creates an instance of the `Controller` class and calls the method to start processing the XML.

**Responsibilities:**
- Acts as the starting point of the application.
- Provides an XML string or file to be parsed by the `Controller`.

---

### **2. Controller Class (`Controller.java`)**
The `Controller` class serves as the intermediary between the main application and the `Service` class. It implements the `Callback` interface and defines what should happen when XML events occur (e.g., printing element paths, filtering based on `<amount>`).

**Responsibilities:**
- Interacts with the `Service` to initiate XML parsing.
- Implements the `Callback` interface to handle events such as:
    - **Start of an element**: Reports the element name, path, and attributes.
    - **Text content**: Handles text inside an element, with additional logic for filtering.
    - **End of an element**: Reports the element and its path.
    - **Error handling**: Logs errors during parsing.

---

### **3. Service Class (`Service.java`)**
The `Service` class contains the **core XML parsing logic**. It reads the XML character by character using a **state machine** and invokes the appropriate `Callback` methods for different events.

**Responsibilities:**
- Implements a state machine for parsing:
    - **`<`**: Start of a tag.
    - **`>`**: End of a tag.
    - **`;`**: Handle escape sequences.
    - Default: Collect content between tags.
- Tracks the current XML path using a stack (`pathStack`).
- Extracts element names and attributes using helper methods like `parseElementName()` and `parseAttributes()`.

## **State Machine Logic Overview**

The state machine in this XML parser operates by examining each character in the XML input stream and taking appropriate actions based on its context. The following describes the states and transitions:

### **1. Detecting the Start of an XML Tag (`<`)**
When the state machine encounters a `<`, it recognizes the beginning of a tag.
- If there is any accumulated text before this `<`, it is considered as the text content of the current element.
- This text is reported using the `onText` callback, and the buffer is cleared for the next tag.

### **2. Identifying an End Tag (`</`) or Start Tag (`<tag>`)**
After accumulating characters between `<` and `>`, the state machine determines whether:
- The tag starts with `</`: This represents an **end tag** (e.g., `</amount>`). The corresponding element is removed from the path stack, and the `onElementEnd` callback is triggered.
- The tag does not start with `</`: This is a **start tag** (e.g., `<order id="1111">`). The element name and attributes are parsed, added to the path stack, and reported via the `onElementStart` callback.

### **3. Handling Escape Sequences (`&...;`)**
If the state machine encounters a `&`, it begins collecting the escape sequence until it finds a `;`.
- After collecting the full sequence (e.g., `&quot;`), the parser looks up the corresponding replacement character (`"`).
- If the sequence is valid, it is replaced in the buffer. If invalid, it logs an error or appends the unrecognized content.

### **4. Collecting Text Content**
When the state machine reads characters outside of a tag (between `>` and `<`), it treats them as text content. These characters are accumulated in the buffer until another `<` is encountered, at which point the text is reported and the buffer is cleared.

