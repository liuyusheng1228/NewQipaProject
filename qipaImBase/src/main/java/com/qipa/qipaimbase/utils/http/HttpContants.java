package com.qipa.qipaimbase.utils.http;

public class HttpContants {
    private static final String DEFAULT_API_HOST = "https://cosmos-im-demo.immomo.com/photonimdemo";
//    private static final String DEFAULT_API_HOST = "https://cosmos-im-demo-hw.immomo.com/photonimdemo";

    //    private static final String DEFAULT_API_HOST = "https://140.210.71.254/photonimdemo";

    public static final String URL_OTHERS_INFO_MULTI = DEFAULT_API_HOST+"/user/remote/profiles";
    public static final String URL_CONTACT_ONLINE = DEFAULT_API_HOST+"/contact/onlineUser";
    public static final String URL_RECENTUSER = DEFAULT_API_HOST+"/contact/recentUser";
    public static final String URL_SET_NICKNAME = DEFAULT_API_HOST+"/user/my/editProfile";
    public static final String URL_My_INFO = DEFAULT_API_HOST+"/user/my/profile";
    public static final String URL_OTHER_INFO = DEFAULT_API_HOST+"/user/remote/profile";
    public static final String URL_REGIST = DEFAULT_API_HOST+"/core/register/index";
    public static final String URL_LOGIN =DEFAULT_API_HOST+"/core/login/index";
    public static final String URL_AUTH_ = DEFAULT_API_HOST+"/imsdk/client/getAuthInfo";
    public static final String URL_UPLOAD_IMG_ = DEFAULT_API_HOST+"/upload/chatimg";
    public static final String URL_UPLOAD_AUDIO_ = DEFAULT_API_HOST+"/upload/chataudio";
    public static final String URL_GET_IGNORE_INFO_ = DEFAULT_API_HOST+"/setting/msg/getP2pRemind";
    public static final String URL_SET_IGNORE = DEFAULT_API_HOST+"/setting/msg/setP2pRemind";

    // 群
    public static final String URL_GROUP_GET_IGNORE_INFO_ = DEFAULT_API_HOST+"/setting/msg/getP2GRemind";
    public static final String URL_GROUP_SET_IGNORE = DEFAULT_API_HOST+"/setting/msg/setP2GRemind";
    public static final String URL_GROUP_MEMBERS = DEFAULT_API_HOST+"/group/remote/members";
    public static final String URL_GROUP_PROFILE = DEFAULT_API_HOST + "/group/remote/profile";
    public static final String URL_GROUP_JOIN = DEFAULT_API_HOST+"/group/remote/join";
    public static final String URL_GROUP_LIST = DEFAULT_API_HOST+"/contact/groups";

}
