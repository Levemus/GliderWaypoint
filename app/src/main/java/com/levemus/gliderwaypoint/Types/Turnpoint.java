package com.levemus.gliderwaypoint.Types;

/**
 * Created by markcarter on 16-01-03.
 */
public class Turnpoint {
    String mName;
    public double mLatitude;
    public double mLongitude;
    public double mAltitude;

    public Turnpoint(String name, Double latitude, double longitude, double altitude) {
        mName = name;
        mLatitude = latitude;
        mLongitude = longitude;
        mAltitude = altitude;
    }

    @Override
    public String toString() {
        return mName;
    }
}
