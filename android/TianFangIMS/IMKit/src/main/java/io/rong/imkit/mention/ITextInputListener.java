package io.rong.imkit.mention;

import android.widget.EditText;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MentionedInfo;


public interface ITextInputListener {
    void onTextEdit(Conversation.ConversationType type, String targetId, int cursorPos, int count, String text);

    MentionedInfo onSendButtonClick();

    void onDeleteClick(Conversation.ConversationType type, String targetId, EditText editText, int cursorPos);
}
