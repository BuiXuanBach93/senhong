/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.web;

import com.hh.action.BaseAction;
import com.hh.database.C3p0Connector;
import com.hh.web.HttpSession;
import com.hh.web.HttpUtils;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author agiletech
 */
public class PriceAction extends BaseAction{

    public PriceAction(HttpUtils hu) {
        super(hu);
    }
    
    public void listPrice() throws IOException {
        returnPage("web/app/price/listPrice.html");
    }
    
    public void viewPrice() throws IOException, SQLException {
        String sql = " select * from price ";
        List<Map> lstPrice = C3p0Connector.getInstance().queryData(sql);
        if(lstPrice != null)
        for(int i = 0; i < lstPrice.size(); i++) {
            this.returnData.put(lstPrice.get(i).get("code"), lstPrice.get(i).get("price"));
        }
        returnAjax();
    }
    
    public void updatePrice() throws IOException, SQLException {
        String sql = " delete from price ";
        C3p0Connector.getInstance().executeData(sql);

        String insertQuery = " insert into price (code, price) values (?,?)";
        
        List lstBatch = buildParam(new ArrayList(), "day", "1");
        lstBatch = buildParam(lstBatch, "day", "3");
        lstBatch = buildParam(lstBatch, "day", "6");
        lstBatch = buildParam(lstBatch, "day", "12");        
        
        lstBatch = buildParam(lstBatch, "night", "1");
        lstBatch = buildParam(lstBatch, "night", "3");
        lstBatch = buildParam(lstBatch, "night", "6");
        lstBatch = buildParam(lstBatch, "night", "12");
        
        C3p0Connector.getInstance().executeDataBatch(insertQuery, lstBatch);
        
        String sqlCache = " select * from price ";
        List<Map> lstPrice = C3p0Connector.getInstance().queryData(sqlCache);            
        HttpSession.getInstance().setCacheAttribute("cache_price".getBytes(), lstPrice);
            
        returnAjax();        
    }
    
    private List buildParam(List lstBatch, String time, String month) {
        for(int i = 1; i <= 7; i++) {
            List lstParam = new ArrayList();
            lstParam.add(time + "_" + month + "_" + i);
            lstParam.add(httpUtils.getParameter(time + "_" + month + "_" + i));
            lstBatch.add(lstParam);
        }
        return lstBatch;
    }
}
