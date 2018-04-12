/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.web;

import com.google.gson.Gson;
import com.hh.action.BaseAction;
import com.hh.app.db.AddressDB;
import com.hh.app.db.MaidDB;
import com.hh.app.db.UserDB;
import com.hh.net.httpserver.Headers;
import com.hh.util.EncryptDecryptUtils;
import com.hh.util.FileUtils;
import com.hh.util.ResourceBundleUtils;
import com.hh.web.FileInfo;
import com.hh.web.HttpSession;
import com.hh.web.HttpUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author agiletech
 */
public class UserAction extends BaseAction{

    public UserAction(HttpUtils hu) {
        super(hu);
    }
    
    public void listUser() throws IOException {
        returnPage("web/app/user/listUser.html");
    }
    
    public void uploadUserPic() throws IOException {
        if(httpUtils.parameters.get("file") != null) {
            String filePath = ResourceBundleUtils.getConfig("DataDirectory") + File.separator + "user";
            if(!filePath.isEmpty()) {
                filePath = filePath.replace("/", File.separator);
                filePath = filePath.replace("\\", File.separator);
                Calendar c = Calendar.getInstance();
                filePath = filePath + File.separator + c.get(Calendar.YEAR) + File.separator + 
                        (c.get(Calendar.MONTH)+1) + File.separator + c.get(Calendar.DAY_OF_MONTH) + 
                        File.separator;
                File directory = new File(filePath);
                if(!directory.exists()) directory.mkdirs();
                
                FileInfo fileInfo = (FileInfo)httpUtils.parameters.get("file");
                String fileName = "" + (new Date()).getTime() + "_" + UUID.randomUUID().toString() + FileUtils.extractFileExt(fileInfo.getFilename());
                HashMap data = new HashMap();
                data.put("localFileName", fileInfo.getFilename());
                data.put("serverFileName", fileName);
                
                FileOutputStream fios = new FileOutputStream(filePath + fileName);
                fios.write(fileInfo.getBytes());
                fios.close();
                
                String json = new Gson().toJson(data);
                httpUtils.sendStringResponse(200, json);
            }
        } else {
            httpUtils.sendNotFoundResponse();
        }
    }
    
    public void uploadUserFile() throws IOException {
        if(httpUtils.parameters.get("file") != null) {
            String filePath = ResourceBundleUtils.getConfig("DataDirectory") + File.separator + "userfile";
            if(!filePath.isEmpty()) {
                filePath = filePath.replace("/", File.separator);
                filePath = filePath.replace("\\", File.separator);
                Calendar c = Calendar.getInstance();
                filePath = filePath + File.separator + c.get(Calendar.YEAR) + File.separator + 
                        (c.get(Calendar.MONTH)+1) + File.separator + c.get(Calendar.DAY_OF_MONTH) + 
                        File.separator;
                File directory = new File(filePath);
                if(!directory.exists()) directory.mkdirs();
                
                FileInfo fileInfo = (FileInfo)httpUtils.parameters.get("file");
                String fileName = "" + (new Date()).getTime() + "_" + UUID.randomUUID().toString() + FileUtils.extractFileExt(fileInfo.getFilename());
                HashMap data = new HashMap();
                data.put("localFileName", fileInfo.getFilename());
                data.put("serverFileName", fileName);
                
                FileOutputStream fios = new FileOutputStream(filePath + fileName);
                fios.write(fileInfo.getBytes());
                fios.close();
                
                String json = new Gson().toJson(data);
                httpUtils.sendStringResponse(200, json);
            }
        } else {
            httpUtils.sendNotFoundResponse();
        }
    }    
    
    public void viewAddUser() throws IOException {
        File resultFile = new File("web/app/user/viewAddUser.html").getCanonicalFile();
        String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
        this.returnData.put("page", contentPage);
        returnAjax();
    }    
    
    public void searchPopup() throws IOException, SQLException, ParseException {
        UserDB udb = new UserDB();
        
        int pageLength = 10;
        if(httpUtils.getParameter("length") != null) {
            pageLength = Integer.parseInt((String)httpUtils.getParameter("length"));
            if(pageLength == 0) pageLength = 10;
        }
        
        int numberRow = 0;
        if(httpUtils.getParameter("start") != null) {
            numberRow = Integer.parseInt((String)httpUtils.getParameter("start"));
        }

        String name = (String)httpUtils.getParameter("username");
        String mobile = (String)httpUtils.getParameter("mobile");
        String home = (String)httpUtils.getParameter("home");
        String fromdate = (String)httpUtils.parameters.get("fromdate");
        String todate = (String)httpUtils.parameters.get("todate");
        
        Integer userType = null;
        if(httpUtils.getParameter("usertype") != null)
            userType = Integer.parseInt((String)httpUtils.getParameter("usertype"));
        
        if(!(name != null && !name.trim().isEmpty())) name = null;
        if(!(mobile != null && !mobile.trim().isEmpty())) mobile = null;
        if(!(home != null && !home.trim().isEmpty())) home = null;
        Date fromDate = null;
        if(fromdate != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            fromDate = df.parse(fromdate.trim() + " 00:00:00");
        }
        Date toDate = null;
        if(todate != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            toDate = df.parse(todate.trim() + " 23:59:59");
        }
        
        List<List> listResult = udb.searchPopup(numberRow, pageLength, name, mobile, home, userType, fromDate, toDate);
        List<List> listData = listResult.get(1);
        for(int i = 0; i < listData.size(); i++) {
            listData.get(i).set(0, numberRow + i + 1);
        }
        Integer count = Integer.parseInt(((List<List>)listResult.get(0)).get(0).get(0).toString());
        returnData.put("recordsTotal", count);
        returnData.put("recordsFiltered", count);        
        returnData.put("data", listData);
        returnAjax();        
    }
    
    public void searchUser() throws IOException, SQLException, ParseException {
        UserDB udb = new UserDB();
        if(httpUtils.getParameter("isdelete") != null && httpUtils.getParameter("isdelete").equals("1")) {
            String deleteUsers = (String)httpUtils.getParameter("userid");
            if(deleteUsers != null) {
                deleteUsers = deleteUsers.replace("userid=", "");
                deleteUsers = deleteUsers.replace("&", ",");
                udb.deleteUser(deleteUsers);
                HttpSession.getInstance().setCacheAttribute("cache_maid".getBytes(), (new MaidDB()).getAllMaid());
                
                String[] arrUserId = deleteUsers.split(",");
                for(int i = 0; i < arrUserId.length; i++) {
                    HttpSession.getInstance().removeCacheAttribute(("ssouser_id" + (new EncryptDecryptUtils()).base64Encode(arrUserId[i])).getBytes());        
                }
            }
        }        
        
        int pageLength = 10;
        if(httpUtils.getParameter("length") != null) {
            pageLength = Integer.parseInt((String)httpUtils.getParameter("length"));
            if(pageLength == 0) pageLength = 10;
        }
        
        int numberRow = 0;
        if(httpUtils.getParameter("start") != null) {
            numberRow = Integer.parseInt((String)httpUtils.getParameter("start"));
        }

        String name = (String)httpUtils.getParameter("username");
        String mobile = (String)httpUtils.getParameter("mobile");
        String home = (String)httpUtils.getParameter("home");
        String fromdate = (String)httpUtils.parameters.get("fromdate");
        String todate = (String)httpUtils.parameters.get("todate");
        String sortAsc = (String)httpUtils.parameters.get("sortasc");
        
        Integer userType = null;
        if(httpUtils.getParameter("usertype") != null)
            userType = Integer.parseInt((String)httpUtils.getParameter("usertype"));
        
        if(!(name != null && !name.trim().isEmpty())) name = null;
        if(!(mobile != null && !mobile.trim().isEmpty())) mobile = null;
        if(!(home != null && !home.trim().isEmpty())) home = null;
        Date fromDate = null;
        if(fromdate != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            fromDate = df.parse(fromdate.trim() + " 00:00:00");
        }
        Date toDate = null;
        if(todate != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            toDate = df.parse(todate.trim() + " 23:59:59");
        }
        
        List<List> listResult = udb.searchUser(numberRow, pageLength, name, mobile, home, userType, fromDate, toDate, sortAsc);
        List<List> listData = listResult.get(1);
        for(int i = 0; i < listData.size(); i++) {
            listData.get(i).set(0, numberRow + i + 1);
        }
        Integer count = Integer.parseInt(((List<List>)listResult.get(0)).get(0).get(0).toString());
        returnData.put("recordsTotal", count);
        returnData.put("recordsFiltered", count);        
        returnData.put("data", listData);
        returnAjax();        
    }
    
    public void searchUserMap() throws IOException, SQLException, ParseException {
        UserDB udb = new UserDB();
        
        int pageLength = 10;
        if(httpUtils.getParameter("length") != null) {
            pageLength = Integer.parseInt((String)httpUtils.getParameter("length"));
            if(pageLength == 0) pageLength = 10;
        }
        
        int numberRow = 0;
        if(httpUtils.getParameter("start") != null) {
            numberRow = Integer.parseInt((String)httpUtils.getParameter("start"));
        }

        String name = (String)httpUtils.getParameter("username");
        String mobile = (String)httpUtils.getParameter("mobile");
        String home = (String)httpUtils.getParameter("home");
        String fromdate = (String)httpUtils.parameters.get("fromdate");
        String todate = (String)httpUtils.parameters.get("todate");
        
        Integer userType = null;
        if(httpUtils.getParameter("usertype") != null)
            userType = Integer.parseInt((String)httpUtils.getParameter("usertype"));
        
        if(!(name != null && !name.trim().isEmpty())) name = null;
        if(!(mobile != null && !mobile.trim().isEmpty())) mobile = null;
        if(!(home != null && !home.trim().isEmpty())) home = null;
        Date fromDate = null;
        if(fromdate != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            fromDate = df.parse(fromdate.trim() + " 00:00:00");
        }
        Date toDate = null;
        if(todate != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            toDate = df.parse(todate.trim() + " 23:59:59");
        }
        
        List<List> listResult = udb.searchUserMap(numberRow, pageLength, name, mobile, home, userType, fromDate, toDate);
        List<List> listData = listResult.get(1);
        for(int i = 0; i < listData.size(); i++) {
            listData.get(i).set(0, numberRow + i + 1);
        }
        Integer count = Integer.parseInt(((List<List>)listResult.get(0)).get(0).get(0).toString());
        returnData.put("recordsTotal", count);
        returnData.put("recordsFiltered", count);        
        returnData.put("data", listData);
        returnAjax();        
    }
    
    public void removeFile() throws IOException, SQLException {
        String fileName = (String)httpUtils.parameters.get("fileName");
        String[] arrData = fileName.split("_");
        long createTime = Long.parseLong(arrData[0]);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(createTime));
        String filePath = ResourceBundleUtils.getConfig("DataDirectory") + File.separator + "user";
        filePath = filePath.replace("/", File.separator);
        filePath = filePath.replace("\\", File.separator);                
        filePath = filePath + File.separator + c.get(Calendar.YEAR) + File.separator + 
                (c.get(Calendar.MONTH)+1) + File.separator + c.get(Calendar.DAY_OF_MONTH) + 
                File.separator;
        File deleteFile = new File(filePath + fileName);
        if(deleteFile.exists()) {
            if(httpUtils.parameters.get("userid") != null && 
                    !httpUtils.parameters.get("userid").toString().trim().isEmpty()) {
                (new UserDB()).deleteUserImage(Integer.parseInt((String)httpUtils.parameters.get("userid")));
            }
            deleteFile.delete();
        }
        httpUtils.sendStringResponse(200, "ok");
    }
    
    public void addUser() throws IOException, SQLException, ParseException {
        String hoten = (String)httpUtils.parameters.get("username");
        String mobile = (String)httpUtils.parameters.get("mobile");
        String birthday = (String)httpUtils.parameters.get("birthday");
        String idnumber = (String)httpUtils.parameters.get("idnumber");
        String password = (String)httpUtils.parameters.get("password");
        String familyMobile = (String)httpUtils.parameters.get("familymobile");
        String home = (String)httpUtils.parameters.get("home");
        String userType = (String)httpUtils.parameters.get("usertype");
        String picture = (String)httpUtils.parameters.get("imagepath");
        String address = (String)httpUtils.parameters.get("address");
        String latitude = (String)httpUtils.parameters.get("latitude");
        String longitude = (String)httpUtils.parameters.get("longitude");
        String videoUrl = (String)httpUtils.parameters.get("videourl");
        String detail = (String)httpUtils.parameters.get("detail");
        String source = (String)httpUtils.parameters.get("source");

        HashMap user = new HashMap();
        
        List lstParam = new ArrayList();
        
        if(hoten != null) {
            lstParam.add(hoten.trim());
            user.put("name", hoten.trim());
        } else lstParam.add(null);
        
        if(mobile != null) {
            lstParam.add(mobile.trim());
            user.put("mobile", mobile.trim());
        } else {
            lstParam.add(null);
            returnData.put("error_code", "createuser_01");
            returnData.put("error_message", "Số điện thoại bắt buộc nhập");
            returnData.put("response_message", "Hãy nhập số điện thoại");
            returnAjax();
            return;   
        }
        
        if(birthday != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            Date birthDate = df.parse(birthday.trim());
            lstParam.add(birthDate);
            user.put("birthday", birthDate);
        }
        else lstParam.add(null);
        
        if(idnumber != null) {
            lstParam.add(idnumber.trim());
            user.put("id_number", idnumber.trim());
        }
        else lstParam.add(null);
        
        if(password != null) {
            String passwordEncode = (new EncryptDecryptUtils().encodePassword(password.trim()));
            lstParam.add(passwordEncode);
            user.put("password", passwordEncode);
        }
        else {
            lstParam.add(null);
            returnData.put("error_code", "createuser_02");
            returnData.put("error_message", "Mật khẩu bắt buộc nhập");
            returnData.put("response_message", "Hãy nhập mật khẩu");
            returnAjax();
            return;   
        }
        
        if(familyMobile != null) {
            lstParam.add(familyMobile.trim());
            user.put("family_mobile", familyMobile.trim());
        }
        else lstParam.add(null);
        
        if(videoUrl != null) {
            lstParam.add(videoUrl.trim());
            user.put("video_url", videoUrl.trim());
        }
        else lstParam.add(null);        
        
        if(detail != null) {
            lstParam.add(detail.trim());
            user.put("detail", detail.trim());
        }
        else lstParam.add(null);                

        if(home != null) {
            lstParam.add(home.trim());
            user.put("home_province", home.trim());
        }
        else lstParam.add(null);
        
        if(address != null) {
            lstParam.add(address.trim());
            user.put("address", address.trim());
        }
        else lstParam.add(null);     
        
        Double doubleLatitude = null;
        if(latitude != null && !latitude.trim().isEmpty()) {
            doubleLatitude = Double.parseDouble(latitude);
            lstParam.add(doubleLatitude);
            user.put("latitude", doubleLatitude);
        } else lstParam.add(null);
        
        Double doubleLongitude = null;
        if(longitude != null && !longitude.trim().isEmpty()) {
            doubleLongitude = Double.parseDouble(longitude);
            lstParam.add(doubleLongitude);
            user.put("longitude", doubleLongitude);
        } else lstParam.add(null);
        
        Integer intUserType = null;
        if(userType != null && !userType.trim().isEmpty()) {
            intUserType = Integer.parseInt(userType);
            lstParam.add(intUserType);
            user.put("user_type", userType);
        }
        else {
            lstParam.add(null);
            returnData.put("error_code", "createuser_03");
            returnData.put("error_message", "Phân quyền bắt buộc nhập");
            returnData.put("response_message", "Hãy nhập quyền người dùng");
            returnAjax();
            return;               
        }        
        
        if(picture != null) {
            String fileName = picture.trim();
            if(fileName.contains("-")) {
                String[] arrData = fileName.split("_");
                lstParam.add(fileName);
                user.put("picture", fileName);
            }
        } else  lstParam.add(null);
        
        lstParam.add(new Date());
        user.put("create_date", new Date());
        
        if(StringUtils.isNotEmpty(source)){
            int intSource = Integer.parseInt(source);
            if(intUserType.intValue() != 4 && intSource == 1){
                returnData.put("error_code", "createuser_05");
                returnData.put("error_message", "Chỉ được tạo role khách hàng từ web");
                returnData.put("response_message", "Invalid Role");
                returnAjax();
                return;  
            }
        }
        
        Map countMap =  (new UserDB().countUserEnableByMobile(mobile));
        if(countMap != null && Integer.parseInt(countMap.get("user_number").toString()) > 0){
             returnData.put("error_code", "createuser_04");
                returnData.put("error_message", "Số điện thoại đã tồn tại");
                returnData.put("response_message", "Số điện thoại đã tồn tại");
                returnAjax();
                return;    
        }
        
        Integer userId = null;
        try {
            userId = (new UserDB()).insertUser(lstParam);
        } catch(Exception ex) {
            if(ex.getMessage().contains("Duplicate entry")) {
                returnData.put("error_code", "createuser_04");
                returnData.put("error_message", "Số điện thoại đã tồn tại");
                returnData.put("response_message", "Số điện thoại đã tồn tại");
                returnAjax();
                return;                
            }
        }
        // update source for user 0 = app, 1 = web
        List listPrSource = new ArrayList();
        if(StringUtils.isNotEmpty(source)){
            int roleInt = Integer.parseInt(source);
            listPrSource.add(roleInt);
            
        }else{
            listPrSource.add(0);
        }
        listPrSource.add(userId);
        (new UserDB()).updateUserSource(listPrSource);
        
        user.put("user_id", userId);
        
        if(doubleLatitude != null && doubleLongitude != null) {
            List lstAddress = new ArrayList();
            lstAddress.add(address);
            lstAddress.add(home);
            lstAddress.add(doubleLatitude);
            lstAddress.add(doubleLongitude);
            lstAddress.add(userId);
            (new AddressDB()).insertAddress(lstAddress);        
        }        
        
        if(intUserType == 3) HttpSession.getInstance().setCacheAttribute("cache_maid".getBytes(), (new MaidDB()).getAllMaid());
        
        EncryptDecryptUtils edu = new EncryptDecryptUtils();
        HttpSession.getInstance().setCacheAttribute(("ssouser_" + edu.base64Encode(mobile)).getBytes(), user);
        HttpSession.getInstance().setCacheAttribute(("ssouser_id" + edu.base64Encode(user.get("user_id").toString())).getBytes(), user);        
        
        returnAjax(); 
    }
    
    public void loadViewUser() throws IOException, SQLException {
        String userId = (String)httpUtils.getParameter("userid");
        if(userId != null && !userId.trim().isEmpty()) {
            this.returnData.put("user", HttpSession.getInstance().getCacheAttribute(("ssouser_id" + (new EncryptDecryptUtils()).base64Encode(userId)).getBytes()));
            File resultFile = new File("web/app/user/viewUser.html").getCanonicalFile();
            String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
            this.returnData.put("page", contentPage);
        }
        returnAjax();
    }
    
    public void getMyInfo() throws IOException, SQLException {
        HashMap user = (HashMap) httpUtils.getSessionAttribute("sso_username");
        if(user != null) {
            this.returnData.put("user",user);
        }
        returnAjax();
    }
    
    public void myInfo() throws IOException, SQLException {
        returnPage("web/app/user/myInfo.html");
    }
    
    public void downloadUserFile() throws IOException {
        Headers resHeader = httpUtils.httpExchange.getResponseHeaders();
        resHeader.set("Content-Type", "application/octet-stream");
        String fileName = (String)httpUtils.getParameter("filename");
        resHeader.set("Content-Disposition", "attachment; filename=\"user-file" + FileUtils.extractFileExt(fileName) + "\"");        
        if(fileName == null) {
            httpUtils.sendNotFoundResponse();
            return;
        }
        String filePath = "";
        if(fileName.contains("_")) {
            String[] arrData = fileName.split("_");
            long createTime = Long.parseLong(arrData[0]);
            Calendar c = Calendar.getInstance();
            c.setTime(new Date(createTime));
            filePath = ResourceBundleUtils.getConfig("DataDirectory") + File.separator + "userfile";
            filePath = filePath.replace("/", File.separator);
            filePath = filePath.replace("\\", File.separator);                
            filePath = filePath + File.separator + c.get(Calendar.YEAR) + File.separator + 
                    (c.get(Calendar.MONTH)+1) + File.separator + c.get(Calendar.DAY_OF_MONTH) + 
                    File.separator;
        }
        File tmpFile = new File(filePath + fileName);
        if(!tmpFile.exists()) {
            httpUtils.sendNotFoundResponse();
            return;
        }
        InputStream is = new FileInputStream(tmpFile);
        httpUtils.httpExchange.sendResponseHeaders(200, 0);
        OutputStream os = httpUtils.httpExchange.getResponseBody();
        IOUtils.copy(is, os);
        is.close();
        os.close();        
    }
    
    public void viewImage() throws IOException {
        Headers resHeader = httpUtils.httpExchange.getResponseHeaders();
        resHeader.set("Content-Type", "image/jpeg");
        String fileName = (String)httpUtils.getParameter("filename");
        if(fileName == null) {
            httpUtils.sendNotFoundResponse();
            return;
        }
        String filePath = "";
        if(fileName.contains("_")) {
            String[] arrData = fileName.split("_");
            long createTime = Long.parseLong(arrData[0]);
            Calendar c = Calendar.getInstance();
            c.setTime(new Date(createTime));
            filePath = ResourceBundleUtils.getConfig("DataDirectory") + File.separator + "user";
            filePath = filePath.replace("/", File.separator);
            filePath = filePath.replace("\\", File.separator);                
            filePath = filePath + File.separator + c.get(Calendar.YEAR) + File.separator + 
                    (c.get(Calendar.MONTH)+1) + File.separator + c.get(Calendar.DAY_OF_MONTH) + 
                    File.separator;
        }
        File tmpFile = new File(filePath + fileName);
        if(!tmpFile.exists()) tmpFile = new File(System.getProperty("user.dir") + File.separator +
                "share" + File.separator + "assets" + File.separator + 
                "images" + File.separator + "users" + File.separator + "no-image.jpg");
        InputStream is = new FileInputStream(tmpFile);
        httpUtils.httpExchange.sendResponseHeaders(200, 0);
        OutputStream os = httpUtils.httpExchange.getResponseBody();
        IOUtils.copy(is, os);
        is.close();
        os.close();
    }
    
    public void updateUser() throws IOException, ParseException, SQLException {
        //------- Update user --------------------------------------------
        String userId = (String)httpUtils.getParameter("userid");
        HashMap cacheUser = new HashMap();
        if(userId != null && !userId.trim().isEmpty()) {
            try {
                cacheUser = (HashMap)HttpSession.getInstance().getCacheAttribute(("ssouser_id" + (new EncryptDecryptUtils()).base64Encode(userId)).getBytes());
                if(cacheUser == null) {
                    returnData.put("error_code", "updateorder_02");
                    returnData.put("error_message", "userid không tồn tại");
                    returnData.put("response_message", "userid không tồn tại");
                    returnAjax();
                    return;            
                }
            } catch(Exception ex) {
                returnData.put("error_code", "updateorder_01");
                returnData.put("error_message", "Thiếu id người dùng");
                returnData.put("response_message", "Thiếu id người dùng");
                returnAjax();
                return;            
            }
        } else {
            returnData.put("error_code", "updateorder_01");
            returnData.put("error_message", "Thiếu id người dùng");
            returnData.put("response_message", "Thiếu id người dùng");
            returnAjax();
            return;   
        }
        
        String hoten = (String)httpUtils.parameters.get("username");
        String mobile = (String)httpUtils.parameters.get("mobile");
        String birthday = (String)httpUtils.parameters.get("birthday");
        String idnumber = (String)httpUtils.parameters.get("idnumber");
        String password = (String)httpUtils.parameters.get("password");
        String familyMobile = (String)httpUtils.parameters.get("familymobile");
        String home = (String)httpUtils.parameters.get("home");
        String userType = (String)httpUtils.parameters.get("usertype");
        String picture = (String)httpUtils.parameters.get("imagepath");
        String userFile = (String)httpUtils.parameters.get("filepath");
        String address = (String)httpUtils.parameters.get("address");
        String latitude = (String)httpUtils.parameters.get("latitude");
        String longitude = (String)httpUtils.parameters.get("longitude");
        String videoUrl = (String)httpUtils.parameters.get("videourl");
        String salaryLevel = (String)httpUtils.parameters.get("salarylevel");
        String amount = (String)httpUtils.parameters.get("amount");
        String detail = (String)httpUtils.parameters.get("detail");
        
        List lstParam = new ArrayList();
        
        if(hoten != null) lstParam.add(hoten.trim());
        else lstParam.add(null);
        
        if(mobile != null) lstParam.add(mobile.trim());
        else lstParam.add(null);
        
        if(birthday != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            Date birthDate = df.parse(birthday.trim());
            lstParam.add(birthDate);
        }
        else lstParam.add(null);
        
        if(idnumber != null) lstParam.add(idnumber.trim());
        else lstParam.add(null);
        
        if(familyMobile != null) lstParam.add(familyMobile.trim());
        else lstParam.add(null);

        boolean changeAddress = false;
        if(!(home != null && home.equals(cacheUser.get("home_province")))) {
            if(home != null && !home.isEmpty()) lstParam.add(home.trim());
            else lstParam.add(null);
            changeAddress = true;
        }
        else lstParam.add(home);
        
        Integer intUserType = null;
        boolean changeType = true;
        if(userType != null && !userType.trim().isEmpty()) {
            intUserType = Integer.parseInt(userType);
            lstParam.add(intUserType);
        }
        else changeType = false;  
        
        if(!(address != null && address.trim().equals(cacheUser.get("address")))) {
            lstParam.add(address.trim());
            changeAddress = true;
        }
        else lstParam.add(address);
        
        Double doubleLat = null;
        if(!(cacheUser.get("latitude") != null && cacheUser.get("latitude").toString().equals(latitude))) {
            if(latitude != null && !latitude.trim().isEmpty()) {
                doubleLat = Double.parseDouble(latitude);
                lstParam.add(doubleLat);
                changeAddress = true;
            }
        } else if(latitude != null && !latitude.trim().isEmpty())lstParam.add(Double.parseDouble(latitude));
        else lstParam.add(null);

        Double doubleLong = null;
        if(!(cacheUser.get("longitude") != null && cacheUser.get("longitude").toString().equals(longitude))) {
            if(longitude != null && !longitude.trim().isEmpty()) {
                doubleLong = Double.parseDouble(longitude);
                lstParam.add(doubleLong);
                changeAddress = true;
            }
        } else if(longitude != null && !longitude.trim().isEmpty())lstParam.add(Double.parseDouble(longitude));
        else lstParam.add(null);
                
        if(videoUrl != null) lstParam.add(videoUrl.trim());
        else lstParam.add(null);
        
        Float floatLevel = null;
        if(salaryLevel != null && !salaryLevel.trim().isEmpty()) {
            floatLevel = Float.parseFloat(salaryLevel);
            lstParam.add(floatLevel);
        }
        else lstParam.add(null);        

        if(amount != null) lstParam.add(amount.trim().replace(",",""));
        else lstParam.add(null);        

        if(detail != null) lstParam.add(detail.trim());
        else lstParam.add(null);
        
        boolean changePassword = false;
        if(password != null && !password.trim().isEmpty()) {
            lstParam.add((new EncryptDecryptUtils()).encodePassword(password.trim()));
            changePassword = true;
        }
        
        boolean deletePic = false;
        if("1".equals(httpUtils.getParameter("deletepic"))) deletePic = true;
        if(picture != null) {
            String fileName = picture.trim();
            if(fileName.contains("-")) {
                String[] arrData = fileName.split("_");
                lstParam.add(fileName);
                deletePic = true;
            }
        } else if(deletePic) {
            lstParam.add(null);
        }
        
        boolean deleteFile = false;
        if(userFile != null) {
            String fileName = userFile.trim();
            if(fileName.contains("-")) {
                String[] arrData = fileName.split("_");
                lstParam.add(fileName);
                deleteFile = true;
            }
        }        
        
        Integer intUserId = null;
        if(userId != null && !userId.trim().isEmpty()) {
            intUserId = Integer.parseInt(userId);
            lstParam.add(intUserId);
        }
        else lstParam.add(null);        
        
        try {
            (new UserDB()).updateUser(lstParam, changePassword, deletePic, deleteFile, changeType);
        } catch(Exception ex) {
            if(ex.getMessage().contains("Duplicate entry")) {
                returnData.put("error_code", "createuser_04");
                returnData.put("error_message", "Số điện thoại đã tồn tại");
                returnData.put("response_message", "Số điện thoại đã tồn tại");
                returnAjax();
                return;                
            }
        }        
        
        if(changeAddress) {
            List lstAddress = new ArrayList();
            lstAddress.add(address);
            lstAddress.add(home);
            lstAddress.add(doubleLat);
            lstAddress.add(doubleLong);
            lstAddress.add(intUserId);
            (new AddressDB()).insertAddress(lstAddress);               
        }
        
        if(intUserType != null && intUserType == 3) HttpSession.getInstance().setCacheAttribute("cache_maid".getBytes(), (new MaidDB()).getAllMaid());
        
        Map user = (new UserDB()).getUserById(intUserId);
        EncryptDecryptUtils edu = new EncryptDecryptUtils();
        
        HttpSession.getInstance().setCacheAttribute(("ssouser_" + edu.base64Encode(user.get("mobile").toString())).getBytes(), user);
        HttpSession.getInstance().setCacheAttribute(("ssouser_id" + edu.base64Encode(user.get("user_id").toString())).getBytes(), user);        
        
        this.returnData.put("data", user);
        
        if(!cacheUser.get("mobile").equals(user.get("mobile"))) {
            HttpSession.getInstance().removeCacheAttribute(("ssouser_" + edu.base64Encode(cacheUser.get("mobile").toString())).getBytes());
        }
        returnAjax(); 
    }
    
    public void backListUser() throws IOException, ParseException, SQLException {
        if("listMaid".equals(httpUtils.getParameter("parent"))) {
            File resultFile = new File("web/app/maid/listMaid.html").getCanonicalFile();
            String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
            this.returnData.put("page", contentPage);            
        } else if("listCustomer".equals(httpUtils.getParameter("parent"))) {
            File resultFile = new File("web/app/customer/listCustomer.html").getCanonicalFile();
            String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
            this.returnData.put("page", contentPage);            
        } else if("listOrder".equals(httpUtils.getParameter("parent"))) {
            File resultFile = new File("web/app/order/listOrder.html").getCanonicalFile();
            String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
            this.returnData.put("page", contentPage);            
        } else if("listPlan".equals(httpUtils.getParameter("parent"))) {
            File resultFile = new File("web/app/plan/listPlan.html").getCanonicalFile();
            String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);
            this.returnData.put("page", contentPage);
        } else if("listTask".equals(httpUtils.getParameter("parent"))) {
            File resultFile = new File("web/app/task/listTask.html").getCanonicalFile();
            String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);
            this.returnData.put("page", contentPage);
        } else if("listSalary".equals(httpUtils.getParameter("parent"))) {
            File resultFile = new File("web/app/salary/listSalary.html").getCanonicalFile();
            String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);
            this.returnData.put("page", contentPage);
        } else if("listMap".equals(httpUtils.getParameter("parent"))) {
            File resultFile = new File("web/app/maid/listMap.html").getCanonicalFile();
            String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);
            this.returnData.put("page", contentPage);
        } else if("listReceipt".equals(httpUtils.getParameter("parent"))) {
            File resultFile = new File("web/app/receipt/listReceipt.html").getCanonicalFile();
            String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);
            this.returnData.put("page", contentPage);
        } else if("listSpend".equals(httpUtils.getParameter("parent"))) {
            File resultFile = new File("web/app/spend/listSpend.html").getCanonicalFile();
            String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);
            this.returnData.put("page", contentPage);
        } else {
            File resultFile = new File("web/app/user/listUser.html").getCanonicalFile();
            String contentPage = (new FileUtils()).readFileToString(resultFile, FileUtils.UTF_8);            
            this.returnData.put("page", contentPage);
        }
        returnAjax();
    }
    
    public void listMaid() throws IOException {
        returnPage("web/app/maid/listMaid.html");
    }    
    
    public void listCustomer() throws IOException {
        returnPage("web/app/customer/listCustomer.html");
    }      
    
    public void listMap() throws IOException {
        returnPage("web/app/maid/listMap.html");
    }        
}
