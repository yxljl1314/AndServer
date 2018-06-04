/*
 * Copyright 2018 Yan Zhenjie.
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
package com.yanzhenjie.andserver.annotation;

import java.util.Locale;

/** Created by Yan Zhenjie on 2018/6/3. */
public enum RequestMethod {
  GET("GET"),
  HEAD("HEAD"),
  POST("POST"),
  PUT("PUT"),
  PATCH("PATCH"),
  DELETE("DELETE"),
  OPTIONS("OPTIONS"),
  TRACE("TRACE");

  private String method;

  RequestMethod(String method) {
    this.method = method;
  }

  public String getValue() {
    return method;
  }

  /**
   * Whether to allow the body to be transmitted.
   *
   * @return true, otherwise is false.
   */
  public boolean allowRequestBody() {
    switch (this) {
      case POST:
      case PUT:
      case PATCH:
      case DELETE:
        return true;
      default:
        return false;
    }
  }

  /**
   * Reverse the text for the request method.
   *
   * @param method method text, such as: GET, POST.
   *
   * @return {@link RequestMethod}.
   */
  public static RequestMethod reverse(String method) {
    method = method.toUpperCase(Locale.ENGLISH);
    switch (method) {
      case "GET": {
        return GET;
      }
      case "HEAD": {
        return HEAD;
      }
      case "POST": {
        return POST;
      }
      case "PUT": {
        return PUT;
      }
      case "PATCH": {
        return PATCH;
      }
      case "DELETE": {
        return DELETE;
      }
      case "OPTIONS": {
        return OPTIONS;
      }
      case "TRACE": {
        return TRACE;
      }
      default: {
        return GET;
      }
    }
  }
}