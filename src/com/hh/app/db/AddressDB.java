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

/**
 *
 * @author agiletech
 */

public class AddressDB {    
    final static int R_KM_OF_EARTH = 6371;
    public Integer insertAddress(List lstParam) throws SQLException {
        String sql = " insert into address(address, detail, latitude, longitude, user_id) values (?,?,?,?,?) ";
        return C3p0Connector.getInstance().insertData(sql, lstParam);
    }  
    
     public Map getAddressById(Integer addressId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(addressId);
        List<Map> lstAddress = DatabaseConnector.getInstance().queryData("select * from address where address_id = ?", lstParam);
        if(lstAddress != null && !lstAddress.isEmpty()) return lstAddress.get(0);
        else return null;
    }
     
     public List<Map> getAddressByUser(Integer userId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(userId);
        return DatabaseConnector.getInstance().queryData("select * from address where user_id = ? order by address_id desc", lstParam);
    }
     
    public double getDistanceFromAddressId(int addressId, double longitude, double latitude) throws SQLException{
        double distance = 0;
        Map addressObj = getAddressById(addressId);
        double addLong = new Double(addressObj.get("longitude").toString());
        double addLat = new Double(addressObj.get("latitude").toString());
        double dLat = deg2rad(addLat - latitude);
        double dLon = deg2rad(addLong - longitude);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(deg2rad(addLat)) * Math.cos(deg2rad(latitude)) * 
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
        distance  = R_KM_OF_EARTH * c;
        return distance;
    }
    
    public double getDistance(double addLat, double addLong, double latitude, double longitude) {
        double distance = 0;
        double dLat = deg2rad(addLat - latitude);
        double dLon = deg2rad(addLong - longitude);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(deg2rad(addLat)) * Math.cos(deg2rad(latitude)) * 
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
        distance  = R_KM_OF_EARTH * c;
        return distance;     
    }    
    
    private double deg2rad(double deg){
        return deg * (Math.PI/180);
    }
}
