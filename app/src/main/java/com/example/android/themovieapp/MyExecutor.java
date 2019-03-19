package com.example.android.themovieapp;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by ashu on 29-06-2018.
 */

class MyExecutor implements Executor {
    @Override
    public void execute(@NonNull Runnable runnable) {
        new Thread(runnable).start();
    }
}
