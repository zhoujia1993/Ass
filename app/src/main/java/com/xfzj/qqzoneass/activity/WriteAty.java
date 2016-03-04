package com.xfzj.qqzoneass.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.umeng.analytics.MobclickAgent;
import com.xfzj.qqzoneass.R;
import com.xfzj.qqzoneass.config.Config;
import com.xfzj.qqzoneass.model.Location;
import com.xfzj.qqzoneass.model.PicInfo;
import com.xfzj.qqzoneass.model.PublishParameter;
import com.xfzj.qqzoneass.utils.Code;
import com.xfzj.qqzoneass.utils.Constants;
import com.xfzj.qqzoneass.utils.MyPopUpWindow;
import com.xfzj.qqzoneass.utils.UrlUtils;

import net.steamcrafted.loadtoast.LoadToast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WriteAty extends BaseCommActivity implements View.OnClickListener, MyPopUpWindow.IdCallBck, Toolbar.OnMenuItemClickListener {

    private MyPopUpWindow myPopUpWindow;
    private PublishParameter p = new PublishParameter();
    /**
     * 存放图片路径的集合
     */
    private List<String> pathLists = new ArrayList<>();
    private String gtk;
    private RelativeLayout rl;
    //    private TextView tvCancel;
    private TextView tvPic;
    //    private TextView tvPublish;
    private MaterialEditText etShuoShuo;
    private ImageView ivEmoj;
    //    private TextView tvWordCount;
    private LinearLayout llPosition, llPic;
    private TextView tvLocation;
    private LinearLayout llPhoneType;
    private TextView tvPhoneType;
    private String phoneType;
    private CardView cv;
    private Toolbar toolbar;
    //    private ProgressDialog pd;
    private LoadToast lt;
    /**是否是上传的第一张图片，并且记录他的时间*/
    private boolean isFirstPic=true;
    private long firstPicTime;
    @SuppressWarnings("ConstantConditions")
    private void assignViews() {
        cv = (CardView) findViewById(R.id.cv);
        rl = (RelativeLayout) findViewById(R.id.rl);
//        tvCancel = (TextView) findViewById(R.id.tvCancel);
        tvPic = (TextView) findViewById(R.id.tvPic);
//        tvPublish = (TextView) findViewById(R.id.tvPublish);
        etShuoShuo = (MaterialEditText) findViewById(R.id.etShuoShuo);
        ivEmoj = (ImageView) findViewById(R.id.ivEmoj);
//        tvWordCount = (TextView) findViewById(R.id.tvWordCount);
        llPosition = (LinearLayout) findViewById(R.id.llPosition);
        llPic = (LinearLayout) findViewById(R.id.llPic);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        llPhoneType = (LinearLayout) findViewById(R.id.llPhoneType);
        tvPhoneType = (TextView) findViewById(R.id.tvPhoneType);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ivEmoj.setOnClickListener(this);
//        tvCancel.setOnClickListener(this);
//        tvPublish.setOnClickListener(this);
        llPhoneType.setOnClickListener(this);
        llPosition.setOnClickListener(this);
        llPic.setOnClickListener(this);
        toolbar.setTitle("写说说");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sspublish);
        assignViews();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        myPopUpWindow = new MyPopUpWindow(getApplicationContext(), this);
        final DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        ViewTreeObserver observer = cv.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    cv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    cv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                myPopUpWindow.setWindowHeight(metrics.heightPixels - (int) cv.getBottom() - getStatusBarHeight() - dip2px(76));
            }
        });

        //获取以前选择的手机类型
        phoneType = Config.getMyPhoneTYpe(getApplicationContext());
        if (null != phoneType && !"".equals(phoneType)) {
            tvPhoneType.setText(phoneType);
        }


    }

    private int dip2px(int i) {


        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (i * density + 0.5f);
    }


    @Override
    protected void onStart() {
        super.onStart();
        etShuoShuo.addTextChangedListener(new TextWatcher() {
            private int editStart;

            private int editEnd;

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                editStart = etShuoShuo.getSelectionStart();
                editEnd = etShuoShuo.getSelectionEnd();

                etShuoShuo.removeTextChangedListener(this);

                while (calculateLength(s.toString()) > 140) {
                    s.delete(editStart - 1, editEnd);
                    editStart--;
                    editEnd--;
                }
                etShuoShuo.setSelection(editStart);
                etShuoShuo.addTextChangedListener(this);
//                tvWordCount.setText(calculateLength(s.toString()) + "/140");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 计算字符数
     *
     * @param c
     * @return
     */
    private long calculateLength(CharSequence c) {
        if (null == c) {
            return 0;
        }
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            int tmp = (int) c.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len += 0.5;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivEmoj:
                myPopUpWindow.setAnimationStyle(R.style.PopUpWindow);
                myPopUpWindow.showAtLocation(ivEmoj, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                break;
//            case R.id.tvCancel:
//                finish();
//                break;
            case R.id.tvPublish:
                startPublishSS();
                break;
            case R.id.llPic:
                startActivityForResult(new Intent(getApplicationContext(), PicSelectAty.class), Constants.SELECT_PIC);
                break;
            case R.id.llPhoneType:
                startActivityForResult(new Intent(getApplicationContext(), PhoneTypeAty.class), Constants.SELECT_PHONETYPE);
                break;
            case R.id.llPosition:
                startActivityForResult(new Intent(getApplicationContext(), LocationAty.class), Constants.SELECT_LOCATION);
                break;

        }
    }

    public void startPublishSS() {
        String content = etShuoShuo.getText().toString().trim();
        if (null == content || "".equals(content)) {
            Toast.makeText(getApplicationContext(), "内容不能为空", Toast.LENGTH_SHORT).show();
            etShuoShuo.requestFocus();
            return;
        }
        p.source_name = tvPhoneType.getText().toString().trim();
        if ("机型".equals(p.source_name)) {
            p.source_name = "";
        }
        gtk = Config.getUserInfo(getApplicationContext()).gtk;
        p.res_uin = Config.getUserInfo(getApplicationContext()).number;
        //获取发表说说需要的sid
        p.sid = Config.getUserInfo(getApplicationContext()).sid;
//                publishSS(gtk, etShuoShuo.getText().toString().trim());
        MobclickAgent.onEvent(this, "startPublish");
//        Log.i("aaa", "onclicked");
        PublishSSAsynTak asynTak = new PublishSSAsynTak(etShuoShuo.getText().toString().trim());
        asynTak.execute();
    }

//    public void publishSS(String gtk, String SSContent) {
//        try {
//
//            final String url = UrlUtils.PUBLISH_URL + gtk;
//
//
//            Log.i("aaa", "gtkk=" + gtk + "shuoshuo" + etShuoShuo.getText().toString().trim());
//            List<NameValuePair> params = new ArrayList<>();
//            params.add(new BasicNameValuePair("g_tk", gtk));
//            params.add(new BasicNameValuePair("content", SSContent));
//            params.add(new BasicNameValuePair("format", p.format));
//            params.add(new BasicNameValuePair("issyncweibo", p.issyncweibo));
//            params.add(new BasicNameValuePair("lat", p.lat));
//            params.add(new BasicNameValuePair("lbsid", p.lbsid));
//            params.add(new BasicNameValuePair("lon", p.lon));
//            params.add(new BasicNameValuePair("opr_type", p.opr_type));
//            params.add(new BasicNameValuePair("res_uin", p.res_uin));
//            params.add(new BasicNameValuePair("sid", p.sid));
//            params.add(new BasicNameValuePair("richval", p.richval));
//            params.add(new BasicNameValuePair("source_name", p.source_name));
//            params.add(new BasicNameValuePair("is_winphone", p.is_winphone));
//            final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
//            Log.i("aaa", url);
//            Log.i("aaa", p.toString());
//            //显示进度条
////            pd = ProgressDialog.show(WriteAty.this, null, "正在发表说说", false, true);
//            lt = new LoadToast(WriteAty.this);
//            new NetConnection(getApplicationContext(), url, entity, new NetConnection.SuccessCallback() {
//                @Override
//                public void onSuccess(HttpResponse response) {
////                    if (pd.isShowing()) {
////                        pd.dismiss();
////                    }
//                }
//                @Override
//                public void onSuccess(String result) {
//                    Log.i("aaa", "result=" + result);
////                    if (pd.isShowing()) {
////                        pd.dismiss();
////                    }
//                    if (result.contains("\"code\":0")) {
//                        Toast.makeText(getApplicationContext(), "说说发表成功", Toast.LENGTH_SHORT).show();
//                        finish();
//                    } else {
//                        Toast.makeText(getApplicationContext(), "说说发表失败", Toast.LENGTH_SHORT).show();
//                    }
//
//
//                }
//            }, new NetConnection.FailCallback() {
//                @Override
//                public void onFail() {
////                    if (pd.isShowing()) {
////                        pd.dismiss();
////                    }
//                }
//            }, "POST");
//
//        } catch (Exception e) {
////            if (pd.isShowing()) {
////                pd.dismiss();
////            }
//            e.printStackTrace();
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Constants.SELECT_PHONETYPE || resultCode == Constants.SELECT_LOCATION || resultCode == Constants.SELECT_PIC) {

            switch (requestCode) {
                case Constants.SELECT_PHONETYPE:
                    phoneType = data.getStringExtra(Constants.PHONETYPE);
                    if ("不显示机型".equals(phoneType)) {
                        phoneType = "机型";
                    }
                    tvPhoneType.setText(phoneType);
                    Config.saveMyPhoneTYpe(getApplicationContext(), phoneType);
                    break;

                case Constants.SELECT_LOCATION:
                    Location location = (Location) data.getSerializableExtra(Constants.LOCATION);
                    if ("不显示位置".equals(location.address)) {
                        location.address = "地点";
                    }
                    tvLocation.setText(location.address);
                    p.lat = location.lat;
                    p.lon = location.lon;
//                    Log.i("aaa", "add=" + location.address + "lat=" + p.lat + "lon=" + p.lon);
                    break;
                case Constants.SELECT_PIC:
                    pathLists = data.getStringArrayListExtra(Constants.PIC);
                    if (pathLists.size() > 0) {
                        tvPic.setText("已选择" + pathLists.size() + "张图片");
                    } else {
                        tvPic.setText("图片");
                    }
                    break;
            }
        }
    }

    @Override
    public void getId(int id, String name) {
//        Log.i("aaa", "点击了" + id);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
        Matrix matrix = new Matrix();
        matrix.postScale(2.0f, 2.0f);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        ImageSpan imageSpan = new ImageSpan(getApplicationContext(), bitmap);

        SpannableString spannableString = new SpannableString("[em]" + name + "[/em]");
        spannableString.setSpan(imageSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        etShuoShuo.append(spannableString);
//        Log.i("aaa", "说说是+" + etShuoShuo.getText().toString());
    }

    /**
     * 获取状态栏的高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int id = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (id > 0) {
            result = getResources().getDimensionPixelSize(id);
        }
        return result;

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tvPublish:
                startPublishSS();
                break;

        }


        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_write_aty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (android.R.id.home == item.getItemId()) {
            finish();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private class PublishSSAsynTak extends AsyncTask<Void, String, Integer> {
        //        ProgressDialog pd;
        LoadToast lt;
        List<Map<String, String>> picInfos = new ArrayList<>();
        List<PicInfo> picInfoList = new ArrayList<>();
        String ssContent;

        public PublishSSAsynTak(String ssContent) {
            this.ssContent = ssContent;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lt = new LoadToast(WriteAty.this);
            lt.setTranslationY(dip2px(getStatusBarHeight()+56));
            if (pathLists.size() > 0)
//                pd = ProgressDialog.show(WriteAty.this, null, "正在上传图片...", false, true);
                lt.setText("正在上传图片...");
            else
//                pd = ProgressDialog.show(WriteAty.this, null, "正在发表说说...", false, true);
                lt.setText("正在发表说说...");
//            lt.setTranslationY(100);
            lt.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {
//                if (null == p.sid || "".equals(p.sid)) {
//                    return Code.Fail;
//                }
                //Log.i("aaa", "sid=" + p.sid);
                //如果选择了图片，就要先上传每张图片
                if (pathLists.size() > 0) {
                    for (String path : pathLists) {
                        publishProgress(new String[]{"0", "正在上传图片....(" + (picInfos.size() + 1) + "/" + pathLists.size() + ")"});
                        Map<String, String> picInfo = uploadPicforLenMd5(UrlUtils.UPLOAD_IMAGE, path);
                        picInfos.add(picInfo);
                    }
                    //Log.i("aaa", picInfos.size() + "=" + pathLists.size());
                    int minus = pathLists.size() - picInfos.size();
                    if (minus > 0) {
                        publishProgress(new String[]{"1", "成功上传" + picInfos.size() + "张图片，有" + minus + "张图片上传失败！"});

                    }
                    //用相同的地址再上传一次图片，这次获取到的是发表说说要用到的参数
                    picInfoList = uploadPicForPicInfo(UrlUtils.UPLOAD_IMAGE, picInfos);
                    minus = picInfos.size() - picInfoList.size();
                    if (minus > 0) {
                        publishProgress(new String[]{"1", "成功解析" + picInfoList.size() + "张图片，有" + minus + "张图片解析失败！"});
                    }
                    StringBuilder sb = new StringBuilder();
                    for (PicInfo info : picInfoList) {
                        sb.append(info.toString() + " ");
                    }
                    p.richval = sb.toString().substring(0, sb.toString().length() - 1);
                }
                //开始发表说说
                publishProgress(new String[]{"0", "正在发表说说..."});
                int feedback = publishSS(UrlUtils.PUBLISH_URL + gtk, ssContent, p);


                return feedback;
            } catch (Exception e) {

                e.printStackTrace();
            }

            return Code.Fail;

        }

        private List<PicInfo> uploadPicForPicInfo(String uploadImage, List<Map<String, String>> picInfos) throws Exception {
            StringBuilder sblen = new StringBuilder();
            StringBuilder sbmd5 = new StringBuilder();
            for (Map<String, String> maps : picInfos) {
                for (Map.Entry<String, String> map : maps.entrySet()) {
                    sblen.append(map.getValue() + "|");
                    sbmd5.append(map.getKey() + "|");
                }
            }

            String strlen = sblen.toString();
            String strMd5 = sbmd5.toString();

            strlen = strlen.substring(0, strlen.length() - 1);
            strMd5 = strMd5.substring(0, strMd5.length() - 1);
            //Log.i("aaa", "filelen=" + strlen + " md5=" + strMd5 + " res_uin" + p.res_uin + " sid=" + p.sid);

            String result = uploadbyPost(uploadImage, setEntityForPicInfo(strlen, strMd5, p.res_uin, p.sid, picInfos.size()),true);
            //Log.i("aaa", "result=" + result);
            List<PicInfo> lists = new ArrayList<>();
            result = result.substring(result.indexOf("["), result.indexOf("]") + 1);
            System.out.println(result);
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                try {
                    PicInfo picInfo = new PicInfo();
                    JSONObject jsonObject = (JSONObject) array.get(i);
                    JSONObject object = jsonObject.getJSONObject("picinfo");
                    picInfo.albumid = object.getString("albumid");
                    picInfo.lloc = object.getString("lloc");
                    picInfo.sloc = object.getString("sloc");
                    picInfo.type = object.getString("type");
                    picInfo.height = object.getString("height");
                    picInfo.width = object.getString("width");
                    if (picInfo != null) {
                        lists.add(picInfo);
                    }
                } catch (Exception e) {

                }
            }
            //Log.i("aaa", lists.size() + "");
            return lists;
        }

        /**
         * 发表说说
         *
         * @param url
         * @param ssContent
         * @param p
         */
        private int publishSS(String url, String ssContent, PublishParameter p) throws Exception {
            String result = uploadbyPost(url, setentityForPubSS(ssContent, p),false);
            //Log.i("aaa", "说说发表内容：" + result);
            if (result.contains("\"code\":0")) {
                return Code.SUCCESS;
            } else {
                return Code.Fail;
            }


        }

        private UrlEncodedFormEntity setentityForPubSS(String ssContent, PublishParameter p) throws Exception {

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("g_tk", gtk));
            params.add(new BasicNameValuePair("content", ssContent));
            params.add(new BasicNameValuePair("format", p.format));
            params.add(new BasicNameValuePair("issyncweibo", p.issyncweibo));
            params.add(new BasicNameValuePair("lat", p.lat));
            params.add(new BasicNameValuePair("lbsid", p.lbsid));
            params.add(new BasicNameValuePair("lon", p.lon));
            params.add(new BasicNameValuePair("opr_type", p.opr_type));
            params.add(new BasicNameValuePair("res_uin", p.res_uin));
            params.add(new BasicNameValuePair("sid", p.sid));
            params.add(new BasicNameValuePair("richval", p.richval));
            params.add(new BasicNameValuePair("source_name", p.source_name));
            params.add(new BasicNameValuePair("is_winphone", p.is_winphone));
            return new UrlEncodedFormEntity(params, "utf-8");


        }

        /**
         * @param filelen
         * @param md5
         * @param res_uin
         * @param sid
         * @return
         */
        private UrlEncodedFormEntity setEntityForPicInfo(String filelen, String md5, String res_uin, String sid, int num) throws Exception {
            List<NameValuePair> lists = new ArrayList<NameValuePair>();
            lists.add(new BasicNameValuePair("output_type", "json"));
            lists.add(new BasicNameValuePair("preupload", "2"));
            lists.add(new BasicNameValuePair("md5", md5));
            lists.add(new BasicNameValuePair("filelen", filelen));
            lists.add(new BasicNameValuePair("batchid", firstPicTime+"000"));
            lists.add(new BasicNameValuePair("currnum", ""));
            lists.add(new BasicNameValuePair("uploadNum", num + ""));
            lists.add(new BasicNameValuePair("uploadtime",firstPicTime/1000+ ""));
            lists.add(new BasicNameValuePair("output_charset", "utf-8"));
            lists.add(new BasicNameValuePair("logintype", "sid"));
            lists.add(new BasicNameValuePair("uploadtype", "1"));
            lists.add(new BasicNameValuePair("upload_hd", "0"));
            lists.add(new BasicNameValuePair("albumtype", "7"));
            lists.add(new BasicNameValuePair("big_style", "1"));
            lists.add(new BasicNameValuePair("albumtype", "7"));
            lists.add(new BasicNameValuePair("op_src", ""));
            lists.add(new BasicNameValuePair("charset", "utf-8"));
            lists.add(new BasicNameValuePair("uin", res_uin));
            lists.add(new BasicNameValuePair("refer", "shuoshuo"));
            lists.add(new BasicNameValuePair("sid", sid));
            return new UrlEncodedFormEntity(lists, "utf-8");
        }

        /**
         * 上传图片来获取图片长度和md5值
         *
         * @param uploadImageUrl
         * @param path
         * @return
         */
        private Map<String, String> uploadPicforLenMd5(String uploadImageUrl, String path) throws Exception {
            String filelen = "";
            String filemd5 = "";
            Map<String, String> map = new HashMap<>();
            String result = uploadbyPostbyString(uploadImageUrl, setEntityForLenMd5byMulti(path, p.res_uin, p.sid));
            //Log.i("aaa", result);
            Pattern pLen = Pattern.compile("(\"filelen\" :)\\s*\\w*");
            Pattern pMd5 = Pattern.compile("(\"filemd5\" :)\\s*\\S*");
            Matcher mLen = pLen.matcher(result);
            Matcher mMd5 = pMd5.matcher(result);
            if (mLen.find() && mMd5.find()) {
                if (isFirstPic) {
                    firstPicTime=System.currentTimeMillis();
                    isFirstPic=false;
                }
                filelen = mLen.group();
                filemd5 = mMd5.group();
                filelen = filelen.substring(filelen.indexOf(":") + 2);
                filemd5 = filemd5.substring(filemd5.indexOf(":") + 3, filemd5.length() - 1);
            }
            //Log.i("aaa", "filelen=" + filelen + " filemd5=" + filemd5);
            map.put(filemd5, filelen);
            return map;
        }

        private String uploadbyPostbyString(String uploadImageUrl, MultipartEntity multipartEntity) throws Exception {

            HttpPost post = new HttpPost(uploadImageUrl);
            String cookie = getSharedPreferences(Constants.USERINFO, Context.MODE_PRIVATE).getString(Constants.COOKIE, "");
            if (null != cookie && !"".equals(cookie)) {
                post.setHeader("Cookie", cookie);
                //Log.i("aaa", "cookie===" + cookie);
            }
            post.setHeader("Origin"," http://m.qzone.com");
            post.setHeader("Referer"," http://m.qzone.com/infocenter?&g_f=");


            post.setHeader("Accept-Language"," zh-CN,zh;q=0.8");
            post.setHeader("Accept-Encoding","gzip, deflate");
            post.setHeader("Accept" ,"*/*");
            post.setHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.132 Safari/537.36");
            post.setEntity(multipartEntity);
            multipartEntity = null;
            DefaultHttpClient client = new DefaultHttpClient();
            HttpParams httpParams = new BasicHttpParams();


            HttpResponse response = new DefaultHttpClient().execute(post);
            return EntityUtils.toString(response.getEntity(), "utf-8");


        }

        private MultipartEntity setEntityForLenMd5byMulti(String path, String res_uin, String sid) throws Exception {
            //Log.i("aaa", "开始解析图片");
            Object[] objects = getBase64Pic(path);
            //Log.i("aaa", "解析完成");
            String base = null;
            try {
                System.gc();
                base = Base64.encodeToString((byte[]) objects[0], Base64.DEFAULT);
            } catch (Exception e) {
                base = Base64.encodeToString((byte[]) objects[0], Base64.DEFAULT);
            }
//            Log.i("aaa", base);
            objects[0] = null;
            MultipartEntity entity = new MultipartEntity();
            entity.addPart("picture", new StringBody(base));
            entity.addPart("base64", new StringBody("1"));
            entity.addPart("hd_height", new StringBody("" + objects[2]));
            entity.addPart("hd_width", new StringBody("" + objects[1]));
            entity.addPart("hd_quality", new StringBody("90"));
            entity.addPart("output_type", new StringBody("json"));
            entity.addPart("preupload", new StringBody("1"));
            entity.addPart("charset", new StringBody("utf-8"));
            entity.addPart("output_charset", new StringBody("utf-8"));
            entity.addPart("logintype", new StringBody("sid"));
            entity.addPart("Exif_CameraMaker", new StringBody(""));
            entity.addPart("Exif_CameraModel", new StringBody(""));
            entity.addPart("Exif_Time", new StringBody(""));
            entity.addPart("uin", new StringBody(res_uin));
            entity.addPart("sid", new StringBody(sid));
            base = null;

            return entity;


        }

        /**
         * 建立httppost连接
         *
         * @param uploadImageUrl
         * @param urlEncodedFormEntity
         */
        private String uploadbyPost(String uploadImageUrl, HttpEntity urlEncodedFormEntity,boolean needOtherHander) throws Exception {

            HttpPost post = new HttpPost(uploadImageUrl);
            String cookie = getSharedPreferences(Constants.USERINFO, Context.MODE_PRIVATE).getString(Constants.COOKIE, "");
            if (null != cookie && !"".equals(cookie)) {
                post.setHeader("Cookie", cookie);
                //Log.i("aaa", "cookie===" + cookie);
            }
            
            if (needOtherHander) {
                post.setHeader("Origin"," http://m.qzone.com");
                post.setHeader("Referer"," http://m.qzone.com/infocenter?&g_f=");


                post.setHeader("Accept-Language"," zh-CN,zh;q=0.8");
                post.setHeader("Accept-Encoding","gzip, deflate");
                post.setHeader("Accept" ,"*/*");
                post.setHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.132 Safari/537.36");
            }
            post.setEntity(urlEncodedFormEntity);
  
      
            
            HttpParams se = new BasicHttpParams();
            ConnManagerParams.setTimeout(se, 60 * 1000);
            HttpConnectionParams.setConnectionTimeout(se, 60 * 1000);
            HttpConnectionParams.setSoTimeout(se, 60 * 1000);
            DefaultHttpClient client = new DefaultHttpClient(se);


            HttpResponse response = client.execute(post);
            return EntityUtils.toString(response.getEntity(), "gb2312");

        }

        /**
         * 设置参数对
         *
         * @param path
         * @param res_uin
         * @param sid
         */
        private UrlEncodedFormEntity setEntityForLenMd5(String path, String res_uin, String sid) throws Exception {
            //Log.i("aaa", "开始解析图片");
            Object[] objects = getBase64Pic(path);
            //Log.i("aaa", "解析完成");
            String base = Base64.encodeToString((byte[]) objects[0], Base64.DEFAULT);
            //Log.i("aaa", base);
            objects[0] = null;
            List<NameValuePair> lists = new ArrayList<NameValuePair>();
            lists.add(new BasicNameValuePair("picture", base));
            lists.add(new BasicNameValuePair("base64", "1"));
            lists.add(new BasicNameValuePair("hd_height", "" + (int) objects[2]));
            lists.add(new BasicNameValuePair("hd_width", "" + (int) objects[1]));
            lists.add(new BasicNameValuePair("hd_quality", "90"));
            lists.add(new BasicNameValuePair("output_type", "json"));
            lists.add(new BasicNameValuePair("preupload", "1"));
            lists.add(new BasicNameValuePair("charset", "utf-8"));
            lists.add(new BasicNameValuePair("output_charset", "utf-8"));
            lists.add(new BasicNameValuePair("logintype", "sid"));
            lists.add(new BasicNameValuePair("Exif_CameraMaker", ""));
            lists.add(new BasicNameValuePair("Exif_CameraModel", ""));
            lists.add(new BasicNameValuePair("Exif_Time", ""));
            lists.add(new BasicNameValuePair("uin", res_uin));
            lists.add(new BasicNameValuePair("sid", sid));
            base = null;
            return new UrlEncodedFormEntity(lists, "utf-8");
        }

        /**
         * 得到图片二进制文件
         *
         * @param path
         */
        private Object[] getBase64Pic(String path) throws Exception {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            StringBuilder sb = new StringBuilder();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = fis.read(bytes)) != -1) {
//                sb.append(Base64.decode(bytes, Base64.DEFAULT));
                bos.write(bytes, 0, len);
            }

            bos.flush();
            bos.close();
            fis.close();
            return new Object[]{bos.toByteArray(), options.outWidth, options.outHeight};
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            switch (values[0]) {
                case "0":
//                    pd.setMessage(values[1]);
                    lt.setText((values[1]));
                    break;
                case "1":
                    Toast.makeText(getApplicationContext(), values[1], Toast.LENGTH_SHORT).show();
                    break;
            }

        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);
//            pd.dismiss();
            
            
            switch (s) {
                case Code.UPLOAD_FAIL:
                    Toast.makeText(getApplicationContext(), "上传图片失败", Toast.LENGTH_SHORT).show();
                    lt.setText("上传图片失败");
                    MobclickAgent.onEvent(WriteAty.this, "PublishUploadPicsFail");
                    lt.error();
                    isFirstPic=true;
                    
                    break;
                case Code.Fail:
                    Toast.makeText(getApplicationContext(), "说说发表失败", Toast.LENGTH_SHORT).show();
                    lt.setText("说说发表失败");
                    lt.error();
                    isFirstPic=true;
                    MobclickAgent.onEvent(WriteAty.this, "PublishFail");
                    break;
                case Code.SUCCESS:
                    Toast.makeText(getApplicationContext(), "说说发表成功", Toast.LENGTH_SHORT).show();
                    lt.setText("说说发表成功");
                    lt.success();
                    MobclickAgent.onEvent(WriteAty.this, "PublishSuccess");
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2000);
                    break;
            }
        }


    }

}
