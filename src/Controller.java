import java.util.Map;

public class Controller {
    private final Service service;

    public Controller() {
        this.service = new Service();
    }

    public void processXml(String xml) {
        service.parseXml(xml, new Callback() {
            @Override
            public void onElementStart(String element, String path, Map<String, String> attributes) {
                System.out.println("Start Element: " + element + ", Path: " + path + ", Attributes: " + attributes);
            }

            @Override
            public void onText(String path, String text) {
                if( text.isEmpty()){ //emptiness
                     }
                else {
                    System.out.println("Text Content: \"" + text + "\", Path: " + path);
                    if (path.endsWith("/amount")) {
                        int amount = Integer.parseInt(text.trim());
                        if (amount > 100) {
                            System.out.println("Order with high amount found: " + amount);
                        }
                    }
                }
                }

            @Override
            public void onElementEnd(String element, String path) {
                System.out.println("End Element: " + element + ", Path: " + path);
            }

            @Override
            public void onError(String errorMessage) {
                System.err.println("Error: " + errorMessage);
            }
        });
    }
}
