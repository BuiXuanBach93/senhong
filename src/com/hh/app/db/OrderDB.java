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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author agiletech
 */

public class OrderDB {    
    public List<List> searchOrder(Integer numberRow, Integer pageLength, String orderCode, String customer, String mobile, Integer status, Date fromDate, Date toDate, Integer userId, String sortType) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData = "SELECT 1 as rownum, CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal\" onclick=\"viewOrder(',a.order_id,');\">',a.order_code,'</a>') as order_code, " +
                " CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal\" onclick=\"loadViewUser(',b.user_id,');\">',b.name,'</a>') as customer, b.mobile, " +
                " start_date, " + 
                " start_length, " + 
                " DATE_FORMAT(a.create_date, '%d-%m-%Y') as create_date, " + 
                " CASE a.status " +
                " WHEN 1 THEN 'Chưa thực hiện' " +
                " WHEN 4 THEN 'Đang thực hiện' " +
                " WHEN 5 THEN 'Hoàn thành' " +
                " WHEN 6 THEN 'Đóng' " +
                " END AS status, " +
                " CASE a.source " +
                " WHEN NULL THEN 'App' " +
                " WHEN 0 THEN 'App' " +
                " WHEN 1 THEN 'Web' " +
                " END AS source, " +
                " is_periodical " +
                " FROM order_service a, sm_user b WHERE a.customer_id = b.user_id ";
        String queryCount = "SELECT count(a.order_id) FROM order_service a, sm_user b WHERE a.customer_id = b.user_id ";
        String query = " ";
        if(orderCode != null && !orderCode.trim().isEmpty()) {
            query += " AND a.order_code like ? ";
            lstParam.add("%" + orderCode.trim() + "%");
            lstParamCount.add("%" + orderCode.trim() + "%");
        }
        if(customer != null && !customer.trim().isEmpty()) {
            query += " AND b.name like ? ";
            lstParam.add("%" + customer.trim() + "%");
            lstParamCount.add("%" + customer.trim() + "%");
        }
        if(mobile != null && !mobile.trim().isEmpty()) {
            query += " AND b.mobile like ? ";
            lstParam.add("%" + mobile.trim() + "%");
            lstParamCount.add("%" + mobile.trim() + "%");
        }
        if(status != null && status != 0) {
            query += " AND a.status = ? ";
            lstParam.add(status);
            lstParamCount.add(status);
        }
        if(userId != null && userId != 0) {
            query += " AND a.customer_id = ? ";
            lstParam.add(userId);
            lstParamCount.add(userId);
        }        
        if(fromDate != null) {
            query += " AND a.create_date >= ? ";
            lstParam.add(fromDate);            
            lstParamCount.add(fromDate);            
        }
        if(toDate != null) {
            query += " AND a.create_date <= ? ";
            lstParam.add(toDate);            
            lstParamCount.add(toDate);            
        }
        queryCount += query;
        if("1".equals(sortType))
            query += " ORDER BY STR_TO_DATE(a.start_date, '%d-%m-%Y %H:%i') ASC LIMIT ?,? ";
        else if("2".equals(sortType))
            query += " ORDER BY a.create_date DESC LIMIT ?,? ";
        else if("3".equals(sortType))
            query += " ORDER BY a.create_date ASC LIMIT ?,? ";
        else
            query += " ORDER BY STR_TO_DATE(a.start_date, '%d-%m-%Y %H:%i') DESC LIMIT ?,? ";
        queryData += query;
        lstParam.add(numberRow);
        lstParam.add(pageLength);
        List<List> lstOrder = DatabaseConnector.getInstance().queryDataToList(queryData, lstParam);
        List<List> lstCount = DatabaseConnector.getInstance().queryDataToList(queryCount, lstParamCount);
        List<List> lstResult = new ArrayList();
        lstResult.add(lstCount);
        lstResult.add(lstOrder);
        return lstResult;
    }
    
    public Map getOrderById(Integer orderId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(orderId);
        List<Map> lstUser = DatabaseConnector.getInstance().queryData(
                "select a.order_code, b.name as customer_name, a.price, c.detail, c.latitude, c.longitude, "
                + " b.picture as customer_picture, "
                + " a.start_date, "
                + " a.start_length, "
                + " a.mon_start, a.mon_end, a.tue_start, a.tue_end, a.wed_start, a.wed_end, "
                + " a.thu_start, a.thu_end, a.fri_start, a.fri_end, a.sat_start, a.sat_end, "
                + " a.sun_start, a.sun_end, a.cooking, a.baby_care, a.old_care, a.cleaning, "
                + " a.is_periodical, a.order_code, a.content, "
                + " CASE a.status "
                + " WHEN 1 THEN 'Chưa thực hiện' "
                + " WHEN 2 THEN 'Nhận việc' "
                + " WHEN 3 THEN 'Thay người' "
                + " WHEN 4 THEN 'Đang thực hiện' "
                + " WHEN 5 THEN 'Hoàn thành' "
                + " WHEN 6 THEN 'Đóng' "
                + " END AS status, "
                + " DATE_FORMAT(a.create_date, '%d-%m-%Y') as create_date, a.order_id, a.order_file "
                + " from order_service a, sm_user b, address c where a.customer_id = b.user_id and a.address_id = c.address_id and a.order_id = ?", lstParam);
            if(lstUser != null && !lstUser.isEmpty()) return lstUser.get(0);
        else return null;
    }    
    
    public Map getOrderInforById(Integer orderId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(orderId);
        List<Map> lstOrder = DatabaseConnector.getInstance().queryData(
                "select * from order_service where order_id = ?", lstParam);
            if(lstOrder != null && !lstOrder.isEmpty()) return lstOrder.get(0);
        else return null;
    }    
    
    public Integer insertOrder(List lstParam) throws SQLException {
        String sql = " insert into order_service( "
                + " customer_id, status, address_id, content, contact_name, contact_mobile, cleaning, cooking, baby_care, old_care, "
                + " is_periodical, start_date, start_length, mon_start, mon_end, tue_start, tue_end, wed_start, wed_end, "
                + " thu_start, thu_end, fri_start, fri_end, sat_start, sat_end, sun_start, sun_end, price, order_code, create_date) "
                + " values (?,1,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        return C3p0Connector.getInstance().insertData(sql, lstParam);
    }    
    
    public void updateOrderSource(List lstParam) throws SQLException{
        String sql = "update order_service set source = ? ";
        sql += " where order_id = ?";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }
    
    public Integer countOrderInDay() throws SQLException {
        List lstParam = new ArrayList();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);        
        Date fromDate = today.getTime();
        lstParam.add(fromDate);            
        
        today.set(Calendar.HOUR_OF_DAY, 23);
        today.set(Calendar.MINUTE, 59);
        today.set(Calendar.SECOND, 59);        
        Date toDate = today.getTime();
        lstParam.add(toDate);
        
        List<Map> lstUser = DatabaseConnector.getInstance().queryData("select count(order_id) as count from order_service where create_date >= ? and create_date <= ? ", lstParam);
        return Integer.parseInt(lstUser.get(0).get("count").toString());
    }    
}
