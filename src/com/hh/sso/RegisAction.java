/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.sso;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import com.google.gson.Gson;
import com.google.j2objc.annotations.AutoreleasePool;
import com.hh.action.BaseAction;
import com.hh.sso.db.UserDB;
import com.hh.server.HHServer;
import com.hh.util.EncryptDecryptUtils;
import com.hh.util.FileUtils;
import com.hh.util.ResourceBundleUtils;
import com.hh.web.HttpSession;
import com.hh.web.HttpUtils;
import com.octo.captcha.image.gimpy.Gimpy;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringEscapeUtils;

public class RegisAction extends BaseAction {
    
    public static final String REGIS_SUCCESS = "0";
    public static final String REGIS_FAIL = "1";
    public static final String CAPTCHA_INCORECT = "2";
    public static final String MOBILE_EMPTY = "3";
    public static final String MOBILE_LENGTH = "4";
    public static final String MOBILE_FORMAT = "5";
    public static final String EMAIL_EMPTY = "6";
    public static final String EMAIL_INCORECT = "7";
    public static final String REGIS_WAIT = "8";
    public static final String EMAIL_LENGTH = "9";
    public static final String PASSWORD_EMPTY = "10";
    public static final String CAPTCHA_EMPTY = "11";
    public static final String FIRSTNAME_EMPTY = "12";
    public static final String LASTNAME_EMPTY = "13";
    public static final String COUNTRY_EMPTY = "14";
    public static final String JOB_EMPTY = "15";
    public static final String ADDRESS_EMPTY = "16";
    public static final String BIRTHDAY_INCORECT = "18";
    public static final String COUNTRY_INCORECT = "19";
    public static final String JOB_INCORECT = "20";
    public static final String FIRSTNAME_LENGTH = "21";
    public static final String LASTNAME_LENGTH = "22";
    public static final String PASSWORD_LENGTH = "23";
    public static final String ORGANIZATION_LENGTH = "24";
    public static final String ADDRESS_LENGTH = "25";
    public static final String EMAIL_EXIST = "26";
    public static final String MOBILE_EXIST = "27";
    public static final String STAFFCODE_EMPTY = "28";
    public static final String STAFFCODE_LENGTH = "29";
    public static final String PASSWORD_WEAK = "30";
    public static final String GENDER_INCORRECT = "31";
    
    public RegisAction(HttpUtils hu) {
        super(hu);
    }
    
    public void regis() throws IOException, ClassNotFoundException {
        String appUrl = (String)httpUtils.getSessionAttribute("sso_appurl");
        FileUtils fu = new FileUtils();
        File resultFile = new File("web/sso/registration.html").getCanonicalFile();
        String content = fu.readFileToString(resultFile, FileUtils.UTF_8);
        if(appUrl == null) content = content.replace("login?app=g_app", "sso");
        else content = content.replace("g_app", appUrl);
        byte[] byteContent = content.getBytes(Charset.forName(FileUtils.UTF_8));
        httpUtils.httpExchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        httpUtils.httpExchange.sendResponseHeaders(200, byteContent.length);
        OutputStream os = httpUtils.httpExchange.getResponseBody();
        os.write(byteContent);
        os.close();
    }
    
    public void doRegis() throws IOException, ParseException {
        if(validateForm()) {
            List lstParam = new ArrayList();
            String phoneNumber = (String)httpUtils.getParameter("mobile");
            if(phoneNumber != null) phoneNumber = phoneNumber.trim();
            else phoneNumber = "";
            if (phoneNumber.charAt(0) == '8' && phoneNumber.charAt(1) == '4') {
                phoneNumber = "0" + phoneNumber.substring(2);
            }            
            String org = (String)httpUtils.getParameter("organization");
            if(org == null) org = "";
            String salt = UUID.randomUUID().toString();
            lstParam.add(phoneNumber);
            lstParam.add(stripXSS((String)httpUtils.getParameter("firstName")).trim());
            lstParam.add(stripXSS((String)httpUtils.getParameter("lastName")).trim());
            lstParam.add(stripXSS((String)httpUtils.getParameter("email")).trim());
            lstParam.add(DigestUtils.sha256Hex(salt + ((String)httpUtils.getParameter("password")).trim()));
            lstParam.add(stripXSS((String)httpUtils.getParameter("country")).trim());
            lstParam.add(stripXSS(org).trim());
            lstParam.add(stripXSS((String)httpUtils.getParameter("job")).trim());
            lstParam.add(stripXSS((String)httpUtils.getParameter("address")).trim());
            lstParam.add(getBirthDay(
                    (String)httpUtils.getParameter("birthDay"),
                    (String)httpUtils.getParameter("birthMonth"),
                    (String)httpUtils.getParameter("birthYear")));
            lstParam.add(new Date());
            lstParam.add(httpUtils.getParameter("gender"));
            lstParam.add(stripXSS((String)httpUtils.getParameter("staffCode")).trim());
            lstParam.add(salt);
            httpUtils.setSessionAttribute("regis_param", lstParam);
            String otp = UUID.randomUUID().toString();
            httpUtils.setSessionAttribute("regis_otp", otp);
            HttpSession.getInstance().setCacheAttribute("regisotp_" + otp, "false_" + (new Date().getTime()));
            ActorRef actor = HHServer.system.actorOf(
                    Props.create(RegisUssdActor.class, otp, "VTMobileConnect", "84" + phoneNumber.substring(1)));
            actor.tell("", actor);
            actor.tell(PoisonPill.getInstance(), ActorRef.noSender());
            sendStatusResponse(REGIS_WAIT);
        }
    }
    
    public void validateRegis() throws ParseException, IOException, SQLException {
        String otp = (String)httpUtils.getSessionAttribute("regis_otp");
        if(otp != null) {
            String result = HttpSession.getInstance().getCacheAttribute("regisotp_" + otp);
            if(result != null && result.contains("_")) {
                long timeCreate = Long.parseLong(result.split("_")[1]);
                if((new Date()).getTime() - timeCreate > 30000) {
                    sendStatusResponse(REGIS_FAIL);
                } else {
                    sendStatusResponse(REGIS_WAIT);
                }
            } else if(result != null && "true".equals(result)) {
                regisSuccess();
            } else {
                sendStatusResponse(REGIS_FAIL);
            }
        }
    }
    
    public void sendValidate(String status, String redirect, Integer validateCount) throws IOException {
        HashMap<String, String> data = new HashMap();
        data.put("status", status);
        data.put("validateCount", validateCount.toString());
        String json = new Gson().toJson(data);
        httpUtils.sendStringResponse(200, json);
    }      
    
    public void regisSuccess() throws ParseException, IOException, SQLException {
        List lstParam = (List)httpUtils.getSessionAttribute("regis_param");
        UserDB userDB = new UserDB();
        userDB.insertUser(lstParam);
        insertToCache(lstParam);
        sendStatusResponse(REGIS_SUCCESS);
    }
    
    @AutoreleasePool
    public boolean isValidEmail(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
    
    public boolean isValidDay(String day, String month, String year) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        df.setLenient(false);
        try {
            df.parse(day + "/" + month + "/" + year);
        }
        catch (ParseException e) { 
            return false;
        } 
        return true;
    }
    
    public Date getBirthDay(String day, String month, String year) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.parse(day + "/" + month + "/" + year);
    }
    
    public boolean isValidCountry(String country) {
        String[] arrCountry = {"Afghanistan","Aland Islands","Albania","Algeria","American Samoa","Andorra","Angola","Anguilla","Antarctica","Antigua and Barbuda","Argentina","Armenia","Aruba","Australia","Austria","Azerbaijan","Bahamas","Bahrain","Bangladesh","Barbados","Belarus","Belgium","Belize","Benin","Bermuda","Bhutan","Bolivia","Bonaire","Bosnia and Herzegovina","Botswana","Bouvet Island","Brazil","British Indian Ocean Territory","Brunei Darussalam","Bulgaria","Burkina Faso","Burundi","Cambodia","Cameroon","Canada","Cape Verde","Cayman Islands","Central African Republic","Chad","Chile","China","Christmas Island","Cocos (Keeling) Islands","Colombia","Comoros","Congo","Cook Islands","Costa Rica","Côte dIvoire","Croatia","Cuba","Curaçao","Cyprus","Czech Republic","Denmark","Djibouti","Dominica","Dominican Republic","Ecuador","Egypt","El Salvador","Equatorial Guinea","Eritrea","Estonia","Ethiopia","Falkland Islands (Malvinas)","Faroe Islands","Fiji","Finland","France","French Guiana","French Polynesia","French Southern Territories","Gabon","Gambia","Georgia","Germany","Ghana","Gibraltar","Greece","Greenland","Grenada","Guadeloupe","Guam","Guatemala","Guernsey","Guinea","Guinea-Bissau","Guyana","Haiti","Heard Island and McDonald Islands","Holy See (Vatican City State)","Honduras","Hong Kong","Hungary","Iceland","India","Indonesia","Iran","Iraq","Ireland","Isle of Man","Israel","Italy","Jamaica","Japan","Jersey","Jordan","Kazakhstan","Kenya","Kiribati","Korea(North)","Korea(South)","Kuwait","Kyrgyzstan","Lao","Latvia","Lebanon","Lesotho","Liberia","Libya","Liechtenstein","Lithuania","Luxembourg","Macao","Macedonia","Madagascar","Malawi","Malaysia","Maldives","Mali","Malta","Marshall Islands","Martinique","Mauritania","Mauritius","Mayotte","Mexico","Micronesia","Moldova","Monaco","Mongolia","Montenegro","Montserrat","Morocco","Mozambique","Myanmar","Namibia","Nauru","Nepal","Netherlands","New Caledonia","New Zealand","Nicaragua","Niger","Nigeria","Niue","Norfolk Island","Northern Mariana Islands","Norway","Oman","Pakistan","Palau","Palestinian Territory","Panama","Papua New Guinea","Paraguay","Peru","Philippines","Pitcairn","Poland","Portugal","Puerto Rico","Qatar","Réunion","Romania","Russian Federation","Rwanda","Saint Barthélemy","Saint Helena","Saint Kitts and Nevis","Saint Lucia","Saint Martin (French part)","Saint Pierre and Miquelon","Saint Vincent and the Grenadines","Samoa","San Marino","Sao Tome and Principe","Saudi Arabia","Senegal","Serbia","Seychelles","Sierra Leone","Singapore","Sint Maarten (Dutch part)","Slovakia","Slovenia","Solomon Islands","Somalia","South Africa","South Georgia and the South Sandwich Islands","South Sudan","Spain","Sri Lanka","Sudan","Suriname","Svalbard and Jan Mayen","Swaziland","Sweden","Switzerland","Syrian Arab Republic","Taiwan","Tajikistan","Tanzania","Thailand","Timor-Leste","Togo","Tokelau","Tonga","Trinidad and Tobago","Tunisia","Turkey","Turkmenistan","Turks and Caicos Islands","Tuvalu","Uganda","Ukraine","United Arab Emirates","United Kingdom","United States","Uruguay","Uzbekistan","Vanuatu","Venezuela","Viet Nam","Virgin Islands","Wallis and Futuna","Western Sahara","Yemen","Zambia","Zimbabwe"};
        return Arrays.asList(arrCountry).contains(country);
    }
    
    public boolean isValidJob(String job) {
        String[] arrJob = {"Nghệ thuật - Thiết kế", "Kinh doanh - Thương mại", "Kỹ thuật - Công nghệ", "Luật - Tư pháp", "Y Dược", "Khoa học - Giáo dục", "Báo chí - Xuất bản", "Giải trí - Truyền thông", "Thực phẩm - Ẩm thực", "Giao thông - Công chánh", "Thể thao", "Công an - Quân đội"};
        return Arrays.asList(arrJob).contains(job);
    }    
    
    @AutoreleasePool
    private boolean validateForm() throws IOException {
        boolean check = false;
        String captcha = (String)httpUtils.getParameter("captcha");
        Gimpy pixCaptcha = (Gimpy) httpUtils.getSessionAttribute("sso_captcha");
        EncryptDecryptUtils edu = new EncryptDecryptUtils();
        String phoneNumber = (String)httpUtils.getParameter("mobile");
        if(phoneNumber != null) phoneNumber = phoneNumber.trim();
        else phoneNumber = "";
        if (phoneNumber != null && phoneNumber.charAt(0) == '8' && phoneNumber.charAt(1) == '4') {
            phoneNumber = "0" + phoneNumber.substring(2);
        }
        String email = (String)httpUtils.getParameter("email");
        if(email != null) email = email.trim();
        
        if (captcha == null || captcha.isEmpty() || "null".equals(captcha)) {
            sendStatusResponse(CAPTCHA_EMPTY);
        } else if (pixCaptcha == null || !pixCaptcha.response.toLowerCase().equals(captcha.toLowerCase())) {
            sendStatusResponse(CAPTCHA_INCORECT);
        } else if (((String)httpUtils.getParameter("mobile") == null) || ((String)httpUtils.getParameter("mobile")).isEmpty()) {
            sendStatusResponse(MOBILE_EMPTY);
        } else if (((String)httpUtils.getParameter("mobile")).length() > 15) {
            sendStatusResponse(MOBILE_LENGTH);
        } else if (phoneNumber.charAt(0) != '0') {
            sendStatusResponse(MOBILE_FORMAT);
        } else if (!phoneNumber.matches("\\d+")) {
            sendStatusResponse(MOBILE_FORMAT);
        } else if (HttpSession.getInstance().getCacheAttribute(("ssouser_" + edu.base64Encode(phoneNumber)).getBytes()) != null) {
            sendStatusResponse(MOBILE_EXIST);
        } else if (email == null || email.isEmpty()) {
            sendStatusResponse(EMAIL_EMPTY);
        } else if (!isValidEmail(email)) {
            sendStatusResponse(EMAIL_INCORECT);
        } else if (email.length() > 100) {
            sendStatusResponse(EMAIL_LENGTH);
        } else if (HttpSession.getInstance().getCacheAttribute(("ssouser_" + edu.base64Encode(((String)httpUtils.getParameter("email")))).getBytes()) != null) {
            sendStatusResponse(EMAIL_EXIST);
        } else if (((String)httpUtils.getParameter("password") == null) || ((String)httpUtils.getParameter("password")).isEmpty()) {
            sendStatusResponse(PASSWORD_EMPTY);
        } else if (((String)httpUtils.getParameter("password")).length() > 100) {
            sendStatusResponse(PASSWORD_LENGTH);
        } else if (!((String)httpUtils.getParameter("password")).matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            sendStatusResponse(PASSWORD_WEAK);
        } else if (((String)httpUtils.getParameter("firstName") == null) || ((String)httpUtils.getParameter("firstName")).isEmpty()) {
            sendStatusResponse(FIRSTNAME_EMPTY);
        } else if (((String)httpUtils.getParameter("firstName")).length() > 100) {
            sendStatusResponse(FIRSTNAME_LENGTH);
        } else if (((String)httpUtils.getParameter("lastName") == null) || ((String)httpUtils.getParameter("lastName")).isEmpty()) {
            sendStatusResponse(LASTNAME_EMPTY);
        } else if (((String)httpUtils.getParameter("lastName")).length() > 100) {
            sendStatusResponse(LASTNAME_LENGTH);
        } else if (((String)httpUtils.getParameter("country") == null) || ((String)httpUtils.getParameter("country")).isEmpty()) {
            sendStatusResponse(COUNTRY_EMPTY);
        } else if (!isValidCountry((String)httpUtils.getParameter("country"))) {
            sendStatusResponse(COUNTRY_INCORECT);
        } else if (httpUtils.getParameter("organization") != null && ((String)httpUtils.getParameter("organization")).length() > 100) {
            sendStatusResponse(ORGANIZATION_LENGTH);
        } else if (((String)httpUtils.getParameter("job") == null) || ((String)httpUtils.getParameter("job")).isEmpty()) {
            sendStatusResponse(JOB_EMPTY);
        } else if (!isValidJob((String)httpUtils.getParameter("job"))) {
            sendStatusResponse(JOB_INCORECT);
        } else if (((String)httpUtils.getParameter("address") == null) || ((String)httpUtils.getParameter("address")).isEmpty()) {
            sendStatusResponse(ADDRESS_EMPTY);
        } else if (((String)httpUtils.getParameter("address")).length() > 200) {
            sendStatusResponse(ADDRESS_LENGTH);
        } else if (((String)httpUtils.getParameter("staffCode") == null) || ((String)httpUtils.getParameter("staffCode")).isEmpty()) {
            sendStatusResponse(STAFFCODE_EMPTY);
        } else if (((String)httpUtils.getParameter("staffCode")).length() > 100) {
            sendStatusResponse(STAFFCODE_LENGTH);
        } else if (((String)httpUtils.getParameter("gender") == null) || 
                (!"0".equals(httpUtils.getParameter("gender").toString()) && !"1".equals(httpUtils.getParameter("gender").toString()))) {
            sendStatusResponse(GENDER_INCORRECT);
        } else if (!isValidDay((String)httpUtils.getParameter("birthDay"), (String)httpUtils.getParameter("birthMonth"), (String)httpUtils.getParameter("birthYear"))) {
            sendStatusResponse(BIRTHDAY_INCORECT);
        } else {
            check = true;
        }
        return check;
    }
    
    public int getUserId(String mobile) throws SQLException {
        UserDB userDB = new UserDB();
        return userDB.getUserId(mobile);
    }
    
    public void insertToCache(List lstParam) throws ParseException, SQLException {
        EncryptDecryptUtils edu = new EncryptDecryptUtils();
        HashMap user = new HashMap();
        int userId = getUserId((String)lstParam.get(0));
        user.put("user_id", userId);
        user.put("mobile", lstParam.get(0));
        user.put("first_name", lstParam.get(1));
        user.put("last_name", lstParam.get(2));
        user.put("email", lstParam.get(3));
        user.put("pin_code", lstParam.get(4));
        user.put("country", lstParam.get(5));
        user.put("organization", lstParam.get(6));
        user.put("job", lstParam.get(7));
        user.put("address", lstParam.get(8));
        user.put("birthday", lstParam.get(9));
        user.put("create_date", new Date());
        user.put("gender", lstParam.get(10));
        user.put("staff_code", lstParam.get(11));
        user.put("salt", lstParam.get(12));
        
        HttpSession.getInstance().setCacheAttribute(("ssouser_" + edu.base64Encode(user.get("email").toString())).getBytes(), user);
        HttpSession.getInstance().setCacheAttribute(("ssouser_" + edu.base64Encode(user.get("mobile").toString())).getBytes(), user);
        HttpSession.getInstance().setCacheAttribute(("ssouser_id" + edu.base64Encode(user.get("user_id").toString())).getBytes(), user);
    }
    
    public void sendStatusResponse(String status) throws IOException {
        HashMap<String, String> data = new HashMap();
        data.put("status", status);
        String json = new Gson().toJson(data);
        httpUtils.sendStringResponse(200, json);
    }    
    
    private String stripXSS(String value) {
        return StringEscapeUtils.escapeXml10(value);
    }    
}
