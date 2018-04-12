/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.api;

import com.hh.action.BaseAction;
import com.hh.web.HttpSession;
import com.hh.web.HttpUtils;
import java.util.HashMap;

/**
 *
 * @author agiletech
 */
public class RsBaseAction extends BaseAction{

    public RsBaseAction(HttpUtils hu) {
        super(hu);
        returnData.put("data", "");
        returnData.put("response_message", "");
        returnData.put("error_code", "");
        returnData.put("error_message", "");
    }
    
    public Object getApiSessionAttribute(String key) {
        String accessToken = httpUtils.httpExchange.getRequestHeaders().get("access_token").get(0);
        HashMap apiSession = (HashMap)HttpSession.getInstance().getCacheAttribute(accessToken.getBytes());
        return apiSession.get(key);
    }
    
    public void setApiSessionAttribute(String key, Object value) {
        String accessToken = httpUtils.httpExchange.getRequestHeaders().get("access_token").get(0);
        HashMap apiSession = (HashMap)HttpSession.getInstance().getCacheAttribute(accessToken.getBytes());
        apiSession.put(key, value);
        HttpSession.getInstance().setCacheAttribute(accessToken.getBytes(), apiSession);
    }
}
