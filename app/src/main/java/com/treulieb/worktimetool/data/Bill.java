package com.treulieb.worktimetool.data;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.treulieb.worktimetool.utils.MathUtils.decimal;

public class Bill {
    private String name;
    private BillUser creator;
    private String created;
    private Posten[] posten;
    private BillUser[] users;
    private BigDecimal costsSum;

    public Bill(String name, BillUser creator, String created, Posten[] posten, BillUser[] users, BigDecimal costsSum) {
        this.name = name;
        this.creator = creator;
        this.created = created;
        this.posten = posten;
        this.users = users;
        this.costsSum = costsSum;
    }

    public List<BillUser> getAllUsers() {
        return getAllUsers(null);
    }

    public List<Posten> getPostenPaidedFrom(BillUser user) {
        return getPostenPaidedFrom(user.name);
    }

    public List<Posten> getPostenPaidedFrom(String user) {
        List<Posten> postens = new LinkedList<>();

        for(Posten a : posten)
            if(a.getCreator().equals(user))
                postens.add(a);

        return postens;
    }

    public List<Posten> getToPayPosten(BillUser user) {
        return getToPayPosten(user.name);
    }

    public List<Posten> getToPayPosten(String user) {
        List<Posten> postens = new LinkedList<>();

        for(Posten a : posten)
            if(a.containsTo(user))
                postens.add(a);

        return postens;
    }

    public List<BillUser> getAllUsers(String except) {
        if(except == null)
            except = "$";

        List<BillUser> users = new LinkedList<>();
        if(!creator.getName().equals(except))
            users.add(creator);

        for(BillUser a : this.users)
            if(!a.getName().equals(except))
                users.add(a);

        return users;
    }

    public BigDecimal getPart(Posten posten) {
        if(posten.isForAllUsers())
            return decimal(this.users.length + 1);

        return decimal(this.users.length);
    }

    public BillUser getUser(String name) {
        if(name.equals(creator.name))
            return creator;

        for(BillUser a : this.users)
            if(a.getName().equals(name))
                return a;
        return null;
    }

    public int getUsersCount() {
        return this.users.length + 1; // + 1 => creator
    }

    public Posten getPosten(String id) {
        for(Posten a : posten)
            if(a.getId().equals(id))
                return a;

        return null;
    }

    public void removePosten(String id){
        Posten[] nPosten = new Posten[this.posten.length];
        int index = 0;
        BigDecimal costs = decimal(0);

        for(Posten a : this.posten){
            if(a.getId().equals(id))
                continue;

            nPosten[index++] = a;
            costs = costs.add(a.getCosts());
        }

        this.posten = nPosten;
        this.costsSum = costs;
    }

    public void removeUser(int index) {
        BillUser[] users = new BillUser[this.users.length - 1];
        BillUser toRemove = this.users[index];

        int i = 0;
        for(BillUser a : this.users){
            if(a != toRemove)
                users[i] = a;
        }

        this.users = users;
    }

    public void addUser(BillUser user) {
        BillUser[] users = Arrays.copyOf(this.users, this.users.length + 1);
        users[users.length - 1] = user;
        this.users = users;
    }

    public BillUser getCreator() {
        return creator;
    }

    public String getCreated() {
        return created;
    }

    public String getName() {
        return name;
    }

    public Posten[] getPosten() {
        return posten;
    }

    public BillUser[] getUsers() {
        return users;
    }

    public BigDecimal getCostsSum() {
        return costsSum;
    }

    public static class BillUser {
        private String name;
        private BillPrivilege[] privileges;

        public BillUser(String name, BillPrivilege[] privileges) {
            this.name = name;
            this.privileges = privileges;
        }

        public String getName() {
            return name;
        }

        public BillPrivilege[] getPrivileges() {
            return privileges;
        }

        public boolean hasPrivilige(BillPrivilege priv) {
            for(BillPrivilege a : privileges)
                if(a == priv)
                    return true;

            return false;
        }

        public void setPrivileges(BillPrivilege[] privileges) {
            this.privileges = privileges;
        }

        public static BillUser fromJSON(String name, JSONObject userData) {
            return new BillUser(name, BillPrivilege.fromString(userData.optString(name)));
        }

        public static BillUser[] fromJSON(JSONObject userData) {
            Iterator<String> names = userData.keys();
            List<BillUser> users = new LinkedList();

            while(names.hasNext()) {
                String name = names.next();
                users.add(fromJSON(name, userData));
            }

            return users.toArray(new BillUser[users.size()]);
        }
    }

    public enum BillPrivilege {
        READ,
        WRITE,
        CONFIGURE,
        NONE;

        public static BillPrivilege fromChar(char chr) {
            switch (chr){
                case 'w': return WRITE;
                case 'r': return READ;
                case 'c': return CONFIGURE;
            }
            return NONE;
        }

        public static BillPrivilege[] fromString(String privsStr) {
            BillPrivilege[] privs = new BillPrivilege[privsStr.length()];
            char[] singlePrivs = privsStr.toCharArray();

            for(int i = 0; i < privsStr.length(); i++)
                privs[i] = fromChar(singlePrivs[i]);

            return privs;
        }

        public static String toString(BillPrivilege... privs) {
            char[] str = new char[privs.length];

            for(int i = 0; i < privs.length; i++) {
                switch (privs[i]) {
                    case WRITE:     str[i] = 'w'; break;
                    case READ:      str[i] = 'r'; break;
                    case CONFIGURE: str[i] = 'c'; break;
                }
            }

            return new String(str);
        }
    }
}
