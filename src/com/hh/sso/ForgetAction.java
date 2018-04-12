/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.sso;

import com.google.gson.Gson;
import com.hh.action.BaseAction;
import com.hh.sso.db.UserDB;
import com.hh.sso.telecom.SmsSender;
import com.hh.server.HHServer;
import com.hh.util.EncryptDecryptUtils;
import com.hh.util.FileUtils;
import com.hh.util.ResourceBundleUtils;
import com.hh.web.HttpSession;
import com.hh.web.HttpUtils;
import com.octo.captcha.image.gimpy.Gimpy;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.apache.commons.codec.digest.DigestUtils;

public class ForgetAction extends BaseAction {

    public static final String RESET_SUCCESS = "0";
    public static final String CAPTCHA_INCORECT = "2";
    public static final String MOBILE_EMPTY = "3";
    public static final String MOBILE_LENGTH = "4";
    public static final String MOBILE_FORMAT = "5";    
    public static final String CAPTCHA_EMPTY = "6";
    public static final String MOBILE_EXIST = "7";
    public static final String LOGIN_INCORRECT = "8";
    public static final String CHANGEPASS_SUCCESS = "9";
    public static final String PASSWORD_WEAK = "10";
    public static final String PASSWORD_DUPLICATE = "11";
    public static final String PASSWORD_EMPTY = "12";
    public static final String NEWPASSWORD_EMPTY = "13";
    
    public ForgetAction(HttpUtils hu) {
        super(hu);
    }
    
    public void forgetPassword() throws IOException, ClassNotFoundException {
        String appUrl = (String)httpUtils.getSessionAttribute("sso_appurl");
        FileUtils fu = new FileUtils();
        File resultFile = new File("web/sso/forget.html").getCanonicalFile();
        String content = fu.readFileToString(resultFile, FileUtils.UTF_8);
        if(appUrl == null) content = content.replace("login?app=g_app", "sso");
        else content = content.replace("g_app", appUrl);
        byte[] byteContent = content.getBytes(Charset.forName(FileUtils.UTF_8));
        httpUtils.httpExchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        httpUtils.httpExchange.sendResponseHeaders(200, byteContent.length);
        OutputStream os = httpUtils.httpExchange.getResponseBody();
        os.write(byteContent);
        os.close();
    }
    
    public void changePassword() throws IOException, ClassNotFoundException {
        String appUrl = (String)httpUtils.getSessionAttribute("sso_appurl");
        FileUtils fu = new FileUtils();
        File resultFile = new File("web/sso/change.html").getCanonicalFile();
        String content = fu.readFileToString(resultFile, FileUtils.UTF_8);
        if(appUrl == null) content = content.replace("login?app=g_app", "sso");
        else content = content.replace("g_app", appUrl);
        byte[] byteContent = content.getBytes(Charset.forName(FileUtils.UTF_8));
        httpUtils.httpExchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        httpUtils.httpExchange.sendResponseHeaders(200, byteContent.length);
        OutputStream os = httpUtils.httpExchange.getResponseBody();
        os.write(byteContent);
        os.close();
    }    
    
    public void doForget() throws IOException, SQLException {
        if(validateForm()) {
            if (((String)httpUtils.getParameter("password") != null) && !((String)httpUtils.getParameter("password")).isEmpty()) {
                List lstParam = new ArrayList();
                String phoneNumber = (String)httpUtils.getParameter("mobile");
                if(phoneNumber != null) phoneNumber = phoneNumber.trim();
                else phoneNumber = "";
                if (phoneNumber.charAt(0) == '8' && phoneNumber.charAt(1) == '4') {
                    phoneNumber = "0" + phoneNumber.substring(2);
                }
                String salt = UUID.randomUUID().toString();
                String password = DigestUtils.sha256Hex(salt + (String)httpUtils.getParameter("newPassword"));
                lstParam.add(password);
                lstParam.add(salt);
                lstParam.add(phoneNumber);
                UserDB userDB = new UserDB();
                userDB.updatePasswordUser(lstParam);
                updatePasswordToCache(phoneNumber, password, salt);
                sendStatusResponse(CHANGEPASS_SUCCESS);                
            } else {
                String phoneNumber = (String)httpUtils.getParameter("mobile");
                if(phoneNumber != null) phoneNumber = phoneNumber.trim();
                else phoneNumber = "";
                if (phoneNumber.charAt(0) == '8' && phoneNumber.charAt(1) == '4') {
                    phoneNumber = "0" + phoneNumber.substring(2);
                }
                EncryptDecryptUtils edu = new EncryptDecryptUtils();
                HashMap user = (HashMap)HttpSession.getInstance().getCacheAttribute(("ssouser_" + edu.base64Encode(phoneNumber)).getBytes());
                if(user != null) {
                    String otp = UUID.randomUUID().toString();
                    HttpSession.getInstance().setCacheAttribute(otp,phoneNumber + "_" + (new Date()).getTime());
                    SmsSender.send("84" + phoneNumber.substring(1), "Ban quen Mật khẩu? Nhan vao dia chi sau: " +
                            ResourceBundleUtils.getConfig("McSmsPage") + "/resetPassword?otp=" + otp);
                    sendStatusResponse(RESET_SUCCESS);
                } else {
                    sendStatusResponse(LOGIN_INCORRECT);
                }
            }
        }
    }
    
    public void resetPassword() throws SQLException, IOException {
        if(httpUtils.getParameter("otp") != null) {
            String data = HttpSession.getInstance().getCacheAttribute(httpUtils.getParameter("otp").toString());
            if(data != null && !data.trim().isEmpty() && data.contains("_")) {
                String phoneNumber = data.split("_")[0];
                Long oldTime = Long.parseLong(data.split("_")[1]);
                if((new Date()).getTime() - oldTime > 120000) {
                    httpUtils.sendStringResponse(200, "<html><head><meta name=viewport content=\"width=device-width, initial-scale=0.9\"></head><body style=\"font-size:26px\"><div style=\"width: 350px; text-align:center; margin:auto; padding: 8px; border-radius: 5px; background-color: rgb(0, 145, 140); color: white\">Đã hết hạn thời gian xác nhận cấp lại Mật khẩu!</div></body></html>");
                } else {
                    HttpSession.getInstance().removeCacheAttribute(httpUtils.getParameter("otp").toString());
                    List lstParam = new ArrayList();
                    phoneNumber = phoneNumber.trim();
                    EncryptDecryptUtils edu = new EncryptDecryptUtils();
                    HashMap user = (HashMap)HttpSession.getInstance().getCacheAttribute(("ssouser_" + edu.base64Encode(phoneNumber)).getBytes());
                    if(user != null) {
                        String randomPass = getRandomPin();
                        String salt = UUID.randomUUID().toString();
                        String password = DigestUtils.sha256Hex(salt + randomPass);
                        lstParam.add(password);
                        lstParam.add(salt);
                        lstParam.add(phoneNumber);
                        UserDB userDB = new UserDB();
                        userDB.updatePasswordUser(lstParam);

                        //update to cache
                        user.put("pin_code", password);
                        user.put("salt", salt);
                        HttpSession.getInstance().setCacheAttribute(("ssouser_" + edu.base64Encode(phoneNumber)).getBytes(), user);
                        HttpSession.getInstance().setCacheAttribute(("ssouser_" + edu.base64Encode(user.get("email").toString())).getBytes(), user);
                        HttpSession.getInstance().setCacheAttribute(("ssouser_id" + edu.base64Encode(user.get("user_id").toString())).getBytes(), user);
                        
                        String message = "Mau khau dang nhap giupviecsenhong: " + randomPass;
                        HHServer.mainLogger.error((String)user.get("email") + " cap lai mat khau thanh cong");
                        SmsSender.send("84" + phoneNumber.substring(1), message);
                    }
                    httpUtils.sendStringResponse(200, "<html><head><meta name=viewport content=\"width=device-width, initial-scale=0.9\"></head><body style=\"font-size:26px\"><div style=\"width: 350px; text-align:center; margin:auto; padding: 8px; border-radius: 5px; background-color: rgb(0, 145, 140); color: white\">Mật khẩu mới sẽ được gửi qua tin nhắn cho bạn!</div></body></html>");
                }
            } else {
                httpUtils.sendStringResponse(200, "<html><head><meta name=viewport content=\"width=device-width, initial-scale=0.9\"></head><body style=\"font-size:26px\"><div style=\"width: 350px; text-align:center; margin:auto; padding: 8px; border-radius: 5px; background-color: rgb(0, 145, 140); color: white\">Đã hết hạn thời gian xác nhận cấp lại Mật khẩu!</div></body></html>");
            }
        }
    }
    
    private void updatePasswordToCache(String phoneNumber, String password, String salt) {
        EncryptDecryptUtils edu = new EncryptDecryptUtils();
        HashMap user = (HashMap)HttpSession.getInstance().getCacheAttribute(("ssouser_" + edu.base64Encode(phoneNumber)).getBytes());
        user.put("pin_code", password);
        user.put("salt", salt);
        HttpSession.getInstance().setCacheAttribute(("ssouser_" + edu.base64Encode(phoneNumber)).getBytes(), user);
        HttpSession.getInstance().setCacheAttribute(("ssouser_" + edu.base64Encode(user.get("email").toString())).getBytes(), user);
        HttpSession.getInstance().setCacheAttribute(("ssouser_id" + edu.base64Encode(user.get("user_id").toString())).getBytes(), user);
        HHServer.mainLogger.error((String)user.get("email") + " thay doi ma pin thanh cong");
    }
    
    public String getRandomPin() {
        String randomPass = "";
        Random r = new Random();
        for(int i = 0; i < 6; i++) {
            randomPass += (char)(48 + r.nextInt(10));
        }
        return randomPass;
    }
    
    public boolean validateForm() throws IOException {
        boolean check = false;
        String captcha = (String)httpUtils.getParameter("captcha");
        Gimpy pixCaptcha = (Gimpy) httpUtils.getSessionAttribute("sso_captcha");
        String phoneNumber = (String)httpUtils.getParameter("mobile");
        if(phoneNumber != null) phoneNumber = phoneNumber.trim();
        else phoneNumber = "";
        if (phoneNumber != null && phoneNumber.length() > 2 && phoneNumber.charAt(0) == '8' && phoneNumber.charAt(1) == '4') {
            phoneNumber = "0" + phoneNumber.substring(2);
        }
        EncryptDecryptUtils edu = new EncryptDecryptUtils();
        if (((String)httpUtils.getParameter("mobile") == null) || ((String)httpUtils.getParameter("mobile")).isEmpty()) {
            sendStatusResponse(MOBILE_EMPTY);
        } else if (((String)httpUtils.getParameter("mobile")).length() > 15) {
            sendStatusResponse(MOBILE_LENGTH);
        } else if (!phoneNumber.matches("\\d+")) {
            sendStatusResponse(MOBILE_FORMAT);
        } else if (((String)httpUtils.getParameter("passType") != null) && "0".equals(httpUtils.getParameter("passType"))) {
            // Neu la truong hop doi mat khau
            check = false;
            HashMap user = (HashMap) HttpSession.getInstance().getCacheAttribute(("ssouser_" + edu.base64Encode(phoneNumber)).getBytes());
            if (((String)httpUtils.getParameter("password") == null) || ((String)httpUtils.getParameter("password")).isEmpty()) {
                sendStatusResponse(PASSWORD_EMPTY);
            } else if(user == null || !user.get("pin_code").equals(DigestUtils.sha256Hex(user.get("salt") + (String)httpUtils.getParameter("password")))) {
                sendStatusResponse(LOGIN_INCORRECT);
            } else if (((String)httpUtils.getParameter("newPassword") == null) || ((String)httpUtils.getParameter("newPassword")).isEmpty()) {
                sendStatusResponse(NEWPASSWORD_EMPTY);
            } else if(((String)httpUtils.getParameter("password")).equals((String)httpUtils.getParameter("newPassword"))) {
                sendStatusResponse(PASSWORD_DUPLICATE);
            } else if(!(((String)httpUtils.getParameter("newPassword")).matches("\\d+") && ((String)httpUtils.getParameter("newPassword")).length() == 6)) {
                sendStatusResponse(PASSWORD_WEAK);    
            } else if (captcha == null || captcha.isEmpty() || "null".equals(captcha)) {
                sendStatusResponse(CAPTCHA_EMPTY);
            } else if (pixCaptcha == null || !pixCaptcha.response.toLowerCase().equals(captcha.toLowerCase())) {
                sendStatusResponse(CAPTCHA_INCORECT);
            } else {
                check = true;
            }
        } else if (HttpSession.getInstance().getCacheAttribute(("ssouser_" + edu.base64Encode(phoneNumber)).getBytes()) == null) {
            sendStatusResponse(MOBILE_EXIST);
        } else if (captcha == null || captcha.isEmpty() || "null".equals(captcha)) {
            sendStatusResponse(CAPTCHA_EMPTY);
        } else if (pixCaptcha == null || !pixCaptcha.response.toLowerCase().equals(captcha.toLowerCase())) {
            sendStatusResponse(CAPTCHA_INCORECT);
        } else {
            check = true;
        }
        return check;
    }

    public void sendStatusResponse(String status) throws IOException {
        HashMap<String, String> data = new HashMap();
        data.put("status", status);
        String json = new Gson().toJson(data);
        httpUtils.sendStringResponse(200, json);
    }     
}
