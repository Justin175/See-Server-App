package com.treulieb.worktimetool.data;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Bill {
    private String name;
    private BillUser creator;
    private String created;
    private Posten[] posten;
    private BillUser[] users;
    private float costsSum;

    public Bill(String name, BillUser creator, String created, Posten[] posten, BillUser[] users, float costsSum) {
        this.name = name;
        this.creator = creator;
        this.created = created;
        this.posten = posten;
        this.users = users;
        this.costsSum = costsSum;
    }

    public BillUser getUser(String name) {
        if(name.equals(creator.name))
            return creator;

        for(BillUser a : this.users)
            if(a.getName().equals(name))
                return a;
        return null;
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
        float costs = 0;

        for(Posten a : this.posten){
            if(a.getId().equals(id))
                continue;

            nPosten[index++] = a;
            costs += a.getCosts();
        }

        this.posten = nPosten;
        this.costsSum = costs;
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

    public float getCostsSum() {
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
