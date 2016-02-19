package com.letv.framework.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.ScrollView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * bitmap相关工具类
 * 
 * @author xiaruri
 * 
 */
public class BitmapUtil {

    /**
     * 裁剪圆角矩形
     * @param bitmap
     * @param pixels
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        if (bitmap == null) {
            return null;
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
	 * 裁剪成圆图片
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        final int roundPx = bitmap.getWidth() / 2;
        return getRoundedCornerBitmap(bitmap, roundPx);
    }

    /**
	 * 压缩bitmap，如果currentSize与targetSize一样，则返回原图
	 * 
	 * @param bitmap
	 *            压缩的bitmap
	 * @param currentSize
	 *            压缩前尺寸，格式为width*height
	 * @param targetSize
	 *            压缩目标尺寸，格式为width*height
	 * @param isRecycle
	 *            是否回收
	 * @return 压缩后的图片Bitmap，可能为null
	 */
	public static Bitmap compressBitmap(Bitmap bitmap, String currentSize,
			String targetSize, boolean isRecycle) {
		try {
			if (targetSize.equals(currentSize)) {
				return bitmap;
			}
			String[] arr = targetSize.split("\\*");
			if (arr == null || arr.length < 2) {
				return null;
			}
			int dstWidth = Integer.parseInt(arr[0]);
			int dstHeight = Integer.parseInt(arr[1]);
			Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, dstWidth,
					dstHeight, false);
			return newBitmap;
		} catch (OutOfMemoryError e) {
			return null;
		} finally {
			if (!targetSize.equals(currentSize) && !bitmap.isRecycled()
					&& isRecycle) {
				bitmap.recycle();
				bitmap = null;
			}
		}
	}

	/**
	 * 根据路径获取图片角度
	 * 
	 * @param path
	 *            图片路径
	 * @return 图片角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;

		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return degree;
	}

	/**
	 * 旋转图片
	 * 
	 * @param angle
	 *            旋转角度
	 * @param bitmap
	 *            图片
	 * @return
	 */
	public static Bitmap rotaingImage(int angle, Bitmap bitmap) {
		Bitmap resizedBitmap = null;
		if (bitmap != null) {
			Matrix matrix = new Matrix();
			matrix.postRotate(angle);
			resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
					bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		}
		return resizedBitmap;
	}

	/**
	 * 获取scrollView的截图
	 * 
	 * @param scrollView
	 * @return
	 */
	public static Bitmap getBitmapByScrollview(ScrollView scrollView) {
		Bitmap bitmap = null;

        try {
            if (scrollView != null) {
                int height = 0;
                // 获取实际高度
                for (int i = 0; i < scrollView.getChildCount(); i++) {
                    height += scrollView.getChildAt(i).getHeight();
                }

                if (scrollView.getWidth() > 0 && height > 0) {
                    bitmap = Bitmap.createBitmap(scrollView.getWidth(), height,
                            Config.RGB_565);
                    Canvas canvas = new Canvas(bitmap);
                    scrollView.draw(canvas);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        } catch(OutOfMemoryError oom){
            oom.printStackTrace();
        }

		return bitmap;
	}

	/**
	 * 获取WebView的截图
	 * 
	 * @param webView
	 * @return
	 */
	public static Bitmap getBitmapByWebView(WebView webView) {
		Bitmap bitmap = null;

        try {
            if (webView != null) {
                Picture snapShot = webView.capturePicture();
                if (snapShot.getWidth() > 0 && snapShot.getHeight() > 0) {
                    bitmap = Bitmap.createBitmap(snapShot.getWidth(),
                            snapShot.getHeight(), Config.RGB_565);
                    Canvas canvas = new Canvas(bitmap);
                    snapShot.draw(canvas);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        } catch(OutOfMemoryError oom){
            oom.printStackTrace();
        }

        return bitmap;
	}

	/**
	 * 根据路径获取图片角度
	 * 
	 * @param path
	 *            图片路径
	 * @return 图片角度
	 */
	public static int readBitmapDegree(String path) {
		int degree = 0;

		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return degree;
	}

	/**
	 * 旋转图片
	 * 
	 * @param angle
	 *            旋转角度
	 * @param bitmap
	 *            图片
	 * @return 旋转后的bitmap
	 */
	public static Bitmap rotaingBitmap(int angle, Bitmap bitmap) {
		Bitmap resizedBitmap = null;
		if (bitmap != null) {
			Matrix matrix = new Matrix();
			matrix.postRotate(angle);
			resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
					bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		}
		return resizedBitmap;
	}

	/**
	 * 根据图片路径获取对应的缩略图
	 * 
	 * @param path
	 *            图片路径
	 * @return 缩放后的bitmap
	 */
	public static Bitmap getBitmapByPathAndScale(String path, int scale) {
		Bitmap bitmap = null;
		if (!TextUtils.isEmpty(path)) {
			Options options = new Options();
			options.inSampleSize = scale;
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(path, options);
		}

		return bitmap;
	}

	/**
	 * 判断bitmap是否可用
	 * 
	 * @param bitmap
	 * @return
	 */
	public static boolean isAvailable(Bitmap bitmap) {
		return bitmap != null && !bitmap.isRecycled();
	}

	/**
	 * 回收Bitmap
	 * 
	 * @param bitmap
	 */
	public static void recycle(Bitmap bitmap) {
		if (isAvailable(bitmap)) {
			try {
				bitmap.recycle();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 解析图片宽高
	 * 
	 * @param context
	 * @param resid
	 *            图片资源id
	 * @return int[width, height]，可能为null
	 */
	public static int[] getDecodeBounds(Context context, int resid) {
		if (context != null) {
			try {
				Options options = new Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeResource(context.getResources(), resid,
						options);
				return new int[] { options.outWidth, options.outHeight };
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 对Bitmap进行缩放
	 * 
	 * @param oldBitmap
	 *            源图片
	 * @param width
	 *            缩放后的宽
	 * @param height
	 *            缩放后的高
	 * @return 缩放后的bitmap，可能为空
	 */
	public static Bitmap zoomBitmap(Bitmap oldBitmap, int width, int height) {
		if (oldBitmap != null) {
			Matrix matrix = new Matrix();
			float scaleWidth = ((float) width / oldBitmap.getWidth());
			float scaleHeight = ((float) height / oldBitmap.getHeight());
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap newbmp = Bitmap.createBitmap(oldBitmap, 0, 0,
					oldBitmap.getWidth(), oldBitmap.getHeight(), matrix, true);

			return newbmp;
		}
		return null;
	}

	/**
	 * 通过图片file对图片进行缩放
	 * 
	 * @param size
	 *            缩放比例
	 * @param picfile
	 *            图片file
	 * @throws IOException
	 */
	public static void revitionImageSize(int size, File picfile)
			throws IOException {
		FileInputStream input = new FileInputStream(picfile);
		final Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(input, null, opts);
		input.close();
		int rate = 0;
		for (int i = 0;; i++) {
			if ((opts.outWidth >> i <= size) && (opts.outHeight >> i <= size)) {
				rate = i;
				break;
			}
		}
		input = new FileInputStream(picfile);
		opts.inSampleSize = (int) Math.pow(2, rate);
		opts.inJustDecodeBounds = false;
		Bitmap temp = null;
		try {
			temp = BitmapFactory.decodeStream(input, null, opts);
		} catch (OutOfMemoryError e) {
			opts.inSampleSize *= 2;
			temp = BitmapFactory.decodeStream(input, null, opts);
		}
		if (input != null) {
			input.close();
		}
		if (temp == null) {
			throw new IOException("Bitmap decode error!");
		}

		final FileOutputStream output = new FileOutputStream(picfile);
		if (opts != null && opts.outMimeType != null
				&& opts.outMimeType.contains("png")) {
			temp.compress(Bitmap.CompressFormat.PNG, 100, output);
		} else {
			temp.compress(Bitmap.CompressFormat.JPEG, 75, output);
		}
		if (output != null) {
			output.close();
		}
		temp.recycle();
	}

	/**
	 * 
	 * Description: 加载本地图片(减少内存占用)
	 * 
	 * @param context
	 * @param id
	 *            图片资源Id
	 * @return
	 */
	public static BitmapDrawable LoadBackgroundResource(Context context, int id) {
		Options opt = new Options();
		opt.inPreferredConfig = Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;

		InputStream is = context.getResources().openRawResource(id);

		Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);

		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new BitmapDrawable(context.getResources(), bitmap);
	}

	/**
	 * 
	 * Description: 保存图片到指定文件下
	 * @Version1.0 2015-1-19 上午10:09:37 by 王哲（wangzhejs2@dangdang.com）创建
	 * @param bitmap 图片
	 * @param file   保存的文件
	 * @return
	 */
	public static boolean saveBitamp(Bitmap bitmap, File file) {
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap != null && bitmap.isRecycled()) {
				return false;
			}
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)) {
				out.flush();
				out.close();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * Description: 获取文件
	 * @Version1.0 2015-1-19 上午10:08:36 by 王哲（wangzhejs2@dangdang.com）创建
	 * @param file  图片所在文件
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Bitmap getBtimap(File file) throws FileNotFoundException {

		if (file == null)
			return null;
		Options o = new Options();
		o.inJustDecodeBounds = true;
		Bitmap tmp = BitmapFactory.decodeFile(file.getAbsolutePath(), o);
		o.inJustDecodeBounds = false;

		int rate = (int) (o.outHeight / (float) o.outWidth);
		if (rate <= 0) {
			rate = 1;
		}
		o.inSampleSize = rate;
		o.inPurgeable = true;
		o.inInputShareable = true;

		tmp = BitmapFactory.decodeFile(file.getAbsolutePath(), o);

		return tmp;
	}

	/**
	 * 
	 * Description: 删除sd卡文件
	 * @Version1.0 2015-1-19 上午10:07:40 by 王哲（wangzhejs2@dangdang.com）创建
	 * @param file  要删除的文件
	 */
	public static void deleteFile(File file) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			if (file.exists()) {
				if (file.isFile()) {
					file.delete();
				} else if (file.isDirectory()) {
					File files[] = file.listFiles();
					for (int i = 0; i < files.length; i++) {
						deleteFile(files[i]);
					}
				}
				file.delete();
			}
		}
	}
}
