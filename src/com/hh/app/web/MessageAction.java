/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.web;

import com.hh.action.BaseAction;
import com.hh.database.DatabaseConnector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.hh.app.db.MessageDB;
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


/**
 *
 * @author buixu
 */
public class MessageAction extends BaseAction{
    
    public MessageAction(HttpUtils hu) {
        super(hu);
    }
    
    public void getMyMessage() throws SQLException, IOException {
        HashMap ssoUser = (HashMap) httpUtils.getSessionAttribute("sso_username");
        List lstParam = new ArrayList();
        lstParam.add(ssoUser.get("user_id"));
        List<Map> lstMessage = DatabaseConnector.getInstance().queryData(
                " SELECT a.sender_id, b.name as sender_name, b.picture as sender_picture, a.receiver_id, c.name as receiver_name, c.picture as receiver_picture, a.content, "
                + " DATE_FORMAT(a.send_date, '%H:%i %d-%m-%Y') as send_date, a.status "
                + " FROM message a  "
                + " LEFT JOIN sm_user b on a.sender_id = b.user_id " 
                + " LEFT JOIN sm_user c on a.receiver_id = c.user_id " 
                + " WHERE a.receiver_id = ? ORDER BY message_id desc limit 4 "
                , lstParam);        
        returnData.put("message", lstMessage);
        returnAjax();        
    }

     public void listMessage() throws IOException {
        returnPage("web/app/message/listMessage.html");
    }   
    
    public void searchMessage() throws IOException, SQLException, ParseException {
        MessageDB msgDB = new MessageDB();
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
        String receiverName = (String)httpUtils.getParameter("receiverName");
        String senderName = (String)httpUtils.getParameter("senderName");
        String fromdate = (String)httpUtils.parameters.get("fromdate");
        String todate = (String)httpUtils.parameters.get("todate");
        String sortAsc = (String)httpUtils.parameters.get("sortasc");
        
        if(!(content != null && !content.trim().isEmpty())) content = null;
        if(!(receiverName != null && !receiverName.trim().isEmpty())) receiverName = null;
        if(!(senderName != null && !senderName.trim().isEmpty())) senderName = null;
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
        
        List<List> listResult = msgDB.searchMessage(numberRow, pageLength,content, senderName,receiverName, fromDate, toDate, sortAsc);
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
    
    public void backListMessage() throws IOException, ParseException, SQLException {
        File resultFile = new File("web/app/message/listMessage.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
        this.returnData.put("page", contentPage);
        returnAjax();
    }   

}
