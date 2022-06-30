package com.samsungschool.umbrellaproject.fragments;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.widget.Toast.makeText;
import static com.samsungschool.umbrellaproject.activities.QrActivity.EXTRA_KEY_CODE;
import static java.lang.String.format;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.annimon.stream.Collectors;
import com.annimon.stream.IntStream;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.samsungschool.umbrellaproject.activities.MainActivity;
import com.samsungschool.umbrellaproject.activities.QrActivity;
import com.samsungschool.umbrellaproject.data.FirestoreDatabase;
import com.samsungschool.umbrellaproject.data.Station;
import com.samsungschool.umbrellaproject.databinding.FragmentMainBinding;
import com.samsungschool.umbrellaproject.interfaces.NavigationListener;
import com.samsungschool.umbrellaproject.interfaces.OnCompleteDataListener;
import com.samsungschool.umbrellaproject.interfaces.OnTaskCompleteListener;
import com.samsungschool.umbrellaproject.interfaces.QrCheckCompleteListener;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.interfaces.UserListener;
import com.samsungschool.umbrellaproject.models.User;
import com.samsungschool.umbrellaproject.widgets.TextImageProvider;
import com.samsungschool.umbrellaproject.viewmodels.MainViewModel;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.rxjava3.core.Observable;

public class MainFragment extends Fragment implements ClusterListener, ClusterTapListener, QrCheckCompleteListener, UserListener {

    private MainViewModel viewModel;
    private PlacemarkMapObject Now_Geoposition;

    private FragmentMainBinding binding;
    private MapView mapview;
    private Context context;
    private static boolean initialized = false;
    private static final String API_KEY = "d80212c2-0e79-43e5-b249-7e7612d3f566";
    private MainActivity activity;
    private BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;
    private PlacemarkMapObject currentPoint;
    private FirestoreDatabase firestoreDataBase;
    private ConstraintLayout bottomSheetLayout;

    private ClusterizedPlacemarkCollection clusteredCollection;

    private List<PlacemarkMapObject> PlacerMapObjectCluster;

    private BiMap<String, PlacemarkMapObject> mapStationPoint;

    public static final String TAG_MAIN_FRAGMENT = "main";
    private final Station station = new Station();
    private User user;

    private boolean timerFlag = true;

    private String stationID;

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {


        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                station.checkQrData(data.getStringExtra("code"), new OnCompleteDataListener<String>() {


                    @Override
                    public void onComplete(@NonNull String s) {
                        firestoreDataBase.getFreeUmbrella(s, new OnCompleteDataListener<Integer>() {
                            @Override
                            public void onComplete(@NonNull Integer integer) {
                                firestoreDataBase.returnUmbrella(integer, s, new OnTaskCompleteListener() {
                                    @Override
                                    public void OnComplete() {
                                        timerFlag = true;
                                        ConstraintLayout c1 = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.return_umbrella_final_dialog, null, false);
                                        Log.d("TAG", "4");
                                        AlertDialog fin = new MaterialAlertDialogBuilder(context)
                                                .setCustomTitle(c1)
                                                .setNegativeButton("Отмена", (dialog1, which1) -> {
                                                    timerFlag = false;
                                                    firestoreDataBase.closeStation(s, false);

                                                })
                                                .setPositiveButton("Вернул", (dialog1, which1) -> {
                                                    timerFlag = false;
                                                    firestoreDataBase.endHistory(s, user.getActiveSession());
                                                    firestoreDataBase.closeStation(s, true);


                                                    binding.returnUmbrella.setVisibility(View.INVISIBLE);
                                                })
                                                .show();
                                        CountDownTimer timer = new CountDownTimer(60000, 100) {
                                            public void onTick(long millisUntilFinished) {
                                                if (!timerFlag) {
                                                    cancel();

                                                }
                                                int seconds = (int) (millisUntilFinished / 1000);
                                                CircularProgressIndicator circularProgressIndicator = c1.findViewById(R.id.circularProgressIndicator);
                                                TextView t = c1.findViewById(R.id.textViewTimer);
                                                circularProgressIndicator.setProgress((int) (millisUntilFinished / 60000.0 * 100));
                                                t.setText(String.valueOf(seconds));
                                            }

                                            public void onFinish() {
                                                if (!timerFlag) {
                                                    cancel();
                                                    return;
                                                }
                                                firestoreDataBase.closeStation(s, false);
                                                bottomSheetVisibilityChanged(false);
                                                fin.dismiss();
                                            }
                                        }.start();
                                        fin.setOnDismissListener(dialog -> {
                                            timerFlag = false;
                                            timer.onFinish();
                                            fin.setOnDismissListener(null);
                                        });
                                        fin.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alert_dialog_rounded));
                                    }
                                });
                            }

                            @Override
                            public void onCanceled() {

                            }
                        });


                    }

                    @Override
                    public void onCanceled() {
                        makeText(requireContext(), R.string.error_qr, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }


    });

    @SuppressWarnings("SuspiciousMethodCalls")
    private final MapObjectTapListener mapObjectTapListener = (mapObject, point) -> {
        stationID = mapStationPoint.inverse().get(mapObject);
        selectPoint((PlacemarkMapObject) mapObject);
        return true;
    };

    private final BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {

            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                closeBottomSheet();
            } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                firestoreDataBase.getStationActive(stationID, new OnCompleteDataListener<Integer>() {
                    @Override
                    public void onComplete(@NonNull Integer integer) {
                        if (integer == 0) {
                            TextView t1 = bottomSheet.findViewById(R.id.textView10);
                            TextView t2 = bottomSheet.findViewById(R.id.countUmbrella);
                            t2.setText("");
                            t1.setText(R.string.station_inactive);
                            t1.setTextColor(getResources().getColor(R.color.red));
                        } else {
                            firestoreDataBase.getUmbrellaCount(stationID, new OnCompleteDataListener<Integer>() {
                                @Override
                                public void onCompleteObservable(@NonNull Observable<Integer> observable) {
                                    // Do nothing
                                }

                                @Override
                                public void onComplete(@NonNull Integer count) {
                                    if (count == 0) {
                                        ((TextView) bottomSheet.findViewById(R.id.countUmbrella)).setText(R.string.umbrella_empty);
                                    } else {
                                        ((TextView) bottomSheet.findViewById(R.id.countUmbrella)).setText(format(getString(R.string.umbrella_count), count));
                                    }
                                    ((TextView) bottomSheet.findViewById(R.id.textView10)).setText(R.string.station_active);
                                    ((TextView) bottomSheet.findViewById(R.id.textView10)).setTextColor(getResources().getColor(R.color.green));
                                }

                                @Override
                                public void onCanceled() {
                                    // Do nothing
                                }
                            });
                        }
                    }

                    @Override
                    public void onCanceled() {

                    }
                });


            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            // Do nothing
        }
    };

    private void closeBottomSheet() {
        binding.qrScannerBtn.setVisibility(View.VISIBLE);
        bottomSheetLayout.getViewById(R.id.bottom_sheet).findViewById(R.id.qr_check).setVisibility(View.VISIBLE);
        bottomSheetLayout.getViewById(R.id.bottom_sheet).findViewById(R.id.get_umbrella).setVisibility(View.GONE);
        currentPoint.setIcon(ImageProvider.fromBitmap(getBitmap(R.drawable.ic_map_truck_marker)));
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
        super.onCreate(savedInstanceState);
        initialize(requireContext());
        SearchFactory.initialize(requireContext());
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        int uiThemeCode = getResources().getConfiguration().uiMode;
        context = getContext();
        firestoreDataBase = new FirestoreDatabase();
        binding = FragmentMainBinding.inflate(getLayoutInflater());
        activity = (MainActivity) getActivity();
        mapview = binding.mapView;
        binding.navBtn.setOnClickListener(v -> ((NavigationListener) requireActivity()).onNavigationClick());
        binding.gpsFindPositionBtn.setOnClickListener(v -> moveCamera(viewModel.userLocations.getValue(), 17.0f));
        mapview.getMap().setNightModeEnabled(uiThemeCode == 33);
        viewModel.loadStationsPoint();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel.userLocations.observe(getViewLifecycleOwner(), position -> {
            if (Now_Geoposition == null) {
                Now_Geoposition = mapview.getMap().getMapObjects().addPlacemark(Objects.requireNonNull(viewModel.userLocations.getValue()));
                Now_Geoposition.useCompositeIcon().setIcon(
                        "pin",
                        ImageProvider.fromBitmap(getBitmap(R.drawable.ic_user_navi_pin)),
                        new IconStyle().setRotationType(RotationType.ROTATE)
                );
                Now_Geoposition.useCompositeIcon().setIcon(
                        "icon",
                        ImageProvider.fromBitmap(getBitmap(R.drawable.ic_user_marker)),
                        new IconStyle().setRotationType(RotationType.ROTATE)
                );
                moveCamera(position, 17.0f);
            }
            Now_Geoposition.setGeometry(position);
        });

        viewModel.userAzimuth.observe(getViewLifecycleOwner(), azimuth -> {
            if (Now_Geoposition != null) Now_Geoposition.setDirection(azimuth);
        });

        viewModel.stationPoints.observe(getViewLifecycleOwner(), stationPoints -> {
            clusteredCollection = mapview.getMap().getMapObjects().addClusterizedPlacemarkCollection(MainFragment.this);
            clusteredCollection.addTapListener(mapObjectTapListener);

            PlacerMapObjectCluster = clusteredCollection.addPlacemarks(
                    new ArrayList<>(stationPoints.values()),
                    ImageProvider.fromBitmap(getBitmap(R.drawable.ic_map_truck_marker)),
                    new IconStyle()
            );

            mapStationPoint = zipToMap(new ArrayList<>(stationPoints.keySet()), PlacerMapObjectCluster);
            viewModel.setStationsMapObject(mapStationPoint);
            clusteredCollection.clusterPlacemarks(20, 15);
        });

        Log.w("document2", String.valueOf(viewModel.stationMapObject));

        return binding.getRoot();
    }

    private void showDialog() {
        timerFlag = true;
        ConstraintLayout c = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.get_umbrella_dialog, binding.getRoot(), false);

        AlertDialog d = new MaterialAlertDialogBuilder(context)
                .setCustomTitle(c)
                .setNegativeButton("Отмена", (dialog, which) -> {
                    firestoreDataBase.closeStation(stationID, false);
                    bottomSheetVisibilityChanged(false);
                    timerFlag = false;
                })
                .setPositiveButton("Забрал", (dialog, which) -> {
                    binding.returnUmbrella.setVisibility(View.VISIBLE);
                    timerFlag = false;
                    String date = Calendar.getInstance().getTime().toString();
                    firestoreDataBase.addHistory(stationID, date);
                    user.setActiveSession(date);
                    firestoreDataBase.closeStation(stationID, true);

                    bottomSheetVisibilityChanged(false);
                })
                .show();

        CountDownTimer timer = new CountDownTimer(60000, 100) {

            public void onTick(long millisUntilFinished) {
                if (!timerFlag) {
                    cancel();
                }
                int seconds = (int) (millisUntilFinished / 1000);
                CircularProgressIndicator circularProgressIndicator = c.findViewById(R.id.circularProgressIndicator);
                TextView t = c.findViewById(R.id.textViewTimer);
                circularProgressIndicator.setProgress((int) (millisUntilFinished / 60000.0 * 100));
                t.setText(String.valueOf(seconds));
            }

            public void onFinish() {
                if (!timerFlag) {
                    cancel();
                    return;
                }
                firestoreDataBase.closeStation(stationID, false);
                bottomSheetVisibilityChanged(false);
                d.dismiss();
            }
        }.start();


        d.setOnDismissListener(dialog -> {
            timerFlag = false;
            timer.onFinish();
            d.setOnDismissListener(null);
        });


        d.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.alert_dialog_rounded));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomSheetLayout = view.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);
        bottomSheetVisibilityChanged(false);
        binding.qrScannerBtn.setOnClickListener(v -> activity.startQRActivity());
        bottomSheetLayout.getViewById(R.id.qr_check).findViewById(R.id.bottomQrScannerBtn).setOnClickListener(v -> activity.startQRActivity());
        ConstraintLayout getUmbrellaLayout = (ConstraintLayout) bottomSheetLayout.getViewById(R.id.get_umbrella);

        binding.returnUmbrella.setOnClickListener(v -> {
            ConstraintLayout umbrellaDialog = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.return_umbrella_dialog, binding.getRoot(), false);
            AlertDialog returnUmbrella = new MaterialAlertDialogBuilder(context)
                    .setCustomTitle(umbrellaDialog)
                    .setNegativeButton("Отмена", (dialog1, which1) -> {
                    })
                    .setPositiveButton("Вернуть", (dialog1, which1) -> mStartForResult.launch(QrActivity.newIntent(requireActivity())))
                    .show();
            returnUmbrella.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.alert_dialog_rounded));
        });

        getUmbrellaLayout.getViewById(R.id.button1).setOnClickListener(v -> firestoreDataBase.getUmbrella(1, stationID, () -> {


        }));

        getUmbrellaLayout.getViewById(R.id.button2).setOnClickListener(v -> firestoreDataBase.getUmbrella(2, stationID, () -> {

            showDialog();
        }));

        getUmbrellaLayout.getViewById(R.id.button3).setOnClickListener(v -> firestoreDataBase.getUmbrella(3, stationID, () -> {

            showDialog();
        }));

        getUmbrellaLayout.getViewById(R.id.button4).setOnClickListener(v -> firestoreDataBase.getUmbrella(4, stationID, () -> {

            showDialog();
        }));

        getUmbrellaLayout.getViewById(R.id.button5).setOnClickListener(v -> firestoreDataBase.getUmbrella(5, stationID, () -> {

            showDialog();
        }));

        getUmbrellaLayout.getViewById(R.id.button6).setOnClickListener(v -> firestoreDataBase.getUmbrella(6, stationID, () -> {

            showDialog();
        }));

        getUmbrellaLayout.getViewById(R.id.button7).setOnClickListener(v -> firestoreDataBase.getUmbrella(7, stationID, () -> {

            showDialog();
        }));
    }

    @Override
    public void onStart() {
        super.onStart();
        mapview.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        MapKitFactory.getInstance().onStop();
        mapview.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void moveCamera(Point point, float zoom) {
        mapview.getMap().move(
                new CameraPosition(point, zoom, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 1),
                null);
    }

    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = ContextCompat.getDrawable(requireContext(), drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(
                (drawable != null) ? drawable.getIntrinsicWidth() : 0,
                (drawable != null) ? drawable.getIntrinsicHeight() : 0,
                ARGB_8888
        );
        canvas.setBitmap(bitmap);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
        }
        return bitmap;
    }

    private void selectPoint(PlacemarkMapObject point) {
        bottomSheetVisibilityChanged(false);
        if (currentPoint != null)
            currentPoint.setIcon(ImageProvider.fromBitmap(getBitmap(R.drawable.ic_map_truck_marker)));
        currentPoint = point;
        currentPoint.setIcon(ImageProvider.fromBitmap(getBitmap(R.drawable.ic_map_truck_marker_on_pressed)));
        moveCamera(currentPoint.getGeometry(), 17.0f);
        binding.qrScannerBtn.setVisibility(View.GONE);
        bottomSheetVisibilityChanged(true);
    }

    private void initialize(Context context) {
        if (initialized) {
            return;
        }

        MapKitFactory.setApiKey(API_KEY);
        MapKitFactory.setLocale(String.valueOf(Locale.getDefault()));
        MapKitFactory.initialize(context);
        initialized = true;
    }

    private void bottomSheetVisibilityChanged(boolean flag) {
        bottomSheetBehavior.setState(flag ? BottomSheetBehavior.STATE_EXPANDED :
                BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onClusterAdded(@NonNull Cluster cluster) {
        cluster.getAppearance().setIcon(new TextImageProvider(activity.getDisplayMetrics(), Integer.toString(cluster.getSize())));
        cluster.addClusterTapListener(this);
    }

    @Override
    public boolean onClusterTap(@NonNull Cluster cluster) {
        moveCamera(cluster.getAppearance().getGeometry(), 13.0f);
        return true;
    }

    @Override
    public void QrCheckComplete(@NonNull String stationID) {
        this.stationID = stationID;
        if (viewModel.stationMapObject.getValue() != null)
            Log.w("document3", viewModel.stationMapObject.getValue().get(stationID) + "l");
        bottomSheetLayout.getViewById(R.id.bottom_sheet).findViewById(R.id.qr_check).setVisibility(View.GONE);
        bottomSheetLayout.getViewById(R.id.bottom_sheet).findViewById(R.id.get_umbrella).setVisibility(View.VISIBLE);
        selectPoint(viewModel.stationMapObject.getValue().get(stationID));
    }

    public static <K, V> BiMap<K, V> zipToMap(List<K> keys, List<V> values) {
        return HashBiMap.create(Objects.requireNonNull(IntStream.range(0, keys.size()).boxed().collect(Collectors.toMap(keys::get, values::get))));
    }

    @Override
    public void onLoaded(User user) {
        this.user = user;
        if (user.getActiveSession() != null && !user.getActiveSession().equals("")){
            binding.returnUmbrella.setVisibility(View.VISIBLE);

        }
        else {
            binding.returnUmbrella.setVisibility(View.GONE);
        }
    }
}
