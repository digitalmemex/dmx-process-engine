package com.digitalmemex.process.engine;

import de.deepamehta.core.JSONEnabled;
import org.codehaus.jettison.json.JSONObject;

public class InstanceInfo implements JSONEnabled {

    private String id;

    private String key;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public JSONObject toJSON() {
        try {
            JSONObject i = new JSONObject();
            i.put("id", this.id);
            i.put("key", this.key);
            return i;
        } catch (Exception e) {
            throw new RuntimeException("Serialization failed (" + this + ")", e);
        }
    }
}
