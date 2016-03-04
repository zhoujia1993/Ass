package com.xfzj.qqzoneass.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xfzj.qqzoneass.R;
import com.xfzj.qqzoneass.model.ShowContent;

import java.util.List;

/**
 * Created by zj on 2015/7/25.
 */
public class MyRecyleAdapter extends android.support.v7.widget.RecyclerView.Adapter<MyRecyleAdapter.ViewHolder> implements View.OnClickListener {
    
    private List<ShowContent> lists;
    private Context context;
    private onRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    public MyRecyleAdapter(Context context, List<ShowContent> lists) {
        this.lists = lists;
        this.context = context;

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public MyRecyleAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview_content, viewGroup,false);

        view.setOnClickListener(this);
        return new ViewHolder(view, i);

    }

    @Override
    public void onBindViewHolder(MyRecyleAdapter.ViewHolder viewHolder, int i) {
        ShowContent showContent = lists.get(i);
        viewHolder.tvName.setText(showContent.name);
        viewHolder.root.setTag(R.id.qqname,showContent.name);
        viewHolder.root.setTag(R.id.qq,showContent.qq);
        viewHolder.tvContent.setText(showContent.content);
        Bitmap  bitmapLike = BitmapFactory.decodeResource(context.getResources(), R.drawable.like_succ);
        Bitmap  bitmapComment=BitmapFactory.decodeResource(context.getResources(), R.drawable.comment_succ);
        viewHolder.ivFeedBackComment.setImageBitmap(bitmapComment);
        viewHolder.ivFeedBackLike.setImageBitmap(bitmapLike);
        switch (showContent.type) {
            case LIKE:
                viewHolder.ivFeedBackComment.setVisibility(View.INVISIBLE);
                viewHolder.ivFeedBackLike.setVisibility(View.VISIBLE);
                break;
            case Comment:
                viewHolder.ivFeedBackComment.setVisibility(View.VISIBLE);
                viewHolder.ivFeedBackLike.setVisibility(View.INVISIBLE);
                break;
            case Both:
                viewHolder.ivFeedBackComment.setVisibility(View.VISIBLE);
                viewHolder.ivFeedBackLike.setVisibility(View.VISIBLE);
                break;
        }
       


    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public void setOnRecyclerViewItemClickListener(onRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }
    @Override
    public void onClick(View v) {
        if (onRecyclerViewItemClickListener != null) {
            onRecyclerViewItemClickListener.onItemClick(v, (String) v.getTag(R.id.qq), (String) v.getTag(R.id.qqname));
        }


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvName;
        public final ImageView ivFeedBackLike,ivFeedBackComment;
        public final TextView tvContent;
        public final View root;
        private int position;
        public ViewHolder(View root, int position) {
            super(root);
            this.position = position;
            tvName = (TextView) root.findViewById(R.id.tvName);
            ivFeedBackLike = (ImageView) root.findViewById(R.id.ivFeedBackLike);
            ivFeedBackComment = (ImageView) root.findViewById(R.id.ivFeedBackComment);
            tvContent = (TextView) root.findViewById(R.id.tvContent);
            this.root = root;
           

        }


    }

    public interface onRecyclerViewItemClickListener {
        void onItemClick(View view, String qq,String name);
        
    }
}
