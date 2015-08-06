package com.layer.atlas.cells;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.layer.atlas.Atlas;
import com.layer.atlas.AtlasMessagesList;
import com.layer.atlas.R;
import com.layer.sdk.messaging.MessagePart;

public class TextCell extends Cell {

    protected String text;
    AtlasMessagesList messagesList;

    public TextCell(MessagePart messagePart, AtlasMessagesList messagesList) {
        super(messagePart);
        this.messagesList = messagesList;
    }

    public TextCell(MessagePart messagePart, String text, AtlasMessagesList messagesList) {
        super(messagePart);
        this.text = text;
    }

    public View onBind(ViewGroup cellContainer) {
        MessagePart part = messagePart;
        Cell cell = this;

        View cellText = Atlas.Tools.findChildById(cellContainer, R.id.atlas_view_messages_cell_text);
        if (cellText == null) {
            cellText = LayoutInflater.from(cellContainer.getContext()).inflate(R.layout.atlas_view_messages_cell_text, cellContainer, false);
        }

        if (text == null) {
            if (Atlas.MIME_TYPE_TEXT.equals(part.getMimeType())) {
                text = new String(part.getData());
            } else {
                text = "attach, type: " + part.getMimeType() + ", size: " + part.getSize();
            }
        }

        boolean myMessage = messagesList.client.getAuthenticatedUserId().equals(cell.messagePart.getMessage().getSender().getUserId());
        TextView textMy = (TextView) cellText.findViewById(R.id.atlas_view_messages_convert_text);
        TextView textOther = (TextView) cellText.findViewById(R.id.atlas_view_messages_convert_text_counterparty);
        if (myMessage) {
            textMy.setVisibility(View.VISIBLE);
            textMy.setText(text);
            textOther.setVisibility(View.GONE);

            textMy.setBackgroundResource(R.drawable.atlas_shape_rounded16_blue);

            if (AtlasMessagesList.CLUSTERED_BUBBLES) {
                if (cell.clusterHeadItemId == cell.clusterItemId && !cell.clusterTail) {
                    textMy.setBackgroundResource(R.drawable.atlas_shape_rounded16_blue_no_bottom_right);
                } else if (cell.clusterTail && cell.clusterHeadItemId != cell.clusterItemId) {
                    textMy.setBackgroundResource(R.drawable.atlas_shape_rounded16_blue_no_top_right);
                } else if (cell.clusterHeadItemId != cell.clusterItemId && !cell.clusterTail) {
                    textMy.setBackgroundResource(R.drawable.atlas_shape_rounded16_blue_no_right);
                }
            }
            ((GradientDrawable) textMy.getBackground()).setColor(messagesList.myBubbleColor);
            textMy.setTextColor(messagesList.myTextColor);
            //textMy.setTextSize(TypedValue.COMPLEX_UNIT_DIP, myTextSize);
            textMy.setTypeface(messagesList.myTextTypeface, messagesList.myTextStyle);
        } else {
            textOther.setVisibility(View.VISIBLE);
            textOther.setText(text);
            textMy.setVisibility(View.GONE);

            textOther.setBackgroundResource(R.drawable.atlas_shape_rounded16_gray);
            if (AtlasMessagesList.CLUSTERED_BUBBLES) {
                if (cell.clusterHeadItemId == cell.clusterItemId && !cell.clusterTail) {
                    textOther.setBackgroundResource(R.drawable.atlas_shape_rounded16_gray_no_bottom_left);
                } else if (cell.clusterTail && cell.clusterHeadItemId != cell.clusterItemId) {
                    textOther.setBackgroundResource(R.drawable.atlas_shape_rounded16_gray_no_top_left);
                } else if (cell.clusterHeadItemId != cell.clusterItemId && !cell.clusterTail) {
                    textOther.setBackgroundResource(R.drawable.atlas_shape_rounded16_gray_no_left);
                }
            }
            ((GradientDrawable) textOther.getBackground()).setColor(messagesList.otherBubbleColor);
            textOther.setTextColor(messagesList.otherTextColor);
            //textOther.setTextSize(TypedValue.COMPLEX_UNIT_DIP, otherTextSize);
            textOther.setTypeface(messagesList.otherTextTypeface, messagesList.otherTextStyle);
        }
        return cellText;
    }
}