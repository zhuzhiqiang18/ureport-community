package com.bstek.home.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeAction {

	@RequestMapping("/")
    public String hello(){
        return "forward:index.html";
    }
}
