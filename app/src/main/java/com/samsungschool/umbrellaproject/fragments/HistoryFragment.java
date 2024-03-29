package com.samsungschool.umbrellaproject.fragments;

import static com.samsungschool.umbrellaproject.utils.ApiUtils.API_HISTORY_COLLECTION;
import static com.samsungschool.umbrellaproject.utils.ApiUtils.API_USERS_COLLECTION;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.activities.MainActivity;
import com.samsungschool.umbrellaproject.adapters.HistoryAdapter;
import com.samsungschool.umbrellaproject.databinding.FragmentHistoryBinding;
import com.samsungschool.umbrellaproject.items.HistoryItem;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class HistoryFragment extends Fragment {

    private HistoryAdapter historyAdapter;
    private FragmentHistoryBinding binding;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static HistoryFragment newInstance() {
        Bundle args = new Bundle();
        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void getData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            FirebaseFirestore.getInstance()
                    .collection(API_USERS_COLLECTION)
                    .document(FirebaseAuth.getInstance().getUid())
                    .collection(API_HISTORY_COLLECTION)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Disposable disposable = Observable
                                    .fromArray(task.getResult().getDocuments())
                                    .subscribeOn(Schedulers.io())
                                    .map(list -> Stream.of(list)
                                            .map(l -> l.toObject(HistoryItem.class))
                                            .collect(Collectors.toList())
                                    )
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(historyItems -> {
                                        binding.progressBar.setVisibility(View.GONE);
                                        Log.w("document2", historyItems.toString());
                                        if (historyItems.size() == 0) {
                                            binding.cleanHistory.setText(R.string.history_empty);
                                        } else {
                                            historyAdapter.setHistoryItems(historyItems);
                                        }
                                    }, throwable -> Log.w("document2", throwable.getMessage()));
                            compositeDisposable.add(disposable);
                        }
                    })
                    .addOnFailureListener(e -> {
                        binding.progressBar.setVisibility(View.GONE);
                        Log.w("document2", "gg2");
                    });
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();
        RecyclerView recyclerView = binding.recyclerView;
        historyAdapter = new HistoryAdapter(historyItem -> ((MainActivity) requireActivity()).startHistoryItemFragment(historyItem));
        recyclerView.setAdapter(historyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }
}
