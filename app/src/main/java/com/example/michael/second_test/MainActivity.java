package com.example.michael.second_test;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {


    public String tabchar = "\t";

    public Spinner validStates;
    public String RateState;
    public String year;
    public String tyear;
    public String make;
    public String model;
    public TextView helloWorld;
    public TextView showTerr;
    public Spinner modelYears;
    public Spinner modelMakes;
    public Spinner modelModels;
    public EditText zipCode;
    public String terrNum;
    public Button birthdayButton;
    public DatePickerDialog datePicker;
    public String rateFileKey;
    public Button submitButton;
    public Button submitEvent;
    public String bikeCC;
    public String bikeType;
    public ArrayAdapter<String> myMakeAdapter;
    public ArrayAdapter<String> myModelAdapter;
    public ArrayList<String> vehiclesList;
    public HashMap<String, String[]> modelData;
    public int driverAge;
    public RadioButton male;
    public RadioButton married;
    public RadioButton fullcoverage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        modelYears = (Spinner) this.findViewById(R.id.modelYears);
        validStates = (Spinner) this.findViewById(R.id.validStates);
        helloWorld = (TextView) this.findViewById(R.id.textView1);
        showTerr = (TextView) this.findViewById(R.id.RateTerr);
        modelMakes = (Spinner) this.findViewById(R.id.modelMakes);
        modelModels = (Spinner) this.findViewById(R.id.modelModels);
        zipCode = (EditText) this.findViewById(R.id.ZipCode);
        birthdayButton = (Button) this.findViewById(R.id.birthday);
        submitButton = (Button) this.findViewById(R.id.submit);
        submitEvent = (Button) this.findViewById(R.id.submitEvent);
        male = (RadioButton) this.findViewById(R.id.Male);
        married = (RadioButton) this.findViewById(R.id.Married);
        fullcoverage = (RadioButton) this.findViewById(R.id.FullCoverage);


        modelData = new HashMap<String, String[]>();

//      Birth Date Entry
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                birthdayButton.setText(dateFormatter.format(newDate.getTime()));
            }

        },mYear,mMonth,mDay);
        c.add(c.DAY_OF_YEAR,-(365*18));
        datePicker.getDatePicker().setMaxDate(c.getTimeInMillis());

        birthdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show();
            }
        });

//      Zip Code Entry
        zipCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onEnter();
                    InputMethodManager imm = (InputMethodManager)getBaseContext().getSystemService(
                            getBaseContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(zipCode.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    return true;
                }
                else {
                    return false;
                }
            }
        });


//      Model Year Entry - set-up
        ArrayAdapter<String> myYearAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, android.R.id.text1);
        myYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        myMakeAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, android.R.id.text1);
        myMakeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelMakes.setAdapter(myMakeAdapter);

        myModelAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        myModelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelModels.setAdapter(myModelAdapter);

        vehiclesList = getVehicleMaster();

        ArrayList<String> validYears = new ArrayList<String>();

        for (String line:vehiclesList) {
            String[] list = line.split(tabchar);
            tyear = list[5];
            if (validYears.contains(tyear) == false) {
                    validYears.add(tyear);
            }
        }

        Collections.sort(validYears, Collections.reverseOrder());
        myYearAdapter.addAll(validYears);
        modelYears.setAdapter(myYearAdapter);
        myYearAdapter.notifyDataSetChanged();

//      Model Year Entry
        modelYears.setSelection(0,false);
        modelYears.setOnItemSelectedListener(this);


//      Model Make Entry
        modelMakes.setSelection(0,false);
        modelMakes.setOnItemSelectedListener(this);


//      Model Model Entry
        modelModels.setSelection(0,false);
        modelModels.setOnItemSelectedListener(this);

//      State Entry
        validStates.setOnItemSelectedListener(this);

//      Submit Button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               final ArrayList<String> ratePage = getRatePage();
               calculateRates(ratePage);
            }
        });

//      Submit Event Button
        submitEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Toast toast = Toast.makeText(getApplicationContext(),
                       "Feature not implemented yet.", Toast.LENGTH_SHORT);
                 toast.show();

            }

        });


    }
     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.menu_main, menu);
         return true;
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         // Handle action bar item clicks here. The action bar will
         // automatically handle clicks on the Home/Up button, so long
         // as you specify a parent activity in AndroidManifest.xml.
         int id = item.getItemId();

         //noinspection SimplifiableIfStatement
         if (id == R.id.action_settings) {
             return true;
         }

         return super.onOptionsItemSelected(item);
     }


    //Utility Stuff Down Here

//  Get Vehicle File
    private ArrayList<String> getVehicleMaster(){
        BufferedReader vehiclefile = new BufferedReader(new InputStreamReader(
                getResources().openRawResource(R.raw.vehicle_master_data_tab)));

        ArrayList<String> vehicles = new ArrayList<String>();
        String line = null;

        try {
            while ((line = vehiclefile.readLine()) != null) {
                vehicles.add(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return vehicles;
    }

//  Get Main Rate File
    private ArrayList<String> getRatePage() {

        rateFileKey = "raw/" + RateState.toLowerCase() + "_t" + terrNum.toLowerCase();
        String rateBikeType = "";

        if (bikeType.toLowerCase().equals("sport")) {
            rateBikeType = "_sport";
        }
        else {
            rateBikeType = "_ns";
        }

        rateFileKey = rateFileKey.concat(rateBikeType);

        helloWorld.setText(rateFileKey);
        BufferedReader rateFile = new BufferedReader(new InputStreamReader(
                getResources().openRawResource(getResources().getIdentifier(rateFileKey,"raw",getPackageName()))

        ))  ;


        ArrayList<String> ratePage = new ArrayList<String>();
        String line = null;

        try {
            while ((line = rateFile.readLine()) != null) {
                ratePage.add(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ratePage;

    }

//  Get Territory/Zip file
    private ArrayList<String> getStateTerritories(String state) {
        BufferedReader zipFile = new BufferedReader(new InputStreamReader(

                getResources().openRawResource(getResources().getIdentifier(
                        "raw/" + RateState.toLowerCase(),"raw",getPackageName()))
        ));

        ArrayList<String> zips = new ArrayList<String>();
        String line = null;

        try {
            while ((line = zipFile.readLine()) != null) {
                zips.add(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return zips;
    }

//  After clicking 'Done' when getting zip code
    private void onEnter() {
        int length = zipCode.getText().toString().length();

        if (!(length == 1 || length == 5)) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Must be valid Zip Code or Territory", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            String zip = zipCode.getText().toString();

            if (length == 1) {
                terrNum = String.valueOf(zip);
                showTerr.setText(terrNum);
            }
            else {
                ArrayList<String> zips = getStateTerritories(RateState);
                final HashMap<String, String> territory = new HashMap<String, String>();
                for (String line: zips) {
                    String[] cities = line.split(tabchar);
                    if (cities[0].equals(zip)) {
                        territory.put(cities[1], cities[2]);
                    }
                }
                if (territory.keySet().size() > 1) {
                    AlertDialog.Builder question = new AlertDialog.Builder(this);
                    question.setTitle("Choose City: ");
                    question.setCancelable(false);

                    LayoutInflater inflater = getLayoutInflater();
                    View dialoglayout = inflater.inflate(R.layout.dialog_layout, null);
                    question.setView(dialoglayout);

                    final Spinner dialogSpin = (Spinner) dialoglayout.findViewById(R.id.dialog_spin);

                    question.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String city = dialogSpin.getSelectedItem().toString();
                            terrNum = territory.get(city);
                            showTerr.setText(terrNum);
                            dialog.dismiss();
                        }
                    });

                    final ArrayAdapter<String> myDialogAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
                    myDialogAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    myDialogAdapter.addAll(territory.keySet());
                    dialogSpin.setAdapter(myDialogAdapter);
                    myDialogAdapter.notifyDataSetChanged();

                    AlertDialog dialog = question.create();
                    dialog.show();

                }
                else if (territory.keySet().isEmpty()) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Error", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    for (String value: territory.values()) {
                        terrNum = value;
                        showTerr.setText(terrNum);
                    }
                }
            }
        }
    }

//  Main Rate Calculation
    public void calculateRates(ArrayList<String> ratePage) {

        int intCC = Integer.parseInt(bikeCC);
        int ageOffset = 0;
        int ccOffset = 0;
        int totalOffset = 0;
        String rateLine = "";

        boolean addCompColl = fullcoverage.isSelected();

        driverAge = getAge();

        if (!bikeType.equals("SPORT")) {

            if (driverAge > 34) {
                ageOffset = 3;
            } else if (driverAge > 29) {
                ageOffset = 15;
            } else if (driverAge > 25) {
                ageOffset = 27;
            } else if (driverAge > 20) {
                ageOffset = 39;
            } else {
                ageOffset = 51;
            }

            if (intCC > 1400) {
                ccOffset = 6;
            } else if (intCC > 1210) {
                ccOffset = 5;
            } else if (intCC > 950) {
                ccOffset = 4;
            } else if (intCC > 750) {
                ccOffset = 3;
            } else if (intCC > 575) {
                ccOffset = 2;
            } else if (intCC > 370) {
                ccOffset = 1;
            } else {
                ccOffset = 0;
            }
        } else {
            if (driverAge > 34) {
                ageOffset = 3;
            } else if (driverAge > 29) {
                ageOffset = 12;
            } else if (driverAge > 25) {
                ageOffset = 21;
            } else if (driverAge > 20) {
                ageOffset = 30;
            } else {
                ageOffset = 39;
            }
            if (intCC > 1400) {
                ccOffset = 4;
            } else if (intCC > 1210) {
                ccOffset = 3;
            } else if (intCC > 950) {
                ccOffset = 2;
            } else if (intCC > 750) {
                ccOffset = 1;
            } else {
                ccOffset = 0;
            }

        }
        totalOffset = ageOffset + ccOffset ;
//     helloWorld.setText(totalOffset+" "+intCC+" "+bikeType);
//     helloWorld.setText(totalOffset + "");
        rateLine = ratePage.get(totalOffset);
     helloWorld.setText(totalOffset + " " +rateLine);

        String[] line = rateLine.split(tabchar);
        ArrayList<String> limits = new ArrayList<String>();
        for (int i=4; i<line.length; i++) {
            limits.add(line[i]);
        }
        displayRates(limits);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        ViewGroup vg = (ViewGroup) view.getParent();
        int vgId = vg.getId();
        switch (vgId) {
            case R.id.modelYears:
                year = modelYears.getItemAtPosition(position).toString();
                myMakeAdapter.clear();
                myModelAdapter.clear();
                myMakeAdapter.notifyDataSetChanged();
                myModelAdapter.notifyDataSetChanged();


                ArrayList<String> validMakes = new ArrayList<String>();

                for(String line: vehiclesList) {
                    String[] list = line.split(tabchar);
                    if (list[5].equals(year)) {
                        String make = list[2];
                        if (validMakes.contains(make) == false) {
                            validMakes.add(make);
                        }
                    }
                }
                Collections.sort(validMakes);
                myMakeAdapter.addAll(validMakes);
                myMakeAdapter.notifyDataSetChanged();
                break;
            case R.id.validStates:
                RateState = validStates.getItemAtPosition(position).toString();
                break;
            case R.id.modelMakes:
                make = modelMakes.getItemAtPosition(position).toString();
                myModelAdapter.clear();
                myModelAdapter.notifyDataSetChanged();

                ArrayList<String> validModels = new ArrayList<String>();
                for(String line: vehiclesList) {
                    String[] list = line.split(tabchar);
                    if (list[5].equals(year)) {
                        if (list[2].equals(make)) {
                            String model = list[3];
                            if (validModels.contains(model) == false) {
                                validModels.add(model);
                                String[] data = {list[1], list[6]};
                                modelData.put(model,data);
                            }
                        }
                    }
                }

                Collections.sort(validModels);
                myModelAdapter.addAll(validModels);
                myModelAdapter.notifyDataSetChanged();
                break;
            case R.id.modelModels:
                model = modelModels.getItemAtPosition(position).toString();

                bikeType = modelData.get(model)[0];
                bikeCC = modelData.get(model)[1];
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public int getAge() {
        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();
        Date dateOfBirth = null;
        try {
            String date = birthdayButton.getText().toString();
            DateFormat format = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
            dateOfBirth = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int age = 0;

        birthDate.setTime(dateOfBirth);
        if (birthDate.after(today)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }

        age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year
        if ( (birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
                (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH ))){
            age--;

            // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
        }else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH )) &&
                (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH ))){
            age--;
        }

        return age;
    }


    public void displayRates(ArrayList<String> limits) {
        AlertDialog.Builder quote = new AlertDialog.Builder(this);
        quote.setTitle("Save Your Quote?");

        String strFinal = "";
        String str = "";
        for (int i=0; i < limits.size(); i++) {
            str = limits.get(i) + " = $" + /*quote*/ "\n";
            strFinal = str + limits.get(i);
        }

        quote.setMessage(strFinal);

        quote.setCancelable(true);
        quote.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });

        quote.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Answer All Fields: ");
                builder.setCancelable(false);

                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.save_dialog, null);
                builder.setView(dialogLayout);
                builder.setPositiveButton(android.R.string.yes, null);
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog saveDia = builder.create();
                dialog.dismiss();
                saveDia.show();
            }
        });

        AlertDialog dialog = quote.create();
        dialog.show();


    }

}
