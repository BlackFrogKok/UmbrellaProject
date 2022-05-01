package com.samsungschool.umbrellaproject.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.samsungschool.umbrellaproject.Activity.MainActivity;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.databinding.FragmentMainBinding;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.location.FilteringMode;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.util.Arrays;


public class MainFragment extends Fragment {

    private static final String TAG = "lllll";
    private static final double DESIRED_ACCURACY = 1;
    private static final long MINIMAL_TIME = 1000;
    private static final double MINIMAL_DISTANCE = 0.1;
    private static final boolean USE_IN_BACKGROUND = true;
    public static final float COMFORTABLE_ZOOM_LEVEL = 18.0f;
    private final String MAPKIT_API_KEY = "";
    private LocationManager locationManager;
    private LocationListener myLocationListener;
    private Point myLocation;
    private PlacemarkMapObject Now_Geoposition;

    private FragmentMainBinding binding;
    private MapView mapview;
    private onNavBtnClickListener navClickListener;
    private Context context;
    private SensorManager manager;

    private int mAzimuth = 0; // degree


    private Sensor mGravity;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    boolean haveGravity = false;
    boolean haveAccelerometer = false;
    boolean haveMagnetometer = false;

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
        this.context = context;
        super.onAttach(context);
        try {
            navClickListener = (onNavBtnClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(getLayoutInflater());
        binding.navBtn.setOnClickListener(v -> navClickListener.onClick());
        MainActivity activity = (MainActivity) getActivity();
        manager = activity.getManager();
        binding.gpsFindPositionBtn.setOnClickListener(v ->{
            if(myLocation != null){
                mapview.getMap().move(
                        new CameraPosition(myLocation, 17.0f, 0.0f, 0.0f),
                        new Animation(Animation.Type.SMOOTH, 1),
                        null);
            }
        });

        mapview = binding.mapView;
        mapview.getMap().setNightModeEnabled(false);
        mapview.getMap().move(new CameraPosition(new Point(0, 0), 14, 0, 0));
// разработка кастомного serLocationPoint
        locationManager = MapKitFactory.getInstance().createLocationManager();
        myLocationListener = new LocationListener() {
            @Override
            public void onLocationUpdated(@NonNull Location location) {
                if (myLocation == null) {
                    mapview.getMap().move(
                            new CameraPosition(location.getPosition(), 17.0f, 0.0f, 0.0f),
                            new Animation(Animation.Type.SMOOTH, 1),
                            null);
                    Now_Geoposition = mapview.getMap().getMapObjects().addPlacemark(location.getPosition());
                    Now_Geoposition.useCompositeIcon().setIcon("pin", ImageProvider.fromBitmap(getBitmap(R.drawable.ic_user_navi_pin)), new IconStyle().setRotationType(RotationType.ROTATE));
                    Now_Geoposition.useCompositeIcon().setIcon("icon", ImageProvider.fromBitmap(getBitmap(R.drawable.ic_user_marker)), new IconStyle().setRotationType(RotationType.ROTATE));
                }
                myLocation = location.getPosition(); //this user point
                Now_Geoposition.setGeometry(myLocation);

            }

            @Override
            public void onLocationStatusUpdated(@NonNull LocationStatus locationStatus) {
                if (locationStatus == LocationStatus.NOT_AVAILABLE) {
                    System.out.println(Arrays.toString(LocationStatus.values()));
                }
            }
        };


        mGravity = manager.getDefaultSensor( Sensor.TYPE_GRAVITY );
        haveGravity = manager.registerListener( mSensorEventListener, mGravity, SensorManager.SENSOR_DELAY_GAME );

        mAccelerometer = manager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
        haveAccelerometer = manager.registerListener( mSensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME );

        mMagnetometer = manager.getDefaultSensor( Sensor.TYPE_MAGNETIC_FIELD );
        haveMagnetometer = manager.registerListener( mSensorEventListener, mMagnetometer, SensorManager.SENSOR_DELAY_GAME );

        // if there is a gravity sensor we do not need the accelerometer
        if(haveGravity)
            manager.unregisterListener(mSensorEventListener, mAccelerometer );
        if ( ( haveGravity || haveAccelerometer ) && haveMagnetometer ) {
            // ready to go
        } else {
            // unregister and stop
        }
        return binding.getRoot();
    }


    @Override
    public void onStart() {
        super.onStart();
        mapview.onStart();
        MapKitFactory.getInstance().onStart();
         subscribeToLocationUpdate();
    }


    @Override public void onStop() {
        super.onStop();
        MapKitFactory.getInstance().onStop();
        locationManager.unsubscribe(myLocationListener);
        mapview.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public interface onNavBtnClickListener {
        void onClick();
    }



    private void subscribeToLocationUpdate() {
        if (locationManager != null && myLocationListener != null) {
            locationManager.subscribeForLocationUpdates(DESIRED_ACCURACY, MINIMAL_TIME, MINIMAL_DISTANCE, USE_IN_BACKGROUND, FilteringMode.OFF, myLocationListener);
        }
    }

    private void moveCamera(Point point, float zoom) {
        mapview.getMap().move(
                new CameraPosition(point, zoom, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
    }

    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private SensorEventListener mSensorEventListener = new SensorEventListener() {

        float[] gData = new float[3]; // gravity or accelerometer
        float[] mData = new float[3]; // magnetometer
        float[] rMat = new float[9];
        float[] iMat = new float[9];
        float[] orientation = new float[3];

        public void onAccuracyChanged( Sensor sensor, int accuracy ) {}

        @Override
        public void onSensorChanged( SensorEvent event ) {
            float[] data;
            switch ( event.sensor.getType() ) {
                case Sensor.TYPE_GRAVITY:
                    gData = event.values.clone();
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    gData = event.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mData = event.values.clone();
                    break;
                default: return;
            }

            if ( SensorManager.getRotationMatrix( rMat, iMat, gData, mData ) ) {
                mAzimuth= (int) ( Math.toDegrees( SensorManager.getOrientation( rMat, orientation )[0] ) + 360 ) % 360;
            }
            if (Now_Geoposition != null) Now_Geoposition.setDirection(mAzimuth);
        }
    };


}
