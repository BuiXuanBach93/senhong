/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.sso;

import com.hh.action.BaseAction;
import com.hh.web.HttpUtils;
import java.io.IOException;

public class UserAction extends BaseAction {

    public UserAction(HttpUtils hu) {
        super(hu);
    }

    public void createUser()
            throws IOException {
        returnFullPage("web/index.html");
    }
}
