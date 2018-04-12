/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.db;

import com.hh.database.DatabaseConnector;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author agiletech
 */

public class PolicyDB {
    public void insertPolicy(List lstParam) throws SQLException {
        String sql = "insert into price_policy(policy_code, content, time_from, time_to, from_date, to_date, area_id, status, add_rate, add_vnd, create_date) values (?,?,?,?,?,?,?,?,?,?,?)";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }
    
    public void updatePolicy(List lstParam) throws SQLException {
        String sql = "update price_policy set policy_code = ?, content = ?, time_from = ?, time_to = ?, from_date = ?, to_date = ?, area_id = ?, status = ?, add_rate = ?, add_vnd = ? where policy_id = ?";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }    
    
    public List<List> searchPolicy(Integer numberRow, Integer pageLength, String policyCode, String content, Integer status, Date fromDate, Date toDate) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData = " SELECT 1 as rownum, CONCAT('<a href=\"javascript:void(0)\" onclick=\"viewPolicy(',policy_id,');\">',policy_code,'</a>') as policy_code, " +
                " content, " +
                " DATE_FORMAT(from_date, '%d-%m-%Y') as from_date, " + 
                " DATE_FORMAT(to_date, '%d-%m-%Y') as to_date, " + 
                " CASE status " +
                " WHEN 1 THEN 'Chưa áp dụng' " +
                " WHEN 2 THEN 'Đang áp dụng' " +
                " WHEN 3 THEN 'Hết hiệu lực' " +
                " END AS status, " +
                " CONCAT('<input type=\"checkbox\" name=\"policyId\" onclick=\"validateCheckAll()\" value=\"',policy_id,'\"/>') as policy_id " +
                " FROM price_policy WHERE 1 = 1 ";
        String queryCount = " SELECT count(policy_id) FROM price_policy WHERE 1 = 1 ";
        String query = " ";
        if(policyCode != null && !policyCode.trim().isEmpty()) {
            query += " AND policy_code like ? ";
            lstParam.add("%" + policyCode.trim() + "%");
            lstParamCount.add("%" + policyCode.trim() + "%");
        }
        if(content != null && !content.trim().isEmpty()) {
            query += " AND content like ? ";
            lstParam.add("%" + content.trim() + "%");
            lstParamCount.add("%" + content.trim() + "%");
        }     
        if(status != null && status != 0) {
            query += " AND status = ? ";
            lstParam.add(status);
            lstParamCount.add(status);
        }

        if(fromDate != null) {
            query += " AND to_date >= ? ";
            lstParam.add(fromDate);            
            lstParamCount.add(fromDate);            
        }
        if(toDate != null) {
            query += " AND from_date <= ? ";
            lstParam.add(toDate);            
            lstParamCount.add(toDate);            
        }

        queryCount += query;
        query += " ORDER BY from_date DESC LIMIT ?,? ";
        queryData += query;
        lstParam.add(numberRow);
        lstParam.add(pageLength);
        List<List> lstPolicy = DatabaseConnector.getInstance().queryDataToList(queryData, lstParam);
        List<List> lstCount = DatabaseConnector.getInstance().queryDataToList(queryCount, lstParamCount);
        List<List> lstResult = new ArrayList();
        lstResult.add(lstCount);
        lstResult.add(lstPolicy);
        return lstResult;
    }
    
    public void deletePolicy(String lstPolicy) throws SQLException {
        DatabaseConnector.getInstance().executeData("delete from price_policy where policy_id in (" + lstPolicy + ")");
    }
    
    public Map getPolicyById(Integer policyId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(policyId);
        List<Map> lstPolicy = DatabaseConnector.getInstance().queryData("select policy_id, policy_code, content, time_from, time_to, DATE_FORMAT(from_date, '%d-%m-%Y') as from_date, DATE_FORMAT(to_date, '%d-%m-%Y') as to_date, status, area_id, add_rate, add_vnd, DATE_FORMAT(create_date, '%d-%m-%Y') as create_date from price_policy where policy_id = ?", lstParam);
        if(lstPolicy != null && !lstPolicy.isEmpty()) return lstPolicy.get(0);
        else return null;
    }
    
    public List<Map> getArea() throws SQLException {
        return DatabaseConnector.getInstance().queryData("select * from bm_area");
    }
    
    public List<Map> getAllPolicy() throws SQLException {
        return DatabaseConnector.getInstance().queryData("select * from price_policy where status = 2 order by policy_id desc");
    }
}
