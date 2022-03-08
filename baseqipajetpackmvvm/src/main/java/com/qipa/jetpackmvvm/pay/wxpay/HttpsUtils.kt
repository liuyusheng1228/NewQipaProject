package com.qipa.jetpackmvvm.pay.wxpay

import android.text.TextUtils
import java.io.*
import java.lang.Exception
import java.lang.NullPointerException
import java.net.HttpURLConnection
import java.net.URL
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

object HttpsUtils {
    fun post(url: String?, body: String): ByteArray? {
        if (TextUtils.isEmpty(url)) {
            throw NullPointerException("url can't be empty")
        }
        try {
            val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
            if (connection is HttpsURLConnection) {
                initHttps(connection as HttpsURLConnection, null)
            }
            connection.setRequestMethod("POST")
            connection.setDoOutput(true)
            val out = DataOutputStream(connection.getOutputStream())
            out.write(body.toByteArray())
            out.close()
            val code: Int = connection.getResponseCode()
            if (code == HttpURLConnection.HTTP_OK) {
                val `in` = DataInputStream(connection.getInputStream())
                val bos = ByteArrayOutputStream()
                copyStream(`in`, bos)
                `in`.close()
                val data: ByteArray = bos.toByteArray()
                bos.close()
                return data
            }
            connection.disconnect()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(IOException::class)
    fun copyStream(`is`: InputStream, os: OutputStream): Int {
        var total = 0
        var read = 0
        val buf = ByteArray(8192)
        while (`is`.read(buf).also { read = it } > 0) {
            os.write(buf, 0, read)
            total += read
        }
        return total
    }

    fun initHttps(connection: HttpsURLConnection, protocol: String?) {
        try {
            val context: SSLContext =
                SSLContext.getInstance(if (TextUtils.isEmpty(protocol)) "TLS" else protocol)
            context.init(null, arrayOf(EmptyTrustManager()), null)
            connection.setSSLSocketFactory(context.getSocketFactory())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class EmptyTrustManager : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(
            chain: Array<X509Certificate?>?,
            authType: String?
        ) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(
            chain: Array<X509Certificate?>?,
            authType: String?
        ) {
            require(!(chain == null || chain.size <= 0)) { "Server X509Certificate is empty" }
            for (cert in chain) {
//                System.out.println(cert.getPublicKey())
                cert?.checkValidity()
            }
        }

        override fun getAcceptedIssuers(): Array<X509Certificate?>? {
            return arrayOfNulls(0)
        }


    }

    class EmptyHostVerifier : HostnameVerifier {
        override fun verify(hostname: String, session: SSLSession?): Boolean {
            println("verify $hostname")
            val hv: HostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier()
            return hv.verify(hostname, session)
        }
    }
}
