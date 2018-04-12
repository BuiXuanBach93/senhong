/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.web;

import com.hh.action.BaseAction;
import com.hh.app.db.MaidDB;
import com.hh.app.db.WorkDB;
import com.hh.util.FileUtils;
import com.hh.web.HttpSession;
import com.hh.web.HttpUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author agiletech
 */
public class WorkAction extends BaseAction{

    public WorkAction(HttpUtils hu) {
        super(hu);
    }
    
    public void listWork() throws IOException {
        String userId = (String)httpUtils.getParameter("userid");
        String webPage = "web/app/work/listWork.html";
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
    
    public void viewAddWork() throws IOException, SQLException {
        File resultFile = new File("web/app/work/viewAddWork.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);
        this.returnData.put("maid", HttpSession.getInstance().getCacheAttribute("cache_maid".getBytes()));
        this.returnData.put("page", contentPage);
        returnAjax();
    }    
    
    public void searchWork() throws IOException, SQLException, ParseException {
        WorkDB wdb = new WorkDB();
        if(httpUtils.getParameter("isdelete") != null && httpUtils.getParameter("isdelete").equals("1")) {
            String deleteWorks = (String)httpUtils.getParameter("workId");
            if(deleteWorks != null) {
                deleteWorks = deleteWorks.replace("workId=", "");
                deleteWorks = deleteWorks.replace("&", ",");
                wdb.deleteWork(deleteWorks);
                HttpSession.getInstance().setCacheAttribute("cache_workTime".getBytes(), (new MaidDB()).getAllWorkTime());
            }
        }        
        
        int pageLength = 10;
        if(httpUtils.getParameter("length") != null) {
            pageLength = Integer.parseInt((String)httpUtils.getParameter("length"));
            if(pageLength == 0) pageLength = 10;
        }
        
        int numberRow = 0;
        if(httpUtils.getParameter("start") != null) {
            numberRow = Integer.parseInt((String)httpUtils.getParameter("start"));
        }
        
        String maidName = (String)httpUtils.getParameter("maidName");
        
        String fromdate = (String)httpUtils.getParameter("fromDate");
        Date fromDate = null;
        if(fromdate != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            fromDate = df.parse(fromdate.trim() + " 00:00:00");
        }
        
        String todate = (String)httpUtils.getParameter("toDate");
        Date toDate = null;
        if(todate != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            toDate = df.parse(todate.trim() + " 00:00:00");
        }
        
        Integer dayOfWeek = null;
        if(httpUtils.getParameter("dayOfWeek") != null) {
            dayOfWeek = Integer.parseInt((String)httpUtils.getParameter("dayOfWeek"));
        }
        
        String userId = (String)httpUtils.getParameter("userid");

        String fromHour = (String)httpUtils.getParameter("fromHour");

        String toHour = (String)httpUtils.getParameter("toHour");
        
        List<Map> lstWork = (List<Map>)HttpSession.getInstance().getCacheAttribute("cache_workTime".getBytes()); 
        
        List<List> listResult = wdb.searchWork(lstWork, maidName, fromDate, toDate, dayOfWeek, fromHour, toHour, userId);
        List<List> listData = new ArrayList();
        
        for(int i = numberRow; i < numberRow + pageLength; i++) {
            if(i < listResult.size()) {                
                listData.add(listResult.get(i));
            } else break;
        }
        returnData.put("recordsTotal", listResult.size());
        returnData.put("recordsFiltered", listResult.size());        
        returnData.put("data", listData);
        returnAjax();        
    }
        
    public void addWork() throws IOException, SQLException, ParseException {
        String maidId = (String)httpUtils.parameters.get("maidId");
        String startDate = (String)httpUtils.parameters.get("fromDate");
        String endDate = (String)httpUtils.parameters.get("toDate");
        String monStart = (String)httpUtils.parameters.get("monStart");
        String monEnd = (String)httpUtils.parameters.get("monEnd");
        String tueStart = (String)httpUtils.parameters.get("tueStart");
        String tueEnd = (String)httpUtils.parameters.get("tueEnd");
        String wedStart = (String)httpUtils.parameters.get("wedStart");
        String wedEnd = (String)httpUtils.parameters.get("wedEnd");
        String thuStart = (String)httpUtils.parameters.get("thuStart");
        String thuEnd = (String)httpUtils.parameters.get("thuEnd");
        String friStart = (String)httpUtils.parameters.get("friStart");
        String friEnd = (String)httpUtils.parameters.get("friEnd");
        String satStart = (String)httpUtils.parameters.get("satStart");
        String satEnd = (String)httpUtils.parameters.get("satEnd");
        String sunStart = (String)httpUtils.parameters.get("sunStart");
        String sunEnd = (String)httpUtils.parameters.get("sunEnd");

        List lstParam = new ArrayList();
        
        Integer intMaidId = null;
        if(maidId != null && !maidId.trim().isEmpty()) {
            intMaidId = Integer.parseInt(maidId);
            lstParam.add(intMaidId);
        } else {
            this.returnData.put("error", "Giúp việc không được để trống");
            returnAjax();
            return;
        }
        
        Date dtStartDate = null;
        if(startDate != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            dtStartDate = df.parse(startDate.trim() + " 00:00:00");
            lstParam.add(dtStartDate);
        } else {
            this.returnData.put("error", "Ngày bắt đầu không được để trống");
            returnAjax();
            return;
        }
        
        Date dtEndDate = null;
        if(endDate != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            dtEndDate = df.parse(endDate.trim() + " 00:00:00");
            lstParam.add(dtEndDate);
        } else {
            this.returnData.put("error", "Ngày kết thúc không được để trống");
            returnAjax();
            return;
        }

        if(monStart != null && monEnd != null) {
            lstParam.add(monStart);
            lstParam.add(monEnd);
        } else {
            lstParam.add(null);
            lstParam.add(null);            
        }
        if(tueStart != null && tueEnd != null) {
            lstParam.add(tueStart);
            lstParam.add(tueEnd);
        } else {
            lstParam.add(null);
            lstParam.add(null);            
        }
        if(wedStart != null && wedEnd != null) {
            lstParam.add(wedStart);
            lstParam.add(wedEnd);
        } else {
            lstParam.add(null);
            lstParam.add(null);            
        }
        if(thuStart != null && thuEnd != null) {
            lstParam.add(thuStart);
            lstParam.add(thuEnd);
        } else {
            lstParam.add(null);
            lstParam.add(null);            
        }
        if(friStart != null && friEnd != null) {
            lstParam.add(friStart);
            lstParam.add(friEnd);
        } else {
            lstParam.add(null);
            lstParam.add(null);            
        }
        if(satStart != null && satEnd != null) {
            lstParam.add(satStart);
            lstParam.add(satEnd);
        } else {
            lstParam.add(null);
            lstParam.add(null);            
        }
        if(sunStart != null && sunEnd != null) {
            lstParam.add(sunStart);
            lstParam.add(sunEnd);
        } else {
            lstParam.add(null);
            lstParam.add(null);            
        }
        lstParam.add(new Date());
        
        (new WorkDB()).insertWork(lstParam);
        
        HttpSession.getInstance().setCacheAttribute("cache_workTime".getBytes(), (new MaidDB()).getAllWorkTime());
        
        returnAjax(); 
    }
        
    public void viewWork() throws IOException, SQLException {
        String workId = (String)httpUtils.getParameter("workId");
        if(workId != null && !workId.trim().isEmpty()) {
            this.returnData.put("work",(HashMap)(new WorkDB()).getWorkById(Integer.parseInt(workId)));
            File resultFile = new File("web/app/work/viewWork.html").getCanonicalFile();
            String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
            this.returnData.put("page", contentPage);
        }
        returnAjax();
    }
            
    public void backListWork() throws IOException, ParseException, SQLException {
        File resultFile = new File("web/app/work/listWork.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
        this.returnData.put("page", contentPage);
        returnAjax();
    }   
}
