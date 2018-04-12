/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.web;

import com.hh.action.BaseAction;
import com.hh.app.db.IncidentDB;
import com.hh.util.FileUtils;
import com.hh.web.HttpUtils;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author buixu
 */
public class IncidentAction extends BaseAction {
    
    public IncidentAction(HttpUtils hu) {
        super(hu);
    }
    
     public void listIncident() throws IOException {
        returnPage("web/app/incident/listIncident.html");
    }   
    
    public void searchIncident() throws IOException, SQLException, ParseException {
        IncidentDB msgDB = new IncidentDB();
        int pageLength = 10;
        if(httpUtils.getParameter("length") != null) {
            pageLength = Integer.parseInt((String)httpUtils.getParameter("length"));
            if(pageLength == 0) pageLength = 10;
        }
        
        int numberRow = 0;
        if(httpUtils.getParameter("start") != null) {
            numberRow = Integer.parseInt((String)httpUtils.getParameter("start"));
        }

        String content = (String)httpUtils.getParameter("content");
        String managerName = (String)httpUtils.getParameter("managerName");
        String userName = (String)httpUtils.getParameter("userName");
        String fromdate = (String)httpUtils.parameters.get("fromdate");
        String todate = (String)httpUtils.parameters.get("todate");
        String statusStr = (String)httpUtils.parameters.get("status");
        String sortAsc = (String)httpUtils.parameters.get("sortasc");
        
        if(!(content != null && !content.trim().isEmpty())) content = null;
        if(!(managerName != null && !managerName.trim().isEmpty())) managerName = null;
        if(!(userName != null && !userName.trim().isEmpty())) userName = null;
        Integer status = null;
        if(StringUtils.isNotEmpty(statusStr)){
            status = Integer.parseInt(statusStr.trim());
            if(status == 0){
                status = null;
            }
        }
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
        
        List<List> listResult = msgDB.searchIncident(numberRow, pageLength,content, userName,managerName, fromDate, toDate, status, sortAsc);
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
    
    public void backListIncident() throws IOException, ParseException, SQLException {
        File resultFile = new File("web/app/incident/listIncident.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
        this.returnData.put("page", contentPage);
        returnAjax();
    }   
}
