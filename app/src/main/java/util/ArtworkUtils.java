package util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import winter.zxb.smilesb101.winterMusic.R;

/**
 * Created by SmileSB101 on 2016/10/30 0030.
 */

public class ArtworkUtils{

	private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
	private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();

	public static Bitmap getArtwork(Context context,String title,long song_id,long album_id,
									boolean allowdefault){
		Log.i("获取专辑图片","getArtwork: "+song_id);
		if(album_id < 0) {
			if(song_id >= 0) {
				Bitmap bm = getArtworkFromFile(context,song_id,- 1);
				if(bm != null) {
					return bm;
				}
			}
			if(allowdefault) {
				return getDefaultArtwork(context);
			}
			return null;
		}
		ContentResolver res = context.getContentResolver();
		Uri uri = ContentUris.withAppendedId(sArtworkUri,album_id);
		if(uri != null) {
			InputStream in = null;
			try {
				in = res.openInputStream(uri);
				Bitmap bmp = BitmapFactory.decodeStream(in,null,sBitmapOptions);
				if(bmp == null) {
					bmp = getDefaultArtwork(context);
				}
				return bmp;
			} catch(FileNotFoundException ex) {
				Bitmap bm = getArtworkFromFile(context,song_id,album_id);
				if(bm != null) {
					if(bm.getConfig() == null) {
						bm = bm.copy(Bitmap.Config.RGB_565,false);
						if(bm == null && allowdefault) {
							return getDefaultArtwork(context);
						}
					}
				} else if(allowdefault) {
					bm = getDefaultArtwork(context);
				}
				return bm;
			}
			finally {
					if(in != null) {
						try {
							in.close();
						} catch(IOException e) {
							e.printStackTrace();
						}
					}
			}
		}
		return null;
	}

	private static Bitmap getArtworkFromFile(Context context,long songid,long albumid){
		Bitmap bm = null;
		if(albumid < 0 && songid < 0) {
			throw new IllegalArgumentException("Must specify an album or a song id");
		}
		try {
			if(albumid < 0) {
				Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
				ParcelFileDescriptor pfd = context.getContentResolver()
						.openFileDescriptor(uri,"r");
				if(pfd != null) {
					FileDescriptor fd = pfd.getFileDescriptor();
					bm = BitmapFactory.decodeFileDescriptor(fd);
				}
			} else {
				Uri uri = ContentUris.withAppendedId(sArtworkUri,albumid);
				ParcelFileDescriptor pfd = context.getContentResolver()
						.openFileDescriptor(uri,"r");
				if(pfd != null) {
					FileDescriptor fd = pfd.getFileDescriptor();
					bm = BitmapFactory.decodeFileDescriptor(fd);
				}
			}
		} catch(FileNotFoundException ex) {

		}
		return bm;
	}

	private static Bitmap getDefaultArtwork(Context context){
		Log.i("获取Default图片","getDefaultArtwork: ");
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeResource(context.getResources(),R.mipmap.default_play_window_image,opts);
	}
}

