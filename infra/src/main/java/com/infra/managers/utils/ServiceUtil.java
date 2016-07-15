package com.infra.managers.utils;

import com.infra.BuildConfig;
import com.infra.managers.DataManager;
import com.infra.managers.requests.Service;
import com.infra.util.LogUtils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by hetashah on 7/15/16.
 */
public class ServiceUtil {
    private static final String TAG = ServiceUtil.class.getName();

    //TODO: The timeouts should be determined based on the network type (cellular vs Wifi) and speed
    public static final int CONNECT_TIMEOUT_SECONDS = 10;
    public static final int WRITE_TIMEOUT_SECONDS = 5;
    public static final int READ_TIMEOUT_SECONDS = 5;

    public static DataManager.RetrofitServicePair buildService(Converter.Factory converterFactory) {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Request newRequest;

                        newRequest = request.newBuilder()
                                .headers(buildRequestHeaders())
                                .build();

                        return chain.proceed(newRequest);
                    }
                });

        // !!! ONLY FOR DEBUG BUILDS !!!
        // See the CharlesProxy setup instructions: https://docs.google.com/document/d/1X3cFGtnxVcIokADMacOeoUsivf5n5VqyF8TR-MNfYQw/edit#heading=h.c44usz4sjqe7
        // Accept all certificates (including CharlesProxy) to be able to inspect the service network traffic
        if(BuildConfig.DEBUG) {
            disableTLSHandshake(okHttpBuilder);

        }

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpBuilder
                        .build())
                .baseUrl("YOUR_API_END_POINT") //TODO: Needs to replace with you end point
                .addConverterFactory(converterFactory)
                .build();

        return new DataManager.RetrofitServicePair(retrofit, retrofit.create(Service.class));
    }

    /** Use this only in DEBUG mode!!! */
    private static void disableTLSHandshake(OkHttpClient.Builder okHttpBuilder) {
        // Create a trust manager that does not validate certificate chains
        final TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
                    @Override public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
                    @Override public java.security.cert.X509Certificate[] getAcceptedIssuers() {return new java.security.cert.X509Certificate[]{};}
                }
        };

        try {
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            okHttpBuilder.sslSocketFactory(sslSocketFactory);

            okHttpBuilder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch(NoSuchAlgorithmException |KeyManagementException ex) {
            LogUtils.e(TAG, ex.getMessage(), ex);
        }
    }

    private static Headers buildRequestHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");

        /**
         * Logging request Id to back track user query.
         * This is very helpful for debugging.
         */
        String requestId = UUID.randomUUID().toString();
        LogUtils.i("X-Request-Id", requestId);

        headers.put("X-Request-Id", requestId);
        //headers.put("X-Device-Type", "Android");

        //TODO: Add your Common headers here
        return Headers.of(headers);
    }
}
