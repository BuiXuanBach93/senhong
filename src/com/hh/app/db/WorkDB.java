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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author agiletech
 */

public class WorkDB {
    public void insertWork(List lstParam) throws SQLException {
        String sql = "insert into work_time(maid_id, start_date, end_date, mon_start, mon_end, tue_start, tue_end, wed_start, wed_end, thu_start, thu_end, fri_start, fri_end, sat_start, sat_end, sun_start, sun_end, create_date) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        DatabaseConnector.getInstance().executeData(sql, lstParam);
    }
    
    public void updateWork(List lstParam) throws SQLException {
        String sql = "update work_time set time_length = ?, hour_price = ? where id = ?";
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
    
    public List<Map> getWorkTimeByMaidId(List<Map> lstWork, String maidId) {
        List<Map> lstResult = new ArrayList();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        DateFormat datef = new SimpleDateFormat("dd-MM-yyyy");
        for(int i = 0; i < lstWork.size(); i++) {
            if(lstWork.get(i).get("maid_id").toString().equals(maidId)) {
                HashMap workTime = (HashMap)((HashMap)lstWork.get(i)).clone();
                workTime.put("start_date", datef.format((Date)workTime.get("start_date")));
                workTime.put("end_date", datef.format((Date)workTime.get("end_date")));
                workTime.put("create_date", df.format((Date)workTime.get("create_date")));
                lstResult.add(workTime);
            }
        }
        return lstResult;
    }
    
    public List<Map> getRestTimeByMaidId(List<Map> lstRest, String maidId) {
        List<Map> lstResult = new ArrayList();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        DateFormat datef = new SimpleDateFormat("dd-MM-yyyy");        
        for(int i = 0; i < lstRest.size(); i++) {
            if(lstRest.get(i).get("maid_id").toString().equals(maidId)) {
                HashMap restTime = (HashMap)((HashMap)lstRest.get(i)).clone();
                restTime.put("start_date", datef.format((Date)restTime.get("start_date")));
                restTime.put("end_date", datef.format((Date)restTime.get("end_date")));
                restTime.put("create_date", df.format((Date)restTime.get("create_date")));
                lstResult.add(restTime);                
            }
        }
        return lstResult;
    }    
    
    public List<List> searchWork(List<Map> lstWork, String maidName, Date fromDate, Date toDate, Integer dayOfWeek, String fromHour, String toHour, String userId) throws SQLException {
        List lstResult = new ArrayList();
        for(int i = 0; i < lstWork.size(); i++) {
            if(userId != null && lstWork.get(i).get("maid_id") != null) 
                if(!lstWork.get(i).get("maid_id").toString().equals(userId)) continue;
            if(maidName != null && lstWork.get(i).get("maid_name") != null) 
                if(!lstWork.get(i).get("maid_name").toString().toLowerCase().contains(maidName.toLowerCase())) continue;
            if(fromDate != null && lstWork.get(i).get("end_date") != null) 
                if(((Date)lstWork.get(i).get("end_date")).getTime() < fromDate.getTime()) continue;
            if(toDate != null && lstWork.get(i).get("start_date") != null) 
                if(((Date)lstWork.get(i).get("start_date")).getTime() > toDate.getTime()) continue;
            if(dayOfWeek != null) {
                if(fromHour != null) {
                    if(dayOfWeek == 1) 
                        if(compareMinute(fromHour, (String)lstWork.get(i).get("sun_start")) > 1) continue;
                    if(dayOfWeek == 2) 
                        if(compareMinute(fromHour, (String)lstWork.get(i).get("mon_start")) > 1) continue;
                    if(dayOfWeek == 3) 
                        if(compareMinute(fromHour, (String)lstWork.get(i).get("tue_start")) > 1) continue;
                    if(dayOfWeek == 4) 
                        if(compareMinute(fromHour, (String)lstWork.get(i).get("wed_start")) > 1) continue;
                    if(dayOfWeek == 5) 
                        if(compareMinute(fromHour, (String)lstWork.get(i).get("thu_start")) > 1) continue;
                    if(dayOfWeek == 6) 
                        if(compareMinute(fromHour, (String)lstWork.get(i).get("fri_start")) > 1) continue;
                    if(dayOfWeek == 7) 
                        if(compareMinute(fromHour, (String)lstWork.get(i).get("sat_start")) > 1) continue;
                }
                if(toHour != null) {
                    if(dayOfWeek == 1) 
                        if(compareMinute(toHour, (String)lstWork.get(i).get("sun_end")) < 1) continue;
                    if(dayOfWeek == 2) 
                        if(compareMinute(toHour, (String)lstWork.get(i).get("mon_end")) < 1) continue;
                    if(dayOfWeek == 3) 
                        if(compareMinute(toHour, (String)lstWork.get(i).get("tue_end")) < 1) continue;
                    if(dayOfWeek == 4) 
                        if(compareMinute(toHour, (String)lstWork.get(i).get("wed_end")) < 1) continue;
                    if(dayOfWeek == 5) 
                        if(compareMinute(toHour, (String)lstWork.get(i).get("thu_end")) < 1) continue;
                    if(dayOfWeek == 6) 
                        if(compareMinute(toHour, (String)lstWork.get(i).get("fri_end")) < 1) continue;
                    if(dayOfWeek == 7) 
                        if(compareMinute(toHour, (String)lstWork.get(i).get("sat_end")) < 1) continue;
                }
            }
            List lstTemp = new ArrayList();
            lstTemp.add(lstResult.size() + 1);
            lstTemp.add("<a href=\"javascript:void(0)\" data-toggle=\"modal\" data-target=\"#selectModal\" onclick=\"loadViewUser(" + lstWork.get(i).get("maid_id") + ");\">" + lstWork.get(i).get("maid_name") + "</a>");
            if(lstWork.get(i).get("mon_start") != null) lstTemp.add(lstWork.get(i).get("mon_start") + " - " + lstWork.get(i).get("mon_end"));
            else lstTemp.add(null);
            if(lstWork.get(i).get("tue_start") != null) lstTemp.add(lstWork.get(i).get("tue_start") + " - " + lstWork.get(i).get("tue_end"));
            else lstTemp.add(null);
            if(lstWork.get(i).get("wed_start") != null) lstTemp.add(lstWork.get(i).get("wed_start") + " - " + lstWork.get(i).get("wed_end"));
            else lstTemp.add(null);
            if(lstWork.get(i).get("thu_start") != null) lstTemp.add(lstWork.get(i).get("thu_start") + " - " + lstWork.get(i).get("thu_end"));
            else lstTemp.add(null);
            if(lstWork.get(i).get("fri_start") != null) lstTemp.add(lstWork.get(i).get("fri_start") + " - " + lstWork.get(i).get("fri_end"));
            else lstTemp.add(null);
            if(lstWork.get(i).get("sat_start") != null) lstTemp.add(lstWork.get(i).get("sat_start") + " - " + lstWork.get(i).get("sat_end"));
            else lstTemp.add(null);
            if(lstWork.get(i).get("sun_start") != null) lstTemp.add(lstWork.get(i).get("sun_start") + " - " + lstWork.get(i).get("sun_end"));
            else lstTemp.add(null);
            
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            if(lstWork.get(i).get("start_date") != null) lstTemp.add(df.format(lstWork.get(i).get("start_date")));
            if(lstWork.get(i).get("end_date") != null) lstTemp.add(df.format(lstWork.get(i).get("end_date")));
            lstTemp.add("<input type=\"checkbox\" name=\"workId\" onclick=\"validateCheckAll()\" value=\"" + lstWork.get(i).get("id") + "\"/>");
            lstResult.add(lstTemp);
        }
        return lstResult;
    }
    
    public void deleteWork(String lstWork) throws SQLException {
        DatabaseConnector.getInstance().executeData("delete from work_time where id in (" + lstWork + ")");
    }
    
    public Map getWorkById(Integer workId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(workId);
        List<Map> lstWork = DatabaseConnector.getInstance().queryData("select * from work_time where id = ?", lstParam);
        if(lstWork != null && !lstWork.isEmpty()) return lstWork.get(0);
        else return null;
    }  
}
