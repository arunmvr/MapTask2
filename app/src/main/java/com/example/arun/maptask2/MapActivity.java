package com.example.arun.maptask2;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;

/**
 * Created by Arun on 4/18/2016.
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mGoogleMap;
    LatLng origin;
    LatLng destination;
    double lat_silk_board = 12.9592;
    double lng_silk_board = 77.6974;
    double lat_marathahalli = 12.9172;
    double lng_marathahalli = 77.6227;
    Boolean isDirectionDrawn = false;
    int choice;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        new LongOperation().execute("");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        Intent intent = this.getIntent();
        choice = intent.getIntExtra("Choice", 0);
        if (choice == 0){
            origin = new LatLng(lat_silk_board, lng_silk_board);
            destination = new LatLng(lat_marathahalli, lng_marathahalli);
        }else if(choice==1){
            destination = new LatLng(lat_silk_board, lng_silk_board);
            origin = new LatLng(lat_marathahalli, lng_marathahalli);
        }

        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);

        new LongOperation().execute("");
    }

    private void zoomToPoints() {
        try {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            //        for (Marker marker : markers) {
            builder.include(origin);
            builder.include(destination);
            //        }
            LatLngBounds bounds = builder.build();

            int padding = 50; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            mGoogleMap.animateCamera(cu);
        } catch (Exception e) {
            ///possible error:
            /// java.lang.NullPointerException: Attempt to invoke interface method 'org.w3c.dom.NodeList org.w3c.dom.Document.getElementsByTagName(java.lang.String)' on a null object reference
        }
    }

    private class LongOperation extends AsyncTask<String, Void, PolylineOptions> {

        private PolylineOptions getDirection() {
            try {
                DirectionHelperClass md = new DirectionHelperClass();

                Document doc = md.getDocument(origin, destination,
                        DirectionHelperClass.MODE_DRIVING);

                ArrayList<LatLng> directionPoint = md.getDirection(doc);
                PolylineOptions rectLine = new PolylineOptions();
                if (choice == 0){
                    rectLine = new PolylineOptions().width(9).color(
                            Color.RED);
                }else if(choice == 1){
                    rectLine = new PolylineOptions().width(9).color(
                            Color.BLUE);
                }
                //PolylineOptions rectLine = new PolylineOptions().width(9).color(
                        //Color.GREEN);
                for (int i = 0; i < directionPoint.size(); i++) {
                    rectLine.add(directionPoint.get(i));
                }
                isDirectionDrawn = true;

                return rectLine;
            } catch (Exception e) {
                ///possible error:
                ///java.lang.IllegalStateException: Error using newLatLngBounds(LatLngBounds, int): Map size can't be 0. Most likely, layout has not yet occured for the map view.  Either wait until layout has occurred or use newLatLngBounds(LatLngBounds, int, int, int) which allows you to specify the map's dimensions.
                return null;
            }

        }

        @Override
        protected PolylineOptions doInBackground(String... params) {
            PolylineOptions polylineOptions = null;
            try {
                polylineOptions = getDirection();
            } catch (Exception e) {
                Thread.interrupted();
            }
            return polylineOptions;
        }

        @Override
        protected void onPostExecute(PolylineOptions result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
            mGoogleMap.clear();///TODO: clean the path only.

            if(result != null){
                mGoogleMap.addPolyline(result);
                mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat_silk_board,lng_silk_board))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .visible(true)
                        .title("Marathahalli"));
                mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat_marathahalli,lng_marathahalli))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .title("SilkBoard"));
                //map.moveCamera(CameraUpdateFactory.newLatLng(origin));
                //map.animateCamera(CameraUpdateFactory.zoomTo(15));

            }
            zoomToPoints();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
    }