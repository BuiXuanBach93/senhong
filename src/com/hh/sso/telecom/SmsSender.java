package com.hh.sso.telecom;

import com.hh.util.ResourceBundleUtils;
import sendmt.MtStub;

public class SmsSender {
    static final String DEFAULT_SENDER="316";
    public static void send(String msisdn, String confirmContent) {

        String url=ResourceBundleUtils.getConfig("SmsUrl");
        String xmls=ResourceBundleUtils.getConfig("SmsXmls");
        String username=ResourceBundleUtils.getConfig("SmsUser");
        String password=ResourceBundleUtils.getConfig("SmsPassword");

        MtStub stub = new MtStub(url, xmls, username, password);

        // (sessionid, receiverid,
        stub.send("0", "0", DEFAULT_SENDER, msisdn, "0", confirmContent, "0");
    }
}
