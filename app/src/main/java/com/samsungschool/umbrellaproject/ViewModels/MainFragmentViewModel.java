package com.samsungschool.umbrellaproject.ViewModels;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.annimon.stream.Collector;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.samsungschool.umbrellaproject.Compass;
import com.samsungschool.umbrellaproject.FirestoreDataBase;
import com.samsungschool.umbrellaproject.Interface.MyOnCompliteDataListener;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.location.FilteringMode;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.PlacemarkMapObject;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainFragmentViewModel extends AndroidViewModel {
    //----------------------------ViewModel Variable-----------------------------------//
    private final MutableLiveData<Point> _userLocations = new MutableLiveData<Point>();
    public LiveData<Point> userLocations = _userLocations;

    private final MutableLiveData<Integer> _userAzimuth = new MutableLiveData<Integer>();
    public LiveData<Integer> userAzimuth = _userAzimuth;

    private final MutableLiveData<Map<String, Point>> _stationPoints = new MutableLiveData<Map<String, Point>>();
    public LiveData<Map<String, Point>> stationPoints = _stationPoints;

    private final MutableLiveData<Map<String, PlacemarkMapObject>> _stationMapObject = new MutableLiveData<Map<String, PlacemarkMapObject>>();
    public LiveData<Map<String, PlacemarkMapObject>> stationMapObject = _stationMapObject;
    //----------------------------UserLocation Variable-----------------------------------//
    private static final double DESIRED_ACCURACY = 1;
    private static final long MINIMAL_TIME = 1000;
    private static final double MINIMAL_DISTANCE = 0.1;
    private static final boolean USE_IN_BACKGROUND = true;
    //----------------------------FirestoreDataBase Variable-----------------------------------//
    private FirestoreDataBase firestoreDataBase;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    //----------------------------UserAzimuth Variable-----------------------------------//
    private Compass compass;
    private List<Integer> numArray = new ArrayList<Integer>();

    //----------------------------SharedPreferences Variable-----------------------------//

    private final String APP_PREFERENCE = "APP_PREFERENCE";
    private final String LONGITUDE_PREFERENCE_KEY = "LONGITUDE";
    private final String LATITUDE_PREFERENCE_KEY = "LATITUDE";
    private SharedPreferences UserDataPref;

    //-----------------------------------Listeners----------------------------------------//
    private final LocationManager locationManager = MapKitFactory.getInstance()
            .createLocationManager();
    private final LocationListener myLocationListener = new LocationListener() {

        @Override
        public void onLocationUpdated(@NonNull Location location) {
            Point user = location.getPosition();
            _userLocations.postValue(user);
            SharedPreferences.Editor editor = UserDataPref.edit();
            editor.putString(LONGITUDE_PREFERENCE_KEY,
                    String.valueOf(user.getLongitude()));
            editor.putString(LATITUDE_PREFERENCE_KEY,
                    String.valueOf(user.getLatitude()));
            editor.apply();
        }

        @Override
        public void onLocationStatusUpdated(@NonNull LocationStatus locationStatus) {
            if (locationStatus == LocationStatus.NOT_AVAILABLE) {
                System.out.println(Arrays.toString(LocationStatus.values()));
            }
        }
    };

    private Compass.CompassListener CompassListener = (azimuth, pitch, roll) -> _userAzimuth.postValue((int) azimuth);

    //----------------------------ViewModel-----------------------------------//


    public MainFragmentViewModel(@NonNull Application application) {
        super(application);
        UserDataPref = application.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
        subscribeToLocationUpdate();
        checkStartedUserLocations();
        setupCompass();
        firestoreDataBase = new FirestoreDataBase();
    }

    public void checkStartedUserLocations() {
        if(_userLocations.getValue() == null){
            Point p = new Point(
                    Double.parseDouble(UserDataPref
                            .getString(LATITUDE_PREFERENCE_KEY, "0")),
                    Double.parseDouble(UserDataPref
                            .getString(LONGITUDE_PREFERENCE_KEY, "0"))
            );
            _userLocations.postValue(p);
        }
    }

    public void setStationsMapObject(Map<String, PlacemarkMapObject> map){
        _stationMapObject.postValue(map);
    }


    private void subscribeToLocationUpdate() {
        locationManager.subscribeForLocationUpdates(
                DESIRED_ACCURACY,
                MINIMAL_TIME,
                MINIMAL_DISTANCE,
                USE_IN_BACKGROUND,
                FilteringMode.OFF,
                myLocationListener);
    }

    public void loadStationsPoint(){
        firestoreDataBase.getDataStations(new MyOnCompliteDataListener<List<DocumentSnapshot>>() {
            @Override
            public void onCompleteObservable(@NonNull Observable<List<DocumentSnapshot>> observable) {
                Disposable disposable = observable
                        .subscribeOn(Schedulers.io())
                        .map(s -> Stream.of(s)
                                .collect(Collectors.toMap(DocumentSnapshot::getId,
                                        FirestoreDataBase::castHashMapToPoint)))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(_stationPoints::postValue, throwable -> {
                            FirebaseCrashlytics.getInstance().recordException(throwable);
                        });

                compositeDisposable.add(disposable);
            }

            @Override
            public void onComplete(@NonNull List<DocumentSnapshot> documentSnapshots) {}

            @Override
            public void onCanceled() {
                //TODO onCanceled loading Stations point
            }
        });
    }

    private void setupCompass() {
        compass = new Compass(context, CompassListener);
        compass.start();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compass.stop();
        compositeDisposable.clear();
        locationManager.unsubscribe(myLocationListener);
    }
}
