package com.udacity.gradle.builditbigger.utils;

import android.app.Activity;
import android.content.Intent;

import com.arbuleac.joketeller.JokeActivity;

/**
 * @since 7/25/15.
 */
public class Navigation {

    private static Navigation sInstance;

    public static Navigation get() {
        if (sInstance == null) {
            synchronized (Navigation.class) {
                if (sInstance == null) {
                    sInstance = new Navigation();
                }
            }
        }
        return sInstance;
    }

    public void joke(Activity activity, String joke) {
        Intent jokeIntent = new Intent(activity, JokeActivity.class);
        jokeIntent.putExtra(JokeActivity.EXTRA_JOKE, joke);
        activity.startActivity(jokeIntent);
    }
}
