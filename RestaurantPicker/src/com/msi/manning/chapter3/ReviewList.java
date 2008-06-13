package com.msi.manning.chapter3;

import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Menu.Item;
import android.widget.ListView;
import android.widget.TextView;

import com.msi.manning.chapter3.data.Review;
import com.msi.manning.chapter3.data.ReviewFetcher;

/**
 * "List" of reviews screen - show reviews that match Criteria user selected.
 * Users ReviewFetcher which makes a Google Base call via Rome.
 * 
 * @author charliecollins
 * 
 */
public class ReviewList extends ListActivity {

    private static final String CLASSTAG = ReviewList.class.getSimpleName();
    private static final int NUM_RESULTS_PER_PAGE = 5;
    private static final int MENU_GET_NEXT_PAGE = Menu.FIRST;

    private ProgressDialog progressDialog;

    private List<Review> reviews;
    private ReviewAdapter reviewAdapter;

    private TextView empty;

    // use a Handler in order to update UI thread after worker done
    // (cannot update UI thread inline (not done yet), or from separate thread)
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            Log.v(Constants.LOGTAG, " " + CLASSTAG + " worker thread done, setup ReviewAdapter");
            progressDialog.dismiss();
            if (reviews == null || reviews.size() == 0) {
                empty.setText("No Data");
            } else {
                reviewAdapter = new ReviewAdapter(ReviewList.this, reviews);
                setListAdapter(reviewAdapter);
            }
        }

    };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.v(Constants.LOGTAG, " " + CLASSTAG + " onCreate");

        setDefaultKeyMode(SHORTCUT_DEFAULT_KEYS);

        // NOTE* This Activity MUST contain a ListView named "@android:id/list"
        // (or "list" in code) in order to be customized
        // http://code.google.com/android/reference/android/app/ListActivity.html
        setContentView(R.layout.review_list);

        // Tell the list view which view to display when the list is empty
        getListView().setEmptyView(findViewById(R.id.empty));

        empty = (TextView) findViewById(R.id.empty);

        // get the current review criteria from the Application (global state
        // placed there)
        RestaurantPickerApplication application = (RestaurantPickerApplication) this.getApplication();
        Review reviewCriteria = application.getCurrentReview();

        // get start from, an int, from extras
        int startFrom = this.getIntent().getIntExtra(Constants.STARTFROM_EXTRA, 1);

        loadReviews(reviewCriteria.getCuisine(), reviewCriteria.getLocation(), reviewCriteria.getRating(), startFrom);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_GET_NEXT_PAGE, R.string.menu_get_next_page, R.drawable.icon_load);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, Item item) {
        switch (item.getId()) {
            case MENU_GET_NEXT_PAGE:

                // increment the startFrom value and call this Activity again
                Intent intent = new Intent(Constants.INTENT_ACTION_VIEW_LIST);
                intent.putExtra(Constants.STARTFROM_EXTRA, getIntent().getIntExtra(Constants.STARTFROM_EXTRA, 1)
                        + NUM_RESULTS_PER_PAGE);
                startActivity(intent);
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // set the current review to the Application (global state placed there)
        RestaurantPickerApplication application = (RestaurantPickerApplication) this.getApplication();
        application.setCurrentReview(reviews.get(position));

        // startFrom page is not part of review, it's a simple "extra"
        Intent intent = new Intent(Constants.INTENT_ACTION_VIEW_DETAIL);
        intent.putExtra(Constants.STARTFROM_EXTRA, this.getIntent().getIntExtra(Constants.STARTFROM_EXTRA, 1));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(Constants.LOGTAG, " " + CLASSTAG + " onResume");
    }

    private void loadReviews(String description, String location, String rating, int startFrom) {
        Log.v(Constants.LOGTAG, " " + CLASSTAG + " loadReviews");
        Log.v(Constants.LOGTAG, " " + CLASSTAG + "    description (cuisine) - " + description);
        Log.v(Constants.LOGTAG, " " + CLASSTAG + "    location - " + location);
        Log.v(Constants.LOGTAG, " " + CLASSTAG + "    rating - " + rating);
        Log.v(Constants.LOGTAG, " " + CLASSTAG + "    startFrom - " + startFrom);

        final ReviewFetcher rf = new ReviewFetcher(description, location, rating, startFrom, NUM_RESULTS_PER_PAGE);

        progressDialog = ProgressDialog.show(this, " Working...", " Retrieving reviews", true, false);

        // get reviews in a separate thread for ProgressDialog/Handler
        // when complete send "empty" message to handler indicating thread is
        // done
        // TODO will handler clean up separate thread (or do need to implement
        // onDestroy?)
        new Thread() {
            public void run() {
                reviews = rf.getReviews();
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

}