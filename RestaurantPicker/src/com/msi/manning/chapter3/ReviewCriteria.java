package com.msi.manning.chapter3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Menu.Item;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.msi.manning.chapter3.data.Review;

/**
 * "Criteria" to select reviews screen - choose Location, Cuisine, and Rating.
 * 
 * @author charliecollins
 * 
 */
public class ReviewCriteria extends Activity {

    private static final String CLASSTAG = ReviewCriteria.class.getSimpleName();
    private static final int MENU_GET_REVIEWS = Menu.FIRST;

    private EditText location;
    private Spinner cuisine;
    private Spinner rating;
    
    ///private TextView locationLabel;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.v(Constants.LOGTAG, " " + CLASSTAG + " onCreate");

        setContentView(R.layout.review_criteria);

        ///locationLabel = (TextView) findViewById(R.id.location_label);
        ///locationLabel.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scaler));
        
        location = (EditText) findViewById(R.id.location);
        cuisine = (Spinner) findViewById(R.id.cuisine);
        rating = (Spinner) findViewById(R.id.rating);

        ArrayAdapter<String> cuisines = new ArrayAdapter<String>(this, 
                R.layout.spinner_view,
                getResources().getStringArray(R.array.cuisines));
        cuisines.setDropDownViewResource(R.layout.spinner_view_dropdown);
        cuisine.setAdapter(cuisines);
        ArrayAdapter<String> ratings = new ArrayAdapter<String>(this, 
                R.layout.spinner_view,
                getResources().getStringArray(R.array.ratings));
        ratings.setDropDownViewResource(R.layout.spinner_view_dropdown);
        rating.setAdapter(ratings);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_GET_REVIEWS, R.string.menu_get_reviews, R.drawable.icon_load);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, Item item) {
        switch (item.getId()) {
            case MENU_GET_REVIEWS:
                if (!validate())
                    return true;

                // create a Review to persist criteria user has selected
                Review reviewCriteria = new Review();
                reviewCriteria.setLocation(location.getText().toString());
                reviewCriteria.setCuisine(cuisine.getSelectedItem().toString());
                reviewCriteria.setRating(rating.getSelectedItem().toString());

                // use the "Application" to store global state beyond primitives
                // and Strings (extras)
                RestaurantPickerApplication application = (RestaurantPickerApplication) this.getApplication();
                application.setCurrentReview(reviewCriteria);

                // call next Activity, VIEW_LIST
                Intent intent = new Intent(Constants.INTENT_ACTION_VIEW_LIST);
                startActivity(intent);
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private boolean validate() {

        boolean valid = true;
        StringBuffer validationText = new StringBuffer();

        if (location.getText() == null || location.getText().toString().equals("")) {
            validationText.append(R.string.no_location_message);
            valid = false;
        }
        if (!valid) {
            new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.alert_label)).setMessage(
                    validationText.toString()).setPositiveButton("Continue", new OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    setResult(RESULT_OK);
                    finish();
                }
            }).show();
            validationText = null;
        }
        return valid;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(Constants.LOGTAG, " " + CLASSTAG + " onResume");
    }

}