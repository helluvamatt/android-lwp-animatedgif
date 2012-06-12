package com.schneenet.android.filemanager;

import java.io.File;
import java.io.FileFilter;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import com.schneenet.android.animatedgif.R;

public class FileSelectionDialog extends Dialog implements OnClickListener, OnItemClickListener
{

	private ListView mListView_fileList;
	private EditText mEditText_location;
	private ImageButton mImageButton_up;
	private ImageButton mImageButton_go;

	private File mCurrentLocation;
	private FileFilter mFileFilter = null;
	private FileManagerAdapter mFileManagerAdapter;
	private FileSelectionDialogInterface mListener;

	public FileSelectionDialog(Context context, FileSelectionDialogInterface l)
	{
		super(context);
		setContentView(R.layout.file_manager_dialog_layout);

		mListener = l;

		mListView_fileList = (ListView) findViewById(R.id.fm_list);
		mListView_fileList.setOnItemClickListener(this);

		mEditText_location = (EditText) findViewById(R.id.fm_location);

		mImageButton_up = (ImageButton) findViewById(R.id.fm_up_button);
		mImageButton_up.setOnClickListener(this);
		mImageButton_go = (ImageButton) findViewById(R.id.fm_go_button);
		mImageButton_go.setOnClickListener(this);

		mFileManagerAdapter = new FileManagerAdapter(context, R.drawable.ic_image);

		loadFolder(new File(DEFAULT_LOCATION));
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.fm_up_button:
				loadFolder(mCurrentLocation.getParentFile());
				break;
			case R.id.fm_go_button:
				// TODO Go Button
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View v, int pos, long id)
	{
		File clickedFile = mFileManagerAdapter.getFileItem(pos);
		if (clickedFile.isDirectory())
		{
			loadFolder(clickedFile);
		}
		else
		{
			mListener.onFileSelected(this, clickedFile);
		}
	}

	public void onBackPressed()
	{
		mListener.onBackPressed(this, mCurrentLocation);
	}

	public void setFilter(FileFilter filter)
	{
		mFileFilter = filter;
		loadFolder(mCurrentLocation);
	}

	public void loadFolder(File location)
	{
		if (location != null && location.isDirectory())
		{
			mCurrentLocation = location;
			mEditText_location.setText(mCurrentLocation.getAbsolutePath());
			mFileManagerAdapter.clear();
			File[] files = mCurrentLocation.listFiles(mFileFilter);
			for (File f : files)
			{
				mFileManagerAdapter.addFile(f);
			}
			mListView_fileList.setAdapter(mFileManagerAdapter);
		}
	}

	public void show()
	{
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.FILL_PARENT;
		lp.height = WindowManager.LayoutParams.FILL_PARENT;
		super.show();
		getWindow().setAttributes(lp);
	}

	public static final String DEFAULT_LOCATION = "/sdcard";

	public interface FileSelectionDialogInterface
	{
		public void onFileSelected(DialogInterface di, File selectedFile);

		public void onBackPressed(DialogInterface di, File currentLocation);
	}

}
