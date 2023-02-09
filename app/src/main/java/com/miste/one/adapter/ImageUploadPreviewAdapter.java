package com.miste.one.adapter;




import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.miste.one.R;

import java.util.ArrayList;
import java.util.List;

public class ImageUploadPreviewAdapter extends RecyclerView.Adapter<ImageUploadPreviewAdapter.ViewHolder>
{

	private List<Uri> imagePath = new ArrayList<>();
	private Context context;



	public ImageUploadPreviewAdapter(List<Uri> imagePath, Context context) {
		this.imagePath = imagePath;
		this.context = context;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_preview_item,parent,false);
		return new ViewHolder(myView);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Glide.with(context).load(imagePath.get(position)).into(holder.imageView);

		holder.cancelImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(imagePath.size()>0)
				{
					imagePath.remove(position);
					notifyDataSetChanged();
				}

			}
		});

	}

	@Override
	public int getItemCount() {
		return imagePath.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public ImageView imageView,cancelImageView;


		public ViewHolder(View itemView) {
			super(itemView);
			imageView = itemView.findViewById(R.id.image_preview_item_imageview);
			cancelImageView= itemView.findViewById(R.id.image_preview_cancelitem_imageview);

		}
	}
}