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
public class BmAreaDB {
    
    public List<Map> getAllArea() throws SQLException {
        return DatabaseConnector.getInstance().queryData("select * from bm_area");
    }    
    
    public Integer insertArea(List lstParam) throws SQLException {
        String sql = " insert into bm_area(area_name) values (?) ";
        return C3p0Connector.getInstance().insertData(sql, lstParam);
    }  
    
     public void updateArea(List lstParam) throws SQLException {
        String sql = "update bm_area set area_name = ? ";
        sql += " where area_id = ?";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    } 
    
     public Map getAreaById(Integer areaId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(areaId);
        List<Map> lstArea = DatabaseConnector.getInstance().queryData("select * from bm_area where area_id = ?", lstParam);
        if(lstArea != null && !lstArea.isEmpty()) return lstArea.get(0);
        else return null;
    }
     
    public void deleteArea(String lstAreas) throws SQLException {
        DatabaseConnector.getInstance().executeData("delete from bm_area where area_id in (" + lstAreas + ")");
    }
    
    public List<List> searchArea(Integer numberRow, Integer pageLength, String areaName) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData = " SELECT 1 as rownum, CONCAT('<a href=\"javascript:void(0)\" onclick=\"loadViewArea(',area_id,');\">',area_name,'</a>') as area_name, " +
                " CONCAT('<input type=\"checkbox\" name=\"areaId\" onclick=\"validateCheckAll()\" value=\"',area_id,'\"/>') as area_id " +
                " FROM bm_area WHERE 1 = 1 ";
        String queryCount = " SELECT count(area_id) FROM bm_area WHERE 1 = 1 ";
        String query = " ";
        if(StringUtils.isNotEmpty(areaName)){
           query += " AND area_name like ? ";
           lstParam.add("%" + areaName.trim() + "%");
           lstParamCount.add("%" + areaName.trim() + "%"); 
        }
        queryCount += query;
        query += " ORDER BY area_id DESC LIMIT ?,? ";
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
