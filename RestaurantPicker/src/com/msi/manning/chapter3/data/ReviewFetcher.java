package com.msi.manning.chapter3.data;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom.Element;

import android.util.Log;

import com.msi.manning.chapter3.Constants;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * Use Google Base and Rome with specified criteria to obtain Review data.
 * 
 * @author charliecollins
 * 
 * TODO - this is slow on Androud, via Rome and JDOM, replace this with a Custom
 * SAXParser and Handler, just like WeatherReporter has (rip out Rome and JDOM). 
 *
 */
public class ReviewFetcher {
    private static final String CLASSTAG = ReviewFetcher.class.getSimpleName();

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final String QBASE = "http://www.google.com/base/feeds/snippets/-/reviews?bq=[review%20type:restaurant]";
    private static final String QC_PREFIX = "[description:";
    private static final String QC_SUFFIX = "]";
    private static final String QL_PREFIX = "[location:"; 
    private static final String QL_SUFFIX = "]";
    private static final String QR_PREFIX = "[rating:";
    private static final String QR_SUFFIX = "]";
    private static final String QSTART_INDEX = "&start-index=";
    private static final String QMAX_RESULTS = "&max-results=";

    private String query;
    private int start;
    private int numResults;
    private boolean devMode;

    /**
     * Construct ReviewFetcher with cuisine, location, rating, paging params, and boolean to indicate devMode.
     * 
     * @param description
     * @param location
     * @param rating
     * @param start
     * @param numResults
     * @param devMode
     */
    public ReviewFetcher(String cuisine, String location, String rating, int start, int numResults, boolean devMode) {
        this.start = start;
        this.numResults = numResults;
        this.devMode = devMode;

        // urlencode params
        try {
            if (cuisine != null) {
                cuisine = URLEncoder.encode(cuisine, "UTF-8");
            }
            if (location != null) {
                location = URLEncoder.encode(location, "UTF-8");
            }
            if (rating != null) {
                rating = URLEncoder.encode(rating, "UTF-8");
            }
        } catch (UnsupportedEncodingException e1) {

            Log.e(Constants.LOGTAG, " " + CLASSTAG + " ERROR - could not encode query params");
        }

        // build query
        this.query = QBASE;
        if (cuisine != null && !cuisine.equals("ANY")) {
            this.query += QC_PREFIX + cuisine + QC_SUFFIX;
        }
        if (rating != null && !rating.equals("ALL")) {
            this.query += QR_PREFIX + rating + QR_SUFFIX;
        }
        if (location != null && !location.equals("")) {
            this.query += QL_PREFIX + location + QL_SUFFIX;
        }
        this.query += QSTART_INDEX + this.start + QMAX_RESULTS + this.numResults;

        Log.v(Constants.LOGTAG, " " + CLASSTAG + " query - " + this.query);
    }

    /**
     * Construct ReviewFetcher with cuisine, location, rating, and paging params.
     * 
     * @param cuisine
     * @param location
     * @param rating
     * @param start
     * @param numResults
     */
    public ReviewFetcher(String cuisine, String location, String rating, int start, int numResults) {
        this(cuisine, location, rating, start, numResults, false);
    }
    
    /**
     * Call Google Base via Rome and establish Reviews.
     * 
     * @return
     */
    public List<Review> getReviews() {

        Log.v(Constants.LOGTAG, " " + CLASSTAG + " getReviews");
        long start = System.currentTimeMillis();
        List<Review> results = null;

        if (!devMode) {
            try {
                URL feedUrl = new URL(this.query);
                // TODO - huge delay here on build call, takes 15-20 seconds 
                // (takes < second for same class outside Android)
                SyndFeed feed = new SyndFeedInput().build(new XmlReader(feedUrl));
                List<SyndEntry> entries = feed.getEntries();
                for (SyndEntry e : entries) {
                    Log.v(Constants.LOGTAG, " " + CLASSTAG + " processing entry " + e.getTitle());

                    if (results == null)
                        results = new ArrayList<Review>(numResults);

                    Review review = new Review();
                    review.setTitle(e.getTitle());
                    review.setAuthor(e.getAuthor());
                    review.setDate(e.getPublishedDate());
                    review.setLink(e.getLink());

                    if (e.getContents() != null && e.getContents().size() > 0) {
                        SyndContent content = (SyndContent) e.getContents().get(0);
                        review.setContent(content.getValue());
                    } else {
                        review.setContent("NA");
                    }

                    // getting foreignMarkup
                    List<Element> elements = (List<Element>) e.getForeignMarkup();
                    for (Element ele : elements) {
                        String eleName = ele.getName();
                        String eleValue = ele.getText();
                        if (eleName.equals("phone_of_item_reviewed")) {
                            review.setPhone(eleValue);
                        } else if (eleName.equals("name_of_item_reviewed")) {
                            review.setName(eleValue);
                        } else if (eleName.equals("rating")) {
                            review.setRating(eleValue);
                        } else if (eleName.equals("review_date")) {
                            review.setDate(DATE_FORMAT.parse(eleValue.substring(0, 9)));
                        } else if (eleName.equals("location")) {
                            review.setLocation(eleValue);
                        }
                    }
                    results.add(review);
                }
            } catch (Exception e) {
                Log.e(Constants.LOGTAG, " " + CLASSTAG + " getReviews ERROR", e);
            }
        } else {
            Log.v(Constants.LOGTAG, " " + CLASSTAG + " devMode true - returning MOCK reviews");
            results = this.getMockReviews();
        }

        long duration = (System.currentTimeMillis() - start) / 1000;
        Log.v(Constants.LOGTAG, " " + CLASSTAG + " call duration - " + duration);
        return results;
    }

    /**
     * Mock reviews for devMode debug.
     * 
     * @return
     */
    private List<Review> getMockReviews() {

        Log.v(Constants.LOGTAG, " " + CLASSTAG + " getMockReviews");
        List<Review> results = new ArrayList<Review>(5);

        Review r1 = new Review();
        r1.setAuthor("author1");
        r1.setDate(new Date());
        r1.setLink("link1");
        r1.setLocation("location1");
        r1.setName("name1");
        r1.setPhone("phone1");
        r1.setRating("rating1");
        r1.setTitle("title1");
        results.add(r1);

        Review r2 = new Review();
        r2.setAuthor("author2");
        r2.setDate(new Date());
        r2.setLink("link2");
        r2.setLocation("location2");
        r2.setName("name2");
        r2.setPhone("phone2");
        r2.setRating("rating2");
        r2.setTitle("title2");
        results.add(r2);

        Review r3 = new Review();
        r3.setAuthor("author3");
        r3.setDate(new Date());
        r3.setLink("link3");
        r3.setLocation("location3");
        r3.setName("name3");
        r3.setPhone("phone3");
        r3.setRating("rating3");
        r3.setTitle("title3");
        results.add(r3);

        return results;
    }    

}