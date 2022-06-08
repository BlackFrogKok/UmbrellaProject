package com.samsungschool.umbrellaproject.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.annimon.stream.Collectors;
import com.annimon.stream.IntStream;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.samsungschool.umbrellaproject.Activity.MainActivity;
import com.samsungschool.umbrellaproject.FirestoreDataBase;
import com.samsungschool.umbrellaproject.Interface.MyOnCompliteDataListener;
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
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.core.Observable;


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
    private ConstraintLayout bottomSheetLayout;

    private ClusterizedPlacemarkCollection clusterizedCollection;

    private List<PlacemarkMapObject> PlacemarkMapObjectClaster;

    private BiMap<String, PlacemarkMapObject> mapStationPoint;

    private String stationID;

    private ConstraintLayout getUmbrellaLayout;




    private MapObjectTapListener mapObjectTapListener = (mapObject, point) -> {
        stationID = mapStationPoint.inverse().get(mapObject);
        selectPoint((PlacemarkMapObject) mapObject);
        return true;
    };


    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback =
            new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                closeBottomSheet();
            }else if(newState == BottomSheetBehavior.STATE_EXPANDED){
                firestoreDataBase.getUmbrellaCount(stationID, new MyOnCompliteDataListener<Integer>() {
                    @Override
                    public void onCompleteObservable(@NonNull Observable<Integer> observable) {}

                    @Override
                    public void onComplete(@NonNull Integer integer) {
                        String text = "";
                        switch (integer){
                            case 0: text = "Нет свободных зонтов";
                                break;
                            case 1: text = "Свободен 1 зонт";
                                break;
                            case 2:text = "Свободо 2 зонта";
                                break;
                            case 3: text = "Свободно 3 зонта";
                                break;
                            case 4: text = "Свободно 4 зонта";
                                break;
                            case 5: text = "Свободно 5 зонтов";
                                break;
                            case 6: text = "Свободно 6 зонтов";
                                break;
                            case 7: text = "Свободно 7 зонтов";
                                break;
                        }

                        TextView t = (TextView) bottomSheet.findViewById(R.id.countUmbrella);
                        t.setText(text);
                    }

                    @Override
                    public void onCanceled() {}
                });
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    private void closeBottomSheet() {
        binding.qrScannerBtn.setVisibility(View.VISIBLE);
        bottomSheetLayout.getViewById(R.id.bottom_sheet)
                .findViewById(R.id.qr_check).setVisibility(View.VISIBLE);
        bottomSheetLayout.getViewById(R.id.bottom_sheet)
                .findViewById(R.id.get_umbrella).setVisibility(View.GONE);
        curentPoint.setIcon(ImageProvider.
                fromBitmap(getBitmap(R.drawable.ic_map_truck_marker)));
        stationID = "";
    }



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

    private void showDialog(){
        ConstraintLayout c = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.get_umbrella_dialog, null, false);

        AlertDialog d = new MaterialAlertDialogBuilder(context)
                .setCustomTitle(c)
                .setNegativeButton("Отмена", (dialog, which) -> {
                    firestoreDataBase.closeStation(stationID);
                    bottomSheetVisibilityChanged(false);
                })
                .setPositiveButton("Забрал", (dialog, which) -> {
                    firestoreDataBase.addHistory(stationID);
                    firestoreDataBase.closeStation(stationID);
                    bottomSheetVisibilityChanged(false);
                })
                .show();

        CountDownTimer timer = new CountDownTimer(60000, 100) {
        public void onTick(long millisUntilFinished) {
            int seconds = (int) (millisUntilFinished / 1000);
            CircularProgressIndicator circularProgressIndicator = c.findViewById(R.id.circularProgressIndicator);
            TextView t = c.findViewById(R.id.textViewTimer);
            circularProgressIndicator.setProgress((int)(millisUntilFinished / 60000.0 * 100));
            t.setText(String.valueOf(seconds));
        }
        public void onFinish() {
            firestoreDataBase.closeStation(stationID);
            bottomSheetVisibilityChanged(false);
            d.dismiss();
        }
    }.start();

        d.setOnDismissListener(dialog -> {
            timer.cancel();
            d.setOnDismissListener(null);
        });
        d.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alert_dialog_rounded));


    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomSheetLayout = (ConstraintLayout) view
                .findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);
        bottomSheetVisibilityChanged(false);
        binding.qrScannerBtn.setOnClickListener(v -> activity.startQRActivity());
        bottomSheetLayout.getViewById(R.id.qr_check).findViewById(R.id.bottomQrScannerBtn)
                .setOnClickListener(v -> activity.startQRActivity());

        getUmbrellaLayout = (ConstraintLayout) bottomSheetLayout.getViewById(R.id.get_umbrella);

        getUmbrellaLayout.getViewById(R.id.button1).setOnClickListener(v -> {
           firestoreDataBase.getUmbrella(1, stationID, new MyOnCompliteListener() {
               @Override
               public void OnComplite() {
                   showDialog();
               }
           });
        });

        getUmbrellaLayout.getViewById(R.id.button2).setOnClickListener(v -> {
            firestoreDataBase.getUmbrella(2, stationID, new MyOnCompliteListener() {
                @Override
                public void OnComplite() {
                    showDialog();
                }
            });
        });
        getUmbrellaLayout.getViewById(R.id.button3).setOnClickListener(v -> {
            firestoreDataBase.getUmbrella(3, stationID, new MyOnCompliteListener() {
                @Override
                public void OnComplite() {
                    showDialog();
                }
            });
        });
        getUmbrellaLayout.getViewById(R.id.button4).setOnClickListener(v -> {
            firestoreDataBase.getUmbrella(4, stationID, new MyOnCompliteListener() {
                @Override
                public void OnComplite() {
                    showDialog();
                }
            });
        });
        getUmbrellaLayout.getViewById(R.id.button5).setOnClickListener(v -> {
            firestoreDataBase.getUmbrella(5, stationID, new MyOnCompliteListener() {
                @Override
                public void OnComplite() {
                    showDialog();
                }
            });
        });
        getUmbrellaLayout.getViewById(R.id.button6).setOnClickListener(v -> {
            firestoreDataBase.getUmbrella(6, stationID, new MyOnCompliteListener() {
                @Override
                public void OnComplite() {
                    showDialog();
                }
            });
        });
        getUmbrellaLayout.getViewById(R.id.button7).setOnClickListener(v -> {
            firestoreDataBase.getUmbrella(7, stationID, new MyOnCompliteListener() {
                @Override
                public void OnComplite() {
                    showDialog();



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
        bottomSheetLayout.getViewById(R.id.bottom_sheet)
                .findViewById(R.id.qr_check).setVisibility(View.GONE);
        bottomSheetLayout.getViewById(R.id.bottom_sheet)
                .findViewById(R.id.get_umbrella).setVisibility(View.VISIBLE);
        selectPoint(viewModel.stationMapObject.getValue().get(stationID));
    }

    public static <K, V> BiMap<K, V> zipToMap(List<K> keys, List<V> values) {
        return HashBiMap.create(IntStream.range(0, keys.size()).boxed()
                .collect(Collectors.toMap(keys::get, values::get)));
    }

}
