package com.ekplate.android.activities.socialsharemodule;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.config.BaseActivity;
import com.google.android.gms.plus.PlusShare;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.listeners.OnPublishListener;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

public class SocialShareActivity extends BaseActivity {

    private ImageView ivTwitterIcon, ivFacebookIcon, ivWechatIcon, ivMailIcon, ivGooglePlusIcon, ivWhatsappIcon;
    private Animation animMoveRightToLeftMailEntry, animMoveRightToLeftGooglePlusEntry, animMoveRightToLeftWhatsappEntry,
            animMoveLeftToRightTwitterEntry, animMoveLeftToRightFacebookEntry, animMoveLeftToRightWechatEntry,
            animMoveLeftToRightTwitterExit, animMoveRightToLeftMailExit, animMoveRightToLeftGooglePlusExit,
            animMoveRightToLeftWhatsappExit, animMoveLeftToRightFacebookExit, animMoveLeftToRightWechatExit;
    private final static String TAG = "SocialShareActivity";
    private SimpleFacebook mSimpleFacebook;
    private Feed feed;
    private LinearLayout llLeftLayoutSocialShare, llRightLayoutSocialShare;
    private String vendorName, appUrl, sharedText;
    private int shareCompleteFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_share);
        getSupportActionBar().hide();
        setUpParameter();
        initialize();
        doAnimationForEntry();
        onClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
        if(shareCompleteFlag == 1) {
            shareCompleteFlag = 0;
            doAnimationForExit();
        }
    }

    private void setUpParameter(){
        if(getIntent().getExtras().getString("route_from").equalsIgnoreCase("user_profile")) {
            vendorName = "";
            appUrl = "http://www.ekplate.com/app.php";
            sharedText = "I am using an amazing app called Ekplate ! - your ultimate street food guide ! " +
                    "Click the link below and Download it| " + appUrl;
        } /*else if (getIntent().getExtras().getString("route_from").equalsIgnoreCase("my_story")) {
            vendorName = getIntent().getExtras().getString("vendor_name");
            appUrl = "http://www.ekplate.com/app.php";
            sharedText = "Check the story of " + vendorName + " | " + vendorAddress
                    + " on Ekplate | " + imageUrl;

        }*/
        else if (getIntent().getExtras().getString("route_from").equalsIgnoreCase("discovar")) {
            vendorName = "";
            appUrl = "http://www.ekplate.com/app.php";
            sharedText = "I am using an amazing app called Ekplate ! - your ultimate street food guide ! " +
                    "Click the link below and Download it|" + appUrl;
        }
        else {
            vendorName = getIntent().getExtras().getString("vendor_name");
            appUrl = "http://www.ekplate.com/app.php";
            sharedText = "I found this amazing street food vendor " + vendorName + " on Ekplate app - " +
                    "your ultimate street food guide | " + appUrl;
        }
    }

    private void initialize(){
        llLeftLayoutSocialShare = (LinearLayout) findViewById(R.id.llLeftLayoutSocialShare);
        llRightLayoutSocialShare = (LinearLayout) findViewById(R.id.llRightLayoutSocialShare);
        ivTwitterIcon = (ImageView) findViewById(R.id.ivTwitterIcon);
        ivFacebookIcon = (ImageView) findViewById(R.id.ivFacebookIcon);
        ivWechatIcon = (ImageView) findViewById(R.id.ivWechatIcon);
        ivMailIcon = (ImageView) findViewById(R.id.ivMailIcon);
        ivGooglePlusIcon = (ImageView) findViewById(R.id.ivGooglePlusIcon);
        ivWhatsappIcon = (ImageView) findViewById(R.id.ivWhatsappIcon);
        feed = new Feed.Builder()
                .setMessage(vendorName)
                .setName(vendorName)
                .setCaption(vendorName)
                .setDescription(sharedText)
                .setLink(appUrl)
                .build();
    }

    private void onClick(){
        ivWhatsappIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAppInstalled("com.whatsapp")) {
                    shareWithWhatsapp();
                } else {
                    Toast.makeText(SocialShareActivity.this, "Whatsapp is not installed.", Toast.LENGTH_LONG).show();
                }
            }
        });

        ivMailIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAppInstalled("com.google.android.gm")) {
                    shareWithEmail();
                } else {
                    Toast.makeText(SocialShareActivity.this, "Gmail add is not installed.", Toast.LENGTH_LONG).show();
                }
            }
        });

        ivGooglePlusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWithGooglePlus();
            }
        });

        ivFacebookIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareCompleteFlag = 1;
                mSimpleFacebook.publish(feed, true, onPublishListener);
            }
        });

        ivWechatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (isAppInstalled("com.tencent.mm")) {
                    shareWithWechat();
                } else {
                    Toast.makeText(SocialShareActivity.this, "WeChat is not installed.", Toast.LENGTH_LONG).show();
                }*/
                shareWithWechat();
            }
        });

        ivTwitterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (isAppInstalled("com.tencent.mm")) {
                    shareWithTwitter();
                } else {
                    Toast.makeText(SocialShareActivity.this, "Twitter is not installed.", Toast.LENGTH_LONG).show();
                }*/

                shareWithTwitter();
            }
        });

        llLeftLayoutSocialShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAnimationForExit();
            }
        });

        llRightLayoutSocialShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAnimationForExit();
            }
        });
    }

    private void doAnimationForEntry(){
        animMoveLeftToRightTwitterEntry = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_left_to_right_entry);
        animMoveLeftToRightFacebookEntry =  AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_left_to_right_entry);
        animMoveLeftToRightWechatEntry =  AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_left_to_right_entry);
        animMoveRightToLeftMailEntry = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_right_to_left_entry);
        animMoveRightToLeftGooglePlusEntry = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_right_to_left_entry);
        animMoveRightToLeftWhatsappEntry = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_right_to_left_entry);
        animMoveLeftToRightTwitterEntry.setAnimationListener(animationListenerTwitterEntry);
        animMoveLeftToRightFacebookEntry.setAnimationListener(animationListenerFacebookEntry);
        animMoveRightToLeftMailEntry.setAnimationListener(animationListenerMailEntry);
        animMoveRightToLeftGooglePlusEntry.setAnimationListener(animationListenerGooglePlusEntry);

        ivTwitterIcon.startAnimation(animMoveLeftToRightTwitterEntry);
        ivMailIcon.startAnimation(animMoveRightToLeftMailEntry);
    }

    private void doAnimationForExit(){
        animMoveRightToLeftMailExit = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_left_to_right_exit);
        animMoveRightToLeftGooglePlusExit = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_left_to_right_exit);
        animMoveRightToLeftWhatsappExit = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_left_to_right_exit);
        animMoveLeftToRightTwitterExit = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_right_to_left_exit);
        animMoveLeftToRightFacebookExit = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_right_to_left_exit);
        animMoveLeftToRightWechatExit = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_right_to_left_exit);
        animMoveRightToLeftMailExit.setAnimationListener(animationListenerMailExit);
        animMoveRightToLeftGooglePlusExit.setAnimationListener(animationListenerGooglePlusExit);
        animMoveLeftToRightTwitterExit.setAnimationListener(animationListenerTwitterExit);
        animMoveLeftToRightFacebookExit.setAnimationListener(animationListenerFacebookExit);
        animMoveRightToLeftWhatsappExit.setAnimationListener(animationListenerWhatsappExit);
        animMoveLeftToRightWechatExit.setAnimationListener(animationListenerWechatExit);

        ivMailIcon.startAnimation(animMoveRightToLeftMailExit);
        ivTwitterIcon.startAnimation(animMoveLeftToRightTwitterExit);
    }

    Animation.AnimationListener animationListenerTwitterEntry = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            ivFacebookIcon.setVisibility(View.VISIBLE);
            ivFacebookIcon.startAnimation(animMoveLeftToRightFacebookEntry);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    Animation.AnimationListener animationListenerFacebookEntry = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            ivWechatIcon.setVisibility(View.VISIBLE);
            ivWechatIcon.startAnimation(animMoveLeftToRightWechatEntry);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    Animation.AnimationListener animationListenerMailEntry = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            ivGooglePlusIcon.setVisibility(View.VISIBLE);
            ivGooglePlusIcon.startAnimation(animMoveRightToLeftGooglePlusEntry);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    Animation.AnimationListener animationListenerGooglePlusEntry = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            ivWhatsappIcon.setVisibility(View.VISIBLE);
            ivWhatsappIcon.startAnimation(animMoveRightToLeftWhatsappEntry);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    Animation.AnimationListener animationListenerTwitterExit = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            ivTwitterIcon.setVisibility(View.INVISIBLE);
            ivFacebookIcon.startAnimation(animMoveLeftToRightFacebookExit);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    Animation.AnimationListener animationListenerMailExit = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            ivMailIcon.setVisibility(View.INVISIBLE);
            ivGooglePlusIcon.startAnimation(animMoveRightToLeftGooglePlusExit);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    Animation.AnimationListener animationListenerFacebookExit = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            ivFacebookIcon.setVisibility(View.INVISIBLE);
            ivWechatIcon.startAnimation(animMoveLeftToRightWechatExit);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    Animation.AnimationListener animationListenerGooglePlusExit = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            ivGooglePlusIcon.setVisibility(View.INVISIBLE);
            ivWhatsappIcon.startAnimation(animMoveRightToLeftWhatsappExit);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    Animation.AnimationListener animationListenerWechatExit = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            ivWechatIcon.setVisibility(View.INVISIBLE);
            onBackPressed();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    Animation.AnimationListener animationListenerWhatsappExit = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            ivWhatsappIcon.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    private void shareWithWhatsapp(){
        shareCompleteFlag = 1;
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, sharedText);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        startActivity(sendIntent);
    }

    private void shareWithWechat(){
        shareCompleteFlag = 1;
        /*Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, sharedText);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.tencent.mm");
        startActivity(sendIntent);*/
        Intent sendIntent = new Intent(android.content.Intent.ACTION_VIEW);
        sendIntent.putExtra("address", "");
        sendIntent.putExtra("sms_body", sharedText);
        sendIntent.setType("vnd.android-dir/mms-sms");
        startActivity(sendIntent);
    }

    private void shareWithTwitter(){
        /*Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.twitter.android");
        startActivity(sendIntent);*/
        shareCompleteFlag = 1;
        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text(sharedText);
        builder.show();
    }

    private void shareWithEmail(){
        shareCompleteFlag = 1;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
        intent.putExtra(Intent.EXTRA_TEXT, sharedText);
        intent.setPackage("com.google.android.gm");
        startActivity(intent);
    }

    private void  shareWithGooglePlus(){
        shareCompleteFlag = 1;
        Intent shareIntent = new PlusShare.Builder(this)
                .setType("text/plain")
                .setText(sharedText)
                .setContentUrl(Uri.parse(appUrl))
                .getIntent();

        startActivityForResult(shareIntent, 0);
    }

    private boolean isAppInstalled(String packageName){
        Intent mIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (mIntent != null) {
            return true;
        }
        else {
            return false;
        }
    }

    OnPublishListener onPublishListener = new OnPublishListener() {
        @Override
        public void onComplete(String postId) {
            Log.i(TAG, "Published successfully. The new post id = " + postId);
            Toast.makeText(SocialShareActivity.this, "Published successfully", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onException(Throwable throwable) {
            super.onException(throwable);
        }

        @Override
        public void onFail(String reason) {

            Log.i(TAG, "Failed message sharing = " + reason);
           // Toast.makeText(SocialShareActivity.this, "Not Published!.", Toast.LENGTH_LONG).show();
            super.onFail(reason);
        }
    };
}
