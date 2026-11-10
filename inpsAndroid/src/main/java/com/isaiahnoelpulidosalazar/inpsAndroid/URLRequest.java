package com.isaiahnoelpulidosalazar.inpsAndroid;

import com.isaiahnoelpulidosalazar.inpsAndroid.network.HttpRequest;

/**
 * Legacy request wrapper. No longer uses {@code AsyncTask}; delegates to {@link HttpRequest}.
 * Keeps the v1 contract: only HTTP 200 yields the body, anything else yields
 * {@code "Error: <code or message>"}.
 *
 * @deprecated Since 2.0.0. Use {@link HttpRequest}. See {@code migrating_to_v2.md}.
 */
@Deprecated
public class URLRequest {

    private final Actions action = new Actions();

    public URLRequest build(Runnable runnable) {
        action.build(runnable);
        return this;
    }

    public Object getDefaultActionArg() {
        return action.getArg();
    }

    public void execute(String URL, String method) {
        HttpRequest.of(method, URL).enqueue(new HttpRequest.Callback() {
            @Override public void onSuccess(HttpRequest.Response response) {
                action.setArg(response.code == 200 ? response.body : "Error: " + response.code).execute();
            }
            @Override public void onError(Exception error) {
                action.setArg("Error: " + error.getMessage()).execute();
            }
        });
    }
}
