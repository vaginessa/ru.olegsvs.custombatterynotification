package ru.olegsvs.custombatterynotification;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class SettingsActivity extends AppCompatActivity {
    public static String TAG = SettingsActivity.class.getSimpleName(); //setup TAG
    
    private BatteryManager mBatteryManager = null;
    public Spinner spinnerBatteries;
    public Spinner capacityFiles;
    public Spinner statusFiles;

    public CheckBox chbAutostartService;
    public SharedPreferences sharedPref;
    public Button intervalSetBTN;
    public EditText intervalET;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(SettingsActivity.TAG, "onDestroy: Activity");
        Window w = getWindow();
        if (w != null) {
            View v = w.getDecorView();
            if (v != null) {
                unbindDrawables(v);
            }
        }
        System.gc();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            if (!(view instanceof AdapterView<?>))
                ((ViewGroup) view).removeAllViews();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics()); //activate fabric.crashlytics
        setContentView(R.layout.activity_settings);
        try {
            getSupportActionBar().setDisplayShowHomeEnabled(true); //setup icon on ActionBar
            getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        } catch (NullPointerException e) {
            Log.e(TAG, "onCreate: setDisplayShowHomeEnabled " +e.toString());
        }

        sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE); //get prefs
        Log.i(SettingsActivity.TAG, "onCreate:         setupViews");
        setupViews(); //find views
        Log.i(SettingsActivity.TAG, "onCreate:         setupSpinners");
        setupSpinners(); //set spinners values
        Log.i(SettingsActivity.TAG, "onCreate:         loadParams");
        loadParams(); //load values from prefs
    }

    public void setupSpinners() {
        if(!ScannerPaths.checkPaths()) { //if isError false & directory found 
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, ScannerPaths.getPathsPowerSupply());
            spinnerBatteries.setAdapter(adapter);
        } else Toast.makeText(this,ScannerPaths.power_supply + " directory not found",Toast.LENGTH_LONG).show();

        spinnerBatteries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

            loadPathsEntry(); //load subdirs * subfiles

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }


    public void setupViews() {
        chbAutostartService = (CheckBox) findViewById(R.id.chbServiceAutoStart);
        spinnerBatteries = (Spinner) findViewById(R.id.spinnerBatteries);
        intervalSetBTN = (Button) findViewById(R.id.intervalSetBTN);
        intervalET = (EditText) findViewById(R.id.intervalET);
        spinnerBatteries = (Spinner) findViewById(R.id.spinnerBatteries);
        capacityFiles = (Spinner) findViewById(R.id.spinnerCapacity);
        statusFiles = (Spinner) findViewById(R.id.spinnerStatus);
    }

    public void loadParams() {
        Log.w(SettingsActivity.TAG, "onCreate: loading sharedPrefs batterySelection " + sharedPref.getInt("batterySelection",0));
        Log.w(SettingsActivity.TAG, "onCreate: loading sharedPrefs serviceRun " + sharedPref.getBoolean("serviceRun", false));
        Log.w(SettingsActivity.TAG, "onCreate: loading sharedPrefs serviceAutoStart " + sharedPref.getBoolean("serviceAutoStart", false));
        Log.w(SettingsActivity.TAG, "onCreate: loading sharedPrefs interval " + sharedPref.getInt("interval", 2));
        Log.w(SettingsActivity.TAG, "onCreate: loading sharedPrefs lastTypeBattery " + sharedPref.getString("lastTypeBattery", "null"));
        Log.w(SettingsActivity.TAG, "onCreate: loading sharedPrefs lastStateBattery " + sharedPref.getString("lastStateBattery", "null"));
        Log.w(SettingsActivity.TAG, "onCreate: loading sharedPrefs capacityFiles " + sharedPref.getInt("capacityFiles", 0));
        Log.w(SettingsActivity.TAG, "onCreate: loading sharedPrefs statusFiles " + sharedPref.getInt("statusFiles", 0));
        Log.w(SettingsActivity.TAG, "onCreate: loading sharedPrefs lastID " + sharedPref.getInt("lastID", 0));
        Log.w(SettingsActivity.TAG, "onCreate: loading sharedPrefs lastIDString " + sharedPref.getString("lastIDString", ScannerPaths.power_supply));

        chbAutostartService.setChecked(sharedPref.getBoolean("serviceAutoStart", false));
        intervalET.setText(String.valueOf(sharedPref.getInt("interval", 2)));
        spinnerBatteries.setSelection(sharedPref.getInt("lastID", 0));
    }

    public void loadPathsEntry() {
        ArrayAdapter<String> adapterCapacity = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, ScannerPaths.getPathsEntryOfPowerSupply(spinnerBatteries.getSelectedItem().toString()));
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, ScannerPaths.getPathsEntryOfPowerSupply(spinnerBatteries.getSelectedItem().toString()));
        if((capacityFiles.getAdapter() == null) && (statusFiles.getAdapter() == null)) { //if first show activity
            capacityFiles.setAdapter(adapterCapacity);
            statusFiles.setAdapter(adapterStatus);
            capacityFiles.setSelection(sharedPref.getInt("capacityFiles", 0));
            statusFiles.setSelection(sharedPref.getInt("statusFiles", 0));
        } else { //if do select new path
            capacityFiles.setAdapter(adapterCapacity);
            statusFiles.setAdapter(adapterStatus);
        }
    }
    public void showNotifyClick(View v){
        try {
            mBatteryManager = null;
            Intent intent = new Intent(getApplicationContext(), BatteryManagerService.class);
            stopService(intent); //stop always

            mBatteryManager = new BatteryManager(capacityFiles.getSelectedItem().toString(),statusFiles.getSelectedItem().toString() );
            if (mBatteryManager.isSupport) {
                Log.w(SettingsActivity.TAG, "onCreate: isSupported");
                intent.putExtra("BatteryManager", mBatteryManager); //send parcel mBatteryManager to BatteryManagerService
                mBatteryManager = null;
                saveSpinners(); //save positions to prefs
                if(BatteryManagerService.isMyServiceRunning()) { //if running, then reload service
                    stopService(intent);
                    startService(intent);
                } else startService(intent);
            } else {mBatteryManager = null; }
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    public void saveSpinners() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("lastTypeBattery",capacityFiles.getSelectedItem().toString());
        editor.putString("lastStateBattery",statusFiles.getSelectedItem().toString());
        editor.putString("lastIDString",spinnerBatteries.getSelectedItem().toString());
        editor.putInt("capacityFiles", (int) capacityFiles.getSelectedItemId());
        editor.putInt("statusFiles", (int) statusFiles.getSelectedItemId());
        editor.putInt("lastID", (int) spinnerBatteries.getSelectedItemId());
        editor.apply();
    }

    public void intervalClick(View v) {
        if(!intervalET.getText().toString().equals("0")) { //0 is bad interval value
            Log.w(SettingsActivity.TAG, "intervalClick: setting interval " + intervalET.getText().toString());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("interval" , Integer.parseInt(intervalET.getText().toString()));
            editor.apply();
            editor.commit(); //save interval value to prefs
            BatteryManagerService.setInterval(Integer.parseInt(intervalET.getText().toString())); //setup interval on service
        } else Toast.makeText(getApplicationContext(),getString(R.string.intervalWarning),Toast.LENGTH_LONG).show();
    }

    public void cnbAutoStartClick(View v) {
        Log.w(SettingsActivity.TAG, "cnbAutoStartClick: changedState = " + chbAutostartService.isChecked());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("serviceAutoStart" , chbAutostartService.isChecked());
        editor.apply();
        editor.commit(); //save auto start value
    }

    public void dismissNotifyClick(View view) {
        if(BatteryManagerService.isMyServiceRunning()) {
            mBatteryManager = null;
            Intent intent = new Intent(getApplicationContext(), BatteryManagerService.class);
            stopService(intent); //disable service and dismiss notify
        }
    }
}
