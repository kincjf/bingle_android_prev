package com.androidhuman.example.gallery;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.androidhuman.example.CameraPreview2.R;
import com.androidhuman.example.utils.SocketUtils;

public class GalleryActivity extends Activity {

	Activity act = this;
	GridView gridView;

	ArrayList<Bitmap> picArr = new ArrayList<Bitmap>();

	ArrayList<String> textArr = new ArrayList<String>();

	String[] filepath = null;
	int p;
	private String TAG = "시발?";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		String path = SocketUtils.IMAGE_FILEPATH;
		Bitmap[] bm = null;
		File file = new File(path);
		if(!file.isDirectory()){
			file.mkdir();
		}
		File[] files = file.listFiles();

		filepath = new String[files.length];
		bm = new Bitmap[files.length];

		for (int i = 0; i < files.length; i++) {
			filepath[i] = path + files[i].getName();
			bm[i] = BitmapFactory.decodeFile(filepath[i]);
			picArr.add(bm[i]);
		}

		gridView = (GridView) findViewById(R.id.gridView1);
		gridView.setAdapter(new gridAdapter());
		Log.i("test","gridView");
		gridView.setOnItemClickListener(new OnItemClickListener() {
			
			/**
			 * 이미지 클릭시 panoViewer로 파일경로를 보내 이미지 보여줌
			 */
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.i("test", position + "");
				Log.i("test", "1");
				Intent intent = new Intent(GalleryActivity.this, PanoViewer.class);
				intent.putExtra("path", filepath[position]);
				Log.i("test", "2");
				startActivity(intent);
				Log.i("test", "3");
			}

		});
	}

	public class gridAdapter extends BaseAdapter {
		LayoutInflater inflater;

		public gridAdapter() {
			inflater = (LayoutInflater) act
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return picArr.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return picArr.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.image, parent, false);
			}
			ImageView imageView = (ImageView) convertView
					.findViewById(R.id.imageView1);
			// TextView textView = (TextView)
			// convertView.findViewById(R.id.textView1);
			imageView.setImageBitmap(picArr.get(position));
			// textView.setText(textArr.get(position));

			return convertView;
		}
	}
}
