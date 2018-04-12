/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.web;

import com.hh.action.BaseAction;
import com.hh.database.DatabaseConnector;
import com.hh.net.httpserver.Headers;
import com.hh.util.FileUtils;
import com.hh.util.ResourceBundleUtils;
import com.hh.web.HttpUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author buixu
 */
public class ReportMaid extends BaseAction{
    
    public ReportMaid(HttpUtils hu) {
        super(hu);
    }
    
    public void reportMaid() throws IOException {
        returnPage("web/app/report/reportMaid.html");
    }  
    
    public void exportMaid() throws IOException, ParseException, SQLException {
        String type = (String)httpUtils.getParameter("type");
        String fromdate = (String)httpUtils.getParameter("fromdate");
        String todate = (String)httpUtils.getParameter("todate");
        
        Date fromDate = null;
        if(fromdate != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            fromDate = df.parse(fromdate.trim() + " 00:00:00");
        }
        Date toDate = null;
        if(todate != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            toDate = df.parse(todate.trim() + " 23:59:59");
        }        
        
        List<List> lstResult = new ArrayList();
        List<List> lstReportParam = new ArrayList();
        if("1".equals(type)) {
            List param = new ArrayList();
            param.add("$timehead");
            param.add("NGÀY");
            lstReportParam.add(param);
            String sql = "SELECT 0 AS stt, group_date, SUM(count_maid) AS count_maid, SUM(count_work) AS count_work " +
                        "FROM " +
                        "( " +
                        "SELECT 0 AS stt, DATE_FORMAT(create_date,'%Y/%m/%d') AS group_date, COUNT(user_id) AS count_maid, 0 AS count_work FROM sm_user WHERE user_type = 3 AND create_date > ? AND create_date < ? GROUP BY group_date " +
                        "UNION " +
                        "SELECT 0 AS stt, DATE_FORMAT(start_date,'%Y/%m/%d') AS group_date, 0 AS count_maid, count(maid_id) AS count_work FROM plan WHERE start_date > ? AND start_date < ? GROUP BY group_date " +
                        ") AS report " +
                        "GROUP BY group_date ORDER BY group_date";
            List lstParam = new ArrayList();
            lstParam.add(fromDate);
            lstParam.add(toDate);
            lstParam.add(fromDate);
            lstParam.add(toDate);
            lstResult = DatabaseConnector.getInstance().queryDataToList(sql, lstParam);
        } else if("2".equals(type)) {
            List param = new ArrayList();
            param.add("$timehead");
            param.add("TUẦN");
            lstReportParam.add(param);
            String sql = "SELECT 0 AS stt, group_date, SUM(count_maid) AS count_maid, SUM(count_work) AS count_work " +
                        "FROM " +
                        "( " +
                        "SELECT 0 AS stt, CONCAT(DATE_FORMAT(create_date,'%Y/%m'),' Tuần ', FLOOR((DayOfMonth(create_date)-1)/7)+1) AS group_date, COUNT(user_id) AS count_maid, 0 AS count_work, 0 AS count_rest FROM sm_user WHERE user_type = 3 AND create_date > ? AND create_date < ? GROUP BY group_date " +
                        "UNION " +
                        "SELECT 0 AS stt, CONCAT(DATE_FORMAT(start_date,'%Y/%m'),' Tuần ', FLOOR((DayOfMonth(start_date)-1)/7)+1) AS group_date, 0 AS count_maid, count(maid_id) AS count_work, 0 AS count_rest FROM plan WHERE start_date > ? AND start_date < ? GROUP BY group_date " +
                        ") AS report " +
                        "GROUP BY group_date ORDER BY group_date";
            List lstParam = new ArrayList();
            lstParam.add(fromDate);
            lstParam.add(toDate);
            lstParam.add(fromDate);
            lstParam.add(toDate);
            lstResult = DatabaseConnector.getInstance().queryDataToList(sql, lstParam);            
        } else if("3".equals(type)) {
            List param = new ArrayList();
            param.add("$timehead");
            param.add("THÁNG");
            lstReportParam.add(param);            
            String sql = "SELECT 0 AS stt, group_date, SUM(count_maid) AS count_maid, SUM(count_work) AS count_work " +
                        "FROM " +
                        "( " +
                        "SELECT 0 AS stt, DATE_FORMAT(create_date,'%Y/%m') AS group_date, COUNT(user_id) AS count_maid, 0 AS count_work FROM sm_user WHERE user_type = 3 AND create_date > ? AND create_date < ? GROUP BY group_date " +
                        "UNION " +
                        "SELECT 0 AS stt, DATE_FORMAT(start_date,'%Y/%m') AS group_date, 0 AS count_maid, count(maid_id) AS count_work FROM plan WHERE start_date > ? AND start_date < ? GROUP BY group_date " +
                        ") AS report " +
                        "GROUP BY group_date ORDER BY group_date";
            List lstParam = new ArrayList();
            lstParam.add(fromDate);
            lstParam.add(toDate);
            lstParam.add(fromDate);
            lstParam.add(toDate);
            lstResult = DatabaseConnector.getInstance().queryDataToList(sql, lstParam);                        
        }
        
        Headers resHeader = httpUtils.httpExchange.getResponseHeaders();
        resHeader.set("Content-Type", "application/octet-stream");
        resHeader.set("Content-Disposition", "attachment; filename=\"bao-cao-pt-khach-hang.xls\"");

        String filePath = ResourceBundleUtils.getConfig("DataDirectory") + File.separator + "temp"
                + File.separator + UUID.randomUUID().toString() + ".xls";
        filePath = filePath.replace("/", File.separator);
        filePath = filePath.replace("\\", File.separator);        
        String fileTemplate = ResourceBundleUtils.getConfig("DataDirectory") + File.separator + "template/report-maid.xls";
        filePath = filePath.replace("/", File.separator);
        filePath = filePath.replace("\\", File.separator);
        
        String[][] arrayData = new String[lstResult.size()+1][4];
        for(int i = 0; i < lstResult.size(); i++) {
            arrayData[i+1][0] = "" + (i+1);
            arrayData[i+1][1] = lstResult.get(i).get(1).toString();
            arrayData[i+1][2] = lstResult.get(i).get(2).toString();
            arrayData[i+1][3] = lstResult.get(i).get(3).toString();
        }
        if(arrayData.length > 0)
            FileUtils.exportExcelWithTemplate(arrayData, fileTemplate, filePath, 10, lstReportParam);
        else 
            FileUtils.exportExcelWithTemplate(new String[1][1], fileTemplate, filePath, 10, lstReportParam);
        
        File tmpFile = new File(filePath);
        if(!tmpFile.exists()) {
            httpUtils.sendNotFoundResponse();
            return;
        }
        InputStream is = new FileInputStream(tmpFile);
        httpUtils.httpExchange.sendResponseHeaders(200, 0);
        OutputStream os = httpUtils.httpExchange.getResponseBody();
        IOUtils.copy(is, os);
        is.close();
        os.close();          
    }
}
