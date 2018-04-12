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
public class MistakeDB {
    public List<Map> getAllMistake() throws SQLException {
        return DatabaseConnector.getInstance().queryData("select * from mistake");
    }    
    
    public Integer insertMistake(List lstParam) throws SQLException {
        String sql = " insert into mistake(name,description,peralty_amount) values (?,?,?) ";
        return C3p0Connector.getInstance().insertData(sql, lstParam);
    }  
    
    public void updateMistake(List lstParam) throws SQLException {
        String sql = "update mistake set name = ?, peralty_amount = ?, description = ? ";
        sql += " where mistake_id = ?";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    } 
    
     public Map getMistakeById(Integer mistakeId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(mistakeId);
        List<Map> lstArea = DatabaseConnector.getInstance().queryData("select * from mistake where mistake_id = ?", lstParam);
        if(lstArea != null && !lstArea.isEmpty()) return lstArea.get(0);
        else return null;
    }
     
    public void deleteMistake(String lstMistakes) throws SQLException {
        DatabaseConnector.getInstance().executeData("delete from mistake where mistake_id in (" + lstMistakes + ")");
    }
    
    public List<List> searchMistake(Integer numberRow, Integer pageLength, String name) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData = " SELECT 1 as rownum, CONCAT('<a href=\"javascript:void(0)\" onclick=\"loadViewMistake(',mistake_id,');\">',name,'</a>') as name,description, peralty_amount, " +
                " CONCAT('<input type=\"checkbox\" name=\"mistakeId\" onclick=\"validateCheckAll()\" value=\"',mistake_id,'\"/>') as mistake_id " +
                " FROM mistake WHERE 1 = 1 ";
        String queryCount = " SELECT count(mistake_id) FROM mistake WHERE 1 = 1 ";
        String query = " ";
        if(StringUtils.isNotEmpty(name)){
           query += " AND name like ? ";
           lstParam.add("%" + name.trim() + "%");
           lstParamCount.add("%" + name.trim() + "%"); 
        }
        queryCount += query;
        query += " ORDER BY mistake_id DESC LIMIT ?,? ";
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
