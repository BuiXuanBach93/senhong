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

public class PromotionDB {
    public void insertPromotion(List lstParam) throws SQLException {
        String sql = "insert into promotion(promotion_code, content, time_from, time_to, from_date, to_date, area_id, status, create_date) values (?,?,?,?,?,?,?,?,?)";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }
    
    public void updatePromotion(List lstParam) throws SQLException {
        String sql = "update promotion set promotion_code = ?, content = ?, time_from = ?, time_to = ?, from_date = ?, to_date = ?, area_id = ?, status = ? where promotion_id = ?";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }    
    
    public List<List> searchPromotion(Integer numberRow, Integer pageLength, String promotionCode, String content, Integer status, Date fromDate, Date toDate) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData = " SELECT 1 as rownum, CONCAT('<a href=\"javascript:void(0)\" onclick=\"viewPromotion(',promotion_id,');\">',promotion_code,'</a>') as promotion_code, " +
                " content, " +
                " DATE_FORMAT(from_date, '%d-%m-%Y') as from_date, " + 
                " DATE_FORMAT(to_date, '%d-%m-%Y') as to_date, " + 
                " CASE status " +
                " WHEN 1 THEN 'Chưa áp dụng' " +
                " WHEN 2 THEN 'Đang áp dụng' " +
                " WHEN 3 THEN 'Hết hiệu lực' " +
                " END AS status, " +
                " CONCAT('<input type=\"checkbox\" name=\"promotionId\" onclick=\"validateCheckAll()\" value=\"',promotion_id,'\"/>') as promotion_id " +
                " FROM promotion WHERE 1 = 1 ";
        String queryCount = " SELECT count(promotion_id) FROM promotion WHERE 1 = 1 ";
        String query = " ";
        if(promotionCode != null && !promotionCode.trim().isEmpty()) {
            query += " AND promotion_code like ? ";
            lstParam.add("%" + promotionCode.trim() + "%");
            lstParamCount.add("%" + promotionCode.trim() + "%");
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
        List<List> lstPromotion = DatabaseConnector.getInstance().queryDataToList(queryData, lstParam);
        List<List> lstCount = DatabaseConnector.getInstance().queryDataToList(queryCount, lstParamCount);
        List<List> lstResult = new ArrayList();
        lstResult.add(lstCount);
        lstResult.add(lstPromotion);
        return lstResult;
    }
    
    public void deletePromotion(String lstPromotion) throws SQLException {
        DatabaseConnector.getInstance().executeData("delete from promotion where promotion_id in (" + lstPromotion + ")");
    }
    
    public Map getPromotionById(Integer promotionId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(promotionId);
        List<Map> lstPromotion = DatabaseConnector.getInstance().queryData("select promotion_id, promotion_code, content, time_from, time_to, DATE_FORMAT(from_date, '%d-%m-%Y') as from_date, DATE_FORMAT(to_date, '%d-%m-%Y') as to_date, status, area_id, DATE_FORMAT(create_date, '%d-%m-%Y') as create_date from promotion where promotion_id = ?", lstParam);
        if(lstPromotion != null && !lstPromotion.isEmpty()) return lstPromotion.get(0);
        else return null;
    }
    
    public List<Map> getArea() throws SQLException {
        return DatabaseConnector.getInstance().queryData("select * from bm_area");
    }
}
