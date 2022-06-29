package com.samsungschool.umbrellaproject.adapters;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samsungschool.umbrellaproject.databinding.ItemHistoryBinding;
import com.samsungschool.umbrellaproject.interfaces.UserCallback;
import com.samsungschool.umbrellaproject.items.HistoryItem;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ItemViewHolder> {

    private List<HistoryItem> historyItems = new ArrayList<>();
    private final UserCallback userCallback;

    public HistoryAdapter(UserCallback userCallback) {
        this.userCallback = userCallback;
    }

    public void setHistoryItems(List<HistoryItem> historyItems) {
        int min = min(getItemCount(), historyItems.size());
        int max = max(getItemCount(), historyItems.size());
        this.historyItems = new ArrayList<>(historyItems);
        notifyItemRangeChanged(min, max);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryBinding binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding, userCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bind(historyItems.get(position));
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final ItemHistoryBinding binding;
        private final UserCallback userCallback;

        public ItemViewHolder(ItemHistoryBinding binding, UserCallback userCallback) {
            super(binding.getRoot());
            this.binding = binding;
            this.userCallback = userCallback;
        }

        public void bind(HistoryItem historyItem) {
            binding.textView.setText(historyItem.getAddress());
            binding.textView2.setText(historyItem.getDate());
            binding.textView3.setText(historyItem.getTime());
            binding.getRoot().setOnClickListener(v -> userCallback.onClick(historyItem));
        }
    }
}
