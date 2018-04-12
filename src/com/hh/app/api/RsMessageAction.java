/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.api;

import com.google.gson.Gson;
import com.hh.database.DatabaseConnector;
import com.hh.util.EncryptDecryptUtils;
import com.hh.web.HttpSession;
import com.hh.web.HttpUtils;
import com.hh.websocket.IWebsocketConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author agiletech
 */
public class RsMessageAction extends RsBaseAction{

    public RsMessageAction(HttpUtils hu) {
        super(hu);
    }    
    
    public static String sendRestMessage(String requestUrl, String payload, String key, String senderId, String receiverId, String content) {
        StringBuffer jsonString = new StringBuffer();
        try {
            URL url = new URL(requestUrl);
            //Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.61.11.38", 3128));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Authorization", key);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(payload);
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                    jsonString.append(line);
            }
            br.close();
            connection.disconnect();
            
            List lstParam = new ArrayList();
            Integer intSenderId = null;
            if(senderId != null && !senderId.trim().isEmpty()) {
                intSenderId = Integer.parseInt(senderId);
            }
            lstParam.add(intSenderId);
            
            Integer intReceiverId = null;
            if(receiverId != null && !receiverId.trim().isEmpty()) {
                intReceiverId = Integer.parseInt(receiverId);
            }
            lstParam.add(intReceiverId);
            
            lstParam.add(content);
            lstParam.add(new Date());
            
            /*IWebsocketConnection wc = (IWebsocketConnection)HttpSession.getInstance().getCacheAttribute(("ws_id" + intReceiverId).getBytes());
            if(wc != null) {
                HashMap jsonData = new HashMap();
                HashMap sender = (HashMap)HttpSession.getInstance().getCacheAttribute(("ssouser_id" + (new EncryptDecryptUtils()).base64Encode(senderId)).getBytes());
                HashMap receiver = (HashMap)HttpSession.getInstance().getCacheAttribute(("ssouser_id" + (new EncryptDecryptUtils()).base64Encode(senderId)).getBytes());
                jsonData.put("sender_id",intSenderId);
                jsonData.put("sender_name",sender.get("name"));
                jsonData.put("sender_picture",sender.get("picture"));
                jsonData.put("receiver_id",intReceiverId);
                jsonData.put("receiver_name",receiver.get("name"));
                jsonData.put("receiver_picture",receiver.get("picture"));
                jsonData.put("content", content);
                jsonData.put("send_date", new Date());
                jsonData.put("status", 1);
                wc.send((new Gson()).toJson(jsonData));
            }*/
            
            DatabaseConnector.getInstance().executeData("INSERT INTO message (sender_id, receiver_id, content, send_date, status) values (?,?,?,?,1) ", lstParam);
            
        } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
        }
        return jsonString.toString();
    }
    
    public void sendMessage() throws IOException {
        String payload = (String)httpUtils.getParameter("payload");
        String requestUrl = (String)httpUtils.getParameter("url");
        String key = (String)httpUtils.getParameter("authorization");
        sendRestMessage(requestUrl, payload, key, "", "", "");  
        returnData.put("response_message", "Gửi tin nhắn thành công");
        returnAjax();
    }
    
    public void getMessageByUserId() throws IOException, SQLException {
        String userId = (String)httpUtils.getParameter("userId");
        List lstParam = new ArrayList();
        lstParam.add(Integer.parseInt(userId.trim()));
        List<Map> lstMessage = DatabaseConnector.getInstance().queryData(
                " SELECT a.sender_id, b.name as sender_name, b.picture as sender_picture, a.receiver_id, c.name as receiver_name, c.picture as receiver_picture, a.content, "
                + "DATE_FORMAT(a.send_date, '%d-%m-%Y %H:%i:%s') as send_date, a.status "
                + " FROM message a  "
                + " LEFT JOIN sm_user b on a.sender_id = b.user_id " 
                + " LEFT JOIN sm_user c on a.receiver_id = c.user_id " 
                + " WHERE a.receiver_id = ? ORDER BY send_date desc LIMIT 100 "
                , lstParam);        
        returnData.put("data", lstMessage);
        returnAjax();
    }
}
