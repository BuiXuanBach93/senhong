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
public class ReportDebt extends BaseAction{
    
    public ReportDebt(HttpUtils hu) {
        super(hu);
    }
    
    public void reportDebt() throws IOException {
        returnPage("web/app/report/reportDebt.html");
    }  
    
    public void exportDebt() throws IOException, ParseException, SQLException {
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
        
        List<List> listResult = (new ReceiptDB()).searchReceipt(0, 100000, null, null, fromDate, toDate, 1, "0");
        List<List> listData = listResult.get(1);    
        
        Headers resHeader = httpUtils.httpExchange.getResponseHeaders();
        resHeader.set("Content-Type", "application/octet-stream");
        resHeader.set("Content-Disposition", "attachment; filename=\"bao-cao-thu-no.xls\"");

        String filePath = ResourceBundleUtils.getConfig("DataDirectory") + File.separator + "temp"
                + File.separator + UUID.randomUUID().toString() + ".xls";
        filePath = filePath.replace("/", File.separator);
        filePath = filePath.replace("\\", File.separator);        
        String fileTemplate = ResourceBundleUtils.getConfig("DataDirectory") + File.separator + "template/report-debt.xls";
        filePath = filePath.replace("/", File.separator);
        filePath = filePath.replace("\\", File.separator);
        
        String[][] arrayData = new String[listData.size()+1][6];
        for(int i = 0; i < listData.size(); i++) {
            arrayData[i+1][0] = "" + (i+1);
            arrayData[i+1][1] = listData.get(i).get(1).toString();
            if(listData.get(i).get(2) != null) arrayData[i+1][2] = listData.get(i).get(2).toString();
            String cell3 = listData.get(0).get(3).toString();
            if(cell3.contains("</a>")) {
                arrayData[i+1][3] = cell3.substring(cell3.indexOf(">") + 1, cell3.indexOf("</a>"));
            } else {
                arrayData[i+1][3] = cell3;
            }
            String cell4 = listData.get(0).get(4).toString();
            if(cell3.contains("</a>")) {
                arrayData[i+1][4] = cell4.substring(cell4.indexOf(">") + 1, cell4.indexOf("</a>"));
            } else {
                arrayData[i+1][4] = cell4;
            }
            arrayData[i+1][5] = listData.get(i).get(5).toString();
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
