package com.ekplate.android.activities.addvendormodule;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.ekplate.android.R;
import com.ekplate.android.activities.vendormodule.GallerySingleImageActivity;
import com.ekplate.android.activities.vendormodule.VendorDetailsActivity;
import com.ekplate.android.activities.vendormodule.VendorsActivity;
import com.ekplate.android.activities.vendormodule.WriteReviewActivity;
import com.ekplate.android.adapters.addvendormodule.GalleryAdapter;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class CustomGalleryActivity extends Activity {

	GridView gridGallery;
	Handler handler;
	GalleryAdapter adapter;
	//ImageView imgNoMedia;
	Button btnGalleryOk;
	String action, routeFrom;
	private ImageLoader imageLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.vendor_gallery);
		action = getIntent().getAction();
		if (action == null) {
			finish();
		}
		initImageLoader();
		init();
		routeFrom = getIntent().getExtras().getString("routeFrom");
	}

	private void initImageLoader() {
		try {
			String CACHE_DIR = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/.temp_tmp";
			new File(CACHE_DIR).mkdirs();
			File cacheDir = StorageUtils.getOwnCacheDirectory(getBaseContext(),
					CACHE_DIR);
			DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
					.cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
			ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
					getBaseContext())
					.defaultDisplayImageOptions(defaultOptions)
					.discCache(new UnlimitedDiscCache(cacheDir))
					.memoryCache(new WeakMemoryCache());
			ImageLoaderConfiguration config = builder.build();
			imageLoader = ImageLoader.getInstance();
			imageLoader.init(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() {
		handler = new Handler();
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		gridGallery.setFastScrollEnabled(true);
		adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
		PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader,
				true, true);
		gridGallery.setOnScrollListener(listener);
		if (action.equalsIgnoreCase(Action.ACTION_MULTIPLE_PICK)) {
			findViewById(R.id.llBottomContainer).setVisibility(View.VISIBLE);
			gridGallery.setOnItemClickListener(mItemMulClickListener);
			adapter.setMultiplePick(true);

		} else if (action.equalsIgnoreCase(Action.ACTION_PICK)) {
			findViewById(R.id.llBottomContainer).setVisibility(View.GONE);
			gridGallery.setOnItemClickListener(mItemSingleClickListener);
			adapter.setMultiplePick(false);
		}

		gridGallery.setAdapter(adapter);
		//imgNoMedia = (ImageView) findViewById(R.id.imgNoMedia);

		btnGalleryOk = (Button) findViewById(R.id.btnGalleryOk);
		btnGalleryOk.setOnClickListener(mOkClickListener);

		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				handler.post(new Runnable() {
					@Override
					public void run() {
						adapter.addAll(getGalleryPhotos());
					}
				});
				Looper.loop();
			};
		}.start();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	View.OnClickListener mOkClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			ArrayList<CustomGallery> selected = adapter.getSelected();
			String[] allPath = new String[selected.size()];
			for (int i = 0; i < allPath.length; i++) {
				allPath[i] = selected.get(i).sdcardPath;
				Log.e("file path", selected.get(i).sdcardPath);
			}
			/*Intent data = new Intent().putExtra("all_path", allPath);
			setResult(RESULT_OK, data);
			finish();*/
			if(routeFrom.equalsIgnoreCase("write_review")) {
				WriteReviewActivity.dataT.clear();
				for (String string : allPath) {
					CustomGallery item = new CustomGallery();
					item.sdcardPath = string;
					WriteReviewActivity.dataT.add(item);
				}
				onBackPressed();
			} else if (routeFrom.equalsIgnoreCase("vendor_details")){
				VendorDetailsActivity.dataT.clear();
				for (String string : allPath) {
					CustomGallery item = new CustomGallery();
					item.sdcardPath = string;
					VendorDetailsActivity.dataT.add(item);
				}
				VendorDetailsActivity.galleryImageSelection = 1;
				onBackPressed();
			} else if(routeFrom.equalsIgnoreCase("vendor_list")) {
				VendorsActivity.dataT.clear();
				for (String string : allPath) {
					CustomGallery item = new CustomGallery();
					item.sdcardPath = string;
					VendorsActivity.dataT.add(item);
				}
				VendorsActivity.flagImageSelected = 1;
				onBackPressed();
			} else if(routeFrom.equalsIgnoreCase("gallery_single_image")) {
				GallerySingleImageActivity.dataT.clear();
				for (String string : allPath) {
					CustomGallery item = new CustomGallery();
					item.sdcardPath = string;
					GallerySingleImageActivity.dataT.add(item);
				}
				GallerySingleImageActivity.flagImageSelected = 1;
				onBackPressed();
			} else if (routeFrom.equalsIgnoreCase("add_vendor_image")) {
				//AddVendorImagesActivity.dataT.clear();
				for (String string : allPath) {
					CustomGallery item = new CustomGallery();
					item.sdcardPath = string;
					item.selectedImageId = 0;
					AddVendorImagesActivity.dataT.add(item);
				}
				AddVendorImagesActivity.flagImageSelected = 1;
				onBackPressed();
			} else {
				Intent data = new Intent().putExtra("all_path", allPath);
				setResult(RESULT_OK, data);
				finish();
			}
		}
	};
	AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			adapter.changeSelection(v, position);
		}
	};

	AdapterView.OnItemClickListener mItemSingleClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			CustomGallery item = adapter.getItem(position);
			Intent data = new Intent().putExtra("single_path", item.sdcardPath);
			setResult(RESULT_OK, data);
			finish();
		}
	};

	private ArrayList<CustomGallery> getGalleryPhotos() {
		ArrayList<CustomGallery> galleryList = new ArrayList<CustomGallery>();
		try {
			final String[] columns = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID };
			final String orderBy = MediaStore.Images.Media._ID;
			Cursor imagecursor = managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					null, null, orderBy);
			if (imagecursor != null && imagecursor.getCount() > 0) {
				while (imagecursor.moveToNext()) {
					CustomGallery item = new CustomGallery();
					int dataColumnIndex = imagecursor
							.getColumnIndex(MediaStore.Images.Media.DATA);
					item.sdcardPath = imagecursor.getString(dataColumnIndex);
					galleryList.add(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// show newest photo at beginning of the list
		Collections.reverse(galleryList);
		return galleryList;
	}
}
