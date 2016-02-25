package com.patfives.steps;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.patfives.steps.model.DayView;
import com.patfives.steps.ui.DayViewHolder;

import java.util.ArrayList;
import java.util.List;

public class StepsAdapter extends RecyclerView.Adapter<DayViewHolder> {

    List<DayView> items = new ArrayList<>();

    @Override
    public DayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View dv = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_day, parent, false);
        DayViewHolder dayViewHolder = new DayViewHolder(dv);
        return dayViewHolder;
    }

    public void setItems(List<DayView> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    private DayView getItem(int position){
        if (items == null || items.isEmpty() || (position >= items.size() || position < 0)) {
            return null;
        }
        return items.get(position);
    }

    @Override
    public void onBindViewHolder(DayViewHolder holder, int position) {
        DayView stepData = getItem(position);
        holder.bind(stepData);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
