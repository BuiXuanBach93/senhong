/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.server;

import com.hh.app.api.RsMessageAction;
import com.hh.web.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

/**
 *
 * @author Ha
 */
public class RemindTask extends TimerTask  {

    @Override
    public void run() {
        Date currentDate = new Date();
        List<Map> lstPlan = (List<Map>)HttpSession.getInstance().getStore("cache_plan");
        for(int i = 0; i < lstPlan.size(); i++) {
            Map plan = lstPlan.get(i);
            if(plan.get("start") != null)
            if((((Date)plan.get("start")).getTime() > currentDate.getTime() - 31*60*1000)
                    && (((Date)plan.get("start")).getTime() < currentDate.getTime() - 59*60*1000)) {
                String payload = " {\"app_id\": \"31288b0e-2a4e-4d18-8e14-09c9fddbde0c\",\"filters\": [ "
                        + "  	{\"field\": \"tag\", \"key\": \"user_id\", \"relation\": \"=\", \"value\": \"" + plan.get("maid_id") + "\"}], "
                        + " \"data\": {\"message_type\": \"1\",\"plan_id\":\"\"},\"contents\": "
                        + " {\"en\": \" Còn 30 phút nữa, đến giờ làm việc cho chủ nhà " + plan.get("customer_name")
                        + " - " + plan.get("customer_mobile")
                        + ", địa chỉ: " + plan.get("detail") + ", thời gian: "
                        + plan.get("start_date").toString().substring(0,10) + "(" 
                        + plan.get("start_date").toString().substring(11,16) + " - "
                        + plan.get("end_date").toString().substring(11,16)
                        + "\"}} ";                

                String payload1 = " {\"app_id\": \"31288b0e-2a4e-4d18-8e14-09c9fddbde0c\",\"filters\": [ "
                        + "  	{\"field\": \"tag\", \"key\": \"user_id\", \"relation\": \"=\", \"value\": \"" + plan.get("customer_id") + "\"}], "
                        + " \"data\": {\"message_type\": \"1\",\"plan_id\":\"\"},\"contents\": "
                        + " {\"en\": \" Còn 30 phút, giúp việc " + plan.get("maid_name")
                        + " - " + plan.get("maid_mobile") + " sẽ đến làm việc tại nhà bạn "
                        + "\"}} ";                
                
                String requestUrl = "https://onesignal.com/api/v1/notifications";
                String key = "Basic YmNjNjIyY2YtMzgzNS00NDVlLThjYzMtMGJlNmY1ZWE4MWFk";
                String address = "";
                if(plan.get("address") != null) address += " " + plan.get("address");
                if(plan.get("detail") != null) address += " " + plan.get("detail");                                  
                RsMessageAction.sendRestMessage(requestUrl, payload, key, plan.get("customer_id").toString(), plan.get("maid_id").toString(), 
                        " Còn 30 phút nữa, đến giờ làm việc cho chủ nhà " + plan.get("customer_name") + " - " + plan.get("customer_mobile") + 
                                ", địa chỉ: " + address + ", thời gian: "
                                + plan.get("start_date").toString().substring(0,10) + "(" 
                                + plan.get("start_date").toString().substring(11,16) + " - "
                                + plan.get("end_date").toString().substring(11,16)                        
                );
                
                RsMessageAction.sendRestMessage(requestUrl, payload1, key, plan.get("maid_id").toString(), plan.get("customer_id").toString(), 
                        " Còn 30 phút nữa, giúp việc " + plan.get("maid_name") + " - " + plan.get("maid_mobile") + " sẽ đến làm việc tại nhà bạn "); 
            }
        }
    }
    
}
