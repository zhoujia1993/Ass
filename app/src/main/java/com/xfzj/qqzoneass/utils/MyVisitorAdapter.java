package com.xfzj.qqzoneass.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xfzj.qqzoneass.R;
import com.xfzj.qqzoneass.model.Visitor;

import java.util.List;

/**
 * Created by zj on 2015/8/22.
 */
public class MyVisitorAdapter extends BaseAdapter {
    private Context context;
    private List<Visitor> visitors;

    public MyVisitorAdapter(Context context, List<Visitor> visitors) {
        this.context = context;
        this.visitors = visitors;
    }


    @Override
    public int getCount() {
        return visitors.size();
    }

    @Override
    public Object getItem(int position) {
        return visitors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(context).inflate(R.layout.gridview_visitor, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Visitor visitor = visitors.get(position);
        if (visitor.bitmap != null) {
            viewHolder.iv.setImageBitmap(visitor.bitmap);
            viewHolder.tv.setText(visitor.name);
//            Log.i("aaa", "name=" + visitor.name);
        }
       
        return convertView;
    }

    public class ViewHolder {
        public MyImageView iv;
        public TextView tv;
        public View root;

        public ViewHolder(View root) {
            this.root = root;
            iv = (MyImageView) root.findViewById(R.id.iv);
            tv = (TextView) root.findViewById(R.id.tv);
        }
    }

}
