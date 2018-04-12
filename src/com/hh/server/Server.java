/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.server;

import com.hh.app.AuthenticateFilter;
import com.hh.app.api.RsOrderAction;
import com.hh.app.db.OrderDB;
import com.hh.app.web.PolicyAction;
import com.hh.app.websocket.WebsocketConnection;
import com.hh.database.C3p0Connector;
import com.hh.database.DatabaseConnector;
import com.hh.sso.LoginAction;
import com.hh.web.HttpSession;
import com.hh.web.RedisSession;
import java.util.Calendar;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author agiletech
 */
public class Server {
    public static final Logger mainLogger = LoggerFactory.getLogger(Server.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            HHServer server = new HHServer();
            HttpSession.setConnector(RedisSession.getInstance());
            DatabaseConnector.setConnector(C3p0Connector.getInstance());
            
            server.setFilter(AuthenticateFilter.class)
                    .setWebsocketConnection(WebsocketConnection.class);            
            
            PolicyAction.loadCacheData();            
            LoginAction.loadUserFromDatabase();            
            
            server.start();
            
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);

            Timer timer = new Timer();
            timer.schedule(new ResetOrderTask(), today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));

            Timer remindTimer = new Timer();
            remindTimer.schedule(new RemindTask(), today.getTime(), TimeUnit.MILLISECONDS.convert(30, TimeUnit.MINUTES));
            
            RsOrderAction.orderCount = new AtomicInteger((new OrderDB()).countOrderInDay());
            
        } catch(Exception ex) {
            mainLogger.error("HHServer error: ", ex);
        }
    }     
}
