package com.schneenet.android.filemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.schneenet.android.animatedgif.R;

public class FileManagerAdapter extends BaseAdapter
{

	private ArrayList<File> mFileList;
	private Context mContext;
	private int mDefaultFileImage;
	
	public FileManagerAdapter(Context ctxt, int defaultFileImage)
	{
		mDefaultFileImage = defaultFileImage;
		mContext = ctxt;
		mFileList = new ArrayList<File>();
	}
	
	public void setFileList(List<File> fileList)
	{
		clear();
		mFileList.addAll(fileList);
	}
	
	public void addFile(File file)
	{
		mFileList.add(file);
	}
	
	public void clear()
	{
		mFileList.clear();
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			convertView = LayoutInflater.from(mContext).inflate(R.layout.file_list_item, parent, false);
		}
		
		File f = getFileItem(pos);
		TextView tv = (TextView) convertView.findViewById(R.id.fm_listitem_text);
		int drawable = mDefaultFileImage;
		if (f.isDirectory())
		{
			drawable = R.drawable.ic_folder;
		}
		tv.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
		tv.setText(f.getName());
		return convertView;
	}

	public File getFileItem(int position)
	{
		return mFileList.get(position);
	}
	
	@Override
	public int getCount()
	{
		return mFileList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return getFileItem(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

}
