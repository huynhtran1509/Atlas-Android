package com.layer.atlas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.layer.sdk.messaging.Message;

/**
 * CellFactories manage one or more types ot Messages for display within an AtlasMessagesAdapter.
 */
public interface AtlasCellFactory<T extends AtlasCellFactory.CellHolder> {
    /**
     * Returns `true` if this CellFactory can create and bind a CellHolder for the given Message, or
     * `false` otherwise.
     *
     * @param message Message to analyze for manageability.
     * @return `true` if this CellFactory manages the given Message, or `false` otherwise.
     */
    boolean isBindable(Message message);

    /**
     * This method must perform two actions.  First, any required View hierarchy for rendering this
     * CellFactory's Messages must be added to the provided `cellView` - either by inflating a
     * layout (e.g. <merge>...</merge>), or by adding Views programmatically - and second, creating
     * and returning a CellHolder.  The CellHolder gets passed into bindCellHolder() when rendering
     * a Message and should contain all View references necessary for rendering the Message there.
     *
     * @param cellView       ViewGroup to add necessary Message Views to.
     * @param layoutInflater Convenience Inflater for inflating layouts.
     * @return CellHolder with all View references required for binding Messages to Views.
     */
    T createCellHolder(ViewGroup cellView, LayoutInflater layoutInflater);

    /**
     * Renders a Message by applying data to the provided CellHolder.  The CellHolder was previously
     * created in createCellHolder().
     *
     * @param cellHolder CellHolder to bind with Message data.
     * @param message    Message to bind to the CellHolder.
     * @param isMe       `true` if this Message was sent by the authenticated user, or `false`.
     * @param position   Position of this Message within its AtlasMessagesAdapter items.
     */
    void bindCellHolder(T cellHolder, Message message, boolean isMe, int position);

    /**
     * CellHolders maintain a reference to their Message, and allow the capture of user interactions
     * with their messages (e.g. clicks).  CellHolders can be extended to act as View caches, where
     * createCellHolder() might populate a CellHolder with references to Views for use in future
     * calls to bindCellHolder().
     */
    abstract class CellHolder implements View.OnClickListener, View.OnLongClickListener {
        private OnClickListener mClickListener;
        private Message mMessage;

        public CellHolder setClickableView(View clickableView) {
            clickableView.setOnClickListener(this);
            clickableView.setOnLongClickListener(this);
            return this;
        }

        public CellHolder setMessage(Message message) {
            mMessage = message;
            return this;
        }

        public Message getMessage() {
            return mMessage;
        }

        public CellHolder setClickListener(OnClickListener clickListener) {
            mClickListener = clickListener;
            return this;
        }

        @Override
        public void onClick(View v) {
            if (mClickListener == null) return;
            mClickListener.onClick(this);
        }

        @Override
        public boolean onLongClick(View v) {
            if (mClickListener == null) return false;
            return mClickListener.onLongClick(this);
        }

        public interface OnClickListener {
            void onClick(CellHolder cellHolder);

            boolean onLongClick(CellHolder cellHolder);
        }
    }
}