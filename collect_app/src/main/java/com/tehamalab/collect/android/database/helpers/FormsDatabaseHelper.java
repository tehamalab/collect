/*
 * Copyright 2017 Nafundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tehamalab.collect.android.database.helpers;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tehamalab.collect.android.application.Collect;
import com.tehamalab.collect.android.provider.FormsProviderAPI;
import com.tehamalab.collect.android.utilities.CustomSQLiteQueryBuilder;
import com.tehamalab.collect.android.database.DatabaseContext;

import timber.log.Timber;

import static android.provider.BaseColumns._ID;

/**
 * This class helps open, create, and upgrade the database file.
 */
public class FormsDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "forms.db";
    public static final String FORMS_TABLE_NAME = "forms";

    private static final int DATABASE_VERSION = 7;

    // These exist in database versions 2 and 3, but not in 4...
    private static final String TEMP_FORMS_TABLE_NAME = "forms_v4";
    private static final String MODEL_VERSION = "modelVersion";

    public FormsDatabaseHelper() {
        super(new DatabaseContext(Collect.METADATA_PATH), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createFormsTableV7(db);
    }

    @SuppressWarnings({"checkstyle:FallThrough"})
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.i("Upgrading database from version %d to %d", oldVersion, newVersion);

        switch (oldVersion) {
            case 1:
                upgradeToVersion2(db);
            case 2:
            case 3:
                upgradeToVersion4(db, oldVersion);
            case 4:
                upgradeToVersion5(db);
            case 5:
                upgradeToVersion6(db);
            case 6:
                upgradeToVersion7(db);
                break;
            default:
                Timber.i("Unknown version %s", oldVersion);
        }

        Timber.i("Upgrading database from version %d to %d completed with success.", oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        CustomSQLiteQueryBuilder
                .begin(db)
                .dropIfExists(FORMS_TABLE_NAME)
                .end();

        createFormsTableV7(db);

        Timber.i("Downgrading database from %d to %d completed with success.", oldVersion, newVersion);
    }

    private void upgradeToVersion2(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + FORMS_TABLE_NAME);
        onCreate(db);
    }

    private void upgradeToVersion4(SQLiteDatabase db, int oldVersion) {
        // adding BASE64_RSA_PUBLIC_KEY and changing type and name of
        // integer MODEL_VERSION to text VERSION
        db.execSQL("DROP TABLE IF EXISTS " + TEMP_FORMS_TABLE_NAME);
        createFormsTableV4(db, TEMP_FORMS_TABLE_NAME);
        db.execSQL("INSERT INTO "
                + TEMP_FORMS_TABLE_NAME
                + " ("
                + _ID
                + ", "
                + FormsProviderAPI.FormsColumns.DISPLAY_NAME
                + ", "
                + FormsProviderAPI.FormsColumns.DISPLAY_SUBTEXT
                + ", "
                + FormsProviderAPI.FormsColumns.DESCRIPTION
                + ", "
                + FormsProviderAPI.FormsColumns.JR_FORM_ID
                + ", "
                + FormsProviderAPI.FormsColumns.MD5_HASH
                + ", "
                + FormsProviderAPI.FormsColumns.DATE
                + ", " // milliseconds
                + FormsProviderAPI.FormsColumns.FORM_MEDIA_PATH
                + ", "
                + FormsProviderAPI.FormsColumns.FORM_FILE_PATH
                + ", "
                + FormsProviderAPI.FormsColumns.LANGUAGE
                + ", "
                + FormsProviderAPI.FormsColumns.SUBMISSION_URI
                + ", "
                + FormsProviderAPI.FormsColumns.JR_VERSION
                + ", "
                + ((oldVersion != 3) ? ""
                : (FormsProviderAPI.FormsColumns.BASE64_RSA_PUBLIC_KEY + ", "))
                + FormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH
                + ") SELECT "
                + _ID
                + ", "
                + FormsProviderAPI.FormsColumns.DISPLAY_NAME
                + ", "
                + FormsProviderAPI.FormsColumns.DISPLAY_SUBTEXT
                + ", "
                + FormsProviderAPI.FormsColumns.DESCRIPTION
                + ", "
                + FormsProviderAPI.FormsColumns.JR_FORM_ID
                + ", "
                + FormsProviderAPI.FormsColumns.MD5_HASH
                + ", "
                + FormsProviderAPI.FormsColumns.DATE
                + ", " // milliseconds
                + FormsProviderAPI.FormsColumns.FORM_MEDIA_PATH
                + ", "
                + FormsProviderAPI.FormsColumns.FORM_FILE_PATH
                + ", "
                + FormsProviderAPI.FormsColumns.LANGUAGE
                + ", "
                + FormsProviderAPI.FormsColumns.SUBMISSION_URI
                + ", "
                + "CASE WHEN "
                + MODEL_VERSION
                + " IS NOT NULL THEN "
                + "CAST("
                + MODEL_VERSION
                + " AS TEXT) ELSE NULL END, "
                + ((oldVersion != 3) ? ""
                : (FormsProviderAPI.FormsColumns.BASE64_RSA_PUBLIC_KEY + ", "))
                + FormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH + " FROM "
                + FORMS_TABLE_NAME);

        // risky failures here...
        db.execSQL("DROP TABLE IF EXISTS " + FORMS_TABLE_NAME);
        createFormsTableV4(db, FORMS_TABLE_NAME);
        db.execSQL("INSERT INTO "
                + FORMS_TABLE_NAME
                + " ("
                + _ID
                + ", "
                + FormsProviderAPI.FormsColumns.DISPLAY_NAME
                + ", "
                + FormsProviderAPI.FormsColumns.DISPLAY_SUBTEXT
                + ", "
                + FormsProviderAPI.FormsColumns.DESCRIPTION
                + ", "
                + FormsProviderAPI.FormsColumns.JR_FORM_ID
                + ", "
                + FormsProviderAPI.FormsColumns.MD5_HASH
                + ", "
                + FormsProviderAPI.FormsColumns.DATE
                + ", " // milliseconds
                + FormsProviderAPI.FormsColumns.FORM_MEDIA_PATH + ", "
                + FormsProviderAPI.FormsColumns.FORM_FILE_PATH + ", "
                + FormsProviderAPI.FormsColumns.LANGUAGE + ", "
                + FormsProviderAPI.FormsColumns.SUBMISSION_URI + ", "
                + FormsProviderAPI.FormsColumns.JR_VERSION + ", "
                + FormsProviderAPI.FormsColumns.BASE64_RSA_PUBLIC_KEY + ", "
                + FormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH + ") SELECT "
                + _ID + ", "
                + FormsProviderAPI.FormsColumns.DISPLAY_NAME
                + ", "
                + FormsProviderAPI.FormsColumns.DISPLAY_SUBTEXT
                + ", "
                + FormsProviderAPI.FormsColumns.DESCRIPTION
                + ", "
                + FormsProviderAPI.FormsColumns.JR_FORM_ID
                + ", "
                + FormsProviderAPI.FormsColumns.MD5_HASH
                + ", "
                + FormsProviderAPI.FormsColumns.DATE
                + ", " // milliseconds
                + FormsProviderAPI.FormsColumns.FORM_MEDIA_PATH + ", "
                + FormsProviderAPI.FormsColumns.FORM_FILE_PATH + ", "
                + FormsProviderAPI.FormsColumns.LANGUAGE + ", "
                + FormsProviderAPI.FormsColumns.SUBMISSION_URI + ", "
                + FormsProviderAPI.FormsColumns.JR_VERSION + ", "
                + FormsProviderAPI.FormsColumns.BASE64_RSA_PUBLIC_KEY + ", "
                + FormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH + " FROM "
                + TEMP_FORMS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TEMP_FORMS_TABLE_NAME);
    }

    private void upgradeToVersion5(SQLiteDatabase db) {
        CustomSQLiteQueryBuilder
                .begin(db)
                .alter()
                .table(FORMS_TABLE_NAME)
                .addColumn(FormsProviderAPI.FormsColumns.AUTO_SEND, "text")
                .end();

        CustomSQLiteQueryBuilder
                .begin(db)
                .alter()
                .table(FORMS_TABLE_NAME)
                .addColumn(FormsProviderAPI.FormsColumns.AUTO_DELETE, "text")
                .end();
    }

    private void upgradeToVersion6(SQLiteDatabase db) {
        CustomSQLiteQueryBuilder
                .begin(db)
                .alter()
                .table(FORMS_TABLE_NAME)
                .addColumn(FormsProviderAPI.FormsColumns.LAST_DETECTED_FORM_VERSION_HASH, "text")
                .end();
    }

    private void upgradeToVersion7(SQLiteDatabase db) {
        String temporaryTable = FORMS_TABLE_NAME + "_tmp";
        String[] formsTableColumnsInV7 = new String[] {_ID, FormsProviderAPI.FormsColumns.DISPLAY_NAME, FormsProviderAPI.FormsColumns.DESCRIPTION,
                FormsProviderAPI.FormsColumns.JR_FORM_ID, FormsProviderAPI.FormsColumns.JR_VERSION, FormsProviderAPI.FormsColumns.MD5_HASH, FormsProviderAPI.FormsColumns.DATE, FormsProviderAPI.FormsColumns.FORM_MEDIA_PATH, FormsProviderAPI.FormsColumns.FORM_FILE_PATH, FormsProviderAPI.FormsColumns.LANGUAGE,
                FormsProviderAPI.FormsColumns.SUBMISSION_URI, FormsProviderAPI.FormsColumns.BASE64_RSA_PUBLIC_KEY, FormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH, FormsProviderAPI.FormsColumns.AUTO_SEND, FormsProviderAPI.FormsColumns.AUTO_DELETE,
                FormsProviderAPI.FormsColumns.LAST_DETECTED_FORM_VERSION_HASH};

            CustomSQLiteQueryBuilder
                    .begin(db)
                    .renameTable(FORMS_TABLE_NAME)
                    .to(temporaryTable)
                    .end();

            createFormsTableV7(db);

            CustomSQLiteQueryBuilder
                    .begin(db)
                    .insertInto(FORMS_TABLE_NAME)
                    .columnsForInsert(formsTableColumnsInV7)
                    .select()
                    .columnsForSelect(formsTableColumnsInV7)
                    .from(temporaryTable)
                    .end();

            CustomSQLiteQueryBuilder
                    .begin(db)
                    .dropIfExists(temporaryTable)
                    .end();
    }

    private void createFormsTableV4(SQLiteDatabase db, String tableName) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + _ID + " integer primary key, "
                + FormsProviderAPI.FormsColumns.DISPLAY_NAME + " text not null, "
                + FormsProviderAPI.FormsColumns.DISPLAY_SUBTEXT + " text not null, "
                + FormsProviderAPI.FormsColumns.DESCRIPTION + " text, "
                + FormsProviderAPI.FormsColumns.JR_FORM_ID + " text not null, "
                + FormsProviderAPI.FormsColumns.JR_VERSION + " text, "
                + FormsProviderAPI.FormsColumns.MD5_HASH + " text not null, "
                + FormsProviderAPI.FormsColumns.DATE + " integer not null, " // milliseconds
                + FormsProviderAPI.FormsColumns.FORM_MEDIA_PATH + " text not null, "
                + FormsProviderAPI.FormsColumns.FORM_FILE_PATH + " text not null, "
                + FormsProviderAPI.FormsColumns.LANGUAGE + " text, "
                + FormsProviderAPI.FormsColumns.SUBMISSION_URI + " text, "
                + FormsProviderAPI.FormsColumns.BASE64_RSA_PUBLIC_KEY + " text, "
                + FormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH + " text not null, "
                + FormsProviderAPI.FormsColumns.AUTO_SEND + " text, "
                + FormsProviderAPI.FormsColumns.AUTO_DELETE + " text, "
                + FormsProviderAPI.FormsColumns.LAST_DETECTED_FORM_VERSION_HASH + " text);");
    }

    private void createFormsTableV7(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + FORMS_TABLE_NAME + " ("
                + _ID + " integer primary key, "
                + FormsProviderAPI.FormsColumns.DISPLAY_NAME + " text not null, "
                + FormsProviderAPI.FormsColumns.DESCRIPTION + " text, "
                + FormsProviderAPI.FormsColumns.JR_FORM_ID + " text not null, "
                + FormsProviderAPI.FormsColumns.JR_VERSION + " text, "
                + FormsProviderAPI.FormsColumns.MD5_HASH + " text not null, "
                + FormsProviderAPI.FormsColumns.DATE + " integer not null, " // milliseconds
                + FormsProviderAPI.FormsColumns.FORM_MEDIA_PATH + " text not null, "
                + FormsProviderAPI.FormsColumns.FORM_FILE_PATH + " text not null, "
                + FormsProviderAPI.FormsColumns.LANGUAGE + " text, "
                + FormsProviderAPI.FormsColumns.SUBMISSION_URI + " text, "
                + FormsProviderAPI.FormsColumns.BASE64_RSA_PUBLIC_KEY + " text, "
                + FormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH + " text not null, "
                + FormsProviderAPI.FormsColumns.AUTO_SEND + " text, "
                + FormsProviderAPI.FormsColumns.AUTO_DELETE + " text, "
                + FormsProviderAPI.FormsColumns.LAST_DETECTED_FORM_VERSION_HASH + " text);");
    }
}
