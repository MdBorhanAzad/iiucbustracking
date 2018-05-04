package com.example.fazlulhoque.iiucbustracking.Driver;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.fazlulhoque.iiucbustracking.Common.Common;
import com.example.fazlulhoque.iiucbustracking.Home;
import com.example.fazlulhoque.iiucbustracking.R;
import com.example.fazlulhoque.iiucbustracking.Student.MapDistance;
import com.example.fazlulhoque.iiucbustracking.Student.StudentMapActivity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverMapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,OnMapReadyCallback, android.location.LocationListener,GoogleMap.OnInfoWindowClickListener{

    private DrawerLayout mDrawerlayout;
    private ActionBarDrawerToggle mToggle;

    private GoogleMap mMap;

    private Location mLastLocation;
    private LocationManager mLocationManager;
    public static final int REQUEST_LOCATION_CODE = 99;
    private Button mLogout,btnPoke;
    private String customerId = "";
    private Boolean isLoggingout = false;

    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private static final int RP_ACCESS_LOCATION = 1;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    private static final long MIN_TIME_BW_UPDATES = 500;
    private String getName;
    private List<LatLng> latLngs;
    HashMap<String, LatLng> hashMap;
    private String pokeCondition="";
    private String pokeCondition11="";
    MarkerOptions[] pokeMarker;


    private String newStudentId="6ytt777t77t3456";
    ArrayList<String> allPokeID;
    String driverUndoPoke=" ";
    List<Marker> markerWithColors;
   String logidforstudent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        pokeMarker=new MarkerOptions[5];
      //  btnPoke=(Button)findViewById(R.id.btnPoke);
        /*btnPoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DriverMapsActivity.this,DriverPokeList.class);
                startActivity(intent);
                finish();
                return;
            }
        });*/


            getName=getIntent().getExtras().getString("gender");

        allPokeID=new ArrayList<>();

        getLocation();
        getAssignedPickupLocation();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void signOut() {
        isLoggingout=true;
        disconnectDriver();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(DriverMapsActivity.this, Home.class);
        startActivity(intent);
        finish();
        return;
    }

   /* private void getAssignedPickupLocation() {
        DatabaseReference getAssignedPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("studentRequest");
        getAssignedPickupLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mMap.clear();
                    for (DataSnapshot eachcustomers : dataSnapshot.getChildren()) {


                        String driverId = eachcustomers.getKey();
                        List<Object> map = (List<Object>) eachcustomers.child("l").getValue();
                        double LocationLat = 0;
                        double LocationLng = 0;

                        if (map.get(0) != null) {
                            LocationLat = Double.parseDouble(map.get(0).toString());
                        }
                        if (map.get(1) != null) {
                            LocationLng = Double.parseDouble(map.get(1).toString());
                        }
                        LatLng studentLatLng = new LatLng(LocationLat, LocationLng);
                        //   hashMap.put(driverId,studentLatLng);
                        mMap.addMarker(new MarkerOptions().position(new LatLng(studentLatLng.latitude, studentLatLng.longitude)).title(driverId));


                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }*/


    private String realtype;
    private void getAssignedPickupLocation()  {
        DatabaseReference getAssignedPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("studentRequest");
        getAssignedPickupLocationRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mMap.clear();

                    for (final DataSnapshot eachcustomers : dataSnapshot.getChildren()) {
                        final String studentid = eachcustomers.getKey();
                         logidforstudent=eachcustomers.getKey();
                      //  String driverid=FirebaseAuth.getInstance().getCurrentUser().getUid();

                        DatabaseReference studenttype = FirebaseDatabase.getInstance().getReference("studentRequest").child(studentid).child("type");


                        studenttype.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    realtype=dataSnapshot.getValue(String.class);
                                    //  Log.d("realtype","realtypefind"+ realtype);

                                    if(getName.equals(realtype))
                                    {
                                        DatabaseReference undopoke=FirebaseDatabase.getInstance().getReference("undopoke").child(studentid);
                                        undopoke.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists())
                                                {
                                                    List<Object> map = (List<Object>) eachcustomers.child("l").getValue();
                                                    double LocationLat = 0;
                                                    double LocationLng = 0;

                                                    if (map.get(0) != null) {
                                                        LocationLat = Double.parseDouble(map.get(0).toString());
                                                    }
                                                    if (map.get(1) != null) {
                                                        LocationLng = Double.parseDouble(map.get(1).toString());
                                                    }
                                                    LatLng customerLatLng = new LatLng(LocationLat, LocationLng);

                                                    mMap.addMarker(new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).snippet(studentid).title("undopoke").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                                    //  pokeMarker[1]=new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).title(customerId).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                                                }


                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                        final String driverid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        final DatabaseReference pokeref=FirebaseDatabase.getInstance().getReference("poke").child(driverid).child(studentid);
                                        pokeref.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists())
                                                {


                                                    Log.d("markerDebugPKR","student id"+ studentid);
                                                    if (allPokeID.contains(studentid)){
                                                        // Toast.makeText(DriverMapsActivity.this, "Id in Array", Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        allPokeID.add(studentid);
                                                        //allPokeID.add(driverid);
                                                        // Toast.makeText(DriverMapsActivity.this, "Id not in Array", Toast.LENGTH_SHORT).show();


                                                        AlertDialog.Builder notifyPoke= new AlertDialog.Builder(DriverMapsActivity.this);
                                                        notifyPoke.setTitle("Is there space in bus?");
                                                        notifyPoke.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                DatabaseReference replyRef= pokeref.child("Reply");
                                                                replyRef.setValue("A");

                                                                dialogInterface.dismiss();
                                                                List<Object> map = (List<Object>) eachcustomers.child("l").getValue();
                                                                double LocationLat = 0;
                                                                double LocationLng = 0;

                                                                if (map.get(0) != null) {
                                                                    LocationLat = Double.parseDouble(map.get(0).toString());
                                                                }
                                                                if (map.get(1) != null) {
                                                                    LocationLng = Double.parseDouble(map.get(1).toString());
                                                                }
                                                                LatLng customerLatLng = new LatLng(LocationLat, LocationLng);
                                                                //  hashMap.put(driverId,customerLatLng);
                                                                pokeCondition="A";
                                                                // pokeMarker[0] =new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).title(customerId).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                                                mMap.addMarker(new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).title(studentid).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                                            }
                                                        });
                                                        notifyPoke.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                DatabaseReference replyRef= pokeref.child("Reply");
                                                                replyRef.setValue("D");
                                                                //pokeCondition="D";

                                                                dialogInterface.dismiss();
                                                                List<Object> map = (List<Object>) eachcustomers.child("l").getValue();
                                                                double LocationLat = 0;
                                                                double LocationLng = 0;

                                                                if (map.get(0) != null) {
                                                                    LocationLat = Double.parseDouble(map.get(0).toString());
                                                                }
                                                                if (map.get(1) != null) {
                                                                    LocationLng = Double.parseDouble(map.get(1).toString());
                                                                }
                                                                LatLng customerLatLng = new LatLng(LocationLat, LocationLng);
                                                                pokeCondition="D";
                                                                mMap.addMarker(new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).snippet(studentid).title("undopoke").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).visible(false));
                                                                //  pokeMarker[1]=new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).title(customerId).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                                                            }
                                                        });
                                                        notifyPoke.show();
                                                    }

                                     /*  AlertDialog alert=notifyPoke.create();
                                          alert.show();
                                                    String a[]={studentid.toString()};
                                                   newStudentId=studentid;*/

                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                    }



                                }


                                //  Log.d("realtype",realtype);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        //

                        Log.d("realtypefound","realtypefound"+ realtype);
                        List<Object> map = (List<Object>) eachcustomers.child("l").getValue();
                        double LocationLat = 0;
                        double LocationLng = 0;

                        if (map.get(0) != null) {
                            LocationLat = Double.parseDouble(map.get(0).toString());
                        }
                        if (map.get(1) != null) {
                            LocationLng = Double.parseDouble(map.get(1).toString());
                        }
                        // LatLng studentLatLng = new LatLng(LocationLat, LocationLng);
                        final LatLng customerLatLng = new LatLng(LocationLat, LocationLng);



                        if(allPokeID.contains(studentid))
                        {

                            if (pokeCondition.equals("A")){
                                // mMap.addMarker(pokeMarker[0]);


                                mMap.addMarker(new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).snippet(studentid).title("undopoke").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            }
                            if (pokeCondition.equals("D")){
                                //  mMap.addMarker(pokeMarker[1]);


                              //  mMap.addMarker(new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).snippet(studentid).title("undopoke").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                            }
                            //else
                            //  mMap.addMarker(new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).snippet(studentid).title("undopoke").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));



                        }
                        else {


                            mMap.addMarker(new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).snippet(studentid).title("undopoke").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        }

                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }







    /*private String realtype;
    private void getAssignedPickupLocation()  {
        DatabaseReference getAssignedPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("studentRequest");
        getAssignedPickupLocationRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mMap.clear();

                    for (final DataSnapshot eachcustomers : dataSnapshot.getChildren()) {
                        final String studentid = eachcustomers.getKey();
                        String driverid=FirebaseAuth.getInstance().getCurrentUser().getUid();

                        DatabaseReference studenttype = FirebaseDatabase.getInstance().getReference("studentRequest").child(studentid).child("type");


                        studenttype.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    realtype=dataSnapshot.getValue(String.class);
                                  //  Log.d("realtype","realtypefind"+ realtype);

                                    if(getName.equals(realtype))
                                    {


                                                    final String driverid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                    final DatabaseReference pokeref=FirebaseDatabase.getInstance().getReference("poke").child(driverid).child(studentid);
                                                    pokeref.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(final DataSnapshot dataSnapshot) {
                                                            if(dataSnapshot.exists())
                                                            {


                                                                Log.d("markerDebugPKR","student id"+ studentid);
                                                                if (allPokeID.contains(studentid)){
                                                                    // Toast.makeText(DriverMapsActivity.this, "Id in Array", Toast.LENGTH_SHORT).show();
                                                                }else{
                                                                    allPokeID.add(studentid);
                                                                    //allPokeID.add(driverid);
                                                                    // Toast.makeText(DriverMapsActivity.this, "Id not in Array", Toast.LENGTH_SHORT).show();


                                                                    AlertDialog.Builder notifyPoke= new AlertDialog.Builder(DriverMapsActivity.this);
                                                                    notifyPoke.setTitle("Is there space in bus?");
                                                                    notifyPoke.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                            DatabaseReference replyRef= pokeref.child("Reply");
                                                                            replyRef.setValue("A");

                                                                            dialogInterface.dismiss();
                                                                            List<Object> map = (List<Object>) eachcustomers.child("l").getValue();
                                                                            double LocationLat = 0;
                                                                            double LocationLng = 0;

                                                                            if (map.get(0) != null) {
                                                                                LocationLat = Double.parseDouble(map.get(0).toString());
                                                                            }
                                                                            if (map.get(1) != null) {
                                                                                LocationLng = Double.parseDouble(map.get(1).toString());
                                                                            }
                                                                            LatLng customerLatLng = new LatLng(LocationLat, LocationLng);
                                                                            //  hashMap.put(driverId,customerLatLng);
                                                                           pokeCondition="A";
                                                                            // pokeMarker[0] =new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).title(customerId).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                                                             mMap.addMarker(new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).title(studentid).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                                                        }
                                                                    });
                                                                    notifyPoke.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                            DatabaseReference replyRef= pokeref.child("Reply");
                                                                            replyRef.setValue("D");
                                                                            //pokeCondition="D";

                                                                            dialogInterface.dismiss();
                                                                            List<Object> map = (List<Object>) eachcustomers.child("l").getValue();
                                                                            double LocationLat = 0;
                                                                            double LocationLng = 0;

                                                                            if (map.get(0) != null) {
                                                                                LocationLat = Double.parseDouble(map.get(0).toString());
                                                                            }
                                                                            if (map.get(1) != null) {
                                                                                LocationLng = Double.parseDouble(map.get(1).toString());
                                                                            }
                                                                            LatLng customerLatLng = new LatLng(LocationLat, LocationLng);
                                                                            pokeCondition="D";
                                                                            mMap.addMarker(new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).snippet(studentid).title("undopoke").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                                                          //  pokeMarker[1]=new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).title(customerId).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                                                                        }
                                                                    });
                                                                    notifyPoke.show();
                                                                }

                                     *//*  AlertDialog alert=notifyPoke.create();
                                          alert.show();
                                                    String a[]={studentid.toString()};
                                                   newStudentId=studentid;*//*

                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });


                                    }



                                }


                                //  Log.d("realtype",realtype);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        //

                        Log.d("realtypefound","realtypefound"+ realtype);
                        List<Object> map = (List<Object>) eachcustomers.child("l").getValue();
                        double LocationLat = 0;
                        double LocationLng = 0;

                        if (map.get(0) != null) {
                            LocationLat = Double.parseDouble(map.get(0).toString());
                        }
                        if (map.get(1) != null) {
                            LocationLng = Double.parseDouble(map.get(1).toString());
                        }
                        // LatLng studentLatLng = new LatLng(LocationLat, LocationLng);
                        final LatLng customerLatLng = new LatLng(LocationLat, LocationLng);



                        if(allPokeID.contains(studentid))
                        {

                            if (pokeCondition.equals("A")){
                               // mMap.addMarker(pokeMarker[0]);


                                mMap.addMarker(new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).snippet(studentid).title("undopoke").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                           }
                          else if (pokeCondition.equals("D")){
                              //  mMap.addMarker(pokeMarker[1]);


                                mMap.addMarker(new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).snippet(studentid).title("undopoke").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                            }
                         else
                               mMap.addMarker(new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).snippet(studentid).title("undopoke").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));



                        }
                        else {


                            mMap.addMarker(new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).snippet(studentid).title("undopoke").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        }

                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }*/



   /* private String realtype;
    private void getAssignedPickupLocation()  {




        DatabaseReference getAssignedPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("studentRequest");
        getAssignedPickupLocationRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mMap.clear();

                    for (final DataSnapshot eachcustomers : dataSnapshot.getChildren()) {
                        final String studentid = eachcustomers.getKey();
                        final String driverid=FirebaseAuth.getInstance().getCurrentUser().getUid();

                        DatabaseReference studenttype = FirebaseDatabase.getInstance().getReference("studentRequest").child(studentid).child("type");


                        studenttype.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    realtype=dataSnapshot.getValue(String.class);
                                    //  Log.d("realtype","realtypefind"+ realtype);

                                    if(getName.equals(realtype))
                                    {
                                       DatabaseReference undopoke=FirebaseDatabase.getInstance().getReference("undopoke").child(studentid);
                                        undopoke.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists())
                                                {
                                                    List<Object> map = (List<Object>) eachcustomers.child("l").getValue();
                                                    double LocationLat = 0;
                                                    double LocationLng = 0;

                                                    if (map.get(0) != null) {
                                                        LocationLat = Double.parseDouble(map.get(0).toString());
                                                    }
                                                    if (map.get(1) != null) {
                                                        LocationLng = Double.parseDouble(map.get(1).toString());
                                                    }
                                                    LatLng customerLatLng = new LatLng(LocationLat, LocationLng);

                                                    mMap.addMarker(new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).snippet(studentid).title("undopoke").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                                    //  pokeMarker[1]=new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).title(customerId).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                                                }


                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                        final String driverid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        final DatabaseReference pokeref=FirebaseDatabase.getInstance().getReference("poke").child(driverid).child(studentid);
                                        pokeref.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists())
                                                {


                                                    Log.d("markerDebugPKR","student id"+ studentid);
                                                    if (allPokeID.contains(studentid)){
                                                        // Toast.makeText(DriverMapsActivity.this, "Id in Array", Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        allPokeID.add(studentid);
                                                        //allPokeID.add(driverid);
                                                        // Toast.makeText(DriverMapsActivity.this, "Id not in Array", Toast.LENGTH_SHORT).show();


                                                        AlertDialog.Builder notifyPoke= new AlertDialog.Builder(DriverMapsActivity.this);
                                                        notifyPoke.setTitle("Is there space in bus?");
                                                        notifyPoke.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                DatabaseReference replyRef= pokeref.child("Reply");
                                                                replyRef.setValue("A");

                                                                dialogInterface.dismiss();
                                                                List<Object> map = (List<Object>) eachcustomers.child("l").getValue();
                                                                double LocationLat = 0;
                                                                double LocationLng = 0;

                                                                if (map.get(0) != null) {
                                                                    LocationLat = Double.parseDouble(map.get(0).toString());
                                                                }
                                                                if (map.get(1) != null) {
                                                                    LocationLng = Double.parseDouble(map.get(1).toString());
                                                                }
                                                                LatLng customerLatLng = new LatLng(LocationLat, LocationLng);
                                                                //  hashMap.put(driverId,customerLatLng);
                                                                pokeCondition="A";
                                                                // pokeMarker[0] =new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).title(customerId).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                                                mMap.addMarker(new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).title(studentid).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                                            }
                                                        });
                                                        notifyPoke.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                DatabaseReference replyRef= pokeref.child("Reply");
                                                                replyRef.setValue("D");
                                                                //pokeCondition="D";

                                                                dialogInterface.dismiss();
                                                                List<Object> map = (List<Object>) eachcustomers.child("l").getValue();
                                                                double LocationLat = 0;
                                                                double LocationLng = 0;

                                                                if (map.get(0) != null) {
                                                                    LocationLat = Double.parseDouble(map.get(0).toString());
                                                                }
                                                                if (map.get(1) != null) {
                                                                    LocationLng = Double.parseDouble(map.get(1).toString());
                                                                }
                                                                LatLng customerLatLng = new LatLng(LocationLat, LocationLng);
                                                                pokeCondition="D";
                                                                mMap.addMarker(new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).snippet(studentid).title("undopoke").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                                                //  pokeMarker[1]=new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).title(customerId).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                                                            }
                                                        });
                                                        notifyPoke.show();
                                                    }

                                     *//*  AlertDialog alert=notifyPoke.create();
                                          alert.show();
                                                    String a[]={studentid.toString()};
                                                   newStudentId=studentid;*//*

                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                    }



                                }


                                //  Log.d("realtype",realtype);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        //

                        Log.d("realtypefound","realtypefound"+ realtype);
                        List<Object> map = (List<Object>) eachcustomers.child("l").getValue();
                        double LocationLat = 0;
                        double LocationLng = 0;

                        if (map.get(0) != null) {
                            LocationLat = Double.parseDouble(map.get(0).toString());
                        }
                        if (map.get(1) != null) {
                            LocationLng = Double.parseDouble(map.get(1).toString());
                        }
                        // LatLng studentLatLng = new LatLng(LocationLat, LocationLng);
                        final LatLng customerLatLng = new LatLng(LocationLat, LocationLng);



                        if(allPokeID.contains(studentid))
                        {

                            if (pokeCondition.equals("A")){
                                // mMap.addMarker(pokeMarker[0]);


                                mMap.addMarker(new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).snippet(studentid).title("undopoke").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            }
                             if (pokeCondition.equals("D")){
                                //  mMap.addMarker(pokeMarker[1]);


                                mMap.addMarker(new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).snippet(studentid).title("undopoke").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                            }
                            //else
                              //  mMap.addMarker(new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).snippet(studentid).title("undopoke").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));



                        }
                        else {


                            mMap.addMarker(new MarkerOptions().position(new LatLng(customerLatLng.latitude, customerLatLng.longitude)).snippet(studentid).title("undopoke").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        }

                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

*/





    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            mMap.setOnInfoWindowClickListener(this);
        }
        mMap.setOnInfoWindowClickListener(this);


    }



    public boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;
        } else
            return true;

    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d("locUpdate", String.valueOf(location.getLatitude()));
        mLastLocation = location;
      /*  DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driverAvailable");
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(driverId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        getAssignedPickupLocation();*/
      //  getAssignedPickupLocation();
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriverAvailable");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(driverId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));


        if(getName.equals("male"))
        {
            ref.child(driverId).child("type").setValue(getName);

           // MarkerOptions markerOptions = new MarkerOptions();

            //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        }

        else if(getName.equals("female"))
        {

            ref.child(driverId).child("type").setValue(getName);

            //MarkerOptions markerOptions = new MarkerOptions();
           // markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        }
        else if(getName.equals("teacher"))
        {

            ref.child(driverId).child("type").setValue(getName);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        }





        /*LatLng driverLocation = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(driverLocation).title("hello students");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bus));
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(driverLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));*/
        /*Marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)).position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude())));
       */ /*LatLng pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(pickupLocation).title("Pickup Here");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pickupLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));*/
    }




    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    public void getLocation() {
        mLocationManager = (LocationManager) DriverMapsActivity.this.getSystemService(LOCATION_SERVICE);
        isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location location;

        if (!isGPSEnabled && !isNetworkEnabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DriverMapsActivity.this);
            alertDialog.setTitle("GPS Settings");
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    DriverMapsActivity.this.startActivity(intent);
                }
            });

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            alertDialog.show();
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                        ActivityCompat.shouldShowRequestPermissionRationale(this,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    String[] perm = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
                    ActivityCompat.requestPermissions(this, perm,
                            RP_ACCESS_LOCATION);
                } else {
                    String[] perm = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
                    ActivityCompat.requestPermissions(this, perm,
                            RP_ACCESS_LOCATION);
                }
            } else {
                if (isNetworkEnabled) {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, (android.location.LocationListener) DriverMapsActivity.this);

                    if (mLocationManager != null) {
                        location = mLocationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (mLastLocation == null) {
                        mLocationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, (android.location.LocationListener) DriverMapsActivity.this);

                        if (mLocationManager != null) {
                            location = mLocationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                onLocationChanged(location);
                            }
                        }
                    }
                }
            }
        }
    }

    private void disconnectDriver() {


        if (ActivityCompat.checkSelfPermission(DriverMapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DriverMapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else{
            mLocationManager.removeUpdates(DriverMapsActivity.this);

        }

        mLocationManager.removeUpdates(DriverMapsActivity.this);
        String  userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
       // DatabaseReference ref= FirebaseDatabase.getInstance().getReference("DriverAvailable");
       /* GeoFire geoFire1=new GeoFire(ref);
        geoFire1.removeLocation(userId);*/
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("DriverAvailable").child(userId);
       // DatabaseReference ref1=FirebaseDatabase.getInstance().getReference("poke").child(userId);
        //DatabaseReference ref2=FirebaseDatabase.getInstance().getReference("undopoke").child(driverUndoPoke).child(userId);
        //ref2.removeValue();
        //ref1.removeValue();
        ref.removeValue();

    }
    @Override
    protected void onStop() {
        super.onStop();
        if(!isLoggingout)
        {
            disconnectDriver();
        }


    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driver_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_passchange) {

            showDialogChangePwd();

        } else if (id == R.id.nav_logout) {
            signOut();

        } else if (id == R.id.nav_help) {

        }

        mDrawerlayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerlayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showDialogChangePwd() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("CHANGE PASSWORD");
        dialog.setMessage("Please fill all information");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_pwd = inflater.inflate(R.layout.layout_change_password,null);

        final MaterialEditText edtOldPassword = layout_pwd.findViewById(R.id.edtOldPassword);
        final MaterialEditText edtNewPassword = layout_pwd.findViewById(R.id.edtNewPassword);
        final MaterialEditText edtRepeatPassword = layout_pwd.findViewById(R.id.edtRepeatPassword);

        dialog.setView(layout_pwd);

        dialog.setPositiveButton("CHANGE PASSWORD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final AlertDialog waitingDialog = new SpotsDialog(DriverMapsActivity.this);

                if(edtNewPassword.getText().toString().equals(edtRepeatPassword.getText().toString()))
                {
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                    AuthCredential credential = EmailAuthProvider.getCredential(email,edtOldPassword.getText().toString());

                    FirebaseAuth.getInstance().getCurrentUser().
                            reauthenticate(credential).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        FirebaseAuth.getInstance().getCurrentUser().
                                                updatePassword(edtRepeatPassword.getText().toString()).
                                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            //update driver password column
                                                            Map<String,Object> password = new HashMap<>();

                                                            password.put("password",edtRepeatPassword.getText().toString());

                                                            DatabaseReference driverInformation = FirebaseDatabase.getInstance().getReference(Common.user_driver_tbl);

                                                            driverInformation.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(password)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if(task.isSuccessful())
                                                                                Toast.makeText(DriverMapsActivity.this, "Password was changed", Toast.LENGTH_SHORT).show();
                                                                            else
                                                                                Toast.makeText(DriverMapsActivity.this, "Password was changed but not update in Driver Information", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            Toast.makeText(DriverMapsActivity.this, "Password Doesn`t Change", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                    else
                                    {
                                        waitingDialog.dismiss();
                                        Toast.makeText(DriverMapsActivity.this, "Wrong Old Password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    waitingDialog.dismiss();
                    Toast.makeText(DriverMapsActivity.this, "Password doesn`t match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        //show dialog
        dialog.show();
    }



    @Override
    public void onInfoWindowClick(Marker marker) {
        driverUndoPoke = marker.getSnippet();
         createUndoPoke();

    }

    public void createUndoPoke() {
        String  userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("undopoke").child(driverUndoPoke);

        GeoFire geoFire=new GeoFire(ref);
        geoFire.setLocation(userId,new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()));

        //geoFire.setLocation(userId,new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()));


    }
}
