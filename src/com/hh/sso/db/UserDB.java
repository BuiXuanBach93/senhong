/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.sso.db;

import com.hh.database.DatabaseConnector;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author agiletech
 */

public class UserDB {
    public List<Map> loadUserFromDatabase() throws SQLException {
        List<Map> lstUser = DatabaseConnector.getInstance().queryData(
                " select user_id, name, mobile, home_province, user_type, DATE_FORMAT(birthday, '%d-%m-%Y') as birthday, DATE_FORMAT(create_date, '%d-%m-%Y %H:%i') as create_date, id_number, family_mobile, picture, amount, address, latitude, longitude, rating, hour_work, salary_level, reward_amount, penalty_amount, password, video_url, detail, address_id, is_enable, user_file "
                        + " FROM sm_user where is_enable = 1");
        return lstUser;
    }
        
    public void insertUser(List lstParam) throws SQLException {
        String sql = "insert into sm_user(mobile, first_name, last_name, email, pin_code, country, organization, job, address, birthday, create_date, gender, staff_code, salt, is_enable, role_id) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,1,1)";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }
    
    public Integer getUserId(String mobile) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(mobile);
        String sql = "select user_id from sm_user where mobile = ?";
        List lstUser = DatabaseConnector.getInstance().queryData(sql, lstParam);
        if(lstUser != null && !lstUser.isEmpty()) {
            Map user = (Map)lstUser.get(0);
            if(user != null && user.get("user_id") != null) {
                String userId = user.get("user_id").toString();
                return Integer.parseInt(userId);
            }
        }
        return null;
    }
    
    public void updatePasswordUser(List lstParam) throws SQLException {
        String sql = "update sm_user set pin_code = ?, salt = ? where mobile = ? ";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }    
}
