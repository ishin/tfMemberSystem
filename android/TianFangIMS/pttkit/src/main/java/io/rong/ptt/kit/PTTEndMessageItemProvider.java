package io.rong.ptt.kit;

import io.rong.imkit.model.ProviderTag;
import io.rong.ptt.message.server.PTTEndMessage;

@ProviderTag(messageContent = PTTEndMessage.class,
        showPortrait = false,
        showProgress = false,
        showWarning = false,
        centerInHorizontal = true,
        showSummaryWithName = false)
public class PTTEndMessageItemProvider extends InfoNotificationMsgItemProvider {

}

