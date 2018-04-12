/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.api;

import com.hh.app.db.OrderDB;
import com.hh.app.db.PlanDB;
import com.hh.app.db.ReceiptDB;
import com.hh.app.db.UserDB;
import com.hh.web.HttpSession;
import com.hh.web.HttpUtils;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author buixu
 */
public class RsReceiptAction extends RsBaseAction{
    
    public RsReceiptAction(HttpUtils hu) {
        super(hu);
    }
    
    public void customerPayForPlan() throws SQLException, IOException{
         String planId = (String)httpUtils.getParameter("planId");
         String paymentId = (String)httpUtils.getParameter("paymentId");
         String giftCode = (String)httpUtils.getParameter("giftCode");
         
         if(StringUtils.isEmpty(planId)){
            returnData.put("error_code", "CustomerPayment");
            returnData.put("error_message", "planId missing!");
            returnAjax();
         }
         if(StringUtils.isEmpty(paymentId)){
            returnData.put("error_code", "CustomerPayment");
            returnData.put("error_message", "paymentId missing!");
            returnAjax();
         }
         
         Map planObj = (Map)HttpSession.getInstance().getStoreAttribute("cache_plan", planId);
         int payerId = Integer.parseInt(planObj.get("customer_id").toString());

         String payerName = planObj.get("customer_name").toString();
         String payerMobile = planObj.get("customer_mobile").toString();

         int amount = Integer.parseInt(planObj.get("price").toString());
         
         // init parmas
         List lstParam = new ArrayList();
         lstParam.add(1); // receiver_id : default for senhong
         lstParam.add(payerId); // payer_id = customer_id
         lstParam.add("Sen Hồng"); // reciever_name
         lstParam.add(""); // receiever_mobile
         lstParam.add(payerName);
         lstParam.add(payerMobile);
         lstParam.add(new Date()); // pay_date
         lstParam.add(amount);
         lstParam.add("");
         lstParam.add(1); // fund_id default
         lstParam.add(1); // status
         lstParam.add(Integer.parseInt(planId));
         lstParam.add(Integer.parseInt(paymentId));
         lstParam.add(giftCode);
         
         // insert receipt record
         (new ReceiptDB()).insertCustomerPayReceipt(lstParam);
         
        returnData.put("response_message", "Thanh toán thành công");
        returnAjax();
    }
}
