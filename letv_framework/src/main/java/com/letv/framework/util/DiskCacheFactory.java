package com.letv.framework.util;

import android.content.Context;

import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;

import java.io.File;

/**
 * Creates an {@link com.bumptech.glide.disklrucache.DiskLruCache} based disk cache in the internal
 * disk cache directory.
 */
public final class DiskCacheFactory extends DiskLruCacheFactory {

  public DiskCacheFactory(Context context, String dir) {
    this(context,dir, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR,
        DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE);
  }

  public DiskCacheFactory(Context context, String dir, int diskCacheSize) {
    this(context, dir, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR, diskCacheSize);
  }

  public DiskCacheFactory(final Context context, final String dir, final String diskCacheName,
                          int diskCacheSize) {
    super(new CacheDirectoryGetter() {
      @Override
      public File getCacheDirectory() {
        File cacheDirectory = new File(dir);
        if(!new StoragePathsManager(context).mkDir(cacheDirectory)) {
          return null;
        }
        if (diskCacheName != null) {
          return new File(cacheDirectory, diskCacheName);
        }
        return cacheDirectory;
      }
    }, diskCacheSize);
  }
}
