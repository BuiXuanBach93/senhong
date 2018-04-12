/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app;

import com.google.gson.Gson;
import com.hh.action.IFilter;
import com.hh.server.Server;
import com.hh.web.HttpUtils;
import com.hh.util.ResourceBundleUtils;
import com.hh.web.HttpSession;
import java.util.HashMap;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author agiletech
 */
public class AuthenticateFilter implements IFilter {

    @Override
    public boolean execute(HttpUtils hu) {
        boolean check = false;
        try {
            String action = ResourceBundleUtils.getAction(hu.getPath());
            if(action.contains("[no_session]")) {
                check = true;
            } else if(action.contains("[api]")) {
                if(hu.httpExchange.getRequestHeaders().get("access_token") != null) {
                    String accessToken = hu.httpExchange.getRequestHeaders().get("access_token").get(0);
                    HashMap apiSession = (HashMap)HttpSession.getInstance().getCacheAttribute(accessToken.getBytes());
                    if(apiSession != null) {
                        if(accessToken != null) {
                            check = true;
                        } else {
                            HashMap returnData = new HashMap();
                            returnData.put("error", "Không tìm thấy access_token trong header của request");
                            hu.sendJsonResponse(401, (new Gson()).toJson(returnData));
                        }
                    } else {
                        HashMap returnData = new HashMap();
                        returnData.put("error", "Không tìm thấy access_token trên server cache");
                        hu.sendJsonResponse(401, (new Gson()).toJson(returnData));
                    }
                } else {
                    HashMap returnData = new HashMap();
                    returnData.put("error", "Không tìm thấy access_token trong header của request");
                    hu.sendJsonResponse(401, (new Gson()).toJson(returnData));
                }
            } else {
                HashMap ssoUser = (HashMap) hu.getSessionAttribute("sso_username");
                if(ssoUser != null) { // Da co session
                    check = true;
                } else { 
                    hu.invalidateSession();
                    hu.sendRedirect(ResourceBundleUtils.getConfig("SessionTimeoutPage"));
                }
            }
        } catch (Exception ex) {
            Server.mainLogger.error("HHServer error: ", ex);
        } 
        return check;
    }
}
