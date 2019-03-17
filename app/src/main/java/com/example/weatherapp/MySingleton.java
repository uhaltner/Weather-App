package com.example.weatherapp;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MySingleton {

    private static MySingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    public MySingleton(Context context) {
        mCtx = context.getApplicationContext();
        mRequestQueue = getRequestQueue();
    }


    public static synchronized MySingleton getInstance(Context context){

        if(mInstance == null){
            mInstance = new MySingleton(context.getApplicationContext());
        }

        return  mInstance;
    }


    private RequestQueue getRequestQueue() {

        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req){ getRequestQueue().add(req); }
}
