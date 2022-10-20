package com.treulieb.worktimetool.req;

import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.net.ssl.HttpsURLConnection;

public class HttpRequest {

    private static RequestQueue requestQueue;

    public static void setup(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public static void doPost(String url, JSONObject postData, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, postData, listener, errorListener);
        requestQueue.add(req);
    }

    public static void doGet(String url, JSONObject postData, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, postData, listener, errorListener);
        requestQueue.add(req);
    }
}
