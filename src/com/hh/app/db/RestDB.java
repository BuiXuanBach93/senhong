/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.db;

import com.hh.database.DatabaseConnector;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author agiletech
 */

public class RestDB {
    public void insertRest(List lstParam) throws SQLException {
        String sql = "insert into rest_time(maid_id, start_date, end_date, mon_start, mon_end, tue_start, tue_end, wed_start, wed_end, thu_start, thu_end, fri_start, fri_end, sat_start, sat_end, sun_start, sun_end, create_date) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }
    
    public void updateRest(List lstParam) throws SQLException {
        String sql = "update rest_time set time_length = ?, hour_price = ? where id = ?";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }    
    
    private int compareMinute(String strSearch, String strDate) {
        String hour = strSearch.split(":")[0];
        if(hour.charAt(0) == '0') hour = hour.substring(1);
        String minute = strSearch.split(":")[1];
        if(minute.charAt(0) == '0') minute = minute.substring(1);
        int mn1 = Integer.parseInt(hour) * 60 + Integer.parseInt(minute);
        
        String hour2 = strDate.split(":")[0];
        if(hour2.charAt(0) == '0') hour2 = hour2.substring(1);
        String minute2 = strDate.split(":")[1];
        if(minute2.charAt(0) == '0') minute2 = minute2.substring(1);
        int mn2 = Integer.parseInt(hour2) * 60 + Integer.parseInt(minute2);
        
        if(mn1 > mn2) return 1;
        else if(mn1 == mn2) return 0;
        else return -1;
    }
    
    public List<List> searchRest(List<Map> lstRest, String maidName, Date fromDate, Date toDate, Integer dayOfWeek, String fromHour, String toHour) throws SQLException {
        List lstResult = new ArrayList();
        for(int i = 0; i < lstRest.size(); i++) {
            if(maidName != null && lstRest.get(i).get("maid_name") != null) 
                if(!lstRest.get(i).get("maid_name").toString().toLowerCase().contains(maidName.toLowerCase())) continue;
            if(fromDate != null && lstRest.get(i).get("end_date") != null) 
                if(((Date)lstRest.get(i).get("end_date")).getTime() < fromDate.getTime()) continue;
            if(toDate != null && lstRest.get(i).get("start_date") != null) 
                if(((Date)lstRest.get(i).get("start_date")).getTime() > toDate.getTime()) continue;
            if(dayOfWeek != null) {
                if(fromHour != null) {
                    if(dayOfWeek == 1) 
                        if(compareMinute(fromHour, (String)lstRest.get(i).get("sun_start")) > 1) continue;
                    if(dayOfWeek == 2) 
                        if(compareMinute(fromHour, (String)lstRest.get(i).get("mon_start")) > 1) continue;
                    if(dayOfWeek == 3) 
                        if(compareMinute(fromHour, (String)lstRest.get(i).get("tue_start")) > 1) continue;
                    if(dayOfWeek == 4) 
                        if(compareMinute(fromHour, (String)lstRest.get(i).get("wed_start")) > 1) continue;
                    if(dayOfWeek == 5) 
                        if(compareMinute(fromHour, (String)lstRest.get(i).get("thu_start")) > 1) continue;
                    if(dayOfWeek == 6) 
                        if(compareMinute(fromHour, (String)lstRest.get(i).get("fri_start")) > 1) continue;
                    if(dayOfWeek == 7) 
                        if(compareMinute(fromHour, (String)lstRest.get(i).get("sat_start")) > 1) continue;
                }
                if(toHour != null) {
                    if(dayOfWeek == 1) 
                        if(compareMinute(toHour, (String)lstRest.get(i).get("sun_end")) < 1) continue;
                    if(dayOfWeek == 2) 
                        if(compareMinute(toHour, (String)lstRest.get(i).get("mon_end")) < 1) continue;
                    if(dayOfWeek == 3) 
                        if(compareMinute(toHour, (String)lstRest.get(i).get("tue_end")) < 1) continue;
                    if(dayOfWeek == 4) 
                        if(compareMinute(toHour, (String)lstRest.get(i).get("wed_end")) < 1) continue;
                    if(dayOfWeek == 5) 
                        if(compareMinute(toHour, (String)lstRest.get(i).get("thu_end")) < 1) continue;
                    if(dayOfWeek == 6) 
                        if(compareMinute(toHour, (String)lstRest.get(i).get("fri_end")) < 1) continue;
                    if(dayOfWeek == 7) 
                        if(compareMinute(toHour, (String)lstRest.get(i).get("sat_end")) < 1) continue;
                }
            }
            List lstTemp = new ArrayList();
            lstTemp.add(lstResult.size() + 1);
            lstTemp.add("<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal\" onclick=\"loadViewUser(" + lstRest.get(i).get("maid_id") + ");\">" + lstRest.get(i).get("maid_name") + "</a>");
            if(lstRest.get(i).get("mon_start") != null) lstTemp.add(lstRest.get(i).get("mon_start") + " - " + lstRest.get(i).get("mon_end"));
            else lstTemp.add(null);
            if(lstRest.get(i).get("tue_start") != null) lstTemp.add(lstRest.get(i).get("tue_start") + " - " + lstRest.get(i).get("tue_end"));
            else lstTemp.add(null);
            if(lstRest.get(i).get("wed_start") != null) lstTemp.add(lstRest.get(i).get("wed_start") + " - " + lstRest.get(i).get("wed_end"));
            else lstTemp.add(null);
            if(lstRest.get(i).get("thu_start") != null) lstTemp.add(lstRest.get(i).get("thu_start") + " - " + lstRest.get(i).get("thu_end"));
            else lstTemp.add(null);
            if(lstRest.get(i).get("fri_start") != null) lstTemp.add(lstRest.get(i).get("fri_start") + " - " + lstRest.get(i).get("fri_end"));
            else lstTemp.add(null);
            if(lstRest.get(i).get("sat_start") != null) lstTemp.add(lstRest.get(i).get("sat_start") + " - " + lstRest.get(i).get("sat_end"));
            else lstTemp.add(null);
            if(lstRest.get(i).get("sun_start") != null) lstTemp.add(lstRest.get(i).get("sun_start") + " - " + lstRest.get(i).get("sun_end"));
            else lstTemp.add(null);
            
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            if(lstRest.get(i).get("start_date") != null) lstTemp.add(df.format(lstRest.get(i).get("start_date")));
            if(lstRest.get(i).get("end_date") != null) lstTemp.add(df.format(lstRest.get(i).get("end_date")));
            lstTemp.add("<input type=\"checkbox\" name=\"restId\" onclick=\"validateCheckAll()\" value=\"" + lstRest.get(i).get("id") + "\"/>");
            lstResult.add(lstTemp);
        }
        return lstResult;
    }
    
    public void deleteRest(String lstRest) throws SQLException {
        DatabaseConnector.getInstance().executeData("delete from rest_time where id in (" + lstRest + ")");
    }
    
    public Map getRestById(Integer restId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(restId);
        List<Map> lstRest = DatabaseConnector.getInstance().queryData("select * from rest_time where id = ?", lstParam);
        if(lstRest != null && !lstRest.isEmpty()) return lstRest.get(0);
        else return null;
    }
    
}
