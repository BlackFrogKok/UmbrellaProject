package com.samsungschool.umbrellaproject.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.samsungschool.umbrellaproject.Activity.MainActivity;
import com.samsungschool.umbrellaproject.FirestoreDataBase;
import com.samsungschool.umbrellaproject.Interface.MyOnCompliteListener;
import com.samsungschool.umbrellaproject.Interface.QrCheckCompliteInterface;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.TextImageProvider;
import com.samsungschool.umbrellaproject.ViewModels.MainFragmentViewModel;
import com.samsungschool.umbrellaproject.databinding.FragmentMainBinding;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class MainFragment extends Fragment implements ClusterListener, ClusterTapListener,
        QrCheckCompliteInterface {

    private MainFragmentViewModel viewModel;
    private PlacemarkMapObject Now_Geoposition;

    private FragmentMainBinding binding;
    private MapView mapview;
    private onNavBtnClickListener navClickListener;
    private Context context;
    private static boolean initialized = false;
    private static final String API_KEY = "d80212c2-0e79-43e5-b249-7e7612d3f566";
    private MainActivity activity;
    private BottomSheetBehavior bottomSheetBehavior;
    private PlacemarkMapObject curentPoint;
    private Integer uiThemeCode;
    private FirestoreDataBase firestoreDataBase;

    private ClusterizedPlacemarkCollection clusterizedCollection;

    private List<PlacemarkMapObject> PlacemarkMapObjectClaster;

    private Map<String, PlacemarkMapObject> mapStationPoint;

    private String stationID;




    private MapObjectTapListener mapObjectTapListener = new MapObjectTapListener() {
        @Override
        public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
            selectPoint((PlacemarkMapObject) mapObject);
            return true;
        }
    };


    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback =
            new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if(newState == 4){
                binding.qrScannerBtn.setVisibility(View.VISIBLE);
                curentPoint.setIcon(ImageProvider.
                        fromBitmap(getBitmap(R.drawable.ic_map_truck_marker)));
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
        viewModel = new ViewModelProvider(this).get(MainFragmentViewModel.class);
        uiThemeCode = getResources().getConfiguration().uiMode;
        context = getContext();
        firestoreDataBase = new FirestoreDataBase();
        binding = FragmentMainBinding.inflate(getLayoutInflater());
        activity = (MainActivity) getActivity();
        mapview = binding.mapView;
        binding.navBtn.setOnClickListener(v -> navClickListener.onClick());
        binding.gpsFindPositionBtn.setOnClickListener(v ->
                moveCamera(viewModel.userLocations.getValue(), 17.0f));
        mapview.getMap().setNightModeEnabled(uiThemeCode == 33);
        viewModel.loadStationsPoint();


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


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        viewModel.userLocations.observe(getViewLifecycleOwner(), position -> {
            if(Now_Geoposition == null){
                Now_Geoposition = mapview.getMap()
                        .getMapObjects().addPlacemark(viewModel.userLocations.getValue());
                Now_Geoposition.useCompositeIcon()
                        .setIcon("pin",
                                ImageProvider.fromBitmap(getBitmap(R.drawable.ic_user_navi_pin)),
                                new IconStyle().setRotationType(RotationType.ROTATE));
                Now_Geoposition.useCompositeIcon().setIcon("icon",
                        ImageProvider.fromBitmap(getBitmap(R.drawable.ic_user_marker)),
                        new IconStyle().setRotationType(RotationType.ROTATE));
                moveCamera(position, 17.0f);
            }
            Now_Geoposition.setGeometry(position);
        });

        viewModel.userAzimuth.observe(getViewLifecycleOwner(), azimuth -> {
            if(Now_Geoposition != null) Now_Geoposition.setDirection(azimuth);
        });

        viewModel.stationPoints.observe(getViewLifecycleOwner(), stationPoints ->{
            clusterizedCollection =
                    mapview.getMap().getMapObjects()
                            .addClusterizedPlacemarkCollection(
                                    MainFragment.this);

            clusterizedCollection.addTapListener(mapObjectTapListener);

            PlacemarkMapObjectClaster = clusterizedCollection.addPlacemarks(new ArrayList(stationPoints.values()),
                    ImageProvider.fromBitmap(getBitmap(R.drawable.ic_map_truck_marker)),
                    new IconStyle());

            mapStationPoint = zipToMap(new ArrayList(stationPoints.keySet()), PlacemarkMapObjectClaster);
            viewModel.setStationsMapObject(mapStationPoint);
            clusterizedCollection.clusterPlacemarks(20, 15);
        });

        Log.w("document2", String.valueOf(viewModel.stationMapObject));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ConstraintLayout bottomSheetLayout = (ConstraintLayout) view
                .findViewById(R.id.bottom_sheet_qr_check);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);
        MaterialButton b1 = view.findViewById(R.id.button11);
        MaterialButton b2 = view.findViewById(R.id.button12);
        MaterialButton b3 = view.findViewById(R.id.button13);
        MaterialButton b4 = view.findViewById(R.id.button14);
        MaterialButton b5 = view.findViewById(R.id.button15);
        MaterialButton b6 = view.findViewById(R.id.button16);
        MaterialButton b7 = view.findViewById(R.id.button17);
        bottomSheetVisibilityChanged(false);
        binding.qrScannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startQRActivity();
            }
        });
        b1.setOnClickListener(v -> {
           firestoreDataBase.getUmbrella(1, stationID, new MyOnCompliteListener() {
               @Override
               public void OnComplite() {
                   Toast.makeText(context, "Зонт выдан", Toast.LENGTH_SHORT).show();
               }
           });
        });

        b2.setOnClickListener(v -> {
            firestoreDataBase.getUmbrella(2, stationID, new MyOnCompliteListener() {
                @Override
                public void OnComplite() {
                    Toast.makeText(context, "Зонт выдан", Toast.LENGTH_SHORT).show();
                }
            });
        });
        b3.setOnClickListener(v -> {
            firestoreDataBase.getUmbrella(3, stationID, new MyOnCompliteListener() {
                @Override
                public void OnComplite() {
                    Toast.makeText(context, "Зонт выдан", Toast.LENGTH_SHORT).show();
                }
            });
        });
        b4.setOnClickListener(v -> {
            firestoreDataBase.getUmbrella(4, stationID, new MyOnCompliteListener() {
                @Override
                public void OnComplite() {
                    Toast.makeText(context, "Зонт выдан", Toast.LENGTH_SHORT).show();
                }
            });
        });
        b5.setOnClickListener(v -> {
            firestoreDataBase.getUmbrella(5, stationID, new MyOnCompliteListener() {
                @Override
                public void OnComplite() {
                    Toast.makeText(context, "Зонт выдан", Toast.LENGTH_SHORT).show();
                }
            });
        });
        b6.setOnClickListener(v -> {
            firestoreDataBase.getUmbrella(6, stationID, new MyOnCompliteListener() {
                @Override
                public void OnComplite() {
                    Toast.makeText(context, "Зонт выдан", Toast.LENGTH_SHORT).show();
                }
            });
        });
        b7.setOnClickListener(v -> {
            firestoreDataBase.getUmbrella(7, stationID, new MyOnCompliteListener() {
                @Override
                public void OnComplite() {
                    Toast.makeText(context, "Зонт выдан", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mapview.onStart();
        MapKitFactory.getInstance().onStart();
    }


    @Override public void onStop() {
        super.onStop();
        MapKitFactory.getInstance().onStop();
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


    private void moveCamera(Point point, float zoom) {
        mapview.getMap().move(
                new CameraPosition(point, zoom, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 1),
                null);
    }

    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void selectPoint(PlacemarkMapObject point){
        if(curentPoint != null) curentPoint
                .setIcon(ImageProvider.fromBitmap(getBitmap(R.drawable.ic_map_truck_marker)));
        curentPoint  = point;
        curentPoint.setIcon(ImageProvider
                .fromBitmap(getBitmap(R.drawable.ic_map_truck_marker_on_pressed)));
        moveCamera(curentPoint.getGeometry(), 17.0f);
        binding.qrScannerBtn.setVisibility(View.GONE);
        bottomSheetVisibilityChanged(true);
    }


    private void initialize(String apiKey, Context context) {
        if (initialized) {
            return;
        }

        MapKitFactory.setApiKey(apiKey);
        MapKitFactory.setLocale(String.valueOf(Locale.getDefault()));
        MapKitFactory.initialize(context);
        initialized = true;
    }


    private void bottomSheetVisibilityChanged(boolean flag){
        bottomSheetBehavior.setState(flag ? BottomSheetBehavior.STATE_EXPANDED :
                BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onClusterAdded(@NonNull Cluster cluster) {
        cluster.getAppearance().setIcon(
                new TextImageProvider(activity.getDisplayMetrics(),
                        Integer.toString(cluster.getSize())));;
        cluster.addClusterTapListener(this);
    }

    @Override
    public boolean onClusterTap(@NonNull Cluster cluster) {
        moveCamera(cluster.getAppearance().getGeometry(), 13.0f);
        return true;
    }
    @Override
    public void QrCheckComplite(@NonNull String stationID) {
        this.stationID = stationID;
        Log.w("document3",String.valueOf(viewModel.stationMapObject.getValue().get(stationID)) + "l");
        selectPoint(viewModel.stationMapObject.getValue().get(stationID));
    }

    public static <K, V> Map<K, V> zipToMap(List<K> keys, List<V> values) {
        return IntStream.range(0, keys.size()).boxed()
                .collect(Collectors.toMap(keys::get, values::get));
    }

}
