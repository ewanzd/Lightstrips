package com.ewanzd.lightstrips.core;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class RestClient {
    private static final String TAG = "RestClient";
    private RequestQueue requestQueue;
    private Context context;

    public RestClient(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void sendGet(String url) {

        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Fehlgeschlagen", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
        );
        stringRequest.setTag(TAG);

        // Add the request to the queue
        requestQueue.add(stringRequest);
    }

    public void stop() {
        requestQueue.cancelAll(TAG);
    }
}
