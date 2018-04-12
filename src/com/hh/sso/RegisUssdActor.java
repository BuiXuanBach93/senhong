package com.hh.sso;

import akka.actor.UntypedActor;
import com.hh.server.HHServer;
import com.hh.web.HttpSession;

public class RegisUssdActor extends UntypedActor {
    private String otp;
    private String appCode;
    private String phoneNumber;
    
    public RegisUssdActor(String otp, String appCode, String phoneNumber) {
        this.otp = otp;
        this.appCode = appCode;
        this.phoneNumber = phoneNumber;
    }    
    
    @Override
    public void onReceive(Object message) {
        try {
            HttpSession.getInstance().setCacheAttribute("regisotp_" + otp, "true");
        } catch(Exception ex) {
            HHServer.mainLogger.error("HHServer error: ", ex);
        }
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    } 
}
