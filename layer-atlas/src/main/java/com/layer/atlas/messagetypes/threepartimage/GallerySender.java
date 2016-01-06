package com.layer.atlas.messagetypes.threepartimage;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.layer.atlas.R;
import com.layer.atlas.messagetypes.AttachmentSender;
import com.layer.atlas.util.Log;
import com.layer.sdk.messaging.Message;

import java.io.IOException;

public class GallerySender extends AttachmentSender {
    public static final int REQUEST_CODE = 47001;

    @SuppressWarnings("unused")
    public GallerySender(int titleResId, Integer iconResId, Activity activity) {
        super(titleResId, iconResId, activity);
    }

    @SuppressWarnings("unused")
    public GallerySender(String title, Integer iconResId, Activity activity) {
        super(title, iconResId, activity);
    }

    @SuppressWarnings("unused")
    public GallerySender(int titleResId, Integer iconResId, android.app.Fragment fragment) {
        super(titleResId, iconResId, fragment);
    }

    @SuppressWarnings("unused")
    public GallerySender(String title, Integer iconResId, android.app.Fragment fragment) {
        super(title, iconResId, fragment);
    }

    @SuppressWarnings("unused")
    public GallerySender(int titleResId, Integer iconResId, Fragment fragment) {
        super(titleResId, iconResId, fragment);
    }

    @SuppressWarnings("unused")
    public GallerySender(String title, Integer iconResId, Fragment fragment) {
        super(title, iconResId, fragment);
    }

    @Override
    public boolean requestSend() {
        if (!super.requestSend()) {
            return false;
        }
        if (Log.isLoggable(Log.VERBOSE)) Log.v("Sending gallery image");
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getActivity().getString(R.string.atlas_gallery_sender_chooser)), REQUEST_CODE);
        return true;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE) return false;
        if (resultCode != Activity.RESULT_OK) {
            if (Log.isLoggable(Log.ERROR)) Log.e("Result: " + requestCode + ", data: " + data);
            return true;
        }
        if (Log.isLoggable(Log.VERBOSE)) Log.v("Received gallery response");
        try {
            String myName = getParticipantProvider().getParticipant(getLayerClient().getAuthenticatedUserId()).getName();
            Message message = ThreePartImageUtils.newThreePartImageMessage(getActivity(), getLayerClient(), data.getData());
            message.getOptions().pushNotificationMessage(getActivity().getString(R.string.atlas_notification_image, myName));
            send(message);
        } catch (IOException e) {
            if (Log.isLoggable(Log.ERROR)) Log.e(e.getMessage(), e);
        }
        return true;
    }
}
