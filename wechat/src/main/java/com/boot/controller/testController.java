package com.boot.controller;

import com.boot.pojo.SysUser;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
@AllArgsConstructor
public class testController {

    private static final Logger LOGGER = LoggerFactory.getLogger(testController.class);

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value = "/test/get",method = RequestMethod.GET)
    @ResponseBody
    public String TestMethod(){
        //此参数为注册在Eureka中的服务
        String ticketName = restTemplate.getForObject("http://PROVIDER-TICKET//test/get", String.class);
        return ticketName;
    }

    @RequestMapping("/")
    public String showHome() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.info("当前登陆用户：" + name);
        return "redirect:/index";
    }


    @RequestMapping("/index")
    public String index(Model model) {
        String accountNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        SysUser sysUser = restTemplate.getForObject("http://PROVIDER-TICKET//findAllByName?accountNumber="+accountNumber, SysUser.class);
        model.addAttribute("headPortrait",sysUser.getHeadPortrait());
        model.addAttribute("nickName",sysUser.getNickName());
        model.addAttribute("accountNumber",accountNumber);
        model.addAttribute("region",sysUser.getRegion());
        model.addAttribute("signature",sysUser.getSignature());
        model.addAttribute("sex",sysUser.getSex());
        return "home";
    }


    @RequestMapping("/login")
    public String showLogin() {
        return "login";
    }


    @RequestMapping("/admin")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String printAdmin() {
        return "如果你看见这句话，说明你有ROLE_ADMIN角色";
    }

    @RequestMapping("/user")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER')")
    public String printUser() {
        return "如果你看见这句话，说明你有ROLE_USER角色";
    }

}
