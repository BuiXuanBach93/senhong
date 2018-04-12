/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.web;

import com.hh.action.BaseAction;
import com.hh.app.db.BmAreaDB;
import com.hh.app.db.FundDB;
import com.hh.util.FileUtils;
import com.hh.web.HttpSession;
import com.hh.web.HttpUtils;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author buixu
 */
public class BmAreaAction extends BaseAction{
    
    public BmAreaAction(HttpUtils hu) {
        super(hu);
    }
     public void listArea() throws IOException {
        returnPage("web/app/area/listArea.html");
    }
    
    public void loadViewArea() throws IOException, SQLException {
        String areaId = (String)httpUtils.getParameter("areaId");
        if(areaId != null && !areaId.trim().isEmpty()) {
            this.returnData.put("area",(HashMap)(new BmAreaDB()).getAreaById(Integer.parseInt(areaId)));
            File resultFile = new File("web/app/area/viewArea.html").getCanonicalFile();
            String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
            this.returnData.put("page", contentPage);
        }
        returnAjax();
    } 
     
    public void viewAddArea() throws IOException, SQLException {
        File resultFile = new File("web/app/area/viewAddArea.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);
        this.returnData.put("page", contentPage);
        returnAjax();
    }    
    
    public void searchArea() throws IOException, SQLException, ParseException {
        BmAreaDB areaDB = new BmAreaDB();
        if(httpUtils.getParameter("isdelete") != null && httpUtils.getParameter("isdelete").equals("1")) {
            String deleteAreas = (String)httpUtils.getParameter("areaId");
            if(deleteAreas != null) {
                deleteAreas = deleteAreas.replace("areaId=", "");
                deleteAreas = deleteAreas.replace("&", ",");
                areaDB.deleteArea(deleteAreas);
                HttpSession.getInstance().setCacheAttribute("bm_area".getBytes(), areaDB.getAllArea());
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
        
        String areaName = null;
        if(httpUtils.getParameter("areaName") != null)
            areaName = (String)httpUtils.getParameter("areaName");
        
        List<List> listResult = areaDB.searchArea(numberRow, pageLength, areaName);
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
        
    public void addArea() throws IOException, SQLException, ParseException {
        String areaName = (String)httpUtils.parameters.get("areaName");
       
        List lstParam = new ArrayList();
        
        if(areaName != null && !areaName.trim().isEmpty()) lstParam.add(areaName);
        else lstParam.add(null);
        
        (new BmAreaDB()).insertArea(lstParam);
        
        HttpSession.getInstance().setCacheAttribute("bm_area".getBytes(), (new BmAreaDB()).getAllArea());
        
        returnAjax(); 
    }
        
    public void updateArea() throws IOException, ParseException, SQLException {
        String areaId = (String)httpUtils.getParameter("areaid");
        String areaName = (String)httpUtils.parameters.get("areaname");
        
        List lstParam = new ArrayList();
        
        if(areaName != null) lstParam.add(areaName.trim());
        else lstParam.add(null);
        
        if(areaId != null && !areaId.trim().isEmpty()) {
            lstParam.add(Integer.parseInt(areaId));
        }
        else lstParam.add(null);        
        
        (new BmAreaDB()).updateArea(lstParam);
        
        Map area = (new BmAreaDB()).getAreaById(Integer.parseInt(areaId));
        
        HttpSession.getInstance().setCacheAttribute("bm_area".getBytes(), (new BmAreaDB()).getAllArea());
        this.returnData.put("data", area);
        returnAjax(); 
    }
            
    public void backListArea() throws IOException, ParseException, SQLException {
        File resultFile = new File("web/app/area/listArea.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
        this.returnData.put("page", contentPage);
        returnAjax();
    }   
}
