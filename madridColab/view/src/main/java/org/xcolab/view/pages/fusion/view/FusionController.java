package org.xcolab.view.pages.fusion.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.xcolab.client.fusion.FusionClient;
import org.xcolab.client.fusion.beans.FusionBean;
import org.xcolab.client.fusion.utils.FusionStatus;
import org.xcolab.client.members.exceptions.MemberNotFoundException;
import org.xcolab.client.members.pojo.Member;
import org.xcolab.view.pages.fusion.beans.FusionRequestBean;

import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
public class FusionController {

    FusionClient fusionClient;
    private boolean sent=false;

    @GetMapping("/fusionRequests")
    public String getReceivedFusions(HttpServletResponse response, Model model,
            Integer pageNumber, Member loggedInMember){

        fusionClient= new FusionClient();
        FusionBean fusionBean;
        try {
            fusionBean=fusionClient.createFusionBean((long)1, (long)10148,(long)10146, (long)8, (long)9,
                    FusionStatus.PENDING, null, null, "", "");
        }catch (MemberNotFoundException e){
            fusionBean=null;
        }
        ArrayList<FusionBean> fusions= new ArrayList<>();
        fusions.add(fusionBean);
        fusionBean.setStatus(FusionStatus.ACCEPTED);
        fusions.add(fusionBean);
        fusionBean.setStatus(FusionStatus.REJECTED);
        fusions.add(fusionBean);
        sent=false;
        model.addAttribute("fusionRequests", fusions);
        model.addAttribute("user", loggedInMember);
        model.addAttribute("sent", sent);
        return "/fusion/fusionRequests";
    }

    @GetMapping("/fusionRequests/sent")
    public String getSentFusions(HttpServletResponse response, Model model,
            Integer pageNumber, Member loggedInMember){
        sent=true;
        model.addAttribute("user", loggedInMember);
        model.addAttribute("sent", sent);
        return "/fusion/fusionRequests";
    }

    @GetMapping("/fusionRequest/{proposalId}")
    public String newFusionRequest(HttpServletResponse response, Model model,
            @PathVariable Integer proposalId, Member loggedInMember){
        System.out.println(proposalId);
        fusionClient= new FusionClient();
        FusionBean fusionBean;
        try {
            fusionBean=fusionClient.createFusionBean((long)1, (long)10148,(long)10146, (long)8, (long)9,
                    FusionStatus.PENDING, null, null, "", "");
        }catch (MemberNotFoundException e){
            fusionBean=null;
        }
        fusionBean.setRequestText("<p>Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.</p>");

        model.addAttribute("user", loggedInMember);
        model.addAttribute("fusionBean", fusionBean);
        return "/fusion/fusionRequest";
    }

    @PostMapping("/fusionRequest/api/new")
    public String fusionRequest(@Valid FusionRequestBean fusionRequestBean,
            HttpServletResponse response, Model model, Member loggedInMember){
        System.out.println(fusionRequestBean);
        return null;
    }
}
