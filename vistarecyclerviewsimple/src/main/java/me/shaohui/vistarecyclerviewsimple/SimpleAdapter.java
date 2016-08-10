package me.shaohui.vistarecyclerviewsimple;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by shaohui on 16/7/31.
 */
public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder> {

    private List<String> data;

    public SimpleAdapter(List<String> data) {
        this.data = data;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SimpleViewHolder(LayoutInflater
                .from(parent.getContext()).inflate(R.layout.item_simple, parent, false));
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        String str = data.get(position);

//        holder.text.setText(str);

        Glide.with(holder.img.getContext())
                .load(str)
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        private ImageView img;
        private TextView text;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.image);
            text = (TextView) itemView.findViewById(R.id.item_text);
        }
    }
}
