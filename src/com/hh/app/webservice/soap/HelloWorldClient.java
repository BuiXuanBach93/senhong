/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.app.webservice.soap;

import java.net.URL;
import java.util.Random;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class HelloWorldClient{

    public static void main(String[] args) throws Exception {
        Random r = new Random();
        char a = 97;
        char b = 97 + 25;
        System.out.println("a:" + a);
        System.out.println("b:" + b);
        char random_3_Char = (char) (97 + r.nextInt(25));
        System.out.println("random:" + random_3_Char);
        
	/*URL url = new URL("http://localhost:8080/soap/hello?wsdl");
        QName qname = new QName("http://soap.webservice.app.hh.com/", "HelloWorldImplService");
        Service service = Service.create(url, qname);
        HelloWorld hello = service.getPort(HelloWorld.class);
        System.out.println(hello.getHelloWorldAsString("mkyong"));
        */
    }

}
