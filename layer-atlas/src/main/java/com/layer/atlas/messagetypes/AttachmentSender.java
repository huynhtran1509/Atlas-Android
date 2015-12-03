package com.layer.atlas.messagetypes;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.layer.atlas.util.Log;

import java.lang.ref.WeakReference;

/**
 * AttachmentSenders populate the AtlasMessageComposer attachment menu and handle message sending
 * requests.  AttachmentSenders can interact with the Activity lifecycle to preserve instance state
 * and receive activity results when needed.
 */
public abstract class AttachmentSender extends MessageSender {
    private final String mTitle;
    private final Integer mIcon;

    private final WeakReference<Activity> mActivity;
    private final WeakReference<android.app.Fragment> mFragment;
    private final WeakReference<Fragment> mSupportFragment;

    public AttachmentSender(int titleResId, Integer iconResId, Activity activity) {
        this(activity.getString(titleResId), iconResId, activity);
    }

    public AttachmentSender(String title, Integer iconResId, Activity activity) {
        mTitle = title;
        mIcon = iconResId;
        mActivity = new WeakReference<>(activity);
        mFragment = null;
        mSupportFragment = null;
    }

    public AttachmentSender(int titleResId, Integer iconResId, android.app.Fragment fragment) {
        this(fragment.getString(titleResId), iconResId, fragment);
    }

    public AttachmentSender(String title, Integer iconResId, android.app.Fragment fragment) {
        mTitle = title;
        mIcon = iconResId;
        mActivity = null;
        mFragment = new WeakReference<>(fragment);
        mSupportFragment = null;
    }

    public AttachmentSender(int titleResId, Integer iconResId, Fragment fragment) {
        this(fragment.getString(titleResId), iconResId, fragment);
    }

    public AttachmentSender(String title, Integer iconResId, Fragment fragment) {
        mTitle = title;
        mIcon = iconResId;
        mActivity = null;
        mFragment = null;
        mSupportFragment = new WeakReference<>(fragment);
    }

    /**
     * Begins an attachment sending operation.  This might launch an Intent for selecting from a
     * gallery, taking a camera photo, or simply sending a message of the given type.  If an Intent
     * is generated for a result, consider overriding onActivityResult().
     *
     * @return `true` if a send operation is started, or `false` otherwise.
     * @see #onActivityResult(int, int, Intent)
     */
    public boolean requestSend() {
        if ((mActivity == null || mActivity.get() == null)
                && (mFragment == null || mFragment.get() == null)
                && (mSupportFragment == null || mSupportFragment.get() == null)) {
            if (Log.isLoggable(Log.ERROR)) Log.e("Activity or fragment went out of scope.");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Override to save instance state.
     *
     * @return new saved instance state.
     * @see #onRestoreInstanceState(Parcelable)
     */
    public Parcelable onSaveInstanceState() {
        // Optional override
        return null;
    }

    /**
     * Override if saved instance state is required.
     *
     * @param state State previously created with onSaveInstanceState().
     * @see #onSaveInstanceState()
     */
    public void onRestoreInstanceState(Parcelable state) {
        // Optional override
    }

    /**
     * Override to handle results from onActivityResult.
     *
     * @return true if the result was handled, or false otherwise.
     */
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        // Optional override
        return false;
    }

    /**
     * Returns the title for this AttachmentSender, typically for use in the AtlasMessageComposer
     * attachment menu.
     *
     * @return The title for this AttachmentSender.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the icon resource ID for this AttachmentSender, typically for use in the
     * AtlasMessageComposer attachment menu, or `null` for none.
     *
     * @return The icon resource ID for this AttachmentSender.
     */
    public Integer getIcon() {
        return mIcon;
    }

    /**
     * Starts an activity for result with the associated activity or fragment.
     * @param intent Intent to start.
     * @param requestCode Request code.
     */
    protected void startActivityForResult(Intent intent, int requestCode) {
        if (mActivity != null && mActivity.get() != null) {
            mActivity.get().startActivityForResult(intent, requestCode);
        } else if (mFragment != null && mFragment.get() != null) {
            mFragment.get().startActivityForResult(intent, requestCode);
        } else if (mSupportFragment != null && mSupportFragment.get() != null) {
            mSupportFragment.get().startActivityForResult(intent, requestCode);
        } else {
            if (Log.isLoggable(Log.ERROR)) Log.e("Activity or fragment went out of scope.");
        }
    }

    public Activity getActivity() {
        if (mActivity != null && mActivity.get() != null) {
            return mActivity.get();
        } else if (mFragment != null && mFragment.get() != null) {
            return mFragment.get().getActivity();
        } else if (mSupportFragment != null && mSupportFragment.get() != null) {
            return mSupportFragment.get().getActivity();
        } else {
            if (Log.isLoggable(Log.ERROR)) Log.e("Activity or fragment went out of scope.");
            return null;
        }
    }
}
