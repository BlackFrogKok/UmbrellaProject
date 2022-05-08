package com.samsungschool.umbrellaproject.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.samsungschool.umbrellaproject.Activity.MainActivity;
import com.samsungschool.umbrellaproject.FirestoreDataBase;
import com.samsungschool.umbrellaproject.Interface.OnLoadDataListener;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.TextImageProvider;
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
import com.yandex.mapkit.map.Cluster;
import com.yandex.mapkit.map.ClusterListener;
import com.yandex.mapkit.map.ClusterTapListener;
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.runtime.image.ImageProvider;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MainFragment extends Fragment implements ClusterListener, ClusterTapListener {

    private static final double DESIRED_ACCURACY = 1;
    private static final long MINIMAL_TIME = 1000;
    private static final double MINIMAL_DISTANCE = 0.1;
    private static final boolean USE_IN_BACKGROUND = true;
    public static final float COMFORTABLE_ZOOM_LEVEL = 18.0f;
    private LocationManager locationManager;
    private LocationListener myLocationListener;
    private Point myLocation;
    private PlacemarkMapObject Now_Geoposition;

    private FragmentMainBinding binding;
    private MapView mapview;
    private onNavBtnClickListener navClickListener;
    private Context context;
    private SensorManager manager;
    private FirebaseFirestore dataBase;
    private FirestoreDataBase firestoreDataBase;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static boolean initialized = false;
    private static final String API_KEY = "d80212c2-0e79-43e5-b249-7e7612d3f566";
    private MainActivity activity;
    private BottomSheetBehavior bottomSheetBehavior;
    private PlacemarkMapObject p;


    private int mAzimuth = 0; // degree
    private Sensor mGravity;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    boolean haveGravity = false;
    boolean haveAccelerometer = false;
    boolean haveMagnetometer = false;

    private MapObjectTapListener mapObjectTapListener = new MapObjectTapListener() {
        @Override
        public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
            if(p != null) p.setIcon(ImageProvider.fromBitmap(getBitmap(R.drawable.ic_map_truck_marker)));
            p = (PlacemarkMapObject) mapObject;
            p.setIcon(ImageProvider.fromBitmap(getBitmap(R.drawable.ic_map_truck_marker_on_pressed)));
            moveCamera(p.getGeometry(), 17.0f);
            binding.qrScanerBtn.setVisibility(View.GONE);
            bottomSheetVisibilityChanged(true);
            return true;
        }
    };

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if(newState == 4){
                binding.qrScanerBtn.setVisibility(View.VISIBLE);
                p.setIcon(ImageProvider.fromBitmap(getBitmap(R.drawable.ic_map_truck_marker)));
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };


    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        initialize(API_KEY, this.getContext());
        SearchFactory.initialize(this.getContext());

        context = getContext();
        binding = FragmentMainBinding.inflate(getLayoutInflater());
        binding.navBtn.setOnClickListener(v -> navClickListener.onClick());
        binding.gpsFindPositionBtn.setOnClickListener(v ->{
            if(myLocation != null) moveCamera(myLocation, 17.0f);
        });

        activity = (MainActivity) getActivity();
        manager = activity.getManager();
        mapview = binding.mapView;
        firestoreDataBase = new FirestoreDataBase();
        mapview.getMap().setNightModeEnabled(true);


        mGravity = manager.getDefaultSensor( Sensor.TYPE_GRAVITY );
        haveGravity = manager.registerListener( mSensorEventListener, mGravity, SensorManager.SENSOR_DELAY_GAME );
        mAccelerometer = manager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
        haveAccelerometer = manager.registerListener( mSensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME );
        mMagnetometer = manager.getDefaultSensor( Sensor.TYPE_MAGNETIC_FIELD );
        haveMagnetometer = manager.registerListener( mSensorEventListener, mMagnetometer, SensorManager.SENSOR_DELAY_GAME );



        firestoreDataBase.setOnCompleteDataListener(new OnLoadDataListener<List<DocumentSnapshot>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Observable<List<DocumentSnapshot>> observable) {
                Disposable disposable = observable
                        .subscribeOn(Schedulers.io())
                        .map(obj -> obj.stream()
                                .map(FirestoreDataBase::castHashMapToPoint)
                                .collect(Collectors.toList())
                        )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(output -> {
                            ClusterizedPlacemarkCollection clusterizedCollection =
                                    mapview.getMap().getMapObjects().addClusterizedPlacemarkCollection(MainFragment.this);

                            clusterizedCollection.addTapListener(mapObjectTapListener);

                            clusterizedCollection.addPlacemarks(output, ImageProvider.fromBitmap(getBitmap(R.drawable.ic_map_truck_marker)), new IconStyle());

                            clusterizedCollection.clusterPlacemarks(20, 15);
                        }, throwable -> {
                            FirebaseCrashlytics.getInstance().recordException(throwable);
                        });

                compositeDisposable.add(disposable);
            }
        });



        locationManager = MapKitFactory.getInstance().createLocationManager();
        myLocationListener = new LocationListener() {
            @Override
            public void onLocationUpdated(@NonNull Location location) {
                if (myLocation == null) {
                    moveCamera(location.getPosition(), 17.0f);
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



        // if there is a gravity sensor we do not need the accelerometer
        if(haveGravity)
            manager.unregisterListener(mSensorEventListener, mAccelerometer );
        if ( ( haveGravity || haveAccelerometer ) && haveMagnetometer ) {
            // ready to go
        } else {
            // unregister and stop
        }

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ConstraintLayout bottomSheetLayout = (ConstraintLayout) view.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);
        bottomSheetVisibilityChanged(false);

        binding.qrScanerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).startQRActivity();
            }
        });
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
        compositeDisposable.clear();
        binding = null;
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
                new Animation(Animation.Type.SMOOTH, 1),
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




    private void initialize(String apiKey, Context context) {
        if (initialized) {
            return;
        }

        MapKitFactory.setApiKey(apiKey);
        MapKitFactory.initialize(context);
        initialized = true;
    }

    private void bottomSheetVisibilityChanged(boolean flag){
        bottomSheetBehavior.setState(flag ? BottomSheetBehavior.STATE_EXPANDED : BottomSheetBehavior.STATE_COLLAPSED);
    }





    @Override
    public void onClusterAdded(@NonNull Cluster cluster) {
        cluster.getAppearance().setIcon(
                new TextImageProvider(activity.getWindowManager(), Integer.toString(cluster.getSize())));;
        cluster.addClusterTapListener(this);
    }

    @Override
    public boolean onClusterTap(@NonNull Cluster cluster) {
        moveCamera(cluster.getAppearance().getGeometry(), 13.0f);
        return true;
    }
}
