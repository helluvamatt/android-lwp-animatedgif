package com.schneenet.android.animatedgif.giflib;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;

/**
 * Utility Class for drawing scaled Bitmaps to a Canvas
 * 
 * @author Matt Schneeberger
 * 
 */
public class ScaleManager
{

	public static void drawImage(Canvas c, Bitmap bm, int st)
	{
		drawImage(c, bm, st, Color.BLACK);
	}

	public static void drawImage(Canvas c, Bitmap bm, int st, int bgColor)
	{
		if (c == null || bm == null) return;
		c.drawColor(bgColor);
		int bmWidth = bm.getWidth();
		int bmHeight = bm.getHeight();
		int cWidth = c.getWidth();
		int cHeight = c.getHeight();
		float scale;
		float dx = 0;
		float dy = 0;
		Matrix m = new Matrix();
		switch (st)
		{
			case Type.CENTER_CROP:
				if (bmWidth * cHeight > cWidth * bmHeight)
				{
					scale = (float) cHeight / (float) bmHeight;
					dx = (cWidth - bmWidth * scale) * 0.5f;
				}
				else
				{
					scale = (float) cWidth / (float) cWidth;
					dy = (cHeight - bmHeight * scale) * 0.5f;
				}
				
				m.setScale(scale, scale);
				m.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
				c.drawBitmap(bm, m, null);
				break;
			case Type.CENTER_INSIDE:
				if (bmWidth <= cWidth && bmHeight <= cHeight)
				{
					scale = 1.0f;
				}
				else
				{
					scale = Math.min((float) cWidth / (float) bmWidth, (float) cHeight / (float) bmHeight);
				}
				dx = (int) ((cWidth - bmWidth * scale) * 0.5f + 0.5f);
				dy = (int) ((cHeight - bmHeight * scale) * 0.5f + 0.5f);
				m.setScale(scale, scale);
				m.postTranslate(dx, dy);
				c.drawBitmap(bm, m, null);
				break;
			case Type.CENTER:
				m.setTranslate((int) ((cWidth - bmWidth) * 0.5f + 0.5f), (int) ((cHeight - bmHeight) * 0.5f + 0.5f));
				c.drawBitmap(bm, m, null);
				break;
			case Type.STRETCH:
				c.drawBitmap(Bitmap.createScaledBitmap(bm, c.getWidth(), c.getHeight(), true), 0, 0, null);
				break;
			case Type.TILE:
				int x_repeat = c.getWidth() / bm.getWidth() + 1;
				int y_repeat = c.getHeight() / bm.getHeight() + 1;
				for (int y = 0; y < y_repeat; y++)
				{
					for (int x = 0; x < x_repeat; x++)
					{
						c.drawBitmap(bm, x * bm.getWidth(), y * bm.getHeight(), null);
					}
				}
				break;
			case Type.DEFAULT:
			default:
				c.drawBitmap(bm, 0, 0, null);
				break;
		}
	}

	public static class Type
	{
		public static final int DEFAULT = 0;
		public static final int CENTER = 1;
		public static final int CENTER_CROP = 2;
		public static final int CENTER_INSIDE = 3;
		public static final int STRETCH = 4;
		public static final int TILE = 5;
		
		public static final String[] SCALE_NAME_ARRAY = {
			"Default",
			"Centered",
			"Fill Centered",
			"Centered Inside",
			"Stretch",
			"Tiled"
		};
		public static final String[] SCALE_DESCRIPTION_ARRAY = {
			"Draw in top left, do not resize.",
			"Centered, do not resize.",
			"Fill entire canvas, preserve aspect ratio, may crop image.",
			"Centered, preserve aspect ratio, do not crop.",
			"Resize image to fit screen, image may look distorted.",
			"Tile image to fill screen. Same as \"Default\" if image is larger than screen."
			
		};
	}
}
