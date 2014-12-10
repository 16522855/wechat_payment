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

import org.apache.cordova.*;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WechatPay extends CordovaPlugin
{
  private static IWXAPI api;
    private static final String WXAPP_ID = "";
    /** 
     * 注意 构造方法不能为 
     *  
     * Plugin_intent(){} 
     *  
     * 可以不写或者 定义为如下 
     *  
     */  
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
            // 获取JS传递的args的第一个参数  
            String infos = args.getString(0); 
            this.start(infos);
            return true;  
        }  
        return false;  
  
    }  
  
    // 方法执行体  
    private void start(String str) {  
        // cordova.getActivity() 获取当前activity的this  
//      System.out.print("123" + cordova.getActivity().toString());
//      Toast.makeText(cordova.getActivity(), str, Toast.LENGTH_SHORT).show();
//      callbackContext.success("succeed");
      
      // send oauth request 
      final SendAuth.Req req = new SendAuth.Req();
      req.scope = "snsapi_userinfo";
      req.state = "wechat_sdk_demo_test";
      api.sendReq(req);
    }
    public static void successCallback(String result){
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, result);
      pluginResult.setKeepCallback(true);
      callbackContext.sendPluginResult(pluginResult);
    }
    public static void failedCallback(String result){
      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, result);
      pluginResult.setKeepCallback(true);
      callbackContext.sendPluginResult(pluginResult);
    }
    public static IWXAPI getWXApi(){
      return api;
    }
}
