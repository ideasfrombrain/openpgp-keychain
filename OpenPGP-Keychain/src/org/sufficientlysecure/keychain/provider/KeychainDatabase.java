/*
 * Copyright (C) 2012 Dominik Schürmann <dominik@dominikschuermann.de>
 * Copyright (C) 2010 Thialfihar <thi@thialfihar.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sufficientlysecure.keychain.provider;

import org.sufficientlysecure.keychain.Constants;
import org.sufficientlysecure.keychain.provider.KeychainContract.CryptoConsumersColumns;
import org.sufficientlysecure.keychain.provider.KeychainContract.KeyRingsColumns;
import org.sufficientlysecure.keychain.provider.KeychainContract.KeysColumns;
import org.sufficientlysecure.keychain.provider.KeychainContract.UserIdsColumns;
import org.sufficientlysecure.keychain.util.Log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class KeychainDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "apg.db";
    private static final int DATABASE_VERSION = 5;

    public interface Tables {
        String KEY_RINGS = "key_rings";
        String KEYS = "keys";
        String USER_IDS = "user_ids";
        String CRYPTO_CONSUMERS = "crypto_consumers";
    }

    private static final String CREATE_KEY_RINGS = "CREATE TABLE IF NOT EXISTS " + Tables.KEY_RINGS
            + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KeyRingsColumns.MASTER_KEY_ID + " INT64, " + KeyRingsColumns.TYPE + " INTEGER, "
            + KeyRingsColumns.KEY_RING_DATA + " BLOB)";

    private static final String CREATE_KEYS = "CREATE TABLE IF NOT EXISTS " + Tables.KEYS + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KeysColumns.KEY_ID
            + " INT64, " + KeysColumns.TYPE + " INTEGER, " + KeysColumns.IS_MASTER_KEY
            + " INTEGER, " + KeysColumns.ALGORITHM + " INTEGER, " + KeysColumns.KEY_SIZE
            + " INTEGER, " + KeysColumns.CAN_CERTIFY + " INTEGER, " + KeysColumns.CAN_SIGN
            + " INTEGER, " + KeysColumns.CAN_ENCRYPT + " INTEGER, " + KeysColumns.IS_REVOKED
            + " INTEGER, " + KeysColumns.CREATION + " INTEGER, " + KeysColumns.EXPIRY
            + " INTEGER, " + KeysColumns.KEY_DATA + " BLOB," + KeysColumns.RANK + " INTEGER, "
            + KeysColumns.KEY_RING_ROW_ID + " INTEGER NOT NULL, FOREIGN KEY("
            + KeysColumns.KEY_RING_ROW_ID + ") REFERENCES " + Tables.KEY_RINGS + "("
            + BaseColumns._ID + ") ON DELETE CASCADE)";

    private static final String CREATE_USER_IDS = "CREATE TABLE IF NOT EXISTS " + Tables.USER_IDS
            + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + UserIdsColumns.USER_ID + " TEXT, " + UserIdsColumns.RANK + " INTEGER, "
            + UserIdsColumns.KEY_RING_ROW_ID + " INTEGER NOT NULL, FOREIGN KEY("
            + UserIdsColumns.KEY_RING_ROW_ID + ") REFERENCES " + Tables.KEY_RINGS + "("
            + BaseColumns._ID + ") ON DELETE CASCADE)";

    private static final String CREATE_CRYPTO_CONSUMERS = "CREATE TABLE IF NOT EXISTS "
            + Tables.CRYPTO_CONSUMERS + " (" + BaseColumns._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CryptoConsumersColumns.PACKAGE_NAME
            + " TEXT UNIQUE)";

    KeychainDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w(Constants.TAG, "Creating database...");

        db.execSQL(CREATE_KEY_RINGS);
        db.execSQL(CREATE_KEYS);
        db.execSQL(CREATE_USER_IDS);
        db.execSQL(CREATE_CRYPTO_CONSUMERS);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(Constants.TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        // Upgrade from oldVersion through all methods to newest one
        for (int version = oldVersion; version < newVersion; ++version) {
            Log.w(Constants.TAG, "Upgrading database to version " + version);

            switch (version) {
            case 3:
                db.execSQL("ALTER TABLE " + Tables.KEYS + " ADD COLUMN " + KeysColumns.CAN_CERTIFY
                        + " INTEGER DEFAULT 0;");
                db.execSQL("UPDATE " + Tables.KEYS + " SET " + KeysColumns.CAN_CERTIFY
                        + " = 1 WHERE " + KeysColumns.IS_MASTER_KEY + "= 1;");
                break;
            case 4:
                db.execSQL(CREATE_CRYPTO_CONSUMERS);

            default:
                break;

            }
        }
    }

}
