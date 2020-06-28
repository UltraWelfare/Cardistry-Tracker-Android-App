package com.tob.magicvault.Activities.MagicTrickEditAddActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.tob.magicvault.Helpers.BasicRatingBarChangeListener;
import com.tob.magicvault.Helpers.TextOnChangedWatcher;
import com.tob.magicvault.Helpers.Utils;
import com.tob.magicvault.Models.MagicTrickModel;
import com.tob.magicvault.R;
import com.tob.magicvault.Services.Repository.MagicTrickRepository;
import com.tob.magicvault.Services.Repository.NameAlreadyExistsException;


public class MagicTrickEditAddActivity extends AppCompatActivity {
    private static final int RESULT_GALLERY = 0;

    private EditText nameInput;
    private EditText descriptionInput;
    private EditText ownerInput;
    private TextView difficultyLabel;
    private RecyclerView stepRecyclerView;
    private ImageView imageViewPreview;
    private ChipGroup difficultyChipGroup;

    private String imageUri = "";
    private MagicTrickRepository repo;

    private boolean isEditing = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magic_trick_edit_add);
        repo = new MagicTrickRepository(this);

        isEditing = getIntent().getBooleanExtra("isEditing", false);
        MagicTrickModel editMagicTrick = getIntent().getParcelableExtra(MagicTrickModel.INTENT_KEY);

        nameInput = ((TextInputLayout) findViewById(R.id.nameInput)).getEditText();
        ownerInput = ((TextInputLayout) findViewById(R.id.ownerInput)).getEditText();
        descriptionInput = ((TextInputLayout) findViewById(R.id.descInput)).getEditText();
        difficultyChipGroup = findViewById(R.id.difficulty_chip_group);
        imageViewPreview = findViewById(R.id.image_preview);
        difficultyLabel = findViewById(R.id.trick_difficulty_label);


        RatingBar difficultyBar = findViewById(R.id.trick_difficulty_bar);
        difficultyBar.setOnRatingBarChangeListener(new BasicRatingBarChangeListener(difficulty -> difficultyLabel.setText(Utils.getDifficulty(this, difficulty))));

        stepRecyclerView = findViewById(R.id.step_recycler_view);
        stepRecyclerView.setAdapter(new StepEditAdapter(this));
        stepRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TextInputLayout nameLayout = findViewById(R.id.nameInput);
        nameInput.addTextChangedListener(new TextOnChangedWatcher(
                text -> {
                    if (nameInput.length() > 32) {
                        nameLayout.setError("Exceeding character limit. ");
                    } else {
                        nameLayout.setError(null);
                    }
                }));

        TextInputLayout ownerLayout = findViewById(R.id.ownerInput);
        ownerInput.addTextChangedListener(new TextOnChangedWatcher(
                text -> {
                    if (ownerInput.length() > 32) {
                        ownerLayout.setError("Exceeding character limit. ");
                    } else {
                        ownerLayout.setError(null);
                    }
                }));

        TextInputLayout descLayout = findViewById(R.id.descInput);
        descriptionInput.addTextChangedListener(new TextOnChangedWatcher(
                text -> {
                    if (descriptionInput.length() > 256) {
                        descLayout.setError("Exceeding character limit. ");
                    } else {
                        descLayout.setError(null);
                    }
                }));

        final Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {

            if (confirmData()) {
                new AlertDialog.Builder(this)
                        .setTitle("Confirmation")
                        .setMessage("Are you sure you want to save?")
                        .setIcon(R.drawable.ic_check_24px)
                        .setPositiveButton(android.R.string.yes, (_dialog, _whichButton) -> {
                            String resultName = nameInput.getText().toString().trim();
                            String resultDescription = descriptionInput.getText().toString().trim();
                            String resultOwner = ownerInput.getText().toString().trim();
                            int categoryChipId = difficultyChipGroup.getCheckedChipId();
                            if (categoryChipId == View.NO_ID) {
                                Snackbar
                                        .make(stepRecyclerView, "You need to select at least one category", Snackbar.LENGTH_SHORT)
                                        .show();
                                return;
                            }
                            String categoryText = ((Chip) findViewById(categoryChipId)).getText().toString();
                            Log.d("category", categoryText);


                            int difficultyValue = (int) difficultyBar.getRating() - 1;

                            MagicTrickModel trick = new MagicTrickModel(resultName, resultDescription, resultOwner, imageUri, difficultyValue, categoryText, ((StepEditAdapter) stepRecyclerView.getAdapter()).getSteps());


                            // If we are editing just update the magic trick
                            if (isEditing) {
                                trick.set_id(editMagicTrick.get_id());
                                repo.updateMagicTrick(trick);
                                finish();
                            } else {
                                // If we are adding, add the new trick.
                                try {
                                    repo.addMagicTrick(trick);
                                    Snackbar
                                            .make(findViewById(R.id.root), "Trick saved!", Snackbar.LENGTH_SHORT)
                                            .show();
                                    finish();
                                } catch (NameAlreadyExistsException e) {
                                    String error = e.getMessage();
                                    Snackbar
                                            .make(findViewById(R.id.root), error, Snackbar.LENGTH_SHORT)
                                            .setAction("Rename", v2 -> {
                                                nameInput.requestFocus();
                                                nameInput.getShowSoftInputOnFocus();
                                            })
                                            .show();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            } else {
                Snackbar
                        .make(findViewById(R.id.root), "Error in one or multiple fields", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });


        final Button selectYoutubeButton = findViewById(R.id.selectYoutubeButton);
        selectYoutubeButton.setOnClickListener(_v -> {
            // add input field
            final EditText youtubeUrlInput = new EditText(this);
            youtubeUrlInput.setInputType(InputType.TYPE_CLASS_TEXT);
            LinearLayout container = new LinearLayout(this);
            container.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int margin = Utils.convertPixelsToDp(100, this);
            lp.setMargins(margin, 0, margin, 0);
            youtubeUrlInput.setLayoutParams(lp);
            youtubeUrlInput.setGravity(android.view.Gravity.TOP | android.view.Gravity.LEFT);
            youtubeUrlInput.setInputType(InputType.TYPE_CLASS_TEXT);
            container.addView(youtubeUrlInput);
            final AlertDialog youtubeUrlDialog = new MaterialAlertDialogBuilder(this)
                    .setTitle("YouTube URL")
                    .setView(container)
                    .setMessage("Please enter a YouTube URL.")
                    .setPositiveButton(android.R.string.ok, (_dialog, _whichButton) -> {

                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();

            youtubeUrlDialog.show();
            youtubeUrlDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(_v1 -> {
                if (Utils.isYoutubeUrl(youtubeUrlInput.getText().toString())) {
                    this.imageUri = youtubeUrlInput.getText().toString();
                } else {
                    if (youtubeUrlInput.getText().toString().isEmpty()) {
                        Snackbar.make(findViewById(R.id.root), "URL can't be empty.", Snackbar.LENGTH_SHORT)
                                .show();
                        return;
                    } else {
                        Snackbar.make(findViewById(R.id.root), "Invalid Youtube URL.", Snackbar.LENGTH_SHORT)
                                .show();
                        this.imageUri = "";
                        return;
                    }
                }
                this.refreshImagePreview();
                youtubeUrlDialog.dismiss();
            });


        });
        final Button selectImage = findViewById(R.id.selectImageButton);
        selectImage.setOnClickListener(_v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, RESULT_GALLERY);
        });

        final MaterialButton addStepButton = findViewById(R.id.addStepButton);
        addStepButton.setOnClickListener((_v) -> {
            _v.requestFocusFromTouch();
            ((StepEditAdapter) stepRecyclerView.getAdapter()).addStep("");
        });

        final Button removeCancelButton = findViewById(R.id.removeCancelButton);
        removeCancelButton.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to " + (isEditing ? "remove" : "cancel") + " this trick?")
                .setIcon(R.drawable.ic_delete_24px)
                .setPositiveButton(android.R.string.yes, (_dialog, _whichButton) -> {
                    if (isEditing) {
                        this.repo.removeMagicTrick(editMagicTrick.get_id());
                    }
                    finish();
                })
                .setNegativeButton(android.R.string.no, null)
                .show());


        if (isEditing) {
            nameInput.setEnabled(false);
            nameInput.setText(editMagicTrick.getName());
            descriptionInput.setText(editMagicTrick.getDescription());
            ownerInput.setText(editMagicTrick.getOwner());
            for (int i = 0; i < difficultyChipGroup.getChildCount(); i++) {
                Chip c = (Chip) difficultyChipGroup.getChildAt(i);
                if (c.getText().equals(editMagicTrick.getCategory())) {
                    c.setChecked(true);
                    break;
                }
            }
            this.imageUri = editMagicTrick.getImage_uri();
            refreshImagePreview();

            difficultyBar.setProgress(editMagicTrick.getDifficulty());
            removeCancelButton.setText(this.getResources().getText(R.string.remove));
            for (String step : editMagicTrick.getSteps()) {
                ((StepEditAdapter) stepRecyclerView.getAdapter()).addStep(step);
            }
        } else {
            difficultyBar.setProgress(0);
        }


        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                ((StepEditAdapter) stepRecyclerView.getAdapter()).removeStep(viewHolder);
            }
        });

        helper.attachToRecyclerView(stepRecyclerView);
    }

    private void refreshImagePreview() {
        Utils.getImageFromUri(this, this.imageUri, imageViewPreview);
    }

    private boolean validateName() {
        String NameInput = nameInput.getText().toString().trim();
        boolean isNumeric = android.text.TextUtils.isDigitsOnly(NameInput);
        TextInputLayout nameLayout = findViewById(R.id.nameInput);
        if (NameInput.isEmpty()) {
            nameLayout.setError("Field can't be empty. ");
            return false;
        } else if (nameInput.length() > 32) {
            nameLayout.setError("Exceeding character limit. ");
            return false;
        } else {
            if (isNumeric) {
                nameLayout.setError("Input must include at least one letter.");
                return false;
            }
            nameLayout.setError(null);
            return true;
        }
    }

    private boolean validateOwner() {
        String OwnerInput = ownerInput.getText().toString().trim();
        boolean isNumeric = android.text.TextUtils.isDigitsOnly(OwnerInput);
        TextInputLayout ownerLayout = findViewById(R.id.ownerInput);
        if (OwnerInput.isEmpty()) {
            ownerLayout.setError("Field can't be empty. ");
            return false;
        } else if (ownerInput.length() > 32) {
            ownerLayout.setError("Exceeding character limit. ");
            return false;
        } else {
            if (isNumeric) {
                ownerLayout.setError("Input must include at least one letter.");
                return false;
            }
            ownerLayout.setError(null);
            return true;
        }
    }

    private boolean validateDesc() {
        String DescInput = descriptionInput.getText().toString().trim();
        TextInputLayout descriptionLayout = findViewById(R.id.descInput);
        if (DescInput.isEmpty()) {
            descriptionLayout.setError("Field can't be empty. ");
            return false;
        } else if (descriptionInput.length() > 256) {
            descriptionLayout.setError("Exceeding character limit. ");
            return false;
        } else {
            descriptionInput.setError(null);
            return true;
        }
    }

    private boolean confirmData() {
        if (!validateName()) {
            nameInput.requestFocus();
            nameInput.getShowSoftInputOnFocus();
        }
        if (!validateOwner()) {
            ownerInput.requestFocus(5);
            ownerInput.getShowSoftInputOnFocus();
        }
        if (!validateDesc()) {
            descriptionInput.requestFocus();
            descriptionInput.getShowSoftInputOnFocus();
        }
        return validateName() & validateOwner() & validateDesc();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_GALLERY) {
            if (data != null) {
                Uri uri = data.getData();
                // Take permission for a persistent URI across reboots / restarts.
                this.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                this.imageUri = uri.toString();
            }
            this.refreshImagePreview(); // Refresh preview image after acquiring the new image uri
        }
    }
}
