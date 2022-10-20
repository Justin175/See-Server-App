package com.treulieb.worktimetool.req;

import android.app.Activity;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.android.volley.Response;
import com.treulieb.worktimetool.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;

public final class SeeServerRequests {

    private static volatile String URL = null;
    private static volatile String TOKEN = null;
    private static volatile String NAME = null;
    private static volatile Activity activity;

    public static void setURL(String url) {
        SeeServerRequests.URL = url;
    }
    public static void setActivity(Activity activity) { SeeServerRequests.activity = activity; }

    public static void getVersion(String url, SeeServerResponses.ResponseCallback<SeeServerResponses.ResponseVersion> listener, Response.ErrorListener errorListener) {
        HttpRequest.doGet(url + "/version", null, response -> {
            System.out.println(response);
            listener.onResponse(new SeeServerResponses.ResponseVersion(response));
        }, errorListener);
    }

    public static void logout(SeeServerResponses.ResponseCallback<SeeServerResponses.ResponseLogout> listener, Response.ErrorListener errorListener){
        JSONObject toPost = new JSONObject();
        try {
            toPost.put("name", NAME);
            toPost.put("token", TOKEN);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpRequest.doPost(URL + "/logout", toPost, response -> {
            setTOKEN(null);
            listener.onResponse(new SeeServerResponses.ResponseLogout(response));
        }, errorListener);
    }

    public static void getBills(SeeServerResponses.ResponseCallback<SeeServerResponses.ResponseGetBills> listener, Response.ErrorListener errorListener) {
        doDefaultGenericPost("getBills", listener, errorListener, SeeServerResponses.ResponseGetBills.class);
    }

    public static void login(String name, String password, SeeServerResponses.ResponseCallback<SeeServerResponses.ResponseLogin> listener, Response.ErrorListener errorListener) {
        JSONObject toPost = new JSONObject();
        try {
            toPost.put("name", name);
            toPost.put("password", password);
            System.out.println(toPost);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        doDefaultGenericPost("/login", response -> {
            SeeServerRequests.NAME = name;
            listener.onResponse(response);
        }, errorListener, toPost, SeeServerResponses.ResponseLogin.class);
    }

    public static void createUser(String name, String password, int level, SeeServerResponses.ResponseCallback<SeeServerResponses.ResponseCreateUser> listener, Response.ErrorListener errorListener) {
        JSONObject account = new JSONObject();
        JSONObject toPost = new JSONObject();
        try {
            account.put("name", name);
            account.put("password", password);
            account.put("level", level);

            toPost.put("account", account);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        doDefaultGenericPost("createAccount", listener, errorListener, toPost, SeeServerResponses.ResponseCreateUser.class);
    }

    public static void createBill(String billName, SeeServerResponses.ResponseCallback<SeeServerResponses.ResponseCreateBill> listener, Response.ErrorListener errorListener) {
        JSONObject toPost = new JSONObject();
        JSONObject bill = new JSONObject();

        try {
            bill.put("name", billName);
            toPost.put("bill", bill);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        doDefaultGenericPost("createBill", listener, errorListener, toPost, SeeServerResponses.ResponseCreateBill.class);
    }

    public static void getAccounts(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        doDefaultPost("getAccounts", listener, errorListener);
    }

    public static void getBill(String billName, SeeServerResponses.ResponseCallback<SeeServerResponses.ResponseGetBill> listener, Response.ErrorListener errorListener) {
        JSONObject toPost = new JSONObject();
        JSONObject bill = new JSONObject();

        try {
            bill.put("name", billName);
            toPost.put("bill", bill);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        doDefaultGenericPost("getBill", listener, errorListener, toPost, SeeServerResponses.ResponseGetBill.class);
    }

    public static void deleteBill(String billName, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        JSONObject toPost = new JSONObject();
        JSONObject bill = new JSONObject();

        try {
            bill.put("name", billName);
            toPost.put("bill", bill);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        doDefaultPost("deleteBill", listener, errorListener, toPost);
    }

    public static void getPosten(String billName, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        JSONObject toPost = new JSONObject();
        JSONObject bill = new JSONObject();

        try {
            bill.put("name", billName);
            toPost.put("bill", bill);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        doDefaultPost("getPosten", listener, errorListener, toPost);
    }

    public static void deletePosten(String billName, String postenID, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        JSONObject toPost = new JSONObject();
        JSONObject posten = new JSONObject();

        try {
            posten.put("bill", billName);
            posten.put("id", postenID);
            toPost.put("posten", posten);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        doDefaultPost("deletePosten", listener, errorListener, toPost);
    }

    public static void addPosten(String billName, String title, float costs, @Nullable  String info, @Nullable String[] to, @Nullable String creator, SeeServerResponses.ResponseCallback<SeeServerResponses.ResponseAddPosten> listener, Response.ErrorListener errorListener) {
        JSONObject toPost = new JSONObject();
        JSONObject posten = new JSONObject();

        try {
            posten.put("bill", billName);
            posten.put("title", title);
            posten.put("costs", costs);
            if(info != null)
                posten.put("info", info);
            posten.put("creator", creator == null ? NAME : creator);
            if(to != null)
                posten.put("to", to);

            toPost.put("posten", posten);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        doDefaultGenericPost("addPosten", listener, errorListener, toPost, SeeServerResponses.ResponseAddPosten.class);
    }

    public static void addUserToBill(String billName, String userName, String privileges, SeeServerResponses.ResponseCallback<SeeServerResponses.ResponseAddUserToBill> listener, Response.ErrorListener errorListener) {
        JSONObject toPost = new JSONObject();
        JSONObject bill = new JSONObject();
        JSONObject users = new JSONObject();

        try {
            users.put(userName, privileges);

            bill.put("name", billName);
            bill.put("users", users);

            toPost.put("bill", bill);
        }catch(Exception e){
            e.printStackTrace();
        }

        doDefaultGenericPost("addUserToBill", listener, errorListener, toPost, SeeServerResponses.ResponseAddUserToBill.class);
    }

    public static void removeUserFromBill(String billName, String userName, SeeServerResponses.ResponseCallback<SeeServerResponses.ResponseRemoveUserFromBill> listener, Response.ErrorListener errorListener) {
        JSONObject toPost = new JSONObject();
        JSONObject bill = new JSONObject();

        try {
            bill.put("name", billName);
            bill.put("user", userName);

            toPost.put("bill", bill);
        }catch(Exception e){
            e.printStackTrace();
        }

        doDefaultGenericPost("removeUserFromBill", listener, errorListener, toPost, SeeServerResponses.ResponseRemoveUserFromBill.class);
    }

    /* ---------------------------- DEFAULTS ---------------------------- */

    private static void doDefaultPost(String uri, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        doDefaultPost(uri, listener, errorListener, null);
    }

    private static <T extends SeeServerResponses.BaseResponse> void doDefaultGenericPost(String uri, SeeServerResponses.ResponseCallback<T> listener, Response.ErrorListener errorListener, @Nullable JSONObject toPost, Class<T> genClass) {
        if(toPost == null) {
            toPost = new JSONObject();
        }

        try {
            if(!toPost.has("name"))
                toPost.put("name", NAME);
            toPost.put("token", TOKEN);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpRequest.doPost(URL + "/" + uri, toPost, response -> {
            System.out.println("GOT: " + response.toString());

            if(JSONUtils.getBoolean(response, "successful")) {
                setTOKEN(JSONUtils.getString(response, "token"));
            }
            else {
                Toast.makeText(activity, response.optString("msg", "Unbekannter Fehler."), Toast.LENGTH_LONG);

                if(response.optString("msg", "").endsWith("Bitte neu anmelden.")) {
                    activity.finish(); // Activity wird beendet
                    return;
                }
            }

            listener.onResponse(createResponseInstance(response, genClass));
        }, errorListener);
    }

    private static <T extends SeeServerResponses.BaseResponse> void doDefaultGenericPost(String uri, SeeServerResponses.ResponseCallback<T> listener, Response.ErrorListener errorListener, Class<T> cl) {
        doDefaultGenericPost(uri, listener, errorListener, null, cl);
    }

    private static void doDefaultPost(String uri, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, @Nullable JSONObject toPost) {
        if(toPost == null) {
            toPost = new JSONObject();
        }

        try {
            toPost.put("name", NAME);
            toPost.put("token", TOKEN);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpRequest.doPost(URL + "/" + uri, toPost, response -> {
            System.out.println("GOT: " + response.toString());

            if(JSONUtils.getBoolean(response, "successful")) {
                setTOKEN(JSONUtils.getString(response, "token"));
            }
            else {
                Toast.makeText(activity, response.optString("msg", "Unbekannter Fehler."), Toast.LENGTH_LONG);

                if(response.optString("msg", "").endsWith("Bitte neu anmelden.")) {
                    activity.finish(); // Activity wird beendet
                    return;
                }
            }

            listener.onResponse(response);
        }, errorListener);
    }

    private static void doDefaultGet(String uri, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        doDefaultGet(uri, listener, errorListener, null);
    }

    private static void doDefaultGet(String uri, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, @Nullable JSONObject toPost) {
        if(toPost == null) {
            toPost = new JSONObject();
        }

        try {
            toPost.put("name", NAME);
            toPost.put("token", TOKEN);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpRequest.doGet(URL + "/" + uri, toPost, response -> {
            System.out.println("GOT: " + response.toString());
            setTOKEN(JSONUtils.getString(response, "token"));

            listener.onResponse(response);
        }, errorListener);
    }

    private static <T extends SeeServerResponses.BaseResponse> T createResponseInstance(JSONObject response, Class<T> cl) {
        try {
            return cl.getConstructor(JSONObject.class).newInstance(response);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* ---------------------------- OTHERS ---------------------------- */

    public static String getNAME() {
        return NAME;
    }

    public static String getTOKEN() {
        return TOKEN;
    }

    public static void setTOKEN(String TOKEN) {
        if(TOKEN != null)
            System.out.println("SET NEW TOKEN: " + TOKEN.substring(0, 15) + "...");
        else
            System.err.println("NEW TOKEN IS NULL");
        SeeServerRequests.TOKEN = TOKEN;
    }
}
