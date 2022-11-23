package com.example.markerinfowindowapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.markerinfowindowapp.model.ListLocationModel;
import com.example.markerinfowindowapp.model.LocationModel;
import com.example.markerinfowindowapp.network.ApiClient;
import com.example.markerinfowindowapp.network.ApiService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.markerinfowindowapp.databinding.ActivityMapsBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private List<LocationModel> mListMarker = new ArrayList<>();

    //for infowindow
    String URLString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
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

        getAllDataLocation();
    }

    private void getAllDataLocation() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Waiting...");
        progressDialog.show();

        ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
        Call<ListLocationModel> call = apiService.getAllLocation();
        call.enqueue(new Callback<ListLocationModel>() {
            @Override
            public void onResponse(Call<ListLocationModel> call, Response<ListLocationModel> response) {
                progressDialog.dismiss();
                assert response.body() != null;
                mListMarker = response.body().getmData();
                initMarker(mListMarker);
            }

            @Override
            public void onFailure(Call<ListLocationModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MapsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initMarker(List<LocationModel> mListMarker) {
        for (int i = 0; i < mListMarker.size(); i++) {
            LatLng location = new LatLng(Double.parseDouble(mListMarker.get(i).getLatitude()),
                    Double.parseDouble(mListMarker.get(i).getLongitude()));

            Marker marker = mMap.addMarker(new MarkerOptions().position(location)
                    .title(mListMarker.get(i)
                            .getCity())
                    .snippet(mListMarker.get(i).getUrl()));

            LocationModel info = new LocationModel();
            info.setUrl(mListMarker.get(i).getUrl());

            marker.setTag(info);

            LatLng latLng = new LatLng(Double.parseDouble(mListMarker.get(0).getLatitude()),
                    Double.parseDouble(mListMarker.get(0).getLongitude()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), 11.0f));


            if (mListMarker.size() != 0) {
                TestInfoWindowAdapter testInfoWindowAdapter = new TestInfoWindowAdapter(this);
                mMap.setInfoWindowAdapter(testInfoWindowAdapter);
            }

        }
    }

    private class TestInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

        private Context context;

        public TestInfoWindowAdapter(Context context) {
            this.context = context;
        }

        @Nullable
        @Override
        public View getInfoContents(@NonNull Marker marker) {
            return null;
        }

        @Nullable
        @Override
        public View getInfoWindow(@NonNull Marker marker) {
            View view = ((Activity)context).getLayoutInflater().inflate(R.layout.info_popup, null);
            TextView city = view.findViewById(R.id.mCityname);
            ImageView imageView = view.findViewById(R.id.image);
            city.setText(marker.getTitle());

            LocationModel infomodel = (LocationModel) marker.getTag();
            URLString = infomodel.getUrl();

            Picasso.get()
                    .load(URLString)
                    .error(R.mipmap.ic_launcher)
                    .into(imageView, new MarkerCallBack(marker));

            return view;
        }
    }

    private class MarkerCallBack implements com.squareup.picasso.Callback {
        Marker marker = null;
        
        @Override
        public void onSuccess() {
            if(marker != null && marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
                marker.showInfoWindow();
            }
        }

        @Override
        public void onError(Exception e) {
            Toast.makeText(MapsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        public MarkerCallBack(Marker marker) {
            this.marker = marker;
        }
    }
}