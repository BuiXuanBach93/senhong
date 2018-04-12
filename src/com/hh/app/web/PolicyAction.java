/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.web;

import com.hh.action.BaseAction;
import com.hh.app.db.CategoryDB;
import com.hh.app.db.MaidDB;
import com.hh.app.db.PolicyDB;
import com.hh.database.C3p0Connector;
import com.hh.server.HHServer;
import com.hh.util.FileUtils;
import com.hh.web.HttpSession;
import com.hh.web.HttpUtils;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author agiletech
 */
public class PolicyAction extends BaseAction{

    public PolicyAction(HttpUtils hu) {
        super(hu);
    }
    
    public void listPolicy() throws IOException {
        returnPage("web/app/policy/listPolicy.html");
    }
    
    public void viewAddPolicy() throws IOException, SQLException {
        File resultFile = new File("web/app/policy/viewAddPolicy.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);
        this.returnData.put("page", contentPage);
        this.returnData.put("areas", (new PolicyDB()).getArea());
        returnAjax();
    }    
    
    public void searchPolicy() throws IOException, SQLException, ParseException {
        PolicyDB udb = new PolicyDB();
        if(httpUtils.getParameter("isdelete") != null && httpUtils.getParameter("isdelete").equals("1")) {
            String deletePolicys = (String)httpUtils.getParameter("policyId");
            if(deletePolicys != null) {
                deletePolicys = deletePolicys.replace("policyId=", "");
                deletePolicys = deletePolicys.replace("&", ",");
                udb.deletePolicy(deletePolicys);
                HttpSession.getInstance().setCacheAttribute("cache_policy".getBytes(), (new PolicyDB()).getAllPolicy());
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

        String policyCode = (String)httpUtils.getParameter("policyCode");
        String content = (String)httpUtils.getParameter("content");
        String fromdate = (String)httpUtils.parameters.get("fromdate");
        String todate = (String)httpUtils.parameters.get("todate");
        
        Integer status = null;
        if(httpUtils.getParameter("status") != null)
            status = Integer.parseInt((String)httpUtils.getParameter("status"));
        
        if(!(policyCode != null && !policyCode.trim().isEmpty())) policyCode = null;
        if(!(content != null && !content.trim().isEmpty())) content = null;
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
        
        List<List> listResult = udb.searchPolicy(numberRow, pageLength, policyCode, content, status, fromDate, toDate);
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
        
    public void addPolicy() throws IOException, SQLException, ParseException {
        String policyCode = (String)httpUtils.parameters.get("policyCode");
        String content = (String)httpUtils.parameters.get("content");
        String timeFrom = (String)httpUtils.parameters.get("timeFrom");
        String timeTo = (String)httpUtils.parameters.get("timeTo");
        String fromDate = (String)httpUtils.parameters.get("fromDate");
        String toDate = (String)httpUtils.parameters.get("toDate");
        String areaId = (String)httpUtils.parameters.get("areaId");
        String status = (String)httpUtils.parameters.get("status");
        String addRate = (String)httpUtils.parameters.get("addRate");
        String addVnd = (String)httpUtils.parameters.get("addVnd");

        List lstParam = new ArrayList();
        
        if(policyCode != null) lstParam.add(policyCode.trim());
        else lstParam.add(null);
        
        if(content != null) lstParam.add(content.trim());
        else lstParam.add(null);
        
        if(timeFrom != null && !timeFrom.trim().isEmpty()) lstParam.add(Integer.parseInt(timeFrom));
        else lstParam.add(null);          
        
        if(timeTo != null && !timeTo.trim().isEmpty()) lstParam.add(Integer.parseInt(timeTo));
        else lstParam.add(null);          
        
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        if(fromDate != null) {
            Date tmpDate = df.parse(fromDate.trim());
            lstParam.add(tmpDate);
        }
        else lstParam.add(null);

        if(toDate != null) {
            Date tmpDate = df.parse(toDate.trim());
            lstParam.add(tmpDate);
        }
        else lstParam.add(null);
        
        if(areaId != null && !areaId.trim().isEmpty()) lstParam.add(Integer.parseInt(areaId));
        else lstParam.add(null);   
        
        if(status != null && !status.trim().isEmpty()) lstParam.add(Integer.parseInt(status));
        else lstParam.add(null);
        
        if(addRate != null && !addRate.trim().isEmpty()) lstParam.add(Integer.parseInt(addRate));
        else lstParam.add(null);        

        if(addVnd != null && !addVnd.trim().isEmpty()) lstParam.add(Integer.parseInt(addVnd));
        else lstParam.add(null);        
        
        lstParam.add(new Date());
        
        (new PolicyDB()).insertPolicy(lstParam);
        
        HttpSession.getInstance().setCacheAttribute("cache_policy".getBytes(), (new PolicyDB()).getAllPolicy());
        
        returnAjax(); 
    }
        
    public void viewPolicy() throws IOException, SQLException {
        String policyId = (String)httpUtils.getParameter("policyId");
        if(policyId != null && !policyId.trim().isEmpty()) {
            this.returnData.put("policy",(HashMap)(new PolicyDB()).getPolicyById(Integer.parseInt(policyId)));
            File resultFile = new File("web/app/policy/viewPolicy.html").getCanonicalFile();
            String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
            this.returnData.put("page", contentPage);
            this.returnData.put("areas", (new PolicyDB()).getArea());
        }
        returnAjax();
    }
        
    public void updatePolicy() throws IOException, ParseException, SQLException {
        //------- Update policy --------------------------------------------
        String policyId = (String)httpUtils.getParameter("policyId");
        String policyCode = (String)httpUtils.parameters.get("policyCode");
        String content = (String)httpUtils.parameters.get("content");
        String timeFrom = (String)httpUtils.parameters.get("timeFrom");
        String timeTo = (String)httpUtils.parameters.get("timeTo");
        String fromDate = (String)httpUtils.parameters.get("fromDate");
        String toDate = (String)httpUtils.parameters.get("toDate");
        String status = (String)httpUtils.parameters.get("status");
        String areaId = (String)httpUtils.parameters.get("areaId");
        String addRate = (String)httpUtils.parameters.get("addRate");
        String addVnd = (String)httpUtils.parameters.get("addVnd");
        List lstParam = new ArrayList();
        
        if(policyCode != null) lstParam.add(policyCode.trim());
        else lstParam.add(null);
        
        if(content != null) lstParam.add(content.trim());
        else lstParam.add(null);
        
        if(timeFrom != null && !timeFrom.trim().isEmpty()) lstParam.add(Integer.parseInt(timeFrom));
        else lstParam.add(null);        

        if(timeTo != null && !timeTo.trim().isEmpty()) lstParam.add(Integer.parseInt(timeTo));
        else lstParam.add(null);        
        
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        if(fromDate != null) {
            Date tmpDate = df.parse(fromDate.trim());
            lstParam.add(tmpDate);
        }
        else lstParam.add(null);

        if(toDate != null) {
            Date tmpDate = df.parse(toDate.trim());
            lstParam.add(tmpDate);
        }
        else lstParam.add(null);
        
        if(areaId != null && !areaId.trim().isEmpty()) lstParam.add(Integer.parseInt(areaId));
        else lstParam.add(null);        

        if(status != null && !status.trim().isEmpty()) lstParam.add(Integer.parseInt(status));
        else lstParam.add(null);        
        
        if(addRate != null && !addRate.trim().isEmpty()) lstParam.add(Integer.parseInt(addRate));
        else lstParam.add(null);        

        if(addVnd != null && !addVnd.trim().isEmpty()) lstParam.add(Integer.parseInt(addVnd));
        else lstParam.add(null);

        if(policyId != null && !policyId.trim().isEmpty()) lstParam.add(Integer.parseInt(policyId));
        else lstParam.add(null);        
        
        (new PolicyDB()).updatePolicy(lstParam);   
        
        HttpSession.getInstance().setCacheAttribute("cache_policy".getBytes(), (new PolicyDB()).getAllPolicy());
        
        returnAjax(); 
    }
    
    public void backListPolicy() throws IOException, ParseException, SQLException {
        File resultFile = new File("web/app/policy/listPolicy.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
        this.returnData.put("page", contentPage);
        returnAjax();
    }   
    
    public static void loadCacheData() {
        try {
            HttpSession.getInstance().createStore("cache_plan");
            List<Map> lstPlan = (new MaidDB()).getAllPlan();
            for(int i = 0; i < lstPlan.size(); i++) {
                HttpSession.getInstance().setStoreAttribute("cache_plan", lstPlan.get(i).get("plan_id").toString(), lstPlan.get(i));
            }            
            HttpSession.getInstance().setCacheAttribute("cache_policy".getBytes(), (new PolicyDB()).getAllPolicy());
            HttpSession.getInstance().setCacheAttribute("cache_category".getBytes(), (new CategoryDB()).getAllCategory());
            HttpSession.getInstance().setCacheAttribute("cache_restTime".getBytes(), (new MaidDB()).getAllRestTime());
            HttpSession.getInstance().setCacheAttribute("cache_workTime".getBytes(), (new MaidDB()).getAllWorkTime());
            HttpSession.getInstance().setCacheAttribute("cache_maid".getBytes(), (new MaidDB()).getAllMaid());
            
            String sql = " select * from price ";
            List<Map> lstPrice = C3p0Connector.getInstance().queryData(sql);            
            HttpSession.getInstance().setCacheAttribute("cache_price".getBytes(), lstPrice);
            
        } catch (SQLException ex) {
            HHServer.mainLogger.error("HHServer error: ", ex);
        }        
    }
}
