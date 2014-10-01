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

package com.stepinmobile.fantasticbutton.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.AQuery;
import com.stepinmobile.fantasticbutton.R;
import com.stepinmobile.fantasticbutton.adapter.SharingDialogAdapter;
import com.stepinmobile.fantasticbutton.api.ButtonHandle;
import com.stepinmobile.fantasticbutton.objects.SharingDlgItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a list of social networks with support
 * for sharing functions (Facebook, Twitter, Google+).
 * Additionally, if developer set before, dialog provides
 * ability to rate the app on Google Play.
 * Extended {@link android.app.Dialog}
 */
public class SharingDialog extends Dialog {

    public static final int FACEBOOK = R.string.sd_fb;
    public static final int G = R.string.sd_google;
    public static final int TWITTER = R.string.sd_twitter;
    public static final int G_PLAY = R.string.sd_google_play;

    private AQuery aq;

    private boolean isGooglePlayEnabled = true;

    private ListView dialogLayout;

    public SharingDialog(Context context, boolean displayGooglePlay) {
        super(context);

        aq = new AQuery(context);
        isGooglePlayEnabled = displayGooglePlay;
        setupDialog(context);
    }

    public SharingDialog(Context context) {
        this(context, true);
    }

    public void setOnSocialSelectedListener(AdapterView.OnItemClickListener listClickListener) {
        dialogLayout.setOnItemClickListener(listClickListener);
    }

    /**
     * Setup the dialog appearance and title.
     * @param context
     */
    private void setupDialog(Context context) {
        dialogLayout = (ListView) aq.inflate(dialogLayout, R.layout.dlg_sharing, null);
        dialogLayout.setAdapter(prepareAdapter(context));

        this.setContentView(dialogLayout);
        this.setTitle(R.string.sd_title);
    }

    /**
     * Create and fill up an adapter for ListView element in the dialog.
     * @param context
     * @return
     */
    private SharingDialogAdapter prepareAdapter(Context context) {
        List<SharingDlgItem> items = new ArrayList<SharingDlgItem>();
        SharingDlgItem fb = new SharingDlgItem();
        fb.iconId = R.drawable.facebook;
        fb.stringId = FACEBOOK;

        SharingDlgItem twitter = new SharingDlgItem();
        twitter.iconId = R.drawable.twitter;
        twitter.stringId = TWITTER;

        SharingDlgItem google = new SharingDlgItem();
        google.iconId = R.drawable.google;
        google.stringId = G;

        items.add(fb);
        items.add(twitter);
        items.add(google);

        if(isGooglePlayEnabled){
            SharingDlgItem googlePlay = new SharingDlgItem();
            googlePlay.iconId = R.drawable.google_play;
            googlePlay.stringId = G_PLAY;
            items.add(googlePlay);
        }

        return new SharingDialogAdapter(context, 0, items);
    }
}
