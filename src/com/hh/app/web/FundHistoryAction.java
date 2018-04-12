/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.web;

import com.hh.action.BaseAction;
import com.hh.app.db.FundHistoryDB;
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
public class FundHistoryAction extends BaseAction{
    
    public FundHistoryAction(HttpUtils hu) {
        super(hu);
    }

    public void listFundHistory() throws IOException {
        returnPage("web/app/fundhistory/listFundHistory.html");
    }   
    
    public void searchFundHistory() throws IOException, SQLException, ParseException {
        FundHistoryDB msgDB = new FundHistoryDB();
        int pageLength = 10;
        if(httpUtils.getParameter("length") != null) {
            pageLength = Integer.parseInt((String)httpUtils.getParameter("length"));
            if(pageLength == 0) pageLength = 10;
        }
        
        int numberRow = 0;
        if(httpUtils.getParameter("start") != null) {
            numberRow = Integer.parseInt((String)httpUtils.getParameter("start"));
        }

        String fundName = (String)httpUtils.getParameter("fundName");
        String receiptNo = (String)httpUtils.getParameter("receiptNo");
        String spendNo = (String)httpUtils.getParameter("spendNo");
        String fromdate = (String)httpUtils.parameters.get("fromdate");
        String todate = (String)httpUtils.parameters.get("todate");
        String typeStr = (String)httpUtils.parameters.get("type");
        String sortAsc = (String)httpUtils.parameters.get("sortasc");
        
        if(!(fundName != null && !fundName.trim().isEmpty())) fundName = null;
        if(!(receiptNo != null && !receiptNo.trim().isEmpty())) receiptNo = null;
        if(!(spendNo != null && !spendNo.trim().isEmpty())) spendNo = null;
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
        Integer type = null;
        if(StringUtils.isNotEmpty(typeStr)){
            type = Integer.parseInt(typeStr);
            if(type == 0){
                type = null;
            }
        }
        List<List> listResult = msgDB.searchFundHistory(numberRow, pageLength,fundName, spendNo,receiptNo, fromDate, toDate, type, sortAsc);
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
    
    public void backListFundHistory() throws IOException, ParseException, SQLException {
        File resultFile = new File("web/app/fundhistory/listFundHistory.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
        this.returnData.put("page", contentPage);
        returnAjax();
    }   
}
