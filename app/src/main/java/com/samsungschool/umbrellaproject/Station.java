package com.samsungschool.umbrellaproject;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.samsungschool.umbrellaproject.Interface.MyOnCompliteDataListener;

public class Station {

    private final FirebaseFirestore dataBase = FirebaseFirestore.getInstance();


    public void checkQrData(String result, Context context, MyOnCompliteDataListener<String> listener){
        String[] subStr;
        subStr = result.split("-");
        if (subStr.length == 2){
            dataBase.collection("stations").document(subStr[1])
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful() &
                                    task.getResult().get("securityWord").equals(subStr[0])){
                                listener.onComplete(subStr[1]);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            Log.w("document2", e.getMessage());
                        }
                    });
        }
    }
}
