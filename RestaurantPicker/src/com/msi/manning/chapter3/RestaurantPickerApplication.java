package com.msi.manning.chapter3;

import android.app.Application;

import com.msi.manning.chapter3.data.Review;

/**
 * Extend Application for global state information for an application. Access
 * the application via Activity.getApplication().
 * 
 * There are several ways to store global state information, this is one of
 * them. Another is to create a class with static members and just access it
 * from Activities. This could hold anything, such as a HashMap and keys could
 * be passed to and from activities with putExtra(String name, long KEY) - then
 * activities could get long key and access static map, etc.
 * 
 * To do the HashMap static right though, you need another thread that cleans up
 * (time), else everything lives as long as your app and never gets cleaned up.
 * 
 * @author charliecollins
 * 
 */
public class RestaurantPickerApplication extends Application {
    private Review currentReview;

    public RestaurantPickerApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public Review getCurrentReview() {
        return this.currentReview;
    }

    public void setCurrentReview(Review currentReview) {
        this.currentReview = currentReview;
    }
}