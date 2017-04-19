package io.rong.imkit.tools;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.rong.common.RLog;
import io.rong.imageloader.core.DisplayImageOptions;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imageloader.core.assist.FailReason;
import io.rong.imageloader.core.imageaware.ImageAware;
import io.rong.imageloader.core.imageaware.ImageViewAware;
import io.rong.imageloader.core.listener.ImageLoadingListener;
import io.rong.imageloader.core.listener.ImageLoadingProgressListener;
import io.rong.imkit.R;
import io.rong.imkit.plugin.image.AlbumBitmapCacheHelper;
import io.rong.imkit.plugin.image.HackyViewPager;
import io.rong.imkit.fragment.BaseFragment;
import io.rong.imkit.widget.PicturePopupWindow;
import io.rong.imlib.RongCommonDefine;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;
import io.rong.photoview.PhotoView;
import io.rong.photoview.PhotoViewAttacher;

public class PhotoFragment extends BaseFragment {
    private static final String TAG = "PhotoFragment";
    private static final int IMAGE_MESSAGE_COUNT = 10; //每次获取的图片消息数量。
    private HackyViewPager mViewPager;
    private ImageMessage mCurrentImageMessage;
    private Conversation.ConversationType mConversationType;
    private int mCurrentMessageId;
    private String mTargetId = null;
    private int mCurrentIndex = 0;
    private PhotoDownloadListener mDownloadListener;
    private ImageAware mDownloadingImageAware;
    private ImageAdapter mImageAdapter;
    private boolean isFirstTime = false;
    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            RLog.i(TAG, "onPageSelected. position:" + position);
            mCurrentIndex = position;
            View view = mViewPager.findViewById(position);
            if (view != null)
                mImageAdapter.updatePhotoView(position, view, mDownloadListener);
            if (position == (mImageAdapter.getCount() - 1)) {
                getConversationImageUris(mImageAdapter.getItem(position).getMessageId(), RongCommonDefine.GetMessageDirection.BEHIND);
            } else if (position == 0) {
                getConversationImageUris(mImageAdapter.getItem(position).getMessageId(), RongCommonDefine.GetMessageDirection.FRONT);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rc_fr_photo, container, true);
        mViewPager = (HackyViewPager) view.findViewById(R.id.viewpager);
        return view;
    }

    public void initPhoto(Message currentMessage, final PhotoDownloadListener downloadListener) {
        if (currentMessage == null)
            return;
        mCurrentImageMessage = (ImageMessage) currentMessage.getContent();
        mConversationType = currentMessage.getConversationType();
        mCurrentMessageId = currentMessage.getMessageId();
        mTargetId = currentMessage.getTargetId();
        mDownloadListener = downloadListener;
        if (mCurrentMessageId < 0) {
            RLog.e(TAG, "The value of messageId is wrong!");
            return;
        }

        mImageAdapter = new ImageAdapter();
        isFirstTime = true;
        mViewPager.setOnPageChangeListener(mPageChangeListener);

        getConversationImageUris(mCurrentMessageId, RongCommonDefine.GetMessageDirection.FRONT);  //获取当前点开图片之前的图片消息。
        getConversationImageUris(mCurrentMessageId, RongCommonDefine.GetMessageDirection.BEHIND);
    }

    private void getConversationImageUris(int mesageId, final RongCommonDefine.GetMessageDirection direction) {
        if (mConversationType != null && !TextUtils.isEmpty(mTargetId)) {
            RongIMClient.getInstance().getHistoryMessages(mConversationType, mTargetId, "RC:ImgMsg", mesageId, IMAGE_MESSAGE_COUNT, direction, new RongIMClient.ResultCallback<List<Message>>() {
                @Override
                public void onSuccess(List<Message> messages) {
                    int i;
                    ArrayList<ImageInfo> lists = new ArrayList<>();
                    if (messages != null) {
                        if (direction.equals(RongCommonDefine.GetMessageDirection.FRONT))
                            Collections.reverse(messages);
                        for (i = 0; i < messages.size(); i++) {
                            Message message = messages.get(i);
                            if (message.getContent() instanceof ImageMessage) {
                                ImageMessage imageMessage = (ImageMessage) message.getContent();
                                Uri largeImageUri = imageMessage.getLocalUri() == null ? imageMessage.getRemoteUri() : imageMessage.getLocalUri();

                                if (imageMessage.getThumUri() != null && largeImageUri != null) {
                                    lists.add(new ImageInfo(message.getMessageId(), imageMessage.getThumUri(), largeImageUri));
                                }
                            }
                        }
                    }
                    if (direction.equals(RongCommonDefine.GetMessageDirection.FRONT) && isFirstTime) {
                        lists.add(new ImageInfo(mCurrentMessageId, mCurrentImageMessage.getThumUri(),
                                                mCurrentImageMessage.getLocalUri() == null ? mCurrentImageMessage.getRemoteUri() : mCurrentImageMessage.getLocalUri()));
                        mImageAdapter.addData(lists, direction.equals(RongCommonDefine.GetMessageDirection.FRONT));
                        mViewPager.setAdapter(mImageAdapter);
                        isFirstTime = false;
                        mViewPager.setCurrentItem(lists.size() - 1);
                        mCurrentIndex = lists.size() - 1;
                    } else if (lists.size() > 0) {
                        mImageAdapter.addData(lists, direction.equals(RongCommonDefine.GetMessageDirection.FRONT));
                        mImageAdapter.notifyDataSetChanged();
                        if (direction.equals(RongCommonDefine.GetMessageDirection.FRONT)) {
                            mViewPager.setCurrentItem(lists.size());
                            mCurrentIndex = lists.size();
                        }
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {

                }
            });
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onRestoreUI() {

    }

    public interface PhotoDownloadListener {
        void onDownloaded(Uri file);

        void onDownloadError();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private class ImageAdapter extends PagerAdapter {
        private ArrayList<ImageInfo> mImageList = new ArrayList<>();
        private PicturePopupWindow menuWindow;

        public class ViewHolder {
            ProgressBar progressBar;
            TextView progressText;
            PhotoView photoView;
        }

        private View.OnClickListener onMenuWindowClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.rc_content) {

                }
                menuWindow.dismiss();
            }
        };

        private View newView(final Context context, final ImageInfo imageInfo) {
            View result = LayoutInflater.from(context).inflate(R.layout.rc_fr_image, null);

            ViewHolder holder = new ViewHolder();
            holder.progressBar = (ProgressBar) result.findViewById(R.id.rc_progress);
            holder.progressText = (TextView) result.findViewById(R.id.rc_txt);
            holder.photoView = (PhotoView) result.findViewById(R.id.rc_photoView);
            holder.photoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Uri uri = imageInfo.getLargeImageUri();
                    File file = null;
                    if (uri != null) {
                        if (uri.getScheme().startsWith("http") || uri.getScheme().startsWith("https"))
                            file = ImageLoader.getInstance().getDiskCache().get(uri.toString());
                        else
                            file = new File(uri.getPath());
                    }
                    menuWindow = new PicturePopupWindow(getActivity(), file);
                    menuWindow.showAtLocation(v, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    menuWindow.setOutsideTouchable(false);
                    return false;
                }
            });
            holder.photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    getActivity().finish();
                }

                @Override
                public void onOutsidePhotoTap() {

                }
            });
            result.setTag(holder);

            return result;
        }

        public void addData(ArrayList<ImageInfo> newImages, boolean direction) {
            if (newImages == null || newImages.size() == 0)
                return;
            if (mImageList.size() == 0) {
                mImageList.addAll(newImages);
            } else if (direction && !isFirstTime && !isDuplicate(newImages.get(0).getMessageId())) {
                ArrayList<ImageInfo> temp = new ArrayList<>();
                temp.addAll(mImageList);
                mImageList.clear();
                mImageList.addAll(newImages);
                mImageList.addAll(mImageList.size(), temp);
            } else if (!isFirstTime && !isDuplicate(newImages.get(0).getMessageId())) {
                mImageList.addAll(mImageList.size(), newImages);
            }
        }

        private boolean isDuplicate(int messageId) {
            for (ImageInfo info : mImageList) {
                if (info.getMessageId() == messageId)
                    return true;
            }
            return false;
        }

        public ImageInfo getItem(int index) {
            return mImageList.get(index);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mImageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            RLog.i(TAG, "instantiateItem.position:" + position);

            View imageView = newView(container.getContext(), mImageList.get(position));
            updatePhotoView(position, imageView, mDownloadListener);
            imageView.setId(position);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            RLog.i(TAG, "destroyItem.position:" + position);
            ViewHolder holder = (ViewHolder) container.findViewById(position).getTag();
            holder.photoView.setImageURI(null);
            container.removeView((View) object);
        }

        private void updatePhotoView(int position, View view, final PhotoDownloadListener downloadListener) {
            File file;
            final ViewHolder holder = (ViewHolder) view.getTag();
            Uri originalUri = mImageList.get(position).getLargeImageUri();
            Uri thumbUri = mImageList.get(position).getThumbUri();

            if (originalUri == null || thumbUri == null) {
                RLog.e(TAG, "large uri and thumbnail uri of the image should not be null.");
                return;
            }

            if (originalUri.getScheme().startsWith("http") ||
                    originalUri.getScheme().startsWith("https"))
                file = ImageLoader.getInstance().getDiskCache().get(originalUri.toString());
            else
                file = new File(originalUri.getPath());

            if (file != null && file.exists()) {
                if (mDownloadListener != null)
                    mDownloadListener.onDownloaded(originalUri);
                AlbumBitmapCacheHelper.getInstance().addPathToShowlist(file.getAbsolutePath());
                Bitmap bitmap = AlbumBitmapCacheHelper.getInstance().getBitmap(file.getAbsolutePath(), 0, 0, new AlbumBitmapCacheHelper.ILoadImageCallback() {
                    @Override
                    public void onLoadImageCallBack(Bitmap bitmap, String p, Object... objects) {
                        if (bitmap == null) {
                            return;
                        }
                        holder.photoView.setImageBitmap(bitmap);
                    }
                }, position);
                if (bitmap != null) {
                    holder.photoView.setImageBitmap(bitmap);
                } else {
                    Drawable drawable = Drawable.createFromPath(thumbUri.getPath());
                    holder.photoView.setImageDrawable(drawable);
                }
            } else if (position != mCurrentIndex) {
                Drawable drawable = Drawable.createFromPath(thumbUri.getPath());
                holder.photoView.setImageDrawable(drawable);
            } else {
                ImageAware imageAware = new ImageViewAware(holder.photoView);
                if (mDownloadingImageAware != null) {
                    ImageLoader.getInstance().cancelDisplayTask(mDownloadingImageAware);
                }
                ImageLoader.getInstance().displayImage(originalUri.toString(), imageAware, createDisplayImageOptions(thumbUri), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        holder.progressText.setVisibility(View.VISIBLE);
                        holder.progressBar.setVisibility(View.VISIBLE);
                        holder.progressText.setText("0%");
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        if (downloadListener != null)
                            downloadListener.onDownloadError();
                        holder.progressText.setVisibility(View.GONE);
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (downloadListener != null)
                            downloadListener.onDownloaded(Uri.parse(imageUri));
                        holder.progressText.setVisibility(View.GONE);
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        holder.progressText.setVisibility(View.GONE);
                        holder.progressText.setVisibility(View.GONE);
                    }
                },
                new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
                        holder.progressText.setText(current * 100 / total + "%");
                        if (current == total) {
                            holder.progressText.setVisibility(View.GONE);
                            holder.progressBar.setVisibility(View.GONE);
                        } else {
                            holder.progressText.setVisibility(View.VISIBLE);
                            holder.progressBar.setVisibility(View.VISIBLE);
                        }
                    }
                });
                mDownloadingImageAware = imageAware;
            }
        }

        private DisplayImageOptions createDisplayImageOptions(Uri uri) {
            DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
            Drawable drawable = Drawable.createFromPath(uri.getPath());
            return builder.resetViewBeforeLoading(false)
                   .cacheInMemory(false)
                   .cacheOnDisk(true)
                   .bitmapConfig(Bitmap.Config.RGB_565)
                   .showImageForEmptyUri(drawable)
                   .showImageOnFail(drawable)
                   .showImageOnLoading(drawable)
                   .handler(new Handler())
                   .build();
        }
    }

    private class ImageInfo {
        private int messageId;
        private Uri thumbUri;
        private Uri largeImageUri;

        ImageInfo(int messageId, Uri thumbnail, Uri largeImageUri) {
            this.messageId = messageId;
            this.thumbUri = thumbnail;
            this.largeImageUri = largeImageUri;
        }

        public int getMessageId() {
            return messageId;
        }

        public Uri getLargeImageUri() {
            return largeImageUri;
        }

        public Uri getThumbUri() {
            return thumbUri;
        }
    }

}
