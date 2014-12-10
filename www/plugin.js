
module.exports = function(mills, successCallback, failedCallback){
	cordova.exec(successCallback, failedCallback, "WechatPay", "start", [mills]);
	// alert('in plugin.js');
	return true;
}
