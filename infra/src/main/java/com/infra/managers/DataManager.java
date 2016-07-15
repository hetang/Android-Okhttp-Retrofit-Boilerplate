package com.infra.managers;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.infra.managers.deserializer.ErrorDeserializer;
import com.infra.managers.requests.MultipartWithJsonResponseConverterFactory;
import com.infra.managers.requests.Service;
import com.infra.managers.utils.MockServiceUtil;
import com.infra.managers.utils.ServiceUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class DataManager<T> {
    private DataObserver<T> observer;
    private Context context;
    protected Gson mapper = new GsonBuilder().create(); //Creating common Gson mapper to be used in child managers

    private static RetrofitServicePair serviceHolder;
    private static RetrofitServicePair multipartServiceHolder;

    private static RetrofitServicePair mockServiceHolder;

    @Data
    @AllArgsConstructor
    public static class RetrofitServicePair {
        private Retrofit retrofit;
        private Service service;
    }

    static {
        init();
    }

    public DataManager(Context context) {
        this.context = context;
    }

    private static void init() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Error.class, new ErrorDeserializer());
        Gson gson = gsonBuilder.create();

        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);

        serviceHolder = ServiceUtil.buildService(gsonConverterFactory);

        // This serviceHolder wrapper is capable of sending a plain text body (potentially multipart)
        // but still decode a JSON response.
        multipartServiceHolder = ServiceUtil.buildService(new MultipartWithJsonResponseConverterFactory(gsonConverterFactory));

        mockServiceHolder = MockServiceUtil.buildService();
    }

    public static Service getServiceImpl() {
        return serviceHolder.getService();
    }

    public static Retrofit getServiceRetrofit() {
        return serviceHolder.getRetrofit();
    }

    /**
     * @return an implementation that returns mock data defined in {@code MockServiceUtil}
     */
    public static Service getMockServiceImpl() {
        return mockServiceHolder.getService();
    }

    protected /* limit access to package and subclass */ static void setServiceImpl(Service service) {
        DataManager.serviceHolder.setService(service);
    }

    protected /* limit access to package and subclass */ static void setMockServiceImpl(Service service) {
        DataManager.mockServiceHolder.setService(service);
    }

    public static Service getMultipartServiceImpl() {
        return multipartServiceHolder.getService();
    }

    public static Retrofit getMultipartServiceRetrofit() {
        return serviceHolder.getRetrofit();
    }

    protected /* limit access to package and subclass */ static void setMultipartService(Service multipartService) {
        DataManager.multipartServiceHolder.setService(multipartService);
    }

    protected DataObserver<T> getObserver() {
        return observer;
    }

    protected void setObserver(DataObserver<T> observer) {
        this.observer = observer;
    }

    protected Context getContext() {
        return context;
    }
}
