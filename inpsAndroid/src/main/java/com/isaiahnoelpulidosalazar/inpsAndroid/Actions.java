package com.isaiahnoelpulidosalazar.inpsAndroid;

import com.isaiahnoelpulidosalazar.inpsAndroid.concurrent.MainThread;

/**
 * Builds and executes a {@link Runnable} on the main thread, optionally carrying an argument.
 *
 * @deprecated Since 2.0.0. Use {@link MainThread#post(Runnable)} /
 *             {@link MainThread#postDelayed(Runnable, long)} and capture arguments in a lambda.
 *             See {@code migrating_to_v2.md}.
 */
@Deprecated
public class Actions {

    private Runnable runnable;
    private Object arg;

    /** Stores an argument retrievable through {@link #getArg()}. */
    public Actions setArg(Object arg) {
        this.arg = arg;
        return this;
    }

    /** @return the argument set via {@link #setArg(Object)}, or {@code null}. */
    public Object getArg() {
        return arg;
    }

    /** Assigns the runnable to execute. */
    public Actions build(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }

    /** Posts the runnable to the main thread. */
    public void execute() {
        MainThread.post(requireRunnable());
    }

    /** Posts the runnable to the main thread after {@code delay} milliseconds. */
    public void executeDelayed(int delay) {
        MainThread.postDelayed(requireRunnable(), delay);
    }

    private Runnable requireRunnable() {
        if (runnable == null) {
            throw new IllegalStateException("Call build(Runnable) before execute()");
        }
        return runnable;
    }
}
