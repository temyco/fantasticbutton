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

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.stepinmobile.fantasticbutton.R;
import com.stepinmobile.fantasticbutton.api.ButtonHandle;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Represents extended Android Button with an attractive animation and
 * additional functionality.
 *
 * Additional functionality of the button allows user to call a dialog
 * that provides an ability to share specified text with Facebook, Twitter
 * and Google+. Also the dialog allows the user to rate app on Google Play.
 */
public class FantasticButton extends Button implements View.OnClickListener {

    private final Handler handler = new Handler();

    // Parameters can be set in an layout with custom xml attributes.
    // The of available custom attributes are in /values/attrs.xml file.
    private boolean isAnimationNeeded = true;
    private int animationDelay = 5000;
    private boolean isGooglePlayNeeded = true;

    private String textForShare;

    private SharingDialog sharingDialog;

    public FantasticButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOnClickListener(this);
        parseXmlAttrs(context, attrs);
        if(isAnimationNeeded) {
            handler.post(animationRunnable);
        }
    }

    /**
     * Parse custom FantasticButton xml attributes
     * @param context
     * @param attrs
     */
    private void parseXmlAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FantasticButton,
                0, 0);

        try {
            isAnimationNeeded = a.getBoolean(R.styleable.FantasticButton_is_animated, true);
            animationDelay = a.getInteger(R.styleable.FantasticButton_anim_delay_in_millis, 5000);
            isGooglePlayNeeded = a.getBoolean(R.styleable.FantasticButton_is_google_play_enabled, true);
        } finally {
            a.recycle();
        }
    }

    //                                                              //
    //  Helper methods for the button animation                     //
    //  implementing.                                               //
    //  The code was taken from the Cyril Mottier post              //
    //  https://plus.google.com/+CyrilMottier/posts/FABaJhRMCuy</a> //
    //

    private static ObjectAnimator tada(View view) {
        return tada(view, 1f);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static ObjectAnimator tada(View view, float shakeFactor) {

        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofKeyframe(View.SCALE_X,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, .9f),
                Keyframe.ofFloat(.2f, .9f),
                Keyframe.ofFloat(.3f, 1.1f),
                Keyframe.ofFloat(.4f, 1.1f),
                Keyframe.ofFloat(.5f, 1.1f),
                Keyframe.ofFloat(.6f, 1.1f),
                Keyframe.ofFloat(.7f, 1.1f),
                Keyframe.ofFloat(.8f, 1.1f),
                Keyframe.ofFloat(.9f, 1.1f),
                Keyframe.ofFloat(1f, 1f)
        );

        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofKeyframe(View.SCALE_Y,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, .9f),
                Keyframe.ofFloat(.2f, .9f),
                Keyframe.ofFloat(.3f, 1.1f),
                Keyframe.ofFloat(.4f, 1.1f),
                Keyframe.ofFloat(.5f, 1.1f),
                Keyframe.ofFloat(.6f, 1.1f),
                Keyframe.ofFloat(.7f, 1.1f),
                Keyframe.ofFloat(.8f, 1.1f),
                Keyframe.ofFloat(.9f, 1.1f),
                Keyframe.ofFloat(1f, 1f)
        );

        PropertyValuesHolder pvhRotate = PropertyValuesHolder.ofKeyframe(View.ROTATION,
                Keyframe.ofFloat(0f, 0f),
                Keyframe.ofFloat(.1f, -3f * shakeFactor),
                Keyframe.ofFloat(.2f, -3f * shakeFactor),
                Keyframe.ofFloat(.3f, 3f * shakeFactor),
                Keyframe.ofFloat(.4f, -3f * shakeFactor),
                Keyframe.ofFloat(.5f, 3f * shakeFactor),
                Keyframe.ofFloat(.6f, -3f * shakeFactor),
                Keyframe.ofFloat(.7f, 3f * shakeFactor),
                Keyframe.ofFloat(.8f, -3f * shakeFactor),
                Keyframe.ofFloat(.9f, 3f * shakeFactor),
                Keyframe.ofFloat(1f, 0)
        );

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, pvhScaleX, pvhScaleY, pvhRotate).setDuration(1000);

        return animator;
    }

    @Override
    public void onClick(View v) {
        sharingDialog = new SharingDialog(getContext(), isGooglePlayNeeded);
        sharingDialog.setOnSocialSelectedListener(listClickListener);
        sharingDialog.show();
    }

    private AdapterView.OnItemClickListener listClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ButtonHandle handle = ButtonHandle.getInstance((Activity)getContext());
            if((int) id == SharingDialog.FACEBOOK)
                    handle.shareOnFacebook(getTextForShare());
            if((int) id == SharingDialog.TWITTER)
                    handle.shareOnTwitter(getTextForShare());
            if((int) id == SharingDialog.G)
                    handle.shareOnGoogle(getTextForShare());
            if((int) id == SharingDialog.G_PLAY)
                    handle.rateOnMarket();

            sharingDialog.dismiss();
        }
    };

    /**
     * Runnable to implement a repeating
     * for the button animation
     */
    private Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            tada(FantasticButton.this).start();
            handler.postDelayed(animationRunnable, animationDelay);
        }
    };

    /**
     * Cancel button's animation.
     * Operation cannot be undo.
     */
    public void cancelAnimation() {
        handler.removeCallbacks(animationRunnable);
    }

    /**
     * Get the current text of message to share.
     * @return share message
     */
    public String getTextForShare() {
        return textForShare;
    }

    /**
     * Set a message to share.
     * @param textForShare
     */
    public void setTextForShare(String textForShare) {
        this.textForShare = textForShare;
    }

}
