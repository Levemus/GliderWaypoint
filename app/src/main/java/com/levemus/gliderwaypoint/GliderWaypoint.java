package com.levemus.gliderwaypoint;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.levemus.gliderwaypoint.Managers.IClient;
import com.levemus.gliderwaypoint.Messages.IMessage;
import com.levemus.gliderwaypoint.Types.Turnpoint;
import com.levemus.gliderwaypoint.Types.Waypoint;
import com.levemus.gliderwaypoint.Utils.WPTFileReader;
import com.levemus.gliderwaypoint.Utils.WaypointWriter;
import com.levemus.gliderwaypoint.WifiDirect.Messages.OpCodes.WifiDirectEvent;
import com.levemus.gliderwaypoint.WifiDirect.Messages.WifiDirectEventMessage;
import com.levemus.gliderwaypoint.WifiDirect.WifiDirectManager;

public class GliderWaypoint extends AppCompatActivity {

    // Constants
    private final String TAG = this.getClass().getSimpleName();

    ArrayList<Turnpoint> mTurnPoints = new ArrayList<>();
    ArrayList<Waypoint> mWayPoints = new ArrayList<>();
    private int mSelectedItemIndex = -1;

    private WifiDirectManager mManager = new WifiDirectManager();
    private Activity mActivity = this;
    private String mWaypointsFile = "Waypoints - Valadares.wpt";
    private String mOutputFileName = "waypoints.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_glider_waypoint);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setupTurnPointsList();
        setupWayPointsList();

        setUpAddRemoveButton();
        setUpLoadSendButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mManager.resume(this);
        ListView turnPoints = (ListView) findViewById(R.id.turnpoints);
        ArrayAdapter<Turnpoint> turnPointArrayAdapter = ((ArrayAdapter<Turnpoint>) turnPoints.getAdapter());
        turnPointArrayAdapter.clear();
        for(Turnpoint turnPoint : mTurnPoints) {
            turnPointArrayAdapter.add(turnPoint);
        }

        ListView wayPoints = (ListView) findViewById(R.id.waypoints);
        ArrayAdapter<Waypoint> wayPointArrayAdapter = ((ArrayAdapter<Waypoint>) wayPoints.getAdapter());
        wayPointArrayAdapter.clear();
        for(Waypoint wayPoint : mWayPoints) {
            wayPointArrayAdapter.add(wayPoint);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mManager.pause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mManager.stop(this);
    }

    private void setUpAddRemoveButton() {
        final Button loadSendButton = (Button) findViewById(R.id.load_send);
        final Button addRemoveButton = (Button) findViewById(R.id.add_remove);
        addRemoveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Button button = (Button) view;
                if(button.getText().toString().compareTo(getString(R.string.button_add)) == 0) {
                    ListView turnPoints = (ListView) findViewById(R.id.turnpoints);
                    ArrayAdapter<Turnpoint> turnPointArrayAdapter = ((ArrayAdapter<Turnpoint>) turnPoints.getAdapter());
                    final Turnpoint turnPoint = turnPointArrayAdapter.getItem(mSelectedItemIndex);

                    LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
                    View promptView = layoutInflater.inflate(R.layout.radius_alert, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
                    alertDialogBuilder.setView(promptView);
                    final EditText input = (EditText) promptView.findViewById(R.id.userInput);
                    input.setInputType(InputType.TYPE_CLASS_PHONE);
                    KeyListener keyListener = DigitsKeyListener.getInstance("1234567890");
                    input.setKeyListener(keyListener);
                    input.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    try {
                                        // get user input and set it to result
                                        int radius = Integer.parseInt(input.getText().toString());
                                        ListView wayPoints = (ListView) findViewById(R.id.waypoints);

                                        Waypoint newPoint = new Waypoint(turnPoint.mLatitude, turnPoint.mLongitude, turnPoint, radius);
                                        ArrayAdapter<Waypoint> waypointArrayAdapter = (ArrayAdapter<Waypoint>) wayPoints.getAdapter();
                                        if (waypointArrayAdapter.getCount() != 0)
                                            newPoint.updateDistance(waypointArrayAdapter.getItem(waypointArrayAdapter.getCount() - 1));
                                        ((ArrayAdapter<Waypoint>) wayPoints.getAdapter()).add(newPoint);
                                        mWayPoints.add(newPoint);
                                        Log.i(TAG, "Waypoint item Added: " + newPoint);

                                        Button loadSaveButton = (Button) findViewById(R.id.load_send);
                                        loadSaveButton.setText(getString(R.string.button_send));
                                        loadSaveButton.setEnabled(true);
                                    }catch(Exception e){}
                                }
                            })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,	int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create an alert dialog
                    AlertDialog alertD = alertDialogBuilder.create();
                    alertD.show();
                    alertD.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                } else if(button.getText().toString().compareTo(getString(R.string.button_remove)) == 0) {
                    ListView wayPoints = (ListView) findViewById(R.id.waypoints);
                    ArrayAdapter<Waypoint> waypointArrayAdapter = (ArrayAdapter<Waypoint>)wayPoints.getAdapter();
                    Waypoint waypoint = waypointArrayAdapter.getItem(mSelectedItemIndex);
                    waypointArrayAdapter.remove(waypoint);
                    mWayPoints.remove(waypoint);
                    Log.i(TAG, "Waypoint item Removed: " + waypoint);
                    ArrayList<Waypoint> list = new ArrayList<Waypoint>();
                    for(int i = 0; i < waypointArrayAdapter.getCount(); i++)
                        list.add(waypointArrayAdapter.getItem(i));
                    Waypoint previous = null;
                    for(Waypoint current : list) {
                        current.updateDistance(previous);
                        previous = current;
                    }

                    if(mWayPoints.isEmpty()) {
                        addRemoveButton.setEnabled(false);
                        addRemoveButton.setText(getString(R.string.button_add));
                        loadSendButton.setEnabled(false);
                    }

                }
            }
        });
    }

    private void setUpLoadSendButton() {
        final Button loadSendButton = (Button) findViewById(R.id.load_send);
        loadSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Button button = (Button) view;
                if(button.getText().toString().compareTo(getString(R.string.button_load)) == 0) {
                    ListView turnPoints = (ListView) findViewById(R.id.turnpoints);
                    ArrayAdapter<Turnpoint> turnPointArrayAdapter = ((ArrayAdapter<Turnpoint>) turnPoints.getAdapter());
                    mTurnPoints = WPTFileReader.parse(mWaypointsFile);
                    for(Turnpoint turnPoint : mTurnPoints) {
                        turnPointArrayAdapter.add(turnPoint);
                    }
                    Button loadSaveButton = (Button) findViewById(R.id.load_send);
                    loadSaveButton.setText(getString(R.string.button_send));
                    loadSaveButton.setEnabled(false);
                }
                else if(button.getText().toString().compareTo(getString(R.string.button_send)) == 0) {
                    ListView wayPoints = (ListView) findViewById(R.id.waypoints);
                    ArrayAdapter<Waypoint> wayPointArrayAdapter = ((ArrayAdapter<Waypoint>) wayPoints.getAdapter());
                    ArrayList<Waypoint> points = new ArrayList<>();
                    for(int i = 0; i < wayPointArrayAdapter.getCount(); i++) {
                        points.add(wayPointArrayAdapter.getItem(i));
                    }

                    WaypointWriter.save(mOutputFileName, points);
                    mManager.setIsServer(false);
                    mManager.start(mActivity);
                }
            }
        });
    }

    private void setupWayPointsList() {
        ListView wayPoints = (ListView) findViewById(R.id.waypoints);
        final ArrayAdapter<Waypoint> wayPointArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        wayPoints.setAdapter(wayPointArrayAdapter);

        wayPoints.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String  value = ((ListView)parent).getItemAtPosition(position).toString();
                Log.i(TAG, "Waypoint item Selected: " + value);
                Button addRemoveButton = (Button) findViewById(R.id.add_remove);
                addRemoveButton.setText(getString(R.string.button_remove));
                addRemoveButton.setEnabled(true);

                Button loadSaveButton = (Button) findViewById(R.id.load_send);
                loadSaveButton.setText(getString(R.string.button_send));

                mSelectedItemIndex = position;
            }
        });
    }

    private void setupTurnPointsList() {
        ListView turnPoints = (ListView) findViewById(R.id.turnpoints);
        ArrayAdapter<Turnpoint> turnPointArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        turnPoints.setAdapter(turnPointArrayAdapter);

        turnPoints.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String value = parent.getItemAtPosition(position).toString();
                Log.i(TAG, "Turnpoint item Selected: " + value);
                Button addRemoveButton = (Button) findViewById(R.id.add_remove);
                addRemoveButton.setText(getString(R.string.button_add));
                addRemoveButton.setEnabled(true);

                mSelectedItemIndex = position;
                Log.i(TAG, "Selected Index: " + mSelectedItemIndex);
            }
        });
    }
}
