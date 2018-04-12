/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.web;

import com.hh.action.BaseAction;
import com.hh.app.db.PromotionDB;
import com.hh.util.FileUtils;
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

/**
 *
 * @author agiletech
 */
public class PromotionAction extends BaseAction{

    public PromotionAction(HttpUtils hu) {
        super(hu);
    }
    
    public void listPromotion() throws IOException {
        returnPage("web/app/promotion/listPromotion.html");
    }
    
    public void viewAddPromotion() throws IOException, SQLException {
        File resultFile = new File("web/app/promotion/viewAddPromotion.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);
        this.returnData.put("page", contentPage);
        this.returnData.put("areas", (new PromotionDB()).getArea());
        returnAjax();
    }    
    
    public void searchPromotion() throws IOException, SQLException, ParseException {
        PromotionDB udb = new PromotionDB();
        if(httpUtils.getParameter("isdelete") != null && httpUtils.getParameter("isdelete").equals("1")) {
            String deletePromotions = (String)httpUtils.getParameter("promotionId");
            if(deletePromotions != null) {
                deletePromotions = deletePromotions.replace("promotionId=", "");
                deletePromotions = deletePromotions.replace("&", ",");
                udb.deletePromotion(deletePromotions);
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

        String promotionCode = (String)httpUtils.getParameter("promotionCode");
        String content = (String)httpUtils.getParameter("content");
        String fromdate = (String)httpUtils.parameters.get("fromdate");
        String todate = (String)httpUtils.parameters.get("todate");
        
        Integer status = null;
        if(httpUtils.getParameter("status") != null)
            status = Integer.parseInt((String)httpUtils.getParameter("status"));
        
        if(!(promotionCode != null && !promotionCode.trim().isEmpty())) promotionCode = null;
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
        
        List<List> listResult = udb.searchPromotion(numberRow, pageLength, promotionCode, content, status, fromDate, toDate);
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
        
    public void addPromotion() throws IOException, SQLException, ParseException {
        String promotionCode = (String)httpUtils.parameters.get("promotionCode");
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
        
        if(promotionCode != null) lstParam.add(promotionCode.trim());
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
                
        lstParam.add(new Date());
        
        (new PromotionDB()).insertPromotion(lstParam);
        
        returnAjax(); 
    }
        
    public void viewPromotion() throws IOException, SQLException {
        String promotionId = (String)httpUtils.getParameter("promotionId");
        if(promotionId != null && !promotionId.trim().isEmpty()) {
            this.returnData.put("promotion",(HashMap)(new PromotionDB()).getPromotionById(Integer.parseInt(promotionId)));
            File resultFile = new File("web/app/promotion/viewPromotion.html").getCanonicalFile();
            String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
            this.returnData.put("page", contentPage);
            this.returnData.put("areas", (new PromotionDB()).getArea());
        }
        returnAjax();
    }
        
    public void updatePromotion() throws IOException, ParseException, SQLException {
        //------- Update promotion --------------------------------------------
        String promotionId = (String)httpUtils.getParameter("promotionId");
        String promotionCode = (String)httpUtils.parameters.get("promotionCode");
        String content = (String)httpUtils.parameters.get("content");
        String timeFrom = (String)httpUtils.parameters.get("timeFrom");
        String timeTo = (String)httpUtils.parameters.get("timeTo");
        String fromDate = (String)httpUtils.parameters.get("fromDate");
        String toDate = (String)httpUtils.parameters.get("toDate");
        String status = (String)httpUtils.parameters.get("status");
        String areaId = (String)httpUtils.parameters.get("areaId");
        List lstParam = new ArrayList();
        
        if(promotionCode != null) lstParam.add(promotionCode.trim());
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
        
        if(promotionId != null && !promotionId.trim().isEmpty()) lstParam.add(Integer.parseInt(promotionId));
        else lstParam.add(null);        
        
        (new PromotionDB()).updatePromotion(lstParam);    
        returnAjax(); 
    }
    
    public void backListPromotion() throws IOException, ParseException, SQLException {
        File resultFile = new File("web/app/promotion/listPromotion.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
        this.returnData.put("page", contentPage);
        returnAjax();
    }   
}
