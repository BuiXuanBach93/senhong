/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.db;

import com.hh.database.C3p0Connector;
import com.hh.database.DatabaseConnector;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author agiletech
 */

public class PlanDB {    
    public List<List> searchPlan(Integer numberRow, Integer pageLength, String orderCode, String customer, String maid, Integer status, Date fromDate, Date toDate, Integer maidId, Integer customerId, String sortType) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData = "SELECT 1 as rownum, CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal\" onclick=\"viewPlan(',a.plan_id,');\">',a.plan_code,'</a>') as plan_code, " +
                " CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal\" onclick=\"viewOrder(',d.order_id,');\">',d.order_code,'</a>') as order_code," +
                " CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal\" onclick=\"loadViewUser(',b.user_id,');\">',b.name,'</a>') as customer, " +
                " CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal\" onclick=\"loadViewUser(',c.user_id,');\">',c.name,'</a>') as maid, " +
                " DATE_FORMAT(a.start_date, '%d-%m %H:%i') as start_date, " + 
                " DATE_FORMAT(a.end_date, '%d-%m %H:%i') as end_date, " + 
                " CASE a.status " +
                " WHEN 1 THEN 'Chưa thực hiện' " +
                " WHEN 2 THEN 'Nhận việc' " +
                " WHEN 3 THEN 'Thay người' " +
                " WHEN 4 THEN 'Đang thực hiện' " +
                " WHEN 5 THEN 'Hoàn thành' " +
                " WHEN 6 THEN 'Đóng' " +
                " END AS status" +                
                " FROM plan a " +
                " left join sm_user b on a.customer_id = b.user_id " +
                " left join sm_user c on a.maid_id = c.user_id " +
                " left join order_service d on a.order_id = d.order_id where 1=1 ";
        String queryCount = "SELECT count(a.plan_id) FROM plan a " +
                " left join sm_user b on a.customer_id = b.user_id " +
                " left join sm_user c on a.maid_id = c.user_id " +
                " left join order_service d on a.order_id = d.order_id where 1=1 ";
        String query = " ";
        if(orderCode != null && !orderCode.trim().isEmpty()) {
            query += " AND d.order_code like ? ";
            lstParam.add("%" + orderCode.trim() + "%");
            lstParamCount.add("%" + orderCode.trim() + "%");
        }
        if(customer != null && !customer.trim().isEmpty()) {
            query += " AND b.name like ? ";
            lstParam.add("%" + customer.trim() + "%");
            lstParamCount.add("%" + customer.trim() + "%");
        }
        if(maid != null && !maid.trim().isEmpty()) {
            query += " AND c.name like ? ";
            lstParam.add("%" + maid.trim() + "%");
            lstParamCount.add("%" + maid.trim() + "%");
        }
        if(status != null && status != 0) {
            query += " AND a.status = ? ";
            lstParam.add(status);
            lstParamCount.add(status);
        }
        if(maidId != null && maidId != 0) {
            query += " AND a.maid_id = ? ";
            lstParam.add(maidId);
            lstParamCount.add(maidId);
        }    
        if(customerId != null && customerId != 0) {
            query += " AND a.customer_id = ? ";
            lstParam.add(customerId);
            lstParamCount.add(customerId);
        }         
        if(fromDate != null) {
            query += " AND a.start_date >= ? ";
            lstParam.add(fromDate);            
            lstParamCount.add(fromDate);            
        }
        if(toDate != null) {
            query += " AND a.start_date <= ? ";
            lstParam.add(toDate);            
            lstParamCount.add(toDate);            
        }
        queryCount += query;
        if("1".equals(sortType))
            query += " ORDER BY a.start_date ASC LIMIT ?,? ";
        else if("2".equals(sortType))
            query += " ORDER BY a.end_date DESC LIMIT ?,? ";
        else if("3".equals(sortType))
            query += " ORDER BY a.end_date ASC LIMIT ?,? ";
        else
            query += " ORDER BY a.start_date DESC LIMIT ?,? ";
        
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
    
    public List<List> viewSalary(Integer maidId, Integer spendId) throws SQLException {
        if(spendId == null) {
            List lstParam = new ArrayList();
            String queryData = "SELECT 1 as rownum, " +
                    " CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal1\" onclick=\"viewOrder(',d.order_id,');\">',d.order_code,'</a>') as order_code," +
                    " CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal1\" onclick=\"loadViewUser(',c.user_id,');\">',c.name,'</a>') as maid, " +
                    " DATE_FORMAT(a.start_date, '%d-%m %H:%i') as start_date, " + 
                    " DATE_FORMAT(a.end_date, '%d-%m %H:%i') as end_date, " +                 
                    " a.salary, " +
                    " a.reward, " +
                    " a.penalty, " +
                    " a.salary_level, " +
                    " a.maid_id " + 
                    " FROM plan a, sm_user c, order_service d WHERE a.maid_id = c.user_id and a.order_id = d.order_id and a.maid_id = ? and a.status = 5 and a.salary_level is null ORDER BY a.start_date DESC ";
            lstParam.add(maidId);
            List<List> lstOrder = DatabaseConnector.getInstance().queryDataToList(queryData, lstParam);
            List<List> lstResult = new ArrayList();
            lstResult.add(new ArrayList());
            lstResult.add(lstOrder);
            return lstResult;
        } else {
            List lstParam = new ArrayList();
            String queryData = "SELECT 1 as rownum, " +
                    " CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal1\" onclick=\"viewOrder(',d.order_id,');\">',d.order_code,'</a>') as order_code," +
                    " CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal1\" onclick=\"loadViewUser(',c.user_id,');\">',c.name,'</a>') as maid, " +
                    " DATE_FORMAT(a.start_date, '%d-%m %H:%i') as start_date, " + 
                    " DATE_FORMAT(a.end_date, '%d-%m %H:%i') as end_date, " +                 
                    " a.salary, " +
                    " a.reward, " +
                    " a.penalty, " +
                    " a.salary_level, " +
                    " a.maid_id " + 
                    " FROM plan a, sm_user c, order_service d WHERE a.maid_id = c.user_id and a.order_id = d.order_id and a.maid_id = ? and a.status = 5 and a.spend_id = ? ORDER BY a.start_date DESC ";
            lstParam.add(maidId);
            lstParam.add(spendId);
            List<List> lstOrder = DatabaseConnector.getInstance().queryDataToList(queryData, lstParam);
            List<List> lstResult = new ArrayList();
            lstResult.add(new ArrayList());
            lstResult.add(lstOrder);
            return lstResult;            
        }
    }    
    
    public List<List> searchTask(Integer numberRow, Integer pageLength, String orderCode, String maid, Integer status, Date fromDate, Date toDate, Integer maidId, Integer customerId, String sortType) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData = "SELECT 1 as rownum, CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal1\" onclick=\"viewTask(',a.plan_id,');\">',a.plan_code,'</a>') as plan_code, " +
                " CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal1\" onclick=\"viewOrder(',d.order_id,');\">',d.order_code,'</a>') as order_code," +
                " CONCAT(CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal1\" onclick=\"loadViewUser(',c.user_id,');\">', c.name,'</a>'), '<br/>', c.mobile) as maid, " +
                " DATE_FORMAT(a.start_date, '%d-%m-%Y') as work_day, " + 
                " CONCAT(IFNULL(DATE_FORMAT(a.real_start, '%H:%i'), '') ,' (',DATE_FORMAT(a.start_date, '%H:%i'),')') as start_date, " + 
                " CONCAT(IFNULL(DATE_FORMAT(a.real_end, '%H:%i'), '') ,' (',DATE_FORMAT(a.end_date, '%H:%i'),')') as end_date, " + 
                " CASE a.status " +
                " WHEN 1 THEN 'Chưa thực hiện' " +
                " WHEN 2 THEN 'Nhận việc' " +
                " WHEN 3 THEN 'Thay người' " +
                " WHEN 4 THEN 'Đang thực hiện' " +
                " WHEN 5 THEN 'Hoàn thành' " +
                " WHEN 6 THEN 'Đóng' " +
                " END AS status, " +
                " UNIX_TIMESTAMP(a.start_date) as start, " +
                " UNIX_TIMESTAMP(a.real_start) as real_start, " +
                " UNIX_TIMESTAMP(a.end_date) as end, " +
                " UNIX_TIMESTAMP(a.real_end) as real_end, " +
                " a.distance, a.finish_distance, a.real_lat, a.real_long, a.finish_lat, a.finish_long, " +
                " e.latitude, e.longitude, e.address_id, a.plan_id " +
                " FROM plan a, sm_user c, order_service d, address e WHERE a.maid_id = c.user_id and a.order_id = d.order_id and d.address_id = e.address_id ";
        String queryCount = "SELECT count(a.plan_id) FROM plan a, sm_user c, order_service d, address e WHERE a.maid_id = c.user_id and a.order_id = d.order_id and d.address_id = e.address_id ";
        String query = " ";
        if(orderCode != null && !orderCode.trim().isEmpty()) {
            query += " AND d.order_code like ? ";
            lstParam.add("%" + orderCode.trim() + "%");
            lstParamCount.add("%" + orderCode.trim() + "%");
        }
        if(maid != null && !maid.trim().isEmpty()) {
            query += " AND c.name like ? ";
            lstParam.add("%" + maid.trim() + "%");
            lstParamCount.add("%" + maid.trim() + "%");
        }
        if(status != null && status != 0) {
            query += " AND a.status = ? ";
            lstParam.add(status);
            lstParamCount.add(status);
        }
        if(maidId != null && maidId != 0) {
            query += " AND a.maid_id = ? ";
            lstParam.add(maidId);
            lstParamCount.add(maidId);
        }
        if(customerId != null && customerId != 0) {
            query += " AND a.customer_id = ? ";
            lstParam.add(customerId);
            lstParamCount.add(customerId);
        }        
        if(fromDate != null) {
            query += " AND a.start_date >= ? ";
            lstParam.add(fromDate);            
            lstParamCount.add(fromDate);            
        }
        if(toDate != null) {
            query += " AND a.start_date <= ? ";
            lstParam.add(toDate);            
            lstParamCount.add(toDate);            
        }
        queryCount += query;
        if("1".equals(sortType))
            query += " ORDER BY a.start_date ASC LIMIT ?,? ";
        else if("2".equals(sortType))
            query += " ORDER BY a.end_date DESC LIMIT ?,? ";
        else if("3".equals(sortType))
            query += " ORDER BY a.end_date ASC LIMIT ?,? ";
        else
            query += " ORDER BY a.start_date DESC LIMIT ?,? ";
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
    
    public void insertBatchPlan(List lstBatch) throws SQLException {
        String sql = " insert into plan(customer_id, order_id, maid_id, contact_name, contact_mobile, start_date, end_date, price, salary, plan_code, status) values (?,?,?,?,?,?,?,?,?,?,1) ";
        C3p0Connector.getInstance().executeDataBatch(sql, lstBatch);
    }    
    
    public Map getPlanById(Integer planId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(planId);
        List<Map> lstUser = DatabaseConnector.getInstance().queryData(
                " select a.plan_id, a.plan_code, a.status, c.name as customer_name, CONCAT(d.name,' - ',d.mobile) as maid_name, d.user_id as maid_id, " +
                " c.picture as customer_picture, d.picture as maid_picture, " +
                " DATE_FORMAT(a.start_date, '%d-%m-%Y %H:%i') as start_date, " + 
                " DATE_FORMAT(a.end_date, '%d-%m-%Y %H:%i') as end_date, " + 
                " DATE_FORMAT(a.real_start, '%d-%m-%Y %H:%i') as real_start, " + 
                " DATE_FORMAT(a.real_end, '%d-%m-%Y %H:%i') as real_end, " + 
                " start_date as start, " + 
                " end_date as end, " +         
                " a.customer_comment, a.customer_rating, a.time_comment, " +
                " a.maid_comment, a.rating_customer, a.rating_work, a.time_maid_comment, a.salary, a.price, a.contact_name, a.contact_mobile, c.user_id as customer_id, a.order_id " +
                " from plan a " +
                " left join sm_user c on a.customer_id = c.user_id " +
                " left join sm_user d on a.maid_id = d.user_id " +
                " where a.plan_id = ?", lstParam);
            if(lstUser != null && !lstUser.isEmpty()) return lstUser.get(0);
        else return null;
    }       

    public List<Map> getPlansByCustomerId(int customerId, Date fromDate, Date toDate) throws SQLException, ParseException {
        List lstParam = new ArrayList();
        lstParam.add(customerId);
        List<Map> lstPlan = null;
        
        String query = 
        " SELECT a.plan_id, a.plan_code, a.customer_id, b.name as customer_name, b.picture as customer_picture, " +
        " a.maid_id, c.name as maid_name, c.picture as maid_picture, e.address_id, e.province, e.district, e.road, e.address, e.detail, e.longitude, e.latitude,  " +
        " d.price, " +
        " DATE_FORMAT(a.start_date, '%d-%m-%Y %H:%i:%s') as start_date,DATE_FORMAT(a.end_date, '%d-%m-%Y %H:%i:%s') as end_date , a.order_id, a.status,a.salary,a.rating_work,a.rating_customer,a.maid_comment, " +
        " DATE_FORMAT(a.real_start, '%d-%m-%Y %H:%i:%s') as real_start, DATE_FORMAT(a.real_end, '%d-%m-%Y %H:%i:%s') as real_end, a.time_comment, a.salary, a.customer_comment, a.customer_rating, a.real_lat, a.real_long, a.distance  " +
        " FROM plan a " +
        " left join sm_user b on a.customer_id = b.user_id " +
        " left join sm_user c on a.maid_id = c.user_id " +
        " left join order_service d on a.order_id = d.order_id " +
        " left join address e on d.address_id = e.address_id " +
        " where a.customer_id = ? ";
        
        if(fromDate != null) {
            query += " and a.start_date > ? ";
            lstParam.add(fromDate);
        }
        
        if(toDate != null) {
            query += " and a.start_date < ? ";
            lstParam.add(toDate);
        }
        
        query += " order by a.start_date limit 5 ";
        
        lstPlan = DatabaseConnector.getInstance().queryData(query, lstParam);

        return lstPlan;
    }
    
    public List<Map> getPlansByMaidId(int maidId, Date fromDate, Date toDate) throws SQLException, ParseException {
        List lstParam = new ArrayList();
        lstParam.add(maidId);
        List<Map> lstPlan = null;
        
        String query = 
        " SELECT a.plan_id, a.plan_code, a.customer_id, b.name as customer_name, b.picture as customer_picture, " +
        " a.maid_id, c.name as maid_name, c.picture as maid_picture, e.address_id, e.address, e.province, e.district, e.road, e.detail, e.longitude, e.latitude,  " +
        " d.price, a.salary, " +
        " DATE_FORMAT(a.start_date, '%d-%m-%Y %H:%i:%s') as start_date,DATE_FORMAT(a.end_date, '%d-%m-%Y %H:%i:%s') as end_date , a.order_id, a.status,a.salary,a.rating_work,a.rating_customer,a.maid_comment, " +
        " DATE_FORMAT(a.real_start, '%d-%m-%Y %H:%i:%s') as real_start, DATE_FORMAT(a.real_end, '%d-%m-%Y %H:%i:%s') as real_end, a.time_comment, a.salary, a.customer_comment, a.customer_rating, a.real_lat, a.real_long, a.distance  " +
        " FROM plan a " +
        " left join sm_user b on a.customer_id = b.user_id " +
        " left join sm_user c on a.maid_id = c.user_id " +
        " left join order_service d on a.order_id = d.order_id " +
        " left join address e on d.address_id = e.address_id " +
        " where a.maid_id = ? ";
        
        if(fromDate != null) {
            query += " and a.start_date > ? ";
            lstParam.add(fromDate);
        }
        
        if(toDate != null) {
            query += " and a.start_date < ? ";
            lstParam.add(toDate);
        }
        
        query += " order by a.start_date limit 5 ";
        
        lstPlan = DatabaseConnector.getInstance().queryData(query, lstParam);

        return lstPlan;
    }
    
    public void updateCustomerRate(List lstParam) throws SQLException {
        String sql = "update plan set customer_comment = ?, customer_rating = ?, time_comment = ? where plan_id = ?";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }
    
    public void updateMaidRate(List lstParam) throws SQLException {
        String sql = "update plan set maid_comment = ?, rating_customer = ?, rating_work = ?, time_comment = ? where plan_id = ?";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }
    
    public void updatePlanStartWork(int planId, Date startDate, int status, double realLat, double realLong, double distance) throws SQLException{
        List lstParam = new ArrayList();
        lstParam.add(status);
        lstParam.add(startDate);
        lstParam.add(realLat);
        lstParam.add(realLong);
        lstParam.add(distance);
        lstParam.add(planId);
        String sql = " update plan set status = ?, real_start = ?, real_lat = ?, real_long = ?, distance =?  where plan_id = ? ";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }
    
     public void updatePlanFinishWork(int planId, Date endDate, int status, double realLat, double realLong, double distance) throws SQLException{
        List lstParam = new ArrayList();
        lstParam.add(status);
        lstParam.add(endDate);
        lstParam.add(realLat);
        lstParam.add(realLong);
        lstParam.add(distance);        
        lstParam.add(planId);
        String sql = " update plan set status = ?, real_end = ?, finish_lat = ?, finish_long = ?, finish_distance = ? where plan_id = ? ";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }
     
    public void updatePlanStatus(int planId, int status) throws SQLException{
        List lstParam = new ArrayList();
        lstParam.add(status);
        lstParam.add(planId);
        String sql = " update plan set status = ? where plan_id = ? ";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }
    
    public void updateMaidPlan(Integer maidId, Date startDate, Date endDate, Integer status, Integer planId, String apply, Integer orderId) throws SQLException {
        List lstParam = new ArrayList();
        String sql = " update plan set maid_id = ? ";
        lstParam.add(maidId);
        if(startDate != null) {
            sql += ", start_date = ? ";
            lstParam.add(startDate);
        }
        if(endDate != null) {
            sql += ", end_date = ? ";
            lstParam.add(endDate);
        }
        if(status != null) {
            sql += ", status = ? ";
            lstParam.add(status);
        }
        sql += " where plan_id = ? ";
        lstParam.add(planId);
        DatabaseConnector.getInstance().executeData(sql, lstParam);        
        
        if("1".equals(apply)) {
            List lstParam2 = new ArrayList();
            String sql2 = " update plan set maid_id = ? ";
            lstParam2.add(maidId);
            if(status != null) {
                sql2 += ", status = ? ";
                lstParam2.add(status);
            }
            sql += " where order_id = ? and plan_id > ? ";
            lstParam.add(orderId);
            lstParam.add(planId);
            
            DatabaseConnector.getInstance().executeData(sql2, lstParam2);      
        }
    }
    
    public void updateMaidTask(List lstParam) throws SQLException {
        String sql = " update plan set real_start = ?, real_end = ? where plan_id = ? ";
        DatabaseConnector.getInstance().executeData(sql, lstParam);        
    }    
    
    public List<List> searchSalary(Integer numberRow, Integer pageLength, String maid, String mobile, String status, Date fromDate, Date toDate, String sortAsc) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData =
                " SELECT 1 as rownum, "
                + " CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal1\" onclick=\"loadViewUser(',b.user_id,');\">',b.name,'</a>') as maid, "
                + " b.mobile, "
                + " CASE WHEN a.salary_level IS NULL "
                + " THEN (SUM(a.salary) * b.salary_level) + SUM(IFNULL(reward, 0)) - SUM(IFNULL(penalty, 0)) "
                + " ELSE (SUM(a.salary) * a.salary_level) + SUM(IFNULL(reward, 0)) - SUM(IFNULL(penalty, 0)) "
                + " END AS salary, " 
                + " CASE WHEN a.salary_level IS NULL " 
                + " THEN 'Chưa trả' " 
                + " ELSE 'Đã trả' " 
                + " END AS status, " 
                + " DATE_FORMAT(a.salary_date, '%d-%m-%Y') as salary_date, " 
                + " CONCAT('PC',a.spend_id) as spend_id, " 
                + " a.maid_id, b.salary_level, b.name " 
                + " FROM plan a, sm_user b WHERE a.maid_id = b.user_id and a.status = 5 ";
        String queryCount = "SELECT count(*) as count_plan FROM (SELECT 0 FROM plan a, sm_user b WHERE  a.maid_id = b.user_id and a.status = 5 ";
        String query = " ";
        if(maid != null && !maid.trim().isEmpty()) {
            query += " AND b.name like ? ";
            lstParam.add("%" + maid.trim() + "%");
            lstParamCount.add("%" + maid.trim() + "%");
        }
        if(mobile != null && !mobile.trim().isEmpty()) {
            query += " AND b.mobile like ? ";
            lstParam.add("%" + mobile.trim() + "%");
            lstParamCount.add("%" + mobile.trim() + "%");
        }
        if(fromDate != null) {
            query += " AND a.salary_date >= ? ";
            lstParam.add(fromDate);            
            lstParamCount.add(fromDate);            
        }
        if(toDate != null) {
            query += " AND a.salary_date <= ? ";
            lstParam.add(toDate);            
            lstParamCount.add(toDate);            
        }
        queryCount += query + " GROUP BY a.maid_id, a.spend_id, a.salary_level, a.salary_date) cp";
        queryData += query + " GROUP BY a.maid_id, a.spend_id, a.salary_level, a.salary_date ";
        if("1".equals(sortAsc)) queryData += " ORDER BY a.salary_date ASC LIMIT ?,? ";
        else queryData += " ORDER BY a.salary_date DESC LIMIT ?,? ";        
        lstParam.add(numberRow);
        lstParam.add(pageLength);
        List<List> lstOrder = DatabaseConnector.getInstance().queryDataToList(queryData, lstParam);
        List<List> lstCount = DatabaseConnector.getInstance().queryDataToList(queryCount, lstParamCount);
        List<List> lstResult = new ArrayList();
        lstResult.add(lstCount);
        lstResult.add(lstOrder);
        return lstResult;        
    }
    
    public List<Map> getSalaryByMaidId(Integer maidId) throws SQLException {
        List lstParam = new ArrayList();
        String queryData =
                " SELECT "
                + " b.name, "
                + " b.mobile, "
                + " CASE WHEN a.salary_level IS NULL "
                + " THEN (SUM(a.salary) * b.salary_level) + SUM(IFNULL(reward, 0)) - SUM(IFNULL(penalty, 0)) "
                + " ELSE (SUM(a.salary) * a.salary_level) + SUM(IFNULL(reward, 0)) - SUM(IFNULL(penalty, 0)) "
                + " END AS salary, " 
                + " CASE WHEN a.salary_level IS NULL " 
                + " THEN 'Chưa trả' " 
                + " ELSE 'Đã trả' " 
                + " END AS status, " 
                + " DATE_FORMAT(a.salary_date, '%d-%m-%Y') as salary_date, " 
                + " CONCAT('PC',a.spend_id) as spend_id, " 
                + " a.maid_id, b.salary_level, b.name " 
                + " FROM plan a, sm_user b WHERE a.maid_id = b.user_id and a.status = 5 and  a.maid_id = ? "
                + " GROUP BY a.maid_id, a.spend_id, a.salary_level, a.salary_date ";
        lstParam.add(maidId);
        return DatabaseConnector.getInstance().queryData(queryData, lstParam);
    }    
    
    public void paySalary(String[] lstMaid, Integer payerId) throws SQLException {
        for(int i = 0; i < lstMaid.length; i++) {
            List lstParam = new ArrayList();
            String[] values = lstMaid[i].split("_");
            lstParam.add(Integer.parseInt(values[0]));
            lstParam.add(payerId);
            lstParam.add(new Date());
            lstParam.add("Trả lương");
            lstParam.add(Integer.parseInt(values[1]));
            lstParam.add(1);
            Integer spendId = C3p0Connector.getInstance().insertData("insert into spend(receiver_id, payer_id, pay_date, spend_reason, amount, status) values (?,?,?,?,?,?)", lstParam);
            
            List lstParam2 = new ArrayList();
            lstParam2.add(spendId);
            lstParam2.add(Integer.parseInt(values[2]));
            lstParam2.add(new Date());
            lstParam2.add(Integer.parseInt(values[0]));
            
            DatabaseConnector.getInstance().executeData("update plan set spend_id = ?, salary_level = ?, salary_date = ? where maid_id = ? and spend_id is null ", lstParam2);
        }
    }    
    
    public void updateUserSalary(Integer hourWork, Integer salary, Integer userId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(hourWork);
        lstParam.add(salary);
        lstParam.add(userId);
        DatabaseConnector.getInstance().executeData(
                "update sm_user set hour_work = ?, salary = ? where user_id = ? ", lstParam);
    }
    
    public void updateUserAmount(Integer hourWork, Integer amount, Integer userId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(hourWork);
        lstParam.add(amount);
        lstParam.add(userId);
        DatabaseConnector.getInstance().executeData(
                "update sm_user set hour_work = ?, amount = ? where user_id = ? ", lstParam);
    }
    
    public void updateUserRating(Float rating, Integer countRating, Integer userId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(rating);
        lstParam.add(countRating);
        lstParam.add(userId);
        DatabaseConnector.getInstance().executeData(
                "update sm_user set rating = ?, count_rating = ? where user_id = ? ", lstParam);
    }    
}
