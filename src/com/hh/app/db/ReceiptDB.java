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
public class ReceiptDB {
    public void insertReceipt(List lstParam) throws SQLException {
        String sql = " insert into receipt (receiver_id, payer_id, receiver_name, receiver_mobile, payer_name, "
                + " payer_mobile,pay_date, amount,receive_reason,fund_id,status) values (?,?,?,?,?,?,?,?,?,?,?) ";
        C3p0Connector.getInstance().executeData(sql, lstParam);
    }
    
    public void insertReceiptFromOrder(List lstParam) throws SQLException {
        DatabaseConnector.getInstance().executeData("INSERT INTO receipt(payer_id, amount, plan_id, order_id, receiver_id, pay_date, receive_reason, STATUS) SELECT customer_id, price, plan_id, order_id, 1, start_date, 'Khách trả tiền dịch vụ', 1 FROM plan WHERE order_id = ?", lstParam);
    }
     
    public List<List> searchReceipt(Integer numberRow, Integer pageLength, String payerName, String receiverName, Date fromDate, Date toDate, Integer status, String sortAsc) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData = " SELECT 1 as rownum, CONCAT('PT', s.receipt_id) as receipt_id, receive_reason, "
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
                + " CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal\" onclick=\"viewPlan(',c.plan_id,');\">',c.plan_code,'</a>') as plan_code, "
                + " CASE s.status " 
                + " WHEN 1 THEN 'Chưa xác nhận' " 
                + " WHEN 2 THEN 'Đã xác nhận' " 
                + " WHEN 3 THEN 'Đã hủy' " 
                + " END AS status, " 
                + " CONCAT('<input type=\"checkbox\" name=\"receiptId\" onclick=\"validateCheckAll()\" value=\"',receipt_id,'\"/>') as receipt_id " 
                + " FROM receipt s "
                + " LEFT JOIN fund as f ON s.fund_id = f.fund_id "
                + " LEFT JOIN plan as c ON s.plan_id = c.plan_id "
                + " LEFT JOIN sm_user as a ON s.receiver_id = a.user_id "
                + " LEFT JOIN sm_user as b ON s.payer_id = b.user_id WHERE 1=1";
        String queryCount = " SELECT count(receipt_id) "
                + " FROM receipt s "
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
        List<List> lstReceipt = DatabaseConnector.getInstance().queryDataToList(queryData, lstParam);
        List<List> lstCount = DatabaseConnector.getInstance().queryDataToList(queryCount, lstParamCount);
        List<List> lstResult = new ArrayList();
        lstResult.add(lstCount);
        lstResult.add(lstReceipt);
        return lstResult;
    }
    
    public void acceptReceipt(List lstParam) throws SQLException {
        DatabaseConnector.getInstance().executeData("update receipt set status = 2, fund_id = ?, pay_date = now() where receipt_id = ?", lstParam);
    }
    
    public void deleteReceipt(String lstParam) throws SQLException {
        DatabaseConnector.getInstance().executeData("update receipt set status = 3 where receipt_id in (" + lstParam + ")");
    }    
    
     public List<Map> getAllReceipt() throws SQLException {
        return DatabaseConnector.getInstance().queryData("select * from receipt");
    }  
     
    public void insertCustomerPayReceipt(List lstParam) throws SQLException {
        String sql = " insert into receipt (receiver_id, payer_id, receiver_name, receiver_mobile, payer_name, "
                + " payer_mobile,pay_date, amount,receive_reason,fund_id,status,plan_id, payment_id, gift_code) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
        C3p0Connector.getInstance().executeData(sql, lstParam);
    }
    
    public Map getReceiptById(Integer receiptId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(receiptId);
        List<Map> lstReceipt = DatabaseConnector.getInstance().queryData("select * from receipt where receipt_id = ?", lstParam);
        if(lstReceipt != null && !lstReceipt.isEmpty()) return lstReceipt.get(0);
        else return null;
    }
}
