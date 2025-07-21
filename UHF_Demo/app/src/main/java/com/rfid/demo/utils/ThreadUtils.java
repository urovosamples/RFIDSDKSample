package com.rfid.demo.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtils {
    private static ExecutorService executorService = Executors.newCachedThreadPool();
    private static Executor mParallelExecutor = AsyncTask.THREAD_POOL_EXECUTOR;
    private static Executor mSerialExecutor = AsyncTask.SERIAL_EXECUTOR;
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    private ThreadUtils() {
        mParallelExecutor = AsyncTask.THREAD_POOL_EXECUTOR;
        mSerialExecutor = AsyncTask.SERIAL_EXECUTOR;
    }

    public static void ui(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            sHandler.post(runnable);
        }
    }

    public static void asyn(Runnable runnable) {
        asyn(runnable, true);
    }

    public static void asynQueue(Runnable runnable) {
        asyn(runnable, false);
    }

    public static void asyn(Runnable runnable, boolean z) {
        if (z) {
            mParallelExecutor.execute(runnable);
        } else {
            mSerialExecutor.execute(runnable);
        }
    }

    public static void submit(Callable callable) {
        executorService.submit(callable);
    }

}
