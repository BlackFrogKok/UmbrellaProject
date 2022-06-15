package com.samsungschool.umbrellaproject.Fragments.NavigationItems.HistoryFragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.samsungschool.umbrellaproject.FirestoreDataBase;
import com.samsungschool.umbrellaproject.Interface.MyOnCompliteDataListener;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.databinding.FragmentHistoryitemBinding;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
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

public class HistoryItemFragment extends Fragment implements DrivingSession.DrivingRouteListener {
    private FragmentHistoryitemBinding binding;
    private MapView mapView;
    private MapObjectCollection mapObjects;
    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession;
    private HistoryItem historyItem;
    private Point stationGet;
    private Point stationPut;
    private FirestoreDataBase firestoreDataBase = new FirestoreDataBase();
    private Point centerRout;


    public static HistoryItemFragment newInstance(HistoryItem historyItem) {

        Bundle args = new Bundle();
        args.putParcelable("item", historyItem);
        HistoryItemFragment fragment = new HistoryItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DirectionsFactory.initialize(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        historyItem = ((HistoryItem)getArguments().getParcelable("item"));
        binding = FragmentHistoryitemBinding.inflate(getLayoutInflater());
        mapView = binding.map;
        mapView.getMap().setNightModeEnabled(getResources().getConfiguration().uiMode == 33);
        mapObjects = mapView.getMap().getMapObjects().addCollection();
        binding.toolbar.setTitle(historyItem.getDate());
        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        binding.textView19.setText(historyItem.getStationGetID());
        binding.textView17.setText(historyItem.getTimeGet());
        firestoreDataBase.getDocument(historyItem.getStationGetID(), new MyOnCompliteDataListener<DocumentSnapshot>() {
            @Override
            public void onCompleteObservable(@NonNull Observable<DocumentSnapshot> observable) {}

            @Override
            public void onComplete(@NonNull DocumentSnapshot documentSnapshot) {
                stationGet = FirestoreDataBase.castHashMapToPoint(documentSnapshot);
                mapObjects.addPlacemark(stationGet, ImageProvider.fromBitmap(getBitmap(R.drawable.ic_map_truck_marker)));
                if(historyItem.getStationPutID() != null){
                    binding.textView27.setText(historyItem.getStationPutID());
                    firestoreDataBase.getDocument(historyItem.getStationPutID(), new MyOnCompliteDataListener<DocumentSnapshot>() {
                        @Override
                        public void onCompleteObservable(@NonNull Observable<DocumentSnapshot> observable) {}

                        @Override
                        public void onComplete(@NonNull DocumentSnapshot documentSnapshot) {
                            stationPut = FirestoreDataBase.castHashMapToPoint(documentSnapshot);
                            mapObjects.addPlacemark(stationPut, ImageProvider.fromBitmap(getBitmap(R.drawable.ic_map_truck_marker)));
                            initDriving();
                        }
                        @Override
                        public void onCanceled() {}
                    });
                }
            }
            @Override
            public void onCanceled() {}
        });

        if(historyItem.getTimePut() != null){
            binding.textView18.setText(historyItem.getTimePut());
        }
        binding.textView21.setText(historyItem.isStatus() ? "Сдан" : "Аренда");

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
        String errorMessage = "Неизвестная ошибка";
        if (error instanceof RemoteError) {
            errorMessage = "Ошибка сервера";
        } else if (error instanceof NetworkError) {
            errorMessage = "Нет сети";
        }

        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void initDriving(){
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
        submitRequest();
        centerRout = new Point(
                (stationGet.getLatitude() + stationGet.getLatitude()) / 2,
                (stationGet.getLongitude() + stationGet.getLongitude()) / 2);
        mapView.getMap().move(new CameraPosition(
                centerRout, 10, 0, 0));
    }

    private void submitRequest() {
        if(stationGet != null & stationPut != null){
            DrivingOptions drivingOptions = new DrivingOptions();
            VehicleOptions vehicleOptions = new VehicleOptions();
            ArrayList<RequestPoint> requestPoints = new ArrayList<>();
            requestPoints.add(new RequestPoint(
                    stationGet,
                    RequestPointType.WAYPOINT,
                    null));
            requestPoints.add(new RequestPoint(
                    stationPut,
                    RequestPointType.WAYPOINT,
                    null));
            drivingSession = drivingRouter.requestRoutes(requestPoints, drivingOptions, vehicleOptions, this);
        }

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

}
