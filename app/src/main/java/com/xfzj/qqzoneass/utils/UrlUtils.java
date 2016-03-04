package com.xfzj.qqzoneass.utils;

/**
 * Created by zj on 2015/6/27.
 */
public class UrlUtils {
    /**
     * 用来获取登陆时获取验证码所需要的pt_login_sig
     */
    public static final String LOGIN_SIG__URL = "http://xui.ptlogin2.qq.com/cgi-bin/xlogin?proxy_url=http%3A//qzs.qq.com/qzone/" +
            "v6/portal/proxy.html&daid=5&pt_qzone_sig=1&hide_title_bar=1&low_login=0&qlogin_auto_login=1&no_verifyimg=1" +
            "&link_target=blank&appid=549000912&style=22&target=self&s_url=http%3A//qzs.qq.com/qzone/v5/loginsucc.html?para=izone" +
            "&pt_qr_app=%E6%89%8B%E6%9C%BAQQ%E7%A9%BA%E9%97%B4&pt_qr_link=http%3A//z.qzone.com/download.html&self_regurl=http%3A//qzs.qq.com/qzone/v6/reg/index.html" +
            "&pt_qr_help_link=http%3A//z.qzone.com/download.html";
    /**
     * 用来获取验证码的url的第一部分，后面接QQ号
     */

//    public static final String CHECK1_URL = "http://check.ptlogin2.qq.com/check?regmaster=&pt_tea=1&pt_vcode=1&uin=";
    public static final String CHECK1_URL = "http://check.ptlogin2.qzone.com/check?pt_tea=1&uin=";

    /**
     * 用来获取验证码的url的第一部分，后面接pt_login_sig
     */
//    public static final String CHECK2_URL = "&appid=549000912&js_ver=10126&js_type=1&login_sig=";
    public static final String CHECK2_URL = "&appid=549000929&ptlang=2052&r=0.32109533972106874";
    /**
     * 用来获取验证码的url的第三部分。
     */
    public static final String CHECK3_URL = "&u1=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&r=0.34318738523870707";
    /**
     * 登陆时的url的第一部分，后面接QQ号
     */
    public static final String LOGIN1_URL = "http://ptlogin2.qzone.com/login?u=";
    /**
     * 登陆时的url的第二部分，后面接验证码
     */
    public static final String LOGIN2_URL = "&verifycode=";
    /**
     * 登陆时的url的第三部分，后面接获取到验证码的pt_verifysession_v1值
     */
    public static final String LOGIN3_URL = "&pt_vcode_v1=0&pt_verifysession_v1=";
    /**
     * 登陆时的url的第四部分，后面接密码的MD5
     */
    public static final String LOGIN4_URL = "&p=";
    /**
     * 登陆时的url的第五部分，后面接login_sig
     */
//    public static final String LOGIN5_URL = "&pt_randsalt=0&u1=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&ptredirect=0&h=1&t=1&g=1" +
//            "&from_ui=1&ptlang=2052&action=3-15-1435544798406&js_ver=10127&js_type=1&login_sig=";
    public static final String LOGIN5_URL = "&pt_randsalt=0&ptlang=2052&low_login_enable=0&u1=http%3A%2F%2Fm.qzone.com%2Finfocenter%3Fg_f%3D&from_ui=1&fp=loginerroralert&device=2&aid=549000929&pt_ttype=1&pt_3rd_aid=0&daid=147&ptredirect=1&h=1&g=1&pt_uistyle=9&";
    /**
     * 登陆时的url的第六部分。
     */
//    public static final String LOGIN6_URL = "&pt_uistyle=32&aid=549000912&daid=5&pt_qzone_sig=1&";
    /**
     * 获取头像的url
     * 例子：http://qlogo4.store.qq.com/qzone/627252161/627252161/30
     */

    public static final String HEADERIMAGE_URL = "http://qlogo4.store.qq.com/qzone/";


    /**
     * 加载说说url的第一部分，后面接QQ号
     */

    public static final String LOAD_SHUOSHUO1_URL = "http://ic2.s51.qzone.qq.com/cgi-bin/feeds/feeds3_html_more?uin=";
    /**
     * 加载说说url的第二部分，后面接gtk
     */
    public static final String LOAD_SHUOSHUO2_URL = "&scope=0&view=1&daylist=&uinlist=&gid=&flag=1&filter=all&applist=all&refresh=0" +
            "&aisortEndTime=0&aisortOffset=0&getAisort=0&aisortBeginTime=0&pagenum=1&externparam=&firstGetGroup=0" +
            "&icServerTime=0&mixnocache=0&scene=0&begintime=0&count=10&dayspac=0&sidomain=cm.qzonestyle.gtimg.cn&useutf8=1" +
            "&outputhtmlfeed=1&rd=0.14012064069146007&getob=1&g_tk=";


    /**
     * 加载含有验证码网页url的第一部分，后面接QQ号
     */
    public static final String LOAD_VERIFY1_URL = "http://captcha.qq.com/cap_union_show?clientype=3&lang=2052&uin=";
    /**
     * 加载含有验证码网页url的第二部分，后面接返回的一串验证码信息
     */
    public static final String LOAD_VERIFY2_URL = "&aid=549000929&cap_cd=";

    /**
     * 加载验证码图片的第一部分，后面加上aid
     */
    public static final String VERIFY1_URL = "http://captcha.qq.com/getimgbysig?aid=";
    /**
     * 加载验证码图片的第二部分，后面加上QQ号
     */

    public static final String VERIFY2_URL = "&uin=";
    /**
     * 加载验证码图片的第三部分，后面加上sig.
     */
    public static final String VERIFY3_URL = "&sig=";
    /**
     * 提交验证码的Url的第一部分，后面加上aid
     */
    public static final String SUB_VERIFY1_URL = "http://captcha.qq.com/cap_union_verify?aid=";

    /**
     * 提交验证码的Url的第二部分，后面加上QQ号
     */
    public static final String SUB_VERIFY2_URL = "&uin=";
    /**
     * 提交验证码的Url的第三部分，后面加上验证码
     */
    public static final String SUB_VERIFY3_URL = "&captype=2&ans=";
    /**
     * 提交验证码的Url的第四部分，后面加上sig.
     */

    public static final String SUB_VERIFY4_URL = "&sig=";
    /**
     * 发表说说的url。
     */
    public static final String PUBLISH_URL = "http://m.qzone.com/mood/publish_mood?g_tk=";

    /**
     * 获取法宝说说需要的sid
     */
    public static final String SID_URL = "http://m.qzone.com/infocenter?g_f";

    /**
     * 上传图片
     */
    public static final String UPLOAD_IMAGE = "http://up.qzone.com/cgi-bin/upload/cgi_upload_pic_v2";

    /**
     * 进入好友空间的url的第一部分，后面加上gtk,
     */
    public static final String FRIEND1_URL = " http://m.qzone.com/infocenter?g_f=#";
    /**
     * 进入好友空间的url的第二部分，后面加上好友qq号,
     */
    public static final String FRIEND2_URL = "/mine?starttime=";
    /**
     * 进入好友空间的url的第三部分，后面加上sid.
     */
    public static final String FRIEND3_URL = "&action=1&g_f=&refresh_type=1&res_type=2&format=json&sid=";


    /**
     * 获取空间的访问人数及访客情况的url的第一部分，后面加上QQ号，
     */
    public static  final String VISITOR1_URL="http://g.qzone.qq.com/cgi-bin/friendshow/cgi_get_visitor_more?uin=";
    /**
     * 获取空间的访问人数及访客情况的url的第二部分，后面加上gtk.
     */
    public static  final String VISITOR2_URL="&mask=7&page=1&g_tk=";
}
