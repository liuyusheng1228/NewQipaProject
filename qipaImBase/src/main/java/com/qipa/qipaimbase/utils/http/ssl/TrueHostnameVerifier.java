package com.qipa.qipaimbase.utils.http.ssl;

import android.text.TextUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class TrueHostnameVerifier implements HostnameVerifier {
    public String domain;

    public TrueHostnameVerifier(String domain) {
        this.domain = domain;
    }

    public TrueHostnameVerifier() {
    }

    @Override
    public boolean verify(String hostname, SSLSession session) {
        if (TextUtils.isEmpty(domain)) {
//            domain = ...;
        }
        return HttpsURLConnection.getDefaultHostnameVerifier().verify(domain, session);
    }
}
