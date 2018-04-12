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

/**
 *
 * @author buixu
 */
public class SpendDB {
    public void insertSpend(List lstParam) throws SQLException {
        String sql = " insert into spend (receiver_id, payer_id, receiver_name, receiver_mobile, payer_name, "
                + " payer_mobile,pay_date, amount,spend_reason,fund_id,status) values (?,?,?,?,?,?,?,?,?,?,?) ";
        C3p0Connector.getInstance().executeData(sql, lstParam);
    }  
     
    public List<List> searchSpend(Integer numberRow, Integer pageLength, String payerName, String receiverName, Date fromDate, Date toDate, Integer status, String sortAsc) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData = " SELECT 1 as rownum, CONCAT('PC', s.spend_id) as spend_id, spend_reason, "
                + " CASE WHEN s.payer_id is null "
                + " THEN s.payer_name "
                + " ELSE CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal\" onclick=\"loadViewUser(',b.user_id,');\">',b.name,'</a>') "
                + " END AS payer_name, "
                + " CASE WHEN s.receiver_id is null "
                + " THEN s.receiver_name "
                + " ELSE CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal\" onclick=\"loadViewUser(',a.user_id,');\">',a.name,'</a>') "
                + " END AS receiver_name, "
                + " s.amount, DATE_FORMAT(s.pay_date, '%d-%m-%Y') as pay_date, "
                + " CASE s.status " 
                + " WHEN 1 THEN f.fund_id " 
                + " WHEN 2 THEN f.fund_name " 
                + " END AS fund, "                 
                + " CASE s.status " 
                + " WHEN 1 THEN 'Chưa xác nhận' " 
                + " WHEN 2 THEN 'Đã xác nhận' " 
                + " END AS status, " 
                + " CONCAT('<input type=\"checkbox\" name=\"spendId\" onclick=\"validateCheckAll()\" value=\"',spend_id,'\"/>') as spend_id " 
                + " FROM spend s "
                + " LEFT JOIN fund as f ON s.fund_id = f.fund_id "
                + " LEFT JOIN sm_user as a ON s.receiver_id = a.user_id "
                + " LEFT JOIN sm_user as b ON s.payer_id = b.user_id WHERE 1=1";
        String queryCount = " SELECT count(spend_id) "
                + " FROM spend s "
                + " LEFT JOIN fund as f ON s.fund_id = f.fund_id "
                + " LEFT JOIN sm_user as a ON s.receiver_id = a.user_id "
                + " LEFT JOIN sm_user as b ON s.payer_id = b.user_id WHERE 1=1";
        String query = " ";

        if(payerName != null && !payerName.trim().isEmpty()) {
            query += " AND payer_name like ? ";
            lstParam.add("%" + payerName.trim() + "%");
            lstParamCount.add("%" + payerName.trim() + "%");
        }
        if(receiverName != null && !receiverName.trim().isEmpty()) {
            query += " AND receiver_name like ? ";
            lstParam.add("%" + receiverName.trim() + "%");
            lstParamCount.add("%" + receiverName.trim() + "%");
        }        
        if(fromDate != null) {
            query += " AND s.pay_date >= ? ";
            lstParam.add(fromDate);            
            lstParamCount.add(fromDate);            
        }
        if(toDate != null) {
            query += " AND s.pay_date <= ? ";
            lstParam.add(toDate);            
            lstParamCount.add(toDate);            
        }
        if(status != null && status != 0) {
            query += " AND s.status = ? ";
            lstParam.add(status);
            lstParamCount.add(status);
        }        
        queryCount += query;
        if("1".equals(sortAsc)) query += " ORDER BY s.pay_date ASC LIMIT ?,? ";
        else query += " ORDER BY s.pay_date DESC LIMIT ?,? ";     
        queryData += query;
        lstParam.add(numberRow);
        lstParam.add(pageLength);
        List<List> lstSpend = DatabaseConnector.getInstance().queryDataToList(queryData, lstParam);
        List<List> lstCount = DatabaseConnector.getInstance().queryDataToList(queryCount, lstParamCount);
        List<List> lstResult = new ArrayList();
        lstResult.add(lstCount);
        lstResult.add(lstSpend);
        return lstResult;
    }
    
    public void acceptSpend(List lstParam) throws SQLException {
        DatabaseConnector.getInstance().executeData("update spend set status = 2, fund_id = ? where spend_id = ?", lstParam);
    }

    public void deleteSpend(String lstParam) throws SQLException {
        DatabaseConnector.getInstance().executeData("update spend set status = 3 where spend_id in (" + lstParam + ")");
    }
    
    public List<Map> getAllSpend() throws SQLException {
        return DatabaseConnector.getInstance().queryData("select * from spend");
    }  
     
    public void insertCustomerPaySpend(List lstParam) throws SQLException {
        String sql = " insert into spend (receiver_id, payer_id, receiver_name, receiver_mobile, payer_name, "
                + " payer_mobile,pay_date, amount,spend_reason,fund_id,status,plan_id, payment_id, gift_code) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
        C3p0Connector.getInstance().executeData(sql, lstParam);
    }
    
      public Map getSpendById(Integer spendId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(spendId);
        List<Map> lstSpend = DatabaseConnector.getInstance().queryData("select * from spend where spend_id = ?", lstParam);
        if(lstSpend != null && !lstSpend.isEmpty()) return lstSpend.get(0);
        else return null;
    }
}
