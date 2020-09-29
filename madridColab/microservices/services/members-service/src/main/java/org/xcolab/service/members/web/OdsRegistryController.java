package org.xcolab.service.members.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.xcolab.model.tables.pojos.OdsRegistry;
import org.xcolab.service.members.service.member.UserService;

@RestController
@RequestMapping("/odsRegistry")
public class OdsRegistryController {

    private final UserService memberService;

    @Autowired
    public OdsRegistryController(UserService memberService) {
        Assert.notNull(memberService, "UserService bean is required");
        this.memberService = memberService;
    }

    @PostMapping
    public OdsRegistry createOdsRegistry(@RequestBody OdsRegistry odsRegistry) {
        return memberService.createOdsRegistry(odsRegistry.getIdUser(), odsRegistry.getIdOds());
    }
}
