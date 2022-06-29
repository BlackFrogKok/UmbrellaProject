package com.samsungschool.umbrellaproject.fragments;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.yandex.mapkit.RequestPointType.WAYPOINT;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.data.FirestoreDatabase;
import com.samsungschool.umbrellaproject.databinding.FragmentRouteBinding;
import com.samsungschool.umbrellaproject.interfaces.OnCompleteDataListener;
import com.samsungschool.umbrellaproject.items.HistoryItem;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class RouteFragment extends Fragment implements DrivingSession.DrivingRouteListener {


    private final FirestoreDatabase firestoreDataBase = new FirestoreDatabase();

    private MapView mapView;
    private MapObjectCollection mapObjects;
    private DrivingRouter drivingRouter;
    private HistoryItem historyItem;
    private Point stationGet;
    private Point stationPut;

    private FragmentRouteBinding binding;

    private static final String ARG_KEY_HISTORY = "history";

    public static RouteFragment newInstance(HistoryItem historyItem) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_KEY_HISTORY, historyItem);
        RouteFragment fragment = new RouteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DirectionsFactory.initialize(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        historyItem = arguments != null ? getArguments().getParcelable(ARG_KEY_HISTORY) : new HistoryItem("", "", "");
        binding = FragmentRouteBinding.inflate(getLayoutInflater());
        mapView = binding.map;
        mapView.getMap().setNightModeEnabled(getResources().getConfiguration().uiMode == 33);
        mapObjects = mapView.getMap().getMapObjects().addCollection();
        binding.toolbar.setTitle(historyItem.getDate());
        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        binding.textView19.setText(historyItem.getStationGetID());
        binding.textView17.setText(historyItem.getTimeGet());
        firestoreDataBase.getDocument(historyItem.getStationGetID(), new OnCompleteDataListener<DocumentSnapshot>() {
            @Override
            public void onCompleteObservable(@NonNull Observable<DocumentSnapshot> observable) {
            }

            @Override
            public void onComplete(@NonNull DocumentSnapshot documentSnapshot) {
                stationGet = FirestoreDatabase.castHashMapToPoint(documentSnapshot);
                mapObjects.addPlacemark(stationGet, ImageProvider.fromBitmap(getBitmap(R.drawable.ic_map_truck_marker)));
                if (historyItem.getStationPutID() != null) {
                    binding.textView27.setText(historyItem.getStationPutID());
                    firestoreDataBase.getDocument(historyItem.getStationPutID(), new OnCompleteDataListener<DocumentSnapshot>() {
                        @Override
                        public void onCompleteObservable(@NonNull Observable<DocumentSnapshot> observable) {
                        }

                        @Override
                        public void onComplete(@NonNull DocumentSnapshot documentSnapshot) {
                            stationPut = FirestoreDatabase.castHashMapToPoint(documentSnapshot);
                            mapObjects.addPlacemark(stationPut, ImageProvider.fromBitmap(getBitmap(R.drawable.ic_map_truck_marker)));
                            initDriving();
                        }

                        @Override
                        public void onCanceled() {
                        }
                    });
                }
            }

            @Override
            public void onCanceled() {
            }
        });

        if (historyItem.getTimePut() != null) binding.textView18.setText(historyItem.getTimePut());
        binding.textView21.setText(historyItem.isStatus() ? R.string.returned : R.string.rent);

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    public void onDrivingRoutes(List<DrivingRoute> routes) {
        mapObjects.addPolyline(routes.get(0).getGeometry());
    }

    @Override
    public void onDrivingRoutesError(@NonNull Error error) {
        if (error instanceof RemoteError) {
            showError(R.string.error_server);
        } else if (error instanceof NetworkError) {
            showError(R.string.error_network);
        } else {
            showError(R.string.error_unknown);
        }
    }

    private void initDriving() {
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
        submitRequest();
        Point centerRoute = new Point(
                (stationGet.getLatitude() + stationGet.getLatitude()) / 2,
                (stationGet.getLongitude() + stationGet.getLongitude()) / 2
        );
        mapView.getMap().move(new CameraPosition(centerRoute, 10, 0, 0));
    }

    private void submitRequest() {
        if (stationGet != null & stationPut != null) {
            DrivingOptions drivingOptions = new DrivingOptions();
            VehicleOptions vehicleOptions = new VehicleOptions();
            ArrayList<RequestPoint> requestPoints = new ArrayList<>();
            requestPoints.add(new RequestPoint(stationGet, WAYPOINT, null));
            requestPoints.add(new RequestPoint(stationPut, WAYPOINT, null));
            DrivingSession drivingSession = drivingRouter.requestRoutes(requestPoints, drivingOptions, vehicleOptions, this);
            Log.d("Driving session: ", drivingSession.toString());
        }

    }

    private void showError(@StringRes int messageRes) {
        makeText(getContext(), messageRes, LENGTH_SHORT).show();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
