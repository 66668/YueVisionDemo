package com.yuevision.sample.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.guo.android_extend.widget.ExtImageView;
import com.yuevision.sample.R;
import com.yuevision.sample.base.MyApplication;
import com.yuevision.sample.myconfig.FaceDB;

/**
 * Created by sjy on 2018/1/4.
 */

public class RegisterViewAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{
    Context mContext;
    LayoutInflater mLInflater;

    public RegisterViewAdapter(Context c) {
        mContext = c;
        mLInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return MyApplication.getInstance().mFaceDB.mRegister.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView != null) {
            holder = (Holder) convertView.getTag();
        } else {
            convertView = mLInflater.inflate(R.layout.item_sample, null);
            holder = new Holder();
            holder.siv = convertView.findViewById(R.id.imageView1);
            holder.tv = convertView.findViewById(R.id.textView1);
            convertView.setTag(holder);
        }

        if (!MyApplication.getInstance().mFaceDB.mRegister.isEmpty()) {
            FaceDB.FaceRegist face = MyApplication.getInstance().mFaceDB.mRegister.get(position);
            holder.tv.setText(face.mName);
            //holder.siv.setImageResource(R.mipmap.ic_launcher);
            convertView.setWillNotDraw(false);
        }

        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String name = MyApplication.getInstance().mFaceDB.mRegister.get(position).mName;
        final int count = MyApplication.getInstance().mFaceDB.mRegister.get(position).mFaceList.size();
        new AlertDialog.Builder(mContext)
                .setTitle("删除注册名:" + name)
                .setMessage("包含:" + count + "个注册人脸特征信息")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyApplication.getInstance().mFaceDB.delete(name);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    class Holder {
        ExtImageView siv;
        TextView tv;
    }
}