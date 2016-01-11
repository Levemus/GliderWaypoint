package com.levemus.gliderwaypoint.Types;

import android.location.Location;

/**
 * Created by markcarter on 16-01-03.
 */
public class Waypoint {
    public double mLatitude;
    public double mLongitude;
    public double mDistance;
    public Turnpoint mTurnPoint;
    public int mRadius;

    private void init(Double latitude, double longitude, Turnpoint turnpoint, int radius) {
        mLatitude = latitude;
        mLongitude = longitude;
        mTurnPoint = turnpoint;
        mRadius = radius;
        mDistance = 0;
    }

    public Waypoint(Double latitude, double longitude, Turnpoint turnpoint, int radius) {
        init(latitude, longitude, turnpoint, radius);
    }

    public Waypoint(Double latitude, double longitude, Turnpoint turnpoint, int radius, Waypoint previous) {
        init(latitude, longitude, turnpoint, radius);
        updateDistance(previous);
    }

    public void updateDistance(Waypoint previous)
    {
        if(previous == null) {
            mDistance = 0;
            return;
        }

        Location currentLocation = new Location("initial");
        currentLocation.setLatitude(previous.mLatitude);
        currentLocation.setLongitude(previous.mLongitude);
        Location targetLocation = new Location("target");
        targetLocation.setLatitude(mLatitude);
        targetLocation.setLongitude(mLongitude);
        mDistance = (double)currentLocation.distanceTo(targetLocation) + previous.mDistance;
    }

    @Override
    public String toString() {
        return this.mTurnPoint.mName + " - " + mRadius + " m (Distance: " + Math.round(mDistance / 100) / 10 + " km)";
    }

    public String serialize() {
        return (
                mTurnPoint.mName + ","
                + mTurnPoint.mLatitude + ","
                + mTurnPoint.mLongitude + ","
                        + (mTurnPoint.mAltitude  * 0.3048)+ ","
                + mRadius + ","
                + mLatitude + ","
                + mLongitude
        );
    }
}
