/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.db;

import com.hh.database.C3p0Connector;
import com.hh.database.DatabaseConnector;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author buixu
 */
public class MessageDB {
    public List<Map> getAllMessage() throws SQLException {
        return DatabaseConnector.getInstance().queryData("select * from message");
    }    
    
    public Integer insertMessage(List lstParam) throws SQLException {
        String sql = " insert into message(content,sender_id,send_date, status, receiever_id) values (?,?,?,?,?) ";
        return C3p0Connector.getInstance().insertData(sql, lstParam);
    }
    
    public void receiveMessage(int messageId, int status) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(status);
        lstParam.add(messageId);
        DatabaseConnector.getInstance().executeData("update message set status = status  where message_id = ? ", lstParam);
    }
    
    public Map getMessageById(Integer messageId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(messageId);
        List<Map> lstMessage = DatabaseConnector.getInstance().queryData("select * from message where message_id = ?", lstParam);
        if(lstMessage != null && !lstMessage.isEmpty()) return lstMessage.get(0);
        else return null;
    }
     
    public void deleteMessage(String lstMessages) throws SQLException {
        DatabaseConnector.getInstance().executeData("delete from message where message_id in (" + lstMessages + ")");
    }
    
    public List<List> searchMessage(Integer numberRow, Integer pageLength, String content, String senderName, String receiverName, Date fromDate, Date toDate, String sortAsc) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData = " SELECT 1 as rownum, content, CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal\" onclick=\"loadViewUser(',sender_id,');\">',usr1.name,'</a>') as sender_name, "
                + " DATE_FORMAT(send_date, '%d-%m %H:%i') as send_date ,  " +
                " CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal\" onclick=\"loadViewUser(',receiver_id,');\">',usr2.name,'</a>') as receiver_name, " +
                " CASE status " +
                " WHEN 0 THEN 'Đang gửi' " +
                " WHEN 1 THEN 'Đã nhận' " +
                " END AS status, " +
                " CONCAT('<input type=\"checkbox\" name=\"messageId\" onclick=\"validateCheckAll()\" value=\"',message_id,'\"/>') as message_id " +
                " FROM message left join sm_user usr1 on sender_id = usr1.user_id "
                + " left join sm_user usr2 on receiver_id = usr2.user_id "
                + " WHERE 1 = 1 ";
        String queryCount = " SELECT count(message_id) FROM message left join sm_user usr1 on sender_id = usr1.user_id "
                + " left join sm_user usr2 on receiver_id = usr2.user_id  WHERE  1 = 1 ";
        String query = " ";
        if(StringUtils.isNotEmpty(content)){
           query += " AND content like ? ";
           lstParam.add("%" + content.trim() + "%");
           lstParamCount.add("%" + content.trim() + "%"); 
        }
        if(StringUtils.isNotEmpty(senderName)){
           query += " AND usr1.name like ? ";
           lstParam.add("%" + senderName.trim() + "%");
           lstParamCount.add("%" + senderName.trim() + "%"); 
        }
        if(StringUtils.isNotEmpty(receiverName)){
           query += " AND usr2.name like ? ";
           lstParam.add("%" + receiverName.trim() + "%");
           lstParamCount.add("%" + receiverName.trim() + "%"); 
        }
        if(fromDate != null) {
            query += " AND send_date >= ? ";
            lstParam.add(fromDate);            
            lstParamCount.add(fromDate);            
        }
        if(toDate != null) {
            query += " AND send_date <= ? ";
            lstParam.add(toDate);            
            lstParamCount.add(toDate);            
        }
        
        queryCount += query;
        if("1".equals(sortAsc)) query += " ORDER BY message.send_date ASC LIMIT ?,? ";
        else query += " ORDER BY message.send_date DESC LIMIT ?,? "; 
        queryData += query;
        lstParam.add(numberRow);
        lstParam.add(pageLength);
        List<List> lstMessage = DatabaseConnector.getInstance().queryDataToList(queryData, lstParam);
        List<List> lstCount = DatabaseConnector.getInstance().queryDataToList(queryCount, lstParamCount);
        List<List> lstResult = new ArrayList();
        lstResult.add(lstCount);
        lstResult.add(lstMessage);
        return lstResult;
    }
}
