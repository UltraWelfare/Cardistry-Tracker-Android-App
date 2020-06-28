package com.tob.magicvault.Services.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tob.magicvault.Models.MagicTrickModel;
import com.tob.magicvault.Services.Contracts.MagicTrickContract;
import com.tob.magicvault.Services.DatabaseHandler;

import java.util.ArrayList;

public class MagicTrickRepository {


    private static final String DEBUG_TAG = "Magic Trick Repository";
    private static final String FIND_STEPS_BY_ID_QUERY = "SELECT * FROM " + MagicTrickContract.MagicTrickSteps.TABLE_NAME +
            " WHERE " + MagicTrickContract.MagicTrickSteps.COLUMN_NAME_ID_MAGIC_TRICK + " = ?";


    private final ArrayList<MagicTrickModel> magicTricks = new ArrayList<>();
    private final Context _ctx;

    public MagicTrickRepository(Context ctx) {
        _ctx = ctx;
        loadMagicTricks();
    }

    public void addMagicTrick(MagicTrickModel... magicTricks) throws NameAlreadyExistsException {
        for (MagicTrickModel magicTrick : magicTricks) {
            this.addMagicTrick(magicTrick);
        }
    }

    /**
     * Adds a magic trick to the repository
     *
     * @param magicTrick the magic trick to be added
     */
    public void addMagicTrick(MagicTrickModel magicTrick) throws NameAlreadyExistsException {
        SQLiteDatabase db = DatabaseHandler.get(_ctx).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MagicTrickContract.MagicTricks.COLUMN_NAME_TRICK_NAME, magicTrick.getName());
        values.put(MagicTrickContract.MagicTricks.COLUMN_NAME_DESCRIPTION, magicTrick.getDescription());
        values.put(MagicTrickContract.MagicTricks.COLUMN_NAME_OWNER, magicTrick.getOwner());
        values.put(MagicTrickContract.MagicTricks.COLUMN_NAME_DIFFICULTY, magicTrick.getDifficulty());
        values.put(MagicTrickContract.MagicTricks.COLUMN_NAME_CATEGORY, magicTrick.getCategory());
        values.put(MagicTrickContract.MagicTricks.COLUMN_NAME_IMAGE_URI, magicTrick.getImage_uri());
        try {
            long magicTrickId = db.insertOrThrow(MagicTrickContract.MagicTricks.TABLE_NAME, null, values);
            for (String step : magicTrick.getSteps()) {
                ContentValues stepValues = new ContentValues();
                stepValues.put(MagicTrickContract.MagicTrickSteps.COLUMN_NAME_ID_MAGIC_TRICK, magicTrickId);
                stepValues.put(MagicTrickContract.MagicTrickSteps.COLUMN_NAME_STEP_DESCRIPTION, step);
                db.insert(MagicTrickContract.MagicTrickSteps.TABLE_NAME, null, stepValues);
            }
            Log.d(DEBUG_TAG, "Saved new magic trick { " + magicTrick + " }");
        } catch (SQLException e) {
            if (e instanceof SQLiteConstraintException) {
                throw new NameAlreadyExistsException(magicTrick.getName());
            }
        }
    }

    /**
     * Deletes a magic trick and its steps, based on the id
     *
     * @param _id The trick id
     */
    public void removeMagicTrick(long _id) {
        String id = String.valueOf(_id);
        SQLiteDatabase db = DatabaseHandler.get(_ctx).getWritableDatabase();
        db.delete(MagicTrickContract.MagicTricks.TABLE_NAME, MagicTrickContract.MagicTricks._ID + "=?", new String[]{id});

        // Delete steps too
        db.delete(MagicTrickContract.MagicTrickSteps.TABLE_NAME, MagicTrickContract.MagicTrickSteps.COLUMN_NAME_ID_MAGIC_TRICK + "=?", new String[]{id});
        Log.d(DEBUG_TAG, "Deleted magic trick with id : " + id);
    }

    /**
     * Updates a magic trick to the repository
     *
     * @param updatedMagicTrick the magic trick to be added
     */
    public void updateMagicTrick(MagicTrickModel updatedMagicTrick) {
        SQLiteDatabase db = DatabaseHandler.get(_ctx).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MagicTrickContract.MagicTricks.COLUMN_NAME_DESCRIPTION, updatedMagicTrick.getDescription());
        values.put(MagicTrickContract.MagicTricks.COLUMN_NAME_DIFFICULTY, updatedMagicTrick.getDifficulty());
        values.put(MagicTrickContract.MagicTricks.COLUMN_NAME_OWNER, updatedMagicTrick.getOwner());
        values.put(MagicTrickContract.MagicTricks.COLUMN_NAME_CATEGORY, updatedMagicTrick.getCategory());
        values.put(MagicTrickContract.MagicTricks.COLUMN_NAME_IMAGE_URI, updatedMagicTrick.getImage_uri());

        db.update(MagicTrickContract.MagicTricks.TABLE_NAME, values, MagicTrickContract.MagicTricks.COLUMN_NAME_TRICK_NAME + "=?", new String[]{updatedMagicTrick.getName()});


        // Delete all steps before adding the new ones.
        db.delete(MagicTrickContract.MagicTrickSteps.TABLE_NAME, MagicTrickContract.MagicTrickSteps.COLUMN_NAME_ID_MAGIC_TRICK + "=?", new String[]{String.valueOf(updatedMagicTrick.get_id())});

        // Add the new ones.
        for (String step : updatedMagicTrick.getSteps()) {
            ContentValues stepValues = new ContentValues();
            stepValues.put(MagicTrickContract.MagicTrickSteps.COLUMN_NAME_ID_MAGIC_TRICK, updatedMagicTrick.get_id());
            stepValues.put(MagicTrickContract.MagicTrickSteps.COLUMN_NAME_STEP_DESCRIPTION, step);
            db.insert(MagicTrickContract.MagicTrickSteps.TABLE_NAME, null, stepValues);
        }
        Log.d(DEBUG_TAG, "Updated new magic trick { " + updatedMagicTrick + " }");

    }


    /**
     * Loads steps from a magic trick
     *
     * @param id The magic trick id
     * @return An array of steps
     */
    private ArrayList<String> loadStepsById(long id) {
        SQLiteDatabase db = DatabaseHandler.get(_ctx).getReadableDatabase();
        Cursor stepCursor = db.rawQuery(FIND_STEPS_BY_ID_QUERY, new String[]{String.valueOf(id)});

        ArrayList<String> result = new ArrayList<>();
        while (stepCursor.moveToNext()) {
            String description = stepCursor.getString(stepCursor.getColumnIndex(MagicTrickContract.MagicTrickSteps.COLUMN_NAME_STEP_DESCRIPTION));
            result.add(description);
        }

        stepCursor.close();
        return result;
    }


    /**
     * (Re)loads the magic tricks.
     * Use getMagicTricks() to retrieve them.
     */
    public void loadMagicTricks() {
        magicTricks.clear();
        SQLiteDatabase db = DatabaseHandler.get(_ctx).getReadableDatabase();

        Cursor magicTrickCursor = db.query(
                MagicTrickContract.MagicTricks.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MagicTrickContract.MagicTricks.COLUMN_NAME_TRICK_NAME + " ASC"
        );


        while (magicTrickCursor.moveToNext()) {
            long id = magicTrickCursor.getLong(magicTrickCursor.getColumnIndex(MagicTrickContract.MagicTricks._ID));
            String name = magicTrickCursor.getString(magicTrickCursor.getColumnIndex(MagicTrickContract.MagicTricks.COLUMN_NAME_TRICK_NAME));
            String description = magicTrickCursor.getString(magicTrickCursor.getColumnIndex(MagicTrickContract.MagicTricks.COLUMN_NAME_DESCRIPTION));
            String owner = magicTrickCursor.getString(magicTrickCursor.getColumnIndex(MagicTrickContract.MagicTricks.COLUMN_NAME_OWNER));
            String category = magicTrickCursor.getString(magicTrickCursor.getColumnIndex(MagicTrickContract.MagicTricks.COLUMN_NAME_CATEGORY));
            String imageUri = magicTrickCursor.getString(magicTrickCursor.getColumnIndex(MagicTrickContract.MagicTricks.COLUMN_NAME_IMAGE_URI));
            int difficulty = magicTrickCursor.getInt(magicTrickCursor.getColumnIndex(MagicTrickContract.MagicTricks.COLUMN_NAME_DIFFICULTY));

            ArrayList<String> steps = this.loadStepsById(id);
            MagicTrickModel modelToAdd = new MagicTrickModel(name, description, owner, imageUri, difficulty, category, steps);


            modelToAdd.set_id(id);
            magicTricks.add(modelToAdd);
        }

        magicTrickCursor.close();
    }

    public ArrayList<MagicTrickModel> getMagicTricks() {
        return magicTricks;
    }
}
