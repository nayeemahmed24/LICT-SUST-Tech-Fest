package com.example.raihan.sharefoods;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raihan.sharefoods.Objects.Profile_Object;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.raihan.sharefoods.AppClient.Base_URL;

public class Show_details_donate_post extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FoodRequestObject request;
    TextView location,foodForPerson,volunteerNeeded,donatorName,donatorConatct;
    Button submit;
    ArrayList<LatLng> markerPoints;
    String donatorLocation="Amborkhana",volunteerLocation = "IICT SUST";  /////////////// User(Here volunteer) location from Mainactivity.myprofile object

    Address address;
    LatLng volunteer_latLng,Donator_latLng;
    List<Address> distances = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details_donate_post);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Gson gson = new Gson();
        request = gson.fromJson(getIntent().getStringExtra("myjson"),FoodRequestObject.class);
        Toast.makeText(Show_details_donate_post.this,request.getLocation(),Toast.LENGTH_LONG).show();
/////////////////////
      //  request is Foodrequestobject

        location = findViewById(R.id.Donatorlocation);
        foodForPerson = findViewById(R.id.FoodForPerson);
        volunteerNeeded = findViewById(R.id.volunteerNeeded);
        donatorName = findViewById(R.id.DonatorName);
        donatorConatct = findViewById(R.id.DonatorPhoneNo);
        submit = findViewById(R.id.VolunteerSubmit);


        Profile_Object p = MainActivity.fullProfile.get(request.getDonator()-1);
        Toast.makeText(Show_details_donate_post.this,p.getPhoneNumber(),Toast.LENGTH_SHORT).show();
        donatorLocation = request.getLocation().trim();
        volunteerLocation = MainActivity.myprofile.getAddress();
        location.setText(request.getLocation().trim());
        foodForPerson.setText(request.getQuantity().toString().trim()+"");
        volunteerNeeded.setText(request.getQuantity()/4+"");
        donatorName.setText(MainActivity.fullProfile.get(request.getDonator()).getUser().getUsername());


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request.setFoodStatus("PRO");
                Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Base_URL).addConverterFactory(GsonConverterFactory.create());
                Retrofit retrofit = builder.build();
                IApi_Vinfo iApi_vinfo = retrofit.create(IApi_Vinfo.class);
                Call<FoodRequestObject> call = iApi_vinfo.createFoodRequest(request);

                call.enqueue(new Callback<FoodRequestObject>() {
                    @Override
                    public void onResponse(Call<FoodRequestObject> call, Response<FoodRequestObject> response) {
                        Toast.makeText(Show_details_donate_post.this,response.body().toString(),Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onFailure(Call<FoodRequestObject> call, Throwable t) {

                        Toast.makeText(Show_details_donate_post.this,"Failed",Toast.LENGTH_LONG).show();

                    }
                });
            }
        });

    }

    void getLocation()
    {
        Geocoder geocoder = new Geocoder(Show_details_donate_post.this);
        List<Address> addressList = null;
        MarkerOptions markerOptions = new MarkerOptions();


        try{
            addressList = geocoder.getFromLocationName(volunteerLocation,1);

            if(addressList!=null)
            {
                for (int i=0;i<addressList.size();i++)
                {
                    address = addressList.get(i);

                    volunteer_latLng = new LatLng(address.getLatitude(),address.getLongitude());

                    markerOptions.position(volunteer_latLng);
                    markerOptions.title(volunteerLocation);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));




                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(volunteer_latLng,10));



                }



            }

        }catch (Exception e){}

        addressList.clear();


        try{
            addressList = geocoder.getFromLocationName(donatorLocation,1);

            if(addressList!=null)
            {
                for (int i=0;i<addressList.size();i++)
                {
                    address = addressList.get(i);

                    Donator_latLng = new LatLng(address.getLatitude(),address.getLongitude());

                    markerOptions.position(Donator_latLng);
                    markerOptions.title(donatorLocation);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));




                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Donator_latLng,10));



                }



            }

        }catch (Exception e){}

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getLocation();

    }
}
