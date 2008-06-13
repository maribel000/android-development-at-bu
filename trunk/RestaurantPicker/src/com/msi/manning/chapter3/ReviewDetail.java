package com.msi.manning.chapter3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Menu.Item;
import android.widget.TextView;

import com.msi.manning.chapter3.data.Review;

/**
 * Show Review detail for review item user selected.
 * 
 * @author charliecollins
 *
 */
public class ReviewDetail extends Activity {

    private static final String CLASSTAG = ReviewDetail.class.getSimpleName();
    private static final int MENU_WEB_REVIEW = Menu.FIRST;
    private static final int MENU_MAP_REVIEW = Menu.FIRST + 1;
    private static final int MENU_CALL_REVIEW = Menu.FIRST + 2;

    private TextView name;
    private TextView rating;
    private TextView review;
    private TextView location;
    private TextView phone;

    private String link;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.v(Constants.LOGTAG, " " + CLASSTAG + " onCreate");

        setContentView(R.layout.review_detail);
        
        name = (TextView) findViewById(R.id.name_detail);
        rating = (TextView) findViewById(R.id.rating_detail);
        location = (TextView) findViewById(R.id.location_detail);
        phone = (TextView) findViewById(R.id.phone_detail);
        review = (TextView) findViewById(R.id.review_detail);

        // get the current review from the Application (global state placed there)
        RestaurantPickerApplication application = (RestaurantPickerApplication) this.getApplication();
        Review currentReview = application.getCurrentReview();
                
        link = currentReview.getLink();
        
        name.setText(currentReview.getName());
        rating.setText(currentReview.getRating());
        location.setText(currentReview.getLocation());
        phone.setText(currentReview.getPhone());
        review.setText(currentReview.getContent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_WEB_REVIEW, R.string.menu_web_review, R.drawable.icon_web);
        menu.add(0, MENU_MAP_REVIEW, R.string.menu_map_review, android.R.drawable.sym_action_map);
        menu.add(0, MENU_CALL_REVIEW, R.string.menu_call_review, android.R.drawable.sym_action_call);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, Item item) {
        Intent intent = null;
        switch (item.getId()) {
            case MENU_WEB_REVIEW:
                Log.v(Constants.LOGTAG, " " + CLASSTAG + " WEB call - " + link);
                intent = new Intent(Intent.VIEW_ACTION, Uri.parse(link));
                startActivity(intent);
                return true;
            case MENU_MAP_REVIEW:
                Log.v(Constants.LOGTAG, " " + CLASSTAG + " MAP call ");
                intent = new Intent(Intent.VIEW_ACTION, Uri.parse("geo:0,0?q=" + location.getText().toString()));
                startActivity(intent);
                return true;
            case MENU_CALL_REVIEW:
                Log.v(Constants.LOGTAG, " " + CLASSTAG + " PHONE call");
                if (phone.getText() != null) {
                    Log.v(Constants.LOGTAG, " " + CLASSTAG + " phone - " + phone.getText().toString());
                    String phoneString = parsePhone(phone.getText().toString());
                    intent = new Intent(Intent.CALL_ACTION, Uri.parse("tel:" + phoneString));
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.alert_label)).setMessage(
                            R.string.no_phone_message).setPositiveButton("Continue", new OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }).show();
                }
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(Constants.LOGTAG, " " + CLASSTAG + " onResume");
    }

    public static String parsePhone(String p) {
        p = p.replaceAll("\\D", "");
        p = p.replaceAll("\\s", "");
        return p.trim();
    }

}