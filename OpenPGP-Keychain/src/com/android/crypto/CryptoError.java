/*
 * Copyright (C) 2013 Dominik Schürmann <dominik@dominikschuermann.de>
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

package com.android.crypto;

import android.os.Parcel;
import android.os.Parcelable;

public class CryptoError implements Parcelable {
    int errorId;
    String message;

    public CryptoError() {
    }

    public CryptoError(int errorId, String message) {
        this.errorId = errorId;
        this.message = message;
    }

    public CryptoError(CryptoError b) {
        this.errorId = b.errorId;
        this.message = b.message;
    }

    public int getErrorId() {
        return errorId;
    }

    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(errorId);
        dest.writeString(message);
    }

    public static final Creator<CryptoError> CREATOR = new Creator<CryptoError>() {
        public CryptoError createFromParcel(final Parcel source) {
            CryptoError error = new CryptoError();
            error.errorId = source.readInt();
            error.message = source.readString();
            return error;
        }

        public CryptoError[] newArray(final int size) {
            return new CryptoError[size];
        }
    };
}
