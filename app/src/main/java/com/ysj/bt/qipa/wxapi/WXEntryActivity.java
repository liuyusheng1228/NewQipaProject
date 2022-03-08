package com.ysj.bt.qipa.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.qipa.newboxproject.R;
import com.qipa.newboxproject.app.util.Constants;
import com.qipa.newboxproject.app.util.NetworkUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.ysj.bt.qipa.SharedPreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	private static String TAG = "MicroMsg.WXEntryActivity";

    private IWXAPI api;
    private MyHandler handler;

	private static class MyHandler extends Handler {
		private final WeakReference<WXEntryActivity> wxEntryActivityWeakReference;

		public MyHandler(WXEntryActivity wxEntryActivity){
			wxEntryActivityWeakReference = new WeakReference<WXEntryActivity>(wxEntryActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			int tag = msg.what;
			switch (tag) {
				case NetworkUtil.GET_TOKEN: {
					Bundle data = msg.getData();
					JSONObject json = null;
					try {
						json = new JSONObject(data.getString("result"));
						String openId, accessToken, refreshToken, scope;
						openId = json.getString("openid");
						accessToken = json.getString("access_token");
						refreshToken = json.getString("refresh_token");
						scope = json.getString("scope");
					} catch (JSONException e) {
						Log.e(TAG, e.getMessage());
					}
				}
			}
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
		handler = new MyHandler(this);

        try {
            Intent intent = getIntent();
        	api.handleIntent(intent, this);
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
		switch (req.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:

			break;
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:

			break;
		default:
			break;
		}
        finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		int result = 0;
		Log.i("Api","resp:"+resp.getType()+resp.errCode);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			String msg="";
			if ("0".equals(resp.errCode + "")) {
//				"支付成功"
			} else if ("-1".equals(resp.errCode + "")) {
//				"支付失败"
			} else if ("-2".equals(resp.errCode + "")) {
//				"用户取消支付"
			}

		}
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			String code = ((SendAuth.Resp) resp).code;
			getAccessToken(code);
			result = R.string.errcode_success;
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.errcode_cancel;
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = R.string.errcode_deny;
			break;
		case BaseResp.ErrCode.ERR_UNSUPPORT:
			result = R.string.errcode_unsupported;
			break;
		default:
			result = R.string.errcode_unknown;
			break;
		}
		
		Toast.makeText(this, getString(result) + ", type=" + resp.getType(), Toast.LENGTH_SHORT).show();

        finish();
	}
	private void getAccessToken(String code) {
		//获取授权
		StringBuffer loginUrl = new StringBuffer();
		loginUrl.append("https://api.weixin.qq.com/sns/oauth2/access_token")
				.append("?appid=")
				.append("wx29e54bd6326fae9b")
				.append("&secret=")
				.append("023643b2022d75378aacd235bf45c949")
				.append("&code=")
				.append(code)
				.append("&grant_type=authorization_code");
		Log.d("urlurl", loginUrl.toString());

		OkHttpClient okHttpClient = new OkHttpClient();
		final Request request = new Request.Builder()
				.url(loginUrl.toString())
				.get()//默认就是GET请求，可以不写
				.build();
		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				Log.d("fan12", "onFailure: ");
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String responseInfo= response.body().string();
				Log.d("fan12", "onResponse: " +responseInfo);
				String access = null;
				String openId = null;
				String unionid = null;
				try {
					JSONObject jsonObject = new JSONObject(responseInfo);
					access = jsonObject.getString("access_token");
					openId = jsonObject.getString("openid");
					unionid = jsonObject.getString("unionid");
					SharedPreferencesUtil.Companion.getInstance(WXEntryActivity.this).putSP("token",access);
					SharedPreferencesUtil.Companion.getInstance(WXEntryActivity.this).putSP("openid",openId);
					SharedPreferencesUtil.Companion.getInstance(WXEntryActivity.this).putSP("unionid",unionid);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}



}