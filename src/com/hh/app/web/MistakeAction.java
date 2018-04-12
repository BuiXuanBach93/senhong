/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.web;

import com.hh.action.BaseAction;
import com.hh.app.db.MistakeDB;
import com.hh.app.db.MistakeDB;
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
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author buixu
 */
public class MistakeAction extends BaseAction{
    
    public MistakeAction(HttpUtils hu) {
        super(hu);
    }
    public void listMistake() throws IOException {
        returnPage("web/app/mistake/listMistake.html");
    }
    
    public void viewAddMistake() throws IOException, SQLException {
        File resultFile = new File("web/app/mistake/viewAddMistake.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);
        this.returnData.put("page", contentPage);
        returnAjax();
    }    
    
     public void loadViewMistake() throws IOException, SQLException {
        String mistakeId = (String)httpUtils.getParameter("mistakeId");
        if(mistakeId != null && !mistakeId.trim().isEmpty()) {
            this.returnData.put("mistake",(HashMap)(new MistakeDB()).getMistakeById(Integer.parseInt(mistakeId)));
            File resultFile = new File("web/app/mistake/viewMistake.html").getCanonicalFile();
            String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
            this.returnData.put("page", contentPage);
        }
        returnAjax();
    }
    
    
    public void searchMistake() throws IOException, SQLException, ParseException {
        MistakeDB mistakeDB = new MistakeDB();
        if(httpUtils.getParameter("isdelete") != null && httpUtils.getParameter("isdelete").equals("1")) {
            String deleteMistakes = (String)httpUtils.getParameter("mistakeId");
            if(deleteMistakes != null) {
                deleteMistakes = deleteMistakes.replace("mistakeId=", "");
                deleteMistakes = deleteMistakes.replace("&", ",");
                mistakeDB.deleteMistake(deleteMistakes);
                HttpSession.getInstance().setCacheAttribute("bm_mistake".getBytes(), (new MistakeDB()).getAllMistake());
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
        
        String name = null;
        if(httpUtils.getParameter("name") != null)
            name = (String)httpUtils.getParameter("name");
        
        
        List<List> listResult = mistakeDB.searchMistake(numberRow, pageLength, name);
        List<List> listData = listResult.get(1);
        for(int i = 0; i < listData.size(); i++) {
            listData.get(i).set(0, numberRow + i + 1);
        }
        Integer count = Integer.parseInt(((List<List>)listResult.get(0)).get(0).get(0).toString());
        returnData.put("mistakesTotal", count);
        returnData.put("mistakesFiltered", count);        
        returnData.put("data", listData);
        returnAjax();        
    }
        
    public void addMistake() throws IOException, SQLException, ParseException {
        String name = (String)httpUtils.parameters.get("name");
        String description = (String)httpUtils.parameters.get("description");
        String amount = (String)httpUtils.parameters.get("peralty_amount");

        List lstParam = new ArrayList();
        
        if(name != null && !name.trim().isEmpty()) lstParam.add(name);
        else lstParam.add(null);   
        
        if(description != null && !description.trim().isEmpty()) lstParam.add(description);
        else lstParam.add(null);
        
        if(StringUtils.isNotEmpty(amount)) lstParam.add(Integer.parseInt(amount));
        else lstParam.add(null);
                
        (new MistakeDB()).insertMistake(lstParam);
        
        HttpSession.getInstance().setCacheAttribute("bm_mistake".getBytes(), (new MistakeDB()).getAllMistake());
        
        returnAjax(); 
    }
        
   public void updateMistake() throws IOException, ParseException, SQLException {
        String mistakeId = (String)httpUtils.getParameter("mistakeid");
        
        String mistakeName = (String)httpUtils.parameters.get("mistakename");
        String amount = (String)httpUtils.parameters.get("amount");
        String description = (String)httpUtils.parameters.get("description");
        
        List lstParam = new ArrayList();
        
        if(mistakeName != null) lstParam.add(mistakeName.trim());
        else lstParam.add(null);
        
        if(amount != null) lstParam.add(Integer.parseInt(amount.trim()));
        else lstParam.add(null);

        if(description != null) lstParam.add(description.trim());
        else lstParam.add(null);
        
        if(mistakeId != null && !mistakeId.trim().isEmpty()) {
            lstParam.add(Integer.parseInt(mistakeId));
        }
        else lstParam.add(null);        
        
        (new MistakeDB()).updateMistake(lstParam);
        
        Map mistake = (new MistakeDB()).getMistakeById(Integer.parseInt(mistakeId));
        
        HttpSession.getInstance().setCacheAttribute("bm_mistake".getBytes(), (new MistakeDB()).getAllMistake());
        this.returnData.put("data", mistake);
        returnAjax(); 
    }
            
    public void backListMistake() throws IOException, ParseException, SQLException {
        File resultFile = new File("web/app/mistake/listMistake.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
        this.returnData.put("page", contentPage);
        returnAjax();
    }   
}
