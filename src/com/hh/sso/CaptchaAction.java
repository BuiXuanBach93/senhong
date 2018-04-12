/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.sso;

import com.hh.action.BaseAction;
import com.hh.web.HttpUtils;
import com.octo.captcha.engine.image.gimpy.NonLinearTextGimpyEngine;
import com.octo.captcha.image.ImageCaptcha;
import com.octo.captcha.image.ImageCaptchaFactory;
import com.hh.net.httpserver.Headers;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;

public class CaptchaAction extends BaseAction {

    public CaptchaAction(HttpUtils hu) {
        super(hu);
    }

    public void getCaptcha() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();

        NonLinearTextGimpyEngine bge = new NonLinearTextGimpyEngine();

        ImageCaptchaFactory factory = bge.getImageCaptchaFactory();
        ImageCaptcha pixCaptcha = factory.getImageCaptcha();
        httpUtils.setSessionAttribute("sso_captcha", pixCaptcha);

        BufferedImage challenge = pixCaptcha.getImageChallenge();
        ImageIO.write(challenge, "jpeg", jpegOutputStream);
        byte[] captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
        Headers resHeader = httpUtils.httpExchange.getResponseHeaders();
        resHeader.set("Cache-Control", "no-store");
        resHeader.set("Pragma", "no-cache");
        resHeader.set("Expires", "0");
        resHeader.set("Content-Type", "image/jpeg");
        httpUtils.httpExchange.sendResponseHeaders(200, captchaChallengeAsJpeg.length);
        OutputStream os = httpUtils.httpExchange.getResponseBody();
        os.write(captchaChallengeAsJpeg);
        os.close();
    }
}
