package com.xfzj.qqzoneass.net;

import android.content.Context;

import org.apache.http.HttpResponse;

/**
 * Created by zj on 2015/6/29.
 */
public class Result_Get_Net {
    public Result_Get_Net(final Context context,String url, final SuccessCallback successCallback, final FailCallback failCallback) {
        new NetConnection(context,url, null, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(HttpResponse response) {
                if (successCallback != null) {
                    successCallback.onSuccess(response);
                }

            }

            @Override
            public void onSuccess(String result) {
//                Log.i("aaa", "result====" + result);
                if (successCallback != null) {
                    
                    successCallback.onSuccess(result);
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail() {
                if (failCallback != null) {
                    failCallback.onFail();
                }

            }
        }, "GET");


    }

    public interface SuccessCallback {
        void onSuccess(String result);

        void onSuccess(HttpResponse response);
    }


    public interface FailCallback {
        void onFail();
    }

}
