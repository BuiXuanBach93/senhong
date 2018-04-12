/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.web;

import com.hh.action.BaseAction;
import com.hh.app.db.FundDB;
import com.hh.app.db.FundHistoryDB;
import com.hh.app.db.SpendDB;
import com.hh.app.db.UserDB;
import com.hh.util.EncryptDecryptUtils;
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
import java.util.List;

/**
 *
 * @author buixu
 */
public class SpendAction extends BaseAction{
    
    public SpendAction(HttpUtils hu) {
        super(hu);
    }
    
    public void listSpend() throws IOException {
        returnPage("web/app/spend/listSpend.html");
    }
    
     public void viewAddSpend() throws IOException, SQLException {
        File resultFile = new File("web/app/spend/viewAddSpend.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);
        this.returnData.put("fund", (new FundDB().getAllFund()));
        this.returnData.put("user", (new UserDB()).getAllUser());
        this.returnData.put("page", contentPage);
        returnAjax();
    }   
    
    public void addSpend() throws IOException, SQLException, ParseException {
        String payerName = (String)httpUtils.parameters.get("payerName");
        String payerMobile = (String)httpUtils.parameters.get("payerMobile");
        String receiverName = (String)httpUtils.parameters.get("receiverName");
        String receiverMobile = (String)httpUtils.parameters.get("receiverMobile");
        String payDateStr = (String)httpUtils.parameters.get("payDate");
        String amountStr = (String)httpUtils.parameters.get("amount");
        String spendReason = (String)httpUtils.parameters.get("spendReason");
        String statusStr = (String)httpUtils.parameters.get("status");
        String fundIdStr = (String)httpUtils.parameters.get("fundId");
        String payerId = (String)httpUtils.parameters.get("payerId");
        String receiverId = (String)httpUtils.parameters.get("receiverId");
        
       // add param to insert into db
        List lstParam = new ArrayList();
        if(receiverId != null && !receiverId.trim().isEmpty()) lstParam.add(Integer.parseInt(receiverId)); // receiver_name
        else lstParam.add(null);
        if(payerId != null && !payerId.trim().isEmpty()) lstParam.add(Integer.parseInt(payerId)); // receiver_name
        else lstParam.add(null);
        
        if(receiverName != null && !receiverName.trim().isEmpty()) lstParam.add(receiverName); // receiver_name
        else lstParam.add(null);
        if(receiverMobile != null && !receiverMobile.trim().isEmpty()) lstParam.add(receiverMobile); // receiver_mobile
        else lstParam.add(null);
        
        if(payerName != null && !payerName.trim().isEmpty()) lstParam.add(payerName);  // payer_name
        else lstParam.add(null);
        
        if(payerMobile != null && !payerMobile.trim().isEmpty()) lstParam.add(payerMobile);   // payer_mobile
        else lstParam.add(null);
                
        Date payDate = null;
        if(payDateStr != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            payDate = df.parse(payDateStr.trim() + " 00:00:00");
        }
        lstParam.add(payDate); // pay_date
        
        if(amountStr != null && !amountStr.trim().isEmpty()) lstParam.add(Integer.parseInt(amountStr.replace(",", "")));
        else lstParam.add(null);  // amount
        
        if(spendReason != null && !spendReason.trim().isEmpty()) lstParam.add(spendReason);
        else lstParam.add(null); // spendReason

        if(fundIdStr != null && !fundIdStr.trim().isEmpty()) lstParam.add(Integer.parseInt(fundIdStr));
        else lstParam.add(null);  // fund_Id
        
        if(statusStr != null && !statusStr.trim().isEmpty()) lstParam.add(Integer.parseInt(statusStr)); // status
        else lstParam.add(null);
        
        (new SpendDB()).insertSpend(lstParam);
        
        HttpSession.getInstance().setCacheAttribute("bm_spend".getBytes(), (new SpendDB().getAllSpend()));
        
        returnAjax(); 
    }
     
    public void searchSpend() throws IOException, SQLException, ParseException {
        SpendDB udb = new SpendDB();
        FundHistoryDB fundHistoryDb = new FundHistoryDB();
        if(httpUtils.getParameter("isaccept") != null && httpUtils.getParameter("isaccept").equals("1")) {
            String acceptSpends = (String)httpUtils.getParameter("spendId");
            if(acceptSpends != null) {
                String fund = (String)httpUtils.getParameter("fund");
                if(fund != null) {
                    String[] arrFund = fund.split(",");
                    for(int i = 0; i < arrFund.length; i++) {
                        Integer fundId = Integer.parseInt(arrFund[i].split("_")[0]);
                        Integer amount = Integer.parseInt(arrFund[i].split("_")[1]);
                        (new FundDB()).receiveFund(fundId, amount);
                    }
                
                    acceptSpends = acceptSpends.replace("spendId=", "");
                    String[] arrSpend = acceptSpends.split("&");
                    for(int i = 0; i < arrSpend.length; i++) {
                        Integer fundId = Integer.parseInt(arrFund[i].split("_")[0]);
                        Integer receiveId = Integer.parseInt(arrSpend[i]);
                        List lstParam = new ArrayList();
                        lstParam.add(fundId);
                        lstParam.add(receiveId);
                        udb.acceptSpend(lstParam); 
                        // generate log history for fund
                        Integer amount = Integer.parseInt(arrFund[i].split("_")[1]);
                        fundHistoryDb.addSpendRecord(Integer.parseInt(arrSpend[i]), amount);
                    }
                }
            }
        }    
        
        if(httpUtils.getParameter("isdelete") != null && httpUtils.getParameter("isdelete").equals("1")) {
            String deleteSpends = (String)httpUtils.getParameter("spendId");
            if(deleteSpends != null) {
                deleteSpends = deleteSpends.replace("spendId=", "");
                deleteSpends = deleteSpends.replace("&", ",");
                udb.deleteSpend(deleteSpends);
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

        String receiverName = (String)httpUtils.getParameter("receiverName");
        String payerName = (String)httpUtils.getParameter("payerName");
        String fromdate = (String)httpUtils.parameters.get("fromdate");
        String todate = (String)httpUtils.parameters.get("todate");
        String sortAsc = (String)httpUtils.parameters.get("sortasc");
        
        if(!(receiverName != null && !receiverName.trim().isEmpty())) receiverName = null;
        if(!(payerName != null && !payerName.trim().isEmpty())) payerName = null;
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
        
        Integer intStatus = null;
        if(httpUtils.getParameter("status") != null)
            intStatus = Integer.parseInt((String)httpUtils.getParameter("status"));        
        
        List<List> listResult = udb.searchSpend(numberRow, pageLength, payerName,receiverName, fromDate, toDate, intStatus, sortAsc);
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
    
    public void backListSpend() throws IOException, ParseException, SQLException {
        File resultFile = new File("web/app/spend/listSpend.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
        this.returnData.put("page", contentPage);
        returnAjax();
    }   
}
