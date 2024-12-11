package sirs.motorist.prototype.lib.model;

import com.google.gson.JsonObject;

public record Nonce (String base64Random, long timestamp) {
    private final static String TIMESTAMP_PROPERTY = "timestamp";
    private final static String BASE64_PROPERTY = "base64Random";

    /**
     * Converts the Nonce object to a JsonObject
     * @return JsonObject with the Nonce object
     */
    public JsonObject toJsonObject() {
        //Make a nested Object for the nonce
        JsonObject nonceJson = new JsonObject();
        nonceJson.addProperty(BASE64_PROPERTY, base64Random);
        nonceJson.addProperty(TIMESTAMP_PROPERTY, timestamp);
        return nonceJson;
    }

    public byte[] toByteArray() {
        return (this.base64Random + this.timestamp).getBytes();
    }

    @Override
    public String toString() {
        return "Nonce{" +
                "base64Random='" + base64Random + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
