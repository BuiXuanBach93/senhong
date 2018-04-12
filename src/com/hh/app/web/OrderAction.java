/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.web;

import com.google.gson.Gson;
import com.hh.action.BaseAction;
import com.hh.app.db.OrderDB;
import com.hh.database.DatabaseConnector;
import com.hh.net.httpserver.Headers;
import com.hh.util.FileUtils;
import com.hh.util.ResourceBundleUtils;
import com.hh.web.FileInfo;
import com.hh.web.HttpUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author agiletech
 */
public class OrderAction extends BaseAction{

    public OrderAction(HttpUtils hu) {
        super(hu);
    }
    
    public void listOrder() throws IOException {
        String userId = (String)httpUtils.getParameter("userid");
        String webPage = "web/app/order/listOrder.html";
        if(userId == null) returnPage(webPage);
        else {
            String content = getTemplatePage();
            FileUtils fu = new FileUtils();
            File headFile = new File(webPage.replace(".html", "_head.html")).getCanonicalFile();
            if (headFile.exists()) {
                String headPage = fu.readFileToString(headFile, FileUtils.UTF_8);
                content = content.replace("<!-- JS,CSS -->", headPage);
            }
            File resultFile = new File(webPage).getCanonicalFile();
            String contentPage = fu.readFileToString(resultFile, FileUtils.UTF_8);
            content = content.replace("<!-- PAGE CONTENT WRAPPER -->", contentPage);
            if(userId != null)
                content = content.replace("id=\"userid1\" name=\"userid\"", "id=\"userid1\" name=\"userid\" value=\"" + userId + "\"");
            byte[] byteContent = content.getBytes(Charset.forName(FileUtils.UTF_8));
            httpUtils.httpExchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            httpUtils.httpExchange.getResponseHeaders().set("Content-Encoding", "gzip");
            httpUtils.httpExchange.sendResponseHeaders(200, 0);
            try(GZIPOutputStream os = new GZIPOutputStream(httpUtils.httpExchange.getResponseBody());)
            {
                os.write(byteContent);
                os.close();
            }            
        }
    }
        
    public void searchOrder() throws IOException, SQLException, ParseException {
        OrderDB odb = new OrderDB();
        
        int pageLength = 10;
        if(httpUtils.getParameter("length") != null) {
            pageLength = Integer.parseInt((String)httpUtils.getParameter("length"));
            if(pageLength == 0) pageLength = 10;
        }
        
        int numberRow = 0;
        if(httpUtils.getParameter("start") != null) {
            numberRow = Integer.parseInt((String)httpUtils.getParameter("start"));
        }

        String ordercode = (String)httpUtils.getParameter("ordercode");
        String customer = (String)httpUtils.getParameter("customer");
        String mobile = (String)httpUtils.getParameter("mobile");
        String fromdate = (String)httpUtils.parameters.get("fromdate");
        String todate = (String)httpUtils.parameters.get("todate");
        String sortType = (String)httpUtils.parameters.get("sorttype");
        
        Integer status = null;
        if(httpUtils.getParameter("status") != null)
            status = Integer.parseInt((String)httpUtils.getParameter("status"));
        
        Integer userId = null;
        if(httpUtils.getParameter("userid") != null)
            userId = Integer.parseInt((String)httpUtils.getParameter("userid"));        
        
        if(!(ordercode != null && !ordercode.trim().isEmpty())) ordercode = null;
        if(!(customer != null && !customer.trim().isEmpty())) customer = null;
        if(!(mobile != null && !mobile.trim().isEmpty())) mobile = null;
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
        
        List<List> listResult = odb.searchOrder(numberRow, pageLength, ordercode, customer, mobile, status, fromDate, toDate, userId, sortType);
        List<List> listData = listResult.get(1);
        for(int i = 0; i < listData.size(); i++) {
            listData.get(i).set(0, numberRow + i + 1);
        }
        Integer count = Integer.parseInt(((List<List>)listResult.get(0)).get(0).get(0).toString());
        returnData.put("recordsTotal", count);
        returnData.put("recordsFiltered", count);        
        returnData.put("data", listData);
        returnAjax();        
    }
    
    public void viewOrder() throws IOException, SQLException {
        String userId = (String)httpUtils.getParameter("orderid");
        if(userId != null && !userId.trim().isEmpty()) {
            this.returnData.put("order",(HashMap)(new OrderDB()).getOrderById(Integer.parseInt(userId)));
            File resultFile = new File("web/app/order/viewOrder.html").getCanonicalFile();
            String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
            this.returnData.put("page", contentPage);
        }
        returnAjax();
    }    
    
    public void uploadOrderFile() throws IOException, SQLException {
        String orderId = (String)httpUtils.parameters.get("orderId");
        if(httpUtils.parameters.get("file") != null) {
            String filePath = ResourceBundleUtils.getConfig("DataDirectory") + File.separator + "order";
            if(!filePath.isEmpty()) {
                filePath = filePath.replace("/", File.separator);
                filePath = filePath.replace("\\", File.separator);
                Calendar c = Calendar.getInstance();
                filePath = filePath + File.separator + c.get(Calendar.YEAR) + File.separator + 
                        (c.get(Calendar.MONTH)+1) + File.separator + c.get(Calendar.DAY_OF_MONTH) + 
                        File.separator;
                File directory = new File(filePath);
                if(!directory.exists()) directory.mkdirs();
                
                FileInfo fileInfo = (FileInfo)httpUtils.parameters.get("file");
                String fileName = "" + (new Date()).getTime() + "_" + UUID.randomUUID().toString() + FileUtils.extractFileExt(fileInfo.getFilename());
                HashMap data = new HashMap();
                data.put("localFileName", fileInfo.getFilename());
                data.put("serverFileName", fileName);
                
                List lstParam = new ArrayList();
                lstParam.add(fileName);
                lstParam.add(Integer.parseInt(orderId));
                DatabaseConnector.getInstance().executeData("update order_service set order_file = ? where order_id = ?", lstParam);
                
                FileOutputStream fios = new FileOutputStream(filePath + fileName);
                fios.write(fileInfo.getBytes());
                fios.close();
                
                String json = new Gson().toJson(data);
                httpUtils.sendStringResponse(200, json);
            }
        } else {
            httpUtils.sendNotFoundResponse();
        }
    }
    
    public void downloadOrderFile() throws IOException {
        Headers resHeader = httpUtils.httpExchange.getResponseHeaders();
        resHeader.set("Content-Type", "application/octet-stream");
        String fileName = (String)httpUtils.getParameter("filename");
        resHeader.set("Content-Disposition", "attachment; filename=\"order-file" + FileUtils.extractFileExt(fileName) + "\"");        
        if(fileName == null) {
            httpUtils.sendNotFoundResponse();
            return;
        }
        String filePath = "";
        if(fileName.contains("_")) {
            String[] arrData = fileName.split("_");
            long createTime = Long.parseLong(arrData[0]);
            Calendar c = Calendar.getInstance();
            c.setTime(new Date(createTime));
            filePath = ResourceBundleUtils.getConfig("DataDirectory") + File.separator + "order";
            filePath = filePath.replace("/", File.separator);
            filePath = filePath.replace("\\", File.separator);                
            filePath = filePath + File.separator + c.get(Calendar.YEAR) + File.separator + 
                    (c.get(Calendar.MONTH)+1) + File.separator + c.get(Calendar.DAY_OF_MONTH) + 
                    File.separator;
        }
        File tmpFile = new File(filePath + fileName);
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
