package com.tob.magicvault.Activities.MagicTrickActivity;

import android.animation.Animator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tob.magicvault.Activities.AboutActivity.AboutActivity;
import com.tob.magicvault.Activities.MagicTrickEditAddActivity.MagicTrickEditAddActivity;
import com.tob.magicvault.Models.MagicTrickModel;
import com.tob.magicvault.R;
import com.tob.magicvault.Services.Repository.MagicTrickRepository;
import com.tob.magicvault.Services.Repository.NameAlreadyExistsException;
import com.tob.magicvault.databinding.MagicTrickMainBinding;

import java.util.ArrayList;


public class MagicTrickActivity extends AppCompatActivity {


    private MagicTrickMainBinding binding;
    private MagicTrickRepository repo;
    // This list contains all the updated data.
    // The adapter will have a different list that should respect the filtering.
    private ArrayList<MagicTrickModel> list;

    private MagicTrickListAdapter adapter;
    private boolean isCardCompact = false;

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh database cache after resuming (probably coming from either Add Activity or Edit Activity)
        binding.getRoot().requestFocusFromTouch();
        repo.loadMagicTricks();
        this.list = repo.getMagicTricks();
        filter(binding.magicTrickSearchView.getQuery().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MagicTrickMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // -- Refresh DB -- //
        repo = new MagicTrickRepository(this);
        repo.loadMagicTricks();
        this.list = repo.getMagicTricks();
        // Add adapter to the recycler view
        this.adapter = new MagicTrickListAdapter(this, this.list);
        this.adapter.setCardCompact(isCardCompact);

        binding.trickRecyclerView.setAdapter(this.adapter);
        binding.trickRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.topAppBar.setOnMenuItemClickListener((menuItem) -> {
            if (menuItem.getItemId() == R.id.about_drop_menu) {
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
            } else if (menuItem.getItemId() == R.id.add_trick_button) {
                View v1 = findViewById(R.id.circular_reveal);
                int w = binding.getRoot().getWidth();
                int h = binding.getRoot().getHeight();
                int radius = (int) Math.round(Math.sqrt(Math.pow(w, 2.0) + Math.pow(h, 2.0)));
                final Animator animation = ViewAnimationUtils.createCircularReveal(v1, binding.getRoot().getWidth(), 0, 0, radius);
                animation.setDuration(300);
                animation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        v1.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        openMagicTrickAddActivity();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                animation.start();
                return true;
            } else if (menuItem.getItemId() == R.id.swap_view_mode) {
                this.isCardCompact = !this.isCardCompact;
                if (this.isCardCompact) {
                    menuItem.setIcon(R.drawable.stream_view_anim);
                    binding.trickRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                } else {
                    menuItem.setIcon(R.drawable.grid_view_anim);
                    binding.trickRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                }
                this.adapter.setCardCompact(isCardCompact);
                Drawable d = menuItem.getIcon();
                if (d instanceof AnimatedVectorDrawable) {
                    ((AnimatedVectorDrawable) d).start();
                }
            }
            return false;
        });

        // Set query text listener for the search view
        binding.magicTrickSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });


        // Check if the app is fresh installed
        SharedPreferences prefs = getSharedPreferences("com.tob.magicvault", MODE_PRIVATE);
        if (prefs.getBoolean("firstLaunch", true)) {

            MagicTrickModel doubleLift = new MagicTrickModel("Double Lift", "Grab two cards and make it seem as one. The top card may be kept secret by lifting the top two cards as one, making it seem as if only the top card is picked up", "Richard Neve", "https://www.youtube.com/watch?v=Gjd4EDm3EWU", 1, "", new ArrayList<String>() {{
                add("Grab two cards by pinkie counting.");
                add("Grab them from the bottom right corner tightly.");
                add("Flip them over as one.");
            }});

            MagicTrickModel swivelFalseCut = new MagicTrickModel("Swivel False Cut", "Three packet cut that retains that original deck order", "Unknown", "https://www.youtube.com/watch?v=ZexjwH_lSQs", 0, "False Cut", new ArrayList<String>() {{
                add("Use your Left Thumb to lift up half the deck from the bottom.");
                add("Rotate the lifted packet around the Right Middle Finger until it has spun about 180 degrees.");
                add("Let the packet fall into your Left Hand.");
                add("Place the remaining packet in the Right Hand on top.");
            }});

            MagicTrickModel riffleShuffle = new MagicTrickModel("Riffle Shuffle", "Riffle shuffling is a procedure used to randomize a deck of playing cards", "Unknown", "https://www.youtube.com/watch?v=f6ZD1lDbW3M", 0, "Shuffle", new ArrayList<String>() {{
                add("Lift up the deck slightly off the table. Bend the packets inward with your 2nd and 3rd fingers, and the thumb while pushing in with the index finger");
                add("Rotate the packets so that the bottom inner corners meet, and slowly release the thumbs. This will allow the cards to riffle down.");
                add("Push the packets in closer together to allow the cards to overlap as they riffle down.");
                add("Push and rotate the packets so that the long edges are squared off with each other.");
            }});

            MagicTrickModel diagonalPalmShift = new MagicTrickModel("Diagonal Palm Shift", "Diagonal Palm Shift is a way to steal a card from the spectator eyes without them noticing it.", "S. W. Erdnase", "https://www.youtube.com/watch?v=cQSjjoM8-ww", 2, "Palm", new ArrayList<String>() {{
                add("Place the card in the middle of the deck.");
                add("Slightly jog inwards while pushing the card towards yourself.");
                add("Grab the top left corner of the card with your thumb and the opposite long side of the card with the middle finger.");
                add("Continue to push until your thumb clears the long side of the deck.");
                add("Once you reach the long side of the deck, use your pinky finger to rotate the card using the right bottom corner.");
                add("The card should be on your 3 other fingers to palm it");
            }});


            try {
                repo.addMagicTrick(doubleLift, swivelFalseCut, riffleShuffle, diagonalPalmShift);
            } catch (NameAlreadyExistsException e) {
                e.printStackTrace();
            }
            prefs.edit().putBoolean("firstLaunch", false).apply();
        }
    }

    /**
     * Filters the data of the adapter based on the text parameter
     *
     * @param query The query that should match the trick name
     */
    private void filter(String query) {
        // New array list that will hold the filtered data
        ArrayList<MagicTrickModel> filteredTricks = new ArrayList<>();
        if (query.trim().equals("")) {
            // Show everything if the query is empty
            adapter.onFilter(this.list);
        }

        for (MagicTrickModel s : this.list) {
            if (s.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredTricks.add(s);
            }
        }
        // Pass the filtered list to the adapter
        adapter.onFilter(filteredTricks);
    }

    private void openMagicTrickAddActivity() {
        Intent intent = new Intent(this, MagicTrickEditAddActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) this);
        startActivityForResult(intent, 0, options.toBundle());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        View v1 = findViewById(R.id.circular_reveal);
        int w = binding.getRoot().getWidth();
        int h = binding.getRoot().getHeight();
        int radius = (int) Math.round(Math.sqrt(Math.pow(w, 2.0) + Math.pow(h, 2.0)));

        final Animator animation = ViewAnimationUtils.createCircularReveal(v1, binding.getRoot().getWidth(), 0, radius, 1);
        animation.setDuration(300);
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                v1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                v1.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animation.start();
    }

}
