package io.rong.imkit.widget.provider;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.R;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongKitIntent;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.message.PublicServiceMultiRichContentMessage;
import io.rong.message.RichContentItem;


/**
 * Created by weiqinxiao on 15/4/13.
 */
@ProviderTag(messageContent = PublicServiceMultiRichContentMessage.class, showPortrait = false, centerInHorizontal = true, showSummaryWithName = false)
public class PublicServiceMultiRichContentMessageProvider extends IContainerItemProvider.MessageProvider<PublicServiceMultiRichContentMessage> {

    @Override
    public void bindView(final View v, int position, PublicServiceMultiRichContentMessage content, UIMessage message) {
        ViewHolder vh = (ViewHolder) v.getTag();
        final ArrayList<RichContentItem> msgList = content.getMessages();

        if (msgList.size() > 0) {
            vh.tv.setText(msgList.get(0).getTitle());
            vh.iv.setResource(msgList.get(0).getImageUrl(), 0);
        }

        int height = 0;
        ViewGroup.LayoutParams params = v.getLayoutParams();

        PublicAccountMsgAdapter mAdapter = new PublicAccountMsgAdapter(v.getContext(), msgList);
        vh.lv.setAdapter(mAdapter);

        vh.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                RichContentItem item = mAdapter.getItem(position);

                RichContentItem item = msgList.get(position + 1);
                String url = item.getUrl();
                String action = RongKitIntent.RONG_INTENT_ACTION_WEBVIEW;
                Intent intent = new Intent(action);
                intent.setPackage(v.getContext().getPackageName());
                intent.putExtra("url", url);
                v.getContext().startActivity(intent);
            }
        });

        height = getListViewHeight(vh.lv) + vh.height;
        params.height = height;

        v.setLayoutParams(params);
        v.requestLayout();
    }

    private int getListViewHeight(ListView list) {
        int totalHeight = 0;
        View item = null;

        ListAdapter adapter = list.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            item = adapter.getView(i, null, list);
            item.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                         View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight = totalHeight + item.getMeasuredHeight() + 2;
        }

        return totalHeight;
    }

    @Override
    public Spannable getContentSummary(PublicServiceMultiRichContentMessage data) {
        List<RichContentItem> list = data.getMessages();
        if (list.size() > 0)
            return new SpannableString(data.getMessages().get(0).getTitle());
        else
            return null;
    }

    @Override
    public void onItemClick(View view, int position, PublicServiceMultiRichContentMessage content, UIMessage message) {

        if (content.getMessages().size() == 0)
            return;

        /*String url = content.getMessages().get(0).getUrl();
        Intent intent = new Intent(mContext, RongWebviewActivity.class);
        intent.putExtra("url", url);
        mContext.startActivity(intent);*/

        String url = content.getMessages().get(0).getUrl();
        String action = RongKitIntent.RONG_INTENT_ACTION_WEBVIEW;
        Context context = view.getContext();
        Intent intent = new Intent(action);
        intent.setPackage(context.getPackageName());
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position, PublicServiceMultiRichContentMessage content, final UIMessage message) {
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
        int height;
        TextView tv;
        AsyncImageView iv;
        ListView lv;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        ViewHolder holder = new ViewHolder();

        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_public_service_multi_rich_content_message, null);
        holder.lv = (ListView) view.findViewById(R.id.rc_list);
        holder.iv = (AsyncImageView) view.findViewById(R.id.rc_img);
        holder.tv = (TextView) view.findViewById(R.id.rc_txt);
        view.measure(0, 0);
        holder.height = view.getMeasuredHeight();
        view.setTag(holder);

        return view;
    }

    private static class PublicAccountMsgAdapter extends android.widget.BaseAdapter {

        LayoutInflater inflater;
        ArrayList<RichContentItem> itemList;
        int itemCount;

        public PublicAccountMsgAdapter(Context context, ArrayList<RichContentItem> msgList) {
            inflater = LayoutInflater.from(context);
            itemList = new ArrayList<RichContentItem>();
            itemList.addAll(msgList);
            itemCount = msgList.size() - 1;
        }

        @Override
        public int getCount() {
            return itemCount;
        }

        @Override
        public RichContentItem getItem(int position) {
            if (itemList.size() == 0)
                return null;

            return itemList.get(position + 1);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = inflater.inflate(R.layout.rc_item_public_service_message, null);

            AsyncImageView iv = (AsyncImageView) convertView.findViewById(R.id.rc_img);
            TextView tv = (TextView) convertView.findViewById(R.id.rc_txt);

            if (itemList.size() == 0)
                return null;

            String title = itemList.get(position + 1).getTitle();
            if (title != null)
                tv.setText(title);

            iv.setResource(itemList.get(position + 1).getImageUrl(), 0);
            return convertView;
        }
    }
}
