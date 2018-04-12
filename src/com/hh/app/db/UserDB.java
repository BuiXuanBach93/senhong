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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author agiletech
 */

public class UserDB {
    
     public List<Map> getAllUser() throws SQLException {
        return DatabaseConnector.getInstance().queryData("select * from sm_user");
    }    
    
    public int insertUser(List lstParam) throws SQLException {
        String sql = "insert into sm_user(name, mobile, birthday, id_number, password, family_mobile, video_url, detail, home_province, address, latitude, longitude, user_type, picture, create_date, is_enable, salary_level) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,1,1)";
        return C3p0Connector.getInstance().insertData(sql, lstParam);
    }
    
    public void updateUserSource(List lstParam) throws SQLException{
        String sql = "update sm_user set source = ? ";
        sql += " where user_id = ?";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }
    
    public void updateUser(List lstParam, boolean changePassword, boolean deletePic, boolean deleteFile, boolean changeType) throws SQLException {
        String sql = "update sm_user set name = ?, mobile = ?, birthday = ?, id_number = ?, family_mobile = ?, home_province = ?";
        if(changeType) sql += ", user_type = ? ";
        sql += ", address = ?, latitude = ?, longitude = ?, video_url = ?, salary_level = ?, amount = ?, detail = ? ";
        if(changePassword) sql += ", password = ? "; 
        if(deletePic) sql += ", picture = ? ";
        if(deleteFile) sql += ", user_file = ? ";
        sql += " where user_id = ?";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }    
    public boolean phoneAlreadyUse(String phone) throws SQLException{
        String sql = " Select * from sm_user where mobile like '"+phone+"'";
        List<List> lstCountList = DatabaseConnector.getInstance().queryDataToList(sql);
        if(lstCountList.size() > 0){
            return true;
        }
        return false;
    }
    public List<List> searchUser(Integer numberRow, Integer pageLength, String name, String mobile, String address, Integer userType, Date fromDate, Date toDate, String sortAsc) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData = " SELECT 1 as rownum, CONCAT('<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal\" onclick=\"loadViewUser(',user_id,');\">',name,'</a>') as name, " +
                " mobile, address, " +
                " CASE user_type " +
                " WHEN 1 THEN 'Quản trị' " +
                " WHEN 2 THEN 'Điều hành' " +
                " WHEN 3 THEN 'Giúp việc' " +
                " WHEN 4 THEN 'Khách hàng' " +
                " END AS user_type" +
                " , DATE_FORMAT(create_date, '%d-%m-%Y') as create_date, " + 
                " CASE source " +
                " WHEN 0 THEN 'App' " +
                " WHEN 1 THEN 'Web' " +
                " END AS source," +
                " CONCAT('<input type=\"checkbox\" name=\"userid\" onclick=\"validateCheckAll(this)\" value=\"',user_id,'\"/>') as user_id "
                + " FROM sm_user WHERE is_enable = 1 ";
        String queryCount = " SELECT count(user_id) FROM sm_user WHERE is_enable = 1 ";
        String query = " ";
        if(name != null && !name.trim().isEmpty()) {
            query += " AND name like ? ";
            lstParam.add("%" + name.trim() + "%");
            lstParamCount.add("%" + name.trim() + "%");
        }
        if(mobile != null && !mobile.trim().isEmpty()) {
            query += " AND mobile like ? ";
            lstParam.add("%" + mobile.trim() + "%");
            lstParamCount.add("%" + mobile.trim() + "%");
        }
        if(address != null && !address.trim().isEmpty()) {
            query += " AND address like ? ";
            lstParam.add("%" + address.trim() + "%");
            lstParamCount.add("%" + address.trim() + "%");
        }        
        if(userType != null && userType != 0) {
            query += " AND user_type = ? ";
            lstParam.add(userType);
            lstParamCount.add(userType);
        }
        if(fromDate != null) {
            query += " AND sm_user.create_date >= ? ";
            lstParam.add(fromDate);            
            lstParamCount.add(fromDate);            
        }
        if(toDate != null) {
            query += " AND sm_user.create_date <= ? ";
            lstParam.add(toDate);            
            lstParamCount.add(toDate);            
        }
        queryCount += query;
        if("1".equals(sortAsc)) query += " ORDER BY sm_user.create_date ASC LIMIT ?,? ";
        else query += " ORDER BY sm_user.create_date DESC LIMIT ?,? ";
        queryData += query;
        lstParam.add(numberRow);
        lstParam.add(pageLength);
        List<List> lstUser = DatabaseConnector.getInstance().queryDataToList(queryData, lstParam);
        List<List> lstCount = DatabaseConnector.getInstance().queryDataToList(queryCount, lstParamCount);
        List<List> lstResult = new ArrayList();
        lstResult.add(lstCount);
        lstResult.add(lstUser);
        return lstResult;
    }
    
    public List<List> searchPopup(Integer numberRow, Integer pageLength, String name, String mobile, String address, Integer userType, Date fromDate, Date toDate) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData = " SELECT 1 as rownum, name, " +
                " mobile, home_province, " +
                " CASE user_type " +
                " WHEN 1 THEN 'Quản trị' " +
                " WHEN 2 THEN 'Điều hành' " +
                " WHEN 3 THEN 'Giúp việc' " +
                " WHEN 4 THEN 'Khách hàng' " +
                " END AS user_type, " +
                " CONCAT('<input type=\"checkbox\" name=\"userid\" onclick=\"chooseUser(this)\" value=\"',user_id,'\"/>') as user_id "
                + " FROM sm_user WHERE is_enable = 1 ";
        String queryCount = " SELECT count(user_id) FROM sm_user WHERE is_enable = 1 ";
        String query = " ";
        if(name != null && !name.trim().isEmpty()) {
            query += " AND name like ? ";
            lstParam.add("%" + name.trim() + "%");
            lstParamCount.add("%" + name.trim() + "%");
        }
        if(mobile != null && !mobile.trim().isEmpty()) {
            query += " AND mobile like ? ";
            lstParam.add("%" + mobile.trim() + "%");
            lstParamCount.add("%" + mobile.trim() + "%");
        }
        if(address != null && !address.trim().isEmpty()) {
            query += " AND address like ? ";
            lstParam.add("%" + address.trim() + "%");
            lstParamCount.add("%" + address.trim() + "%");
        }        
        if(userType != null && userType != 0) {
            query += " AND user_type = ? ";
            lstParam.add(userType);
            lstParamCount.add(userType);
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
        queryCount += query;
        query += " ORDER BY create_date DESC LIMIT ?,? ";
        queryData += query;
        lstParam.add(numberRow);
        lstParam.add(pageLength);
        List<List> lstUser = DatabaseConnector.getInstance().queryDataToList(queryData, lstParam);
        List<List> lstCount = DatabaseConnector.getInstance().queryDataToList(queryCount, lstParamCount);
        List<List> lstResult = new ArrayList();
        lstResult.add(lstCount);
        lstResult.add(lstUser);
        return lstResult;
    }
    
    public List<List> searchUserMap(Integer numberRow, Integer pageLength, String name, String mobile, String address, Integer userType, Date fromDate, Date toDate) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData = " SELECT 1 as rownum, CONCAT('<a href=\"javascript:void(0)\" onclick=\"loadViewUser(',user_id,');\">',name,'</a>') as name, " +
                " mobile, " +
                " CONCAT('<input type=\"radio\" name=\"userid\" onclick=\"findLocation(,user_id,)\" value=\"',user_id,'\"/>') as user_id "
                + " FROM sm_user WHERE is_enable = 1 ";
        String queryCount = " SELECT count(user_id) FROM sm_user WHERE is_enable = 1 ";
        String query = " ";
        if(name != null && !name.trim().isEmpty()) {
            query += " AND name like ? ";
            lstParam.add("%" + name.trim() + "%");
            lstParamCount.add("%" + name.trim() + "%");
        }
        if(mobile != null && !mobile.trim().isEmpty()) {
            query += " AND mobile like ? ";
            lstParam.add("%" + mobile.trim() + "%");
            lstParamCount.add("%" + mobile.trim() + "%");
        }
        if(address != null && !address.trim().isEmpty()) {
            query += " AND address like ? ";
            lstParam.add("%" + address.trim() + "%");
            lstParamCount.add("%" + address.trim() + "%");
        }        
        if(userType != null && userType != 0) {
            query += " AND user_type = ? ";
            lstParam.add(userType);
            lstParamCount.add(userType);
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
        queryCount += query;
        query += " ORDER BY create_date DESC LIMIT ?,? ";
        queryData += query;
        lstParam.add(numberRow);
        lstParam.add(pageLength);
        List<List> lstUser = DatabaseConnector.getInstance().queryDataToList(queryData, lstParam);
        List<List> lstCount = DatabaseConnector.getInstance().queryDataToList(queryCount, lstParamCount);
        List<List> lstResult = new ArrayList();
        lstResult.add(lstCount);
        lstResult.add(lstUser);
        return lstResult;
    }    
    
    public void deleteUser(String lstUser) throws SQLException {
        DatabaseConnector.getInstance().executeData("update sm_user set is_enable = 0 where user_id in (" + lstUser + ")");
    }
    
    public void deleteUserImage(Integer userId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(userId);
        DatabaseConnector.getInstance().executeData("update sm_user set picture = null where user_id = ?", lstParam);
    }
    
    public Map getUserById(Integer userId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(userId);
        List<Map> lstUser = DatabaseConnector.getInstance().queryData("select user_id, name, mobile, home_province, user_type, DATE_FORMAT(birthday, '%d-%m-%Y') as birthday, DATE_FORMAT(create_date, '%d-%m-%Y %H:%i') as create_date, id_number, family_mobile, picture, amount, address, latitude, longitude, rating, hour_work, salary_level, reward_amount, penalty_amount, password, video_url, detail, address_id, is_enable, user_file from sm_user where user_id = ?", lstParam);
        if(lstUser != null && !lstUser.isEmpty()) return lstUser.get(0);
        else return null;
    }
    
    public Map getUserByPhoneNumber(String phoneNumber) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(phoneNumber);
        List<Map> lstUser = DatabaseConnector.getInstance().queryData("select user_id, name, mobile, home_province, user_type, DATE_FORMAT(birthday, '%d-%m-%Y') as birthday, DATE_FORMAT(create_date, '%d-%m-%Y %H:%i') as create_date, id_number, family_mobile, picture, amount, address, latitude, longitude, rating, hour_work, salary_level, reward_amount, penalty_amount, password, video_url, detail, address_id, is_enable, user_file from sm_user where mobile = ?", lstParam);
        if(lstUser != null && !lstUser.isEmpty()) return lstUser.get(0);
        else return null;
    }
    
    public Map countUserEnableByMobile(String mobile) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(mobile);
        List<Map> lstUser = DatabaseConnector.getInstance().queryData("select count(*) as user_number from sm_user where mobile = ? and is_enable = 1 ", lstParam);
        if(lstUser != null && !lstUser.isEmpty()) return lstUser.get(0);
        else return null;
    }
    
    public Map getUserByPhoneNumberAndPassword(String phoneNumber, String password) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(phoneNumber);
        lstParam.add(password);
        List<Map> lstUser = DatabaseConnector.getInstance().queryData("select user_id, name, mobile, home_province, user_type, DATE_FORMAT(birthday, '%d-%m-%Y') as birthday, DATE_FORMAT(create_date, '%d-%m-%Y %H:%i') as create_date, id_number, family_mobile, picture, amount, address, latitude, longitude, rating, hour_work, salary_level, reward_amount, penalty_amount, password, video_url, detail, address_id, is_enable, user_file from sm_user where mobile = ? and password = ?", lstParam);
        if(lstUser != null && !lstUser.isEmpty()) return lstUser.get(0);
        else return null;
    }
    
    public List<Map> getOrderMaid() throws SQLException {
        return DatabaseConnector.getInstance().queryData("select user_id, name, mobile, home_province, user_type, DATE_FORMAT(birthday, '%d-%m-%Y') as birthday, id_number, family_mobile, picture from sm_user where user_type = 3 limit 3");
    }
    
    public void updateUserImage(String picture, Integer userId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(picture);
        lstParam.add(userId);
        DatabaseConnector.getInstance().executeData("update sm_user set picture = ? where user_id = ?", lstParam);
    }

}
