package org.xcolab.service.members.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.xcolab.model.tables.pojos.DataCity;
import org.xcolab.service.members.service.member.UserService;
import org.xcolab.service.members.util.email.AccountDetailsEmmaAPI;

@RestController
@RequestMapping("/dataCity")
public class DataCityController {

    private final UserService memberService;
    private static final Logger log = LoggerFactory.getLogger(AccountDetailsEmmaAPI.class);


    @Autowired
    public DataCityController(UserService memberService) {
        Assert.notNull(memberService, "UserService bean is required");
        this.memberService = memberService;
    }

    @PostMapping
    public DataCity createDataCity(@RequestBody DataCity dataCity) {
        return memberService.createDataCity(dataCity);
    }
}
