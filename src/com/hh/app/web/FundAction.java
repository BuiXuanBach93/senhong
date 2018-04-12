/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.web;

import com.hh.action.BaseAction;
import com.hh.app.db.FundDB;
import com.hh.app.db.UserDB;
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
public class FundAction extends BaseAction{
    
    public FundAction(HttpUtils hu) {
        super(hu);
    }
    
     public void listFund() throws IOException {
        returnPage("web/app/fund/listFund.html");
    }
    
    public void viewAddFund() throws IOException, SQLException {
        File resultFile = new File("web/app/fund/viewAddFund.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);
        this.returnData.put("page", contentPage);
        returnAjax();
    }    
    
    public void getAllFund() throws SQLException, IOException {
        this.returnData.put("fund", (new FundDB().getAllFund()));
        returnAjax();        
    }
    
    public void loadViewFund() throws IOException, SQLException {
        String fundId = (String) httpUtils.getParameter("fundId");
        if (fundId != null && !fundId.trim().isEmpty()) {
            this.returnData.put("fund", (HashMap) (new FundDB()).getFundById(Integer.parseInt(fundId)));
            File resultFile = new File("web/app/fund/viewFund.html").getCanonicalFile();
            String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);
            this.returnData.put("page", contentPage);
        }
        returnAjax();
    }
    
    public void searchFund() throws IOException, SQLException, ParseException {
        FundDB fundDB = new FundDB();
        if(httpUtils.getParameter("isdelete") != null && httpUtils.getParameter("isdelete").equals("1")) {
            String deleteFunds = (String)httpUtils.getParameter("fundId");
            if(deleteFunds != null) {
                deleteFunds = deleteFunds.replace("fundId=", "");
                deleteFunds = deleteFunds.replace("&", ",");
                fundDB.deleteFund(deleteFunds);
                HttpSession.getInstance().setCacheAttribute("bm_fund".getBytes(), fundDB.getAllFund());
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
        
        String fundName = null;
        if(httpUtils.getParameter("fundName") != null)
            fundName = (String)httpUtils.getParameter("fundName");
        
        List<List> listResult = fundDB.searchFund(numberRow, pageLength, fundName);
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
        
    public void addFund() throws IOException, SQLException, ParseException {
        String fundName = (String)httpUtils.parameters.get("fundName");
        String amountStr = (String)httpUtils.parameters.get("amount");
        String description = (String)httpUtils.parameters.get("description");
        
        List lstParam = new ArrayList();
        
        if(fundName != null && !fundName.trim().isEmpty()) lstParam.add(fundName);
        else lstParam.add(null);
        
        if(amountStr != null && !amountStr.trim().isEmpty()) lstParam.add(Integer.parseInt(amountStr.trim().replace(",", "")));
        else lstParam.add(null);
          
        if(description != null && !description.trim().isEmpty()) lstParam.add(description);
        else lstParam.add(null);
        
        (new FundDB()).insertFund(lstParam);
        
        HttpSession.getInstance().setCacheAttribute("bm_fund".getBytes(), (new FundDB()).getAllFund());
        
        returnAjax(); 
    }
    
        public void updateFund() throws IOException, ParseException, SQLException {
        String fundId = (String)httpUtils.getParameter("fundid");
        
        String fundName = (String)httpUtils.parameters.get("fundname");
        String amount = (String)httpUtils.parameters.get("amount");
        String description = (String)httpUtils.parameters.get("description");
        
        List lstParam = new ArrayList();
        
        if(fundName != null) lstParam.add(fundName.trim());
        else lstParam.add(null);
        
        if(amount != null) lstParam.add(Integer.parseInt(amount.trim()));
        else lstParam.add(null);

        if(description != null) lstParam.add(description.trim());
        else lstParam.add(null);
        
        if(fundId != null && !fundId.trim().isEmpty()) {
            lstParam.add(Integer.parseInt(fundId));
        }
        else lstParam.add(null);        
        
        (new FundDB()).updateFund(lstParam);
        
        Map fund = (new FundDB()).getFundById(Integer.parseInt(fundId));
        
        HttpSession.getInstance().setCacheAttribute("bm_fund".getBytes(), (new FundDB()).getAllFund());
        this.returnData.put("data", fund);
        returnAjax(); 
    }
    
    
    public void backListFund() throws IOException, ParseException, SQLException {
        File resultFile = new File("web/app/fund/listFund.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
        this.returnData.put("page", contentPage);
        returnAjax();
    }   
}
