package com.schneenet.android.animatedgif.giflib;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import android.util.Log;

public class GifFileFilter implements FileFilter
{

	@Override
	public boolean accept(File pathname)
	{
		if (pathname.isDirectory()) return true;
		try
		{
			return isGif(pathname);
		}
		catch (IOException e)
		{
			Log.e("GifFileFilter", e.getLocalizedMessage(), e);
			return false;
		}
	}
	
	public static final int[] GIF_MAGIC = {0x47, 0x49, 0x46, 0x38, 0x39, 0x61};
	
	private boolean isGif(File f) throws IOException
	{
		FileInputStream fis = new FileInputStream(f);
		try
		{
			for (int i = 0; i < GIF_MAGIC.length; i++)
			{
				if (fis.read() != GIF_MAGIC[i]) {
                    return false;
                }
			}
			return true;
		}
		finally
		{
			fis.close();
		}
	}

}
