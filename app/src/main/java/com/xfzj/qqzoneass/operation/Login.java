package com.xfzj.qqzoneass.operation;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.xfzj.qqzoneass.config.Config;
import com.xfzj.qqzoneass.model.Weather;
import com.xfzj.qqzoneass.net.Login_sig_Net;
import com.xfzj.qqzoneass.net.Result_Get_Net;
import com.xfzj.qqzoneass.utils.UrlUtils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zj on 2015/6/29.
 */
public class Login {
    private static final String TAG="Login";
    private static final boolean DEBUG=true;
    private WebView webView;
    private Context context;
    public String pt_login_sig;
    private String pt_verifysession_v1;
    private String verify;
    private String number;
    private String password;
    private SuccessCallback successCallback;
    private FailCallback failCallback;
    private String aid = "", sig = "";
    private String ptcz;
    private boolean isVerifyLogin = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String password1 = bundle.getString("p", "");
            String number1 = bundle.getString("n", "");
            String verify1 = bundle.getString("v", "");
//            Log.i("aaa", password1 + number1 + verify1);
            webView.loadUrl("javascript:getEncryption(\"" + password1 + "\",\"" + number1 + "\",\"" + verify1 + "\",false)");
        }
    };

    public Login(Context context) {
        this.context = context;
        webView = new WebView(context);
        webView.loadUrl("file:///android_asset/html.html");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyObject(), "myObject");
        pt_login_sig = getPt_login_sig();
        

    }

    /**
     * 开始登陆
     *
     * @param number          QQ号
     * @param password        QQ密码
     * @param successCallback
     * @param failCallback
     */
    public void beginLogin(final String number, final String password, SuccessCallback successCallback, FailCallback failCallback, final VerifyCallback verifyCallback) {
        this.number = number;
        this.password = password;
        this.successCallback = successCallback;
        this.failCallback = failCallback;
        //如果pt_login_sig不为空，则获取是否需要验证码，否则再次获取pt_login_sig
        if (null != pt_login_sig && !"".equals(pt_login_sig)) {
            //todo debug
            String url = UrlUtils.CHECK1_URL + number + UrlUtils.CHECK2_URL ;//+ pt_login_sig + UrlUtils.CHECK3_URL;
            new Result_Get_Net(context, url, new Result_Get_Net.SuccessCallback() {
                @Override
                public void onSuccess(HttpResponse response) {

                }

                @Override
                public void onSuccess(String result) {
                    //Log.i("aaa", result);
                    String isNeed = result.substring(result.indexOf("('") + 2, result.indexOf("',"));
                    verify = result.substring(result.indexOf(",'") + 2);
                    verify = verify.substring(0, verify.indexOf("',"));
                    pt_verifysession_v1 = result.split("','")[3];


                    //Log.i("aaa", isNeed + "\n" + verify + "\n" + pt_verifysession_v1);
                    //如果isNeeds是0，则后面的verify就是验证码，否则verify就是获取验证码的参数  
                    if ("0".equals(isNeed)) {
                        //直接登陆
                        webView.loadUrl("javascript:getEncryption(\"" + password + "\",\"" + number + "\",\"" + verify + "\",false)");

                    } else {

                        //获取验证码图片，显示要输入验证码的界面，输入完验证码之后才能登陆
                        //因为目前无法获取到需要验证码的情况，故将需要验证码的信息写到文件中，以备后续需要
                        isVerifyLogin = true;
                        String LoadVerifyUrl = UrlUtils.LOAD_VERIFY1_URL + number + UrlUtils.LOAD_VERIFY2_URL + verify;
                        new Result_Get_Net(context, LoadVerifyUrl, new Result_Get_Net.SuccessCallback() {
                            @Override
                            public void onSuccess(String result) {
                                //Log.i("aaa", "result=" + result);
                                //获取其中的aid信息

                                Pattern pAid = Pattern.compile("(aid=)\\w*(&)");
                                Matcher mAid = pAid.matcher(result);
                                if (mAid.find()) {
                                    aid = mAid.group().substring(4, mAid.group().length() - 1);
                                }
                                //在获取sig信息
                                Pattern pSig = Pattern.compile("(g_click_cap_sig=)\\S*(\")");
                                Matcher mSig = pSig.matcher(result);
                                if (mSig.find()) {
                                    String matResult = mSig.group();
                                    sig = matResult.substring(matResult.indexOf("g_click_cap_sig=") + 17, matResult.length() - 1);
                                }
                                //开始获取验证码图片
                                //Log.i("aaa", "aid=" + aid + "sig=" + sig);
                                String Verify = UrlUtils.VERIFY1_URL + "549000929" + UrlUtils.VERIFY2_URL + number + UrlUtils.VERIFY3_URL + sig;
                                if (verifyCallback != null) {
                                    //Log.i("aaa", "verifyCallback");
                                    verifyCallback.onShowVerify(Verify);

                                }


                            }

                            @Override
                            public void onSuccess(HttpResponse response) {

                            }
                        }, new Result_Get_Net.FailCallback() {
                            @Override
                            public void onFail() {

                            }
                        });


                    }


                }
            }, new Result_Get_Net.FailCallback() {
                @Override
                public void onFail() {

                }
            });

        } else {
            getPt_login_sig();


        }


    }

    public void VerifyLogin(final String number, final String password, final String verify1) {
        String url = UrlUtils.SUB_VERIFY1_URL + aid + UrlUtils.SUB_VERIFY2_URL + number + UrlUtils.SUB_VERIFY3_URL + verify1 + UrlUtils.SUB_VERIFY4_URL + sig;
        new Result_Get_Net(context, url, new Result_Get_Net.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                //Log.i("aaa", result);
                Pattern pattern = Pattern.compile("(randstr:)\\S*(,)");
                Matcher match = pattern.matcher(result);
                if (match.find()) {
                    String str = match.group().substring(9, match.group().length() - 2);
                    String randstr = str.substring(0, str.indexOf("\",sig"));
                    String sig = str.substring(str.indexOf("sig:") + 5);
                    //Log.i("aaa", "randstr=" + randstr);
                    //Log.i("aaa", "sig=" + sig);
                    verify = randstr;
                    pt_verifysession_v1 = sig;
                    webView.loadUrl("javascript:getEncryption(\"" + password + "\",\"" + number + "\",\"" + randstr + "\",false)");


                }
            }

            @Override
            public void onSuccess(HttpResponse response) {

            }
        }, new Result_Get_Net.FailCallback() {
            @Override
            public void onFail() {

            }
        });

    }

    /**
     * 正式的登陆操作
     *
     * @param url 登陆的url
     */
    public void loginNow(String url) {
        //Log.i("aaa", "url!=" + url);
        new Result_Get_Net(context, url, new Result_Get_Net.SuccessCallback() {
            @Override
            public void onSuccess(HttpResponse response) {
                Header[] headers = response.getHeaders("Set-Cookie");
                StringBuffer sb = new StringBuffer();
                for (Header header : headers) {
                    sb.append(header.getValue());
                }

                Header[] headers1 = response.getAllHeaders();

                for (Header header : headers1) {
                   // Log.i("bbb", header.getName() + "==" + header.getValue());
                }
                //Log.i("aaa", "getCookie====" + sb.toString());
                String cookie = sb.toString();
                if (cookie.contains("skey") && cookie.contains("uin")&&cookie.contains("ptcz")) {
                    String skey = cookie.substring(cookie.indexOf("skey=") + 5);
                    skey = skey.substring(0, skey.indexOf(";"));
                    String uin = cookie.substring(cookie.indexOf("uin="));
                    uin = uin.substring(0, uin.indexOf(";"));
                    ptcz = cookie.substring(cookie.indexOf("ptcz="));
                    ptcz = ptcz.substring(0, ptcz.indexOf(";"));
//                    String p_skey = cookie.substring(cookie.indexOf("p_skey="));
//                    p_skey = p_skey.substring(0, p_skey.indexOf(";"));
//                    String pt4_token = cookie.substring(cookie.indexOf("pt4_token="));
//                    pt4_token = pt4_token.substring(0, pt4_token.indexOf(";"));
                    Config.saveUserInfo(context, null, null, uin + "; skey=" + skey + "; " + ptcz + "; ", skey, null,null);
                }
            }

            @Override
            public void onSuccess(String result) {
                Log.i("aaa", result);
                //如果返回的结果中包含0，则说明登陆成功，否则登陆失败
                if (result.split(",")[0].contains("0")) {
                    String str=result.split(",")[5];
                    String name=str.substring(str.indexOf("'")+1,str.lastIndexOf("'"));
                    Config.saveWeatherInfo(context,new Weather(null,null,null,null,name));

                   String  key = result.split("','")[2];
//                Log.i("aaa", result);
//                Config.saveHomeUrl(getApplicationContext(), result);
                Log.i("aaa", "获取地址="+key);
                    getsID(key,ptcz);
                    if (successCallback != null) {
                        successCallback.onSuccess(result);
                    }

                } else {
                    if (failCallback != null) {
                        failCallback.onFail();
                    }

                }
            }
        }, new Result_Get_Net.FailCallback() {
            @Override
            public void onFail() {

            }
        });

    }

    /**
     * 获取发表说说需要的sid
     *
     * @param result
     */
    private void getsID(String result,final String ptcz) {

        new Result_Get_Net(context, result, new Result_Get_Net.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onSuccess(HttpResponse response) {
               // Log.i("aaa","get id&pttoken");
                Header[] headers = response.getHeaders("Set-Cookie");
                StringBuffer sb = new StringBuffer();
                for (Header header : headers) {
                    sb.append(header.getValue());
                }

                Header[] headers1 = response.getAllHeaders();

                for (Header header : headers1) {
                   // Log.i("bbb", header.getName() + "==" + header.getValue());
                }
                //Log.i("aaa", "getCookie====" + sb.toString());
                String cookie = sb.toString();
                if (cookie.contains("skey") && cookie.contains("uin")&&cookie.contains("p_skey")&&cookie.contains("pt4_token")) {
                    String skey = cookie.substring(cookie.indexOf("skey=") + 5);
                    skey = skey.substring(0, skey.indexOf(";"));
                    String uin = cookie.substring(cookie.indexOf("uin="));
                    uin = uin.substring(0, uin.indexOf(";"));
                    
                    String p_skey = cookie.substring(cookie.indexOf("p_skey="));
                    p_skey = p_skey.substring(0, p_skey.indexOf(";"));
                    String pt4_token = cookie.substring(cookie.indexOf("pt4_token="));
                    pt4_token = pt4_token.substring(0, pt4_token.indexOf(";"));
                    Config.saveUserInfo(context, null, null, uin + "; skey=" + skey + "; " + ptcz + "; " + p_skey + "; " + pt4_token+"; ", skey, null,p_skey.substring(p_skey.indexOf("p_skey=")+7));
                }
            }
        }, new Result_Get_Net.FailCallback() {
            @Override
            public void onFail() {


            }
        });
    }
    public interface SuccessCallback {
        void onSuccess(String result);
    }

    public interface FailCallback {
        void onFail();
    }

    public interface VerifyCallback {
        void onShowVerify(String verify);
    }

    private class MyObject {
        @JavascriptInterface
        public void say(String MD5) {
//            Toast.makeText(context, MD5, Toast.LENGTH_SHORT).show();
            String url = UrlUtils.LOGIN1_URL + number + UrlUtils.LOGIN2_URL + verify + UrlUtils.LOGIN3_URL + pt_verifysession_v1 +
                    UrlUtils.LOGIN4_URL + MD5 + UrlUtils.LOGIN5_URL ;//+ pt_login_sig + UrlUtils.LOGIN6_URL;
            //todo  debug
            if (isVerifyLogin) {
                url = url.replace("pt_vcode_v1=0", "pt_vcode_v1=1");
                //todo debug
//                url = url.replace("pt_vcode_v1=0&", "");
                
            }
            loginNow(url);
        }


    }

    /**
     * 获得登陆时需要的pt_login_sig
     *
     * @return
     */
    public String getPt_login_sig() {



        return getPt_login_sig(null);
    }

    public String getPt_login_sig( final Login_Sig_CallBack back) {
        new Login_sig_Net(context, UrlUtils.LOGIN_SIG__URL, new Login_sig_Net.SuccessCallback() {


            @Override
            public void onSuccess(String result) {
//                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                pt_login_sig = result;
                if (back != null) {
                    back.onSigGetSucc(pt_login_sig);
                }
            }
        }, new Login_sig_Net.FailCallback() {
            @Override
            public void onFail() {
            }
        });
       return  pt_login_sig;
    }
    
    public interface Login_Sig_CallBack{
        void onSigGetSucc(String sig);
    }
}
