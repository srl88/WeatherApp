package com.example.wheatherforecast;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Class to produce request and store them.
 * Singleton patter so a single queue is assured.
 */

public class RequestSingletonPattern {

    private static  RequestSingletonPattern mInstance;
    private RequestQueue mRequestQueue;
    private static Context  mCtx;

    /**
     * Constructor. Requires the context from which the request is produced.
     * @param c
     */
    private RequestSingletonPattern(Context c){
        this.mCtx = c;
        //once the context is stored we can create the volley Queue for requests.
        this.mRequestQueue = getRequestQueue();
    }

    /**
     * Creates an object of the same class if the current is null (current is always static)
     * @param c
     * @return
     */
    public static synchronized  RequestSingletonPattern getIstance(Context c){
        try{
            if(mInstance==null){
                mInstance = new RequestSingletonPattern(c.getApplicationContext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return mInstance;
    }

    /**
     * Creates and returns a requests queue
     * @return
     */
    public RequestQueue getRequestQueue(){
        try {
            if(mRequestQueue==null){
                mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return mRequestQueue;
    }

    /**
     * Adds requests to the queue.
     * @param req
     * @param <T>
     */
    public <T> void addToRequestQueue(Request<T> req) {
        try{
            getRequestQueue().add(req);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
