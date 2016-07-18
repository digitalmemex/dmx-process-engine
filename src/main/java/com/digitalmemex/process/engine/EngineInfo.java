package com.digitalmemex.process.engine;

import de.deepamehta.core.JSONEnabled;
import org.codehaus.jettison.json.JSONObject;

public class EngineInfo implements JSONEnabled {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public JSONObject toJSON() {
        try {
            JSONObject i = new JSONObject();
            i.put("name", this.name);
            return i;
        } catch (Exception e) {
            throw new RuntimeException("Serialization failed (" + this + ")", e);
        }
    }
}
