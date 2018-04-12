/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.sso;

import com.google.gson.Gson;
import com.google.j2objc.annotations.AutoreleasePool;
import com.hh.action.BaseAction;
import com.hh.server.HHServer;
import com.hh.sso.db.UserDB;
import com.hh.sso.telecom.SmsSender;
import com.hh.util.EncryptDecryptUtils;
import com.hh.web.HttpUtils;
import com.hh.util.ResourceBundleUtils;
import com.hh.web.HttpSession;
import com.octo.captcha.image.gimpy.Gimpy;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;


public class LoginAction extends BaseAction {
    
    public static final String LOGIN_SUCCESS = "0";
    public static final String LOGIN_INCORRECT = "1";
    public static final String CAPTCHA_INCORRECT = "2";
    public static final String MOBILE_EMPTY = "3";
    public static final String MOBILE_LENGTH_EXCEED = "4";
    public static final String MOBILE_FORMAT_INCORRECT = "5";
    public static final String MOBILE_NOT_REGISTED = "6";
    public static final String LOGIN_FAILED = "7";
    public static final String WAIT_MOBILE_RESPONSE = "8";
    public static final String AUTHEN_TYPE_EMPTY = "9";
    public static final String PASSWORD_EMPTY = "10";
    public static final String CAPTCHA_EMPTY = "11";
    public static final String APP_NOT_REGISTED = "12";
    public static final String LOGIN_LOCK = "13";
    public static AtomicLong countLogin = new AtomicLong(0);

    public LoginAction(HttpUtils hu) {
        super(hu);
    }
    
    public void rootView() throws IOException {
        returnPage("web/app/index.html");
    }

    public void loginView() throws IOException, ClassNotFoundException {
        if (httpUtils.getSessionAttribute("sso_username") != null) {
            returnFullPage("web/sso/loginSuccess.html");
        } else {
            httpUtils.setSessionAttribute("sso_validateCount", 0);
            returnFullPage("web/sso/index.html");
        }
    }
    
    // gui authorise code doi lay id token + access token + refresh token
    @AutoreleasePool
    public void sendAuthenInfo() throws IOException {
        String jwt = ""; // day la id token
        String param = (String)httpUtils.getParameter("authen_code");
        if(param != null) jwt = HttpSession.getInstance().getCacheAttribute(param);
        if(jwt != null && jwt.length() > 0) {
            byte[] data = jwt.getBytes();
            httpUtils.httpExchange.sendResponseHeaders(200, data.length);
            OutputStream os = httpUtils.httpExchange.getResponseBody();
            os.write(data);
            os.close();
            if(HttpSession.getInstance().getCacheAttribute("responseTime_" + param) != null) {
                Long responseTime = Long.parseLong(HttpSession.getInstance().getCacheAttribute("responseTime_" + param));
                String transLog = HttpSession.getInstance().getCacheAttribute("transactionLog_" + param) + 
                        "\nResponse Time\t: " + ((new Date()).getTime() - responseTime) + "ms\n---------------------------------------->";
                long temp = countLogin.incrementAndGet();
                if (temp % 100 == 1) {
                    transLog = transLog + "\nDa co " + temp + " luot dang nhap thanh cong";
                }
                HHServer.mainLogger.error(transLog);
                HttpSession.getInstance().removeCacheAttribute("responseTime_" + param);
                HttpSession.getInstance().removeCacheAttribute("transactionLog_" + param);
            }
            HttpSession.getInstance().removeCacheAttribute(param);
        }
        else httpUtils.sendNotFoundResponse();
    }
    
    public void cancelAutoLogin() throws IOException {
        httpUtils.removeSessionAttribute("sso_save");
        sendLoginResponse("1", 0);
    }
    
    @AutoreleasePool
    public void validateLogin() throws IOException {
        String otp = (String)httpUtils.getParameter("otp");
        if(otp != null) { // Validate thong tin tu SMS gui len
            String result = (String)HttpSession.getInstance().getCacheAttribute("otp_" + otp);
            if(result != null && !"true".equals(result)) {           
                long timeCreate = Long.parseLong(result.split("_")[1]);
                if((new Date()).getTime() - timeCreate > 30000) {
                    httpUtils.sendStringResponse(200, "<html><head><meta name=viewport content=\"width=device-width, initial-scale=0.9\"></head><body style=\"font-size:26px\"><div style=\"width: 350px; text-align:center; margin:auto; padding: 8px; border-radius: 5px; background-color: rgb(0, 145, 140); color: white\">Đã hết thời gian xác nhận đăng nhập!</div><script>setTimeout(function(){if ((navigator.userAgent.match(/iPad/i) != null) || navigator.userAgent.match(/iPhone/i) != null) {window.close();} else {window.close();}; }, 1500);</script></body></html>");
                } else {
                    HttpSession.getInstance().setCacheAttribute("otp_" + otp, "true");
                    httpUtils.sendStringResponse(200, "<html><head><meta name=viewport content=\"width=device-width, initial-scale=0.9\"></head><body style=\"font-size:26px\"><div style=\"width: 350px; text-align:center; margin:auto; padding: 8px; border-radius: 5px; background-color: rgb(0, 145, 140); color: white\">Đăng nhập thành công!</div><script>setTimeout(function(){if ((navigator.userAgent.match(/iPad/i) != null) || navigator.userAgent.match(/iPhone/i) != null) {window.close();} else {window.close();}; }, 1500);</script></body></html>");
                }
            } else {
                httpUtils.sendStringResponse(200, "<html><head><meta name=viewport content=\"width=device-width, initial-scale=0.9\"></head><body style=\"font-size:26px\"><div style=\"width: 350px; text-align:center; margin:auto; padding: 8px; border-radius: 5px; background-color: rgb(0, 145, 140); color: white\">Đã hết thời gian xác nhận đăng nhập!</div><script>setTimeout(function(){if ((navigator.userAgent.match(/iPad/i) != null) || navigator.userAgent.match(/iPhone/i) != null) {window.close();} else {window.close();}; }, 1500);</script></body></html>");
            }
        } else {
            String otpCache = (String)httpUtils.getSessionAttribute("sso_otp");
            String result = (String)HttpSession.getInstance().getCacheAttribute("otp_" + otpCache);
            Integer validateCount = (Integer) httpUtils.getSessionAttribute("sso_validateCount");
            String redirectUrl = (String)httpUtils.getSessionAttribute("sso_appurl");
            if(validateCount == null) validateCount = 0;
            if(result != null && "true".equals(result)) {
                HttpSession.getInstance().removeCacheAttribute("otp_" + otpCache);
                HashMap user = (HashMap)httpUtils.getSessionAttribute("sso_validate_user");
                if(HttpSession.getInstance().getCacheAttribute("otpData_" + otpCache) != null) {
                    user.put("sign_data", HttpSession.getInstance().getCacheAttribute("otpData_" + otpCache));
                    user.put("signature", HttpSession.getInstance().getCacheAttribute("otpSign_" + otpCache));
                }
                // gui authorise code qua ridirectUrl
                loginSuccess(redirectUrl, user);
            } else if(result != null && "false".equals(result)) {
                HttpSession.getInstance().removeCacheAttribute("otp_" + otpCache);
                sendLoginResponse(LOGIN_FAILED, 1);
                httpUtils.removeSessionAttribute("sso_validateCount");
            } else if(result != null && "2".equals(result)) {
                HttpSession.getInstance().removeCacheAttribute("otp_" + otpCache);
                sendLoginResponse(LOGIN_LOCK, 1);
                httpUtils.removeSessionAttribute("sso_validateCount");
            } else {
                validateCount++;
                sendValidate("1", redirectUrl, validateCount);
                if(validateCount > Integer.parseInt(ResourceBundleUtils.getConfig("ValidateCount"))) 
                    validateCount = 0;
                httpUtils.setSessionAttribute("sso_validateCount", validateCount);
            }            
        }
    }
    
    // gui authorise code qua ridirectUrl
    @AutoreleasePool
    public void loginSuccess(String redirectUrl, HashMap user) throws IOException {
        if(redirectUrl == null) redirectUrl = ResourceBundleUtils.getConfig("SSOPage");
        // Bam SHA them secret vao authorise code
        String dataKey = UUID.randomUUID().toString();
        HashMap cloneUser = (HashMap)user.clone();
        cloneUser.put("pin_code", "");
        cloneUser.put("salt", "");
        Object save = httpUtils.getParameter("save");
        Long startTime = (Long)httpUtils.getSessionAttribute("startTime");
        Long responseTime = (new Date()).getTime();
        HttpSession.getInstance().setCacheAttribute("responseTime_" + dataKey, responseTime.toString());
        String log = "\n<---------------------------------------\n" + user.get("email") + " login success."
                + "\nType\t\t\t: " + user.get("authen_type") + "\nSave\t\t\t: " 
                + save + "\nLogin Time\t\t: " + (responseTime - startTime) + "ms";
        HttpSession.getInstance().setCacheAttribute("transactionLog_" + dataKey, log);
        String jwt = generateJwt(redirectUrl, cloneUser);
        HttpSession.getInstance().setCacheAttribute(dataKey, jwt);
        EncryptDecryptUtils edu = new EncryptDecryptUtils();
        boolean secure = "true".equals(ResourceBundleUtils.getConfig("SSL"));
        if(save != null && "true".equals(save)) {
            String userId = user.get("user_id").toString();
            userId = edu.base64Encode(userId);
            userId = userId + "." + edu.encodePassword(user.get("password") + userId);
            long tenYear = 10l*365l*24l*3600l*1000l;
            httpUtils.addCookie("luser", userId, "/", tenYear, true, secure);
            httpUtils.addCookie("luserName", edu.base64Encode(
                    user.get("first_name").toString() + 
                    " " + user.get("last_name").toString() + ";" + user.get("authen_type")), "/", tenYear, true, secure);
        } else {
            //httpUtils.addCookie("luser", "null", "/", true, secure);
            //httpUtils.addCookie("luserName", "null", "/", true, secure);
            httpUtils.removeCookie("luser");
            httpUtils.removeCookie("luserName");
        }
        sendLoginSuccess(LOGIN_SUCCESS, redirectUrl + "/login?authen_code=" + dataKey);

        httpUtils.removeSessionAttribute("sso_appcode");
        httpUtils.removeSessionAttribute("sso_appurl");
        httpUtils.removeSessionAttribute("ssoFailCount");
        httpUtils.removeSessionAttribute("sso_captcha");
        httpUtils.removeSessionAttribute("sso_validate_user");
        httpUtils.removeSessionAttribute("sso_validateCount");
        httpUtils.removeSessionAttribute("sso_otp");
        httpUtils.removeSessionAttribute("sso_save");
        httpUtils.removeSessionAttribute("startTime");        
        httpUtils.invalidateSession();
    }
    
    private void loginComplete() throws IOException {
        httpUtils.removeSessionAttribute("sso_appcode");
        httpUtils.removeSessionAttribute("sso_appurl");
        httpUtils.addHHCookie(httpUtils.getHHCookie(), true, "true".equals(ResourceBundleUtils.getConfig("SSL")));
        httpUtils.sendRedirect(ResourceBundleUtils.getConfig("SSOPage"));
    }
    
    @AutoreleasePool
    private boolean validateForm(String userName, String password, Integer failCount) throws IOException {
        boolean check = false;
        // Kiem tra co luu cookie dang nhap khong
        if (httpUtils.getParameter("userId") != null) {
            check = true;
        } else { // Neu chua dang nhap lan nao thi kiem tra so dien thoai co hop le khong
            // Kiem tra user pass khong duoc de trong
            if (userName != null) userName = userName.trim();
            if ((userName == null) || (userName.trim().isEmpty())) {
                sendLoginResponse(MOBILE_EMPTY, failCount);
            } else if ((password == null) || (password.isEmpty())) {
                sendLoginResponse(PASSWORD_EMPTY, failCount);
            } else if (userName.length() > 15) {
                sendLoginResponse(MOBILE_LENGTH_EXCEED, failCount);
            } else if (!userName.matches("\\d+")) {
                sendLoginResponse(MOBILE_FORMAT_INCORRECT, failCount);
            } else {
                check = true;
            }
        }
        return check;
    }
    
    private boolean validateCaptcha(String captCha, Integer failCount) throws IOException {        
        // Kiem tra captcha
        boolean checkCaptcha = true;
        if (httpUtils.getParameter("userId") == null) {
            if ((captCha != null) && (!captCha.isEmpty()) && (!"null".equals(captCha))) {
                Gimpy pixCaptcha = (Gimpy) httpUtils.getSessionAttribute("sso_captcha");
                if (pixCaptcha != null) {
                    checkCaptcha = pixCaptcha.response.toLowerCase().equals(captCha.toLowerCase());
                }
                if(!checkCaptcha) {
                    sendLoginResponse(CAPTCHA_INCORRECT, failCount);
                }
            } else if(failCount > 2) {
                checkCaptcha = false;
                sendLoginResponse(CAPTCHA_EMPTY, failCount);
            }
        }
        httpUtils.removeSessionAttribute("sso_captcha");
        return checkCaptcha;
    }
    
    @AutoreleasePool
    private void login() throws IOException, UnsupportedEncodingException, ClassNotFoundException, NoSuchAlgorithmException {
        Integer failCount = (Integer) httpUtils.getSessionAttribute("ssoFailCount");
        String userName = "";
        String password = "";
        String ussd = "";
        String sms = "";
        String save = "";

        if (httpUtils.getParameter("userName") instanceof String) {
            userName = (String) httpUtils.getParameter("userName");
        }
        if (httpUtils.getParameter("password") instanceof String) {
            password = (String) httpUtils.getParameter("password");
        }
        if (httpUtils.getParameter("ussd") instanceof String) {
            ussd = (String) httpUtils.getParameter("ussd");
        }
        if (httpUtils.getParameter("sms") instanceof String) {
            sms = (String) httpUtils.getParameter("sms");
        }
        if (httpUtils.getParameter("sms") instanceof String) {
            save = (String) httpUtils.getParameter("save");
        }
        if (failCount == null) failCount = 0;
        if (validateForm(userName, password, failCount)) {
            userName = userName.trim();
            String captCha = "";
            String phone = userName;
            if(userName != null  && !userName.trim().isEmpty()) {
                if (userName.charAt(0) == '0') {
                    phone = "84" + userName.substring(1);
                }           
                if(HttpSession.getInstance().getCacheAttribute("wrongPin" + phone) != null)
                    failCount = 3;
            }
            if (failCount > 2) captCha = (String) httpUtils.getParameter("captcha");
            // Neu nhap dung captcha
            if (validateCaptcha(captCha, failCount)) {
                EncryptDecryptUtils edu = new EncryptDecryptUtils();
                boolean checkAuthen = false;
                HashMap user = new HashMap();
                // truong hop dang nhap tu luu cookie
                if (httpUtils.getParameter("userId") != null) {
                    String userId = httpUtils.getCookie("luser");
                    if (userId.contains(".")) {
                        String[] arrUserId = userId.split("\\.");
                        userId = edu.base64Decode(arrUserId[0]);
                        user = getUserInforById(userId);
                        if(user != null) {
                            userId = edu.base64Decode(arrUserId[0]);
                            user = getUserInforById(userId);
                            if (user != null && user.get("user_type") != null &&
                                    (user.get("user_type").toString().equals("1") || user.get("user_type").toString().equals("2")) &&
                                    user.get("password") != null && user.get("password").equals(edu.encodePassword(password))) {
                                userName = (String) user.get("mobile");
                                checkAuthen = true;
                            }
                        }
                    }
                } else {
                    // truong hop dang nhap khong luu cookie
                    String mobile = userName;
                    if (userName.charAt(0) == '8' && userName.charAt(1) == '4') {
                        mobile = "0" + userName.substring(2);
                    }
                    
                    String[] blackList = {""};
                    if(ResourceBundleUtils.getConfig("BlackListMobile") != null && ResourceBundleUtils.getConfig("BlackListMobile").contains(";"))
                        blackList = ResourceBundleUtils.getConfig("BlackListMobile").split(";");
                    if(!Arrays.asList(blackList).contains(mobile)) {
                        user = getUserInfor(mobile);
                        if (user != null && user.get("user_type") != null &&
                                (user.get("user_type").toString().equals("1") || user.get("user_type").toString().equals("2")) &&
                                user.get("password") != null && user.get("password").equals(edu.encodePassword(password))) {
                            checkAuthen = true;
                        }
                    }
                }

                if (checkAuthen) {
                    httpUtils.setSessionAttribute("sso_username", user);
                    sendLoginSuccess(LOGIN_SUCCESS, ResourceBundleUtils.getConfig("SSOPage"));
                } else {
                    failCount++;
                    httpUtils.setSessionAttribute("ssoFailCount", failCount);
                    sendLoginResponse(MOBILE_NOT_REGISTED, failCount);
                }
            }
        }
    }
        
    private void smsAuthenticate(String otp, String appCode, String phoneNumber, String appURL, String save, int failCount) throws IOException {
        String message = "De dang nhap " + appURL.replace("http://", "").replace("https://", "") + " nhan vao dia chi sau: \n"
                + ResourceBundleUtils.getConfig("McSmsPage") + "/validate?&otp=" + otp;
        HHServer.mainLogger.error("HHServer error: " + message);
        SmsSender.send(phoneNumber, message);
        authenticateComplete(otp, appURL, save, failCount);
    }
    
    private void authenticateComplete(String otp, String appURL, String save, int failCount) throws IOException {
        httpUtils.setSessionAttribute("sso_otp", otp);
        httpUtils.setSessionAttribute("sso_save", save);
        if (appURL == null) {
            HttpSession.getInstance().setCacheAttribute("otp_" + otp, "true");
        } else {
            HttpSession.getInstance().setCacheAttribute("otp_" + otp, "false_" + (new Date().getTime()));
        }
        sendLoginResponse(WAIT_MOBILE_RESPONSE, failCount);        
    }
    
    @AutoreleasePool
    public void authenticate() throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        String appUrl = (String) httpUtils.getParameter("app");
        if ((appUrl != null) && (!appUrl.isEmpty())) { //lan redirect dau tien
            returnFullPage("web/sso/index.html");
        } else if (httpUtils.getSessionAttribute("sso_appurl") == null && httpUtils.getParameter("userName") == null) { 
            // Dang nhap trang SSO
            returnFullPage("web/sso/notInWhiteList.html");
        } else if (httpUtils.getSessionAttribute("sso_username") != null) { // Da co session
            loginComplete();
        } else { // Chua co session thuc hien dang nhap
            login();
        }
    }

    public HashMap getApplicationInWhiteList(String appUrl) {
        EncryptDecryptUtils edu = new EncryptDecryptUtils();
        return (HashMap)HttpSession.getInstance().getCacheAttribute(
                ("ssoapp_" + edu.base64Encode(appUrl)).getBytes());
    }

    public void sendLoginResponse(String status, Integer failCount) throws IOException {
        HashMap<String, String> data = new HashMap();
        data.put("status", status);
        if(failCount == null) failCount = 0;
        data.put("failCount", failCount.toString());
        String json = new Gson().toJson(data);
        httpUtils.sendStringResponse(200, json);
    }
    
    public void sendLoginSuccess(String status, String redirect) throws IOException {
        HashMap<String, String> data = new HashMap();
        data.put("status", status);
        data.put("failCount", "0");
        data.put("redirectUrl", redirect);
        String json = new Gson().toJson(data);
        httpUtils.sendStringResponse(200, json);
    }
    
    public void sendValidate(String status, String redirect, Integer validateCount) throws IOException {
        HashMap<String, String> data = new HashMap();
        data.put("status", status);
        data.put("validateCount", validateCount.toString());
        data.put("redirectUrl", redirect);
        String json = new Gson().toJson(data);
        httpUtils.sendStringResponse(200, json);
    }        

    public static void loadUserFromDatabase() throws IOException {
        try {
            UserDB udb = new UserDB();
            List<Map> lstUser = udb.loadUserFromDatabase();
            EncryptDecryptUtils edu = new EncryptDecryptUtils();
            for (Map user : lstUser) {
                String mobile = (String)user.get("mobile");
                if(mobile != null) mobile = mobile.trim();
                else mobile = "";
                if(StringUtils.isNotEmpty(mobile)){
                    if (mobile.charAt(0) == '8' && mobile.charAt(1) == '4') {
                    mobile = "0" + mobile.substring(2);
                    }                
                    mobile = mobile.replace(" ", "");
                    mobile = mobile.replace(".", "");
                    mobile = mobile.replace("(+84)", "0");
                    if(mobile.contains("/")) mobile = mobile.split("/")[0];
                    if(mobile.contains("-")) mobile = mobile.split("-")[0];
                    if(mobile.contains(",")) mobile = mobile.split(",")[0];                                        
                    if(mobile.charAt(0) == '9' || mobile.charAt(0) == '1') mobile = "0" + mobile;
                    user.put("mobile",mobile);
                }
                HttpSession.getInstance().setCacheAttribute(("ssouser_" + edu.base64Encode(mobile)).getBytes(), user);
                HttpSession.getInstance().setCacheAttribute(("ssouser_id" + edu.base64Encode(user.get("user_id").toString())).getBytes(), user);
            }
        } catch (SQLException ex) {
            HHServer.mainLogger.error("HHServer error: ", ex);
        }
    }

    public HashMap getUserInfor(String userName) throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException, ClassNotFoundException {
        EncryptDecryptUtils edu = new EncryptDecryptUtils();
        HashMap ssoUser = (HashMap)HttpSession.getInstance().getCacheAttribute(("ssouser_" + edu.base64Encode(userName)).getBytes());
        if (ssoUser != null) {
            return ssoUser;
        }
        return null;
    }
    
    public HashMap getUserInforById(String userId) throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException, ClassNotFoundException {
        EncryptDecryptUtils edu = new EncryptDecryptUtils();
        HashMap ssoUser = (HashMap)HttpSession.getInstance().getCacheAttribute(("ssouser_id" + edu.base64Encode(userId)).getBytes());
        if (ssoUser != null) {
            return ssoUser;
        }
        return null;
    }    
    
    public String generateJwt(String appUrl, HashMap user) {
        EncryptDecryptUtils edu = new EncryptDecryptUtils();
        String firstName = (String)user.get("first_name");
        user.put("first_name", user.get("last_name"));
        user.put("last_name", firstName);
        HashMap mapHeader = new HashMap();
        mapHeader.put("typ", "JWT");
        mapHeader.put("alg", "HS256");
        String header = edu.base64Encode(new Gson().toJson(mapHeader));
        HashMap mapJwt = new HashMap();
        mapJwt.put("iss", appUrl);
        mapJwt.put("iat", new Date());
        mapJwt.put("exp", new Date((new Date()).getTime() + HttpSession.sessionTimeout * 60000));
        mapJwt.put("sub", new Gson().toJson(user));
        mapJwt.put("jti", UUID.randomUUID().toString());        
        String payload = edu.base64Encode(new Gson().toJson(mapJwt));
        String jwt = header + "." + payload + "." + DigestUtils.sha256Hex("sec13ret" + header + payload);
        return jwt; 
    }
}
