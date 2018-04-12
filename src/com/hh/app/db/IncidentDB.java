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
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author agiletech
 */

public class IncidentDB {
    
    public void insertIncident(List lstParam) throws SQLException {
        String sql = "insert into incident(user_id, type_id, content, plan_id, create_date, status) values (?,?,?,?,?,1)";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }    
    
    public void resolveIncident(List lstParam) throws SQLException {
        String sql = "update incident set manager_id = ?, resolve_content = ?, status = ? where incident_id = ? ";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }
    
    public Map getIncidentById(Integer incidentId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(incidentId);
        List<Map> lstIncident = DatabaseConnector.getInstance().queryData("select * from incident where incident_id = ?", lstParam);
        if(lstIncident != null && !lstIncident.isEmpty()) return lstIncident.get(0);
        else return null;
    }
     
    public void deleteIncident(String lstIncidents) throws SQLException {
        DatabaseConnector.getInstance().executeData("delete from incident where incident_id in (" + lstIncidents + ")");
    }
    
    public List<List> searchIncident(Integer numberRow, Integer pageLength, String content, String userName, String managerName, Date fromDate, Date toDate, Integer status, String sortAsc) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData = " SELECT 1 as rownum, content, CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal\" onclick=\"loadViewUser(',usr1.user_id,');\">',usr1.name,'</a>') as user_name, "
                + " DATE_FORMAT(incident.create_date, '%d-%m %H:%i') as create_date ,  " +
                " CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal\" onclick=\"loadViewUser(',manager_id,');\">',usr2.name,'</a>') as manager_name, resolve_content, " +
                " CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal\" onclick=\"viewPlan(',pl.plan_id,');\">',pl.plan_code,'</a>') as plan_code, " +
                " CASE incident.status " +
                " WHEN 1 THEN 'Chưa xử lý' " +
                " WHEN 2 THEN 'Đã xử lý' " +
                " END AS status, " +
                " CONCAT('<input type=\"checkbox\" name=\"incidentId\" onclick=\"validateCheckAll()\" value=\"',incident_id,'\"/>') as incident_id " +
                " FROM incident left join sm_user usr1 on incident.user_id = usr1.user_id " +
                " left join sm_user usr2 on incident.manager_id = usr2.user_id " +
                " left join plan pl on incident.plan_id = pl.plan_id " +
                " WHERE 1 = 1 ";
        String queryCount = " SELECT count(incident_id) FROM incident left join sm_user usr1 on incident.user_id = usr1.user_id "
                + " left join sm_user usr2 on incident.manager_id = usr2.user_id  "
                + " left join plan pl on incident.plan_id = pl.plan_id "
                + " WHERE  1 = 1 ";
        String query = " ";
        if(StringUtils.isNotEmpty(content)){
           query += " AND content like ? ";
           lstParam.add("%" + content.trim() + "%");
           lstParamCount.add("%" + content.trim() + "%"); 
        }
        if(StringUtils.isNotEmpty(userName)){
           query += " AND usr1.name like ? ";
           lstParam.add("%" + userName.trim() + "%");
           lstParamCount.add("%" + userName.trim() + "%"); 
        }
        if(StringUtils.isNotEmpty(managerName)){
           query += " AND usr2.name like ? ";
           lstParam.add("%" + managerName.trim() + "%");
           lstParamCount.add("%" + managerName.trim() + "%"); 
        }
        if(fromDate != null) {
            query += " AND create_date >= ? ";
            lstParam.add(fromDate);            
            lstParamCount.add(fromDate);            
        }
        if(toDate != null) {
            query += " AND create_date <= ? ";
            lstParam.add(toDate);            
            lstParamCount.add(toDate);            
        }
        
        if(status != null){
           query += " AND incident.status = ? ";
           lstParam.add(status);
           lstParamCount.add(status); 
        }
        
        queryCount += query;        
        if("1".equals(sortAsc)) query += " ORDER BY incident.create_date ASC LIMIT ?,? ";
        else query += " ORDER BY incident.create_date DESC LIMIT ?,? "; 
        queryData += query;
        lstParam.add(numberRow);
        lstParam.add(pageLength);
        List<List> lstIncident = DatabaseConnector.getInstance().queryDataToList(queryData, lstParam);
        List<List> lstCount = DatabaseConnector.getInstance().queryDataToList(queryCount, lstParamCount);
        List<List> lstResult = new ArrayList();
        lstResult.add(lstCount);
        lstResult.add(lstIncident);
        return lstResult;
    }
}
