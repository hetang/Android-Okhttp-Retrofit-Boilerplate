package com.infra.managers.models;

import java.io.IOException;
import java.lang.annotation.Annotation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Error {
    private int code;
    private String message;

    public static Error readError(Retrofit retrofit, Response<?> response) throws IOException {
        // Look up a converter for the Error type on the Retrofit instance.
        Converter<ResponseBody, Error> errorConverter = retrofit.responseBodyConverter(Error.class, new Annotation[0]);
        // Convert the error body into our Error type.
        return errorConverter.convert(response.errorBody());
    }
}
