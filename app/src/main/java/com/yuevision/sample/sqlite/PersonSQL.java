package com.yuevision.sample.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.yuevision.sample.base.MyApplication;
import com.yuevision.sample.bean.PersonBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjy on 2018/1/8.
 */

public class PersonSQL extends SQLiteOpenHelper {
    private static final String DB_NAME = "person.db";//数据库
    private static final int Db_CO_VERSION = 1;//数据库version
    private static final String TABLE_NAME = "persontable";//子公司下的部门表名

    private static final String NAME = "name";//
    private static final String ID = "ids";//
    private static final String FACEID = "faceid";//
    private static final String IMAGE = "faceimage";//

    public PersonSQL(Context context) {
        super(context, DB_NAME, null, Db_CO_VERSION);
    }

    //创建部门表
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME//表名
            + " ( "
            + NAME + " text,"
            + ID + " text,"
            + FACEID + " text,"
            + IMAGE + " text"
            + " );";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);//建表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //01添加list数据
    public void addList(List<PersonBean> list) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < list.size(); i++) {
            ContentValues values = new ContentValues();

            values.put(NAME, list.get(i).getName());
            values.put(ID, list.get(i).getIds());

            //拼接人脸id的list
            List<String> listFace = list.get(i).getFaceid();
            int faceSize = listFace.size();
            if (faceSize == 1) {
                values.put(FACEID, listFace.get(0));
            } else {
                StringBuffer faceIDs = new StringBuffer();
                for (int j = 0; j < listFace.size(); j++) {
                    if (j == (faceSize - 1)) {
                        faceIDs.append(listFace.get(j));
                    }
                    faceIDs.append(listFace.get(j)).append(",");
                }
                values.put(IMAGE, faceIDs.toString());
            }

            //拼接图片数组
            List<String> listimg = list.get(i).getFaceimage();
            int imgSize = listimg.size();
            if (imgSize == 1) {
                values.put(IMAGE, listimg.get(0));
            } else {
                StringBuffer imgs = new StringBuffer();
                for (int j = 0; j < listimg.size(); j++) {
                    if (j == (imgSize - 1)) {
                        imgs.append(listimg.get(j));
                    }
                    imgs.append(listimg.get(j)).append(",");
                }
                values.put(IMAGE, imgs.toString());
            }
            //导入该条数据
            db.insert(TABLE_NAME, null, values);

        }
        db.close();
    }


    //02获取list数据
    public List<PersonBean> getList() {
        List<PersonBean> listData = new ArrayList<PersonBean>();

        try {

            SQLiteDatabase db = this.getReadableDatabase();

            //筛选
            Cursor cursor = db.query(TABLE_NAME//表名
                    , new String[]{NAME, ID, FACEID, IMAGE}//需要的信息
                    , null
                    , null
                    , null
                    , null
                    , ID + " desc");

            if (cursor == null) {
                return listData;
            } else {
                while (cursor.moveToNext()) {
                    PersonBean model = new PersonBean();

                    model.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                    model.setIds(cursor.getString(cursor.getColumnIndex(ID)));

                    //String转list
                    List<String> listfaceid = new ArrayList<>();
                    String faceIds = cursor.getString(cursor.getColumnIndex(FACEID));
                    String[] faceArray = faceIds.split(",");
                    for (int i = 0; i < faceArray.length; i++) {
                        listfaceid.add(faceArray[i]);
                    }
                    model.setFaceid(listfaceid);


                    //String转list
                    List<String> listimg = new ArrayList<>();
                    String img = cursor.getString(cursor.getColumnIndex(IMAGE));
                    String[] imgArray = img.split(",");
                    for (int i = 0; i < imgArray.length; i++) {
                        listimg.add(imgArray[i]);
                    }
                    model.setFaceimage(listimg);

                    listData.add(model);
                }
            }
            cursor.close();
            db.close();

            return listData;
        } catch (Exception e) {
            Log.e("co_contact异常", "" + e.getMessage());
        }
        return listData;
    }

    //程序退出，清空所有表
    public static void clearDb() {

        SQLiteDatabase db = new PersonSQL(MyApplication.getInstance()).getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_NAME);

        db.close();
    }
}
