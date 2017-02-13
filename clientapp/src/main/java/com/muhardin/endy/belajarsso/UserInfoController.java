package com.muhardin.endy.belajarsso;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserInfoController {
    
    @GetMapping("/saya")
    public ModelMap infoUser(Authentication currentUser){
        return new ModelMap("currentUser", currentUser);
    }
    
    @GetMapping("/api/saya")
    @ResponseBody
    public Authentication infoUserRest(Authentication currentUser){
        return currentUser;
    }
}
