/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.server;

import com.hh.app.api.RsOrderAction;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Ha
 */
public class ResetOrderTask extends TimerTask  {

    @Override
    public void run() {
        RsOrderAction.orderCount = new AtomicInteger(0);
    }
    
}
