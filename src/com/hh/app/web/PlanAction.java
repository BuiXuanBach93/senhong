/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.web;

import com.hh.action.BaseAction;
import static com.hh.action.BaseAction.fileResources;
import com.hh.app.api.RsOrderAction;
import com.hh.app.db.AddressDB;
import com.hh.app.db.MaidDB;
import com.hh.app.db.PlanDB;
import com.hh.database.DatabaseConnector;
import com.hh.util.EncryptDecryptUtils;
import com.hh.util.FileUtils;
import com.hh.util.ResourceBundleUtils;
import com.hh.web.HttpSession;
import com.hh.web.HttpUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author agiletech
 */
public class PlanAction extends BaseAction{
    
    public static AtomicInteger planCount = new AtomicInteger(0);

    public PlanAction(HttpUtils hu) {
        super(hu);
    }
    
    public void listPlan() throws IOException {
        String maidId = (String)httpUtils.getParameter("maidid");
        String webPage = "web/app/plan/listPlan.html";
        String customerId = (String)httpUtils.getParameter("customerid");
        if(maidId == null && customerId == null) returnPage(webPage);
        else {
            String content = getTemplatePage();
            FileUtils fu = new FileUtils();
            File headFile = new File(webPage.replace(".html", "_head.html")).getCanonicalFile();
            if (headFile.exists()) {
                String headPage = fu.readFileToString(headFile, FileUtils.UTF_8);
                content = content.replace("<!-- JS,CSS -->", headPage);
            }
            File resultFile = new File(webPage).getCanonicalFile();
            String contentPage = fu.readFileToString(resultFile, FileUtils.UTF_8);
            content = content.replace("<!-- PAGE CONTENT WRAPPER -->", contentPage);
            if(maidId != null)
                content = content.replace("id=\"maidid1\" name=\"maidid\"", "id=\"maidid1\" name=\"maidid\" value=\"" + maidId + "\"");
            if(customerId != null)
                content = content.replace("id=\"customerid1\" name=\"customerid\"", "id=\"customerid1\" name=\"customerid\" value=\"" + customerId + "\"");
            byte[] byteContent = content.getBytes(Charset.forName(FileUtils.UTF_8));
            httpUtils.httpExchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            httpUtils.httpExchange.getResponseHeaders().set("Content-Encoding", "gzip");
            httpUtils.httpExchange.sendResponseHeaders(200, 0);
            try(GZIPOutputStream os = new GZIPOutputStream(httpUtils.httpExchange.getResponseBody());)
            {
                os.write(byteContent);
                os.close();
            }            
        }
    }   
    
    public void listTask() throws IOException {
        String maidId = (String)httpUtils.getParameter("maidid");
        String webPage = "web/app/task/listTask.html";
        String customerId = (String)httpUtils.getParameter("customerid");
        if(maidId == null && customerId == null) returnPage(webPage);
        else {
            String content = getTemplatePage();
            FileUtils fu = new FileUtils();
            File headFile = new File(webPage.replace(".html", "_head.html")).getCanonicalFile();
            if (headFile.exists()) {
                String headPage = fu.readFileToString(headFile, FileUtils.UTF_8);
                content = content.replace("<!-- JS,CSS -->", headPage);
            }
            File resultFile = new File(webPage).getCanonicalFile();
            String contentPage = fu.readFileToString(resultFile, FileUtils.UTF_8);
            content = content.replace("<!-- PAGE CONTENT WRAPPER -->", contentPage);
            if(maidId != null)
                content = content.replace("id=\"maidid1\" name=\"maidid\"", "id=\"maidid1\" name=\"maidid\" value=\"" + maidId + "\"");
            if(customerId != null)
                content = content.replace("id=\"customerid1\" name=\"customerid\"", "id=\"customerid1\" name=\"customerid\" value=\"" + customerId + "\"");
            byte[] byteContent = content.getBytes(Charset.forName(FileUtils.UTF_8));
            httpUtils.httpExchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            httpUtils.httpExchange.getResponseHeaders().set("Content-Encoding", "gzip");
            httpUtils.httpExchange.sendResponseHeaders(200, 0);
            try(GZIPOutputStream os = new GZIPOutputStream(httpUtils.httpExchange.getResponseBody());)
            {
                os.write(byteContent);
                os.close();
            }            
        }
    }       

    public void listSalary() throws IOException {
        returnPage("web/app/salary/listSalary.html");
    }       

    public void searchPlan() throws IOException, SQLException, ParseException {
        PlanDB odb = new PlanDB();
        
        int pageLength = 10;
        if(httpUtils.getParameter("length") != null) {
            pageLength = Integer.parseInt((String)httpUtils.getParameter("length"));
            if(pageLength == 0) pageLength = 10;
        }
        
        int numberRow = 0;
        if(httpUtils.getParameter("start") != null) {
            numberRow = Integer.parseInt((String)httpUtils.getParameter("start"));
        }

        String ordercode = (String)httpUtils.getParameter("ordercode");
        String customer = (String)httpUtils.getParameter("customer");
        String maid = (String)httpUtils.getParameter("maid");
        String fromdate = (String)httpUtils.parameters.get("fromdate");
        String todate = (String)httpUtils.parameters.get("todate");
        String sortType = (String)httpUtils.parameters.get("sorttype");
        
        Integer status = null;
        if(httpUtils.getParameter("status") != null)
            status = Integer.parseInt((String)httpUtils.getParameter("status"));
        
        Integer maidId = null;
        if(httpUtils.getParameter("maidid") != null)
            maidId = Integer.parseInt((String)httpUtils.getParameter("maidid"));    
        
        Integer customerId = null;
        if(httpUtils.getParameter("customerid") != null)
            customerId = Integer.parseInt((String)httpUtils.getParameter("customerid"));         
        
        if(!(ordercode != null && !ordercode.trim().isEmpty())) ordercode = null;
        if(!(customer != null && !customer.trim().isEmpty())) customer = null;
        if(!(maid != null && !maid.trim().isEmpty())) maid = null;
        Date fromDate = null;
        if(fromdate != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            fromDate = df.parse(fromdate.trim() + " 00:00:00");
        }
        Date toDate = null;
        if(todate != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            toDate = df.parse(todate.trim() + " 23:59:59");
        }
        
        List<List> listResult = odb.searchPlan(numberRow, pageLength, ordercode, customer, maid, status, fromDate, toDate, maidId, customerId, sortType);
        List<List> listData = listResult.get(1);
        for(int i = 0; i < listData.size(); i++) {
            listData.get(i).set(0, numberRow + i + 1);
        }
        Integer count = Integer.parseInt(((List<List>)listResult.get(0)).get(0).get(0).toString());
        returnData.put("recordsTotal", count);
        returnData.put("recordsFiltered", count);        
        returnData.put("data", listData);
        returnAjax();        
    }
    
    public void searchTask() throws IOException, SQLException, ParseException {
        PlanDB odb = new PlanDB();
        
        int pageLength = 10;
        if(httpUtils.getParameter("length") != null) {
            pageLength = Integer.parseInt((String)httpUtils.getParameter("length"));
            if(pageLength == 0) pageLength = 10;
        }
        
        int numberRow = 0;
        if(httpUtils.getParameter("start") != null) {
            numberRow = Integer.parseInt((String)httpUtils.getParameter("start"));
        }

        String ordercode = (String)httpUtils.getParameter("ordercode");
        String maid = (String)httpUtils.getParameter("maid");
        String fromdate = (String)httpUtils.parameters.get("fromdate");
        String todate = (String)httpUtils.parameters.get("todate");
        String sortType = (String)httpUtils.parameters.get("sortType");
        
        Integer status = null;
        if(httpUtils.getParameter("status") != null)
            status = Integer.parseInt((String)httpUtils.getParameter("status"));
        
        Integer maidId = null;
        if(httpUtils.getParameter("maidid") != null)
            maidId = Integer.parseInt((String)httpUtils.getParameter("maidid"));    
        
        Integer customerId = null;
        if(httpUtils.getParameter("customerid") != null)
            customerId = Integer.parseInt((String)httpUtils.getParameter("customerid"));          
        
        if(!(ordercode != null && !ordercode.trim().isEmpty())) ordercode = null;
        if(!(maid != null && !maid.trim().isEmpty())) maid = null;
        Date fromDate = null;
        if(fromdate != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            fromDate = df.parse(fromdate.trim() + " 00:00:00");
        }
        Date toDate = null;
        if(todate != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            toDate = df.parse(todate.trim() + " 23:59:59");
        }
        
        List<List> listResult = odb.searchTask(numberRow, pageLength, ordercode, maid, status, fromDate, toDate, maidId, customerId, sortType);
        List<List> listData = listResult.get(1);
        for(int i = 0; i < listData.size(); i++) {
            listData.get(i).set(0, numberRow + i + 1);
        }
        Integer count = Integer.parseInt(((List<List>)listResult.get(0)).get(0).get(0).toString());
        returnData.put("recordsTotal", count);
        returnData.put("recordsFiltered", count);        
        returnData.put("data", listData);
        returnAjax();        
    }    
    
    public void searchSalary() throws IOException, SQLException, ParseException {
        PlanDB odb = new PlanDB();
        if(httpUtils.getParameter("ispay") != null && httpUtils.getParameter("ispay").equals("1")) {
            String payUsers = (String)httpUtils.getParameter("userid");
            if(payUsers != null) {
                payUsers = payUsers.replace("userid=", "");
                String[] lstMaid = payUsers.split("&");
                HashMap ssoUser = (HashMap) httpUtils.getSessionAttribute("sso_username");
                odb.paySalary(lstMaid, Integer.parseInt(ssoUser.get("user_id").toString()));
                HttpSession.getInstance().setCacheAttribute("cache_maid".getBytes(), (new MaidDB()).getAllMaid());
            }
        }        
        
        int pageLength = 10;
        if(httpUtils.getParameter("length") != null) {
            pageLength = Integer.parseInt((String)httpUtils.getParameter("length"));
            if(pageLength == 0) pageLength = 10;
        }
        
        int numberRow = 0;
        if(httpUtils.getParameter("start") != null) {
            numberRow = Integer.parseInt((String)httpUtils.getParameter("start"));
        }

        String maid = (String)httpUtils.getParameter("maid");
        String mobile = (String)httpUtils.getParameter("mobile");
        String fromdate = (String)httpUtils.parameters.get("fromdate");
        String todate = (String)httpUtils.parameters.get("todate");
        String status = (String)httpUtils.parameters.get("status");
        String sortAsc = (String)httpUtils.parameters.get("sortasc");
                
        if(!(maid != null && !maid.trim().isEmpty())) maid = null;
        if(!(mobile != null && !mobile.trim().isEmpty())) mobile = null;
        if(!(status != null && !status.trim().isEmpty())) status = null;
        
        Date fromDate = null;
        if(fromdate != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            fromDate = df.parse(fromdate.trim() + " 00:00:00");
        }
        Date toDate = null;
        if(todate != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            toDate = df.parse(todate.trim() + " 23:59:59");
        }
        
        List<List> listResult = odb.searchSalary(numberRow, pageLength, maid, mobile, status, fromDate, toDate, sortAsc);
        List<List> listData = listResult.get(1);
        for(int i = 0; i < listData.size(); i++) {
            listData.get(i).set(0, numberRow + i + 1);
        }
        Integer count = 0;
        if(!listResult.get(0).isEmpty())
            count = Integer.parseInt(((List<List>)listResult.get(0)).get(0).get(0).toString());
        returnData.put("recordsTotal", count);
        returnData.put("recordsFiltered", count);        
        returnData.put("data", listData);
        returnAjax();        
    }
    
    public void viewSalary() throws IOException, SQLException, ParseException {
        PlanDB odb = new PlanDB();
        
        Integer maidId = null;
        if(httpUtils.getParameter("maidId") != null)
            maidId = Integer.parseInt((String)httpUtils.getParameter("maidId"));
        
        Integer spendId = null;
        if(httpUtils.getParameter("spendId") != null && !"null".equals(httpUtils.getParameter("spendId")))
            spendId = Integer.parseInt(httpUtils.getParameter("spendId").toString().substring(2));
                
        List<List> listResult = odb.viewSalary(maidId, spendId);
        List<List> listData = listResult.get(1);
        for(int i = 0; i < listData.size(); i++) {
            listData.get(i).set(0, i + 1);
        }
        returnData.put("data", listData);
        returnAjax();        
    }    
    
    public void viewPlan() throws IOException, SQLException, ParseException {
        String planid = (String)httpUtils.getParameter("planid");
        if(planid != null && !planid.trim().isEmpty()) {
            HashMap plan = (HashMap)(new PlanDB()).getPlanById(Integer.parseInt(planid));
            this.returnData.put("plan", plan);
            
            Date startDate = (Date)plan.get("start");
            Date endDate = (Date)plan.get("end");
            Long intStartLength = (endDate.getTime() - startDate.getTime())/60000;
            String startLength = intStartLength.toString();
            List lstStartLength = new ArrayList();
            lstStartLength.add(startLength);
            List lstStartDate = new ArrayList();
            lstStartDate.add(startDate);
            RsOrderAction rso = new RsOrderAction(httpUtils);
            List<Map> lstMaid = rso.getMaidByDate(lstStartLength, lstStartDate, 50);
            this.returnData.put("maid", lstMaid);
                    
            File resultFile = new File("web/app/plan/viewPlan.html").getCanonicalFile();
            String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
            this.returnData.put("page", contentPage);
        }
        returnAjax();
    }
    
    public void viewTask() throws IOException, SQLException, ParseException {
        String planid = (String)httpUtils.getParameter("planid");
        if(planid != null && !planid.trim().isEmpty()) {
            HashMap plan = (HashMap)(new PlanDB()).getPlanById(Integer.parseInt(planid));
            this.returnData.put("plan", plan);
            
            Date startDate = (Date)plan.get("start");
            Date endDate = (Date)plan.get("end");
            Long intStartLength = (endDate.getTime() - startDate.getTime())/60000;
            String startLength = intStartLength.toString();
            List lstStartLength = new ArrayList();
            lstStartLength.add(startLength);
            List lstStartDate = new ArrayList();
            lstStartDate.add(startDate);
            RsOrderAction rso = new RsOrderAction(httpUtils);
            List<Map> lstMaid = rso.getMaidByDate(lstStartLength, lstStartDate, 50);
            this.returnData.put("maid", lstMaid);
                    
            File resultFile = new File("web/app/task/viewTask.html").getCanonicalFile();
            String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
            this.returnData.put("page", contentPage);
        }
        returnAjax();
    }    
    
    public void updatePlan() throws IOException, ParseException, SQLException {
        String planId = (String)httpUtils.getParameter("planId");
        String orderId = (String)httpUtils.getParameter("orderId");
        String maidId = (String)httpUtils.parameters.get("maidId");
        String startDate = (String)httpUtils.parameters.get("start");
        String endDate = (String)httpUtils.parameters.get("end");
        String status = (String)httpUtils.parameters.get("status");
        String apply = (String)httpUtils.parameters.get("apply");
        List lstParam = new ArrayList();
                
        Integer intMaidId = null;
        if(maidId != null && !maidId.trim().isEmpty()) intMaidId = Integer.parseInt(maidId);

        Integer intPlanId = null;
        if(planId != null && !planId.trim().isEmpty()) intPlanId = Integer.parseInt(planId);
        
        Integer intOrderId = null;
        if(orderId != null && !orderId.trim().isEmpty()) intOrderId = Integer.parseInt(orderId);        
        
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");        
        Date dtStartdate = null;
        if(startDate != null && !startDate.isEmpty()) {
            dtStartdate = df.parse(startDate.trim());
            lstParam.add(dtStartdate);
        } else lstParam.add(null);
        
        Date dtEnddate = null;
        if(endDate != null && !endDate.isEmpty()) {
            dtEnddate = df.parse(endDate.trim());
            lstParam.add(dtEnddate);
        } else lstParam.add(null);
        
        Integer intStatus = null;
        if(status != null && !status.trim().isEmpty()) intStatus = Integer.parseInt(status);
        
        if(maidId != null && !maidId.trim().isEmpty() && planId != null && !planId.trim().isEmpty()) {
            (new PlanDB()).updateMaidPlan(intMaidId, dtStartdate, dtEnddate, intStatus, intPlanId, apply, intOrderId);
            List<Map> lstPlan = (new MaidDB()).getAllPlanByOrderId(intOrderId);
            for(int i = 0; i < lstPlan.size(); i++) {
                HttpSession.getInstance().setStoreAttribute("cache_plan", lstPlan.get(i).get("plan_id").toString(), lstPlan.get(i));
            }             
        }
        
        returnAjax(); 
    }
    
    public void updateTask() throws IOException, ParseException, SQLException {
        String planId = (String)httpUtils.getParameter("planId");
        String realStart = (String)httpUtils.parameters.get("realStart");
        String realEnd = (String)httpUtils.parameters.get("realEnd");
        List lstParam = new ArrayList();
                        
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");   
        if(realStart != null && !realStart.isEmpty()) {
            Date tmpDate = df.parse(realStart.trim());
            lstParam.add(tmpDate);
        }
        else lstParam.add(null);

        if(realEnd != null && !realEnd.isEmpty()) {
            Date tmpDate = df.parse(realEnd.trim());
            lstParam.add(tmpDate);
        }
        else lstParam.add(null);
        
        if(planId != null && !planId.trim().isEmpty()) lstParam.add(Integer.parseInt(planId));
        else lstParam.add(null);        
        
        if(planId != null && !planId.trim().isEmpty()) {
            (new PlanDB()).updateMaidTask(lstParam);
            Map planCache = (Map)HttpSession.getInstance().getStoreAttribute("cache_plan", planId);
            if(realStart != null && !realStart.isEmpty()) planCache.put("real_start", realStart + ":00");
            if(realEnd != null && !realEnd.isEmpty()) planCache.put("real_end", realEnd + ":00");
            HttpSession.getInstance().setStoreAttribute("cache_plan", planId, planCache);
        }
        
        returnAjax(); 
    }
    
    public void updateLocation() throws SQLException, IOException {
        String realLat = (String)httpUtils.getParameter("realLat");
        String realLong = (String)httpUtils.getParameter("realLong");
        String addressId = (String)httpUtils.getParameter("addressId");
        String planId = (String)httpUtils.getParameter("planId");
        String isStart = (String)httpUtils.getParameter("isStart");
        
        List lstParam = new ArrayList();
        lstParam.add(Double.parseDouble(realLat));
        lstParam.add(Double.parseDouble(realLong));
        lstParam.add(Integer.parseInt(addressId));
        
        List lstParam1 = new ArrayList();
        lstParam1.add(Integer.parseInt(planId));
        
        DatabaseConnector.getInstance().executeData("update address set latitude = ?, longitude = ? where address_id = ?", lstParam);
        if("1".equals(isStart)) DatabaseConnector.getInstance().executeData("update plan set distance = 0 where plan_id = ? ", lstParam1);
        else DatabaseConnector.getInstance().executeData("update plan set finish_distance = 0 where plan_id = ? ", lstParam1);
        
        Map planCache = (Map)HttpSession.getInstance().getStoreAttribute("cache_plan", planId);
        if("1".equals(isStart)) planCache.put("distance", 0);
        else planCache.put("finish_distance", 0);
        planCache.put("latitude", Double.parseDouble(realLat));
        planCache.put("longitude", Double.parseDouble(realLong));
        HttpSession.getInstance().setStoreAttribute("cache_plan", planId, planCache);
        
        returnAjax();
    }
}
