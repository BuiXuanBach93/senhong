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
public class FundHistoryDB {
    public List<Map> getAllFundHistory() throws SQLException {
        return DatabaseConnector.getInstance().queryData("select * from fund_history");
    }    
    
    public Integer insertFundHistory(List lstParam) throws SQLException {
        String sql = " insert into fund_history(fund_id,receipt_id,spend_id, reason, log_date, type, amount, fund) values (?,?,?,?,?,?,?,?) ";
        return C3p0Connector.getInstance().insertData(sql, lstParam);
    }
    
    public Map getFundHistoryById(Integer fundHistoryId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(fundHistoryId);
        List<Map> lstFundHistory = DatabaseConnector.getInstance().queryData("select * from fund_history where id = ?", lstParam);
        if(lstFundHistory != null && !lstFundHistory.isEmpty()) return lstFundHistory.get(0);
        else return null;
    }
     
    public void deleteFundHistory(String lstFundHistorys) throws SQLException {
        DatabaseConnector.getInstance().executeData("delete from fund_history where id in (" + lstFundHistorys + ")");
    }
    
    public void addReceiptRecord(int receiptId, int fund) throws SQLException{
        ReceiptDB receiptDb = new ReceiptDB();
        Map map = receiptDb.getReceiptById(receiptId);
        if(map != null){
            List params = new ArrayList();
            int fundId = Integer.parseInt(map.get("fund_id").toString());
            Integer spendId = null;
            String reason = map.get("receive_reason").toString();
            Date current = new Date();
            int type = 1;
            int amount = Integer.parseInt(map.get("amount").toString());
            params.add(fundId);
            params.add(receiptId);
            params.add(spendId);
            params.add(reason);
            params.add(current);
            params.add(type);
            params.add(amount);
            params.add(fund);
            insertFundHistory(params);
        }
    }
    
    public void addSpendRecord(int spendId, int fund) throws SQLException{
        SpendDB spendDb = new SpendDB();
        Map map = spendDb.getSpendById(spendId);
        if(map != null){
            List params = new ArrayList();
            int fundId = Integer.parseInt(map.get("fund_id").toString());
            Integer receiptId = null;
            String reason = map.get("spend_reason").toString();
            Date current = new Date();
            int type = 2;
            int amount = Integer.parseInt(map.get("amount").toString());
            params.add(fundId);
            params.add(receiptId);
            params.add(spendId);
            params.add(reason);
            params.add(current);
            params.add(type);
            params.add(amount);
            params.add(fund);
            insertFundHistory(params);
        }
    }
    
    public List<List> searchFundHistory(Integer numberRow, Integer pageLength, String fundName, String receiptNo, String spendNo, Date fromDate, Date toDate, Integer type, String sortAsc) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData = " SELECT 1 as rownum, fund.fund_name, "
                + " DATE_FORMAT(log_date, '%d-%m-%Y') as log_date ,  " +
                " CONCAT('PT', receipt_id), " +
                " CONCAT('PC', spend_id), " +
                " fh.amount, fh.fund, reason " +
                " FROM fund_history fh left join fund on fh.fund_id = fund.fund_id "
                + " WHERE 1 = 1 ";
        String queryCount = " SELECT count(id) FROM fund_history fh left join fund on fh.fund_id = fund.fund_id "
                + " WHERE  1 = 1 ";
        String query = " ";
        if(StringUtils.isNotEmpty(fundName)){
           query += " AND fund.fund_name like ? ";
           lstParam.add("%" + fundName.trim() + "%");
           lstParamCount.add("%" + fundName.trim() + "%"); 
        }
        if(StringUtils.isNotEmpty(receiptNo)){
           query += " AND CONCAT('PT', receipt_id) like ? ";
           lstParam.add("%" + receiptNo.trim() + "%");
           lstParamCount.add("%" + receiptNo.trim() + "%"); 
        }
        if(StringUtils.isNotEmpty(spendNo)){
           query += " AND CONCAT('PC', spend_id) like ? ";
           lstParam.add("%" + spendNo.trim() + "%");
           lstParamCount.add("%" + spendNo.trim() + "%"); 
        }
        if(fromDate != null) {
            query += " AND log_date >= ? ";
            lstParam.add(fromDate);            
            lstParamCount.add(fromDate);            
        }
        if(toDate != null) {
            query += " AND log_date <= ? ";
            lstParam.add(toDate);            
            lstParamCount.add(toDate);            
        }
        
        if(type != null) {
            query += " AND fh.type = ? ";
            lstParam.add(type);            
            lstParamCount.add(type);            
        }
        
        queryCount += query;
        if("1".equals(sortAsc)) query += " ORDER BY fh.log_date ASC LIMIT ?,? ";
        else query += " ORDER BY fh.log_date DESC LIMIT ?,? ";  
        queryData += query;
        lstParam.add(numberRow);
        lstParam.add(pageLength);
        List<List> lstFundHistory = DatabaseConnector.getInstance().queryDataToList(queryData, lstParam);
        List<List> lstCount = DatabaseConnector.getInstance().queryDataToList(queryCount, lstParamCount);
        List<List> lstResult = new ArrayList();
        lstResult.add(lstCount);
        lstResult.add(lstFundHistory);
        return lstResult;
    }
}
