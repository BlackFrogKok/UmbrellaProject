package com.samsungschool.umbrellaproject.Fragments.NavigationItems.HistoryFragment;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.samsungschool.umbrellaproject.Activity.MainActivity;
import com.samsungschool.umbrellaproject.Interface.MakeTransition;
import com.samsungschool.umbrellaproject.Interface.UserCallback;
import com.samsungschool.umbrellaproject.databinding.FragmentHistoryBinding;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class HistoryFragment extends Fragment {
    private HistoryAdapter historyAdapter;
    private FragmentHistoryBinding binding;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MakeTransition makeTransition;

    public static HistoryFragment newInstance() {
        Bundle args = new Bundle();
        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }



    private void getData(){
        binding.progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("history")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
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
                                        if(historyItems.size() == 0){
                                            binding.cleanHistory.setText("Пока что здесь ничего нет");
                                        }else{
                                            historyAdapter.setHistoryItems(historyItems);
                                        }
                                    }, throwable -> {
                                        Log.w("document2", throwable.getMessage());
                                    });
                            compositeDisposable.add(disposable);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Log.w("document2", "gg2");
                    }
                });


    }


    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        Animator anim = AnimatorInflater.loadAnimator(getActivity(), nextAnim);
        anim.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationEnd(Animator animation) {
                anim.removeListener(this);
                getData();
            }

            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}

        });

        return anim;
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
        RecyclerView recyclerView = binding.recyclerView;
        historyAdapter = new HistoryAdapter(new UserCallback() {
            @Override
            public void onClick(HistoryItem historyItem) {
                ((MainActivity)getActivity()).startHistoryItemFragment(historyItem);
            }
        });
        recyclerView.setAdapter(historyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setToolBarListener();
    }


    private void setToolBarListener(){
        binding.toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

//        binding.toolbar.getMenu().findItem(R.id.clear).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                historyItems.clear();
//                historyAdapter.notifyChanged();
//                return false;
//            }
//        });
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
