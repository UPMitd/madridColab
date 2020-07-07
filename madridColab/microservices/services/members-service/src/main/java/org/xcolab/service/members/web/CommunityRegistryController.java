package org.xcolab.service.members.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.xcolab.model.tables.pojos.CommunityRegistry;
import org.xcolab.service.members.service.member.UserService;

@RestController
@RequestMapping("/communityRegistry")
public class CommunityRegistryController {

    private final UserService memberService;

    @Autowired
    public CommunityRegistryController(UserService memberService) {
        Assert.notNull(memberService, "UserService bean is required");
        this.memberService = memberService;
    }

    @PostMapping
    public CommunityRegistry createCommunityRegistry(@RequestBody CommunityRegistry communityRegistry) {
        return memberService.createCommunityRegistry(communityRegistry.getIdUser(), communityRegistry.getIdRole());
    }
}
