/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.web;

import com.hh.action.BaseAction;
import com.hh.app.db.ReceiptDB;
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
public class ReportTask extends BaseAction{
    
    public ReportTask(HttpUtils hu) {
        super(hu);
    }
    
    public void reportTask() throws IOException {
        returnPage("web/app/report/reportTask.html");
    }  
    
    public void exportTask() throws IOException, ParseException, SQLException {
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
        
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData = "SELECT 1 as rownum,  " +
                " DATE_FORMAT(a.start_date, '%d-%m-%Y') as work_day, " + 
                " b.name as customer, " +
                " c.name as maid, " 
                + " CASE WHEN a.real_start IS NULL "
                + " THEN CONCAT('(',DATE_FORMAT(a.start_date, '%H:%i'),')') "
                + " ELSE CONCAT(DATE_FORMAT(a.real_start, '%H:%i'),' (',DATE_FORMAT(a.start_date, '%H:%i'),')') "
                + " END AS start_date, "
                + " CASE WHEN a.real_end IS NULL "
                + " THEN CONCAT('(',DATE_FORMAT(a.end_date, '%H:%i'),')') "
                + " ELSE CONCAT(DATE_FORMAT(a.real_end, '%H:%i'),' (',DATE_FORMAT(a.end_date, '%H:%i'),')') "
                + " END AS end_date, " +
                " UNIX_TIMESTAMP(a.start_date) as start, " +
                " UNIX_TIMESTAMP(a.real_start) as real_start, " +
                " UNIX_TIMESTAMP(a.end_date) as end, " +
                " UNIX_TIMESTAMP(a.real_end) as real_end, " +
                " a.distance " +
                " FROM plan a, sm_user b, sm_user c WHERE a.customer_id = b.user_id AND a.maid_id = c.user_id ";

        String query = " ";
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
        query += " ORDER BY a.start_date DESC";
        queryData += query;
        List<List> listData = DatabaseConnector.getInstance().queryDataToList(queryData, lstParam);
        
        Headers resHeader = httpUtils.httpExchange.getResponseHeaders();
        resHeader.set("Content-Type", "application/octet-stream");
        resHeader.set("Content-Disposition", "attachment; filename=\"bao-cao-cong-viec.xls\"");

        String filePath = ResourceBundleUtils.getConfig("DataDirectory") + File.separator + "temp"
                + File.separator + UUID.randomUUID().toString() + ".xls";
        filePath = filePath.replace("/", File.separator);
        filePath = filePath.replace("\\", File.separator);        
        String fileTemplate = ResourceBundleUtils.getConfig("DataDirectory") + File.separator + "template/report-task.xls";
        filePath = filePath.replace("/", File.separator);
        filePath = filePath.replace("\\", File.separator);
        
        String[][] arrayData = new String[listData.size()+1][9];
        for(int i = 0; i < listData.size(); i++) {
            arrayData[i+1][0] = "" + (i+1);
            arrayData[i+1][1] = listData.get(i).get(1).toString();
            arrayData[i+1][2] = listData.get(i).get(2).toString();
            arrayData[i+1][3] = listData.get(i).get(3).toString();
            arrayData[i+1][4] = listData.get(i).get(4).toString();
            arrayData[i+1][5] = listData.get(i).get(5).toString();
            if(listData.get(i).get(6) != null && listData.get(i).get(7) != null) {
                if(Integer.parseInt(listData.get(i).get(7).toString()) -
                        Integer.parseInt(listData.get(i).get(6).toString()) > 900) {
                    arrayData[i+1][6] = "Muộn giờ";
                }
            }
            if(listData.get(i).get(9) != null && listData.get(i).get(8) != null) {
                if(Integer.parseInt(listData.get(i).get(8).toString()) -
                        Integer.parseInt(listData.get(i).get(9).toString()) > 1800) {
                    arrayData[i+1][7] = "Thiếu giờ";
                }
            }
            if(listData.get(i).get(10) != null && Double.parseDouble(listData.get(i).get(8).toString()) > 1); {
                arrayData[i+1][8] = "Sai vị trí";
            }
        }
        if(arrayData.length > 0)
            FileUtils.exportExcelWithTemplate(arrayData, fileTemplate, filePath, 10, new ArrayList());
        else 
            FileUtils.exportExcelWithTemplate(new String[1][1], fileTemplate, filePath, 10, new ArrayList());
        
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
