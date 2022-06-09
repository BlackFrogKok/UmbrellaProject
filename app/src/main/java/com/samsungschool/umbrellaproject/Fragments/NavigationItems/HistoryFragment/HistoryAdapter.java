package com.samsungschool.umbrellaproject.Fragments.NavigationItems.HistoryFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samsungschool.umbrellaproject.Interface.UserCallback;
import com.samsungschool.umbrellaproject.databinding.ItemHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ItemViewHolder> {
    List<HistoryItem> historyItems = new ArrayList<HistoryItem>();
    UserCallback userCallback;

    public HistoryAdapter(UserCallback userCallback){
        this.userCallback = userCallback;
    }

    public void setHistoryItems(List<HistoryItem> historyItems) {
        this.historyItems = historyItems;
        notifyChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryBinding binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ((ItemViewHolder) holder).setBinding(historyItems.get(position), userCallback);
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    public void notifyChanged(){
        notifyDataSetChanged();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        ItemHistoryBinding binding;
        UserCallback userCallback;
        public ItemViewHolder(ItemHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setBinding(HistoryItem historyItem, UserCallback userCallback) {
            binding.textView.setText(historyItem.getAddress());
            binding.textView2.setText(historyItem.getDate());
            binding.textView3.setText(historyItem.getTime());
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userCallback.onClick(historyItem);
                }
            });
        }
    }
}
