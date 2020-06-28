package com.tob.magicvault.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class MagicTrickModel implements Parcelable {

    private long _id;

    @NonNull
    private String name;

    @NonNull
    private String description;

    @NonNull
    private String owner;

    @NonNull
    private String image_uri;

    @NonNull
    private int difficulty; // 0 - 5

    @NonNull
    private String category;

    @NonNull
    private ArrayList<String> steps;


    public MagicTrickModel(String name, String description, String owner, String imageUri, int difficulty, String category, ArrayList<String> steps) {
        setData(name, description, owner, imageUri, difficulty, category, steps);
    }

    // Helper class to use for the parcelable
    private void setData(String name, String description, String owner, String imageUri, int difficulty, String category, ArrayList<String> steps) {
        setName(name);
        setDescription(description);
        setOwner(owner);
        setImage_uri(imageUri);
        setDifficulty(difficulty);
        setCategory(category);
        setSteps(steps);
    }

    @NonNull
    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }


    @NonNull
    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    public String getOwner() {
        return owner;
    }

    private void setOwner(String owner) {
        this.owner = owner;
    }

    @NonNull
    public String getImage_uri() {
        return image_uri;
    }

    private void setImage_uri(@NonNull String image_uri) {
        this.image_uri = image_uri;
    }

    @NonNull
    public int getDifficulty() {
        return difficulty;
    }

    private void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    @NonNull
    public String getCategory() {
        return category;
    }

    public void setCategory(@NonNull String category) {
        this.category = category;
    }

    @NonNull
    public ArrayList<String> getSteps() {
        return steps;
    }

    private void setSteps(ArrayList<String> steps) {
        this.steps = steps;
    }

    @NonNull
    @Override
    public String toString() { // Debug purposes
        return this.name + ": " + this.description + ", Owner: " + this.owner + " (" + this.difficulty + ")";
    }

    @NonNull
    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    // Parcelable implementation

    // Used for putExtra
    public static final String INTENT_KEY = "magictrickmodel";

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.owner);
        dest.writeString(this.image_uri);
        dest.writeInt(this.difficulty);
        dest.writeString(this.category);
        dest.writeStringList(this.steps);
        dest.writeLong(this._id);
    }


    public static final Parcelable.Creator<MagicTrickModel> CREATOR
            = new Parcelable.Creator<MagicTrickModel>() {
        public MagicTrickModel createFromParcel(Parcel in) {
            return new MagicTrickModel(in);
        }

        @Override
        public MagicTrickModel[] newArray(int size) {
            return new MagicTrickModel[size];
        }
    };

    private MagicTrickModel(Parcel in) {
        String name = in.readString();
        String desc = in.readString();
        String owner = in.readString();
        String imageUri = in.readString();
        int difficulty = in.readInt();
        String category = in.readString();
        ArrayList<String> steps = new ArrayList<>();
        in.readStringList(steps);
        long _id = in.readLong();
        setData(name, desc, owner, imageUri, difficulty, category, steps);
        set_id(_id);
    }
}