package com.healthyteam.android.healthylifers.Data;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public interface OnUploadDataListener {
    void onStart();
    void onSuccess();
    void onFailed(Exception e);
}
