package com.example.mohgggdraw;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private List entrants;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return rootView;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng edmontonMeow = new LatLng(53.5232,-113.5263);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(edmontonMeow,10));
        googleMap.addMarker(new MarkerOptions()
                .position(edmontonMeow)
                .title("Marker"));

        // Example data retrieval from database:
        //List<Entrant> entrants = WaitinglistDB.getEntrantsForEvent(eventId);
        Log.d("sad;fasd","asdfas");
//        for (Entrant entrant : entrants) {
//            LatLng location = new LatLng(entrant.getLatitude(), entrant.getLongitude());
//            mMap.addMarker(new MarkerOptions().position(location).title(entrant.getName()));
//        }

        // Move camera to the first entrant
//        if (!entrants.isEmpty()) {
//            LatLng firstLocation = new LatLng(entrants.get(0).getLatitude(), entrants.get(0).getLongitude());
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10));
//        }
    }
}