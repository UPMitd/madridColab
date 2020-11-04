package org.xcolab.view.pages.fusion.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.xcolab.client.members.pojo.Member;

import javax.jws.WebParam.Mode;
import javax.servlet.http.HttpServletResponse;

@Controller
public class FusionController {

    @GetMapping("/fusionRequests")
    public String getMyFusions(HttpServletResponse response, Model model,
            Integer pageNumber, Member loggedInMember){

        return "/fusion/fusionRequests";
    }

    @GetMapping("/fusionRequest/{proposalId}")
    public String newFusionRequest(HttpServletResponse response, Model model,
            @PathVariable Integer proposalId){
        System.out.println(proposalId);

        return "/fusion/newFusionRequest";
    }

    @PostMapping("/fusionRequest")
    public String fusionRequest(){
        return null;
    }





}
