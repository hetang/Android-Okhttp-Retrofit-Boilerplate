package com.infra.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.infra.managers.deserializer.ErrorDeserializer;
import com.infra.managers.requests.MultipartWithJsonResponseConverterFactory;
import com.infra.managers.requests.Service;
import com.infra.managers.responses.ServiceResponse;
import com.infra.managers.utils.MockServiceUtil;
import com.infra.managers.utils.ServiceUtil;
import com.infra.logging.util.LogUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public abstract class DataManager<T> {
    public static String TAG = DataManager.class.getName();

    /**
     * Needs to override based on Product requirement
     */
    protected static final int API_INTERVAL = 30;
    protected static final int API_RETRY = 3;
    protected static final int API_RETRY_START_MILLISECOND = 1000;

    private DataObserver<T> observer;

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

    public DataObserver<T> getObserver() {
        return observer;
    }

    public void setObserver(DataObserver<T> observer) {
        this.observer = observer;
    }

    /**
     * Unpack a api response into a wrapper, capturing both the response's
     * data and connection state.
     *
     * @param call the Retrofit call object
     * @return the fully constructed response wrapper
     */
    protected ServiceResponse<T> unpackResult(Call<T> call) {
        ServiceResponse<T> result = new ServiceResponse<T>();

        try {
            Response<T> response = call.execute();

            if(response.isSuccessful()) {
                result.setResponseState(ServiceResponse.ResponseState.OK);
                result.setData(response.body());
                result.setHttpResponseCode(response.code());
            } else {
                result.setResponseState(ServiceResponse.ResponseState.parseHttpErrorCode(response.code()));
                result.setHttpResponseCode(response.code());
                result.setResponseMessage(response.message());
            }
        } catch (Exception e) {
            // Specifically ignoring Throwable because we can't handle JVM errors anyway
            result.setResponseState(ServiceResponse.ResponseState.OTHER_ERROR);
            result.setResponseMessage(e.getMessage());
            LogUtils.e(TAG, "Error while processing the response: " + e.getMessage(), e);
        }

        return result;
    }

    protected abstract Observable<ServiceResponse<T>> getRxObservable();
}
