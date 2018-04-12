/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.api;

import com.hh.app.db.MaidDB;
import com.hh.app.db.PlanDB;
import com.hh.util.EncryptDecryptUtils;
import com.hh.util.ResourceBundleUtils;
import com.hh.web.HttpSession;
import com.hh.web.HttpUtils;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author agiletech
 */
public class RsCustomerAction extends RsBaseAction{

    public RsCustomerAction(HttpUtils hu) {
        super(hu);
    }
        
    public void customerRating() throws IOException, SQLException {
        List lstParam = new ArrayList();        
        
        String content = (String)httpUtils.getParameter("content");
        lstParam.add(content);
        
        String rating = (String)httpUtils.getParameter("rating");
        Integer intRating = null;
        if(rating != null && !rating.trim().isEmpty()) {
            intRating = Integer.parseInt(rating);
            lstParam.add(intRating);
        } else {
            returnData.put("error_code", "customerrating_02");
            returnData.put("error_message", "Rating bắt buộc nhập");
            returnData.put("response_message", "Hãy nhập sao chất lượng");
            returnAjax();
            return;
        }        
        
        lstParam.add(new Date());
        
        String id = (String)httpUtils.getParameter("planId");
        Integer intId = null;
        if(id != null && !id.trim().isEmpty()) {
            intId = Integer.parseInt(id);
            lstParam.add(intId);
        } else {
            returnData.put("error_code", "customerrating_03");
            returnData.put("error_message", "Lỗi thiếu plan id");
            returnAjax();
            return;
        }
        
        (new PlanDB()).updateCustomerRate(lstParam);
        
        returnData.put("response_message", "Gửi đánh giá thành công");
        returnAjax();  
        
        Map plan = (Map)HttpSession.getInstance().getStoreAttribute("cache_plan", id);
        String cappId = ResourceBundleUtils.getConfig("customer_appid");
        String crestKey = ResourceBundleUtils.getConfig("customer_restkey");                    
        String mappId = ResourceBundleUtils.getConfig("maid_appid");
        String mrestKey = ResourceBundleUtils.getConfig("maid_restkey");        
        String requestUrl = ResourceBundleUtils.getConfig("onesignal_url");        
        String payload = " {\"app_id\": \"" + cappId + "\",\"filters\": [ "
                + "  	{\"field\": \"tag\", \"key\": \"user_id\", \"relation\": \"=\", \"value\": \"" + 1 + "\"}], "
                + " \"data\": {\"message_type\": \"1\",\"plan_id\":\"" + plan.get("plan_id") + "\"},\"contents\": "
                + " {\"en\": \"Giúp việc " + plan.get("maid_name") 
                + " được đánh giá " + rating 
                + " sao bởi giúp việc " + plan.get("customer_name")
                + "\"}} ";
        
        String payload1 = " {\"app_id\": \"" + mappId + "\",\"filters\": [ "
                + "  	{\"field\": \"tag\", \"key\": \"user_id\", \"relation\": \"=\", \"value\": \"" + plan.get("maid_id") + "\"}], "
                + " \"data\": {\"message_type\": \"1\",\"plan_id\":\"" + plan.get("plan_id") + "\"},\"contents\": "
                + " {\"en\": \"Bạn được đánh giá " + rating 
                + " sao bởi khách hàng " + plan.get("customer_name")
                + "\"}} ";        

        RsMessageAction.sendRestMessage(requestUrl, payload, mrestKey, plan.get("maid_id").toString(), "1", 
                "Giúp việc " + plan.get("maid_name") 
                + " được đánh giá " + rating 
                + " sao bởi khách hàng " + plan.get("customer_name")
                );
        
        RsMessageAction.sendRestMessage(requestUrl, payload1, crestKey, plan.get("customer_id").toString(), plan.get("maid_id").toString(), 
                " Bạn được đánh giá " + rating 
                + " sao bởi khách hàng " + plan.get("customer_name")
                );
        
        //----- update user profile -------------------------------
        Map user = (Map)HttpSession.getInstance().getCacheAttribute(("ssouser_id" + (new EncryptDecryptUtils()).base64Encode(plan.get("maid_id").toString())).getBytes());
        Integer ratingCount = 0;
        if(user.get("count_rating") != null) {
            ratingCount = Integer.parseInt(user.get("count_rating").toString());
        }
        Float currentRating = 0.0f;
        if(user.get("rating") != null) {
            currentRating = Float.parseFloat(user.get("rating").toString());
        }
        float finalRating = Float.parseFloat(intRating.toString()) + currentRating * ratingCount;
        user.put("rating", finalRating);
        HttpSession.getInstance().setCacheAttribute(("ssouser_id" + (new EncryptDecryptUtils()).base64Encode(plan.get("maid_id").toString())).getBytes(), user);
        (new PlanDB()).updateUserRating(finalRating, ratingCount + 1, Integer.parseInt(plan.get("maid_id").toString()));
        //---------------------------------------------------------        
    }
}
