package com.schneenet.android.animatedgif;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;
import com.schneenet.android.animatedgif.giflib.GifDecoder;
import com.schneenet.android.animatedgif.giflib.ScaleManager;

public class GifWallpaper extends WallpaperService
{

	@Override
	public Engine onCreateEngine()
	{
		return new GifWallpaperEngine();
	}
	
	protected Context getContext()
	{
		return this;
	}
	
	class GifWallpaperEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener
	{
		
		private Handler mHandler = new Handler();
		private boolean mVisible;
		private int mCurFrame = 0;
		
		private SharedPreferences mPrefs;
		private int mColorBg = SettingsActivity.DEFAULT_COLOR_BG;
		private float mFrameRate = SettingsActivity.DEFAULT_FRAME_RATE;
		private int mScaleType = SettingsActivity.DEFAULT_GIF_SCALING;
		
		private GifDecoder mGifDecoder = new GifDecoder();
		
		public GifWallpaperEngine()
		{			
			// Load Preferences
			mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
			mPrefs.registerOnSharedPreferenceChangeListener(this);
			onSharedPreferenceChanged(mPrefs, null);
		}
		
		private final Runnable mDrawFrame = new Runnable()
		{
			public void run()
			{
				drawFrame();
			}
		};
		
		@Override
		public void onCreate(SurfaceHolder surfaceHolder)
		{
			super.onCreate(surfaceHolder);
		}

		@Override
		public void onDestroy()
		{
			super.onDestroy();
			mHandler.removeCallbacks(mDrawFrame);
		}

		@Override
		public void onVisibilityChanged(boolean visible)
		{
			mVisible = visible;
			if (visible)
			{
				loadImage();
				drawFrame();
			}
			else
			{
				mHandler.removeCallbacks(mDrawFrame);
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height)
		{
			super.onSurfaceChanged(holder, format, width, height);
			drawFrame();
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder)
		{
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder)
		{
			super.onSurfaceDestroyed(holder);
			mVisible = false;
			mHandler.removeCallbacks(mDrawFrame);
		}
		
		private void drawFrame()
		{
			final SurfaceHolder holder = getSurfaceHolder();
			Canvas c = null;
			try
			{
				c = holder.lockCanvas();
				if (c != null)
				{
					// Draw background
					c.drawColor(mColorBg);
					
					// Draw frame from GIF
					Bitmap b = mGifDecoder.getFrame(mCurFrame);
					if (b != null)
					{
						ScaleManager.drawImage(c, b, mScaleType, mColorBg);
					}
					else
					{
						InputStream is;
						try
						{
							is = getAssets().open("no_image.png");
							ScaleManager.drawImage(c, BitmapFactory.decodeStream(is), ScaleManager.Type.CENTER, 0xFF000000);
						}
						catch (IOException e)
						{
							Log.e("GifWallpaper", "Unable to open no_image.png", e);
						}
					}
					
					// Advance Frame Counter
					if (mCurFrame < mGifDecoder.getFrameCount())
					{
						mCurFrame++;
					}
					else
					{
						mCurFrame = 0;
					}
					
				}
			}
			finally
			{
				if (c != null)
					holder.unlockCanvasAndPost(c);
			}

			mHandler.removeCallbacks(mDrawFrame);
			if (mVisible)
			{
				// Frame delay
				long fDelay = Math.round((float) mGifDecoder.getDelay(mCurFrame) * mFrameRate);
				mHandler.postDelayed(mDrawFrame, fDelay);
			}
		}
		
		private void loadImage()
		{
			try {
				File f = new File(PreferenceManager.getDefaultSharedPreferences(getContext()).getString(SettingsActivity.KEY_GIF_IMAGE, ""));
				if (f != null) {
					InputStream is = new FileInputStream(f);
					if (mGifDecoder.read(is) != GifDecoder.STATUS_OK)
					{
						Toast.makeText(getContext(), "Unable to open GIF image...", Toast.LENGTH_SHORT);
					}
				}
			}
			catch (IOException ex)
			{
				Log.e("GifWallpaper", "Unable to open GIF image...", ex);
			}
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
		{
			mFrameRate = SettingsActivity.FRAME_RATE_DATA_ARRAY[sharedPreferences.getInt(SettingsActivity.KEY_FRAME_RATE, SettingsActivity.DEFAULT_FRAME_RATE)];
			mColorBg = sharedPreferences.getInt(SettingsActivity.KEY_COLOR_BG, SettingsActivity.DEFAULT_COLOR_BG);
			mScaleType = sharedPreferences.getInt(SettingsActivity.KEY_GIF_SCALING, SettingsActivity.DEFAULT_GIF_SCALING);
			loadImage();
		}
		
	}

}
