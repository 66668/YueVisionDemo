package com.yuevision.sample.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yuevision.sample.R;

/**
 * 识别成功的弹窗
 * Created by sjy on 2018/1/11.
 */

public class OkDialog extends Dialog {

    public OkDialog(@NonNull Context context) {
        super(context);
    }

    public OkDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected OkDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {
        private View layout;
        private OkDialog dialog;
        private String name;
        private boolean isOk = false;
        private String imgUrl = "";
        private Context mContext;
        TextView tv_name;
        ImageView img_ok;
        ImageView img_no;
        ImageView img_pic;

        public Builder(Context context) {
            this.mContext = context;
            //这里传入自定义的style，直接影响此Dialog的显示效果。style具体实现见style.xml
            dialog = new OkDialog(context, R.style.Dialog);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.dialog_ok, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            //初始化控件
            tv_name = layout.findViewById(R.id.tv_name);
            img_no = layout.findViewById(R.id.img_state_no);
            img_ok = layout.findViewById(R.id.img_state_ok);
            img_pic = layout.findViewById(R.id.img_pic);
        }

        public Builder setName(String message) {
            this.name = message;
            return this;
        }

        public Builder setOk(boolean ok) {
            isOk = ok;
            return this;
        }

        public Builder setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
            return this;
        }

        public OkDialog build() {
            //显示内容
            if (isOk) {
                img_ok.setVisibility(View.VISIBLE);
                img_no.setVisibility(View.GONE);
            } else {
                img_ok.setVisibility(View.GONE);
                img_no.setVisibility(View.VISIBLE);
            }
            tv_name.setText("您好，" + name);
            Glide.with(mContext)
                    .load(imgUrl)
                    .error(ContextCompat.getDrawable(mContext, R.mipmap.pic_default))
                    .into(img_pic);
            //设置
            dialog.setContentView(layout);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);//用户不能通过点击对话框之外的地方取消对话框显示
            Window dialogWindow = dialog.getWindow();
            dialogWindow.setGravity(Gravity.BOTTOM);//显示在底部
            return dialog;
        }
    }

    //    @Override
    //    public void show() {
    //        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
    //
    //        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
    //        lp.gravity = Gravity.BOTTOM;
    //        this.getWindow().setAttributes(lp);
    //        super.show();
    //    }
}
