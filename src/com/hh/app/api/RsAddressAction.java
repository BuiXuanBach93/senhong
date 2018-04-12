/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.api;

import com.hh.app.db.AddressDB;
import com.hh.app.db.UserDB;
import com.hh.web.HttpUtils;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

/**
 *
 * @author buixu
 */
public class RsAddressAction extends RsBaseAction{

    public RsAddressAction(HttpUtils hu) {
        super(hu);
    }
    
    public void getAddressById() throws IOException, SQLException {
        String addressId = (String)httpUtils.getParameter("addressId");
        if(addressId != null && !addressId.trim().isEmpty()) {
            this.returnData = (HashMap)(new AddressDB()).getAddressById(Integer.parseInt(addressId));
        }
        returnAjax();
    }
    
}
