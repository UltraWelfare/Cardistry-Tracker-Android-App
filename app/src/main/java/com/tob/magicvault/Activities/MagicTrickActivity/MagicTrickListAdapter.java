package com.tob.magicvault.Activities.MagicTrickActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.RecyclerView;

import com.tob.magicvault.Activities.MagicTrickDetailActivity.MagicTrickDetailActivity;
import com.tob.magicvault.Activities.MagicTrickEditAddActivity.MagicTrickEditAddActivity;
import com.tob.magicvault.Helpers.Utils;
import com.tob.magicvault.Models.MagicTrickModel;
import com.tob.magicvault.R;
import com.tob.magicvault.databinding.MagicTrickRow2Binding;

import java.util.ArrayList;


public class MagicTrickListAdapter extends RecyclerView.Adapter<MagicTrickListAdapter.MagicTrickRowHolder> {

    private ArrayList<MagicTrickModel> data;
    private final Context context;

    private boolean isCardCompact = false;


    public MagicTrickListAdapter(Context context, ArrayList<MagicTrickModel> data) {
        this.context = context;
        this.data = data;
    }

    /**
     * Should be called when a filter is applied.
     * The adapter will refresh with the list parameter.
     * PS : Sorts alphabetically
     *
     * @param list The array list to use as data.
     */
    public void onFilter(ArrayList<MagicTrickModel> list) {
        this.data = list;
        list.sort((value1, value2) -> value1.getName().compareTo(value2.getName()));
        this.notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(@NonNull MagicTrickRowHolder holder) {
        super.onViewRecycled(holder);
        holder.isDirty = true; // Set dirty to replace cached image when it binds again
    }

    @NonNull
    @Override
    public MagicTrickRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        MagicTrickRow2Binding itemBinding = MagicTrickRow2Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        MagicTrickRowHolder viewHolder = new MagicTrickRowHolder(itemBinding);

        itemBinding.editButton.setOnClickListener(_v -> {
            Intent intent = new Intent(context, MagicTrickEditAddActivity.class);
            intent.putExtra("isEditing", true);
            intent.putExtra(MagicTrickModel.INTENT_KEY, data.get(viewHolder.getAdapterPosition()));
            context.startActivity(intent);
        });

        itemBinding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(context, MagicTrickDetailActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context,
                    Pair.create(itemBinding.trickTitleText, context.getResources().getString(R.string.trick_title)),
                    Pair.create(itemBinding.trickOwnerText, context.getResources().getString(R.string.owner)),
                    Pair.create(itemBinding.trickDifficultyBar, context.getResources().getString(R.string.difficulty_bar)),
                    Pair.create(itemBinding.imagePreview, context.getResources().getString(R.string.image_preview)));
            MagicTrickModel selectedModel = data.get(viewHolder.getAdapterPosition());
            intent.putExtra(MagicTrickModel.INTENT_KEY, selectedModel);
            context.startActivity(intent, options.toBundle());
        });

        itemBinding.cardTrickExpandButton.setOnClickListener(_v -> {
            int from, to;
            if (itemBinding.cardTrickExpandLayout.getVisibility() == View.GONE) {
                itemBinding.cardTrickExpandLayout.setVisibility(View.VISIBLE);
                from = 0;
                to = 180;
            } else {
                itemBinding.cardTrickExpandLayout.setVisibility(View.GONE);
                from = 180;
                to = 0;
            }
            Animation a = new RotateAnimation(from, to,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);
            a.setInterpolator(new FastOutSlowInInterpolator());
            a.setDuration(420);
            a.setFillAfter(true);
            itemBinding.cardTrickExpandButton.startAnimation(a);

            notifyItemChanged(viewHolder.getAdapterPosition(), new Object());
        });


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MagicTrickRowHolder holder, int position) {
        MagicTrickModel selected = data.get(position);

        // Cap owner and owner text views if they are too big
        holder.binding.trickTitleText.setText(Utils.cap(selected.getName(), 22));
        holder.binding.trickOwnerText.setText(Utils.cap(selected.getOwner(), 22));
        holder.binding.trickDescriptionText.setText(selected.getDescription());
        holder.binding.trickDifficultyBar.setRating(selected.getDifficulty() + 1);

        if (!selected.getCategory().isEmpty()) {
            holder.binding.trickCategoryChip.setText(selected.getCategory());
        } else {
            holder.binding.trickCategoryChip.setVisibility(View.INVISIBLE);
        }
        if (holder.isDirty) {
            Utils.getImageFromUri(context, selected.getImage_uri(), holder.binding.imagePreview);
            holder.isDirty = false;
        }

        if (isCardCompact()) {
            holder.binding.cardTrickExpandButton.setVisibility(View.GONE);
        } else {
            holder.binding.cardTrickExpandButton.setVisibility(View.VISIBLE);
        }
    }

    public ArrayList<MagicTrickModel> getData() {
        return data;
    }

    public void setData(ArrayList<MagicTrickModel> data) {
        this.data = data;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public boolean isCardCompact() {
        return isCardCompact;
    }

    public void setCardCompact(boolean cardCompact) {
        isCardCompact = cardCompact;
    }

    static class MagicTrickRowHolder extends RecyclerView.ViewHolder {
        final MagicTrickRow2Binding binding;

        boolean isDirty = true;

        MagicTrickRowHolder(@NonNull MagicTrickRow2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}