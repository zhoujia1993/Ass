package com.xfzj.qqzoneass.net;


import android.content.Context;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

/**
 * Created by zj on 2015/6/27.
 */
public class Login_sig_Net {

    public Login_sig_Net(final Context context,final String url, final SuccessCallback successCallback, final FailCallback failCallback) {
        new NetConnection( context,url, null, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(HttpResponse response) {
//                Log.i("aaa", "运行了123");
                String pt_login_sig = null;
                Header[] headers = response.getHeaders("Set-Cookie");
                for (Header header : headers) {
                    if (header.getValue().contains("pt_login_sig")) {
                        pt_login_sig = header.getValue();
//							System.out.println(pt_login_sig);
                        pt_login_sig = pt_login_sig.substring(
                                pt_login_sig.indexOf("pt_login_sig=") + 13,
                                pt_login_sig.indexOf(";"));
//							System.out.println(pt_login_sig);
//                        Log.i("aaa",pt_login_sig);
                        if (successCallback != null) {
                            successCallback.onSuccess(pt_login_sig);
                        }


                    } else {
                        if (failCallback != null) {
                            failCallback.onFail();
                        }

                    }
                }


            }

            @Override
            public void onSuccess(String result) {
                
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail() {

            }
        }, "GET") {

        };


    }


    public interface SuccessCallback {
         void onSuccess(String result);

    }


    public  interface FailCallback {
         void onFail();
    }

}
