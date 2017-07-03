package com.example.android.quakereport;

/**
 * Created by billwen on 2017/6/27.
 */


public class Earthquake {
    private double mMagnitude;
    private String mLocation;
    private long mTime;
    private String mUrl;

    /**
     * @param magnitude 地震的震级
     * @param location  地震发生的地点
     * @param time      地震发生的时间
     */
    public Earthquake(double magnitude, String location, long time, String url) {
        mMagnitude = magnitude;
        mLocation = location;
        mTime = time;
        mUrl = url;
    }

    public double getMagnitude() {
        return mMagnitude;
    }

    public String getLocation() {
        return mLocation;
    }

    public long getTime() {
        return mTime;
    }

    public String getUrl() {
        return mUrl;
    }
}
