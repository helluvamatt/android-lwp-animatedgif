package com.schneenet.android.animatedgif;

import java.io.File;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import com.schneenet.android.animatedgif.giflib.GifFileFilter;
import com.schneenet.android.animatedgif.giflib.ScaleManager;
import com.schneenet.android.filemanager.FileSelectionDialog;
import com.schneenet.android.filemanager.FileSelectionDialog.FileSelectionDialogInterface;

public class SettingsActivity extends Activity
{

	private Spinner mSpinner_FrameRate;
	//private Spinner mSpinner_BgColor;
	private Spinner mSpinner_Scaling;
	private ImageButton mImageButton_GifImage;
	private TextView mTextView_GifImage;
	
	private File mCurrentDirectory;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mSpinner_FrameRate = (Spinner) findViewById(R.id.field_frame_rate);
		//mSpinner_BgColor = (Spinner) findViewById(R.id.field_bg_color);
		mSpinner_Scaling = (Spinner) findViewById(R.id.field_scaling);
		mImageButton_GifImage = (ImageButton) findViewById(R.id.select_image_button);
		mTextView_GifImage = (TextView) findViewById(R.id.select_image_label);

		mImageButton_GifImage.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				launchFileSelection();
			}
		});

		ArrayAdapter<CharSequence> frameRateAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, FRAME_RATE_NAME_ARRAY);
		frameRateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner_FrameRate.setAdapter(frameRateAdapter);
		mSpinner_FrameRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> adapter, View v, int pos, long id)
			{
				saveFrameRate(pos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapter)
			{
				// Do nothing
			}
			
		});
		mSpinner_FrameRate.setSelection(PreferenceManager.getDefaultSharedPreferences(this).getInt(KEY_FRAME_RATE, DEFAULT_FRAME_RATE));
		
		ArrayAdapter<CharSequence> scaleAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, ScaleManager.Type.SCALE_NAME_ARRAY);
		scaleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner_Scaling.setAdapter(scaleAdapter);
		mSpinner_Scaling.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View v, int pos, long id)
			{
				saveScale(pos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapter)
			{
				// Do nothing
			}
			
		});
		mSpinner_Scaling.setSelection(PreferenceManager.getDefaultSharedPreferences(this).getInt(KEY_GIF_SCALING, DEFAULT_GIF_SCALING));
		
	}
	
	private void saveFrameRate(int index)
	{
		PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(KEY_FRAME_RATE, index).commit();
	}
	
	private void saveScale(int index)
	{
		PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(KEY_GIF_SCALING, index).commit();
	}
	
	private void saveFileLocation(File selectedFile)
	{
		PreferenceManager.getDefaultSharedPreferences(this).edit().putString(KEY_GIF_IMAGE, selectedFile.getAbsolutePath()).commit();
	}
	
	private void launchFileSelection()
	{
		GifFileFilter gff = new GifFileFilter();
		FileSelectionDialog dialog = new FileSelectionDialog(this, new FileSelectionDialogInterface() {
			
			@Override
			public void onFileSelected(DialogInterface di, File selectedFile)
			{
				mCurrentDirectory = selectedFile.getParentFile();
				mTextView_GifImage.setText(selectedFile.getAbsolutePath());
				saveFileLocation(selectedFile);
				di.dismiss();
			}

			@Override
			public void onBackPressed(DialogInterface di, File currentLocation)
			{
				mCurrentDirectory = currentLocation;
				di.dismiss();
			}
		});
		dialog.loadFolder(mCurrentDirectory);
		dialog.setFilter(gff);
		dialog.setTitle(R.string.fm_title);
		dialog.show();
	}
	
	public static final String KEY_COLOR_BG = "key_Color_BG";
	public static final String KEY_FRAME_RATE = "key_FrameRate";
	public static final String KEY_GIF_IMAGE = "key_GifImage";
	public static final String KEY_GIF_SCALING = "key_GifScaling";

	public static final int DEFAULT_COLOR_BG = 0xFF000000;

	public static final int DEFAULT_FRAME_RATE = 2;
	public static final float[] FRAME_RATE_DATA_ARRAY = {4f, 2f, 1f, 0.5f, 0.25f};
	public static final String[] FRAME_RATE_NAME_ARRAY = {
		"Slowest (0.25x)",
		"Slower (0.5x)",
		"Normal (1x)",
		"Faster (2x)",
		"Fastest (4x)"
	};

	public static final int DEFAULT_GIF_SCALING = 0;

}
