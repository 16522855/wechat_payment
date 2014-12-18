//
//  WechatPay.swift
//  WechatPay
//
//  Created by Larry on 14-10-16.
//
//

import Foundation

extension String {
    subscript (i: Int) -> String {
        return String(Array(self)[i])
    }
    subscript (r: Range<Int>) -> String {
        var start = advance(startIndex, r.startIndex)
        var end = advance(startIndex, r.endIndex)
        return substringWithRange(Range(start: start, end: end))
    }
    func sha1() -> String {
        let data = self.dataUsingEncoding(NSUTF8StringEncoding)!
        var digest = [UInt8](count:Int(CC_SHA1_DIGEST_LENGTH), repeatedValue: 0)
        CC_SHA1(data.bytes, CC_LONG(data.length), &digest)
        let output = NSMutableString(capacity: Int(CC_SHA1_DIGEST_LENGTH))
        for byte in digest {
            output.appendFormat("%02x", byte)
        }
        return output
    }
}

@objc(WechatPay) class WechatPay : CDVPlugin, WXApiDelegate {
    
    var APP_ID = ""
    var PARTNER_ID = ""
    var APP_KEY = ""
    
    struct SubStruct {
        static var cmd = CDVInvokedUrlCommand()
        static var cmdDelegate: CDVCommandDelegate?
        static func sendBack(message: String){
            var pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAsString: message)
            if let cmdDelegate = SubStruct.cmdDelegate{
                cmdDelegate.sendPluginResult(pluginResult, callbackId:SubStruct.cmd.callbackId)
            }
        }
    }
    
    func start(command: CDVInvokedUrlCommand) {
//        var message = command.arguments[0] as String
//        
//        message = message.uppercaseString // Prove the plugin is actually doing something
//        
//        var pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAsString: message)
//        commandDelegate.sendPluginResult(pluginResult, callbackId:command.callbackId)
        
//        println("WechatPay start")
        WXApi.registerApp(APP_ID)
        var payReq: PayReq = PayReq()
        
        payReq.partnerId = PARTNER_ID
        payReq.prepayId = command.arguments[0] as String
//        payReq.package = "Sign=" + String(command.arguments[1] as NSString)
        payReq.package = "Sign=WXPay"
        payReq.nonceStr = command.arguments[3] as String
        if let timeStamp = command.arguments[4] as? String {
            payReq.timeStamp = UInt32((timeStamp as NSString).intValue)
        }
        
        var signParams: [String: String] = [
            "appid": APP_ID,
            "appkey": APP_KEY,
            "noncestr": payReq.nonceStr,
            "package": payReq.package,
            "partnerid": payReq.partnerId,
            "prepayid": payReq.prepayId,
            "timestamp": String(payReq.timeStamp),
        ]
        
        var result: String = String()
        result = "appid=" + APP_ID + "&appkey=" + APP_KEY + "&noncestr=" + payReq.nonceStr + "&package=" + payReq.package + "&partnerid=" + payReq.partnerId + "&prepayid=" + payReq.prepayId + "&timestamp=" + String(payReq.timeStamp)
        result = result.sha1()
        
        payReq.sign = result
        WXApi.sendReq(payReq)
        
        SubStruct.cmd = command;
        SubStruct.cmdDelegate = commandDelegate
    }
    
    func getSign(signParams: [String: String]) -> String{
        var result: String = String()
        
        return result.sha1()
    }
    
    @objc
    class func succeedBack(message: String){
        var pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAsString: message)
        if let cmdDelegate = SubStruct.cmdDelegate{
            cmdDelegate.sendPluginResult(pluginResult, callbackId:SubStruct.cmd.callbackId)
        }
    }
    @objc
    class func failedBack(message: String){
        var pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAsString: message)
        if let cmdDelegate = SubStruct.cmdDelegate{
            cmdDelegate.sendPluginResult(pluginResult, callbackId:SubStruct.cmd.callbackId)
        }
    }
}
