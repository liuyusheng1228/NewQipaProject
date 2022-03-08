package com.qipa.qipaimbase.utils.http;



import com.qipa.qipaimbase.chat.ichat.IChatModel;
import com.qipa.qipaimbase.utils.http.jsons.JsonAuth;
import com.qipa.qipaimbase.utils.http.jsons.JsonContactOnline;
import com.qipa.qipaimbase.utils.http.jsons.JsonContactRecent;
import com.qipa.qipaimbase.utils.http.jsons.JsonGetGroupIgnoreInfo;
import com.qipa.qipaimbase.utils.http.jsons.JsonGetIgnoreInfo;
import com.qipa.qipaimbase.utils.http.jsons.JsonGroupJoin;
import com.qipa.qipaimbase.utils.http.jsons.JsonGroupMembers;
import com.qipa.qipaimbase.utils.http.jsons.JsonGroupProfile;
import com.qipa.qipaimbase.utils.http.jsons.JsonGroups;
import com.qipa.qipaimbase.utils.http.jsons.JsonLogin;
import com.qipa.qipaimbase.utils.http.jsons.JsonMyInfo;
import com.qipa.qipaimbase.utils.http.jsons.JsonOtherInfo;
import com.qipa.qipaimbase.utils.http.jsons.JsonOtherInfoMulti;
import com.qipa.qipaimbase.utils.http.jsons.JsonRegist;
import com.qipa.qipaimbase.utils.http.jsons.JsonResult;
import com.qipa.qipaimbase.utils.http.jsons.JsonSaveGroupIgnoreInfo;
import com.qipa.qipaimbase.utils.http.jsons.JsonSaveIgnoreInfo;
import com.qipa.qipaimbase.utils.http.jsons.JsonSetNickName;
import com.qipa.qipaimbase.utils.http.jsons.JsonUploadImage;
import com.qipa.qipaimbase.utils.http.jsons.JsonUploadVoice;

import java.util.HashMap;
import java.util.Map;

public class HttpUtils {
    private static final HttpUtils ourInstance = new HttpUtils();

    public static HttpUtils getInstance() {
        return ourInstance;
    }

    private IHttpHelper iHttpHelper;

    private HttpUtils() {
        iHttpHelper = new OkHttpHelper();
    }

    public JsonResult login(String userName, String passWord) {
        Map<String, String> map = new HashMap<>();
        map.put("username", userName);
        map.put("password", passWord);
        return iHttpHelper.post(HttpContants.URL_LOGIN, map, JsonLogin.class);
    }

    public JsonResult getRecentUser(String sessionId, String userId) {
        Map<String, String> map = new HashMap<>();
        map.put("Cookie", String.format("sessionId=%s;userId=%s", sessionId, userId));
        return iHttpHelper.post(HttpContants.URL_RECENTUSER, (HashMap) null, map, JsonContactRecent.class);
    }

    public JsonResult regist(String userName, String pwd) {
        Map<String, String> map = new HashMap<>();
        map.put("username", userName);
        map.put("password", pwd);
        return iHttpHelper.post(HttpContants.URL_REGIST, map, JsonRegist.class);
    }

    public JsonResult setIgnoreStatus(String remoteId, boolean open, String sessionId, String userId) {
        Map<String, String> headMap = new HashMap<>();
        headMap.put("Cookie", String.format("sessionId=%s;userId=%s", sessionId, userId));
        Map<String, String> map = new HashMap<>();
        map.put("remoteid", remoteId);
        map.put("switch", open ? "0" : "1");//0（开启勿扰）1(关闭勿扰）
        return iHttpHelper.post(HttpContants.URL_SET_IGNORE, map, headMap, JsonSaveIgnoreInfo.class);
    }

    public JsonResult getOnLineUsers(String sessionId, String userId) {
        Map<String, String> map = new HashMap<>();
        map.put("Cookie", String.format("sessionId=%s;userId=%s", sessionId, userId));
        return iHttpHelper.post(HttpContants.URL_CONTACT_ONLINE, (HashMap) null, map, JsonContactOnline.class);
    }

    public JsonResult getAuth(String sessionId, String userId) {
        Map<String, String> map = new HashMap<>();
        map.put("Cookie", String.format("sessionId=%s;userId=%s", sessionId, userId));
        return iHttpHelper.post(HttpContants.URL_AUTH_, (HashMap) null, map, JsonAuth.class);
    }

    public Object logout() {
        return null;
    }

    public JsonResult changeNickName(String nickName, String sessionId, String userId) {
        Map<String, String> map = new HashMap<>();
        map.put("nickname", nickName);
        Map<String, String> headMap = new HashMap<>();
        headMap.put("Cookie", String.format("sessionId=%s;userId=%s", sessionId, userId));
        return iHttpHelper.post(HttpContants.URL_SET_NICKNAME, map, headMap, JsonSetNickName.class);
    }

    public JsonResult getOtherInfo(String sessionId, String id) {
        Map<String, String> map = new HashMap<>();
        map.put("remoteid", id);
        Map<String, String> headMap = new HashMap<>();
        headMap.put("Cookie", String.format("sessionId=%s", sessionId));
        return iHttpHelper.post(HttpContants.URL_OTHER_INFO, null, map, headMap, JsonOtherInfo.class);
    }

    public Object getMyInfo(String sessionId, String userId) {
        Map<String, String> map = new HashMap<>();
        map.put("Cookie", String.format("sessionId=%s;userId=%s", sessionId, userId));
        return iHttpHelper.post(HttpContants.URL_My_INFO, (HashMap) null, map, JsonMyInfo.class);
    }

    public Object sendPic(String absolutePath, String sessionId, String userId) {
        Map<String, String> map = new HashMap<>();
        map.put("fileUpload", absolutePath);
        Map<String, String> headMap = new HashMap<>();
        headMap.put("Cookie", String.format("sessionId=%s;userId=%s", sessionId, userId));
        return iHttpHelper.post(HttpContants.URL_UPLOAD_IMG_, absolutePath, map, headMap, JsonUploadImage.class);
    }

    public Object sendVoiceFile(String absolutePath, String sessionId, String userId) {
        Map<String, String> map = new HashMap<>();
        map.put("fileUpload", absolutePath);
        Map<String, String> headMap = new HashMap<>();
        headMap.put("Cookie", String.format("sessionId=%s;userId=%s", sessionId, userId));
        return iHttpHelper.post(HttpContants.URL_UPLOAD_AUDIO_, absolutePath, map, headMap, JsonUploadVoice.class);
    }

    public Object getOthersInfo(String[] otherId, String sessionId, String userId) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < otherId.length; i++) {
            if (i != 0) {
                buffer.append(",");
            }
            buffer.append(otherId[i]);
        }
        Map<String, String> map = new HashMap<>();
        map.put("remoteids", buffer.toString());
        Map<String, String> headMap = new HashMap<>();
        headMap.put("Cookie", String.format("sessionId=%s;userId=%s", sessionId, userId));
        return iHttpHelper.post(HttpContants.URL_OTHERS_INFO_MULTI, map, headMap, JsonOtherInfoMulti.class);
    }

    public Object getFile(String fileUrl, String saveFileUrl, IChatModel.OnGetFileListener onGetFileListener) {
        return iHttpHelper.getFile(fileUrl, saveFileUrl, onGetFileListener);
    }

    public Object getIgnoreStatus(String sessionId, String userId, String remoteId) {
        Map<String, String> headMap = new HashMap<>();
        headMap.put("Cookie", String.format("sessionId=%s;userId=%s", sessionId, userId));
        Map<String, String> map = new HashMap<>();
        map.put("remoteid", remoteId);
        return iHttpHelper.post(HttpContants.URL_GET_IGNORE_INFO_, map, headMap, JsonGetIgnoreInfo.class);
    }

    // 群
    public JsonResult getGroupIgnoreStatus(String sessionId, String userId, String gid) {
        Map<String,String> headMap = new HashMap<>();
        headMap.put("Cookie",String.format("sessionId=%s;userId=%s",sessionId,userId));
        Map<String,String> map = new HashMap<>();
        map.put("gid",gid);

        return iHttpHelper.post(HttpContants.URL_GROUP_GET_IGNORE_INFO_,map,headMap, JsonGetGroupIgnoreInfo.class);
    }
//    public static final String URL_GROUP_MEMBERS = DEFAULT_API_HOST+"/group/remote/members";
//    public static final String URL_GROUP_OTHER_INFO = DEFAULT_API_HOST+"/group/remote/profile";
//    public static final String URL_GROUP_JOIN = DEFAULT_API_HOST+"/group/remote/join";
//    public static final String URL_GROUP_LIST = DEFAULT_API_HOST+"/contact/groups";
//
public JsonResult setGroupIgnoreStatus(String sessionId, String userId, String gid, int switchX) {
        Map<String,String> headMap = new HashMap<>();
        headMap.put("Cookie",String.format("sessionId=%s;userId=%s",sessionId,userId));
        Map<String,String> map = new HashMap<>();
        map.put("gid",gid);
    map.put("switch", switchX + "");
        return iHttpHelper.post(HttpContants.URL_GROUP_SET_IGNORE,map,headMap, JsonSaveGroupIgnoreInfo.class);
    }

    public JsonResult getGroupMembers(String sessionId, String userId, String gid) {
        Map<String, String> headMap = new HashMap<>();
        headMap.put("Cookie", String.format("sessionId=%s;userId=%s", sessionId, userId));
        Map<String, String> map = new HashMap<>();
        map.put("gid", gid);
        return iHttpHelper.post(HttpContants.URL_GROUP_MEMBERS, map, headMap, JsonGroupMembers.class);
    }

    public JsonResult getGroupProfile(String sessionId, String userId, String gid) {
        Map<String, String> headMap = new HashMap<>();
        headMap.put("Cookie", String.format("sessionId=%s;userId=%s", sessionId, userId));
        Map<String, String> map = new HashMap<>();
        map.put("gid", gid);
        return iHttpHelper.post(HttpContants.URL_GROUP_PROFILE, map, headMap, JsonGroupProfile.class);
    }

    public JsonResult joinGroup(String sessionId, String userId, String gid) {
        Map<String, String> headMap = new HashMap<>();
        headMap.put("Cookie", String.format("sessionId=%s;userId=%s", sessionId, userId));
        Map<String, String> map = new HashMap<>();
        map.put("gid", gid);
        return iHttpHelper.post(HttpContants.URL_GROUP_JOIN, map, headMap, JsonGroupJoin.class);
    }

    public JsonResult getGroups(String sessionId, String userId) {
        Map<String, String> headMap = new HashMap<>();
        headMap.put("Cookie", String.format("sessionId=%s;userId=%s", sessionId, userId));
        return iHttpHelper.post(HttpContants.URL_GROUP_LIST, (HashMap) null, headMap, JsonGroups.class);
    }

}
