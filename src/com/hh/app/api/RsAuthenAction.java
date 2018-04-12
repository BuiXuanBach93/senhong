/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.api;

import com.hh.server.HHServer;
import com.hh.server.Server;
import com.hh.sso.LoginAction;
import com.hh.util.EncryptDecryptUtils;
import com.hh.web.HttpSession;
import com.hh.web.HttpUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author agiletech
 */
public class RsAuthenAction extends RsBaseAction{

    public RsAuthenAction(HttpUtils hu) {
        super(hu);
    }
    
    public void login() throws IOException, SQLException, NoSuchAlgorithmException, 
            UnsupportedEncodingException, ClassNotFoundException {
        String userName = "";
        String password = "";
        String userType = (String)httpUtils.getParameter("user_type");

        if (httpUtils.getParameter("username") != null && 
                !httpUtils.getParameter("username").toString().trim().isEmpty()) {
            userName = (String) httpUtils.getParameter("username");
        } else {
            returnData.put("response_message", "Hãy nhập Tên đăng nhập");
            returnData.put("error_code", "login_02");
            returnData.put("error_message", "Chưa nhập user");
            returnAjax();
            return;
        }
        
        if (httpUtils.getParameter("password") != null && 
                !httpUtils.getParameter("password").toString().trim().isEmpty()) {
            password = (String) httpUtils.getParameter("password");
        } else {
            returnData.put("response_message", "Hãy nhập Mật khẩu");
            returnData.put("error_code", "login_03");
            returnData.put("error_message", "Chưa nhập mật khẩu");
            returnAjax();
            return;         
        }
        
        boolean checkAuthen = false;
        HashMap user = null;
        if(userName != null & !userName.trim().isEmpty()) {
            user = (new LoginAction(httpUtils)).getUserInfor(userName.trim());
            if (user != null) {
                String pw = user.get("password").toString();
                String ut = user.get("user_type").toString();
                if("1".equals(ut) || "2".equals(ut) || ut.equals(userType))
                if(pw.equals((new EncryptDecryptUtils()).encodePassword(password))) {
                    EncryptDecryptUtils edu = new EncryptDecryptUtils();
                    HashMap ssoUser = (HashMap)HttpSession.getInstance().getCacheAttribute(("ssouser_id" + edu.base64Encode(user.get("user_id").toString())).getBytes());
                    if(ssoUser != null) checkAuthen = true;
                }
            }
        }
        if(checkAuthen) {
            HashMap apiSession = (HashMap)HttpSession.getInstance().getCacheAttribute("api_session".getBytes());
            if(apiSession == null) apiSession = new HashMap();
            String accessToken = UUID.randomUUID().toString();
            apiSession.put("api_user", user);
            HttpSession.getInstance().setCacheAttribute(accessToken.getBytes(), apiSession);
            
            httpUtils.httpExchange.getResponseHeaders().add("access_token", accessToken);
            returnData.put("data", user);
            returnData.put("response_message", "Đăng nhập thành công");
        } else {
            returnData.put("response_message", "Thông tin đăng nhập không chính xác");
            returnData.put("error_code", "login_01");
            returnData.put("error_message", "Sai user hoặc password, user: " + userName + ", password: " + password);
        }
        
        returnAjax();
    }
    
    public void logout() throws IOException {
        String accessToken = "";
        List<String> lstAccess = httpUtils.httpExchange.getRequestHeaders().get("access_token");
        if (lstAccess != null && !lstAccess.isEmpty()) {
            accessToken = (String) lstAccess.get(0);            
            HttpSession.getInstance().removeCacheAttribute(accessToken.getBytes());
        } else {
            returnData.put("error_code", "logout_01");
            returnData.put("error_message", "Không tìm thấy token trong request header");
        }
        
        returnAjax();
    }
}
