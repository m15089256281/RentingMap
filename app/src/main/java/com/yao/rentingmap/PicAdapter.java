package com.yao.rentingmap;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Yao on 2017/2/10 0010.
 */

public class PicAdapter extends RecyclerView.Adapter<PicAdapter.ViewHolder> {

    int pics[] = new int[]{R.mipmap.pic_01, R.mipmap.pic_02, R.mipmap.pic_03, R.mipmap.pic_04};

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pic, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.ivPic.setImageResource(pics[position]);
    }


    @Override
    public int getItemCount() {
        return pics.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPic;

        public ViewHolder(View itemView) {
            super(itemView);
            ivPic = (ImageView) itemView.findViewById(R.id.iv_pic);
        }
    }
}
