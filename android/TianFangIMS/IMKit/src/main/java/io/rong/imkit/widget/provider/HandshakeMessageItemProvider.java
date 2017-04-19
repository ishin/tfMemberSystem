package io.rong.imkit.widget.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;

import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.R;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.message.HandshakeMessage;

@ProviderTag(messageContent = HandshakeMessage.class , showPortrait = false, centerInHorizontal = true, hide = true)
public class HandshakeMessageItemProvider extends IContainerItemProvider.MessageProvider<HandshakeMessage> {


    @Override
    public View newView(Context context, ViewGroup group) {
        return null;
    }

    @Override
    public Spannable getContentSummary(HandshakeMessage data) {
        if (data != null && data.getContent() != null)
            return new SpannableString(AndroidEmoji.ensure(data.getContent()));
        return null;
    }

    @Override
    public void onItemClick(View view, int position, HandshakeMessage content, UIMessage message) {

    }

    @Override
    public void onItemLongClick(View view, int position, HandshakeMessage content, final UIMessage message) {
        String[] items;

        items = new String[] {view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete)};

        OptionsPopupDialog.newInstance(view.getContext(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
            @Override
            public void onOptionsItemClicked(int which) {
                if (which == 0)
                    RongIM.getInstance().deleteMessages(new int[] {message.getMessageId()}, null);

            }
        }).show();
    }

    @Override
    public void bindView(View v, int position, HandshakeMessage content, UIMessage data) {

    }
}
