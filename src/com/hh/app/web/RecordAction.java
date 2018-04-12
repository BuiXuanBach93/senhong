/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.web;

import com.hh.action.BaseAction;
import com.hh.app.db.FundDB;
import com.hh.app.db.RecordDB;
import com.hh.util.FileUtils;
import com.hh.web.HttpSession;
import com.hh.web.HttpUtils;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author buixu
 */
public class RecordAction extends BaseAction{
    
    public RecordAction(HttpUtils hu) {
        super(hu);
    }
     public void listRecord() throws IOException {
        returnPage("web/app/record/listRecord.html");
    }
    
    public void viewAddRecord() throws IOException, SQLException {
        File resultFile = new File("web/app/record/viewAddRecord.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);
        this.returnData.put("page", contentPage);
        returnAjax();
    }    
    
     public void loadViewRecord() throws IOException, SQLException {
        String recordId = (String)httpUtils.getParameter("recordId");
        if(recordId != null && !recordId.trim().isEmpty()) {
            this.returnData.put("record",(HashMap)(new RecordDB()).getRecordById(Integer.parseInt(recordId)));
            File resultFile = new File("web/app/record/viewRecord.html").getCanonicalFile();
            String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
            this.returnData.put("page", contentPage);
        }
        returnAjax();
    }
    
    
    public void searchRecord() throws IOException, SQLException, ParseException {
        RecordDB recordDB = new RecordDB();
        if(httpUtils.getParameter("isdelete") != null && httpUtils.getParameter("isdelete").equals("1")) {
            String deleteRecords = (String)httpUtils.getParameter("recordId");
            if(deleteRecords != null) {
                deleteRecords = deleteRecords.replace("recordId=", "");
                deleteRecords = deleteRecords.replace("&", ",");
                recordDB.deleteRecord(deleteRecords);
                HttpSession.getInstance().setCacheAttribute("bm_record".getBytes(), (new RecordDB()).getAllRecord());
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

        String name = null;
        if(httpUtils.getParameter("name") != null)
            name = (String)httpUtils.getParameter("name");
        
        
        List<List> listResult = recordDB.searchRecord(numberRow, pageLength, name);
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
        
    public void addRecord() throws IOException, SQLException, ParseException {
        String name = (String)httpUtils.parameters.get("name");
        String description = (String)httpUtils.parameters.get("description");
        String amount = (String)httpUtils.parameters.get("reward_amount");    
        List lstParam = new ArrayList();
        
        if(name != null && !name.trim().isEmpty()) lstParam.add(name);
        else lstParam.add(null);   
        
        if(description != null && !description.trim().isEmpty()) lstParam.add(description);
        else lstParam.add(null);
        
        if(amount != null && !amount.trim().isEmpty()) lstParam.add(Integer.parseInt(amount));
        else lstParam.add(null);
                
        (new RecordDB()).insertRecord(lstParam);
        
        HttpSession.getInstance().setCacheAttribute("bm_record".getBytes(), (new RecordDB()).getAllRecord());
        
        returnAjax(); 
    }
    
    public void updateRecord() throws IOException, ParseException, SQLException {
        String recordId = (String)httpUtils.getParameter("recordid");
        
        String recordName = (String)httpUtils.parameters.get("recordname");
        String amount = (String)httpUtils.parameters.get("amount");
        String description = (String)httpUtils.parameters.get("description");
        
        List lstParam = new ArrayList();
        
        if(recordName != null) lstParam.add(recordName.trim());
        else lstParam.add(null);
        
        if(amount != null) lstParam.add(Integer.parseInt(amount.trim()));
        else lstParam.add(null);

        if(description != null) lstParam.add(description.trim());
        else lstParam.add(null);
        
        if(recordId != null && !recordId.trim().isEmpty()) {
            lstParam.add(Integer.parseInt(recordId));
        }
        else lstParam.add(null);        
        
        (new RecordDB()).updateRecord(lstParam);
        
        Map record = (new RecordDB()).getRecordById(Integer.parseInt(recordId));
        
        HttpSession.getInstance().setCacheAttribute("bm_record".getBytes(), (new RecordDB()).getAllRecord());
        this.returnData.put("data", record);
        returnAjax(); 
    }
    
    
    public void backListRecord() throws IOException, ParseException, SQLException {
        File resultFile = new File("web/app/record/listRecord.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
        this.returnData.put("page", contentPage);
        returnAjax();
    }   
}
