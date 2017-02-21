package com.ekplate.android.adapters.addvendormodule;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.addvendormodule.CustomGallery;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

public class GalleryAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater inflate;
	private ArrayList<CustomGallery> data = new ArrayList<CustomGallery>();
	ImageLoader imageLoader;
	private boolean isActionMultiplePick;
	public GalleryAdapter(Context context, ImageLoader imageLoader) {
		inflate = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		this.imageLoader = imageLoader;
		// clearCache();
	}

	@Override
	public int getCount() {
		/*if(!isActionMultiplePick){
			return data.size() + 1;
		}
		else{*/
			return data.size();
		/*}*/
	}

	@Override
	public CustomGallery getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setMultiplePick(boolean isMultiplePick) {
		this.isActionMultiplePick = isMultiplePick;
	}

	public void selectAll(boolean selection) {
		for (int i = 0; i < data.size(); i++) {
			data.get(i).isSeleted = selection;

		}
		notifyDataSetChanged();
	}

	public boolean isAllSelected() {
		boolean isAllSelected = true;

		for (int i = 0; i < data.size(); i++) {
			if (!data.get(i).isSeleted) {
				isAllSelected = false;
				break;
			}
		}

		return isAllSelected;
	}

	public boolean isAnySelected() {
		boolean isAnySelected = false;

		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isSeleted) {
				isAnySelected = true;
				break;
			}
		}
		return isAnySelected;
	}

	public ArrayList<CustomGallery> getSelected() {
		ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isSeleted) {
				dataT.add(data.get(i));
			}
		}
		return dataT;
	}

	public void addAll(ArrayList<CustomGallery> files) {
		try {
			this.data.clear();
			this.data.addAll(files);
		} catch (Exception e) {
			e.printStackTrace();
		}
		notifyDataSetChanged();
	}

	public void changeSelection(View v, int position) {
		if (data.get(position).isSeleted) {
			data.get(position).isSeleted = false;
		} else {

			int isSeletedSize = getSelected().size();

			if (isSeletedSize == 3) {
				Toast.makeText(mContext, "You cant select more than 3 photos",
						Toast.LENGTH_LONG).show();
			} else {
				data.get(position).isSeleted = true;
			}
		}

		((ViewHolder) v.getTag()).imgQueueMultiSelected.setSelected(data
				.get(position).isSeleted);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;

		if (convertView == null) {

			convertView = inflate.inflate(R.layout.vendor_gallery_item, null);
			holder = new ViewHolder();
			holder.imgQueue = (ImageView) convertView
					.findViewById(R.id.imgQueue);

			holder.imgQueueMultiSelected = (ImageView) convertView
					.findViewById(R.id.imgQueueMultiSelected);

			if (isActionMultiplePick) {
				holder.imgQueueMultiSelected.setVisibility(View.VISIBLE);
			} else {
				holder.imgQueueMultiSelected.setVisibility(View.GONE);
			}

			convertView.setTag(holder);

		} else {
			if (position == data.size() && !isActionMultiplePick) {

				//position = position + 1;
				convertView = inflate.inflate(R.layout.vendor_gallery_item, null);
				convertView.setTag("MORE_BUTTON");
				return convertView;
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
		}
		holder.imgQueue.setTag(position);

		try {

			imageLoader.displayImage("file://" + data.get(position).sdcardPath,
					holder.imgQueue, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							holder.imgQueue
									.setImageResource(R.drawable.no_media);
							super.onLoadingStarted(imageUri, view);
						}
					});

			if (isActionMultiplePick) {

				holder.imgQueueMultiSelected
						.setSelected(data.get(position).isSeleted);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	public class ViewHolder {
		ImageView imgQueue;
		ImageView imgQueueMultiSelected;
	}

	public void clearCache() {
		imageLoader.clearDiscCache();
		imageLoader.clearMemoryCache();
	}

	public void clear() {
		data.clear();
		notifyDataSetChanged();
	}
}
