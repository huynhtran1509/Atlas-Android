package com.layer.atlas.messagetypes.generic;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.layer.atlas.R;
import com.layer.atlas.messagetypes.AtlasCellFactory;
import com.layer.atlas.provider.ParticipantProvider;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;

import rx.Single;

/**
 * Mime handles all MIME Types by simply displaying all MessagePart MIME Types as text.
 */
public class GenericCellFactory extends AtlasCellFactory<GenericCellFactory.CellHolder, GenericCellFactory.ParsedContent> {
    public GenericCellFactory() {
        super(256 * 1024);
    }

    @Override
    public String getMessagePreview(Context context, Message message) {
        StringBuilder b = new StringBuilder();
        boolean isFirst = true;
        b.append("[");
        for (MessagePart part : message.getMessageParts()) {
            if (!isFirst) b.append(", ");
            isFirst = false;
            b.append(part.getSize()).append("-byte ").append(part.getMimeType());
        }
        b.append("]");
        return b.toString();
    }

    @Override
    public boolean isBindable(Message message) {
        return true;
    }

    @Override
    public CellHolder createCellHolder(ViewGroup cellView, boolean isMe, LayoutInflater layoutInflater) {
        Context context = cellView.getContext();

        View v = layoutInflater.inflate(R.layout.atlas_message_item_cell_text, cellView, true);
        v.setBackgroundResource(isMe ? R.drawable.atlas_message_item_cell_me : R.drawable.atlas_message_item_cell_them);

        TextView t = (TextView) v.findViewById(R.id.cell_text);
        t.setTextColor(context.getResources().getColor(isMe ? R.color.atlas_cell_me_text : R.color.atlas_cell_them_text));
        return new CellHolder(v);
    }

    @Override
    public Single<ParsedContent> parseContent(LayerClient layerClient, ParticipantProvider participantProvider, Message message) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (MessagePart part : message.getMessageParts()) {
            if (i != 0) builder.append("\n");
            builder.append("[").append(i).append("]: ")
                    .append(part.getSize()).append("-byte `")
                    .append(part.getMimeType()).append("`");
            i++;
        }
        ParsedContent parsedContent = new ParsedContent(builder.toString());
        return Single.just(parsedContent);
    }

    @Override
    public void bindCellHolder(CellHolder cellHolder, @Nullable ParsedContent string, Message message, CellHolderSpecs specs) {
        cellHolder.mTextView.setText(string == null ? null : string.toString());
    }

    public class CellHolder extends AtlasCellFactory.CellHolder {
        TextView mTextView;

        public CellHolder(View view) {
            mTextView = (TextView) view.findViewById(R.id.cell_text);
        }
    }

    public static class ParsedContent implements AtlasCellFactory.ParsedContent {
        private final String mString;
        private final int mSize;

        public ParsedContent(String string) {
            mString = string;
            mSize = mString.getBytes().length;
        }

        public String getString() {
            return mString;
        }

        @Override
        public int sizeOf() {
            return mSize;
        }

        @Override
        public String toString() {
            return mString;
        }
    }
}
