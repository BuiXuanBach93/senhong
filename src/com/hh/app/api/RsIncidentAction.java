/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.api;

import com.hh.app.db.IncidentDB;
import com.hh.util.EncryptDecryptUtils;
import com.hh.util.ResourceBundleUtils;
import com.hh.web.HttpSession;
import com.hh.web.HttpUtils;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author agiletech
 */
public class RsIncidentAction extends RsBaseAction{
    
    public static AtomicInteger orderCount = new AtomicInteger(0);

    public RsIncidentAction(HttpUtils hu) {
        super(hu);
    }
    
    public void createIncident() throws IOException, SQLException, ParseException {
        List lstParam = new ArrayList();
        
        String userId = (String)httpUtils.getParameter("userId");
        Integer intUserId = null;
        if(userId != null && !userId.trim().isEmpty()) {
            intUserId = Integer.parseInt(userId);
            lstParam.add(intUserId);
        } else {
            returnData.put("error_code", "createincident_01");
            returnData.put("error_message", "Người báo sự cố bắt buộc nhập");
            returnData.put("response_message", "Hãy nhập người báo sự cố");
            returnAjax();
            return;
        }
        
        HashMap user = (HashMap)HttpSession.getInstance().getCacheAttribute(("ssouser_id" + (new EncryptDecryptUtils()).base64Encode(userId)).getBytes());

        String typeId = (String)httpUtils.getParameter("typeId");
        Integer intTypeId = null;
        if(typeId != null && !typeId.trim().isEmpty()) {
            intTypeId = Integer.parseInt(typeId);
            lstParam.add(intTypeId);
        } else {
            returnData.put("error_code", "createincident_02");
            returnData.put("error_message", "Loại người dùng bắt buộc nhập");
            returnData.put("response_message", "Hãy nhập người báo sự cố");
            returnAjax();
            return;
        }

        String content = (String)httpUtils.getParameter("content");
        if(content != null && !content.trim().isEmpty()) {
            lstParam.add(content);
        } else {
            returnData.put("error_code", "createincident_03");
            returnData.put("error_message", "Nội dung sự cố bắt buộc nhập");
            returnData.put("response_message", "Hãy nhập Nội dung sự cố");
            returnAjax();
            return;
        }
        
        String planId = (String)httpUtils.getParameter("planId");
        Integer intPlanId = null;
        if(planId != null && !planId.trim().isEmpty()) {
            intPlanId = Integer.parseInt(planId);
            lstParam.add(intPlanId);
        } else {
            returnData.put("error_code", "createincident_04");
            returnData.put("error_message", "planId bắt buộc nhập");
            returnData.put("response_message", "Hãy nhập lịch làm việc");
            returnAjax();
            return;
        }        
        
        lstParam.add(new Date());
        
        (new IncidentDB()).insertIncident(lstParam);
        
        returnData.put("response_message", "Báo sự cố thành công");
        returnAjax();
        
        String type = "";
        if(intTypeId == 3) type = "NGV";
        if(intTypeId == 4) type = "KH";
        String appId = "";
        String restKey = "";
        if(intTypeId == 3) {
            appId = ResourceBundleUtils.getConfig("maid_appid");
            restKey = ResourceBundleUtils.getConfig("maid_restkey");
        } else {
            appId = ResourceBundleUtils.getConfig("customer_appid");
            restKey = ResourceBundleUtils.getConfig("customer_restkey");
        }
        
        String payload = " {\"app_id\": \"" + appId + "\",\"filters\": [ "
                + "  	{\"field\": \"tag\", \"key\": \"user_id\", \"relation\": \"=\", \"value\": \"" + 1 + "\"}], "
                + " \"data\": {\"message_type\": \"1\",\"plan_id\":\"" + planId + "\"},\"contents\": "
                + " {\"en\": \"Sự cố (" + type + " " + user.get("name") 
                + ") " + content + "\"}} ";                

        String requestUrl = ResourceBundleUtils.getConfig("onesignal_url");
        RsMessageAction rsm = new RsMessageAction(httpUtils);
        rsm.sendRestMessage(requestUrl, payload, restKey, userId, "1", "Sự cố (" + type + " " + user.get("name") 
                + ") " + content);
    }
    
    public void resolveIncident() throws IOException, SQLException, ParseException {
        List lstParam = new ArrayList();
        
        String managerId = (String)httpUtils.getParameter("managerId");
        Integer intManagerId = null;
        if(managerId != null && !managerId.trim().isEmpty()) {
            intManagerId = Integer.parseInt(managerId);
            lstParam.add(intManagerId);
        } else {
            returnData.put("error_code", "resolveincident_03");
            returnData.put("error_message", "Lỗi thiếu id");
            returnData.put("response_message", "");
            returnAjax();
            return;
        }
        
        String content = (String)httpUtils.getParameter("resolveContent");
        if(content != null && !content.trim().isEmpty()) {
            lstParam.add(content);
        } else {
            returnData.put("error_code", "resolveincident_01");
            returnData.put("error_message", "Nội dung sự cố bắt buộc nhập");
            returnData.put("response_message", "Hãy nhập Nội dung sự cố");
            returnAjax();
            return;
        }
        
        String status = (String)httpUtils.getParameter("status");
        Integer intStatus = null;
        if(status != null && !status.trim().isEmpty()) {
            intStatus = Integer.parseInt(status);
            lstParam.add(intStatus);
        } else {
            returnData.put("error_code", "resolveincident_02");
            returnData.put("error_message", "Trạng thái sự cố bắt buộc nhập");
            returnData.put("response_message", "Hãy nhập trạng thái sự cố");
            returnAjax();
            return;
        }      
        
        
        String id = (String)httpUtils.getParameter("id");
        Integer intId = null;
        if(id != null && !id.trim().isEmpty()) {
            intId = Integer.parseInt(id);
            lstParam.add(intId);
        } else {
            returnData.put("error_code", "resolveincident_03");
            returnData.put("error_message", "Lỗi thiếu id");
            returnData.put("response_message", "");
            returnAjax();
            return;
        }
        
        (new IncidentDB()).resolveIncident(lstParam);
        
        returnData.put("response_message", "Cập nhật sự cố thành công");
        returnAjax();   
        
        String appId = ResourceBundleUtils.getConfig("customer_appid");
        String restKey = ResourceBundleUtils.getConfig("customer_restkey");        
        String requestUrl = ResourceBundleUtils.getConfig("onesignal_url");
        
        HashMap user = (HashMap)HttpSession.getInstance().getCacheAttribute(("ssouser_id" + (new EncryptDecryptUtils()).base64Encode(managerId)).getBytes());
        String payload = " {\"app_id\": \"" + appId + "\",\"filters\": [ "
                + "  	{\"field\": \"tag\", \"key\": \"user_id\", \"relation\": \"=\", \"value\": \"" + 1 + "\"}], "
                + " \"data\": {\"message_type\": \"1\",\"plan_id\":\"\"},\"contents\": "
                + " {\"en\": \"" + user.get("name") + "Đã giải quyết sự cố: " + content + "\"}} ";                

        RsMessageAction rsm = new RsMessageAction(httpUtils);
        rsm.sendRestMessage(requestUrl, payload, restKey, managerId, "1", user.get("name") + "Đã giải quyết sự cố: " + content);
    }
}
