package com.tob.magicvault.Activities.MagicTrickDetailActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tob.magicvault.R;

import java.util.ArrayList;

public class StepDetailsAdapter extends RecyclerView.Adapter<StepDetailsAdapter.StepDetailViewHolder> {

    private final Context context;
    private final ArrayList<String> steps;

    StepDetailsAdapter(Context context, ArrayList<String> steps) {
        this.context = context;
        this.steps = steps;
    }

    @NonNull
    @Override
    public StepDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(this.context);
        View view = li.inflate(R.layout.step_details_row, parent, false);
        return new StepDetailViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull StepDetailViewHolder holder, int position) {
        holder.stepLabel.setText("Step " + (holder.getAdapterPosition() + 1) + ":");
        holder.stepDescription.setText(steps.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    static class StepDetailViewHolder extends RecyclerView.ViewHolder {
        final TextView stepLabel;
        final TextView stepDescription;

        StepDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            stepLabel = itemView.findViewById(R.id.step_label);
            stepDescription = itemView.findViewById(R.id.step_description_text);
        }
    }
}
