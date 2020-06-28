package com.tob.magicvault.Activities.MagicTrickEditAddActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tob.magicvault.Helpers.TextAfterChangedWatcher;
import com.tob.magicvault.R;

import java.util.ArrayList;


public class StepEditAdapter extends RecyclerView.Adapter<StepEditAdapter.StepViewHolder> {
    private final ArrayList<String> steps;
    private final Context context;

    public StepEditAdapter(Context ct) {
        context = ct;
        steps = new ArrayList<>();
    }

    public void addStep(String step) {
        steps.add(step);
        this.notifyItemInserted(steps.size() - 1);
    }

    public void removeStep(RecyclerView.ViewHolder viewHolder) {
        int adapterPosition = viewHolder.getAdapterPosition();
        steps.remove(adapterPosition);

        notifyItemRemoved(adapterPosition);

        for (int i = adapterPosition; i < steps.size(); i++) {
            notifyItemChanged(i);
        }
    }


    @NonNull
    @Override
    public StepEditAdapter.StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.step_text_edit, parent, false);
        StepViewHolder viewHolder = new StepViewHolder(view);
        viewHolder.removeButton.setOnClickListener(_v -> {
            steps.remove(viewHolder.getAdapterPosition());
            notifyItemRemoved(viewHolder.getAdapterPosition());

            for (int i = viewHolder.getAdapterPosition(); i < steps.size(); i++) {
                notifyItemChanged(i);
            }

        });

        viewHolder.stepText.addTextChangedListener(new TextAfterChangedWatcher(stepText -> steps.set(viewHolder.getAdapterPosition(), stepText)
        ));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StepEditAdapter.StepViewHolder holder, int position) {
        holder.stepText.setText(steps.get(position));
        int labelPosition = holder.getAdapterPosition() + 1;
        holder.stepLabel.setText(context.getResources().getString(R.string.step_edit_indicator_label, labelPosition));
//        holder.stepLabel.setText(context.getResources().getString(R.string.step) + " " + (holder.getAdapterPosition() + 1));
    }


    @Override
    public int getItemCount() {
        return steps.size();
    }

    static class StepViewHolder extends RecyclerView.ViewHolder {
        final EditText stepText;
        final Button removeButton;
        final TextView stepLabel;

        StepViewHolder(@NonNull View itemView) {
            super(itemView);
            stepText = itemView.findViewById(R.id.step_input);
            removeButton = itemView.findViewById(R.id.remove_button);
            stepLabel = itemView.findViewById(R.id.step_label);
        }
    }

    public ArrayList<String> getSteps() {
        return steps;
    }
}