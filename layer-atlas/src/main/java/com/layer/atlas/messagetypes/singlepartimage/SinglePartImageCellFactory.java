package com.layer.atlas.messagetypes.singlepartimage;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.layer.atlas.R;
import com.layer.atlas.messagetypes.AtlasCellFactory;
import com.layer.atlas.provider.ParticipantProvider;
import com.layer.atlas.util.imagepopup.AtlasImagePopupActivity;
import com.layer.atlas.util.imagepopup.MessagePartRegionDecoder;
import com.layer.atlas.util.picasso.transformations.RoundedTransform;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.lang.ref.WeakReference;

/**
 * BasicImage handles non-ThreePartImage images.  It relies on the ThreePartImage RequestHandler and does not handle image rotation.
 */
public class SinglePartImageCellFactory extends AtlasCellFactory<SinglePartImageCellFactory.CellHolder, SinglePartImageCellFactory.PartId> implements View.OnClickListener {
    protected static final String PICASSO_TAG = SinglePartImageCellFactory.class.getSimpleName();
    protected static final int PLACEHOLDER = com.layer.atlas.R.drawable.atlas_message_item_cell_placeholder;

    private final WeakReference<Activity> mActivity;
    private final LayerClient mLayerClient;
    private final Picasso mPicasso;
    private final Transformation mTransform;

    public SinglePartImageCellFactory(Activity activity, LayerClient layerClient, Picasso picasso) {
        super(256 * 1024);
        mActivity = new WeakReference<>(activity);
        mLayerClient = layerClient;
        mPicasso = picasso;
        float radius = activity.getResources().getDimension(com.layer.atlas.R.dimen.atlas_message_item_cell_radius);
        mTransform = new RoundedTransform(radius);
    }

    @Override
    public String getMessagePreview(Context context, Message message) {
        return context.getString(R.string.atlas_message_preview_image);
    }

    protected boolean isMimeType(String mimeType) {
        return mimeType.startsWith("image/");
    }

    @Override
    public boolean isBindable(Message message) {
        for (MessagePart part : message.getMessageParts()) {
            if (isMimeType(part.getMimeType())) return true;
        }
        return false;
    }

    @Override
    public CellHolder createCellHolder(ViewGroup cellView, boolean isMe, LayoutInflater layoutInflater) {
        return new CellHolder(layoutInflater.inflate(R.layout.atlas_message_item_cell_image, cellView, true));
    }

    @Override
    public void bindCellHolder(final CellHolder cellHolder, PartId index, Message message, CellHolderSpecs specs) {
        cellHolder.mImageView.setTag(index);
        cellHolder.mImageView.setOnClickListener(this);
        cellHolder.mProgressBar.show();
        mPicasso.load(index.mId).tag(PICASSO_TAG).placeholder(PLACEHOLDER)
                .centerInside().resize(specs.maxWidth, specs.maxHeight).onlyScaleDown()
                .transform(mTransform).into(cellHolder.mImageView, new Callback() {
            @Override
            public void onSuccess() {
                cellHolder.mProgressBar.hide();
            }

            @Override
            public void onError() {
                cellHolder.mProgressBar.hide();
            }
        });
    }

    @Override
    public void onClick(View v) {
        AtlasImagePopupActivity.init(mLayerClient, getMessagePartContent());
        Activity activity = mActivity.get();
        if (activity == null) return;
        Intent intent = new Intent(activity, AtlasImagePopupActivity.class);
        intent.putExtra("fullId", ((PartId) v.getTag()).mId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity, v, "image").toBundle());
        } else {
            activity.startActivity(intent);
        }
    }

    public MessagePartRegionDecoder.MessagePartContent getMessagePartContent() {
        return null;
    }

    @Override
    public void onScrollStateChanged(int newState) {
        switch (newState) {
            case RecyclerView.SCROLL_STATE_DRAGGING:
                mPicasso.pauseTag(PICASSO_TAG);
                break;
            case RecyclerView.SCROLL_STATE_IDLE:
            case RecyclerView.SCROLL_STATE_SETTLING:
                mPicasso.resumeTag(PICASSO_TAG);
                break;
        }
    }

    @Override
    public PartId parseContent(LayerClient layerClient, ParticipantProvider participantProvider, Message message) {
        for (MessagePart part : message.getMessageParts()) {
            if (isMimeType(part.getMimeType())) return new PartId(part.getId());
        }
        return null;
    }

    public Picasso getPicasso() {
        return mPicasso;
    }

    public Transformation getTransform() {
        return mTransform;
    }

    //==============================================================================================
    // Inner classes
    //==============================================================================================

    public static class CellHolder extends AtlasCellFactory.CellHolder {
        public ImageView mImageView;
        public ContentLoadingProgressBar mProgressBar;

        public CellHolder(View view) {
            mImageView = (ImageView) view.findViewById(R.id.cell_image);
            mProgressBar = (ContentLoadingProgressBar) view.findViewById(R.id.cell_progress);
        }
    }

    public static class PartId implements AtlasCellFactory.ParsedContent {
        public final Uri mId;

        public PartId(Uri id) {
            mId = id;
        }

        @Override
        public int sizeOf() {
            return mId.toString().getBytes().length;
        }
    }
}
