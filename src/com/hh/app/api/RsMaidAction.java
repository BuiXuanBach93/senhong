/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.api;

import com.hh.app.db.MaidDB;
import com.hh.app.db.PlanDB;
import com.hh.app.db.WorkDB;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author agiletech
 */
public class RsMaidAction extends RsBaseAction{

    public RsMaidAction(HttpUtils hu) {
        super(hu);
    }
    
    public void regisWorkTime() throws ParseException, IOException, SQLException {
        List lstParam = new ArrayList();

        String maidId = (String) httpUtils.getParameter("maidId");
        Integer intMaidId = null;
        if (maidId != null && !maidId.trim().isEmpty()) {
            intMaidId = Integer.parseInt(maidId);
            lstParam.add(intMaidId);
        } else {
            returnData.put("error_code", "regis_worktime_01");
            returnData.put("error_message", "Chưa nhập id người giúp việc");
            returnData.put("response_message", "Hãy nhập người giúp việc");
            returnAjax();
            return;
        }                
        
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        
        String startDate = (String)httpUtils.getParameter("startDate");
        if(startDate != null && startDate.trim().length() >= 10) {
            startDate = startDate.trim().substring(0, 10);
            Date tmpDate = df.parse(startDate.trim());
            lstParam.add(tmpDate);
        } else {
            returnData.put("error_code", "regis_worktime_02");
            returnData.put("error_message", "Chưa nhập thời gian bắt đầu");
            returnData.put("response_message", "Hãy nhập thời gian bắt đầu");
            returnAjax();
            return;
        } 
        
        String endDate = (String)httpUtils.getParameter("endDate");
        if(endDate != null && endDate.trim().length() >= 10) {
            endDate = endDate.trim().substring(0, 10);
            Date tmpDate = df.parse(endDate.trim());
            lstParam.add(tmpDate);
        } else {
            returnData.put("error_code", "regis_worktime_03");
            returnData.put("error_message", "Chưa nhập thời gian kết thúc");
            returnData.put("response_message", "Hãy nhập thời gian kết thúc");
            returnAjax();
            return;
        } 
        
        String monStart = (String)httpUtils.getParameter("monStart");
        lstParam.add(monStart);
        String monEnd = (String)httpUtils.getParameter("monEnd");
        lstParam.add(monEnd);
        
        String tueStart = (String)httpUtils.getParameter("tueStart");
        lstParam.add(tueStart);
        String tueEnd = (String)httpUtils.getParameter("tueEnd");
        lstParam.add(tueEnd);
        
        String wedStart = (String)httpUtils.getParameter("wedStart");
        lstParam.add(wedStart);
        String wedEnd = (String)httpUtils.getParameter("wedEnd");
        lstParam.add(wedEnd);
        
        String thuStart = (String)httpUtils.getParameter("thuStart");
        lstParam.add(thuStart);
        String thuEnd = (String)httpUtils.getParameter("thuEnd");
        lstParam.add(thuEnd);
        
        String friStart = (String)httpUtils.getParameter("friStart");
        lstParam.add(friStart);
        String friEnd = (String)httpUtils.getParameter("friEnd");
        lstParam.add(friEnd);
        
        String satStart = (String)httpUtils.getParameter("satStart");
        lstParam.add(satStart);
        String satEnd = (String)httpUtils.getParameter("satEnd");
        lstParam.add(satEnd);
        
        String sunStart = (String)httpUtils.getParameter("sunStart");
        lstParam.add(sunStart);
        String sunEnd = (String)httpUtils.getParameter("sunEnd");
        lstParam.add(sunEnd);
        
        lstParam.add(new Date());
        
        List lstDelete = new ArrayList();
        lstDelete.add(intMaidId);
        
        (new MaidDB()).insertWorkTime(lstParam);
        
        HttpSession.getInstance().setCacheAttribute("cache_workTime".getBytes(), (new MaidDB()).getAllWorkTime());

        returnData.put("response_message", "Đăng ký thời gian làm việc thành công");
        returnAjax();        
    }
    
    public void regisRestTime() throws ParseException, IOException, SQLException {
        List lstParam = new ArrayList();

        String maidId = (String) httpUtils.getParameter("maidId");
        Integer intMaidId = null;
        if (maidId != null && !maidId.trim().isEmpty()) {
            intMaidId = Integer.parseInt(maidId);
            lstParam.add(intMaidId);
        } else {
            returnData.put("error_code", "regis_resttime_01");
            returnData.put("error_message", "Chưa nhập id người giúp việc");
            returnData.put("response_message", "Hãy nhập người giúp việc");
            returnAjax();
            return;
        }                
        
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        
        String startDate = (String)httpUtils.getParameter("startDate");
        if(startDate != null && startDate.trim().length() >= 10) {
            startDate = startDate.trim().substring(0, 10);
            Date tmpDate = df.parse(startDate.trim());
            lstParam.add(tmpDate);
        } else {
            returnData.put("error_code", "regis_resttime_02");
            returnData.put("error_message", "Chưa nhập thời gian bắt đầu");
            returnData.put("response_message", "Hãy nhập thời gian bắt đầu");
            returnAjax();
            return;
        } 
        
        String endDate = (String)httpUtils.getParameter("endDate");
        if(endDate != null && endDate.trim().length() >= 10) {
            endDate = endDate.trim().substring(0, 10);
            Date tmpDate = df.parse(endDate.trim());
            lstParam.add(tmpDate);
        } else {
            returnData.put("error_code", "regis_resttime_03");
            returnData.put("error_message", "Chưa nhập thời gian kết thúc");
            returnData.put("response_message", "Hãy nhập thời gian kết thúc");
            returnAjax();
            return;
        } 
        
        String monStart = (String)httpUtils.getParameter("monStart");
        lstParam.add(monStart);
        String monEnd = (String)httpUtils.getParameter("monEnd");
        lstParam.add(monEnd);
        
        String tueStart = (String)httpUtils.getParameter("tueStart");
        lstParam.add(tueStart);
        String tueEnd = (String)httpUtils.getParameter("tueEnd");
        lstParam.add(tueEnd);
        
        String wedStart = (String)httpUtils.getParameter("wedStart");
        lstParam.add(wedStart);
        String wedEnd = (String)httpUtils.getParameter("wedEnd");
        lstParam.add(wedEnd);
        
        String thuStart = (String)httpUtils.getParameter("thuStart");
        lstParam.add(thuStart);
        String thuEnd = (String)httpUtils.getParameter("thuEnd");
        lstParam.add(thuEnd);
        
        String friStart = (String)httpUtils.getParameter("friStart");
        lstParam.add(friStart);
        String friEnd = (String)httpUtils.getParameter("friEnd");
        lstParam.add(friEnd);
        
        String satStart = (String)httpUtils.getParameter("satStart");
        lstParam.add(satStart);
        String satEnd = (String)httpUtils.getParameter("satEnd");
        lstParam.add(satEnd);
        
        String sunStart = (String)httpUtils.getParameter("sunStart");
        lstParam.add(sunStart);
        String sunEnd = (String)httpUtils.getParameter("sunEnd");
        lstParam.add(sunEnd);
        
        lstParam.add(new Date());
        
        (new MaidDB()).insertRestTime(lstParam);
        
        HttpSession.getInstance().setCacheAttribute("cache_restTime".getBytes(), (new MaidDB()).getAllRestTime());
        
        returnData.put("response_message", "Đăng ký thời gian nghỉ thành công");
        returnAjax();
    }
    
    public void getWorkTime() throws IOException {
        List<Map> lstWork = (List<Map>)HttpSession.getInstance().getCacheAttribute("cache_workTime".getBytes());
        String maidId = (String)httpUtils.getParameter("maidId");
        returnData.put("data", (new WorkDB()).getWorkTimeByMaidId(lstWork, maidId));
        returnAjax();        
    }
    
    public void getRestTime() throws IOException {
        List<Map> lstRest = (List<Map>)HttpSession.getInstance().getCacheAttribute("cache_restTime".getBytes());
        String maidId = (String)httpUtils.getParameter("maidId");
        returnData.put("data", (new WorkDB()).getWorkTimeByMaidId(lstRest, maidId));
        returnAjax();
    }
    
    public void getSalary() throws IOException, SQLException {
        String maidId = (String)httpUtils.getParameter("maidId");
        returnData.put("data", (new PlanDB()).getSalaryByMaidId(Integer.parseInt(maidId)));
        returnAjax();        
    }
    
    public void updateLocation() {
        String userId = (String)httpUtils.getParameter("userId");
        String latitude = (String)httpUtils.getParameter("latitude");
        String longitude = (String)httpUtils.getParameter("longitude");
        
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = df.format(new Date());        
        
        Calendar cal = Calendar.getInstance();
        Integer hour = cal.get(Calendar.HOUR_OF_DAY);
        Integer minute = cal.get(Calendar.MINUTE);
        if(0 <= minute && minute < 15) minute = 0;
        else if(15 <= minute && minute < 30) minute = 15;
        else if(30 <= minute && minute < 45) minute = 30;
        else if(45 <= minute && minute < 60) minute = 45;
        
        if(HttpSession.getInstance().getSessionAttribute("location_" + userId + "_" + currentDate + "_" + hour + "_" + minute, httpUtils.SESSION_DEFAULT_KEY) == null) {
            HttpSession.getInstance().createSession("location_" + userId + "_" + currentDate + "_" + hour + "_" + minute);
        }
        
        HttpSession.getInstance().setSessionAttribute("location_" + userId + "_" + currentDate + "_" + hour + "_" + minute, "location", latitude + "_" + longitude);        
    }
    
    public void maidRating() throws IOException, SQLException {
        List lstParam = new ArrayList();        
        
        String content = (String)httpUtils.getParameter("content");
        lstParam.add(content);
        
        String ratingCustomer = (String)httpUtils.getParameter("ratingCustomer");
        Integer intRatingCustomer = null;
        if(ratingCustomer != null && !ratingCustomer.trim().isEmpty()) {
            intRatingCustomer = Integer.parseInt(ratingCustomer);
            lstParam.add(intRatingCustomer);
        } else {
            returnData.put("error_code", "maidrating_02");
            returnData.put("error_message", "Rating chủ nhà bắt buộc nhập");
            returnData.put("response_message", "Hãy nhập sao chủ nhà");
            returnAjax();
            return;
        } 
        
        String ratingWork = (String)httpUtils.getParameter("ratingWork");
        Integer intRatingWork = null;
        if(ratingWork != null && !ratingWork.trim().isEmpty()) {
            intRatingWork = Integer.parseInt(ratingWork);
            lstParam.add(intRatingWork);
        } else {
            returnData.put("error_code", "maidrating_03");
            returnData.put("error_message", "Rating công việc bắt buộc nhập");
            returnData.put("response_message", "Hãy nhập sao công việc");
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
            returnData.put("error_code", "maidrating_04");
            returnData.put("error_message", "Lỗi thiếu plan id");
            returnAjax();
            return;
        }
        
        (new PlanDB()).updateMaidRate(lstParam);
        
        returnData.put("response_message", "Gửi đánh giá thành công");
        returnAjax();
        
        Map plan = (Map)HttpSession.getInstance().getStoreAttribute("cache_plan", id);
        String cappId = ResourceBundleUtils.getConfig("customer_appid");
        String crestKey = ResourceBundleUtils.getConfig("customer_restkey");                    
        String requestUrl = ResourceBundleUtils.getConfig("onesignal_url");        
        String payload = " {\"app_id\": \"" + cappId + "\",\"filters\": [ "
                + "  	{\"field\": \"tag\", \"key\": \"user_id\", \"relation\": \"=\", \"value\": \"" + 1 + "\"}], "
                + " \"data\": {\"message_type\": \"1\",\"plan_id\":\"" + plan.get("plan_id") + "\"},\"contents\": "
                + " {\"en\": \"Khách hàng " + plan.get("customer_name") 
                + " được đánh giá " + ratingCustomer 
                + " sao bởi giúp việc " + plan.get("maid_name")
                + "\"}} ";

        RsMessageAction.sendRestMessage(requestUrl, payload, crestKey, plan.get("maid_id").toString(), "1", 
                "Khách hàng " + plan.get("customer_name") 
                + " được đánh giá " + ratingCustomer 
                + " sao bởi giúp việc " + plan.get("maid_name"));
        
        //----- update user profile -------------------------------
        Map user = (Map)HttpSession.getInstance().getCacheAttribute(("ssouser_id" + (new EncryptDecryptUtils()).base64Encode(plan.get("customer_id").toString())).getBytes());
        Integer ratingCount = 0;
        if(user.get("count_rating") != null) {
            ratingCount = Integer.parseInt(user.get("count_rating").toString());
        }
        Float currentRating = 0.0f;
        if(user.get("rating") != null) {
            currentRating = Float.parseFloat(user.get("rating").toString());
        }
        float finalRating = Float.parseFloat(intRatingCustomer.toString()) + currentRating * ratingCount;
        user.put("rating", finalRating);
        HttpSession.getInstance().setCacheAttribute(("ssouser_id" + (new EncryptDecryptUtils()).base64Encode(plan.get("customer_id").toString())).getBytes(), user);
        (new PlanDB()).updateUserRating(finalRating, ratingCount + 1, Integer.parseInt(plan.get("customer_id").toString()));
        //---------------------------------------------------------        
    }
    
    public void setLocation() throws IOException {
        String userId = (String)httpUtils.getParameter("userId");
        HashMap location = new HashMap();
        location.put("userId", userId);
        location.put("latitude", httpUtils.getParameter("latitude"));
        location.put("longitude", httpUtils.getParameter("longitude"));
        location.put("time", new Date());
                
        List<Map> lstLocation = (List<Map>)HttpSession.getInstance().getCacheAttribute(("location_" + userId).getBytes());
        if(lstLocation != null) {
            lstLocation.add(location);
            if(lstLocation.size() >= 1000) {
                lstLocation.remove(0);
            }
        } else {
            lstLocation = new ArrayList();
            lstLocation.add(location);
        }
        
        HttpSession.getInstance().setCacheAttribute(("location_" + userId).getBytes(), lstLocation);
        returnData.put("response_message", "Gửi tọa độ thành công");
        returnAjax();
    }
    
    public void getLocation() throws IOException, ParseException {
        String userId = (String) httpUtils.getParameter("userId");
        String date = (String) httpUtils.getParameter("date");
        String time = (String) httpUtils.getParameter("longitude");
        
        List<Map> lstLocation = (List<Map>)HttpSession.getInstance().getCacheAttribute(("location_" + userId).getBytes());
        if(lstLocation != null && userId != null && date != null && time != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date dateParam = df.parse(date.trim() +  " " + time.trim());
            int index = 0;
            for(int i = 0; i < lstLocation.size() - 1; i++) {
                Date beforeDate = (Date)lstLocation.get(i).get("time");
                Date afterDate = (Date)lstLocation.get(i+1).get("time");
                if(beforeDate.before(dateParam) && afterDate.after(dateParam)) {
                    if(dateParam.getTime() - beforeDate.getTime() < afterDate.getTime() - dateParam.getTime()) {
                        index = i;
                    } else index = i + 1;
                    break;
                }
            }
            this.returnData.put("location", lstLocation.get(index));
            returnAjax();
        } else {
            this.returnData.put("response_message", "Không có dữ liệu");
            returnAjax();
        }
    }
}
