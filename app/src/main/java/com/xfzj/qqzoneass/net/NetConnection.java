package com.xfzj.qqzoneass.net;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.xfzj.qqzoneass.utils.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by zj on 2015/6/27.
 */
public class NetConnection {
    public NetConnection(final Context context, final String url, final UrlEncodedFormEntity params, final SuccessCallback successCallback, final FailCallback failCallback, final String method) {
        //异步处理
        new AsyncTask<Void, Void, List<Object>>
                () {

            @Override
            protected List<Object> doInBackground(Void... param) {

                if ("POST".equals(method)) {


                    try {
                        //执行post方式
                        HttpResponse response = getDatabyPost(context, url, params);
                        List<Object> lists = new ArrayList<>();
                        lists.add(response);
                        lists.add(EntityUtils.toString(response.getEntity()));
                        return lists;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    //执行get方式
                    try {
                        HttpResponse httpResponse = getDatabyGet(url, context);
                        List<Object> lists = new ArrayList<>();
                        lists.add(httpResponse);
                        String result=EntityUtils.toString(httpResponse.getEntity());
                        lists.add(result);
                        Log.d("aaa", url + result);
                        return lists;


                    } catch (Exception e) {
                        e.printStackTrace();
                        if (failCallback != null) {
                            failCallback.onFail();
                        }


                    }


                }


                return null;
            }

            @Override
            protected void onPostExecute(List<Object> lists) {

                try {
                    HttpResponse response = (HttpResponse) lists.get(0);
                    String result = (String) lists.get(1);
                    if (null != response) {
//                        Log.i("aaa", "response执行");
                        if (successCallback != null) {
                            successCallback.onSuccess(response);
                            
                        }
//                        Log.i("aaa", "response执行执行");
                    }
//                    Log.i("aaa", "result不执行="+result);
                    if (null != result && !"".equals(result)) {
                        if (successCallback != null) {
                            successCallback.onSuccess(result);
                        }
                    } else {
                        if (failCallback != null) {
                            failCallback.onFail();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (failCallback != null) {
                        failCallback.onFail();
                    }
                }


            }
        }.execute();


    }

    private HttpResponse getDatabyPost(Context context, String url, UrlEncodedFormEntity params) throws Exception {
        HttpPost post = new HttpPost(url);

        String cookie = context.getSharedPreferences(Constants.USERINFO, Context.MODE_PRIVATE).getString(Constants.COOKIE, "");
        if (null != cookie && !"".equals(cookie)) {
            post.setHeader("Cookie", cookie);
//            Log.i("aaa", "cookie===" + cookie);
        }
        post.setEntity(params);
        HttpResponse response = new DefaultHttpClient().execute(post);

        if (response.getStatusLine().getStatusCode() == 200) {
            return response;
        }
        return null;

    }

    /**
     * 通过get方式去获取数据
     *
     * @param url
     * @param context
     */

    private HttpResponse getDatabyGet(String url, Context context) throws Exception {
        Log.i("aaa", "运行了");
        HttpGet get = new HttpGet(url);
        String cookie = context.getSharedPreferences(Constants.USERINFO, Context.MODE_PRIVATE).getString(Constants.COOKIE, "");
        if (null != cookie && !"".equals(cookie)) {
            get.setHeader("Cookie", cookie);
            Log.i("aaa", "cookie===" + cookie);
        }
        DefaultHttpClient client = new DefaultHttpClient();
        client.setRedirectHandler(new RedirectHandler() {

            @Override
            public boolean isRedirectRequested(HttpResponse response,
                                               HttpContext context) {
                return false;
            }

            @Override
            public URI getLocationURI(HttpResponse response, HttpContext context)
                    throws ProtocolException {
                return null;
            }
        });
        HttpResponse httpResponse = client.execute(get);
//        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            return httpResponse;
//        }
//        return null;
    }


    public interface SuccessCallback {
        void onSuccess(HttpResponse response);

        void onSuccess(String result);

    }


    public interface FailCallback {
        void onFail();
    }


}
