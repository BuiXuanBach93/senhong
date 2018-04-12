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
 * @author hiendm1
 */
public class MaidDB {
    public void insertWorkTime(List lstParam) throws SQLException {
        String sql = "insert into work_time(maid_id, start_date, end_date, mon_start, mon_end, tue_start, tue_end, wed_start, wed_end, thu_start, thu_end, fri_start, fri_end, sat_start, sat_end, sun_start, sun_end, create_date) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }    
    
    public void insertRestTime(List lstParam) throws SQLException {
        String sql = "insert into rest_time(maid_id, start_date, end_date, mon_start, mon_end, tue_start, tue_end, wed_start, wed_end, thu_start, thu_end, fri_start, fri_end, sat_start, sat_end, sun_start, sun_end, create_date) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }
    
    public void deleteWorkTime(List lstParam) throws SQLException {
        String sql = "delete from work_time where maid_id = ?";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }    

    public void deleteRestTime(List lstParam) throws SQLException {
        String sql = "delete from rest_time where maid_id = ?";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }    

    public List<Map> getAllWorkTime() throws SQLException {
        return DatabaseConnector.getInstance().queryData("select a.*, b.name as maid_name from work_time a, sm_user b where a.maid_id = b.user_id");
    }
    
    public List<Map> getAllRestTime() throws SQLException {
        return DatabaseConnector.getInstance().queryData("select a.*, b.name as maid_name from rest_time a, sm_user b where a.maid_id = b.user_id");
    }    
    
    public List<Map> getAllPlan() throws SQLException {
        return DatabaseConnector.getInstance().queryData(getSqlPlan());
    }
    
    public List<Map> getAllPlanByOrderId(Integer orderId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(orderId);
        return DatabaseConnector.getInstance().queryData(getSqlPlan() + " where a.order_id = ? ", lstParam);
    }    
    
    public List<Map> getPlanByOrderId(Integer orderId) throws SQLException {
        List<Map> lstResult = new ArrayList();
        List lstParam = new ArrayList();
        lstParam.add(orderId);
        
        lstResult = DatabaseConnector.getInstance().queryData(getSqlPlan() + " WHERE a.order_id = ?", lstParam);
        return lstResult;
    }  
    
    public List<Map> getPlanByPlanId(Integer planId) throws SQLException {
        List<Map> lstResult = new ArrayList();
        List lstParam = new ArrayList();
        lstParam.add(planId);
        
        lstResult = DatabaseConnector.getInstance().queryData(getSqlPlan() + " WHERE a.plan_id = ?", lstParam);
        return lstResult;
    }
    
    private String getSqlPlan() {
        return " SELECT a.plan_id, a.plan_code, a.customer_id, b.name as customer_name, b.picture as customer_picture, b.mobile as customer_mobile, " +
        " a.maid_id, c.name as maid_name, c.picture as maid_picture, c.mobile as maid_mobile, e.address_id, e.address, e.province, e.district, e.road, e.detail, e.longitude, e.latitude,  " +
        " a.price, a.salary, a.contact_name, a.contact_mobile, d.cooking, d.cleaning, d.baby_care, d.old_care, d.content,  " +
        " DATE_FORMAT(a.start_date, '%d-%m-%Y %H:%i:%s') as start_date, a.start_date as start, a.end_date as end, DATE_FORMAT(a.end_date, '%d-%m-%Y %H:%i:%s') as end_date , a.order_id, a.status,a.salary,a.rating_work,a.rating_customer,a.maid_comment, " +
        " DATE_FORMAT(a.real_start, '%d-%m-%Y %H:%i:%s') as real_start, DATE_FORMAT(a.real_end, '%d-%m-%Y %H:%i:%s') as real_end, a.time_comment, a.salary, a.customer_comment, a.customer_rating, a.real_lat, a.real_long, a.distance  " +
        " FROM plan a " +
        " left join sm_user b on a.customer_id = b.user_id " +
        " left join sm_user c on a.maid_id = c.user_id " +
        " left join order_service d on a.order_id = d.order_id " +
        " left join address e on e.address_id = d.address_id";
    }
    
    public List<Map> getAllMaid() throws SQLException {
        return DatabaseConnector.getInstance().queryData("select * from sm_user where is_enable = 1 and user_type = 3");
    }
    
}
