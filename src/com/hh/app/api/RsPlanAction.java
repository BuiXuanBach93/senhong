/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.api;

import com.hh.app.db.AddressDB;
import com.hh.app.db.MaidDB;
import com.hh.app.db.PlanDB;
import com.hh.app.db.RestDB;
import com.hh.database.DatabaseConnector;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author buixu
 */
public class RsPlanAction extends RsBaseAction{
    final static double PLAN_DISTANCE_TO_WORK = 0.1;
    
    public RsPlanAction(HttpUtils hu) {
        super(hu);
    }
    
    private void getAppPlans(String id, int intPage, int intSize, String field) throws IOException {
        if(id != null && !id.trim().isEmpty()) {
            List<Map> plans = (List<Map>)HttpSession.getInstance().getStore("cache_plan");
            List<Map> lstPlan = new ArrayList();
            for(int i = 0; i < plans.size(); i++) {
                if(plans.get(i).get(field) != null && 
                        plans.get(i).get(field).toString().equals(id)) {
                    lstPlan.add(plans.get(i));
                }
            }
            
            List lstResult = new ArrayList();
            if(lstPlan.size() > 0) {
                for(int i = 0; i < lstPlan.size(); i++) {
                    for(int j = i+1; j < lstPlan.size(); j++) {
                        if(((Date)lstPlan.get(i).get("start")).before(((Date)lstPlan.get(j).get("start")))) {
                            Map temp = lstPlan.get(i);
                            lstPlan.set(i, lstPlan.get(j));
                            lstPlan.set(j, temp);
                        }
                    }
                }

                int index = -1;
                for(int i = 0; i < lstPlan.size() - 1; i++) {
                    if(((Date)lstPlan.get(i).get("start")).after(new Date()) &&
                            ((Date)lstPlan.get(i+1).get("start")).before(new Date())) {
                        index = i;
                    }
                }
                if(index == -1) {
                    if(((Date)lstPlan.get(0).get("start")).after(new Date())) {
                        for(int i = lstPlan.size() - intSize; i < index - 1; i++ ) {
                            if(i + intPage*intSize >= 0 && i + intPage*intSize < lstPlan.size()) 
                                lstResult.add(lstPlan.get(i + intPage*intSize));
                        }
                    }
                    else {
                        for(int i = 0; i < intSize; i++ ) {
                            if(i + intPage*intSize >= 0 && i + intPage*intSize < lstPlan.size()) 
                                lstResult.add(lstPlan.get(i + intPage*intSize));
                        }
                    }
                } else {
                    for(int i = index - intSize/2; i < index + intSize/2; i++ ) {
                        if(i + intPage*intSize >= 0 && i + intPage*intSize < lstPlan.size()) 
                            lstResult.add(lstPlan.get(i + intPage*intSize));
                    }
                }
            
            }
            this.returnData.put("data", lstPlan);
        }
        returnAjax();        
    }
    
    public void getPlansByCustomerId() throws IOException, SQLException, ParseException {
        String customerId = (String)httpUtils.getParameter("customerId");
        String page = (String)httpUtils.getParameter("page");
        String size = (String)httpUtils.getParameter("size");
        
        int intPage = 0;
        if(page != null) intPage = Integer.parseInt(page);
        int intSize = 10;
        if(size != null) intSize = Integer.parseInt(size);
        
        getAppPlans(customerId, intPage, intSize, "customer_id");
    }
    
    public void getPlansByMaidId() throws IOException, SQLException, ParseException {
        String customerId = (String)httpUtils.getParameter("maidId");
        String page = (String)httpUtils.getParameter("page");
        String size = (String)httpUtils.getParameter("size");
        
        int intPage = 0;
        if(page != null) intPage = Integer.parseInt(page);
        int intSize = 10;
        if(size != null) intSize = Integer.parseInt(size);
        
        getAppPlans(customerId, intPage, intSize, "maid_id");
    }
     
    public void planStartWork() throws SQLException, IOException{
        String planId = (String)httpUtils.getParameter("planId");
        String longitude = (String)httpUtils.getParameter("longitude");
        String latitude = (String)httpUtils.getParameter("latitude");
        
        PlanDB planDB = new PlanDB();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Map plan = (Map)HttpSession.getInstance().getStoreAttribute("cache_plan", planId);
        int addressId = Integer.parseInt(plan.get("address_id").toString());
        double distance = (new AddressDB())
                .getDistanceFromAddressId(addressId, new Double(longitude), new Double(latitude));
        Date startDate = new Date();
        planDB.updatePlanStartWork(Integer.parseInt(planId),startDate, 4, new Double(latitude), new Double(longitude), distance);

        plan.put("real_start", df.format(startDate));
        plan.put("real_lat", new Double(latitude));
        plan.put("real_long", new Double(longitude));
        plan.put("status", 4);
        plan.put("distance", distance);
        HttpSession.getInstance().setStoreAttribute("cache_plan", planId, plan);
        
        returnData.put("response_message", "Bắt đầu công việc thành công");
        returnAjax();
        
        String cappId = ResourceBundleUtils.getConfig("customer_appid");
        String crestKey = ResourceBundleUtils.getConfig("customer_restkey");                    
        String requestUrl = ResourceBundleUtils.getConfig("onesignal_url");   
        String address = "";
        if(plan.get("address") != null) address += " " + plan.get("address");
        if(plan.get("detail") != null) address += " " + plan.get("detail");        
        
        String payload = " {\"app_id\": \"" + cappId + "\",\"filters\": [ "
                + "  	{\"field\": \"tag\", \"key\": \"user_id\", \"relation\": \"=\", \"value\": \"" + 1 + "\"}], "
                + " \"data\": {\"message_type\": \"1\",\"plan_id\":\"" + plan.get("plan_id") + "\"},\"contents\": "
                + " {\"en\": \"" + plan.get("maid_name") 
                + " BẮT ĐẦU LÀM VIỆC của chủ nhà " + plan.get("customer_name") 
                + ", địa chỉ: " + address
                + ", thời gian: " 
                + plan.get("start_date").toString().substring(0,10) + "(" 
                + plan.get("start_date").toString().substring(11,16) + " - "
                + plan.get("end_date").toString().substring(11,16)
                + ")\"}} ";

        String payload1 = " {\"app_id\": \"" + cappId + "\",\"filters\": [ "
                + "  	{\"field\": \"tag\", \"key\": \"user_id\", \"relation\": \"=\", \"value\": \"" + plan.get("customer_id") + "\"}], "
                + " \"data\": {\"message_type\": \"1\",\"plan_id\":\"" + plan.get("plan_id") + "\"},\"contents\": "
                + " {\"en\": \"" + plan.get("maid_name") 
                + " BẮT ĐẦU LÀM VIỆC của chủ nhà " + plan.get("customer_name") 
                + ", địa chỉ: " + address
                + ", thời gian: " 
                + plan.get("start_date").toString().substring(0,10) + "(" 
                + plan.get("start_date").toString().substring(11,16) + " - "
                + plan.get("end_date").toString().substring(11,16)
                + ")\"}} ";                

        RsMessageAction.sendRestMessage(requestUrl, payload, crestKey, plan.get("maid_id").toString(), "1", 
                plan.get("maid_name") 
                + " BẮT ĐẦU LÀM VIỆC của chủ nhà " + plan.get("customer_name") 
                + ", địa chỉ: " + address
                + ", thời gian: " 
                + plan.get("start_date").toString().substring(0,10) + "(" 
                + plan.get("start_date").toString().substring(11,16) + " - "
                + plan.get("end_date").toString().substring(11,16)
                + ")");
        RsMessageAction.sendRestMessage(requestUrl, payload1, crestKey, plan.get("maid_id").toString(), plan.get("customer_id").toString(), 
                plan.get("maid_name") 
                + " BẮT ĐẦU LÀM VIỆC của chủ nhà " + plan.get("customer_name") 
                + ", địa chỉ: " + address
                + ", thời gian: " 
                + plan.get("start_date").toString().substring(0,10) + "(" 
                + plan.get("start_date").toString().substring(11,16) + " - "
                + plan.get("end_date").toString().substring(11,16)
                + ")");
    }
    
    public void planFinishWork() throws SQLException, IOException{
        String planId = (String)httpUtils.getParameter("planId");
        String longitude = (String)httpUtils.getParameter("longitude");
        String latitude = (String)httpUtils.getParameter("latitude");
        
        PlanDB planDB = new PlanDB();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Map plan = (Map)HttpSession.getInstance().getStoreAttribute("cache_plan", planId);
        int addressId = Integer.parseInt(plan.get("address_id").toString());
        double distance = (new AddressDB())
                .getDistanceFromAddressId(addressId, new Double(longitude), new Double(latitude));
        Date endDate = new Date();
        planDB.updatePlanFinishWork(Integer.parseInt(planId),endDate, 5, new Double(latitude), new Double(longitude), distance);
        
        plan.put("real_end", df.format(endDate));
        plan.put("finish_lat", new Double(latitude));
        plan.put("finish_long", new Double(longitude));
        plan.put("status", 5);
        plan.put("finish_distance", distance);
        HttpSession.getInstance().setStoreAttribute("cache_plan", planId, plan);
        
        returnData.put("response_message", "Hoàn thành công việc thành công");
        returnAjax();
        
        String cappId = ResourceBundleUtils.getConfig("customer_appid");
        String crestKey = ResourceBundleUtils.getConfig("customer_restkey");                    
        String requestUrl = ResourceBundleUtils.getConfig("onesignal_url");                            
        String address = "";
        if(plan.get("address") != null) address += " " + plan.get("address");
        if(plan.get("detail") != null) address += " " + plan.get("detail");
        
        //----- update user profile -------------------------------
        Map user = (Map)HttpSession.getInstance().getCacheAttribute(("ssouser_id" + (new EncryptDecryptUtils()).base64Encode(plan.get("maid_id").toString())).getBytes());
        Date start = (Date)plan.get("start");
        Date end = (Date)plan.get("end");
        Long minute = (end.getTime() - start.getTime())/(60 * 1000);
        
        Integer hourWork = 0;
        if(user.get("hour_work") != null) {
            hourWork = Integer.parseInt(user.get("hour_work").toString());
        }
        user.put("hour_work", hourWork + minute);
        
        Integer salary = 0;
        if(user.get("salary") != null) {
            salary = Integer.parseInt(user.get("salary").toString());
        }         
        if(plan.get("salary") != null && user.get("salary_level") != null) {
            salary += Math.round(Float.parseFloat(plan.get("salary").toString()) * Float.parseFloat(user.get("salary_level").toString()));
        }        
        user.put("salary", salary);
        HttpSession.getInstance().setCacheAttribute(("ssouser_id" + (new EncryptDecryptUtils()).base64Encode(plan.get("maid_id").toString())).getBytes(), user);
        planDB.updateUserSalary(hourWork, salary, Integer.parseInt(plan.get("maid_id").toString()));
        //---------------------------------------------------------
        Map customer = (Map)HttpSession.getInstance().getCacheAttribute(("ssouser_id" + (new EncryptDecryptUtils()).base64Encode(plan.get("customer_id").toString())).getBytes());
        Integer amount = 0;
        if(user.get("amount") != null) {
            amount = Integer.parseInt(user.get("amount").toString());
        } 
        if(plan.get("price") != null) {
            amount += Integer.parseInt(plan.get("price").toString());
        } 
        customer.put("amount", amount);
        Integer hourOrder = 0;
        if(customer.get("hour_work") != null) {
            hourOrder = Integer.parseInt(customer.get("hour_work").toString());
        }
        customer.put("hour_work", hourOrder + minute);
        HttpSession.getInstance().setCacheAttribute(("ssouser_id" + (new EncryptDecryptUtils()).base64Encode(plan.get("customer_id").toString())).getBytes(), customer);
        planDB.updateUserAmount(hourWork, amount, Integer.parseInt(plan.get("customer_id").toString()));
        //---------------------------------------------------------
        
        String payload = " {\"app_id\": \"" + cappId + "\",\"filters\": [ "
                + "  	{\"field\": \"tag\", \"key\": \"user_id\", \"relation\": \"=\", \"value\": \"" + 1 + "\"}], "
                + " \"data\": {\"message_type\": \"1\",\"plan_id\":\"" + plan.get("plan_id") + "\"},\"contents\": "
                + " {\"en\": \"" + plan.get("maid_name") 
                + " đã HOÀN THÀNH VIỆC của chủ nhà " + plan.get("customer_name") 
                + ", địa chỉ: " + address
                + ", thời gian: " 
                + plan.get("start_date").toString().substring(0,10) + "(" 
                + plan.get("start_date").toString().substring(11,16) + " - "
                + plan.get("end_date").toString().substring(11,16)
                + ")\"}} ";

        String payload1 = " {\"app_id\": \"" + cappId + "\",\"filters\": [ "
                + "  	{\"field\": \"tag\", \"key\": \"user_id\", \"relation\": \"=\", \"value\": \"" + plan.get("customer_id") + "\"}], "
                + " \"data\": {\"message_type\": \"1\",\"plan_id\":\"" + plan.get("plan_id") + "\"},\"contents\": "
                + " {\"en\": \"" + plan.get("maid_name") 
                + " đã HOÀN THÀNH VIỆC của chủ nhà " + plan.get("customer_name") 
                + ", địa chỉ: " + address
                + ", thời gian: " 
                + plan.get("start_date").toString().substring(0,10) + "(" 
                + plan.get("start_date").toString().substring(11,16) + " - "
                + plan.get("end_date").toString().substring(11,16)
                + ")\"}} ";                

        RsMessageAction.sendRestMessage(requestUrl, payload, crestKey, plan.get("maid_id").toString(), "1", 
                plan.get("maid_name") 
                + " đã HOÀN THÀNH VIỆC của chủ nhà " + plan.get("customer_name") 
                + ", địa chỉ: " + address
                + ", thời gian: " 
                + plan.get("start_date").toString().substring(0,10) + "(" 
                + plan.get("start_date").toString().substring(11,16) + " - "
                + plan.get("end_date").toString().substring(11,16)
                + ")"
                );
        RsMessageAction.sendRestMessage(requestUrl, payload1, crestKey, plan.get("maid_id").toString(), plan.get("customer_id").toString(), 
                plan.get("maid_name") 
                + " đã HOÀN THÀNH VIỆC của chủ nhà " + plan.get("customer_name") 
                + ", địa chỉ: " + address
                + ", thời gian: " 
                + plan.get("start_date").toString().substring(0,10) + "(" 
                + plan.get("start_date").toString().substring(11,16) + " - "
                + plan.get("end_date").toString().substring(11,16)
                + ")"
                );
    }
    
    public void planUpdateStatus() throws SQLException, IOException{
        String planStr = (String)httpUtils.getParameter("planId");
        String statusStr = (String)httpUtils.getParameter("status");
        if(StringUtils.isNotEmpty(planStr)&&StringUtils.isNotEmpty(statusStr)){
            PlanDB planDB = new PlanDB();
            Integer intStatus = Integer.parseInt(statusStr);
            Integer intPlanId = Integer.parseInt(planStr);
            planDB.updatePlanStatus(intPlanId, intStatus);
            Map plan = (Map)HttpSession.getInstance().getStoreAttribute("cache_plan", planStr);
            plan.put("status", intStatus);
            HttpSession.getInstance().setStoreAttribute("cache_plan", planStr, plan);
            
            returnData.put("response_message", "Cập nhật trạng thái thành công");
            returnAjax();
            if(intStatus == 2) {
                String cappId = ResourceBundleUtils.getConfig("customer_appid");
                String crestKey = ResourceBundleUtils.getConfig("customer_restkey");                    
                String requestUrl = ResourceBundleUtils.getConfig("onesignal_url");
                String address = "";
                if(plan.get("address") != null) address += " " + plan.get("address");
                if(plan.get("detail") != null) address += " " + plan.get("detail"); 
                
                String payload = " {\"app_id\": \"" + cappId + "\",\"filters\": [ "
                        + "  	{\"field\": \"tag\", \"key\": \"user_id\", \"relation\": \"=\", \"value\": \"" + 1 + "\"}], "
                        + " \"data\": {\"message_type\": \"1\",\"plan_id\":\"" + plan.get("plan_id") + "\"},\"contents\": "
                        + " {\"en\": \"" + plan.get("maid_name") 
                        + " ĐÃ NHẬN VIỆC của chủ nhà " + plan.get("customer_name") 
                        + ", địa chỉ: " + address
                        + ", thời gian: " 
                        + plan.get("start_date").toString().substring(0,10) + "(" 
                        + plan.get("start_date").toString().substring(11,16) + " - "
                        + plan.get("end_date").toString().substring(11,16)
                        + ")\"}} ";
                
                String payload1 = " {\"app_id\": \"" + cappId + "\",\"filters\": [ "
                        + "  	{\"field\": \"tag\", \"key\": \"user_id\", \"relation\": \"=\", \"value\": \"" + plan.get("customer_id") + "\"}], "
                        + " \"data\": {\"message_type\": \"1\",\"plan_id\":\"" + plan.get("plan_id") + "\"},\"contents\": "
                        + " {\"en\": \"" + plan.get("maid_name") 
                        + " ĐÃ NHẬN VIỆC của chủ nhà " + plan.get("customer_name") 
                        + ", địa chỉ: " + address
                        + ", thời gian: " 
                        + plan.get("start_date").toString().substring(0,10) + "(" 
                        + plan.get("start_date").toString().substring(11,16) + " - "
                        + plan.get("end_date").toString().substring(11,16)
                        + ")\"}} ";                

                RsMessageAction.sendRestMessage(requestUrl, payload, crestKey, plan.get("maid_id").toString(), "1", 
                        plan.get("maid_name") 
                        + " ĐÃ NHẬN VIỆC của chủ nhà " + plan.get("customer_name") 
                        + ", địa chỉ: " + address
                        + ", thời gian: " 
                        + plan.get("start_date").toString().substring(0,10) + "(" 
                        + plan.get("start_date").toString().substring(11,16) + " - "
                        + plan.get("end_date").toString().substring(11,16)
                        + ")"
                        );
                RsMessageAction.sendRestMessage(requestUrl, payload1, crestKey, plan.get("maid_id").toString(), plan.get("customer_id").toString(),
                        plan.get("maid_name") 
                        + " ĐÃ NHẬN VIỆC của chủ nhà " + plan.get("customer_name") 
                        + ", địa chỉ: " + address
                        + ", thời gian: " 
                        + plan.get("start_date").toString().substring(0,10) + "(" 
                        + plan.get("start_date").toString().substring(11,16) + " - "
                        + plan.get("end_date").toString().substring(11,16)
                        + ")"
                        );
            }
        }else{
            returnData.put("error_code", "UpdatePlanStatus");
            returnData.put("error_message", "Thiếu dữ liệu");
            returnAjax();
        }
    }
    
    public void sendPlanRest() throws IOException, SQLException, ParseException {
        PlanDB plandb = new PlanDB();
        Integer planId = null;
        if(httpUtils.getParameter("planId") != null)
            planId = Integer.parseInt((String)httpUtils.getParameter("planId"));
        plandb.updatePlanStatus(planId, 3);
        
        /*Map plan = plandb.getPlanById(planId);
        String startDate = (String)plan.get("start_date");
        String endDate = (String)plan.get("end_date");
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date restDate = df.parse(startDate.trim());
        Calendar cal = Calendar.getInstance();
        cal.setTime(restDate);
        Integer dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        
        List lstParam = new ArrayList();
        lstParam.add(plan.get("maid_id"));
        lstParam.add(restDate);
        lstParam.add(restDate);
        if(dayOfWeek == 2) {
            lstParam.add(startDate.substring(11,16));
            lstParam.add(endDate.substring(11,16));
        } else {
            lstParam.add(null);
            lstParam.add(null);            
        }
        if(dayOfWeek == 3) {
            lstParam.add(startDate.substring(11,16));
            lstParam.add(endDate.substring(11,16));
        } else {
            lstParam.add(null);
            lstParam.add(null);            
        }
        if(dayOfWeek == 4) {
            lstParam.add(startDate.substring(11,16));
            lstParam.add(endDate.substring(11,16));
        } else {
            lstParam.add(null);
            lstParam.add(null);            
        }
        if(dayOfWeek == 5) {
            lstParam.add(startDate.substring(11,16));
            lstParam.add(endDate.substring(11,16));
        } else {
            lstParam.add(null);
            lstParam.add(null);            
        }
        if(dayOfWeek == 6) {
            lstParam.add(startDate.substring(11,16));
            lstParam.add(endDate.substring(11,16));
        } else {
            lstParam.add(null);
            lstParam.add(null);            
        }
        if(dayOfWeek == 7) {
            lstParam.add(startDate.substring(11,16));
            lstParam.add(endDate.substring(11,16));
        } else {
            lstParam.add(null);
            lstParam.add(null);            
        }
        if(dayOfWeek == 1) {
            lstParam.add(startDate.substring(11,16));
            lstParam.add(endDate.substring(11,16));
        } else {
            lstParam.add(null);
            lstParam.add(null);            
        }
        lstParam.add(new Date());
        (new RestDB()).insertRest(lstParam);
        
        HttpSession.getInstance().setCacheAttribute("cache_restTime".getBytes(), (new MaidDB()).getAllRestTime());    */    
        Map planCache = (Map)HttpSession.getInstance().getStoreAttribute("cache_plan", planId.toString());
        planCache.put("status", 3);
        HttpSession.getInstance().setStoreAttribute("cache_plan", planId.toString(), planCache);

        returnData.put("response_message", "Báo nghỉ thành công");
        returnAjax();        
    }    
    
}
