/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.web;

import com.hh.action.BaseAction;
import com.hh.app.db.CategoryDB;
import com.hh.util.FileUtils;
import com.hh.web.HttpSession;
import com.hh.web.HttpUtils;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author agiletech
 */
public class CategoryAction extends BaseAction{

    public CategoryAction(HttpUtils hu) {
        super(hu);
    }
    
    public void listCategory() throws IOException {
        returnPage("web/app/category/listCategory.html");
    }
    
    public void viewAddCategory() throws IOException, SQLException {
        File resultFile = new File("web/app/category/viewAddCategory.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);
        this.returnData.put("page", contentPage);
        returnAjax();
    }    
    
    public void searchCategory() throws IOException, SQLException, ParseException {
        CategoryDB udb = new CategoryDB();
        if(httpUtils.getParameter("isdelete") != null && httpUtils.getParameter("isdelete").equals("1")) {
            String deleteCategorys = (String)httpUtils.getParameter("categoryId");
            if(deleteCategorys != null) {
                deleteCategorys = deleteCategorys.replace("categoryId=", "");
                deleteCategorys = deleteCategorys.replace("&", ",");
                udb.deleteCategory(deleteCategorys);
                HttpSession.getInstance().setCacheAttribute("cache_category".getBytes(), (new CategoryDB()).getAllCategory());
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
        
        String name = (String)httpUtils.getParameter("categoryName");
        
        Integer timeLength = null;
        if(httpUtils.getParameter("timeLength") != null)
            timeLength = Integer.parseInt((String)httpUtils.getParameter("timeLength"));
        
        List<List> listResult = udb.searchCategory(numberRow, pageLength, timeLength, name);
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
        
    public void addCategory() throws IOException, SQLException, ParseException {
        String categoryName = (String)httpUtils.parameters.get("categoryName");
        String timeLength = (String)httpUtils.parameters.get("timeLength");

        List lstParam = new ArrayList();

        if(timeLength != null && !timeLength.trim().isEmpty()) lstParam.add(Integer.parseInt(timeLength));
        else lstParam.add(null);   
                
        lstParam.add(categoryName);
                
        (new CategoryDB()).insertCategory(lstParam);
        
        HttpSession.getInstance().setCacheAttribute("cache_category".getBytes(), (new CategoryDB()).getAllCategory());
        
        returnAjax(); 
    }
        
    public void backListCategory() throws IOException, ParseException, SQLException {
        File resultFile = new File("web/app/category/listCategory.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
        this.returnData.put("page", contentPage);
        returnAjax();
    }   
    
}
