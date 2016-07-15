package com.infra.managers.utils;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.gson.GsonBuilder;
import com.infra.managers.DataManager;
import com.infra.managers.requests.Service;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Helper class to return mock data for services that aren't yet available.
 *
 */
public class MockServiceUtil {
    public static DataManager.RetrofitServicePair buildService() {
            GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(new GsonBuilder().create());

        Retrofit retrofit = new Retrofit.Builder()
                .client(new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .addInterceptor(buildMockInterceptor())
                        .build())
                .baseUrl("YOUR_API_END_POINT")
                .addConverterFactory(gsonConverterFactory)
                .build();

        return new DataManager.RetrofitServicePair(retrofit, retrofit.create(Service.class));

    }

    private static Interceptor buildMockInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                List<String> pathSegments = chain.request().url().pathSegments();

                if(pathSegments.size() == 1 && pathSegments.get(0).equals("item")) {
                    return new Response
                            .Builder()
                            .body(ResponseBody.create(MediaType.parse("application/json\n"), mockData))
                            .request(chain.request())
                            .protocol(Protocol.HTTP_1_1)
                            .code(200)
                            .message("OK")
                            .build();
                }

                return chain.proceed(chain.request());
            }
        };
    }

    protected static String mockData = "SAMPLE_JSON";

        public static String generateLargeDescription() {
                return StringUtils.join(
                        Stream.ofRange(1, 200)
                                .map((Integer i) -> {
                                        return "Line " + i;
                                })
                                .collect(Collectors.toList()), "\n");
        }
}
