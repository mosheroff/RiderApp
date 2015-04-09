package com.example.michael.second_test;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Telephony;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Property;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

    public String dataFileName;
    public String eventName;
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
    public String chosenCity;
    public String chosenCounty;
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
    public CheckBox transferDisc;
    public CheckBox multiBikeDisc;
    public CheckBox podDisc;
    public EditText Fname;
    public EditText Lname;
    public EditText emailaddr;
    public EditText eventText;
    public File saveFile;
    public Button deleteButton;
    public Button uploadButton;
    private int check = 0;
    private boolean del = false;
    private boolean dialogDone = false;
    private Thread thread;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deleteButton = (Button) this.findViewById(R.id.delete);
        uploadButton = (Button) this.findViewById(R.id.upload);
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
        transferDisc = (CheckBox) this.findViewById(R.id.transferDisc);
        multiBikeDisc = (CheckBox) this.findViewById(R.id.multiBikeDisc);
        podDisc = (CheckBox) this.findViewById(R.id.podDisc);


        eventName = setEventName();
        String printEventName = eventName;
        if (printEventName == "") {
            printEventName = "          ";
        }
        helloWorld.setText(printEventName);

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

        }, mYear, mMonth, mDay);
        c.add(c.DAY_OF_YEAR, -(365 * 18));
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
                    InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(
                            getBaseContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(zipCode.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    return true;
                } else {
                    return false;
                }
            }
        });


//      Model Year Entry - set-up
        ArrayAdapter<String> myYearAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        myYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        myMakeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        myMakeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelMakes.setAdapter(myMakeAdapter);

        myModelAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        myModelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelModels.setAdapter(myModelAdapter);

        vehiclesList = getVehicleMaster();

        ArrayList<String> validYears = new ArrayList<String>();

        for (String line : vehiclesList) {
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
        modelYears.setSelection(0, false);
        modelYears.setOnItemSelectedListener(this);


//      Model Make Entry
        modelMakes.setSelection(0, false);
        modelMakes.setOnItemSelectedListener(this);


//      Model Model Entry
        modelModels.setSelection(0, false);
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
                email();
            }

        });


//      Upload Button
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Are You Sure?");
                builder.setCancelable(true);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File folder = new File(getBaseContext().getExternalFilesDir("") + "");
                        Integer quoteCtr = 0;
                        final File[] filePaths = folder.listFiles();
                        for (int i = 0; i < filePaths.length; i++) {
                            String fileName = filePaths[i].toString();
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Uploading file " + fileName, Toast.LENGTH_SHORT);
                            toast.show();
                            try {
                                File uploadFile = new File(fileName);
                                BufferedReader uploadBr = new BufferedReader(new FileReader(uploadFile));
                                String qline;
                                try {
                                    while ((qline = uploadBr.readLine()) != null) {
                                        String[] quoteLine = qline.split(tabchar);
                                        quoteCtr += 1;
                                        Toast toast1 = Toast.makeText(getApplicationContext(),
                                                "Uploading Quote " + quoteLine[15] + " " + quoteLine[16], Toast.LENGTH_SHORT);
                                        toast1.show();
//                                        if (quoteCtr == 1 && i == 1) {
                                            uploadQoute(quoteLine, fileName, quoteCtr);
                                        SystemClock.sleep(5000);
//                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        dialog.dismiss();
                        Toast toast2 = Toast.makeText(getApplicationContext(),
                                "Files successfully uploaded", Toast.LENGTH_SHORT);
                        toast2.show();
                    }
                });

                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

//      Delete Button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Are You Sure?");
                builder.setCancelable(true);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File folder = new File(getBaseContext().getExternalFilesDir("") + "");
                        final File[] filePaths = folder.listFiles();
                        for (int i=0; i<filePaths.length; i++) {
                            filePaths[i].delete();
                        }
                        dialog.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Files successfully deleted", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

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
    private ArrayList<String> getVehicleMaster() {
        BufferedReader vehiclefile = new BufferedReader(new InputStreamReader(
                getResources().openRawResource(R.raw.vehicle_master_data_tab)));

        ArrayList<String> vehicles = new ArrayList<String>();
        String line = null;

        try {
            while ((line = vehiclefile.readLine()) != null) {
                vehicles.add(line);
            }
        } catch (Exception e) {
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
        } else {
            rateBikeType = "_ns";
        }

        rateFileKey = rateFileKey.concat(rateBikeType);

        BufferedReader rateFile = new BufferedReader(new InputStreamReader(
                getResources().openRawResource(getResources().getIdentifier(rateFileKey, "raw", getPackageName()))

        ));


        ArrayList<String> ratePage = new ArrayList<String>();
        String line = null;

        try {
            while ((line = rateFile.readLine()) != null) {
                ratePage.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ratePage;

    }

    //  Get Territory/Zip file
    private ArrayList<String> getStateTerritories(String state) {
        BufferedReader zipFile = new BufferedReader(new InputStreamReader(

                getResources().openRawResource(getResources().getIdentifier(
                        "raw/" + RateState.toLowerCase(), "raw", getPackageName()))
        ));

        ArrayList<String> zips = new ArrayList<String>();
        String line = null;

        try {
            while ((line = zipFile.readLine()) != null) {
                zips.add(line);
            }
        } catch (Exception e) {
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
        } else {
            String zip = zipCode.getText().toString();

            if (length == 1) {
                terrNum = String.valueOf(zip);
                chosenCity = "FakeCity";
                chosenCounty = "FakeCounty";
                showTerr.setText(terrNum);
            } else {
                ArrayList<String> zips = getStateTerritories(RateState);
                final HashMap<String, String> territory = new HashMap<String, String>();
                for (String line : zips) {
                    String[] cities = line.split(tabchar);
                    if (cities[0].equals(zip)) {
                        territory.put(cities[1], cities[2]);
                        chosenCity = cities[1];
                        chosenCounty = cities[3];
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
//                            chosenCity = city;
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

                } else if (territory.keySet().isEmpty()) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    for (String value : territory.values()) {
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
        String limitLine = "";

        boolean addCompColl = fullcoverage.isChecked();
        boolean transDiscount = transferDisc.isChecked();
        boolean multiDiscount = multiBikeDisc.isChecked();
        boolean podDiscount = podDisc.isChecked();

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
        totalOffset = ageOffset + ccOffset;
//     helloWorld.setText(totalOffset+" "+intCC+" "+bikeType);
//     helloWorld.setText(totalOffset + "");
        limitLine = ratePage.get(2);
        rateLine = ratePage.get(totalOffset);

        String[] rline = rateLine.split(tabchar);
        String[] limline = limitLine.split(tabchar);
        ArrayList<String> limits = new ArrayList<String>();
        ArrayList<String> rates = new ArrayList<String>();
        int liabrate;
        int compcollrate;
        double totalrate;
        double discountperc;
        int limitPointStart = 4;
        int limitItemCount;

        if ((RateState.equals("NJ")) || ((RateState.equals("PA")) || RateState.equals("DE"))) {
            limitItemCount = 5;
        } else {
            limitItemCount = 4;
        }

        if (RateState.equals("VA")) {
            limitPointStart = 5;
        }

        if (addCompColl) {
            Calendar cal = Calendar.getInstance();
            int thisyear = cal.get(Calendar.YEAR);
            Integer bikeAge = thisyear - Integer.parseInt(year);
            helloWorld.setText(bikeAge.toString());

            int cmpindex = 0;
            if (bikeAge < 2) {
                cmpindex = 1;
            } else {
                if (bikeAge < 4) {
                   cmpindex = 2;
                } else {
                    cmpindex = 3;
                }
            }

            compcollrate = Integer.valueOf(rline[cmpindex]);

        } else {
            compcollrate = 0;
            if (RateState.equals("OH")){
                limitPointStart += limitItemCount;
            }
        }

        for (int i = limitPointStart; i < (limitPointStart+limitItemCount); i++) {
            limits.add(limline[i]);
            String junk = rline[i];
            liabrate = Integer.valueOf(junk);
            totalrate = liabrate + compcollrate;

//          handle discounts here

            if (transDiscount) {
                totalrate = totalrate * 0.9;
            }
            if (multiDiscount) {
                totalrate = totalrate * 0.9;
            }
            if ((podDiscount) && (addCompColl)) {
                if (bikeType.equals("SPORT")) {
                    discountperc = 0.85;
                } else {
                    if ((RateState.equals("PA")) || (RateState.equals("WV")) || (RateState.equals("VA")) || (RateState.equals("MI"))) {
                        discountperc = 0.85;
                    } else {
                        discountperc = 0.75;
                    }

                }
                totalrate = totalrate * discountperc;
            }

            if (totalrate < 60) {
                totalrate = 60;
            }

            int ttotalrate = (int)Math.round(totalrate);
            rates.add(ttotalrate + "");
        }

        displayRates(limits, rates);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        check += 1;
        if (check > 1) {
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

                    for (String line : vehiclesList) {
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
                    for (String line : vehiclesList) {
                        String[] list = line.split(tabchar);
                        if (list[5].equals(year)) {
                            if (list[2].equals(make)) {
                                String model = list[3];
                                if (validModels.contains(model) == false) {
                                    validModels.add(model);
                                    String[] data = {list[1], list[6]};
                                    modelData.put(model, data);
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
                    helloWorld.setText(bikeType);
                    bikeCC = modelData.get(model)[1];
                    break;
                default:
                    break;
            }
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
        if ((birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
                (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH))) {
            age--;

            // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
        } else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)) &&
                (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        return age;
    }


    public void displayRates(ArrayList<String> limits, ArrayList<String> rates) {
        AlertDialog.Builder quote = new AlertDialog.Builder(this);
        quote.setTitle("Save Your Quote?");

        String str1 = String.format("%1$-" + 20 + "s", limits.get(0));
        String str = str1 + "= $" + rates.get(0);

        for (int i = 1; i < limits.size(); i++) {
            int t = 20-i;
            str1 = String.format("%1$-" + t + "s", limits.get(i));
            str += "\n" + str1 + "= $" + rates.get(i);
        }

        quote.setMessage(str);

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

                Fname = (EditText) dialogLayout.findViewById(R.id.first_name);
                Lname = (EditText) dialogLayout.findViewById(R.id.last_name);
                emailaddr = (EditText) dialogLayout.findViewById(R.id.email);

                builder.setView(dialogLayout);
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveFile();
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Saved!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog saveDia = builder.create();
                dialog.dismiss();
                eventText = (EditText) dialogLayout.findViewById(R.id.event);
                if (!eventName.equals("")) {
                    eventText.setText(eventName);
                }
                saveDia.show();
            }
        });

        AlertDialog dialog = quote.create();
        dialog.show();

    }

    public void saveFile() {

        FileOutputStream fos;

        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        eventName = eventText.getText().toString();

        try {

            fos = new FileOutputStream(saveFile, true);
            StringBuilder sb = new StringBuilder();
            sb.append(eventName);
            sb.append(tabchar + RateState);
            String zip = zipCode.getText().toString();
            sb.append(tabchar + zip);
            sb.append(tabchar + chosenCity);
            sb.append(tabchar + chosenCounty);
            sb.append(tabchar + birthdayButton.getText().toString());
            if (male.isChecked()) {
                sb.append(tabchar + "M");
            } else {
                sb.append(tabchar + "F");
            }
            if (married.isChecked()) {
                sb.append(tabchar + "M");
            } else {
                sb.append(tabchar + "S");
            }
            sb.append(tabchar + year);
            sb.append(tabchar + make);
            sb.append(tabchar + model);
            sb.append(tabchar + bikeCC);
            if (fullcoverage.isChecked()) {
                sb.append(tabchar + "Y");
            } else {
                sb.append(tabchar + "N");
            }
            if (transferDisc.isChecked()) {
                sb.append(tabchar + "Y");
            } else {
                sb.append(tabchar + "N");
            }
            if (multiBikeDisc.isChecked()) {
                sb.append(tabchar + "Y");
            } else {
                sb.append(tabchar + "N");
            }
            if (podDisc.isChecked()) {
                sb.append(tabchar + "Y");
            } else {
                sb.append(tabchar + "N");
            }
            String fname = Fname.getText().toString();
            String lname = Lname.getText().toString();
            String emailA = emailaddr.getText().toString();
            sb.append(tabchar + fname);
            sb.append(tabchar + lname);
            sb.append(tabchar + emailA);
            String output = sb.append("\n").toString();
            fos.write(output.getBytes());
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String setEventName() {
        dataFileName = new SimpleDateFormat("yyyyMMdd").format(new Date()).toString();

        try {
            saveFile = new File(MainActivity.this.getBaseContext().getExternalFilesDir(""), dataFileName + ".txt");
            saveFile.setReadable(true);
            saveFile.setWritable(true);
            if (!saveFile.exists()) {
                return "";
            }
            else {
                BufferedReader br = new BufferedReader(new FileReader(saveFile));
                String line = br.readLine();
                helloWorld.setText(line);
                String[] l = new String[2];
                while (line != null) {
                    l = line.split(tabchar);
                    line = br.readLine();
                }
                br.close();
                return l[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void email() {

        File folder = new File(MainActivity.this.getBaseContext().getExternalFilesDir("") + "");

        final File[] filePaths = folder.listFiles();

        dialogDone = false;

        if (filePaths.length == 0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "There were no quote files to send", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        else {

            for (int i = 0; i < filePaths.length; i++) {
                final Intent emailIntent = new Intent(Intent.ACTION_SEND);
//                final Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                        new String[]{"Eventquote@rider.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_CC,
                        new String[]{"mosheroff@rider.com"});
                String subject = filePaths[i].getName().replace(".txt", "").trim();
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Uploaded Quotes From: " + subject);
                subject = subject.substring(4, 6) + " " + subject.substring(6) + ", " + subject.substring(0, 4);
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Attached is the quote file from " + subject + ".");

                Uri u = FileProvider.getUriForFile(MainActivity.this, "com.example.michael.second_test.fileProvider", filePaths[i]);

                emailIntent.putExtra(Intent.EXTRA_STREAM, u);
                emailIntent.setFlags(1); // Uris are readable
                emailIntent.setFlags(2); // Uris are writable
                MainActivity.this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));

            }
        }

            //send(filePaths);

            /*AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Before you email... ");
            builder.setCancelable(false);
            builder.setMessage("Would you like to delete your files after you email them?");
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    del = true;
                    dialog.dismiss();
                    send(filePaths);
                }
            });
            builder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    del = false;
                    dialog.dismiss();
                    send(filePaths);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();*/
   // }

 /*   public void send(File[] filePaths) {

        for (int i = 0; i < filePaths.length; i++) {
            //File fileIn = new File(filePaths[i] + "");
*//*
            if (del) {
                File destFile = null;

                try{

                    String fileName = fileIn.getName().replace("/","").trim();
                    destFile = new File(getBaseContext().getCacheDir(), fileName);
                    destFile.createNewFile();

                    BufferedReader br = new BufferedReader(new FileReader(fileIn));
                    String line;
                    FileOutputStream fos = new FileOutputStream(destFile,true);
                    while ((line = br.readLine()) != null) {
                        fos.write(line.getBytes());
                    }
                    fos.flush();
                    br.close();
                    fos.close();
                }catch(IOException e){
                    e.printStackTrace();
                }

                u = FileProvider.getUriForFile(this, "com.example.michael.second_test.fileprovider", destFile);
                fileIn.delete();
            }
            else {*//*
               // u = FileProvider.getUriForFile(this, "com.example.michael.second_test.fileProvider", fileIn);
    //          }

            final Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                    new String[]{"mosheroff@rider.com"});

            String subject = filePaths[i].getName().replace(".txt","").trim();
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Uploaded Quotes From: " + subject);
            subject = subject.substring(4,6) + " " + subject.substring(6) + ", " + subject.substring(0,4);
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Attached is the rate file from " + subject + ".");

            Uri u = FileProvider.getUriForFile(MainActivity.this, "com.example.michael.second_test.fileProvider", filePaths[i]);

            emailIntent.putExtra(Intent.EXTRA_STREAM, u);
            emailIntent.setFlags(1); // Uris are readable
            emailIntent.setFlags(2); // Uris are writable
            this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        }*/
    }

//  SOAP Call to upload quotes to iPOS
    public void uploadQoute(final String[] quoteLine, final String fileName, final Integer quoteCtr) {

       thread = new Thread() {
           public void run() {
               try {


                   String RIDER_METHOD_NAME = "ComparativeRater";
                   String RIDER_NAMESPACE = "http://www.rider.com/RIDER_QUOTE_RATER";
                   String RIDER_SOAP_ACTION = "http://www.rider.com/RIDER_QUOTE_RATER/ComparativeRater";
                   String RIDER_URL = "https://uat.riderpos.net:444/RiderComparativeRater/services/RiderComparativeRater?wsdl";

                   SoapObject comparativeRaterRequest = new SoapObject(RIDER_NAMESPACE, "RiderComparativeRaterRequest");
                   SoapObject quoteInfo = new SoapObject(RIDER_NAMESPACE, "quoteinfo");

                   PropertyInfo requestID = new PropertyInfo();
                   requestID.setNamespace(RIDER_NAMESPACE);
                   requestID.setType(PropertyInfo.STRING_CLASS);
                   requestID.setName("RequestID");
//                   requestID.setValue("RiderRemote-"+fileName+"-"+quoteCtr.toString());
                   requestID.setValue("OctaneLending-132D-105112.37-174-Q7"+quoteCtr.toString());
                   quoteInfo.addProperty(requestID);
                   PropertyInfo requestType = new PropertyInfo();
                   requestType.setNamespace(RIDER_NAMESPACE);
                   requestType.setType(PropertyInfo.STRING_CLASS);
                   requestType.setName("RequestType");
                   requestType.setValue("Q");
                   quoteInfo.addProperty(requestType);
                   PropertyInfo howHearRider = new PropertyInfo();
                   howHearRider.setNamespace(RIDER_NAMESPACE);
                   howHearRider.setName("How_Hear_Rider");
                   howHearRider.setType(PropertyInfo.STRING_CLASS);
                   howHearRider.setValue("A05");
                   quoteInfo.addProperty(howHearRider);
                   PropertyInfo subline = new PropertyInfo();
                   subline.setNamespace(RIDER_NAMESPACE);
                   subline.setName("subline");
                   subline.setType(PropertyInfo.STRING_CLASS);
//                   subline.setValue("MD");
                   subline.setValue(quoteLine[1]);
                   quoteInfo.addProperty(subline);
                   PropertyInfo policyEffectiveDate = new PropertyInfo();
                   policyEffectiveDate.setNamespace(RIDER_NAMESPACE);
                   policyEffectiveDate.setName("policyEffectiveDate");
                   policyEffectiveDate.setType(PropertyInfo.STRING_CLASS);
//                 need to fix date
                   policyEffectiveDate.setValue("2015-04-20");
                   quoteInfo.addProperty(policyEffectiveDate);
                   PropertyInfo sourceField = new PropertyInfo();
                   sourceField.setNamespace(RIDER_NAMESPACE);
                   sourceField.setName("Source_Field");
                   sourceField.setType(PropertyInfo.STRING_CLASS);
                   sourceField.setValue("Octane Lending");
                   quoteInfo.addProperty(sourceField);
                   PropertyInfo agentID = new PropertyInfo();
                   agentID.setNamespace(RIDER_NAMESPACE);
                   agentID.setName("Agent_ID");
                   agentID.setType(PropertyInfo.STRING_CLASS);
                   agentID.setValue("");
                   quoteInfo.addProperty(agentID);
                   PropertyInfo subAgentID = new PropertyInfo();
                   subAgentID.setNamespace(RIDER_NAMESPACE);
                   subAgentID.setName("Sub_Agent_ID");
                   subAgentID.setType(PropertyInfo.STRING_CLASS);
                   subAgentID.setValue("1142");
                   quoteInfo.addProperty(subAgentID);
                   comparativeRaterRequest.addSoapObject(quoteInfo);

                   SoapObject driver = new SoapObject(RIDER_NAMESPACE, "driver");
                   PropertyInfo insuredmailID = new PropertyInfo();
                   insuredmailID.setNamespace(RIDER_NAMESPACE);
                   insuredmailID.setName("insuredmailID");
                   insuredmailID.setType(PropertyInfo.STRING_CLASS);
//                   insuredmailID.setValue("mosheroff@rider.com");
                   insuredmailID.setValue(quoteLine[18]);
                   driver.addProperty(insuredmailID);
                   PropertyInfo firstName = new PropertyInfo();
                   firstName.setNamespace(RIDER_NAMESPACE);
                   firstName.setName("FirstName");
                   firstName.setType(PropertyInfo.STRING_CLASS);
//                   firstName.setValue("MichaelTest");
                   firstName.setValue(quoteLine[16]);
                   driver.addProperty(firstName);
                   PropertyInfo lastName = new PropertyInfo();
                   lastName.setNamespace(RIDER_NAMESPACE);
                   lastName.setName("LastName");
                   lastName.setType(PropertyInfo.STRING_CLASS);
//                   lastName.setValue("OsheroffTest");
                   lastName.setValue(quoteLine[17]);
                   driver.addProperty(lastName);
                   PropertyInfo suffix = new PropertyInfo();
                   suffix.setNamespace(RIDER_NAMESPACE);
                   suffix.setName("Suffix");
                   suffix.setType(PropertyInfo.STRING_CLASS);
                   suffix.setValue("");
                   driver.addProperty(suffix);
                   PropertyInfo insuredDOB = new PropertyInfo();
                   insuredDOB.setNamespace(RIDER_NAMESPACE);
                   insuredDOB.setName("InsuredDOB");
                   insuredDOB.setType(PropertyInfo.STRING_CLASS);
//                 need to fix date of birth
                   insuredDOB.setValue("1961-07-15");
                   driver.addProperty(insuredDOB);
                   PropertyInfo maritalStatus = new PropertyInfo();
                   maritalStatus.setNamespace(RIDER_NAMESPACE);
                   maritalStatus.setName("MaritalStatus");
                   maritalStatus.setType(PropertyInfo.STRING_CLASS);
//                   maritalStatus.setValue("M");
                   maritalStatus.setValue(quoteLine[7]);
                   driver.addProperty(maritalStatus);
                   PropertyInfo gender = new PropertyInfo();
                   gender.setNamespace(RIDER_NAMESPACE);
                   gender.setName("Gender");
                   gender.setType(PropertyInfo.STRING_CLASS);
//                   gender.setValue("M");
                   gender.setValue(quoteLine[6]);
                   driver.addProperty(gender);
                   PropertyInfo zip = new PropertyInfo();
                   zip.setNamespace(RIDER_NAMESPACE);
                   zip.setName("Zip");
                   zip.setType(PropertyInfo.STRING_CLASS);
//                   zip.setValue("20759");
                   zip.setValue(quoteLine[2]);
                   driver.addProperty(zip);
                   PropertyInfo city = new PropertyInfo();
                   city.setNamespace(RIDER_NAMESPACE);
                   city.setName("City");
                   city.setType(PropertyInfo.STRING_CLASS);
//                   city.setValue("FULTON");
                   city.setValue(quoteLine[3]);
                   driver.addProperty(city);
                   PropertyInfo county = new PropertyInfo();
                   county.setNamespace(RIDER_NAMESPACE);
                   county.setType(PropertyInfo.STRING_CLASS);
                   county.setName("County");
//                   county.setValue("HOWARD");
                   county.setValue(quoteLine[4]);
                   driver.addProperty(county);
                   PropertyInfo state = new PropertyInfo();
                   state.setNamespace(RIDER_NAMESPACE);
                   state.setType(PropertyInfo.STRING_CLASS);
                   state.setName("State");
//                   state.setValue("MD");
                   state.setValue(quoteLine[1]);
                   driver.addProperty(state);
                   PropertyInfo existingPolicyHolder = new PropertyInfo();
                   existingPolicyHolder.setNamespace(RIDER_NAMESPACE);
                   existingPolicyHolder.setName("ExistingPolicyHolder");
                   existingPolicyHolder.setType(PropertyInfo.STRING_CLASS);
                   existingPolicyHolder.setValue("False");
                   driver.addProperty(existingPolicyHolder);
                   SoapObject license = new SoapObject(RIDER_NAMESPACE, "License");
                   PropertyInfo licensestate = new PropertyInfo();
                   licensestate.setNamespace(RIDER_NAMESPACE);
                   licensestate.setType(PropertyInfo.STRING_CLASS);
                   licensestate.setName("LicenseState");
//                   licensestate.setValue("");
                   licensestate.setValue(quoteLine[1]);
                   license.addProperty(licensestate);
                   PropertyInfo licensestatus = new PropertyInfo();
                   licensestatus.setNamespace(RIDER_NAMESPACE);
                   licensestatus.setType(PropertyInfo.STRING_CLASS);
                   licensestatus.setName("LicenseStatus");
                   licensestatus.setValue("");
                   license.addProperty(licensestatus);
                   driver.addSoapObject(license);
                   PropertyInfo additionalOperator = new PropertyInfo();
                   additionalOperator.setNamespace((RIDER_NAMESPACE));
                   additionalOperator.setName("AdditionalOperator");
                   additionalOperator.setType(PropertyInfo.STRING_CLASS);
                   additionalOperator.setValue("N");
                   driver.addProperty(additionalOperator);
                   PropertyInfo validMCLicense = new PropertyInfo();
                   validMCLicense.setNamespace(RIDER_NAMESPACE);
                   validMCLicense.setName("ValidMotorcycleLicense");
                   validMCLicense.setType(PropertyInfo.STRING_CLASS);
                   validMCLicense.setValue("Y");
                   driver.addProperty(validMCLicense);
                   PropertyInfo validMCPermit = new PropertyInfo();
                   validMCPermit.setNamespace(RIDER_NAMESPACE);
                   validMCPermit.setName("ValidMotorcyclePermit");
                   validMCPermit.setType(PropertyInfo.STRING_CLASS);
                   validMCPermit.setValue("Y");
                   driver.addProperty(validMCPermit);
                   PropertyInfo numOfViolations = new PropertyInfo();
                   numOfViolations.setNamespace(RIDER_NAMESPACE);
                   numOfViolations.setName("NumberOfViolations");
                   numOfViolations.setType(PropertyInfo.STRING_CLASS);
//                   numOfViolations.setValue("0");
                   if (quoteLine[15] == "Y") {
                       numOfViolations.setValue("0");
                   } else {
                       numOfViolations.setValue("2");
                   }
                   driver.addProperty(numOfViolations);
                   PropertyInfo numOfAccidents = new PropertyInfo();
                   numOfAccidents.setNamespace(RIDER_NAMESPACE);
                   numOfAccidents.setName("NumberOfAccidents");
                   numOfAccidents.setType(PropertyInfo.STRING_CLASS);
                   numOfAccidents.setValue("0");
                   driver.addProperty(numOfAccidents);
                   PropertyInfo defDriverDisc = new PropertyInfo();
                   defDriverDisc.setNamespace(RIDER_NAMESPACE);
                   defDriverDisc.setName("DDCD");
                   defDriverDisc.setType(PropertyInfo.STRING_CLASS);
                   defDriverDisc.setValue("");
                   driver.addProperty(defDriverDisc);
                   PropertyInfo mopedLicense = new PropertyInfo();
                   mopedLicense.setNamespace(RIDER_NAMESPACE);
                   mopedLicense.setName("Valid_Moped_License");
                   mopedLicense.setType(PropertyInfo.STRING_CLASS);
                   mopedLicense.setValue("");
                   driver.addProperty(mopedLicense);
                   PropertyInfo mopedPermit = new PropertyInfo();
                   mopedPermit.setNamespace(RIDER_NAMESPACE);
                   mopedPermit.setName("Valid_Moped_Permit");
                   mopedPermit.setType(PropertyInfo.STRING_CLASS);
                   mopedPermit.setValue("");
                   driver.addProperty(mopedPermit);
                   comparativeRaterRequest.addSoapObject(driver);

                   SoapObject vehicle = new SoapObject(RIDER_NAMESPACE, "vehicle");
                   PropertyInfo vin = new PropertyInfo();
                   vin.setNamespace(RIDER_NAMESPACE);
                   vin.setName("VIN");
                   vin.setType(PropertyInfo.STRING_CLASS);
                   vin.setValue("");
                   vehicle.addProperty(vin);
                   PropertyInfo year = new PropertyInfo();
                   year.setNamespace(RIDER_NAMESPACE);
                   year.setName("Year");
                   year.setType(PropertyInfo.STRING_CLASS);
//                   year.setValue("2008");
                   year.setValue(quoteLine[8]);
                   vehicle.addProperty(year);
                   PropertyInfo make = new PropertyInfo();
                   make.setNamespace(RIDER_NAMESPACE);
                   make.setName("Make");
                   make.setType(PropertyInfo.STRING_CLASS);
//                   make.setValue("HARLEY-DAVIDSON");
                   make.setValue(quoteLine[9]);
                   vehicle.addProperty(make);
                   PropertyInfo model = new PropertyInfo();
                   model.setNamespace(RIDER_NAMESPACE);
                   model.setName("Model");
                   model.setType(PropertyInfo.STRING_CLASS);
//                   model.setValue("FXDL");
                   model.setValue(quoteLine[10]);
                   vehicle.addProperty(model);
                   PropertyInfo cc = new PropertyInfo();
                   cc.setNamespace(RIDER_NAMESPACE);
                   cc.setName("CC");
                   cc.setType(PropertyInfo.STRING_CLASS);
//                   cc.setValue("1584");
                   cc.setValue(quoteLine[11]);
                   vehicle.addProperty(cc);
                   PropertyInfo ftcc = new PropertyInfo();
                   ftcc.setNamespace(RIDER_NAMESPACE);
                   ftcc.setName("Ftcc");
                   ftcc.setType(PropertyInfo.STRING_CLASS);
//                 need to adjust this when we get full coverage field loaded in.
                   ftcc.setValue("N");
                   vehicle.addProperty(ftcc);
                   PropertyInfo trike = new PropertyInfo();
                   trike.setNamespace(RIDER_NAMESPACE);
                   trike.setName("Trike");
                   trike.setType(PropertyInfo.STRING_CLASS);
                   trike.setValue("N");
                   vehicle.addProperty(trike);
                   PropertyInfo currentlyInsured = new PropertyInfo();
                   currentlyInsured.setNamespace(RIDER_NAMESPACE);
                   currentlyInsured.setName("CycleCurrentlyInsured");
                   currentlyInsured.setType(PropertyInfo.STRING_CLASS);
                   currentlyInsured.setValue("N");
                   vehicle.addProperty(currentlyInsured);
                   PropertyInfo assignedDriver = new PropertyInfo();
                   assignedDriver.setNamespace(RIDER_NAMESPACE);
                   assignedDriver.setName("AssignedDriver");
                   assignedDriver.setType(PropertyInfo.STRING_CLASS);
                   assignedDriver.setValue("1");
                   vehicle.addProperty(assignedDriver);
                   PropertyInfo onRoadUse = new PropertyInfo();
                   onRoadUse.setNamespace(RIDER_NAMESPACE);
                   onRoadUse.setName("on_road_use");
                   onRoadUse.setType(PropertyInfo.STRING_CLASS);
                   onRoadUse.setValue("Y");
                   vehicle.addProperty(onRoadUse);
                   PropertyInfo intendOnRoadUse = new PropertyInfo();
                   intendOnRoadUse.setNamespace(RIDER_NAMESPACE);
                   intendOnRoadUse.setName("intend_to_on_road_use");
                   intendOnRoadUse.setType(PropertyInfo.STRING_CLASS);
                   intendOnRoadUse.setValue("Y");
                   vehicle.addProperty(intendOnRoadUse);
                   SoapObject coverage1 = new SoapObject(RIDER_NAMESPACE, "Coverage");
                   PropertyInfo coverageName1 = new PropertyInfo();
                   coverageName1.setNamespace(RIDER_NAMESPACE);
                   coverageName1.setName("CoverageName");
                   coverageName1.setType(PropertyInfo.STRING_CLASS);
                   coverageName1.setValue("BI");
                   coverage1.addProperty(coverageName1);
                   PropertyInfo limit1 = new PropertyInfo();
                   limit1.setNamespace(RIDER_NAMESPACE);
                   limit1.setName("limit");
                   limit1.setType(PropertyInfo.STRING_CLASS);
                   limit1.setValue("30-60");
                   coverage1.addProperty(limit1);
                   PropertyInfo deductible1 = new PropertyInfo();
                   deductible1.setNamespace(RIDER_NAMESPACE);
                   deductible1.setName("Deductible");
                   deductible1.setType(PropertyInfo.STRING_CLASS);
                   deductible1.setValue("N/A");
                   coverage1.addProperty(deductible1);
                   vehicle.addSoapObject(coverage1);
                   SoapObject coverage2 = new SoapObject(RIDER_NAMESPACE, "Coverage");
                   PropertyInfo coverageName2 = new PropertyInfo();
                   coverageName2.setNamespace(RIDER_NAMESPACE);
                   coverageName2.setName("CoverageName");
                   coverageName2.setType(PropertyInfo.STRING_CLASS);
                   coverageName2.setValue("PD");
                   coverage2.addProperty(coverageName2);
                   PropertyInfo limit2 = new PropertyInfo();
                   limit2.setNamespace(RIDER_NAMESPACE);
                   limit2.setName("limit");
                   limit2.setType(PropertyInfo.STRING_CLASS);
                   limit2.setValue("15");
                   coverage2.addProperty(limit2);
                   PropertyInfo deductible2 = new PropertyInfo();
                   deductible2.setNamespace(RIDER_NAMESPACE);
                   deductible2.setName("Deductible");
                   deductible2.setType(PropertyInfo.STRING_CLASS);
                   deductible2.setValue("N/A");
                   coverage2.addProperty(deductible2);
                   vehicle.addSoapObject(coverage2);
                   SoapObject coverage3 = new SoapObject(RIDER_NAMESPACE, "Coverage");
                   PropertyInfo coverageName3 = new PropertyInfo();
                   coverageName3.setNamespace(RIDER_NAMESPACE);
                   coverageName3.setName("CoverageName");
                   coverageName3.setType(PropertyInfo.STRING_CLASS);
                   coverageName3.setValue("UMBI/UIMBI");
                   coverage3.addProperty(coverageName3);
                   PropertyInfo limit3 = new PropertyInfo();
                   limit3.setNamespace(RIDER_NAMESPACE);
                   limit3.setName("limit");
                   limit3.setType(PropertyInfo.STRING_CLASS);
                   limit3.setValue("30-60");
                   coverage3.addProperty(limit3);
                   PropertyInfo deductible3 = new PropertyInfo();
                   deductible3.setNamespace(RIDER_NAMESPACE);
                   deductible3.setName("Deductible");
                   deductible3.setType(PropertyInfo.STRING_CLASS);
                   deductible3.setValue("N/A");
                   coverage3.addProperty(deductible3);
                   vehicle.addSoapObject(coverage3);
                   SoapObject coverage4 = new SoapObject(RIDER_NAMESPACE, "Coverage");
                   PropertyInfo coverageName4 = new PropertyInfo();
                   coverageName4.setNamespace(RIDER_NAMESPACE);
                   coverageName4.setName("CoverageName");
                   coverageName4.setType(PropertyInfo.STRING_CLASS);
                   coverageName4.setValue("UMPD/UIMPD");
                   coverage4.addProperty(coverageName4);
                   PropertyInfo limit4 = new PropertyInfo();
                   limit4.setNamespace(RIDER_NAMESPACE);
                   limit4.setName("limit");
                   limit4.setType(PropertyInfo.STRING_CLASS);
                   limit4.setValue("15");
                   coverage4.addProperty(limit4);
                   PropertyInfo deductible4 = new PropertyInfo();
                   deductible4.setNamespace(RIDER_NAMESPACE);
                   deductible4.setName("Deductible");
                   deductible4.setType(PropertyInfo.STRING_CLASS);
                   deductible4.setValue("N/A");
                   coverage4.addProperty(deductible4);
                   vehicle.addSoapObject(coverage4);

                   comparativeRaterRequest.addSoapObject(vehicle);

                   SoapSerializationEnvelope riderenvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

                   HttpTransportSE riderQuoteTransport = new HttpTransportSE(RIDER_URL);
                   riderQuoteTransport.debug = true;

                   riderenvelope.dotNet = true;
                   riderenvelope.implicitTypes = true;
                   riderenvelope.setAddAdornments(false);
                   riderenvelope.encodingStyle = SoapSerializationEnvelope.XSD;
                   riderenvelope.setOutputSoapObject(comparativeRaterRequest);

                   try

                   {
                       riderQuoteTransport.call(RIDER_SOAP_ACTION, riderenvelope);
                       SoapPrimitive riderresponse = (SoapPrimitive) riderenvelope.getResponse();

                       String requestDump = riderQuoteTransport.requestDump;
                       String responseDump = riderQuoteTransport.responseDump;

                   } catch (Exception e1){
                       String requestDump = riderQuoteTransport.requestDump;
                       String responseDump = riderQuoteTransport.responseDump;
                       e1.printStackTrace();
                   }

               }

               catch(Exception e){
                   e.printStackTrace();
               }

               final Runnable createUI = new Runnable() {
                   public void run() {
                   // do something here
                   helloWorld.setText("Uploading "+quoteCtr.toString());
                   }
               };
               handler.post(createUI);
           }
       };
       thread.start();
    }
//  end of SOAP Call

}
