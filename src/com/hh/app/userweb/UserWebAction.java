/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.userweb;

import com.hh.action.BaseAction;
import static com.hh.app.api.RsOrderAction.orderCount;
import com.hh.app.db.AddressDB;
import com.hh.app.db.MaidDB;
import com.hh.app.db.OrderDB;
import com.hh.app.db.PlanDB;
import com.hh.app.db.ReceiptDB;
import com.hh.app.db.UserDB;
import com.hh.util.EncryptDecryptUtils;
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
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author agiletech
 */
public class UserWebAction extends BaseAction{

    public UserWebAction(HttpUtils hu) {
        super(hu);
    }
    
    public void mainPage() throws IOException {
        HashMap ssoUser = (HashMap) httpUtils.getSessionAttribute("sso_username");
        if(ssoUser != null) { // Da co session
            returnPage("web/app/index.html");
        } else {
            returnFullPage("web/app/webuser/home.html");
        }
    }
    
    public void searchMadOneTime() throws IOException {
        returnFullPage("web/app/webuser/find_one_time_mad.html");
    }
    
    public void searchMadPeriodic() throws IOException {
        returnFullPage("web/app/webuser/find_periodic_mad.html");
    }
    
    public void aboutUs() throws IOException {
        returnFullPage("web/app/webuser/about_us.html");
    }
    
    public void promotion() throws IOException {
        returnFullPage("web/app/webuser/promotion.html");
    }
    
     public void contactUs() throws IOException {
        returnFullPage("web/app/webuser/contact.html");
    }
    
    public void checkPhoneNumber() throws IOException, SQLException, ParseException{
        String phoneNumber = (String)httpUtils.getParameter("phoneNumber");
        UserDB userDB = new UserDB();
        boolean registered = userDB.phoneAlreadyUse(phoneNumber);
        returnData.put("registered", registered);
        returnAjax();     
    }
    
    public void validateLoginInfo() throws IOException, SQLException, ParseException{
        String phoneNumber = (String)httpUtils.getParameter("phoneNumber");
        String passwordPlain = (String)httpUtils.getParameter("password");
        
        EncryptDecryptUtils edu = new EncryptDecryptUtils();
        String password =  edu.encodePassword(passwordPlain);
        
        UserDB userDB = new UserDB();
        Map user = userDB.getUserByPhoneNumberAndPassword(phoneNumber, password);
        boolean login = false;
        if(user != null){
            login = true;
        }
        returnData.put("login", login);
        returnAjax();     
    }
    
     public void createOrder() throws IOException, SQLException, ParseException {
        List lstParam = new ArrayList();
        List lstParam1 = new ArrayList();
        List lstParamReceipt = new ArrayList();
        
        UserDB userDB = new UserDB();
        
        String phoneNumber = (String)httpUtils.getParameter("phoneNumber");
        String customerId = userDB.getUserByPhoneNumber(phoneNumber).get("user_id").toString();
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
        
        String contactName = (String)httpUtils.getParameter("contactName");
        if(contactName != null && !contactName.trim().isEmpty()) {
            lstParam.add(contactName);
        }
        
        String contactMobile = (String)httpUtils.getParameter("contactMobile");
        if(contactMobile != null && !contactMobile.trim().isEmpty()) {
            lstParam.add(contactMobile);
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

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");        
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
            } else {
                calendar.setTime(dtStartdate);
                calendar.add(Calendar.DATE, 7);
                dtEnddate = calendar.getTime(); 
            }
        } else {
            startLength = (String)httpUtils.getParameter("startLength");
        }
        
        String[] arrayCheck = new String[8];
        
        lstParam.add(startLength);
                
        String monStart = (String)httpUtils.getParameter("monStart");        
        lstParam.add(monStart);
        
        String monEnd = (String)httpUtils.getParameter("monLength");
        lstParam.add(monEnd);
        if(monEnd != null && !monEnd.trim().isEmpty()) arrayCheck[2] = monEnd;
        
        String tueStart = (String)httpUtils.getParameter("tueStart");
        lstParam.add(tueStart);
        
        String tueEnd = (String)httpUtils.getParameter("tueLength");
        lstParam.add(tueEnd);
        if(tueEnd != null && !tueEnd.trim().isEmpty()) arrayCheck[3] = tueEnd;
        
        String wedStart = (String)httpUtils.getParameter("wedStart");
        lstParam.add(wedStart);
        
        String wedEnd = (String)httpUtils.getParameter("wedLength");
        lstParam.add(wedEnd);
        if(wedEnd != null && !wedEnd.trim().isEmpty()) arrayCheck[4] = wedEnd;
        
        String thuStart = (String)httpUtils.getParameter("thuStart");
        lstParam.add(thuStart);
        
        String thuEnd = (String)httpUtils.getParameter("thuLength");
        lstParam.add(thuEnd);
        if(thuEnd != null && !thuEnd.trim().isEmpty()) arrayCheck[5] = thuEnd;
        
        String friStart = (String)httpUtils.getParameter("friStart");
        lstParam.add(friStart);
        
        String friEnd = (String)httpUtils.getParameter("friLength");
        lstParam.add(friEnd);
        if(friEnd != null && !friEnd.trim().isEmpty()) arrayCheck[6] = friEnd;
        
        String satStart = (String)httpUtils.getParameter("satStart");
        lstParam.add(satStart);
        
        String satEnd = (String)httpUtils.getParameter("satLength");
        lstParam.add(satEnd);
        if(satEnd != null && !satEnd.trim().isEmpty()) arrayCheck[7] = satEnd;
        
        String sunStart = (String)httpUtils.getParameter("sunStart");
        lstParam.add(sunStart);
        
        String sunEnd = (String)httpUtils.getParameter("sunLength");
        lstParam.add(sunEnd);
        if(sunEnd != null && !sunEnd.trim().isEmpty()) arrayCheck[1] = sunEnd;
        
        String source = (String)httpUtils.parameters.get("source");
        
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
                        if(timeLength.contains(",")) {
                            String[] arrTL = timeLength.split(",");
                            for(int i = 0; i < arrTL.length; i++) {
                                calPrice += getPrice(Integer.parseInt(arrTL[i]));
                            }
                        } else {
                            calPrice += getPrice(Integer.parseInt(timeLength));
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
                        calPrice += getPrice(Integer.parseInt(arrTL[i]));
                    }
                } else {
                    calPrice += getPrice(Integer.parseInt(startLength));
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
        
        //update source for order 1 = web, 0 = app
        if(orderId > 0){
            List lstPrUpdateSource = new ArrayList();
            if(StringUtils.isNotEmpty(source)){
                lstPrUpdateSource.add(Integer.parseInt(source));
            }else{
                lstPrUpdateSource.add(0);
            }
            lstPrUpdateSource.add(orderId);
            (new OrderDB()).updateOrderSource(lstPrUpdateSource);
        }
        
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
                        List<List> lstResult = getPeriodicalDate(lstParam2, start, dtEnddate, monStart, monEnd, Calendar.MONDAY);
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
                        List<List> lstResult = getPeriodicalDate(lstParam3, start, dtEnddate, tueStart, tueEnd, Calendar.TUESDAY);
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
                        List<List> lstResult = getPeriodicalDate(lstParam4, start, dtEnddate, wedStart, wedEnd, Calendar.WEDNESDAY);
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
                        List<List> lstResult = getPeriodicalDate(lstParam5, start, dtEnddate, thuStart, thuEnd, Calendar.THURSDAY);
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
                        List<List> lstResult = getPeriodicalDate(lstParam6, start, dtEnddate, friStart, friEnd, Calendar.FRIDAY);
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
                        List<List> lstResult = getPeriodicalDate(lstParam7, start, dtEnddate, satStart, satEnd, Calendar.SATURDAY);
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
                        List<List> lstResult = getPeriodicalDate(lstParam8, start, dtEnddate, sunStart, sunEnd, Calendar.SUNDAY);
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
                    lstParam11.add(getPrice(Integer.parseInt(arrStartlength[i].trim())));
                    lstParam11.add(500 * Integer.parseInt(arrStartlength[i].trim()));
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
                lstParam11.add(getPrice(Integer.parseInt(startLength.trim())));
                lstParam11.add(500 * Integer.parseInt(startLength.trim()));
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
    
    public Integer getPrice(Integer timeLength) {
        List<Map> lstPolicy = (List<Map>)HttpSession.getInstance().getCacheAttribute("cache_policy".getBytes());
        List<Map> lstCategory = (List<Map>)HttpSession.getInstance().getCacheAttribute("cache_category".getBytes());
        Integer price = null;
        for(int i = 0; i < lstCategory.size(); i++) {
            if(lstCategory.get(i).get("hour_price") != null)
                if(timeLength.equals(lstCategory.get(i).get("time_length"))) 
                    price = Integer.parseInt(lstCategory.get(i).get("hour_price").toString());
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
    
    private List getPeriodicalDate(List lstParam, Date dtStartdate, Date dtEnddate, String periodicalStart, String periodicalLength, int dayOfWeek) throws ParseException {
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
                        lstParam11.add(getPrice(Integer.parseInt(arrStartlength[i].trim())));
                        lstParam11.add(500 * Integer.parseInt(arrStartlength[i].trim()));
                        lstResult.add(lstParam11);
                    }
                } else {
                    dtStartdate = df.parse(dateMon + " " + periodicalStart.trim() + ":00");
                    dtEnddate = new Date(dtStartdate.getTime() + Integer.parseInt(periodicalLength.trim()) * 60 * 1000);
                    List lstParam11 = new ArrayList(lstParam);
                    lstParam11.add(dtStartdate);
                    lstParam11.add(dtEnddate);
                    lstParam11.add(getPrice(Integer.parseInt(periodicalLength.trim())));
                    lstParam11.add(500 * Integer.parseInt(periodicalLength.trim()));
                    lstResult.add(lstParam11);
                }
            } else {
                lstResult = null;
            }
        }
        return lstResult;
    }
}
