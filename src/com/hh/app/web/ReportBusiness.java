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
public class ReportBusiness extends BaseAction{
    
    public ReportBusiness(HttpUtils hu) {
        super(hu);
    }
    
    public void listBusiness() throws IOException {
        returnPage("web/app/report/listBusiness.html");
    }  
    
    public void exportBusiness() throws IOException, ParseException, SQLException {
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
            String sql = "SELECT 0 AS stt, create_date, SUM(new_order) AS new_order, SUM(order_revenue) AS order_revenue, SUM(other_revenue) AS other_revenue, SUM(salary_cost) AS salary_cost, SUM(other_cost) AS other_cost, (SUM(order_revenue) + SUM(other_revenue) - SUM(salary_cost) - SUM(other_cost)) AS profit  " +
                        "FROM " +
                        "( " +
                        "SELECT 0 AS stt, DATE_FORMAT(pay_date,'%Y/%m/%d') AS create_date, COUNT(order_id) AS new_order, 0 AS order_revenue, 0 AS other_revenue, 0 AS salary_cost, 0 AS other_cost, 0 AS profit FROM receipt WHERE STATUS = 2 AND pay_date > ? AND pay_date < ? AND plan_id IS NOT NULL GROUP BY create_date  " +
                        "UNION " +
                        "SELECT 0 AS stt, DATE_FORMAT(pay_date,'%Y/%m/%d') AS create_date, 0 AS new_order, IFNULL(SUM(IFNULL(amount,0)),0) AS order_revenue, 0 AS other_revenue, 0 AS salary_cost, 0 AS other_cost, 0 AS profit FROM receipt WHERE STATUS = 2 AND pay_date > ? AND pay_date < ? AND plan_id IS NOT NULL GROUP BY create_date  " +
                        "UNION " +
                        "SELECT 0 AS stt, DATE_FORMAT(pay_date,'%Y/%m/%d') AS create_date, 0 AS new_order, 0 AS order_revenue, IFNULL(SUM(IFNULL(amount,0)),0) AS other_revenue, 0 AS salary_cost, 0 AS other_cost, 0 AS profit FROM receipt WHERE STATUS = 2 AND pay_date > ? AND pay_date < ? AND plan_id IS NULL GROUP BY create_date  " +
                        "UNION " +
                        "SELECT 0 AS stt, DATE_FORMAT(pay_date,'%Y/%m/%d') AS create_date, 0 AS new_order, 0 AS order_revenue, 0 AS other_revenue, IFNULL(SUM(IFNULL(amount,0)),0) AS salary_cost, 0 AS other_cost, 0 AS profit FROM spend WHERE STATUS = 2 AND pay_date > ? AND pay_date < ? AND spend_reason = 'Trả lương' GROUP BY create_date  " +
                        "UNION " +
                        "SELECT 0 AS stt, DATE_FORMAT(pay_date,'%Y/%m/%d') AS create_date, 0 AS new_order, 0 AS order_revenue, 0 AS other_revenue, 0 AS salary_cost, IFNULL(SUM(IFNULL(amount,0)),0) AS other_cost, 0 AS profit FROM spend WHERE STATUS = 2 AND pay_date > ? AND pay_date < ? AND spend_reason != 'Trả lương' GROUP BY create_date  " +
                        ") AS report " +
                        "GROUP BY create_date ORDER BY create_date";
            List lstParam = new ArrayList();
            lstParam.add(fromDate);
            lstParam.add(toDate);
            lstParam.add(fromDate);
            lstParam.add(toDate);
            lstParam.add(fromDate);
            lstParam.add(toDate);
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
            String sql = "SELECT 0 AS stt, create_date, SUM(new_order) AS new_order, SUM(order_revenue) AS order_revenue, SUM(other_revenue) AS other_revenue, SUM(salary_cost) AS salary_cost, SUM(other_cost) AS other_cost, (SUM(order_revenue) + SUM(other_revenue) - SUM(salary_cost) - SUM(other_cost)) AS profit  " +
                        "FROM " +
                        "( " +
                        "SELECT 0 AS stt, CONCAT(DATE_FORMAT(pay_date,'%Y/%m'),' Tuần ', FLOOR((DayOfMonth(pay_date)-1)/7)+1) AS create_date, COUNT(order_id) AS new_order, 0 AS order_revenue, 0 AS other_revenue, 0 AS salary_cost, 0 AS other_cost, 0 AS profit FROM receipt WHERE STATUS = 2 AND pay_date > ? AND pay_date < ? AND plan_id IS NOT NULL GROUP BY create_date  " +
                        "UNION " +
                        "SELECT 0 AS stt, CONCAT(DATE_FORMAT(pay_date,'%Y/%m'),' Tuần ', FLOOR((DayOfMonth(pay_date)-1)/7)+1) AS create_date, 0 AS new_order, IFNULL(SUM(IFNULL(amount,0)),0) AS order_revenue, 0 AS other_revenue, 0 AS salary_cost, 0 AS other_cost, 0 AS profit FROM receipt WHERE STATUS = 2 AND pay_date > ? AND pay_date < ? AND plan_id IS NOT NULL GROUP BY create_date  " +
                        "UNION " +
                        "SELECT 0 AS stt, CONCAT(DATE_FORMAT(pay_date,'%Y/%m'),' Tuần ', FLOOR((DayOfMonth(pay_date)-1)/7)+1) AS create_date, 0 AS new_order, 0 AS order_revenue, IFNULL(SUM(IFNULL(amount,0)),0) AS other_revenue, 0 AS salary_cost, 0 AS other_cost, 0 AS profit FROM receipt WHERE STATUS = 2 AND pay_date > ? AND pay_date < ? AND plan_id IS NULL GROUP BY create_date  " +
                        "UNION " +
                        "SELECT 0 AS stt, CONCAT(DATE_FORMAT(pay_date,'%Y/%m'),' Tuần ', FLOOR((DayOfMonth(pay_date)-1)/7)+1) AS create_date, 0 AS new_order, 0 AS order_revenue, 0 AS other_revenue, IFNULL(SUM(IFNULL(amount,0)),0) AS salary_cost, 0 AS other_cost, 0 AS profit FROM spend WHERE STATUS = 2 AND pay_date > ? AND pay_date < ? AND spend_reason = 'Trả lương' GROUP BY create_date  " +
                        "UNION " +
                        "SELECT 0 AS stt, CONCAT(DATE_FORMAT(pay_date,'%Y/%m'),' Tuần ', FLOOR((DayOfMonth(pay_date)-1)/7)+1) AS create_date, 0 AS new_order, 0 AS order_revenue, 0 AS other_revenue, 0 AS salary_cost, IFNULL(SUM(IFNULL(amount,0)),0) AS other_cost, 0 AS profit FROM spend WHERE STATUS = 2 AND pay_date > ? AND pay_date < ? AND spend_reason != 'Trả lương' GROUP BY create_date  " +
                        ") AS report " +
                        "GROUP BY create_date";
            List lstParam = new ArrayList();
            lstParam.add(fromDate);
            lstParam.add(toDate);
            lstParam.add(fromDate);
            lstParam.add(toDate);
            lstParam.add(fromDate);
            lstParam.add(toDate);
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
            String sql = "SELECT 0 AS stt, create_date, SUM(new_order) AS new_order, SUM(order_revenue) AS order_revenue, SUM(other_revenue) AS other_revenue, SUM(salary_cost) AS salary_cost, SUM(other_cost) AS other_cost, (SUM(order_revenue) + SUM(other_revenue) - SUM(salary_cost) - SUM(other_cost)) AS profit  " +
                        "FROM " +
                        "( " +
                        "SELECT 0 AS stt, DATE_FORMAT(pay_date,'%Y/%m') AS create_date, COUNT(order_id) AS new_order, 0 AS order_revenue, 0 AS other_revenue, 0 AS salary_cost, 0 AS other_cost, 0 AS profit FROM receipt WHERE STATUS = 2 AND pay_date > ? AND pay_date < ? AND plan_id IS NOT NULL GROUP BY create_date  " +
                        "UNION " +
                        "SELECT 0 AS stt, DATE_FORMAT(pay_date,'%Y/%m') AS create_date, 0 AS new_order, IFNULL(SUM(IFNULL(amount,0)),0) AS order_revenue, 0 AS other_revenue, 0 AS salary_cost, 0 AS other_cost, 0 AS profit FROM receipt WHERE STATUS = 2 AND pay_date > ? AND pay_date < ? AND plan_id IS NOT NULL GROUP BY create_date  " +
                        "UNION " +
                        "SELECT 0 AS stt, DATE_FORMAT(pay_date,'%Y/%m') AS create_date, 0 AS new_order, 0 AS order_revenue, IFNULL(SUM(IFNULL(amount,0)),0) AS other_revenue, 0 AS salary_cost, 0 AS other_cost, 0 AS profit FROM receipt WHERE STATUS = 2 AND pay_date > ? AND pay_date < ? AND plan_id IS NULL GROUP BY create_date  " +
                        "UNION " +
                        "SELECT 0 AS stt, DATE_FORMAT(pay_date,'%Y/%m') AS create_date, 0 AS new_order, 0 AS order_revenue, 0 AS other_revenue, IFNULL(SUM(IFNULL(amount,0)),0) AS salary_cost, 0 AS other_cost, 0 AS profit FROM spend WHERE STATUS = 2 AND pay_date > ? AND pay_date < ? AND spend_reason = 'Trả lương' GROUP BY create_date  " +
                        "UNION " +
                        "SELECT 0 AS stt, DATE_FORMAT(pay_date,'%Y/%m') AS create_date, 0 AS new_order, 0 AS order_revenue, 0 AS other_revenue, 0 AS salary_cost, IFNULL(SUM(IFNULL(amount,0)),0) AS other_cost, 0 AS profit FROM spend WHERE STATUS = 2 AND pay_date > ? AND pay_date < ? AND spend_reason != 'Trả lương' GROUP BY create_date  " +
                        ") AS report " +
                        "GROUP BY create_date";
            List lstParam = new ArrayList();
            lstParam.add(fromDate);
            lstParam.add(toDate);
            lstParam.add(fromDate);
            lstParam.add(toDate);
            lstParam.add(fromDate);
            lstParam.add(toDate);
            lstParam.add(fromDate);
            lstParam.add(toDate);
            lstParam.add(fromDate);
            lstParam.add(toDate);
            lstResult = DatabaseConnector.getInstance().queryDataToList(sql, lstParam);                        
        }
        
        Headers resHeader = httpUtils.httpExchange.getResponseHeaders();
        resHeader.set("Content-Type", "application/octet-stream");
        resHeader.set("Content-Disposition", "attachment; filename=\"bao-cao-kinh-doanh.xls\"");

        String filePath = ResourceBundleUtils.getConfig("DataDirectory") + File.separator + "temp"
                + File.separator + UUID.randomUUID().toString() + ".xls";
        filePath = filePath.replace("/", File.separator);
        filePath = filePath.replace("\\", File.separator);        
        String fileTemplate = ResourceBundleUtils.getConfig("DataDirectory") + File.separator + "template/report-business.xls";
        filePath = filePath.replace("/", File.separator);
        filePath = filePath.replace("\\", File.separator);
        
        String[][] arrayData = new String[lstResult.size()+1][8];
        for(int i = 0; i < lstResult.size(); i++) {
            arrayData[i+1][0] = "" + (i+1);
            arrayData[i+1][1] = lstResult.get(i).get(1).toString();
            arrayData[i+1][2] = lstResult.get(i).get(2).toString();
            arrayData[i+1][3] = lstResult.get(i).get(3).toString();
            arrayData[i+1][4] = lstResult.get(i).get(4).toString();
            arrayData[i+1][5] = lstResult.get(i).get(5).toString();
            arrayData[i+1][6] = lstResult.get(i).get(6).toString();
            arrayData[i+1][7] = lstResult.get(i).get(7).toString();
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
