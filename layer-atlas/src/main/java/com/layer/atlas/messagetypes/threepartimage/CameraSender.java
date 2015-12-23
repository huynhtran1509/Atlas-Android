package com.layer.atlas.messagetypes.threepartimage;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.layer.atlas.R;
import com.layer.atlas.messagetypes.AttachmentSender;
import com.layer.atlas.util.Log;
import com.layer.sdk.messaging.Message;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class CameraSender extends AttachmentSender {
    public static final int REQUEST_CODE = 47002;

    private final Class<? extends Activity> cameraActivityClass;

    @SuppressWarnings("unused")
    public CameraSender(int titleResId, Integer iconResId, Activity activity, Class<? extends Activity> cameraActivityClass) {
        super(titleResId, iconResId, activity);
        this.cameraActivityClass = cameraActivityClass;
    }

    @SuppressWarnings("unused")
    public CameraSender(String title, Integer iconResId, Activity activity, Class<? extends Activity> cameraActivityClass) {
        super(title, iconResId, activity);
        this.cameraActivityClass = cameraActivityClass;
    }

    @SuppressWarnings("unused")
    public CameraSender(int titleResId, Integer iconResId, android.app.Fragment fragment, Class<? extends Activity> cameraActivityClass) {
        super(titleResId, iconResId, fragment);
        this.cameraActivityClass = cameraActivityClass;
    }

    @SuppressWarnings("unused")
    public CameraSender(String title, Integer iconResId, android.app.Fragment fragment, Class<? extends Activity> cameraActivityClass) {
        super(title, iconResId, fragment);
        this.cameraActivityClass = cameraActivityClass;
    }

    @SuppressWarnings("unused")
    public CameraSender(int titleResId, Integer iconResId, Fragment fragment, Class<? extends Activity> cameraActivityClass) {
        super(titleResId, iconResId, fragment);
        this.cameraActivityClass = cameraActivityClass;
    }

    @SuppressWarnings("unused")
    public CameraSender(String title, Integer iconResId, Fragment fragment, Class<? extends Activity> cameraActivityClass) {
        super(title, iconResId, fragment);
        this.cameraActivityClass = cameraActivityClass;
    }

    @Override
    public boolean requestSend() {
        if (!super.requestSend()) {
            return false;
        }
        if (Log.isLoggable(Log.VERBOSE)) Log.v("Sending camera image");
        Intent cameraIntent = new Intent(getContext(), cameraActivityClass);
        startActivityForResult(cameraIntent, REQUEST_CODE);
        return true;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE) return false;
        if (resultCode != Activity.RESULT_OK) {
            if (Log.isLoggable(Log.ERROR)) Log.e("Result: " + requestCode + ", data: " + data);
            return true;
        }
        if (Log.isLoggable(Log.VERBOSE)) Log.v("Received camera response");
        try {
            String myName = getParticipantProvider().getParticipant(getLayerClient().getAuthenticatedUserId()).getName();
            Message message = ThreePartImageUtils.newThreePartImageMessage(getActivity(), getLayerClient(), (File) null); // FIXME: 05/01/16 this shouldn't be null, but we are not using it
            message.getOptions().pushNotificationMessage(getActivity().getString(R.string.atlas_notification_image, myName));
            send(message);
        } catch (IOException e) {
            if (Log.isLoggable(Log.ERROR)) Log.e(e.getMessage(), e);
        }
        return true;
    }
}
