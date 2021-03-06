package mobile.computing.expressstore;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class HomeActivity extends AppCompatActivity {

    public static final int REQUEST=1;

    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;

    private FloatingActionButton shop;
    private FloatingActionButton manuallyAdd;


    private TextView noOrders;

    private SharedPreferences prefs, prefs2, prefs3;

    private List<String> dates;
    private List<Items> itemsList;
    private LinkedHashMap<String, List<Items>> orderDetails;

    private static String url="https://expressstorecsci.000webhostapp.com/api.php";

    private RequestQueue requestQueue;
    private String cust_id ;
    ArrayList<String> storeList;

    LocationManager locationManager;
    String l1, l2;
    public String inputCode = "";
    String tut = "";

    public ScannerScreen manualInput;

    @Override
    public void onBackPressed() {
        super.onBackPressed();Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStoreData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getStoreData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Get cust_id from user_login activity;
        prefs = getApplicationContext().getSharedPreferences("mobile.computing.expressstore", Context.MODE_PRIVATE);
        prefs2 = getApplicationContext().getSharedPreferences("userdata", Context.MODE_PRIVATE);
        prefs3 = getApplicationContext().getSharedPreferences("intro", Context.MODE_PRIVATE);
        cust_id = prefs2.getString("customerID","NA");
        tut = prefs3.getString("tutorial","0");

        requestPermissions(new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST);
        askPermission();

        if(tut.equals("0")) {

            new MaterialTapTargetPrompt.Builder(HomeActivity.this)
                    .setTarget(R.id.prevOrders)
                    .setPrimaryText("Track your orders")
                    .setSecondaryText("A list will display your previous orders made via ExpressStore.")
                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                        @Override
                        public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                            if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                                // User has pressed the prompt target
                                new MaterialTapTargetPrompt.Builder(HomeActivity.this)
                                        .setTarget(R.id.startShop)
                                        .setPrimaryText("Start Scanning Barcode")
                                        .setSecondaryText("Tap this button to scan the barcode of the products to be purchased.")
                                        .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                                            @Override
                                            public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                                                if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                                                    // User has pressed the prompt target
                                                    new MaterialTapTargetPrompt.Builder(HomeActivity.this)
                                                            .setTarget(R.id.startScan)
                                                            .setPrimaryText("Enter Barcode Manually")
                                                            .setSecondaryText("Tap this button to enter the barcode of the products manually.")
                                                            .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                                                                @Override
                                                                public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                                                                    if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                                                                        // User has pressed the prompt target
                                                            new MaterialTapTargetPrompt.Builder(HomeActivity.this)
                                                                    .setTarget(R.id.btn_cart)
                                                                    .setPrimaryText("Your Cart")
                                                                    .setSecondaryText("Tap this button to see the items that you have scanned.")
                                                                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                                                                        @Override
                                                                        public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                                                                            if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                                                                                // User has pressed the prompt target
                                                                                new MaterialTapTargetPrompt.Builder(HomeActivity.this)
                                                                                        .setTarget(R.id.btn_settings)
                                                                                        .setPrimaryText("Settings")
                                                                                        .setSecondaryText("Tap this button to access the settings and Edit Profile option.")
                                                                                        .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                                                                                            @Override
                                                                                            public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                                                                                                if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                                                                                                    // User has pressed the prompt target
                                                                                                    prefs3.edit().putString("tutorial", "1").apply();
                                                                                                    tut = "1";
                                                                                                }
                                                                                            }
                                                                                        })
                                                                                        .show();

                                                                            }
                                                                        }
                                                                    })
                                                                    .show();
                                                        }
                                                    }
                                                }).show();

                                                }
                                            }
                                        })
                                        .show();
                            }
                        }
                    })
                    .show();
        }


        expandableListView = findViewById(R.id.order1);
        noOrders = findViewById(R.id.noOrders);

        shop = findViewById(R.id.startShop);
        manuallyAdd = findViewById(R.id.startScan);

        manualInput = new ScannerScreen();

        requestQueue = Volley.newRequestQueue(this);
        storeList = new ArrayList<>();

        getStoreData();
        getStoreData();

        if(cust_id.equals("NA")){
            startActivity(new Intent(HomeActivity.this,LoginActivity.class));
        }

        getData(cust_id);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener(){
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {

            }
        });

        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,ScannerScreen.class));
            }
        });

        manuallyAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Manual Item Addition")
                        .setMessage("Please type the barcode manually")
                        .setCancelable(false);

                final EditText codeInput = new EditText(HomeActivity.this);
                codeInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                alertBuilder.setView(codeInput);
                alertBuilder.setPositiveButton("Add Item", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inputCode = codeInput.getText().toString();
                        manualInput.fetchProductDetails(inputCode, HomeActivity.this);
                    }
                });
                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertBuilder.show();

            }
        });

    }

    private void getStoreData() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            getGPS();
        }
        else
        {
            if(!cust_id.equals("NA")) {
                String location;
                location = getLocation();
                getStoreList(location);
                getSuperStoreList(location);
                try {
                    Toasty.success(getApplicationContext(), storeList.get(0), Toast.LENGTH_SHORT, true).show();
                    SharedPreferences.Editor editor = prefs.edit();

                    int store_id;

                    if(storeList.get(0).contains("Atlantic")){
                        store_id=1;
                    }else if(storeList.get(0).contains("Walmart")){
                        store_id=2;
                    }else{
                        store_id=3;
                    }

                    editor.putString("store_name", storeList.get(0));
                    editor.putString("store_id", store_id+"");
                    editor.apply();
                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                storeList.clear();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.action_menu, menu);
        MenuItem item = menu.findItem(R.id.btn_home);
        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){

            case R.id.btn_cart:
                startActivity(new Intent(HomeActivity.this,CartActivity.class));
                break;
            case R.id.btn_settings:
                startActivity(new Intent(HomeActivity.this,SettingsActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    /*
    fetches customer order details from api, stores it in a Linked Hash Map and sets the adapter
    of expandableListView for display
     */
    private void getData(final String userID){
        StringRequest stringRequest=new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {

            String oldDate="";
            String prev_order_id="";
            int flag=0;
            @Override
            public void onResponse(String response) {
                try {
                    orderDetails=new LinkedHashMap<>();
                    JSONArray orders=new JSONArray(response);
                    for(int i=0;i<orders.length();i++){
                        JSONObject order=orders.getJSONObject(i);
                        String order_id=order.getString("orderId");
                        String date=order.getString("date");

                        //if the orderId is same, then the item is added to the same list
                        if(order_id.equals(prev_order_id)) {

                            String prod_name = order.getString("name");
                            String imgUrl = order.getString("imgUrl");
                            int price = order.getInt("price");
                            String priceS = "$" + price;
                            System.out.println("if  or " + imgUrl + "  " + prod_name);
                            Items item = new Items(prod_name, priceS, imgUrl);
                            itemsList.add(item);
                        }
                        else
                        {
                            itemsList = new ArrayList<>();
                            flag+=1;
                            oldDate=date.concat(" "+flag);
                            String prod_name = order.getString("name");
                            String imgUrl = order.getString("imgUrl");
                            int price = order.getInt("price");
                            String priceS = "$" + price;
                            System.out.println("else  or " + imgUrl + "  " + prod_name);
                            Items item = new Items(prod_name, priceS, imgUrl);
                            itemsList.add(item);

                        }
                        System.out.println("date"+date);
                        System.out.println("oldDate"+oldDate);
                        orderDetails.put(oldDate,itemsList);
                        prev_order_id=order_id;

                    }
                    if(orderDetails.isEmpty())
                    {
                        noOrders.setVisibility(View.VISIBLE);
                    }

                    dates = new ArrayList<>(orderDetails.keySet());
                    expandableListAdapter = new ExpandableListAdapter(HomeActivity.this, dates, orderDetails);
                    expandableListView.setAdapter(expandableListAdapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams()
            {
                Map<String,String> params = new HashMap<>();
                params.put("custId",userID);
                return params;
            }
        };

        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


    public void askPermission()
    {
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST);
        }
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.CAMERA}, REQUEST);
        }
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST);
        }
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST);
        }
    }

    private void getStoreList(String location)
    {
        String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+
                location+"&key=AIzaSyDxGET3hUuzDlURuZgWa2_YN3XUtTE5vBc&type=store&rankby=distance";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray responseArray = response.getJSONArray("results");

                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject storeItem = responseArray.getJSONObject(i);
                                String storeName = storeItem.getString("name").toLowerCase();
                                if(storeName.contains("atlantic superstore") || storeName.contains("sobeys"))
                                {
                                    storeList.add(storeList.size(), storeItem.getString("name"));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toasty.error(getApplicationContext(), ""+error.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        }
        );
        requestQueue.add(request);
    }

    private void getSuperStoreList(String location)
    {
        String URL1 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+
                location+"&key=AIzaSyDxGET3hUuzDlURuZgWa2_YN3XUtTE5vBc&type=department_store&rankby=distance";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, URL1, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray responseArray = response.getJSONArray("results");

                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject storeItem = responseArray.getJSONObject(i);
                                String storeName = storeItem.getString("name").toLowerCase();
                                if(storeName.contains("walmart")) {
                                    storeList.add(storeList.size(), storeItem.getString("name"));
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toasty.error(getApplicationContext(), ""+error.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        }
        );
        requestQueue.add(request);
    }

    private String getLocation() {
        String location = "";

        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST);
        }
        else {
            Location locateGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location locationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if(locateGPS != null)
            {
                double lat = locateGPS.getLatitude();
                double lon = locateGPS.getLongitude();

                location = String.valueOf(lat)+","+ String.valueOf(lon);
            }
            else if(locationNetwork != null)
            {
                double lat = locationNetwork.getLatitude();
                double lon = locationNetwork.getLongitude();

                location = String.valueOf(lat)+","+ String.valueOf(lon);
            }
            else if(locationPassive != null)
            {
                double lat = locationPassive.getLatitude();
                double lon = locationPassive.getLongitude();

                location = String.valueOf(lat)+","+ String.valueOf(lon);
            }
            else
            {
                Toasty.error(this, "can't Get Your Location", Toast.LENGTH_SHORT, true).show();
            }

        }
        return location;

    }

    private void getGPS() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
