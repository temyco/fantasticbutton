/**
 * Copyright 2014-present StepInMobile.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package com.stepinmobile.fantasticbutton.api;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.auth.FacebookHandle;
import com.androidquery.auth.TwitterHandle;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.plus.PlusShare;
import com.stepinmobile.fantasticbutton.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * This class handle queries for sharing provided text.
 *
 * Created by Nastya on 24.09.2014.
 */
public class ButtonHandle {
    private static final String TAG = ButtonHandle.class.getSimpleName();
    private static final String PERMISSIONS = "read_stream,read_friendlists,manage_friendlists," +
            "manage_notifications,publish_stream,publish_checkins,offline_access,user_photos," +
            "user_likes,user_groups,friends_photos";
    private final int ACTIVITY_SSO = 1000;

    private static ButtonHandle instance;

    private AQuery aq;
    private FacebookHandle fbHandle;
    private TwitterHandle twitterHandle;

    private String fbAppId;
    private String twitterAppId;
    private String twitterAppSecret;

    private ButtonHandle(Activity activity) {
        aq = new AQuery(activity);
    }

    public static ButtonHandle getInstance(Activity activity) {
        if (instance == null) {
            instance = new ButtonHandle(activity);
        }

        return instance;
    }

    public String getFbAppId() {
        return fbAppId;
    }

    /**
     * Method initialize handler for facebook with provided app id
     *
     * @param fbAppId application id, which would be used for initialization
     */
    public void setFbAppId(String fbAppId) {
        this.fbAppId = fbAppId;
        fbHandle = new FacebookHandle((Activity) aq.getContext(), fbAppId, PERMISSIONS);
    }

    public String getTwitterAppId() {
        return twitterAppId;
    }

    /**
     * Method initialize handler for twitter with provided app id
     *
     * @param twitterAppId application id, which would be used for initialization
     */
    public void setTwitterAppId(String twitterAppId) {
        this.twitterAppId = twitterAppId;
        twitterHandle = new TwitterHandle((Activity) aq.getContext(), twitterAppId, twitterAppSecret);
    }

    public String getTwitterAppSecret() {
        return twitterAppSecret;
    }

    public void setTwitterAppSecret(String twitterAppSecret) {
        this.twitterAppSecret = twitterAppSecret;
    }

    //             //
    //  FACEBOOK   //
    //             //

    /**
     * Method check network availability and share provided text. Also it will ask for authentication if it will be needed.
     * If device already have installed facebook application, it will share via application.
     *
     * @param textToShare text, which wold be shared.
     */
    public void shareOnFacebook(String textToShare) {
        if (!isNetworkAvailable()) {
            Toast.makeText(aq.getContext(),
                    R.string.no_internet, Toast.LENGTH_LONG).show();
        } else {
            if (fbHandle != null) {
                if (fbHandle.authenticated()) {
                    postOnFb(textToShare);
                } else {
                    authFacebookSsoAndShare(textToShare);
                }
            }
        }
    }

    /**
     * Method authenticate in facebook and if authentication would be successful it will call share method.
     *
     * @param textToShare text, which wold be shared.
     */
    private void authFacebookSsoAndShare(final String textToShare) {
        Log.d(TAG, "fb auth...");
        Log.d(TAG, "is sso: " + fbHandle.isSSOAvailable());
        fbHandle.sso(ACTIVITY_SSO);

        String url = "https://graph.facebook.com/me";
        aq.auth(fbHandle).progress(new ProgressBar(aq.getContext()))
                .ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {
                        if (object != null) {
                            Log.d(TAG, "auth success: " + object.toString());
                            postOnFb(textToShare);
                        }
                    }
                });

    }

    /**
     * This method share provided text on Facebook.
     *
     * @param textForShare text, which wold be shared.
     */
    private void postOnFb(String textForShare) {
        Log.d(TAG, "fb posting...");
        String url = "https://graph.facebook.com/me/feed";

        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                if (object != null) {
                    Log.d(TAG, "posting success: " + object.toString());
                }
            }
        };

        Map<String, String> params = new HashMap<String, String>();
        params.put("message", textForShare);
        params.put("access_token", fbHandle.getToken());
        cb.params(params);
        cb.url(url);
        cb.type(JSONObject.class);
        aq.ajax(cb);
    }

    public int getSso_code() {
        return ACTIVITY_SSO;
    }

    //             //
    //     G+      //
    //             //

    /**
     * Method check network availability and share in g+ social, if it possible.
     *
     * @param textToShare  text, which wold be shared.
     */
    public void shareOnGoogle(String textToShare) {
        if (!isNetworkAvailable()) {
            Toast.makeText(aq.getContext(),
                    R.string.no_internet, Toast.LENGTH_LONG).show();
        } else {
            Intent shareIntent = new PlusShare.Builder(aq.getContext())
                    .setType("text/plain")
                    .setText(textToShare)
                    .getIntent();

            ((Activity) aq.getContext()).startActivityForResult(shareIntent, 0);
        }
    }

    //                //
    // Rate on market //
    //                //

    /**
     * Method check network availability and open Google Play.
     *
     */
    public void rateOnMarket() {
        if (!isNetworkAvailable()) {
            Toast.makeText(aq.getContext(),
                    R.string.no_internet, Toast.LENGTH_LONG).show();
        } else {
            Activity ac = (Activity) aq.getContext();
            Uri uri = Uri.parse("market://details?id=" + ac.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            try {
                ac.startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                ac.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id="
                                + ac.getPackageName())));
            }
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        fbHandle.onActivityResult(requestCode, resultCode, data);
    }

    //           //
    //  TWITTER  //
    //           //

    // it's a small hack to provide text which should be shared via all callbacks and runnables, if user don't authenticated yet.
    private String tweetToBeTweeted;

    /**
     * Method check network availability and share provided text. Also it will ask for authentication if it will be needed.
     * If device already have installed twitter application, it will share via application.
     *
     * @param textToShare text, which wold be shared.
     */
    public void shareOnTwitter(String textToShare) {
        if (!isNetworkAvailable()) {
            Toast.makeText(aq.getContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
        } else {
            tweetToBeTweeted = textToShare;
            Log.d(TAG, "share on twitter");
            Intent intent = getShareIntent("twitter", "sub", tweetToBeTweeted);
            if (intent != null) {
                aq.getContext().startActivity(intent);
            } else {
                if (!twitterHandle.authenticated()) {
                    Log.d(TAG, "!authenticated");
                    authTwitter();
                } else {
                    Log.d(TAG, "authenticated");
                    new Thread(postTweetRunnable).start();
                }
            }
        }
    }

    /**
     * This method search in all applications, and if it will find one, which contains in package name parameter <b>type</b>, it will create share intent and return it.
     * In another case, if application wouldn't be found it will return null.
     *
     * @param type part of application package name
     * @param subject title, which would be applied to created share event
     * @param text content, which would be provided into share intent
     * @return created share intent or <b>null</b>, if application wouldn't be found
     */
    private Intent getShareIntent(String type, String subject, String text) {
        boolean found = false;
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");

        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = ((Activity) aq.getContext()).getPackageManager().queryIntentActivities(share, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(type) ||
                        info.activityInfo.name.toLowerCase().contains(type)) {
                    share.putExtra(Intent.EXTRA_SUBJECT, subject);
                    share.putExtra(Intent.EXTRA_TEXT, text);
                    share.setPackage(info.activityInfo.packageName);
                    found = true;
                    break;
                }
            }
            if (!found)
                return null;

            return share;
        }
        return null;
    }

    /**
     * This method launch twitter authentication, and if it would be successfully it will share message.
     */
    private void authTwitter() {
        Log.d(TAG, "twitter auth...");
        String url = "https://api.twitter.com/oauth/authorize";
        aq.auth(twitterHandle).progress(new ProgressBar(aq.getContext()))
                .ajax(url, JSONArray.class, twitterCb);
    }

    private AjaxCallback<JSONArray> twitterCb = new AjaxCallback<JSONArray>() {
        @Override
        public void callback(String url, JSONArray object, AjaxStatus status) {
            Log.d(TAG, "twitter callback");
            if (twitterHandle.authenticated()) {
                Log.d(TAG, "twitter callback authenticated");
                new Thread(postTweetRunnable).start();
            }
        }
    };

    /**
     * Method post tweet.
     */
    private void postTweet() {
        Log.d(TAG, "twitter posting...");
        Twitter twitter = new TwitterFactory().getInstance();
        AccessToken accessToken = new AccessToken(twitterHandle.getToken(),
                twitterHandle.getSecret());
        twitter.setOAuthConsumer(twitterAppId, twitterAppSecret);
        twitter.setOAuthAccessToken(accessToken);

        Status status = null;
        try {
            status = twitter.updateStatus(tweetToBeTweeted);
            Log.d(TAG, "status posted. " + status.getText());
        } catch (TwitterException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private Runnable postTweetRunnable = new Runnable() {
        @Override
        public void run() {
            postTweet();
            Toast.makeText(aq.getContext(), "Twitted", Toast.LENGTH_LONG);
        }
    };

    /**
     * Method check internet connection and retrieve boolean result.
     * @return true if network available
     */
    private boolean isNetworkAvailable() {
        boolean isAvailable = false;

        ConnectivityManager manager = (ConnectivityManager)
                aq.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }


        return isAvailable;
    }
}
