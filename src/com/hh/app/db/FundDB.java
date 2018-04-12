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
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author buixu
 */
public class FundDB {
    public List<Map> getAllFund() throws SQLException {
        return DatabaseConnector.getInstance().queryData("select * from fund");
    }    
    
    public Integer insertFund(List lstParam) throws SQLException {
        String sql = " insert into fund(fund_name,amount,description) values (?,?,?) ";
        return C3p0Connector.getInstance().insertData(sql, lstParam);
    }  
    
    public void payFund(int fundId, int amount) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(amount);
        lstParam.add(fundId);
        DatabaseConnector.getInstance().executeData("update fund set amount = amount - ? where fund_id = ? ", lstParam);
    }
    
    public void receiveFund(int fundId, int amount) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(amount);
        lstParam.add(fundId);
        DatabaseConnector.getInstance().executeData("update fund set amount = amount + ? where fund_id = ? ", lstParam);
    }
        
    public void updateFund(List lstParam) throws SQLException {
        String sql = "update fund set fund_name = ?, amount = ?, description = ? ";
        sql += " where fund_id = ?";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }
    
    public Map getFundById(Integer fundId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(fundId);
        List<Map> lstFund = DatabaseConnector.getInstance().queryData("select * from fund where fund_id = ?", lstParam);
        if(lstFund != null && !lstFund.isEmpty()) return lstFund.get(0);
        else return null;
    }
     
    public void deleteFund(String lstFunds) throws SQLException {
        DatabaseConnector.getInstance().executeData("delete from fund where fund_id in (" + lstFunds + ")");
    }
    
    public List<List> searchFund(Integer numberRow, Integer pageLength, String fundName) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData = " SELECT 1 as rownum, CONCAT('<a href=\"javascript:void(0)\" onclick=\"loadViewFund(',fund_id,');\">',fund_name,'</a>') as fund_name, amount, description, " +
                " CONCAT('<input type=\"checkbox\" name=\"fundId\" onclick=\"validateCheckAll()\" value=\"',fund_id,'\"/>') as fund_id " +
                " FROM fund WHERE 1 = 1 ";
        String queryCount = " SELECT count(fund_id) FROM fund WHERE 1 = 1 ";
        String query = " ";
        if(StringUtils.isNotEmpty(fundName)){
           query += " AND fund_name like ? ";
           lstParam.add("%" + fundName.trim() + "%");
           lstParamCount.add("%" + fundName.trim() + "%"); 
        }
        queryCount += query;
        query += " ORDER BY fund_id DESC LIMIT ?,? ";
        queryData += query;
        lstParam.add(numberRow);
        lstParam.add(pageLength);
        List<List> lstFund = DatabaseConnector.getInstance().queryDataToList(queryData, lstParam);
        List<List> lstCount = DatabaseConnector.getInstance().queryDataToList(queryCount, lstParamCount);
        List<List> lstResult = new ArrayList();
        lstResult.add(lstCount);
        lstResult.add(lstFund);
        return lstResult;
    }
}
