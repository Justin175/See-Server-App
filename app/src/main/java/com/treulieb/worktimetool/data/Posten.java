package com.treulieb.worktimetool.data;

import com.treulieb.worktimetool.JSONUtils;
import com.treulieb.worktimetool.utils.MathUtils;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.treulieb.worktimetool.utils.MathUtils.decimal;

public class Posten {
    private String id;
    private String title;
    private BigDecimal costs;
    private String info;
    private String creator;
    private String created;
    private String[] to;

    public Posten(String id, String title, BigDecimal costs, String info, String creator, String created, String[] to) {
        this.id = id;
        this.title = title;
        this.costs = costs;
        this.info = info;
        this.creator = creator;
        this.created = created;
        this.to = to;
    }

    public boolean isForAllUsers() {
        if(to.length == 0) return true;
        if(to[0].equals("$")) return true;

        return false;
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

    public BigDecimal getCosts() {
        return costs;
    }

    public BigDecimal getPartialCosts(int part) {
        return getPartialCosts(decimal(part));
    }

    public BigDecimal getPartialCosts(BigDecimal part) {
        return costs.divide(part, MathUtils.ROUNDING_MODE);
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

    public boolean containsTo(String user) {
        if(isForAllUsers())
            return true;

        for(String to : this.getTo())
            if(to.equals(user))
                return true;

        return false;
    }

    public static Posten fromJSON(String id, JSONObject postenData) {
        JSONObject data = postenData.optJSONObject(id);

        return new Posten(
                id,
                data.optString("title"),
                decimal(data.optString("costs")),
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
                decimal(posten.optString("costs")),
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
