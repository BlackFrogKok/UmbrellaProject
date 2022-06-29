package com.samsungschool.umbrellaproject.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.samsungschool.umbrellaproject.models.User;
import com.samsungschool.umbrellaproject.items.HistoryItem;
import com.samsungschool.umbrellaproject.interfaces.OnCompleteDataListener;
import com.samsungschool.umbrellaproject.interfaces.OnTaskCompleteListener;
import com.yandex.mapkit.geometry.Point;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

public class FirestoreDatabase {

    private final FirebaseFirestore dataBase = FirebaseFirestore.getInstance();
    private final String userPath = FirebaseAuth.getInstance().getUid();
    private int count;
    private List<DocumentSnapshot> res;

    public void getDocument(String documentID, OnCompleteDataListener<DocumentSnapshot> listener) {
        dataBase.collection("stations")
                .document(documentID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onComplete(task.getResult());
                    }
                })
                .addOnFailureListener(e -> FirebaseCrashlytics.getInstance().recordException(e));
    }

    public void getDataStations(OnCompleteDataListener<List<DocumentSnapshot>> listener) {
        dataBase.collection("stations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onCompleteObservable(Observable.fromArray(task.getResult().getDocuments()));
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onCanceled();
                    FirebaseCrashlytics.getInstance().recordException(e);
                });
    }

    public void getUmbrellaCount(String stationID, OnCompleteDataListener<Integer> listener) {
        dataBase.collection("stations")
                .document(stationID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onComplete(((ArrayList<Integer>) (task.getResult().get("freeUmbrella"))).size());
                    }

                })
                .addOnFailureListener(e -> {
                });

    }

    public void closeStation(String stationID, boolean isTake) {
        Map<String, Object> map = new HashMap();
        map.put("issued", true);
        map.put("trueTake", isTake);
        dataBase.collection("stations")
                .document(stationID)
                .collection("auth")
                .document("isIssued")
                .update(map)
                .addOnCompleteListener(task -> {
                })
                .addOnFailureListener(e -> FirebaseCrashlytics.getInstance().recordException(e));
    }

    private void getStationAdress(String stationID, OnCompleteListener listener) {
        dataBase.collection("stations")
                .document(stationID)
                .get()
                .addOnCompleteListener(listener)
                .addOnFailureListener(e -> FirebaseCrashlytics.getInstance().recordException(e));

    }

    public void getUser(String uID, OnCompleteDataListener<User> listener) {
        dataBase.collection("users")
                .document(uID)
                .get()
                .addOnCompleteListener((OnCompleteListener) task -> {
                    if (task.isSuccessful()) {
                        listener.onComplete(((DocumentSnapshot) task.getResult()).toObject(User.class));
                    }
                })
                .addOnFailureListener(e -> FirebaseCrashlytics.getInstance().recordException(e));

    }

    public void addHistory(String stationID, String name) {
        getStationAdress(stationID, task -> {
            if (task.isSuccessful()) {
                HistoryItem h = new HistoryItem();
                h.setAddress(((DocumentSnapshot) task.getResult()).get("adress", String.class));
                h.setStatus(false);
                h.setDate(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date()));
                h.setTime("Аренда");
                h.setTimeGet(getCurrentTime());
                h.setStationGetID(stationID);
                dataBase.collection("users")
                        .document(FirebaseAuth.getInstance().getUid())
                        .collection("history")
                        .document(name)
                        .set(h)
                        .addOnCompleteListener(task1 -> {
                        })
                        .addOnFailureListener(e -> FirebaseCrashlytics.getInstance().recordException(e));
            }
        });


    }

    public void endHistory(String stationID, String name) {
        DocumentReference b = dataBase.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("history")
                .document(name);
        b.update("timePut", getCurrentTime());
        b.update("time", "Сдан");
        b.update("stationPutID", stationID);


    }


    public void getUmbrella(int umbrella, String stationID, OnTaskCompleteListener listener) {
        dataBase.collection("stations")
                .document(stationID)
                .collection("auth")
                .document("status")
                .update("umbrella", umbrella)
                .addOnCompleteListener((OnCompleteListener) task -> dataBase.collection("stations")
                        .document(stationID)
                        .collection("auth")
                        .document("status")
                        .update("status", "giving")
                        .addOnCompleteListener((OnCompleteListener) task1 -> listener.OnComplete())
                        .addOnFailureListener(e -> FirebaseCrashlytics.getInstance().recordException(e)))
                .addOnFailureListener(e -> FirebaseCrashlytics.getInstance().recordException(e));
    }

    public void returnUmbrella(int umbrella, String stationID, OnTaskCompleteListener listener) {
        dataBase.collection("stations")
                .document(stationID)
                .collection("auth")
                .document("status")
                .update("umbrella", umbrella)
                .addOnCompleteListener((OnCompleteListener) task -> dataBase.collection("stations")
                        .document(stationID)
                        .collection("auth")
                        .document("status")
                        .update("status", "returning")
                        .addOnCompleteListener((OnCompleteListener) task1 -> listener.OnComplete())
                        .addOnFailureListener(e -> FirebaseCrashlytics.getInstance().recordException(e)))
                .addOnFailureListener(e -> FirebaseCrashlytics.getInstance().recordException(e));
    }

    public static Point castHashMapToPoint(DocumentSnapshot document) {
        HashMap hashMap = (HashMap) document.get("location");
        return new Point((Double) hashMap.get("latitude"), (Double) hashMap.get("longitude"));
    }

    private String getCurrentTime() {
        String time;
        Calendar calendar = Calendar.getInstance();
        Log.d("TAG", calendar.getTime().toString());
        return String.valueOf(calendar.getTime().getHours()) + ":" + String.valueOf(calendar.getTime().getMinutes());
    }
}
