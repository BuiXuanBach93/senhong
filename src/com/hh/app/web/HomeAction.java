/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.web;

import com.hh.action.BaseAction;
import com.hh.database.DatabaseConnector;
import com.hh.util.FileUtils;
import com.hh.web.HttpUtils;
import com.hh.util.ResourceBundleUtils;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author agiletech
 */
public class HomeAction extends BaseAction{

    public HomeAction(HttpUtils hu) {
        super(hu);
    }
    
    public void getData() throws SQLException, IOException {
        List<Map> lstRevenueFinish = DatabaseConnector.getInstance()
                .queryData("SELECT 0 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 6 DAY),'%d/%m') AS pay_date, IFNULL(SUM(IFNULL(amount,0)),0) AS amount FROM receipt WHERE status = 2 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 6 DAY),'%d-%m-%Y')" +
                            "UNION " +
                            "SELECT 1 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 5 DAY),'%d/%m') AS pay_date, IFNULL(SUM(IFNULL(amount,0)),0) AS amount FROM receipt WHERE status = 2 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 5 DAY),'%d-%m-%Y') " +
                            "UNION " +
                            "SELECT 2 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 4 DAY),'%d/%m') AS pay_date, IFNULL(SUM(IFNULL(amount,0)),0) AS amount FROM receipt WHERE status = 2 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 4 DAY),'%d-%m-%Y') " +
                            "UNION " +
                            "SELECT 3 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 3 DAY),'%d/%m') AS pay_date, IFNULL(SUM(IFNULL(amount,0)),0) AS amount FROM receipt WHERE status = 2 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 3 DAY),'%d-%m-%Y') " +
                            "UNION " +
                            "SELECT 4 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 2 DAY),'%d/%m') AS pay_date, IFNULL(SUM(IFNULL(amount,0)),0) AS amount FROM receipt WHERE status = 2 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 2 DAY),'%d-%m-%Y') " +
                            "UNION " +
                            "SELECT 5 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY),'%d/%m') AS pay_date, IFNULL(SUM(IFNULL(amount,0)),0) AS amount FROM receipt WHERE status = 2 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY),'%d-%m-%Y') " +
                            "UNION " +
                            "SELECT 6 AS stt, DATE_FORMAT(CURRENT_DATE(),'%d/%m') AS pay_date, IFNULL(SUM(IFNULL(amount,0)),0) AS amount FROM receipt WHERE status = 2 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(CURRENT_DATE(),'%d-%m-%Y') "
                            );
        
        List<Map> lstRevenue = DatabaseConnector.getInstance()
                .queryData("SELECT 0 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 6 DAY),'%d/%m') AS pay_date, IFNULL(SUM(IFNULL(amount,0)),0) AS amount FROM receipt WHERE status = 1 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 6 DAY),'%d-%m-%Y')" +
                            "UNION " +
                            "SELECT 1 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 5 DAY),'%d/%m') AS pay_date, IFNULL(SUM(IFNULL(amount,0)),0) AS amount FROM receipt WHERE status = 1 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 5 DAY),'%d-%m-%Y') " +
                            "UNION " +
                            "SELECT 2 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 4 DAY),'%d/%m') AS pay_date, IFNULL(SUM(IFNULL(amount,0)),0) AS amount FROM receipt WHERE status = 1 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 4 DAY),'%d-%m-%Y') " +
                            "UNION " +
                            "SELECT 3 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 3 DAY),'%d/%m') AS pay_date, IFNULL(SUM(IFNULL(amount,0)),0) AS amount FROM receipt WHERE status = 1 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 3 DAY),'%d-%m-%Y') " +
                            "UNION " +
                            "SELECT 4 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 2 DAY),'%d/%m') AS pay_date, IFNULL(SUM(IFNULL(amount,0)),0) AS amount FROM receipt WHERE status = 1 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 2 DAY),'%d-%m-%Y') " +
                            "UNION " +
                            "SELECT 5 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY),'%d/%m') AS pay_date, IFNULL(SUM(IFNULL(amount,0)),0) AS amount FROM receipt WHERE status = 1 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY),'%d-%m-%Y') " +
                            "UNION " +
                            "SELECT 6 AS stt, DATE_FORMAT(CURRENT_DATE(),'%d/%m') AS pay_date, IFNULL(SUM(IFNULL(amount,0)),0) AS amount FROM receipt WHERE status = 1 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(CURRENT_DATE(),'%d-%m-%Y') "
                            );
        
        List lstResult = new ArrayList();
        HashMap result = new HashMap();
        result.put("key", "Tiền đã thu");
        result.put("values", lstRevenueFinish);
        HashMap result1 = new HashMap();
        result1.put("key", "Tiền chưa thu");
        result1.put("values", lstRevenue);
        lstResult.add(result);
        lstResult.add(result1);
        this.returnData.put("revenue", lstResult);
        
        List<Map> lstMaid = DatabaseConnector.getInstance()
                .queryData("SELECT 0 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 6 DAY),'%d/%m') AS create_date, COUNT(user_id) AS amount FROM sm_user WHERE user_type = 3 AND DATE_FORMAT(create_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 6 DAY),'%d-%m-%Y') " +
                        "UNION " +
                        "SELECT 1 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 5 DAY),'%d/%m') AS create_date, COUNT(user_id) AS amount FROM sm_user WHERE user_type = 3 AND DATE_FORMAT(create_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 5 DAY),'%d-%m-%Y') " +
                        "UNION " +
                        "SELECT 2 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 4 DAY),'%d/%m') AS create_date, COUNT(user_id) AS amount FROM sm_user WHERE user_type = 3 AND DATE_FORMAT(create_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 4 DAY),'%d-%m-%Y') " +
                        "UNION " +
                        "SELECT 3 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 3 DAY),'%d/%m') AS create_date, COUNT(user_id) AS amount FROM sm_user WHERE user_type = 3 AND DATE_FORMAT(create_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 3 DAY),'%d-%m-%Y') " +
                        "UNION " +
                        "SELECT 4 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 2 DAY),'%d/%m') AS create_date, COUNT(user_id) AS amount FROM sm_user WHERE user_type = 3 AND DATE_FORMAT(create_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 2 DAY),'%d-%m-%Y') " +
                        "UNION " +
                        "SELECT 5 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY),'%d/%m') AS create_date, COUNT(user_id) AS amount FROM sm_user WHERE user_type = 3 AND DATE_FORMAT(create_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY),'%d-%m-%Y') " +
                        "UNION " +
                        "SELECT 6 AS stt, DATE_FORMAT(CURRENT_DATE(),'%d/%m') AS create_date, COUNT(user_id) AS amount FROM sm_user WHERE user_type = 3 AND DATE_FORMAT(create_date,'%d-%m-%Y') = DATE_FORMAT(CURRENT_DATE(),'%d-%m-%Y')");
        
        List<Map> lstCustomer = DatabaseConnector.getInstance()
                .queryData("SELECT 0 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 6 DAY),'%d/%m') AS create_date, COUNT(user_id) AS amount FROM sm_user WHERE user_type = 4 AND DATE_FORMAT(create_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 6 DAY),'%d-%m-%Y') " +
                        "UNION " +
                        "SELECT 1 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 5 DAY),'%d/%m') AS create_date, COUNT(user_id) AS amount FROM sm_user WHERE user_type = 4 AND DATE_FORMAT(create_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 5 DAY),'%d-%m-%Y') " +
                        "UNION " +
                        "SELECT 2 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 4 DAY),'%d/%m') AS create_date, COUNT(user_id) AS amount FROM sm_user WHERE user_type = 4 AND DATE_FORMAT(create_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 4 DAY),'%d-%m-%Y') " +
                        "UNION " +
                        "SELECT 3 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 3 DAY),'%d/%m') AS create_date, COUNT(user_id) AS amount FROM sm_user WHERE user_type = 4 AND DATE_FORMAT(create_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 3 DAY),'%d-%m-%Y') " +
                        "UNION " +
                        "SELECT 4 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 2 DAY),'%d/%m') AS create_date, COUNT(user_id) AS amount FROM sm_user WHERE user_type = 4 AND DATE_FORMAT(create_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 2 DAY),'%d-%m-%Y') " +
                        "UNION " +
                        "SELECT 5 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY),'%d/%m') AS create_date, COUNT(user_id) AS amount FROM sm_user WHERE user_type = 4 AND DATE_FORMAT(create_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY),'%d-%m-%Y') " +
                        "UNION " +
                        "SELECT 6 AS stt, DATE_FORMAT(CURRENT_DATE(),'%d/%m') AS create_date, COUNT(user_id) AS amount FROM sm_user WHERE user_type = 4 AND DATE_FORMAT(create_date,'%d-%m-%Y') = DATE_FORMAT(CURRENT_DATE(),'%d-%m-%Y')");

        List lstResult2 = new ArrayList();
        HashMap result2 = new HashMap();
        result2.put("key", "Người giúp việc");
        result2.put("values", lstMaid);
        HashMap result3 = new HashMap();
        result3.put("key", "Khách hàng");
        result3.put("values", lstCustomer);
        lstResult2.add(result2);
        lstResult2.add(result3);
        this.returnData.put("user", lstResult2);
        
        List<Map> lstPlanFinish = DatabaseConnector.getInstance().queryData(
                "SELECT 0 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 6 DAY),'%d/%m') AS start_date, COUNT(plan_id) AS amount FROM plan WHERE STATUS = 5 AND DATE_FORMAT(start_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 6 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 1 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 5 DAY),'%d/%m') AS start_date, COUNT(plan_id) AS amount FROM plan WHERE STATUS = 5 AND DATE_FORMAT(start_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 5 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 2 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 4 DAY),'%d/%m') AS start_date, COUNT(plan_id) AS amount FROM plan WHERE STATUS = 5 AND DATE_FORMAT(start_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 4 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 3 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 3 DAY),'%d/%m') AS start_date, COUNT(plan_id) AS amount FROM plan WHERE STATUS = 5 AND DATE_FORMAT(start_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 3 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 4 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 2 DAY),'%d/%m') AS start_date, COUNT(plan_id) AS amount FROM plan WHERE STATUS = 5 AND DATE_FORMAT(start_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 2 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 5 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY),'%d/%m') AS start_date, COUNT(plan_id) AS amount FROM plan WHERE STATUS = 5 AND DATE_FORMAT(start_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 6 AS stt, DATE_FORMAT(CURRENT_DATE(),'%d/%m') AS start_date, COUNT(plan_id) AS amount FROM plan WHERE STATUS = 5 AND DATE_FORMAT(start_date,'%d-%m-%Y') = DATE_FORMAT(CURRENT_DATE(),'%d-%m-%Y')");

        List<Map> lstPlan = DatabaseConnector.getInstance().queryData(
                "SELECT 0 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 6 DAY),'%d/%m') AS start_date, COUNT(plan_id) AS amount FROM plan WHERE STATUS != 5 AND DATE_FORMAT(start_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 6 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 1 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 5 DAY),'%d/%m') AS start_date, COUNT(plan_id) AS amount FROM plan WHERE STATUS != 5 AND DATE_FORMAT(start_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 5 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 2 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 4 DAY),'%d/%m') AS start_date, COUNT(plan_id) AS amount FROM plan WHERE STATUS != 5 AND DATE_FORMAT(start_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 4 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 3 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 3 DAY),'%d/%m') AS start_date, COUNT(plan_id) AS amount FROM plan WHERE STATUS != 5 AND DATE_FORMAT(start_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 3 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 4 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 2 DAY),'%d/%m') AS start_date, COUNT(plan_id) AS amount FROM plan WHERE STATUS != 5 AND DATE_FORMAT(start_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 2 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 5 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY),'%d/%m') AS start_date, COUNT(plan_id) AS amount FROM plan WHERE STATUS != 5 AND DATE_FORMAT(start_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 6 AS stt, DATE_FORMAT(CURRENT_DATE(),'%d/%m') AS start_date, COUNT(plan_id) AS amount FROM plan WHERE STATUS != 5 AND DATE_FORMAT(start_date,'%d-%m-%Y') = DATE_FORMAT(CURRENT_DATE(),'%d-%m-%Y')");

        List lstResult3 = new ArrayList();
        HashMap result4 = new HashMap();
        result4.put("key", "Việc hoàn thành");
        result4.put("values", lstPlanFinish);
        HashMap result5 = new HashMap();
        result5.put("key", "Việc chưa xong");
        result5.put("values", lstPlan);
        lstResult3.add(result4);
        lstResult3.add(result5);
        this.returnData.put("plan", lstResult3);
        
        List<Map> lstReceiptFinish = DatabaseConnector.getInstance().queryData(
                "SELECT 0 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 6 DAY),'%d/%m') AS pay_date, COUNT(receipt_id) AS amount FROM receipt WHERE STATUS = 2 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 6 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 1 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 5 DAY),'%d/%m') AS pay_date, COUNT(receipt_id) AS amount FROM receipt WHERE STATUS = 2 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 5 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 2 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 4 DAY),'%d/%m') AS pay_date, COUNT(receipt_id) AS amount FROM receipt WHERE STATUS = 2 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 4 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 3 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 3 DAY),'%d/%m') AS pay_date, COUNT(receipt_id) AS amount FROM receipt WHERE STATUS = 2 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 3 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 4 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 2 DAY),'%d/%m') AS pay_date, COUNT(receipt_id) AS amount FROM receipt WHERE STATUS = 2 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 2 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 5 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY),'%d/%m') AS pay_date, COUNT(receipt_id) AS amount FROM receipt WHERE STATUS = 2 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 6 AS stt, DATE_FORMAT(CURRENT_DATE(),'%d/%m') AS pay_date, COUNT(receipt_id) AS amount FROM receipt WHERE STATUS = 2 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(CURRENT_DATE(),'%d-%m-%Y')");

        List<Map> lstReceipt = DatabaseConnector.getInstance().queryData(
                "SELECT 0 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 6 DAY),'%d/%m') AS pay_date, COUNT(receipt_id) AS amount FROM receipt WHERE STATUS != 2 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 6 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 1 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 5 DAY),'%d/%m') AS pay_date, COUNT(receipt_id) AS amount FROM receipt WHERE STATUS != 2 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 5 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 2 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 4 DAY),'%d/%m') AS pay_date, COUNT(receipt_id) AS amount FROM receipt WHERE STATUS != 2 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 4 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 3 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 3 DAY),'%d/%m') AS pay_date, COUNT(receipt_id) AS amount FROM receipt WHERE STATUS != 2 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 3 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 4 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 2 DAY),'%d/%m') AS pay_date, COUNT(receipt_id) AS amount FROM receipt WHERE STATUS != 2 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 2 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 5 AS stt, DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY),'%d/%m') AS pay_date, COUNT(receipt_id) AS amount FROM receipt WHERE STATUS != 2 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY),'%d-%m-%Y') " +
                "UNION " +
                "SELECT 6 AS stt, DATE_FORMAT(CURRENT_DATE(),'%d/%m') AS pay_date, COUNT(receipt_id) AS amount FROM receipt WHERE STATUS != 2 AND DATE_FORMAT(pay_date,'%d-%m-%Y') = DATE_FORMAT(CURRENT_DATE(),'%d-%m-%Y')");

        List lstResult4 = new ArrayList();
        HashMap result6 = new HashMap();
        result6.put("key", "Đã xác nhận");
        result6.put("values", lstReceiptFinish);
        HashMap result7 = new HashMap();
        result7.put("key", "Chưa xác nhận");
        result7.put("values", lstReceipt);
        lstResult4.add(result6);
        lstResult4.add(result7);
        this.returnData.put("receipt", lstResult4);
        returnAjax();
    }

    public void logout() throws IOException {
        httpUtils.removeSession();
        httpUtils.sendRedirect(ResourceBundleUtils.getConfig("SessionTimeoutPage"));
    }    
    
    public void traceLog() throws IOException {
        String line = (String)httpUtils.getParameter("n");
        Integer intLine = null;
        if(line != null && !line.trim().isEmpty()) {
            intLine = Integer.parseInt(line);
        }        
        FileUtils fu = new FileUtils();
        File resultFile = new File("loging.log").getCanonicalFile();
        String content = fu.readLastLines(resultFile, intLine);
        httpUtils.httpExchange.sendResponseHeaders(200, content.getBytes().length);
        try (OutputStream os = httpUtils.httpExchange.getResponseBody();)
        {
            os.write(content.getBytes());
            os.close();
        }
    }
}
