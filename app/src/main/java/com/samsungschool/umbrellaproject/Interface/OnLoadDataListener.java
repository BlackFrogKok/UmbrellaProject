package com.samsungschool.umbrellaproject.Interface;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;

public interface OnLoadDataListener<TResult> {
    void onComplete(@NonNull Observable<TResult> observable);

}
