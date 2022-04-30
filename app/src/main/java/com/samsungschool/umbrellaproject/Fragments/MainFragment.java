package com.samsungschool.umbrellaproject.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.samsungschool.umbrellaproject.databinding.FragmentMainBinding;
import com.samsungschool.umbrellaproject.userPoint;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;

public class MainFragment extends Fragment{

//    private static final String TAG = "lllll";
//    private static final double DESIRED_ACCURACY = 0;
//    private static final long MINIMAL_TIME = 10;
//    private static final double MINIMAL_DISTANCE = 0.1;
//    private static final boolean USE_IN_BACKGROUND = true;
//    public static final float COMFORTABLE_ZOOM_LEVEL = 18.0f;
//    private final String MAPKIT_API_KEY = "";
//    private MapView mapView;
//    private CoordinatorLayout rootCoordinatorLayout;
//    private LocationManager locationManager;
//    private LocationListener myLocationListener;
//    private Point myLocation;

    private FragmentMainBinding binding;
    private MapView mapview;
    private onNavBtnClickListener navClickListener;
    private UserLocationLayer userLocationLayer;

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        MapKitFactory.initialize(this.getContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            navClickListener = (onNavBtnClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(getLayoutInflater());
        binding.navBtn.setOnClickListener(v -> navClickListener.onClick());
        binding.gpsFindPositionBtn.setOnClickListener(v ->{
            if(userLocationLayer.cameraPosition() != null){
                mapview.getMap().move(
                        new CameraPosition(userLocationLayer.cameraPosition().getTarget(), 14.0f, 0.0f, 0.0f),
                        new Animation(Animation.Type.SMOOTH, 1),
                        null);
            }
        });

        mapview = binding.mapView;
        mapview.getMap().move(new CameraPosition(new Point(0, 0), 14, 0, 0));

        userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapview.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setAutoZoomEnabled(false);
        userLocationLayer.setObjectListener(new userPoint(this.getContext(), userLocationLayer, mapview));



// разработка кастомного serLocationPoint
//        locationManager = MapKitFactory.getInstance().createLocationManager();
//        myLocationListener = new LocationListener() {
//
//            @Override
//            public void onLocationUpdated(Location location) {
//                if (myLocation == null) {
//                    mapview.getMap().move(
//                            new CameraPosition(location.getPosition(), 18.0f, 0.0f, 0.0f),
//                            new Animation(Animation.Type.SMOOTH, 1),
//                            null);
//                }
//                myLocation = location.getPosition(); //this user point
//                Log.w(TAG, "my location - " + myLocation.getLatitude() + "," + myLocation.getLongitude());
//                Log.w(TAG, "my heding - " + location.getHeading());
//            }
//
//            @Override
//            public void onLocationStatusUpdated(LocationStatus locationStatus) {
//                if (locationStatus == LocationStatus.NOT_AVAILABLE) {
//                    System.out.println(LocationStatus.values());
//                }
//            }
//        };

        return binding.getRoot();
    }


    @Override
    public void onStart() {
        super.onStart();
        mapview.onStart();
        MapKitFactory.getInstance().onStart();
       // subscribeToLocationUpdate();
    }

    @Override public void onStop() {
        super.onStop();
        MapKitFactory.getInstance().onStop();
        //locationManager.unsubscribe(myLocationListener);
        mapview.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }



    public interface onNavBtnClickListener {
        void onClick();
    }



//    private void subscribeToLocationUpdate() {
//        if (locationManager != null && myLocationListener != null) {
//            locationManager.subscribeForLocationUpdates(DESIRED_ACCURACY, MINIMAL_TIME, MINIMAL_DISTANCE, USE_IN_BACKGROUND, FilteringMode.ON, myLocationListener);
//        }
//    }

    private void moveCamera(Point point, float zoom) {
        mapview.getMap().move(
                new CameraPosition(point, zoom, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
    }
}
