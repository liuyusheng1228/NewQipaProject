package com.qipa.qipaimbase.utils.image;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.qipa.qipaimbase.ImBaseBridge;

public class GlideIImageLoader implements IImageLoader {
    private InternalCacheDiskCacheFactory diskLruCacheFactory = new InternalCacheDiskCacheFactory(
            ImBaseBridge.Companion.getInstance().getApplication());

    public GlideIImageLoader() {
        Glide.init(ImBaseBridge.Companion.getInstance().getApplication(), new GlideBuilder().setDiskCache(diskLruCacheFactory));
    }
    @Override
    public void loadImage(Context context, String url, int placeHolderResId, ImageView imageView) {
        Glide.with(context).load(url).placeholder(placeHolderResId).into(imageView);
    }

    @Override
    public void loadResImage(Context context, int resid, ImageView imageView) {
        Glide.with(context).load(resid).into(imageView);
    }

}
