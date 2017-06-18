package io.rong.imkit.widget.provider;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.rong.imkit.R;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongKitIntent;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.message.PublicServiceRichContentMessage;

/**
 * Created by weiqinxiao on 15/4/18.
 */
@ProviderTag(messageContent = PublicServiceRichContentMessage.class, showPortrait = false, centerInHorizontal = true, showSummaryWithName = false)
public class PublicServiceRichContentMessageProvider extends IContainerItemProvider.MessageProvider<PublicServiceRichContentMessage> {
    private int width, height;

    @Override
    public View newView(Context context, ViewGroup group) {
        ViewHolder holder = new ViewHolder();
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_public_service_rich_content_message, null);

        holder.title = (TextView) view.findViewById(R.id.rc_title);
        holder.time = (TextView) view.findViewById(R.id.rc_time);
        holder.description = (TextView) view.findViewById(R.id.rc_content);
        holder.imageView = (AsyncImageView) view.findViewById(R.id.rc_img);

        WindowManager m = (WindowManager)view.getContext().getSystemService(Context.WINDOW_SERVICE);
        int w = m.getDefaultDisplay().getWidth() - 35;
        view.setLayoutParams(new LinearLayout.LayoutParams(w, LinearLayout.LayoutParams.WRAP_CONTENT));
        width = w - 100;
        height = 800;
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View v, int position, PublicServiceRichContentMessage content, UIMessage message) {
        ViewHolder holder = (ViewHolder) v.getTag();

        PublicServiceRichContentMessage msg = (PublicServiceRichContentMessage) message.getContent();

        holder.title.setText(msg.getMessage().getTitle());
        holder.description.setText(msg.getMessage().getDigest());

        int w = width;
        int h = height;

        holder.imageView.setResource(msg.getMessage().getImageUrl(), 0);
        String time = formatDate(message.getReceivedTime(), "MM月dd日 HH:mm");
        holder.time.setText(time);
    }

    private String formatDate(long timeMillis, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(timeMillis));
    }

    @Override
    public Spannable getContentSummary(PublicServiceRichContentMessage data) {
        return new SpannableString(data.getMessage().getTitle());
    }

    @Override
    public void onItemClick(View view, int position, PublicServiceRichContentMessage content, UIMessage message) {

        String url = content.getMessage().getUrl();
        String action = RongKitIntent.RONG_INTENT_ACTION_WEBVIEW;
        Intent intent = new Intent(action);
        intent.setPackage(view.getContext().getPackageName());
        intent.putExtra("url", url);
        view.getContext().startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position, PublicServiceRichContentMessage content, final UIMessage message) {
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

    private static class ViewHolder {
        TextView title;
        AsyncImageView imageView;
        TextView time;
        TextView description;
    }

}
