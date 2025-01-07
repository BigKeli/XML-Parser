import java.util.Map;

public interface Callback {
    void onElementStart(String element, String path, Map<String, String> attributes);
    void onText(String path, String text);
    void onElementEnd(String element, String path);
    void onError(String errorMessage);
}
