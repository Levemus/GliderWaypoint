package com.levemus.gliderwaypoint.Utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.levemus.gliderwaypoint.Types.Turnpoint;

/**
 * Created by markcarter on 16-01-03.
 */
public class WPTFileReader {
    private final static String TAG = "WPTFileReader";

    private static final int WAYPOINT_START = 4;
    private static final int NAME_INDEX = 10;
    private static final int LATITUDE_INDEX = 2;
    private static final int LONGITUDE_INDEX = 3;
    private static final int ALTITUDE_INDEX = 14;

    public static ArrayList<Turnpoint> parse(String fileName) {
        ArrayList<String> lines = new ArrayList<>();
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(directory, fileName);

        ArrayList<Turnpoint> turnPoints = new ArrayList<>();

        BufferedReader br = null;
        try {
            FileReader fr = new FileReader(file);
            br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            Log.i(TAG, "Exception: " + e);
        } finally {
            try {
                br.close();
            } catch (Exception e) {}
        }

        int lineNum = 0;
        for(String line : lines) {
            if(lineNum >= WAYPOINT_START) {
                ArrayList<String> params = new ArrayList<>();
                StringTokenizer defaultTokenizer = new StringTokenizer(line, ",");
                while(defaultTokenizer.hasMoreElements())
                {
                    params.add(defaultTokenizer.nextToken());
                }
                Turnpoint turnPoint = new Turnpoint(params.get(NAME_INDEX).trim(),
                        Double.parseDouble(params.get(LATITUDE_INDEX).replace(" ", "")),
                        Double.parseDouble(params.get(LONGITUDE_INDEX).replace(" ", "")),
                        Double.parseDouble(params.get(ALTITUDE_INDEX).replace(" ", "")) * 0.3048); // feet to meters
                turnPoints.add(turnPoint);
            }
            lineNum++;
        }
        return turnPoints;
    }
}
