package com.delex.invite;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.delex.driver.R;
import com.delex.utility.ServiceUrl;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

/**
 * Created by Admin on 8/4/2017.
 */

public class InviteFrag extends Fragment {
    private View rootView;
    private Typeface ClanaproNarrNews;
    private SessionManager sessionManager;
    private String inviteMessage;
    private ShareDialog shareDialog;
    private TextView tvReferral;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.invite_fragment, container, false);
        sessionManager = new SessionManager(getActivity());
        //checkAndRequestPermissions();
        if(sessionManager.getLang().equalsIgnoreCase("ar")){
            getActivity().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

//        FacebookSdk.sdkInitialize(getActivity());
        shareDialog=new ShareDialog(getActivity());
        initLayoutID();
        return rootView;
    }

    public void initLayoutID() {

        inviteMessage=getActivity().getResources().getString(R.string.link);
        inviteMessage=inviteMessage.replace("XXXXX",sessionManager.getReferralCode());

        ClanaproNarrNews = Typeface.createFromAsset(getContext().getAssets(), "fonts/ClanPro-NarrNews.otf");

        TextView tvHeader = (TextView) rootView.findViewById(R.id.tvHeader);
        tvHeader.setTypeface(ClanaproNarrNews);

        tvReferral = (TextView) rootView.findViewById(R.id.tvReferral);
        tvReferral.setTypeface(ClanaproNarrNews);

        tvReferral.setText(sessionManager.getReferralCode());

        TextView tvShareText = (TextView) rootView.findViewById(R.id.tvShareText);
        tvShareText.setTypeface(ClanaproNarrNews);

        TextView tvFacebook = (TextView) rootView.findViewById(R.id.tvFacebook);
        tvFacebook.setTypeface(ClanaproNarrNews);

        TextView tvTwitter = (TextView) rootView.findViewById(R.id.tvTwitter);
        tvTwitter.setTypeface(ClanaproNarrNews);

        TextView tvMail = (TextView) rootView.findViewById(R.id.tvMail);
        tvMail.setTypeface(ClanaproNarrNews);

        TextView tvMessage = (TextView) rootView.findViewById(R.id.tvMessage);
        tvMessage.setTypeface(ClanaproNarrNews);

        ImageView ivFacebook = (ImageView) rootView.findViewById(R.id.ivFacebook);
        ivFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteViaFacebook();
            }
        });
        ImageView ivTwitter = (ImageView) rootView.findViewById(R.id.ivTwitter);
        ivTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteViaTwitter();
            }
        });
        ImageView ivMail = (ImageView) rootView.findViewById(R.id.ivMail);
        ivMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteViaEmail();
            }
        });
        ImageView ivMessage = (ImageView) rootView.findViewById(R.id.ivMessage);
        ivMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteViaMessage();
            }
        });

    }

    private void inviteViaTwitter() {
        Intent intent;
        try {
            // get the Twitter app if possible
                    /*getActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);
				     intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=2776019551"));
				     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ServiceUrl.TWITTER_URL));

        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ServiceUrl.TWITTER_URL));
        }
        startActivity(intent);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private void inviteViaMessage() {



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) //At least KitKat
        {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(getContext()); //Need to change the build to API 19

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra("sms_body", inviteMessage);

            if (defaultSmsPackageName != null)//Can be null in case that there is no default, then the user would be able to choose any app that support this intent.
            {
                sendIntent.setPackage(defaultSmsPackageName);
            }
            startActivity(sendIntent);

        }
        else //For early versions, do what worked for you before.
        {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setType("vnd.android-dir/mms-sms");
            sendIntent.putExtra("sms_body", inviteMessage);
            startActivity(sendIntent);
        }

/*
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("vnd.android-dir/mms-sms");
        sendIntent.putExtra("sms_body", inviteMessage);
        startActivity(sendIntent);*/
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private void inviteViaEmail() {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
        i.setType("message/rfc822");
        i.setData(Uri.parse("mailto:" + ""));
        i.putExtra(Intent.EXTRA_SUBJECT, "Invite:" + getResources().getString(R.string.app_name));
        i.putExtra(Intent.EXTRA_TEXT, inviteMessage);
        startActivity(Intent.createChooser(i, "Send email"));
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private void inviteViaFacebook() {
        if (Utility.isNetworkAvailable(getActivity())) {
            /*String facebookUrl = ServiceUrl.FACEBOOK_URL;
            try {
                int versionCode = getActivity().getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
                if (versionCode >= 3002850) {
                    Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } else {

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(facebookUrl));
                    startActivity(i);
                }
            } catch (Exception e) {
                // Facebook is not installed. Open the browser
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(facebookUrl));
                startActivity(i);
            }*/

            if (ShareDialog.canShow(ShareLinkContent.class))
            {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle(getActivity().getResources().getString(R.string.app_name))
                        .setContentDescription(inviteMessage)
//                        .setImageUrl(Uri.parse("https://s3.amazonaws.com/yellowcarappimages/ic_launcher.png"))
                        .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.seven_moola.driver"))
                        .build();

                shareDialog.show(linkContent);
            }
        }

    }
////////////////////////////////////////////////////////////////////////////////////////////////////

}
