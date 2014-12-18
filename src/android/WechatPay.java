/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package co.lingeng.cordova.wechat_pay;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.cordova.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WechatPay extends CordovaPlugin {
  private static IWXAPI api;
  private static final String WXAPP_ID = "";
  private static final String PARTNER_ID = "";
  private static final String APP_KEY = "";

  public WechatPay() {
  }

  private static CallbackContext callbackContext;

  @Override
  public boolean execute(String action, org.json.JSONArray args,
      CallbackContext callbackContext) throws org.json.JSONException {
    WechatPay.callbackContext = callbackContext;

    api = WXAPIFactory.createWXAPI(cordova.getActivity(), WXAPP_ID, true);
    api.registerApp(WXAPP_ID);

    if (action.equals("start")) {
      List<String> strList = new ArrayList<String>();
      Log.d("debug", args.toString());
      for (int i = 0; i < args.length(); i++) {
        strList.add(args.getString(i));
      }
      this.start(strList);
      return true;
    }
    return false;

  }

  private void start(List<String> strList) {
    // System.out.print("123" + cordova.getActivity().toString());
    // Toast.makeText(cordova.getActivity(), str,
    // Toast.LENGTH_SHORT).show();
    // callbackContext.success("succeed");

    // send oauth request
    PayReq req = new PayReq();
    req.appId = WechatPay.WXAPP_ID;
    req.partnerId = WechatPay.PARTNER_ID;
    req.prepayId = strList.get(0);
    req.nonceStr = strList.get(3);
    req.timeStamp = strList.get(4);
    req.packageValue = "Sign=" + strList.get(1);
    // req.sign = strList.get(2);

    List<NameValuePair> signParams = new LinkedList<NameValuePair>();
    signParams.add(new BasicNameValuePair("appid", req.appId));
    signParams.add(new BasicNameValuePair("appkey", APP_KEY));
    signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
    signParams.add(new BasicNameValuePair("package", req.packageValue));
    signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
    signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
    signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
    req.sign = genSign(signParams);

    api.sendReq(req);
  }

  private String genSign(List<NameValuePair> params) {
    StringBuilder sb = new StringBuilder();

    int i = 0;
    for (; i < params.size() - 1; i++) {
      sb.append(params.get(i).getName());
      sb.append('=');
      sb.append(params.get(i).getValue());
      sb.append('&');
    }
    sb.append(params.get(i).getName());
    sb.append('=');
    sb.append(params.get(i).getValue());

    String sha1 = WechatPay.sha1(sb.toString());
    Log.d("debug", "genSign, sha1 = " + sha1);
    return sha1;
  }

  public static String sha1(String str) {
    if (str == null || str.length() == 0) {
      return null;
    }

    char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f' };

    try {
      MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
      mdTemp.update(str.getBytes());

      byte[] md = mdTemp.digest();
      int j = md.length;
      char buf[] = new char[j * 2];
      int k = 0;
      for (int i = 0; i < j; i++) {
        byte byte0 = md[i];
        buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
        buf[k++] = hexDigits[byte0 & 0xf];
      }
      return new String(buf);
    } catch (Exception e) {
      return null;
    }
  }

  public static void successCallback(String result) {
    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,
        result);
    pluginResult.setKeepCallback(true);
    callbackContext.sendPluginResult(pluginResult);
  }

  public static void failedCallback(String result) {
    PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR,
        result);
    pluginResult.setKeepCallback(true);
    callbackContext.sendPluginResult(pluginResult);
  }

  public static IWXAPI getWXApi() {
    return api;
  }
}
