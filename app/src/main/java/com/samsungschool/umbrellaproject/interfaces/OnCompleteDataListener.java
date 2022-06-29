package com.samsungschool.umbrellaproject.interfaces;

import androidx.annotation.NonNull;

import io.reactivex.rxjava3.core.Observable;

public interface OnCompleteDataListener<TResult> {

    default void onCompleteObservable(@NonNull Observable<TResult> observable) {};
    void onComplete(@NonNull TResult result);
    void onCanceled();
}
