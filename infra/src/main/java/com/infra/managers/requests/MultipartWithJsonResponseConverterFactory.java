/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.infra.managers.requests;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Writes multipart as text/plain and decodes response as JSON.
 */
public class MultipartWithJsonResponseConverterFactory extends Converter.Factory {
  private static final String TAG = MultipartWithJsonResponseConverterFactory.class.getSimpleName();

  static final MediaType MEDIA_TYPE = MediaType.parse("text/plain");
  private final GsonConverterFactory gsonConverterFactory;

  public MultipartWithJsonResponseConverterFactory(GsonConverterFactory gsonConverterFactory){
    this.gsonConverterFactory = gsonConverterFactory;
  }

  @Override
  public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
      return gsonConverterFactory.responseBodyConverter(type, annotations, retrofit);
  }

  @Override public Converter<?, RequestBody> requestBodyConverter(Type type,
      Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
    if (String.class.equals(type)) {
      return new Converter<String, RequestBody>() {
        @Override public RequestBody convert(String value) throws IOException {
          return RequestBody.create(MEDIA_TYPE, value);
        }
      };
    }

    return null;
  }
}
