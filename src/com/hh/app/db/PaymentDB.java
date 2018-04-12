/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.db;

import com.hh.database.DatabaseConnector;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author buixu
 */
public class PaymentDB {
    public List<Map> getPayments() throws SQLException {
        List<Map> lstPayment = DatabaseConnector.getInstance().queryData("select * from payment where is_active = 1");
        return lstPayment;
    }
}
