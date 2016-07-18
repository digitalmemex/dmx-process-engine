package com.digitalmemex.process.engine;

import de.deepamehta.core.JSONEnabled;
import org.codehaus.jettison.json.JSONObject;

public class ProcessInfo implements JSONEnabled {

    private String name;

    private String description;

    private int version;

    private String key;

    private String resource;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    @Override
    public JSONObject toJSON() {
        try {
            JSONObject i = new JSONObject();
            i.put("name", this.name);
            i.put("description", this.description);
            i.put("version", this.version);
            i.put("key", this.key);
            i.put("resource", this.resource);
            return i;
        } catch (Exception e) {
            throw new RuntimeException("Serialization failed (" + this + ")", e);
        }
    }
}
