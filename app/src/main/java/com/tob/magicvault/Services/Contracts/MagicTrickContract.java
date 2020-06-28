package com.tob.magicvault.Services.Contracts;

import android.provider.BaseColumns;

public final class MagicTrickContract {
    private MagicTrickContract() {
    }

    public static class MagicTricks implements BaseColumns {
        public static final String TABLE_NAME = "magic_tricks";
        public static final String COLUMN_NAME_TRICK_NAME = "name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_OWNER = "owner";
        public static final String COLUMN_NAME_IMAGE_URI = "image_uri";
        public static final String COLUMN_NAME_DIFFICULTY = "difficulty";
        public static final String COLUMN_NAME_CATEGORY = "category";
    }

    public static class MagicTrickSteps implements BaseColumns {
        public static final String TABLE_NAME = "magic_trick_steps";
        public static final String COLUMN_NAME_ID_MAGIC_TRICK = "id_magic_trick";
        public static final String COLUMN_NAME_STEP_DESCRIPTION = "step_description";
    }
}
