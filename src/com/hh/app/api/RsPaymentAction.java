/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.api;

import com.hh.app.db.PaymentDB;
import com.hh.web.HttpUtils;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author buixu
 */
public class RsPaymentAction extends RsBaseAction{
    
    public RsPaymentAction(HttpUtils hu) {
        super(hu);
    }
    
    public void getPaymentMethod() throws SQLException, IOException{
        PaymentDB paymentDB = new PaymentDB();
        List<Map> payments = paymentDB.getPayments();
        this.returnData.put("data", payments);
        returnAjax();
    }
}
