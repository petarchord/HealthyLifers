package com.healthyteam.android.healthylifers.Domain;

import com.google.firebase.database.DatabaseError;

import java.util.List;

public interface OnGetListListener {
    void onChildAdded(List<?> list, int index);
    void onChildChange(List<?> list, int index);
    void onChildRemove(List<?> list, int index,Object removedObject);
    void onChildMoved(List<?> list, int index);
    void onListLoaded(List<?> list);
    void onCanclled(DatabaseError error);
}
