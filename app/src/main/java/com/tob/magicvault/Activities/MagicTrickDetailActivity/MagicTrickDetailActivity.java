package com.tob.magicvault.Activities.MagicTrickDetailActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tob.magicvault.Helpers.Utils;
import com.tob.magicvault.Models.MagicTrickModel;
import com.tob.magicvault.R;
import com.tob.magicvault.databinding.MagicTrickDetailsBinding;

public class MagicTrickDetailActivity extends AppCompatActivity {

    MagicTrickDetailsBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        super.onCreate(savedInstanceState);
        this.binding = MagicTrickDetailsBinding.inflate(getLayoutInflater());


        setContentView(binding.getRoot());
        Intent intent = this.getIntent();
        MagicTrickModel magicTrickModel = intent.getParcelableExtra(MagicTrickModel.INTENT_KEY);

        // -- Update UI Components -- //
        binding.trickTitleText.setText(magicTrickModel.getName());
        binding.trickDescriptionText.setText(magicTrickModel.getDescription());
        binding.trickOwnerText.setText(magicTrickModel.getOwner());
        Utils.getImageFromUri(this, magicTrickModel.getImage_uri(), binding.imagePreview);

        // Show "Watch YouTube" button if the image uri is a valid youtube url.
        if (Utils.isYoutubeUrl(magicTrickModel.getImage_uri())) {
            binding.watchYoutubeButton.setVisibility(View.VISIBLE);
            binding.watchYoutubeButton.setOnClickListener(_v -> {
                Intent url_intent = new Intent(Intent.ACTION_VIEW);
                url_intent.setData(Uri.parse(magicTrickModel.getImage_uri()));
                startActivity(url_intent);
            });
        }

        // Setup recycler view to display steps if there are any, otherwise set the step label to "No Steps"
        if (magicTrickModel.getSteps().size() > 0) {
            binding.stepRecyclerView.setAdapter(new StepDetailsAdapter(this, magicTrickModel.getSteps()));
            binding.stepRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            binding.stepLabel.setText(R.string.no_available_steps);
        }

        binding.trickDifficultyLabel.setText(getString(R.string.difficultyLabelPrefix) + Utils.getDifficulty(this, magicTrickModel.getDifficulty()));

        binding.trickDifficultyBar.setProgress(magicTrickModel.getDifficulty() + 1);
    }
}
