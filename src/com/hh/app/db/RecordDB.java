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
public class RecordDB {
    public List<Map> getAllRecord() throws SQLException {
        return DatabaseConnector.getInstance().queryData("select * from maid_record");
    }    
    
    public Integer insertRecord(List lstParam) throws SQLException {
        String sql = " insert into maid_record(name,description,reward_amount) values (?,?,?) ";
        return C3p0Connector.getInstance().insertData(sql, lstParam);
    }  
    
    public void updateRecord(List lstParam) throws SQLException {
        String sql = "update maid_record set name = ?, reward_amount = ?, description = ? ";
        sql += " where record_id = ?";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    } 
    
     public Map getRecordById(Integer recordId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(recordId);
        List<Map> lstArea = DatabaseConnector.getInstance().queryData("select * from maid_record where record_id = ?", lstParam);
        if(lstArea != null && !lstArea.isEmpty()) return lstArea.get(0);
        else return null;
    }
     
    public void deleteRecord(String lstRecords) throws SQLException {
        DatabaseConnector.getInstance().executeData("delete from maid_record where record_id in (" + lstRecords + ")");
    }
    
    public List<List> searchRecord(Integer numberRow, Integer pageLength, String name) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData = " SELECT 1 as rownum,CONCAT('<a href=\"javascript:void(0)\" onclick=\"loadViewRecord(',record_id,');\">',name,'</a>') as name,description,reward_amount, " +
                " CONCAT('<input type=\"checkbox\" name=\"recordId\" onclick=\"validateCheckAll()\" value=\"',record_id,'\"/>') as record_id " +
                " FROM maid_record WHERE 1 = 1 ";
        String queryCount = " SELECT count(record_id) FROM maid_record WHERE 1 = 1 ";
        String query = " ";
        if(StringUtils.isNotEmpty(name)){
           query += " AND name like ? ";
           lstParam.add("%" + name.trim() + "%");
           lstParamCount.add("%" + name.trim() + "%"); 
        }
        queryCount += query;
        query += " ORDER BY record_id DESC LIMIT ?,? ";
        queryData += query;
        lstParam.add(numberRow);
        lstParam.add(pageLength);
        List<List> lstArea = DatabaseConnector.getInstance().queryDataToList(queryData, lstParam);
        List<List> lstCount = DatabaseConnector.getInstance().queryDataToList(queryCount, lstParamCount);
        List<List> lstResult = new ArrayList();
        lstResult.add(lstCount);
        lstResult.add(lstArea);
        return lstResult;
    }
}
