package sirs.carserver.model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Config implements Serializable {

    //ac
    private int out1;
    private int out2;

    //seat
    private int pos1;
    private int pos3;

    public Config() {
        this.out1 = 0;
        this.out2 = 0;
        this.pos1 = 0;
        this.pos3 = 0;
    }

    // Method to convert to JSONObject
    public JsonObject toJsonObject() {
        Gson gson = new Gson();

        Map<String, String> acMap = new HashMap<>();
        acMap.put("out1", String.valueOf(out1));
        acMap.put("out2", String.valueOf(out2));

        // Create the "seat" section
        Map<String, String> seatMap = new HashMap<>();
        seatMap.put("pos1", String.valueOf(pos1));
        seatMap.put("pos3", String.valueOf(pos3));

        // Wrap sections in their arrays
        JsonObject acArray = new JsonObject();
        acMap.forEach(acArray::addProperty);

        JsonObject seatArray = new JsonObject();
        seatMap.forEach(seatArray::addProperty);

        // Wrap everything in the "configuration" object
        JsonObject configuration = new JsonObject();
        configuration.add("ac", gson.toJsonTree(acMap.entrySet()));
        configuration.add("seat", gson.toJsonTree(seatMap.entrySet()));

        // Wrap in the main object
        JsonObject mainObject = new JsonObject();
        mainObject.add("configuration", configuration);

        return mainObject;
    }
}
