package pt.tecnico.sirs.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.tecnico.sirs.model.Nonce;

public class JSONUtil {

    private static final Logger logger = LoggerFactory.getLogger(JSONUtil.class);

    // TODO: Added prettyPrinting --> GsonBuilder().setPrettyPrinting().create();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Converts an object to a byte array using JSON serialization
     * @param object Object to be converted
     * @return Byte array with the object
     */
    public static byte[] toByteArray(Object object) {
        String jsonString = gson.toJson(object);
        return jsonString.getBytes(); // Default to UTF-8 encoding
    }

    /**
     * Converts a JSON string to a JsonObject
     * @param jsonString JSON string to be converted
     * @return JsonObject with the JSON string
     */
    public static JsonObject parseJson(String jsonString) {
        return gson.fromJson(jsonString, JsonObject.class);
    }

    /**
     * Converts a JsonObject to a JSON string
     * @param jsonObject JsonObject to be converted
     * @return JSON string with the JsonObject
     */
    public static String toJsonString(JsonObject jsonObject) {
        return gson.toJson(jsonObject);
    }

    public static <T> T parseJsonToClass(JsonObject jsonObject, Class<T> classOfT) {
        return gson.fromJson(jsonObject, classOfT);
    }

    public static <T> T parseJsonToClass(String jsonString, Class<T> classOfT) {
        return gson.fromJson(jsonString, classOfT);
    }

    public static <T> String parseClassToJsonString(T object) {
        return gson.toJson(object);
    }
}
