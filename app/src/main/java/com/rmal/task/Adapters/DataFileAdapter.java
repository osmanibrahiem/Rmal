package com.rmal.task.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rmal.task.Models.DataFile;
import com.rmal.task.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DataFileAdapter extends RecyclerView.Adapter<DataFileAdapter.DataFileViewHolder> {

    private static final int IMAGE_TYPE = 56;
    private static final int VIDEO_TYPE = 64;

    private LayoutInflater inflater;
    private List<DataFile> fileList;
    private Context context;
    private cardListener listener;

    public DataFileAdapter(Context context, List<DataFile> fileList) {
        this.inflater = LayoutInflater.from(context);
        this.fileList = fileList;
        this.context = context;
    }

    @NonNull
    @Override
    public DataFileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIDEO_TYPE)
            view = inflater.inflate(R.layout.video_item, parent, false);
        else view = inflater.inflate(R.layout.image_item, parent, false);
        return new DataFileViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return fileList.get(position).isImage() ? IMAGE_TYPE : VIDEO_TYPE;
    }

    public void setListener(cardListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull DataFileViewHolder holder, int position) {
        Picasso.get()
                .load(fileList.get(position).getValue())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_placeholder)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public class DataFileViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        public DataFileViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
        }
    }


    public interface cardListener {
        void onCardListener(View v, int position, DataFile file);
    }
}
