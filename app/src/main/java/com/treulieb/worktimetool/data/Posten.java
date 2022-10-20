package com.treulieb.worktimetool.data;

import com.treulieb.worktimetool.JSONUtils;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Posten {
    private String id;
    private String title;
    private float costs;
    private String info;
    private String creator;
    private String created;
    private String[] to;

    public Posten(String id, String title, float costs, String info, String creator, String created, String[] to) {
        this.id = id;
        this.title = title;
        this.costs = costs;
        this.info = info;
        this.creator = creator;
        this.created = created;
        this.to = to;
    }

    public String[] getTo() {
        return to;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public float getCosts() {
        return costs;
    }

    public String getInfo() {
        return info;
    }

    public String getCreator() {
        return creator;
    }

    public String getCreated() {
        return created;
    }

    public static Posten fromJSON(String id, JSONObject postenData) {
        JSONObject data = postenData.optJSONObject(id);

        return new Posten(
                id,
                data.optString("title"),
                (float) data.optDouble("costs"),
                data.optString("p_info"),
                data.optString("creator"),
                data.optString("created"),
                JSONUtils.toArray(data.optJSONArray("to"), null)
        );
    }

    public static Posten fromJSONSingle(JSONObject posten) {
        return new Posten(
                null,
                posten.optString("title"),
                (float) posten.optDouble("costs"),
                posten.optString("p_info"),
                posten.optString("creator"),
                posten.optString("created"),
                JSONUtils.toArray(posten.optJSONArray("to"), null)
        );
    }

    public static Posten[] fromJSON(JSONObject postenData) {
        Iterator<String> ids = postenData.keys();
        List<Posten> posten = new LinkedList();

        while(ids.hasNext()) {
            String id = ids.next();
            posten.add(fromJSON(id, postenData));
        }

        return posten.toArray(new Posten[posten.size()]);
    }
}