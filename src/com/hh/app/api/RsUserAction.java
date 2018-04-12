/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.api;

import com.google.gson.Gson;
import com.hh.app.db.AddressDB;
import com.hh.app.db.MaidDB;
import com.hh.app.db.UserDB;
import com.hh.app.web.UserAction;
import com.hh.database.DatabaseConnector;
import com.hh.sso.LoginAction;
import com.hh.util.EncryptDecryptUtils;
import com.hh.util.FileUtils;
import com.hh.util.ResourceBundleUtils;
import com.hh.web.FileInfo;
import com.hh.web.HttpSession;
import com.hh.web.HttpUtils;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
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
import javax.imageio.ImageIO;
import sun.misc.BASE64Decoder;

/**
 *
 * @author agiletech
 */
public class RsUserAction extends RsBaseAction{

    public RsUserAction(HttpUtils hu) {
        super(hu);
    }
    
    public void getUserById() throws IOException, SQLException {
        String userId = (String)httpUtils.getParameter("userid");
        if(userId != null && !userId.trim().isEmpty()) {
            this.returnData = (HashMap)(new UserDB()).getUserById(Integer.parseInt(userId));
        }
        returnAjax();
    }
    
    public void addUser() throws IOException, SQLException, ParseException {
        UserAction ua = new UserAction(httpUtils);
        returnData.put("response_message", "Thêm mới thành công");
        ua.returnData = returnData;
        ua.addUser();
    }
    
    public void getAddressByUser() throws SQLException, IOException {
        String userId = (String)httpUtils.getParameter("userid");
        if(userId != null && !userId.trim().isEmpty()) {
            returnData.put("data", (new AddressDB()).getAddressByUser(Integer.parseInt(userId)));
        }
        returnAjax();
    }
    
    public void updateUser() throws IOException, SQLException, ParseException {
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
        
        //------- Update user --------------------------------------------
        String hoten = (String)httpUtils.parameters.get("username");
        String mobile = (String)httpUtils.parameters.get("mobile");
        String birthday = (String)httpUtils.parameters.get("birthday");
        String idnumber = (String)httpUtils.parameters.get("idnumber");
        String password = (String)httpUtils.parameters.get("password");
        String familyMobile = (String)httpUtils.parameters.get("familymobile");
        String home = (String)httpUtils.parameters.get("home");
        String userType = (String)httpUtils.parameters.get("usertype");
        //String picture = (String)httpUtils.parameters.get("imagepath");
        String address = (String)httpUtils.parameters.get("address");
        String latitude = (String)httpUtils.parameters.get("latitude");
        String longitude = (String)httpUtils.parameters.get("longitude");
        String videoUrl = (String)httpUtils.parameters.get("videourl");
        String detail = (String)httpUtils.parameters.get("detail");
        
        List lstParam = new ArrayList();
        
        String sql = "update sm_user set ";
        boolean first = true;
        
        if(hoten != null) {
            if(first) first = false; else sql += ",";
            sql += "name = ? ";
            lstParam.add(hoten.trim());
        }
        
        if(mobile != null) {
            if(first) first = false; else sql += ",";
            sql += "mobile = ? ";            
            lstParam.add(mobile.trim());
        }
        
        if(birthday != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            Date birthDate = df.parse(birthday.trim());
            if(first) first = false; else sql += ",";
            sql += "birthday = ? ";            
            lstParam.add(birthDate);
        }
        
        if(idnumber != null) {
            lstParam.add(idnumber.trim());
            if(first) first = false; else sql += ",";
            sql += "id_number = ? ";            
        }
        
        if(familyMobile != null) {
            if(first) first = false; else sql += ",";
            sql += "family_mobile = ? ";            
            lstParam.add(familyMobile.trim());
        }
        
        if(videoUrl != null) {
            if(first) first = false; else sql += ",";
            sql += "video_url = ? ";            
            lstParam.add(videoUrl.trim());
        }
        
        if(detail != null) {
            if(first) first = false; else sql += ",";
            sql += "detail = ? ";            
            lstParam.add(detail.trim());
        }
        
        Integer intUserType = null;
        if(userType != null && !userType.trim().isEmpty()) {
            if(first) first = false; else sql += ",";
            sql += "user_type = ? ";            
            intUserType = Integer.parseInt(userType);
            lstParam.add(intUserType);
        } 
        
        boolean changeAddress = false;
        if(home != null && !home.trim().equals(cacheUser.get("home_province"))) {
            changeAddress = true;
        }        
        
        if(address != null && !address.trim().equals(cacheUser.get("address"))) {
            changeAddress = true;
        }
        
        Double doubleLat = null;
        if((latitude != null && !latitude.trim().isEmpty() && cacheUser.get("latitude") != null && !latitude.trim().equals(cacheUser.get("latitude").toString()))
                || (latitude != null && cacheUser.get("latitude") == null)
                || (latitude == null && cacheUser.get("latitude") != null))
        {
            changeAddress = true;
        }

        Double doubleLong = null;
        if((longitude != null && !longitude.trim().isEmpty() && cacheUser.get("longitude") != null && !longitude.trim().equals(cacheUser.get("longitude").toString()))
                || (longitude != null && cacheUser.get("longitude") == null)
                || (longitude == null && cacheUser.get("longitude") != null))
        {
            changeAddress = true;
        }
        
        if(changeAddress) {
            if(first) first = false; else sql += ",";
            sql += "home_province = ? ";            
            lstParam.add(home.trim());

            if(first) first = false; else sql += ",";
            sql += "address = ? ";            
            lstParam.add(address.trim());

            if(latitude != null && !latitude.trim().isEmpty()) {
                if(first) first = false; else sql += ",";
                sql += "latitude = ? ";            
                doubleLat = RsUserAction.parseLatitude(latitude);
                lstParam.add(doubleLat);
            } else if(cacheUser.get("latitude") != null) {
                doubleLat = RsUserAction.parseLatitude(cacheUser.get("latitude").toString());           
            }

            if(longitude != null && !longitude.trim().isEmpty()) {
                if(first) first = false; else sql += ",";
                sql += "longitude = ? ";            
                doubleLong = RsUserAction.parseLongitude(longitude);
                lstParam.add(doubleLong);            
            } else if(cacheUser.get("longitude") != null) {
                doubleLong = RsUserAction.parseLongitude(cacheUser.get("longitude").toString());           
            }
        }
        
        if(password != null && !password.trim().isEmpty()) {
            if(first) first = false; else sql += ",";
            sql += " password = ? ";            
            lstParam.add((new EncryptDecryptUtils()).encodePassword(password.trim()));
        }
         
        Integer intUserId = null;
        if(userId != null && !userId.trim().isEmpty()) {
            intUserId = Integer.parseInt(userId);
        } 
        
        Integer addressId = null;
        if(changeAddress) {
            List lstAddress = new ArrayList();
            lstAddress.add(address);
            lstAddress.add(home);
            lstAddress.add(doubleLat);
            lstAddress.add(doubleLong);
            lstAddress.add(intUserId);
            addressId = (new AddressDB()).insertAddress(lstAddress);   
            lstParam.add(addressId);
            if(first) first = false; else sql += ",";
            sql += " address_id = ? ";            
        }         
        
        sql += " where user_id = ? ";
        lstParam.add(intUserId);
        
        try {
            DatabaseConnector.getInstance().executeData(sql, lstParam);
        } catch(Exception ex) {
            if(ex.getMessage().contains("Duplicate entry")) {
                returnData.put("error_code", "createuser_04");
                returnData.put("error_message", "Số điện thoại đã tồn tại");
                returnData.put("response_message", "Số điện thoại đã tồn tại");
                returnAjax();
                return;                
            }
        }
        
        if(intUserType != null && intUserType == 3) HttpSession.getInstance().setCacheAttribute("cache_maid".getBytes(), (new MaidDB()).getAllMaid());
        
        Map user = (new UserDB()).getUserById(intUserId);
        EncryptDecryptUtils edu = new EncryptDecryptUtils();
        HttpSession.getInstance().setCacheAttribute(("ssouser_" + edu.base64Encode(user.get("mobile").toString())).getBytes(), user);
        HttpSession.getInstance().setCacheAttribute(("ssouser_id" + edu.base64Encode(user.get("user_id").toString())).getBytes(), user);        
        
        this.returnData.put("data", user);
        returnAjax();         
        returnData.put("response_message", "Cập nhật thành công");
    }
    
    public void uploadUserPic() throws IOException, SQLException {
        if(httpUtils.parameters.get("imagefile") != null && httpUtils.parameters.get("userid") != null) {
            Integer intUserId = Integer.parseInt(httpUtils.parameters.get("userid").toString());
            String sourceData = httpUtils.parameters.get("imagefile").toString();
            String imageName = httpUtils.parameters.get("filename").toString();
            if(!imageName.contains(".")) imageName += ".jpg";

            // tokenize the data
            String[] parts = sourceData.split(",");
            String imageString = parts[1];         
            
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
                String extension = FileUtils.extractFileExt(imageName);
                String fileName = "" + (new Date()).getTime() + "_" + UUID.randomUUID().toString() + extension;
                HashMap data = new HashMap();

                // create a buffered image
                BufferedImage image = null;
                byte[] imageByte;

                BASE64Decoder decoder = new BASE64Decoder();
                imageByte = decoder.decodeBuffer(imageString);
                ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
                image = ImageIO.read(bis);
                bis.close();

                // write the image to a file
                extension = extension.replace(".", "");
                File outputfile = new File(filePath + fileName);
                ImageIO.write(image, extension, outputfile);                   

                data.put("serverFileName", fileName);
                (new UserDB()).updateUserImage(fileName, intUserId);
                
                Map user = (new UserDB()).getUserById(intUserId);
                EncryptDecryptUtils edu = new EncryptDecryptUtils();
                HttpSession.getInstance().setCacheAttribute(("ssouser_" + edu.base64Encode(user.get("mobile").toString())).getBytes(), user);
                HttpSession.getInstance().setCacheAttribute(("ssouser_id" + edu.base64Encode(user.get("user_id").toString())).getBytes(), user);        

                String json = new Gson().toJson(data);
                httpUtils.sendStringResponse(200, json);
            }
        } else {
            httpUtils.sendNotFoundResponse();
        }
    }
    
    public static Double parseLatitude(String latitude) {
        Double lat = Double.parseDouble(latitude);
        if(lat.toString().length() < 9) lat += 0.000001;
        return lat;
    }
    
    public static Double parseLongitude(String longitude) {
        Double lng = Double.parseDouble(longitude);
        if(lng.toString().length() < 10) lng += 0.000001;
        return lng;
    }    
    
    public void changePass() throws IOException, NoSuchAlgorithmException, UnsupportedEncodingException, ClassNotFoundException, SQLException {
        String userName = (String)httpUtils.parameters.get("userName");
        String password = (String)httpUtils.parameters.get("password");
        String newPass = (String)httpUtils.parameters.get("newPass");
        
        if (!(userName != null && !userName.trim().isEmpty())) {
            returnData.put("response_message", "Hãy nhập Tên đăng nhập");
            returnData.put("error_code", "changepass_02");
            returnData.put("error_message", "Chưa nhập user");
            returnAjax();
            return;
        }
        
        if (!(password != null && !password.trim().isEmpty())) {
            returnData.put("response_message", "Hãy nhập Mật khẩu cũ");
            returnData.put("error_code", "changepass_03");
            returnData.put("error_message", "Chưa nhập mật khẩu cũ");
            returnAjax();
            return;         
        }
        
        if (!(newPass != null && !newPass.trim().isEmpty())) {
            returnData.put("response_message", "Hãy nhập Mật khẩu mới");
            returnData.put("error_code", "changepass_04");
            returnData.put("error_message", "Chưa nhập mật khẩu mới");
            returnAjax();
            return;         
        }        
        EncryptDecryptUtils edu = new EncryptDecryptUtils();
        boolean checkAuthen = false;
        HashMap user = null;
        if(userName != null & !userName.trim().isEmpty()) {
            user = (new LoginAction(httpUtils)).getUserInfor(userName.trim());
            if (user != null) {
                String pw = user.get("password").toString();
                if(pw.equals((new EncryptDecryptUtils()).encodePassword(password))) {
                    HashMap ssoUser = (HashMap)HttpSession.getInstance().getCacheAttribute(("ssouser_id" + edu.base64Encode(user.get("user_id").toString())).getBytes());
                    if(ssoUser != null) checkAuthen = true;
                }
            }
        }
        if(checkAuthen) {
            List lstParam = new ArrayList();
            String encodePass = edu.encodePassword(newPass);
            lstParam.add(encodePass);
            lstParam.add(Integer.parseInt(user.get("user_id").toString()));
            DatabaseConnector.getInstance().executeData("update sm_user set password = ? where user_id = ? ", lstParam);
            user.put("password", encodePass);
            HttpSession.getInstance().setCacheAttribute(("ssouser_" + edu.base64Encode(user.get("user_name").toString())).getBytes(), user);
            HttpSession.getInstance().setCacheAttribute(("ssouser_id" + edu.base64Encode(user.get("user_id").toString())).getBytes(), user);
        } else {
            returnData.put("response_message", "Sai tên đăng nhập hoặc mật khẩu");
            returnData.put("error_code", "changepass_05");
            returnData.put("error_message", "Sai tên đăng nhập hoặc mật khẩu");
            returnAjax();            
        }
    }
    
    public void resetPass() throws IOException, NoSuchAlgorithmException, UnsupportedEncodingException, ClassNotFoundException, SQLException {
        String userName = (String)httpUtils.parameters.get("userName");
        
        if (!(userName != null && !userName.trim().isEmpty())) {
            returnData.put("response_message", "Hãy nhập Tên đăng nhập");
            returnData.put("error_code", "changepass_01");
            returnData.put("error_message", "Chưa nhập user");
            returnAjax();
            return;
        }      
        
        HashMap user = null;
        if(userName != null & !userName.trim().isEmpty()) {
            user = (new LoginAction(httpUtils)).getUserInfor(userName.trim());
            if (user != null) {
                returnData.put("response_message", "Hãy nhập OTP");
                returnData.put("token", UUID.randomUUID().toString());
                returnAjax();
            } else {
                returnData.put("response_message", "Số điện thoại này chưa đăng ký.");
                returnData.put("error_code", "changepass_02");
                returnData.put("error_message", "Số điện thoại này chưa đăng ký.");
                returnAjax();
            }
        }
    } 
    
    public void validateOTP() throws IOException {
        String newPass = (String)httpUtils.parameters.get("newPass");
        if (!(newPass != null && !newPass.trim().isEmpty())) {
            returnData.put("response_message", "Hãy nhập Mật khẩu mới");
            returnData.put("error_code", "changepass_03");
            returnData.put("error_message", "Chưa nhập mật khẩu mới");
            returnAjax();
            return;         
        }  
        String otp = (String)httpUtils.parameters.get("otp");
        if (!(otp != null && !otp.trim().isEmpty())) {
            returnData.put("response_message", "Hãy nhập mã OTP");
            returnData.put("error_code", "changepass_04");
            returnData.put("error_message", "Chưa nhập mã OTP");
            returnAjax();
            return;         
        }  
        String token = (String)httpUtils.parameters.get("token");
        if (!(token != null && !token.trim().isEmpty())) {
            returnData.put("response_message", "Yêu cầu không hợp lệ");
            returnData.put("error_code", "changepass_05");
            returnData.put("error_message", "Không tìm thấy Token");
            returnAjax();
            return;         
        } 
        
        returnData.put("response_message", "Hiện tại chúng tôi chưa hỗ trợ tính năng quên mật khẩu. Vui lòng gọi Hotline 01644752126 để lấy lại mật khẩu");
        returnAjax();
    }
}
