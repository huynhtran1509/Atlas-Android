package com.layer.atlas.messagetypes.threepartimage;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
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

    private final AtomicReference<String> mPhotoFilePath = new AtomicReference<>(null);

    @SuppressWarnings("unused")
    public CameraSender(int titleResId, Integer iconResId, Activity activity) {
        super(titleResId, iconResId, activity);
    }

    @SuppressWarnings("unused")
    public CameraSender(String title, Integer iconResId, Activity activity) {
        super(title, iconResId, activity);
    }

    @SuppressWarnings("unused")
    public CameraSender(int titleResId, Integer iconResId, android.app.Fragment fragment) {
        super(titleResId, iconResId, fragment);
    }

    @SuppressWarnings("unused")
    public CameraSender(String title, Integer iconResId, android.app.Fragment fragment) {
        super(title, iconResId, fragment);
    }

    @SuppressWarnings("unused")
    public CameraSender(int titleResId, Integer iconResId, Fragment fragment) {
        super(titleResId, iconResId, fragment);
    }

    @SuppressWarnings("unused")
    public CameraSender(String title, Integer iconResId, Fragment fragment) {
        super(title, iconResId, fragment);
    }

    @Override
    public boolean requestSend() {
        if (!super.requestSend()) {
            return false;
        }
        if (Log.isLoggable(Log.VERBOSE)) Log.v("Sending camera image");
        String fileName = "cameraOutput" + System.currentTimeMillis() + ".jpg";
        File file = new File(getActivity().getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES), fileName);
        mPhotoFilePath.set(file.getAbsolutePath());
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final Uri outputUri = Uri.fromFile(file);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
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
            Message message = ThreePartImageUtils.newThreePartImageMessage(getActivity(), getLayerClient(), new File(mPhotoFilePath.get()));
            message.getOptions().pushNotificationMessage(getActivity().getString(R.string.atlas_notification_image, myName));
            send(message);
        } catch (IOException e) {
            if (Log.isLoggable(Log.ERROR)) Log.e(e.getMessage(), e);
        }
        return true;
    }

    /**
     * Saves photo file path during e.g. screen rotation
     */
    @Override
    public Parcelable onSaveInstanceState() {
        String path = mPhotoFilePath.get();
        if (path == null) return null;
        Bundle bundle = new Bundle();
        bundle.putString("photoFilePath", path);
        return bundle;
    }

    /**
     * Restores photo file path during e.g. screen rotation
     */
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state == null) return;
        String path = ((Bundle) state).getString("photoFilePath");
        mPhotoFilePath.set(path);
    }
}
