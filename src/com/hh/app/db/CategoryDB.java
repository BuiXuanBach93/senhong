/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.db;

import com.hh.database.DatabaseConnector;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author agiletech
 */

public class CategoryDB {
    public void insertCategory(List lstParam) throws SQLException {
        String sql = "insert into category(time_length, category_name) values (?,?)";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }
    
    public void updateCategory(List lstParam) throws SQLException {
        String sql = "update category set time_length = ?, category_name = ? where category_id = ?";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }    
    
    public List<List> searchCategory(Integer numberRow, Integer pageLength, Integer timeLength, String name) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData = " SELECT 1 as rownum, category_name, time_length, " +
                " CONCAT('<input type=\"checkbox\" name=\"categoryId\" onclick=\"validateCheckAll()\" value=\"',category_id,'\"/>') as category_id " +
                " FROM category WHERE 1 = 1 ";
        String queryCount = " SELECT count(category_id) FROM category WHERE 1 = 1 ";
        String query = " ";
        
        if(timeLength != null && timeLength != 0) {
            query += " AND time_length = ? ";
            lstParam.add(timeLength);
            lstParamCount.add(timeLength);
        }
        
        if(name != null && !name.trim().isEmpty()) {
            query += " AND category_name = ? ";
            lstParam.add("%" + name.trim() + "%");
            lstParamCount.add("%" + name.trim() + "%");
        }

        queryCount += query;
        query += " ORDER BY category_id DESC LIMIT ?,? ";
        queryData += query;
        lstParam.add(numberRow);
        lstParam.add(pageLength);
        List<List> lstCategory = DatabaseConnector.getInstance().queryDataToList(queryData, lstParam);
        List<List> lstCount = DatabaseConnector.getInstance().queryDataToList(queryCount, lstParamCount);
        List<List> lstResult = new ArrayList();
        lstResult.add(lstCount);
        lstResult.add(lstCategory);
        return lstResult;
    }
    
    public void deleteCategory(String lstCategory) throws SQLException {
        DatabaseConnector.getInstance().executeData("delete from category where category_id in (" + lstCategory + ")");
    }
    
    public Map getCategoryById(Integer categoryId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(categoryId);
        List<Map> lstCategory = DatabaseConnector.getInstance().queryData("select * from category where category_id = ?", lstParam);
        if(lstCategory != null && !lstCategory.isEmpty()) return lstCategory.get(0);
        else return null;
    }
    
    public List<Map> getAllCategory() throws SQLException {
        return DatabaseConnector.getInstance().queryData("select * from category");
    }    
}
