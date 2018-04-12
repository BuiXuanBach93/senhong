/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.api;

import com.hh.app.db.AddressDB;
import com.hh.app.db.MaidDB;
import com.hh.app.db.OrderDB;
import com.hh.app.db.PlanDB;
import com.hh.app.db.ReceiptDB;
import com.hh.server.HHServer;
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
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author agiletech
 */
public class RsOrderAction extends RsBaseAction{
    
    public static AtomicInteger orderCount = new AtomicInteger(0);

    public RsOrderAction(HttpUtils hu) {
        super(hu);
    }
    
    public void createOrder() throws IOException, SQLException, ParseException {
        List lstParam = new ArrayList();
        List lstParam1 = new ArrayList();
        List lstParamReceipt = new ArrayList();
        
        String customerId = (String)httpUtils.getParameter("customerId");
        Integer intCustomerId = null;
        if(customerId != null && !customerId.trim().isEmpty()) {
            intCustomerId = Integer.parseInt(customerId);
        } else {
            returnData.put("error_code", "createorder_01");
            returnData.put("error_message", "Khách hàng bắt buộc nhập");
            returnData.put("response_message", "Hãy nhập khách hàng");
            returnAjax();
            return;
        }
        lstParam.add(intCustomerId);
        
        Integer intAddressId = null;
        String addressId = (String) httpUtils.getParameter("addressId");
        if(addressId == null || addressId.trim().isEmpty()) {
            String address = (String)httpUtils.getParameter("address");
            if(!(address != null && !address.trim().isEmpty())) {
                returnData.put("error_code", "createorder_03");
                returnData.put("error_message", "Chưa nhập địa chỉ");
                returnData.put("response_message", "Hãy nhập địa chỉ");
                address = "address";
            }
            
            String detail = (String)httpUtils.getParameter("detail");
            if(!(detail != null && !detail.trim().isEmpty())) {
                returnData.put("error_code", "createorder_03");
                returnData.put("error_message", "Chưa nhập địa chỉ");
                returnData.put("response_message", "Hãy nhập địa chỉ");
                detail = "detail";
            }

            String latitude = (String)httpUtils.getParameter("latitude");
            Float floatLatitude = null;
            if(latitude != null && !latitude.trim().isEmpty()) {
                floatLatitude = Float.parseFloat(latitude);
            } else {
                returnData.put("error_code", "createorder_04");
                returnData.put("error_message", "Vĩ độ bắt buộc nhập");
                returnData.put("response_message", "Hãy chọn địa điểm trên bản đồ");
                floatLatitude = 21.02992f;
            } 

            String longitude = (String)httpUtils.getParameter("longitude");
            Float floatLongitude = null;
            if(longitude != null && !longitude.trim().isEmpty()) {
                floatLongitude = Float.parseFloat(longitude);
            } else {
                returnData.put("error_code", "createorder_05");
                returnData.put("error_message", "Kinh độ bắt buộc nhập");
                returnData.put("response_message", "Hãy chọn địa điểm trên bản đồ");
                floatLongitude = 105.77463f;
            } 

            List lstAddress = new ArrayList();
            lstAddress.add(address);
            lstAddress.add(detail);
            lstAddress.add(floatLatitude);
            lstAddress.add(floatLongitude);
            lstAddress.add(intCustomerId);
            intAddressId = (new AddressDB()).insertAddress(lstAddress);
            
        } else {
            intAddressId = Integer.parseInt(addressId);
        }
        lstParam.add(intAddressId);
        
        String content = (String)httpUtils.getParameter("content");
        if(content != null && !content.trim().isEmpty()) {
            lstParam.add(content);
        } else {
            lstParam.add(null);
        }        
        
        HashMap customer = (HashMap)HttpSession.getInstance().getCacheAttribute(("ssouser_id" + (new EncryptDecryptUtils()).base64Encode(customerId)).getBytes());
        
        String contactName = (String)httpUtils.getParameter("contactName");
        if(contactName != null && !contactName.trim().isEmpty()) {
            lstParam.add(contactName);
        } else {
            lstParam.add(customer.get("name"));
        }
        
        String contactMobile = (String)httpUtils.getParameter("contactMobile");
        if(contactMobile != null && !contactMobile.trim().isEmpty()) {
            lstParam.add(contactMobile);
        } else {
            lstParam.add(customer.get("mobile"));
        }
        
        boolean checkType = true;
        String cleaning = (String)httpUtils.getParameter("cleaning");
        Integer intCleaning = null;
        if(cleaning != null && !cleaning.trim().isEmpty()) {
            intCleaning = Integer.parseInt(cleaning);
            checkType = false;
        }
        lstParam.add(intCleaning);
        
        String cooking = (String)httpUtils.getParameter("cooking");
        Integer intCooking = null;
        if(cooking != null && !cooking.trim().isEmpty()) {
            intCooking = Integer.parseInt(cooking);
            checkType = false;
        }
        lstParam.add(intCooking);
        
        String babyCare = (String)httpUtils.getParameter("babyCare");
        Integer intBabyCare = null;
        if(babyCare != null && !babyCare.trim().isEmpty()) {
            intBabyCare = Integer.parseInt(babyCare);
            checkType = false;
        }
        lstParam.add(intBabyCare);
        
        String oldCare = (String)httpUtils.getParameter("oldCare");
        Integer intOldCare = null;
        if(oldCare != null && !oldCare.trim().isEmpty()) {
            intOldCare = Integer.parseInt(oldCare);
            checkType = false;
        }
        lstParam.add(intOldCare);
        
        /*if(checkType) {
            returnData.put("error_code", "createorder_06");
            returnData.put("error_message", "Phải chọn 1 trong 4 loại công việc");
            returnData.put("response_message", "Hãy chọn loại công việc");
            returnAjax();
            return;
        }*/

        String isPeriodical = (String)httpUtils.getParameter("isPeriodical");
        Integer intPeriodical = null;
        if(isPeriodical != null && !isPeriodical.trim().isEmpty()) {
            intPeriodical = Integer.parseInt(isPeriodical);
        }
        lstParam.add(intPeriodical);        
        
        String startDate = (String)httpUtils.getParameter("startDate");
        if(startDate != null) {
            lstParam.add(startDate);
        } else {
            returnData.put("error_code", "createorder_07");
            returnData.put("error_message", "Chưa nhập thời gian bắt đầu");
            returnData.put("response_message", "Hãy nhập thời gian bắt đầu");
            returnAjax();
            return;
        }        

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");        
        Date dtStartdate = null;
        if(intPeriodical != null && intPeriodical == 1) {
            dtStartdate = df.parse(startDate.trim());
        }
        
        String startLength = "";

        Date dtEnddate = null;
        if(intPeriodical != null && intPeriodical == 1) {
            startLength = (String)httpUtils.getParameter("expireTime");
            
            Calendar calendar = Calendar.getInstance();
            if("1".equals(startLength)) {
                calendar.setTime(dtStartdate);
                calendar.add(Calendar.DATE, 7);
                dtEnddate = calendar.getTime();
            } else if("2".equals(startLength)) {
                calendar.setTime(dtStartdate);
                calendar.add(Calendar.DATE, 31);
                dtEnddate = calendar.getTime();
            } else if("3".equals(startLength)) {
                calendar.setTime(dtStartdate);
                calendar.add(Calendar.DATE, 62);
                dtEnddate = calendar.getTime();
            } else if("4".equals(startLength)) {
                dtEnddate = df.parse(httpUtils.getParameter("endMonth").toString() + " 23:59:59");
            } else {
                calendar.setTime(dtStartdate);
                calendar.add(Calendar.DATE, 7);
                dtEnddate = calendar.getTime(); 
            }
        } else {
            startLength = (String)httpUtils.getParameter("startLength");
        }
        
        Integer month = 0;
        if(dtStartdate != null && dtEnddate != null) {
            Long dateDiff = dtStartdate.getTime() - dtEnddate.getTime();
            if(dateDiff > 360*24*3600*1000) {
                month = 12;
            } else if(dateDiff > 175*24*3600*1000) {
                month = 6;
            } else if(dateDiff > 85*24*3600*1000) {
                month = 3;
            } else if(dateDiff > 25*24*3600*1000) {
                month = 1;
            }
        }
        
        int countInWeek = 0;
        String[] arrayCheck = new String[8];
        String[] arrayStart = new String[8];
        
        lstParam.add(startLength);
                
        String monStart = (String)httpUtils.getParameter("monStart");        
        lstParam.add(monStart);
        
        String monEnd = (String)httpUtils.getParameter("monLength");
        lstParam.add(monEnd);
        if(monEnd != null && !monEnd.trim().isEmpty()) {
            arrayCheck[2] = monEnd;
            arrayStart[2] = monStart;
            countInWeek++;
        }
        
        String tueStart = (String)httpUtils.getParameter("tueStart");
        lstParam.add(tueStart);
        
        String tueEnd = (String)httpUtils.getParameter("tueLength");
        lstParam.add(tueEnd);
        if(tueEnd != null && !tueEnd.trim().isEmpty()) {
            arrayCheck[3] = tueEnd;
            arrayStart[3] = tueStart;
            countInWeek++;
        }
        
        String wedStart = (String)httpUtils.getParameter("wedStart");
        lstParam.add(wedStart);
        
        String wedEnd = (String)httpUtils.getParameter("wedLength");
        lstParam.add(wedEnd);
        if(wedEnd != null && !wedEnd.trim().isEmpty()) {
            arrayCheck[4] = wedEnd;
            arrayStart[4] = wedStart;
            countInWeek++;
        }
        
        String thuStart = (String)httpUtils.getParameter("thuStart");
        lstParam.add(thuStart);
        
        String thuEnd = (String)httpUtils.getParameter("thuLength");
        lstParam.add(thuEnd);
        if(thuEnd != null && !thuEnd.trim().isEmpty()) {
            arrayCheck[5] = thuEnd;
            arrayStart[5] = thuStart;
            countInWeek++;
        }
        
        String friStart = (String)httpUtils.getParameter("friStart");
        lstParam.add(friStart);
        
        String friEnd = (String)httpUtils.getParameter("friLength");
        lstParam.add(friEnd);
        if(friEnd != null && !friEnd.trim().isEmpty()) {
            arrayCheck[6] = friEnd;
            arrayStart[6] = friStart;
            countInWeek++;
        }
        
        String satStart = (String)httpUtils.getParameter("satStart");
        lstParam.add(satStart);
        
        String satEnd = (String)httpUtils.getParameter("satLength");
        lstParam.add(satEnd);
        if(satEnd != null && !satEnd.trim().isEmpty()) {
            arrayCheck[7] = satEnd;
            arrayStart[7] = satStart;
            countInWeek++;
        }
        
        String sunStart = (String)httpUtils.getParameter("sunStart");
        lstParam.add(sunStart);
        
        String sunEnd = (String)httpUtils.getParameter("sunLength");
        lstParam.add(sunEnd);
        if(sunEnd != null && !sunEnd.trim().isEmpty()) {
            arrayCheck[1] = sunEnd;
            arrayStart[1] = sunStart;
            countInWeek++;
        }
        
        //price
        String timeLength = null;
        Integer calPrice = 0;
        if(intPeriodical != null && intPeriodical == 1) {
            if(dtStartdate != null && dtEnddate != null) {
                Calendar tmpDate = Calendar.getInstance();
                tmpDate.setTime(dtStartdate);
                Date start = tmpDate.getTime();
                
                while(start.getTime() < dtEnddate.getTime()) {
                    int startDay = tmpDate.get(Calendar.DAY_OF_WEEK);
                    
                    if(arrayCheck[startDay] != null) {
                        timeLength = arrayCheck[startDay];
                        int weekend = 0;
                        if(startDay == 1 || startDay == 7) weekend = 1;
                        
                        if(timeLength.contains(",")) {
                            String[] arrTL = timeLength.split(",");
                            String[] arrStart = arrayStart[startDay].split(",");
                            for(int i = 0; i < arrTL.length; i++) {
                                String st = arrStart[i].substring(0,2);
                                if(st.startsWith("0")) st.replace("0", "");
                                if(Integer.parseInt(st) > 17) weekend = 1;                                
                                calPrice += getPrice(Integer.parseInt(arrTL[i]), countInWeek, month, weekend);
                            }
                        } else {
                            String st = arrayStart[startDay].substring(0,2);
                            if(st.startsWith("0")) st.replace("0", "");
                            if(Integer.parseInt(st) > 17) weekend = 1;
                            calPrice += getPrice(Integer.parseInt(timeLength), countInWeek, month, weekend);
                        }
                    }

                    tmpDate.add(Calendar.DATE, 1);
                    start = tmpDate.getTime();
                }
                lstParam.add(calPrice);
                this.returnData.put("price", calPrice);
            }
        } else {
            if(startLength != null) {
                if(startLength.contains(",")) {
                    String[] arrTL = startLength.split(",");
                    for(int i = 0; i < arrTL.length; i++) {
                        calPrice += getPrice(Integer.parseInt(arrTL[i]), countInWeek, month, 0);
                    }
                } else {
                    calPrice += getPrice(Integer.parseInt(startLength), countInWeek, month, 0);
                }
                lstParam.add(calPrice);
                this.returnData.put("price", calPrice);
            }
            else lstParam.add(null);            
        }        
        
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");        
        String today = dateFormat.format(new Date());
        String orderCode = "DH" + today + "." + orderCount.incrementAndGet();
        lstParam.add(orderCode);
        
        lstParam.add(new Date());
        
        Integer orderId = (new OrderDB()).insertOrder(lstParam);
        
        lstParam1.add(intCustomerId);
        lstParam1.add(orderId);
        String maidId = (String) httpUtils.getParameter("maidId");
        Integer intMaidId = null;
        if (maidId != null && !maidId.trim().isEmpty()) {
            intMaidId = Integer.parseInt(maidId);
            lstParam1.add(intMaidId);
        } else {
            lstParam1.add(null);
        }        
        
        lstParam1.add(contactName);
        lstParam1.add(contactMobile);
        
        int countPlan = 1;
        List lstBatch = new ArrayList();
        
        if(intPeriodical != null && intPeriodical == 1) {
            if(dtStartdate != null && dtEnddate != null) {
                Calendar tmpDate = Calendar.getInstance();
                tmpDate.setTime(dtStartdate);
                Date start = tmpDate.getTime();            
                while(start.getTime() < dtEnddate.getTime()) {
                    if(monStart != null && !monStart.trim().isEmpty()) {
                        List lstParam2 = new ArrayList(lstParam1);
                        List<List> lstResult = getPeriodicalDate(lstParam2, start, dtEnddate, monStart, monEnd, Calendar.MONDAY, countInWeek, month);
                        if(lstResult != null) {
                            for(int i = 0; i < lstResult.size(); i++) {
                                lstResult.get(i).add(orderCode.replace("DH", "LLV") + "." + countPlan);
                                countPlan++;
                                lstBatch.add(lstResult.get(i));
                            }
                        }
                    }
                    if(tueStart != null && !tueStart.trim().isEmpty()) {
                        List lstParam3 = new ArrayList(lstParam1);
                        List<List> lstResult = getPeriodicalDate(lstParam3, start, dtEnddate, tueStart, tueEnd, Calendar.TUESDAY, countInWeek, month);
                        if(lstResult != null) {
                            for(int i = 0; i < lstResult.size(); i++) {
                                lstResult.get(i).add(orderCode.replace("DH", "LLV") + "." + countPlan);
                                countPlan++;
                                lstBatch.add(lstResult.get(i));
                            }
                        }
                    }
                    if(wedStart != null && !wedStart.trim().isEmpty()) {
                        List lstParam4 = new ArrayList(lstParam1);
                        List<List> lstResult = getPeriodicalDate(lstParam4, start, dtEnddate, wedStart, wedEnd, Calendar.WEDNESDAY, countInWeek, month);
                        if(lstResult != null) {
                            for(int i = 0; i < lstResult.size(); i++) {
                                lstResult.get(i).add(orderCode.replace("DH", "LLV") + "." + countPlan);
                                countPlan++;
                                lstBatch.add(lstResult.get(i));
                            }
                        }
                    }
                    if(thuStart != null && !thuStart.trim().isEmpty()) {
                        List lstParam5 = new ArrayList(lstParam1);
                        List<List> lstResult = getPeriodicalDate(lstParam5, start, dtEnddate, thuStart, thuEnd, Calendar.THURSDAY, countInWeek, month);
                        if(lstResult != null) {
                            for(int i = 0; i < lstResult.size(); i++) {
                                lstResult.get(i).add(orderCode.replace("DH", "LLV") + "." + countPlan);
                                countPlan++;
                                lstBatch.add(lstResult.get(i));
                            }
                        }
                    }
                    if(friStart != null && !friStart.trim().isEmpty()) {
                        List lstParam6 = new ArrayList(lstParam1);
                        List<List> lstResult = getPeriodicalDate(lstParam6, start, dtEnddate, friStart, friEnd, Calendar.FRIDAY, countInWeek, month);
                        if(lstResult != null) {                    
                            for(int i = 0; i < lstResult.size(); i++) {
                                lstResult.get(i).add(orderCode.replace("DH", "LLV") + "." + countPlan);
                                countPlan++;
                                lstBatch.add(lstResult.get(i));
                            }
                        }
                    }
                    if(satStart != null && !satStart.trim().isEmpty()) {
                        List lstParam7 = new ArrayList(lstParam1);
                        List<List> lstResult = getPeriodicalDate(lstParam7, start, dtEnddate, satStart, satEnd, Calendar.SATURDAY, countInWeek, month);
                        if(lstResult != null) {
                            for(int i = 0; i < lstResult.size(); i++) {
                                lstResult.get(i).add(orderCode.replace("DH", "LLV") + "." + countPlan);
                                countPlan++;
                                lstBatch.add(lstResult.get(i));
                            }
                        }
                    }
                    if(sunStart != null && !sunStart.trim().isEmpty()) {
                        List lstParam8 = new ArrayList(lstParam1);
                        List<List> lstResult = getPeriodicalDate(lstParam8, start, dtEnddate, sunStart, sunEnd, Calendar.SUNDAY, countInWeek, month);
                        if(lstResult != null) {
                            for(int i = 0; i < lstResult.size(); i++) {
                                lstResult.get(i).add(orderCode.replace("DH", "LLV") + "." + countPlan);
                                countPlan++;
                                lstBatch.add(lstResult.get(i));
                            }
                        }
                    }
                    tmpDate.add(Calendar.DATE, 7);
                    start = tmpDate.getTime();
                }
            }
        } else {
            if(startDate.contains(",")) {
                String[] arrStartdate = startDate.split(",");
                String[] arrStartlength = startLength.split(",");
                for(int i = 0; i < arrStartdate.length; i++) {
                    dtStartdate = df.parse(arrStartdate[i].trim());
                    dtEnddate = new Date(dtStartdate.getTime() + Integer.parseInt(arrStartlength[i].trim()) * 60 * 1000);
                    List lstParam11 = new ArrayList(lstParam1);
                    lstParam11.add(dtStartdate);
                    lstParam11.add(dtEnddate);
                    int weekend = getPriceType(dtStartdate);
                    lstParam11.add(getPrice(Integer.parseInt(arrStartlength[i].trim()), countInWeek, month, weekend));
                    int salaryMinute = 500;
                    if(weekend == 1) salaryMinute = 575;
                    lstParam11.add(salaryMinute * Integer.parseInt(arrStartlength[i].trim()));
                    lstParam11.add(orderCode.replace("DH", "LLV") + "." + countPlan);
                    countPlan++;
                    lstBatch.add(lstParam11);                    
                }
            } else {
                dtStartdate = df.parse(startDate.trim());
                dtEnddate = new Date(dtStartdate.getTime() + Integer.parseInt(startLength.trim()) * 60 * 1000);
                List lstParam11 = new ArrayList(lstParam1);
                lstParam11.add(dtStartdate);
                lstParam11.add(dtEnddate);
                int weekend = getPriceType(dtStartdate);
                int salaryMinute = 500;
                if(weekend == 1) salaryMinute = 575;                
                lstParam11.add(getPrice(Integer.parseInt(startLength.trim()), countInWeek, month, weekend));
                lstParam11.add(salaryMinute * Integer.parseInt(startLength.trim()));
                lstParam11.add(orderCode.replace("DH", "LLV") + "." + countPlan);
                countPlan++;
                lstBatch.add(lstParam11);
            }            
        }
        
        (new PlanDB()).insertBatchPlan(lstBatch);
        
        List<Map> lstPlanByOrder = (new MaidDB()).getPlanByOrderId(orderId);
        for(int i = 0; i < lstPlanByOrder.size(); i++) {
            HttpSession.getInstance().setStoreAttribute("cache_plan", lstPlanByOrder.get(i).get("plan_id").toString(), lstPlanByOrder.get(i));
        }
         
        // insert receipt record
        lstParamReceipt.add(orderId);
        (new ReceiptDB()).insertReceiptFromOrder(lstParamReceipt);        
        
        returnData.put("response_message", "Tạo đơn hàng thành công");
        returnData.put("order_code", orderCode);
        Date payDate = new Date((new Date()).getTime() + 24*3600*1000);
        DateFormat dfpay = new SimpleDateFormat("dd-MM-yyyy"); 
        returnData.put("pay_message", "Bạn vui lòng chuyển khoản phí dịch vụ tới tài khoản Phạm Hùng Thắng, 0491000104796, Ngân hàng TMCP Ngoại Thương VCB, chi nhánh Thăng Long trước ngày " + dfpay.format(payDate));
        returnAjax();
        
        if(intMaidId != null) {
            String appId = ResourceBundleUtils.getConfig("maid_appid");
            String restKey = ResourceBundleUtils.getConfig("maid_restkey");        
            String cappId = ResourceBundleUtils.getConfig("customer_appid");
            String crestKey = ResourceBundleUtils.getConfig("customer_restkey");                    
            String requestUrl = ResourceBundleUtils.getConfig("onesignal_url");            
            
            String payload = " {\"app_id\": \"" + appId + "\",\"filters\": [ "
                    + "  	{\"field\": \"tag\", \"key\": \"user_id\", \"relation\": \"=\", \"value\": \"" + intMaidId + "\"}], "
                    + " \"data\": {\"message_type\": \"1\",\"plan_id\":\"\"},\"contents\": "
                    + " {\"en\": \" Bạn được GIAO VIỆC của chủ nhà " + customer.get("name") 
                    + " - " + customer.get("mobile")
                    + "\"}} "; 
            
            String payload1 = " {\"app_id\": \"" + cappId + "\",\"filters\": [ "
                    + "  	{\"field\": \"tag\", \"key\": \"user_id\", \"relation\": \"=\", \"value\": \"1\"}], "
                    + " \"data\": {\"message_type\": \"1\",\"plan_id\":\"\"},\"contents\": "
                    + " {\"en\": \"Chủ nhà " + customer.get("name") 
                    + " - " + customer.get("mobile") + " vừa thực hiện đặt 1 đơn hàng"
                    + "\"}} "; 

            RsMessageAction rsm = new RsMessageAction(httpUtils);
            rsm.sendRestMessage(requestUrl, payload, restKey, intCustomerId.toString(), intMaidId.toString(), 
                    " Bạn được GIAO VIỆC của chủ nhà " + customer.get("name") + " - " + customer.get("mobile"));            
            
            rsm.sendRestMessage(requestUrl, payload1, crestKey, intCustomerId.toString(), "1", 
                    "Chủ nhà " + customer.get("name") + " - " + customer.get("mobile") + " vừa thực hiện đặt 1 đơn hàng");            
            
        }
    }
    
    private int getPriceType(Date startDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        if(cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7) return 1;
        if(cal.get(Calendar.HOUR_OF_DAY) > 17) return 1;
        return 0;
    }
    
    public void getTotalPrice() throws IOException, ParseException {

        String isPeriodical = (String)httpUtils.getParameter("isPeriodical");
        Integer intPeriodical = null;
        if(isPeriodical != null && !isPeriodical.trim().isEmpty()) {
            intPeriodical = Integer.parseInt(isPeriodical);
        }
        
        String startDate = (String)httpUtils.getParameter("startDate");
        if(startDate != null) {
        } else {
            returnData.put("error_code", "createorder_07");
            returnData.put("error_message", "Chưa nhập thời gian bắt đầu");
            returnData.put("response_message", "Hãy nhập thời gian bắt đầu");
            returnAjax();
            return;
        }        

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");        
        Date dtStartdate = null;
        if(intPeriodical != null && intPeriodical == 1) {
            dtStartdate = df.parse(startDate.trim());
        }
        
        String startLength = "";

        Date dtEnddate = null;
        if(intPeriodical != null && intPeriodical == 1) {
            startLength = (String)httpUtils.getParameter("expireTime");
            
            Calendar calendar = Calendar.getInstance();
            if("1".equals(startLength)) {
                calendar.setTime(dtStartdate);
                calendar.add(Calendar.DATE, 7);
                dtEnddate = calendar.getTime();
            } else if("2".equals(startLength)) {
                calendar.setTime(dtStartdate);
                calendar.add(Calendar.DATE, 31);
                dtEnddate = calendar.getTime();
            } else if("3".equals(startLength)) {
                calendar.setTime(dtStartdate);
                calendar.add(Calendar.DATE, 62);
                dtEnddate = calendar.getTime();
            } else if("4".equals(startLength)) {
                dtEnddate = df.parse(httpUtils.getParameter("endMonth").toString() + " 23:59:59");
            } else {
                calendar.setTime(dtStartdate);
                calendar.add(Calendar.DATE, 7);
                dtEnddate = calendar.getTime(); 
            }
        } else {
            startLength = (String)httpUtils.getParameter("startLength");
        }
        
        Integer month = 0;
        if(dtStartdate != null && dtEnddate != null) {
            Long dateDiff = dtStartdate.getTime() - dtEnddate.getTime();
            if(dateDiff > 360*24*3600*1000) {
                month = 12;
            } else if(dateDiff > 175*24*3600*1000) {
                month = 6;
            } else if(dateDiff > 85*24*3600*1000) {
                month = 3;
            } else if(dateDiff > 25*24*3600*1000) {
                month = 1;
            }
        }        
        
        String[] arrayCheck = new String[8];
        String[] arrayStart = new String[8];
        String monEnd = (String)httpUtils.getParameter("monLength");
        Integer countInWeek = 0;
        if(monEnd != null && !monEnd.trim().isEmpty()) {
            arrayCheck[2] = monEnd;
            arrayStart[2] = (String)httpUtils.getParameter("monStart");
            countInWeek++;
        }
        
        String tueEnd = (String)httpUtils.getParameter("tueLength");
        if(tueEnd != null && !tueEnd.trim().isEmpty()) {
            arrayCheck[3] = tueEnd;
            arrayStart[3] = (String)httpUtils.getParameter("tueStart");
            countInWeek++;
        }
        
        String wedEnd = (String)httpUtils.getParameter("wedLength");
        if(wedEnd != null && !wedEnd.trim().isEmpty()) {
            arrayCheck[4] = wedEnd;
            arrayStart[4] = (String)httpUtils.getParameter("wedStart");
            countInWeek++;            
        }
        
        String thuEnd = (String)httpUtils.getParameter("thuLength");
        if(thuEnd != null && !thuEnd.trim().isEmpty()) {
            arrayCheck[5] = thuEnd;
            arrayStart[5] = (String)httpUtils.getParameter("thuStart");
            countInWeek++;            
        }
        
        String friEnd = (String)httpUtils.getParameter("friLength");
        if(friEnd != null && !friEnd.trim().isEmpty()) {
            arrayCheck[6] = friEnd;
            arrayStart[6] = (String)httpUtils.getParameter("friStart");
            countInWeek++;
        }
        
        
        String satEnd = (String)httpUtils.getParameter("satLength");
        if(satEnd != null && !satEnd.trim().isEmpty()) {
            arrayCheck[7] = satEnd;
            arrayStart[7] = (String)httpUtils.getParameter("satStart");
            countInWeek++;
        }
        
        String sunEnd = (String)httpUtils.getParameter("sunLength");
        if(sunEnd != null && !sunEnd.trim().isEmpty()) {
            arrayCheck[1] = sunEnd;
            arrayStart[1] = (String)httpUtils.getParameter("sunStart");
            countInWeek++;
        }
        
        //price
        String timeLength = null;
        Integer calPrice = 0;
        if(intPeriodical != null && intPeriodical == 1) {
            if(dtStartdate != null && dtEnddate != null) {
                Calendar tmpDate = Calendar.getInstance();
                tmpDate.setTime(dtStartdate);
                Date start = tmpDate.getTime();
                
                while(start.getTime() < dtEnddate.getTime()) {
                    int startDay = tmpDate.get(Calendar.DAY_OF_WEEK);
                    
                    if(arrayCheck[startDay] != null) {
                        timeLength = arrayCheck[startDay];
                        int weekend = 0;
                        if(startDay == 1 || startDay == 7) weekend = 1;
                        
                        if(timeLength.contains(",")) {
                            String[] arrTL = timeLength.split(",");
                            String[] arrStart = arrayStart[startDay].split(",");
                            for(int i = 0; i < arrTL.length; i++) {
                                String st = arrStart[i].substring(0,2);
                                if(st.startsWith("0")) st.replace("0", "");
                                if(Integer.parseInt(st) > 17) weekend = 1;
                                calPrice += getPrice(Integer.parseInt(arrTL[i]), countInWeek, month, weekend);
                            }
                        } else {
                            String st = arrayStart[startDay].substring(0,2);
                            if(st.startsWith("0")) st.replace("0", "");
                            if(Integer.parseInt(st) > 17) weekend = 1;                            
                            calPrice += getPrice(Integer.parseInt(timeLength), countInWeek, month, weekend);
                        }
                    }

                    tmpDate.add(Calendar.DATE, 1);
                    start = tmpDate.getTime();
                }
                this.returnData.put("price", calPrice);
            }
        } else {
            if(startLength != null) {
                if(startLength.contains(",")) {
                    String[] arrTL = startLength.split(",");
                    for(int i = 0; i < arrTL.length; i++) {
                        calPrice += getPrice(Integer.parseInt(arrTL[i]), countInWeek, month, 0);
                    }
                } else {
                    calPrice += getPrice(Integer.parseInt(startLength), countInWeek, month, 0);
                }
                this.returnData.put("price", calPrice);
            }
        }
        
        returnData.put("response_message", "");
        returnAjax();        
    }
    
    private List getPeriodicalDate(List lstParam, Date dtStartdate, Date dtEnddate, String periodicalStart, String periodicalLength, int dayOfWeek, int countInWeek, int month) throws ParseException {
        List lstResult = new ArrayList();
        if(periodicalStart != null && !periodicalStart.trim().isEmpty() && 
                periodicalLength != null && !periodicalLength.trim().isEmpty() ) {
            Calendar tmpDate = Calendar.getInstance();
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            DateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");

            tmpDate.setTime(dtStartdate);
            while (tmpDate.get(Calendar.DAY_OF_WEEK) != dayOfWeek) {
                tmpDate.add(Calendar.DATE, 1);
            }
            Date nextDay = tmpDate.getTime();
            if(nextDay.getTime() < dtEnddate.getTime()) {
                String dateMon = dateformat.format(nextDay);
                if (periodicalStart.contains(",")) {
                    String[] arrStartdate = periodicalStart.split(",");
                    String[] arrStartlength = periodicalLength.split(",");
                    for (int i = 0; i < arrStartdate.length; i++) {
                        dtStartdate = df.parse(dateMon + " " + arrStartdate[i].trim() + ":00");
                        dtEnddate = new Date(dtStartdate.getTime() + Integer.parseInt(arrStartlength[i].trim()) * 60 * 1000);
                        List lstParam11 = new ArrayList(lstParam);
                        lstParam11.add(dtStartdate);
                        lstParam11.add(dtEnddate);
                        int weekend = getPriceType(dtStartdate);
                        int salaryMinute = 500;
                        if(weekend == 1) salaryMinute = 575;
                        lstParam11.add(getPrice(Integer.parseInt(arrStartlength[i].trim()), countInWeek, month, weekend));
                        lstParam11.add(salaryMinute * Integer.parseInt(arrStartlength[i].trim()));
                        lstResult.add(lstParam11);
                    }
                } else {
                    dtStartdate = df.parse(dateMon + " " + periodicalStart.trim() + ":00");
                    dtEnddate = new Date(dtStartdate.getTime() + Integer.parseInt(periodicalLength.trim()) * 60 * 1000);
                    List lstParam11 = new ArrayList(lstParam);
                    lstParam11.add(dtStartdate);
                    lstParam11.add(dtEnddate);
                    int weekend = getPriceType(dtStartdate);
                    int salaryMinute = 500;
                    if(weekend == 1) salaryMinute = 575;
                    lstParam11.add(getPrice(Integer.parseInt(periodicalLength.trim()), countInWeek, month, weekend));
                    lstParam11.add(salaryMinute * Integer.parseInt(periodicalLength.trim()));
                    lstResult.add(lstParam11);
                }
            } else {
                lstResult = null;
            }
        }
        return lstResult;
    }
    
    public void getOrderMaid() throws SQLException, IOException, ParseException {
        List<Map> lstChoose = new ArrayList();
                
        String strLimit = (String)httpUtils.getParameter("limit");
        Integer limit = 50;
        if(strLimit != null && !strLimit.trim().isEmpty()) limit = Integer.parseInt(strLimit);

        String startDate = (String)httpUtils.getParameter("startDate");
        if(startDate == null) {
            returnData.put("error_code", "createorder_07");
            returnData.put("error_message", "Chưa nhập thời gian bắt đầu");
            returnData.put("response_message", "Hãy nhập thời gian bắt đầu");
            returnAjax();
            return;
        }       

        String isPeriodical = (String)httpUtils.getParameter("isPeriodical");
        Integer intPeriodical = null;
        if(isPeriodical != null && !isPeriodical.trim().isEmpty()) {
            intPeriodical = Integer.parseInt(isPeriodical);
        }
        
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");        
        Date dtStartdate = null;
        if(intPeriodical != null && intPeriodical == 1) {
            dtStartdate = df.parse(startDate.trim());
        } 
        
        String startLength = "";

        if(intPeriodical != null && intPeriodical == 1) {
            String[] arrayCheck = new String[8];
            String[] arrayStart = new String[8];
            String monStart = (String) httpUtils.getParameter("monStart");
            String monEnd = (String) httpUtils.getParameter("monLength");
            if(monEnd != null && !monEnd.trim().isEmpty()) {
                arrayCheck[2] = monEnd;
                arrayStart[2] = monStart;
            }
            
            String tueStart = (String) httpUtils.getParameter("tueStart");
            String tueEnd = (String) httpUtils.getParameter("tueLength");
            if(tueEnd != null && !tueEnd.trim().isEmpty()) {
                arrayCheck[3] = tueEnd;
                arrayStart[3] = tueStart;
            }
            
            String wedStart = (String) httpUtils.getParameter("wedStart");
            String wedEnd = (String) httpUtils.getParameter("wedLength");
            if(wedEnd != null && !wedEnd.trim().isEmpty()) {
                arrayCheck[4] = wedEnd;
                arrayStart[4] = wedStart;
            }
            
            String thuStart = (String) httpUtils.getParameter("thuStart");
            String thuEnd = (String) httpUtils.getParameter("thuLength");
            if(thuEnd != null && !thuEnd.trim().isEmpty()) {
                arrayCheck[5] = thuEnd;
                arrayStart[5] = thuStart;
            }
            
            String friStart = (String) httpUtils.getParameter("friStart");
            String friEnd = (String) httpUtils.getParameter("friLength");
            if(friEnd != null && !friEnd.trim().isEmpty()) {
                arrayCheck[6] = friEnd;
                arrayStart[6] = friStart;
            }
            
            String satStart = (String) httpUtils.getParameter("satStart");
            String satEnd = (String) httpUtils.getParameter("satLength");
            if(satEnd != null && !satEnd.trim().isEmpty()) {
                arrayCheck[7] = satEnd;
                arrayStart[7] = satStart;
            }
            
            String sunStart = (String) httpUtils.getParameter("sunStart");
            String sunEnd = (String) httpUtils.getParameter("sunLength");
            if(sunEnd != null && !sunEnd.trim().isEmpty()) {
                arrayCheck[1] = sunEnd;
                arrayStart[1] = sunStart;
            }
            
            Date dtEnddate = null;
            if(intPeriodical != null && intPeriodical == 1) {
                startLength = (String)httpUtils.getParameter("expireTime");

                Calendar calendar = Calendar.getInstance();
                if("1".equals(startLength)) {
                    calendar.setTime(dtStartdate);
                    calendar.add(Calendar.DATE, 7);
                    dtEnddate = calendar.getTime();
                } else if("2".equals(startLength)) {
                    calendar.setTime(dtStartdate);
                    calendar.add(Calendar.DATE, 31);
                    dtEnddate = calendar.getTime();
                } else if("3".equals(startLength)) {
                    calendar.setTime(dtStartdate);
                    calendar.add(Calendar.DATE, 62);
                    dtEnddate = calendar.getTime();
                } else {
                    calendar.setTime(dtStartdate);
                    calendar.add(Calendar.DATE, 7);
                    dtEnddate = calendar.getTime(); 
                }
            } else {
                startLength = (String)httpUtils.getParameter("startLength");
            }
            
            Calendar tmpDate = Calendar.getInstance();
            tmpDate.setTime(dtStartdate);
            Date start = tmpDate.getTime();

            List lstStartLength = new ArrayList();            
            List lstStartDate = new ArrayList();            
            while(start.getTime() < dtEnddate.getTime()) {
                int startDay = tmpDate.get(Calendar.DAY_OF_WEEK);            
            
                String startTime = "";
                String timeLength = "";
                timeLength = arrayCheck[startDay];
                startTime = arrayStart[startDay];
                
                if(timeLength != null && startTime != null) {
                    if(startTime.contains(",")) {
                        String[] arrStartTime = startTime.split(",");
                        String[] arrTimeLength = timeLength.split(",");
                        for(int j = 0; j < arrStartTime.length; j++) {
                            String hour = arrStartTime[j].split(":")[0];
                            if(hour.charAt(0) == '0') hour = hour.substring(1);
                            String minute = arrStartTime[j].split(":")[1];
                            if(minute.charAt(0) == '0') minute = minute.substring(1);
                            tmpDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
                            tmpDate.set(Calendar.MINUTE, Integer.parseInt(minute));
                            lstStartLength.add(arrTimeLength[j]);
                            lstStartDate.add(tmpDate.getTime());         
                        }
                    } else {
                        String hour = startTime.split(":")[0];
                        if(hour.charAt(0) == '0') hour = hour.substring(1);
                        String minute = startTime.split(":")[1];
                        if(minute.charAt(0) == '0') minute = minute.substring(1);
                        tmpDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
                        tmpDate.set(Calendar.MINUTE, Integer.parseInt(minute));
                        if(timeLength.contains(",")) timeLength = timeLength.split(",")[0];

                        lstStartLength.add(timeLength);
                        lstStartDate.add(tmpDate.getTime());         
                    }
                }
                
                tmpDate.add(Calendar.DATE, 1);
                start = tmpDate.getTime();
            }
            
            lstChoose = getMaidByDate(lstStartLength, lstStartDate, limit);
        } else {
            if(startDate.contains(",")) {
                startLength = (String)httpUtils.getParameter("startLength");
                String[] arrStart = startDate.trim().split(",");
                String[] arrStartLength = startLength.trim().split(",");
                List lstStartLength = new ArrayList();
                List lstStartDate = new ArrayList();
                for(int j = 0; j < arrStart.length; j++) {
                    lstStartLength.add(arrStartLength[j]);
                    lstStartDate.add(df.parse(arrStart[j]));                    
                }
                lstChoose = getMaidByDate(lstStartLength, lstStartDate, limit);
            } else {
                startLength = (String)httpUtils.getParameter("startLength");
                List lstStartLength = new ArrayList();
                lstStartLength.add(startLength);
                List lstStartDate = new ArrayList();   
                lstStartDate.add(df.parse(startDate.trim()));
                lstChoose = getMaidByDate(lstStartLength, lstStartDate, limit);
            }
        }     
        
        String checkLocation = HttpSession.getInstance().getCacheAttribute("bm_checklocation");
        if(checkLocation != null) {
            String latitude = (String)httpUtils.getParameter("latitude");
            Float floatLatitude = null;
            if(latitude != null && !latitude.trim().isEmpty()) {
                floatLatitude = Float.parseFloat(latitude);
            } else {
                returnData.put("error_code", "getMaid_04");
                returnData.put("error_message", "Vĩ độ bắt buộc nhập");
                returnData.put("response_message", "Hãy chọn địa điểm trên bản đồ");
                returnAjax();
                return;
            } 

            String longitude = (String)httpUtils.getParameter("longitude");
            Float floatLongitude = null;
            if(longitude != null && !longitude.trim().isEmpty()) {
                floatLongitude = Float.parseFloat(longitude);
            } else {
                returnData.put("error_code", "getMaid_05");
                returnData.put("error_message", "Kinh độ bắt buộc nhập");
                returnData.put("response_message", "Hãy chọn địa điểm trên bản đồ");
                returnAjax();
                return;
            }
        }
        returnData.put("data", lstChoose);
        returnAjax();
    }    
    
    public List<Map> getMaidByDate(List<String> lstStartLength, List<Date> lstStartdate, int limit) throws ParseException {
        List<Map> lstChoose = new ArrayList();
        List<Integer> lstIntStart = new ArrayList();
        List<Integer> lstIntStartLength = new ArrayList();
        List<Integer> lstIntDayofweek = new ArrayList();
        DateFormat datef = new SimpleDateFormat("dd-MM-yyyy");  
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        
        for(int i = 0; i < lstStartLength.size(); i++) {
            String temp = lstStartLength.get(i);
            if(temp.contains(",")) temp = temp.split(",")[0];
            if(temp != null) lstIntStartLength.add(Integer.parseInt(temp));
        }
        for(int i = 0; i < lstStartdate.size(); i++) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(lstStartdate.get(i));
            Integer dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            lstIntStart.add(cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE));
            lstIntDayofweek.add(dayOfWeek);
        }
        List<Map> lstMaid = (List<Map>)HttpSession.getInstance().getCacheAttribute("cache_maid".getBytes());
        List<Map> lstWork = (List<Map>)HttpSession.getInstance().getCacheAttribute("cache_workTime".getBytes());
        List<Map> lstRest = (List<Map>)HttpSession.getInstance().getCacheAttribute("cache_restTime".getBytes());
        List<Map> lstPlan = (List<Map>)HttpSession.getInstance().getStore("cache_plan");

        int count = 0;
        for(int i = 0; i < lstMaid.size(); i++) {
            if(count >= limit) break;
            boolean checkSuccess = true;
                
            for(int n = 0; n < lstIntStartLength.size(); n++) {
                Date dtStartdate = lstStartdate.get(n);
                Integer dayOfWeek = lstIntDayofweek.get(n);
                Integer intStart = lstIntStart.get(n);
                Integer intStartLength = lstIntStartLength.get(n);
                
                boolean checkWork = false;
                boolean checkRest = false;
                boolean checkPlan = false;
                for(int j = 0; j < lstWork.size(); j++) {
                    if(lstWork.get(j).get("maid_id").equals(lstMaid.get(i).get("user_id"))) {
                        Date startDate = df.parse(datef.format((Date)lstWork.get(j).get("start_date")) + " 00:00:00");
                        Date endDate = df.parse(datef.format((Date)lstWork.get(j).get("end_date")) + " 23:59:59");
                        if(lstWork.get(j).get("start_date") != null && 
                                lstWork.get(j).get("end_date") != null &&
                                dtStartdate.after(startDate) && 
                                dtStartdate.before(endDate)) {
                            if(dayOfWeek == 1) {
                                Integer start = getMinuteOfDay((String)lstWork.get(j).get("sun_start"));
                                Integer end = getMinuteOfDay((String)lstWork.get(j).get("sun_end"));
                                if(start != null && end != null)
                                if(intStart >= start && (intStart + intStartLength) <= end) {
                                    checkWork = true;
                                    break;
                                }
                            } else if(dayOfWeek == 2) {
                                Integer start = getMinuteOfDay((String)lstWork.get(j).get("mon_start"));
                                Integer end = getMinuteOfDay((String)lstWork.get(j).get("mon_end"));
                                if(start != null && end != null)
                                if(intStart >= start && (intStart + intStartLength) <= end) {
                                    checkWork = true;
                                    break;
                                }                            
                            } else if(dayOfWeek == 3) {
                                Integer start = getMinuteOfDay((String)lstWork.get(j).get("tue_start"));
                                Integer end = getMinuteOfDay((String)lstWork.get(j).get("tue_end"));
                                if(start != null && end != null)
                                if(intStart >= start && (intStart + intStartLength) <= end) {
                                    checkWork = true;
                                    break;
                                }
                            } else if(dayOfWeek == 4) {
                                Integer start = getMinuteOfDay((String)lstWork.get(j).get("wed_start"));
                                Integer end = getMinuteOfDay((String)lstWork.get(j).get("wed_end"));
                                if(start != null && end != null)
                                if(intStart >= start && (intStart + intStartLength) <= end) {
                                    checkWork = true;
                                    break;
                                }                            
                            } else if(dayOfWeek == 5) {
                                Integer start = getMinuteOfDay((String)lstWork.get(j).get("thu_start"));
                                Integer end = getMinuteOfDay((String)lstWork.get(j).get("thu_end"));
                                if(start != null && end != null)
                                if(intStart >= start && (intStart + intStartLength) <= end) {
                                    checkWork = true;
                                    break;
                                }                            
                            } else if(dayOfWeek == 6) {
                                Integer start = getMinuteOfDay((String)lstWork.get(j).get("fri_start"));
                                Integer end = getMinuteOfDay((String)lstWork.get(j).get("fri_end"));
                                if(start != null && end != null)
                                if(intStart >= start && (intStart + intStartLength) <= end) {
                                    checkWork = true;
                                    break;
                                }                            
                            } else if(dayOfWeek == 7) {
                                Integer start = getMinuteOfDay((String)lstWork.get(j).get("sat_start"));
                                Integer end = getMinuteOfDay((String)lstWork.get(j).get("sat_end"));
                                if(start != null && end != null)
                                if(intStart >= start && (intStart + intStartLength) <= end) {
                                    checkWork = true;
                                    break;
                                }                            
                            }
                        }
                    }
                }

                for(int j = 0; j < lstRest.size(); j++) {
                    if(lstRest.get(j).get("maid_id").equals(lstMaid.get(i).get("user_id"))) {
                        Date startDate = df.parse(datef.format((Date)lstWork.get(j).get("start_date")) + " 00:00:00");
                        Date endDate = df.parse(datef.format((Date)lstWork.get(j).get("end_date")) + " 23:59:59");
                        
                        if(lstRest.get(j).get("start_date") != null &&
                                lstRest.get(j).get("end_date") != null &&
                                dtStartdate.after(startDate) && 
                                dtStartdate.before(endDate)) {
                            if(dayOfWeek == 1) {
                                Integer start = getMinuteOfDay((String)lstRest.get(j).get("sun_start"));
                                Integer end = getMinuteOfDay((String)lstRest.get(j).get("sun_end"));
                                if(start != null && end != null)
                                if(!(intStart >= end || (intStart + intStartLength) <= start)) {
                                    checkRest = true;
                                    break;
                                }
                            } else if(dayOfWeek == 2) {
                                Integer start = getMinuteOfDay((String)lstRest.get(j).get("mon_start"));
                                Integer end = getMinuteOfDay((String)lstRest.get(j).get("mon_end"));
                                if(start != null && end != null)
                                if(!(intStart >= end || (intStart + intStartLength) <= start)) {
                                    checkRest = true;
                                    break;
                                }                            
                            } else if(dayOfWeek == 3) {
                                Integer start = getMinuteOfDay((String)lstRest.get(j).get("tue_start"));
                                Integer end = getMinuteOfDay((String)lstRest.get(j).get("tue_end"));
                                if(start != null && end != null)
                                if(!(intStart >= end || (intStart + intStartLength) <= start)) {
                                    checkRest = true;
                                    break;
                                }                            
                            } else if(dayOfWeek == 4) {
                                Integer start = getMinuteOfDay((String)lstRest.get(j).get("wed_start"));
                                Integer end = getMinuteOfDay((String)lstRest.get(j).get("wed_end"));
                                if(start != null && end != null)
                                if(!(intStart >= end || (intStart + intStartLength) <= start)) {
                                    checkRest = true;
                                    break;
                                }                            
                            } else if(dayOfWeek == 5) {
                                Integer start = getMinuteOfDay((String)lstRest.get(j).get("thu_start"));
                                Integer end = getMinuteOfDay((String)lstRest.get(j).get("thu_end"));
                                if(start != null && end != null)
                                if(!(intStart >= end || (intStart + intStartLength) <= start)) {
                                    checkRest = true;
                                    break;
                                }                            
                            } else if(dayOfWeek == 6) {
                                Integer start = getMinuteOfDay((String)lstRest.get(j).get("fri_start"));
                                Integer end = getMinuteOfDay((String)lstRest.get(j).get("fri_end"));
                                if(start != null && end != null)
                                if(!(intStart >= end || (intStart + intStartLength) <= start)) {
                                    checkRest = true;
                                    break;
                                }                            
                            } else if(dayOfWeek == 7) {
                                Integer start = getMinuteOfDay((String)lstRest.get(j).get("sat_start"));
                                Integer end = getMinuteOfDay((String)lstRest.get(j).get("sat_end"));
                                if(start != null && end != null)
                                if(!(intStart >= end || (intStart + intStartLength) <= start)) {
                                    checkRest = true;
                                    break;
                                }                            
                            }
                        }
                    }
                }  

                for(int j = 0; j < lstPlan.size(); j++) {
                    if(lstPlan.get(j).get("status") != null && 
                            "1".equals(lstPlan.get(j).get("status").toString()) &&
                            "2".equals(lstPlan.get(j).get("status").toString())) {
                        Date planStart = (Date)lstPlan.get(j).get("start");
                        Date planEnd = (Date)lstPlan.get(j).get("end");
                        if(lstPlan.get(j).get("maid_id") != null && lstPlan.get(j).get("maid_id").equals(lstMaid.get(i).get("user_id"))) {
                            if(datef.format(dtStartdate).equals(datef.format(planStart))) {
                                Integer start = getMinuteOfDay(planStart);
                                Integer end = getMinuteOfDay(planEnd);
                                if(!(intStart >= end || (intStart + intStartLength) <= start)) {
                                    checkPlan = true;
                                    break;
                                }  
                            }
                        }
                    }
                }

                if(!(checkWork && !checkRest && !checkPlan)) {
                    checkSuccess = false;
                }
            }
            if(checkSuccess) {
                lstChoose.add(lstMaid.get(i));
                count++;                
            }
        }
        return lstChoose;
    }
    
    private Integer getMinuteOfDay(String time) {
        if(time != null && !time.trim().isEmpty()) {
            String hour = time.split(":")[0];
            if(hour.charAt(0) == '0') hour = hour.substring(1);
            String minute = time.split(":")[1];
            if(minute.charAt(0) == '0') minute = minute.substring(1);
            return Integer.parseInt(hour) * 60 + Integer.parseInt(minute);
        } else {
            return null;
        }
    }
    
    private Integer getMinuteOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
    }    
    
    public Integer getPrice(Integer timeLength, Integer countInWeek, Integer month, Integer type) {
        List<Map> lstPolicy = (List<Map>)HttpSession.getInstance().getCacheAttribute("cache_policy".getBytes());
        List<Map> lstPrice = (List<Map>)HttpSession.getInstance().getCacheAttribute("cache_price".getBytes());
        Integer price = null;
        if(month == 0) month = 1;
        String code = "";
        if(countInWeek == 0) {
            code = "day_1_1";
        } else {
            if(type == 1) code = "night_";
            else code = "day_";
            code = code + month + "_" + countInWeek;
        }
        for(int i = 0; i < lstPrice.size(); i++) {
            if(code.equals(lstPrice.get(i).get("code"))) {
                if(lstPrice.get(i).get("price") != null) {
                    price = (timeLength * Integer.parseInt(lstPrice.get(i).get("price").toString())) / 60;
                    break;
                }
            }
        }
        
        for(int i = 0; i < lstPolicy.size(); i++) {
            Date fromDate = (Date)lstPolicy.get(i).get("from_date");
            Date toDate = (Date)lstPolicy.get(i).get("to_date");
            if(fromDate.before(new Date()) && toDate.after(new Date())) {
                Integer addRate = (Integer)lstPolicy.get(i).get("add_rate");
                Integer addVnd = (Integer)lstPolicy.get(i).get("add_vnd");
                if(addRate != null) price += (price * addRate)/100;
                if(addVnd != null) price += addVnd;
                break;
            }
        }
        
        return price;
    }
    
    public void getCategory() throws IOException {
        this.returnData.put("chooseMaid", ResourceBundleUtils.getConfig("chooseMaid"));
        this.returnData.put("data", HttpSession.getInstance().getCacheAttribute("cache_category".getBytes()));
        returnAjax();
    }
}
