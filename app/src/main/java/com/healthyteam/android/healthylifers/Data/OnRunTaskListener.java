package com.healthyteam.android.healthylifers.Data;

import com.google.android.gms.tasks.Task;

public interface OnRunTaskListener {
    void OnStart();
    void OnComplete(Task<?> task);
}
