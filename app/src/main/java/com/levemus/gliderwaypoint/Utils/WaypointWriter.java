package com.levemus.gliderwaypoint.Utils;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import com.levemus.gliderwaypoint.Types.Waypoint;

/**
 * Created by markcarter on 16-01-03.
 */
public class WaypointWriter {
    public static boolean save(String fileName, ArrayList<Waypoint> wayPoints) {
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(directory, fileName);
        try {
            FileWriter writer = new FileWriter(file);
            for(Waypoint waypoint : wayPoints) {
                writer.append(waypoint.serialize() + "\n");
            }
            writer.flush();
            writer.close();
        } catch(Exception e) { return false; }
        return true;
    }
}
