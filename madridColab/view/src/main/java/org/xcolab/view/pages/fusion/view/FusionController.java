package org.xcolab.view.pages.fusion.view;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.xcolab.client.fusion.FusionClient;
import org.xcolab.client.fusion.beans.FusionBean;
import org.xcolab.client.fusion.pojo.Fusion;
import org.xcolab.client.fusion.utils.FusionStatus;
import org.xcolab.client.members.exceptions.MemberNotFoundException;
import org.xcolab.client.members.pojo.Member;
import org.xcolab.commons.servlet.flash.AlertMessage;
import org.xcolab.view.errors.AccessDeniedPage;
import org.xcolab.view.pages.fusion.beans.FusionRequestBean;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@Controller
public class FusionController {

    FusionClient fusionClient;
    private boolean sent=false;
    private static final Logger log = LoggerFactory.getLogger(FusionController.class);

    ///received requests
    @GetMapping("/fusionRequests")
    public String getReceivedFusions(HttpServletResponse response, Model model,
            Integer pageNumber, Member loggedInMember){
        if (loggedInMember == null) {
            return new AccessDeniedPage(null).toViewName(response);
        }
        fusionClient= new FusionClient();
        ArrayList<FusionBean> fusionRequests=fusionClient.listByToUserID(loggedInMember.getId());
        sent=false;
        model.addAttribute("user", loggedInMember);
        model.addAttribute("fusionRequests", fusionRequests);
        model.addAttribute("sent", sent);
        return "/fusion/fusionRequests";
    }

    //sent requests
    @GetMapping("/fusionRequests/sent")
    public String getSentFusions(HttpServletResponse response, Model model,
            Integer pageNumber, Member loggedInMember){
        if (loggedInMember == null) {
            return new AccessDeniedPage(null).toViewName(response);
        }
        fusionClient= new FusionClient();
        ArrayList<FusionBean> fusionRequests=fusionClient.listByFromUserID(loggedInMember.getId());
        sent=true;
        model.addAttribute("user", loggedInMember);
        model.addAttribute("fusionRequests", fusionRequests);
        model.addAttribute("sent", sent);
        return "/fusion/fusionRequests";
    }

    //show a request
    @GetMapping("/fusionRequest/{proposalId}")
    public String newFusionRequest(HttpServletResponse response, Model model,
            @PathVariable Long proposalId, Member loggedInMember){
        if (loggedInMember == null) {
            return new AccessDeniedPage(null).toViewName(response);
        }
        FusionBean fusionBean= fusionClient.getFusionRequestById(proposalId);
        if(loggedInMember.getId()!=fusionBean.getToUser().getId()&&
                loggedInMember.getId()!=fusionBean.getFromUser().getId()){
            return new AccessDeniedPage(null).toViewName(response);
        }
        boolean canAcceptOrDeny=false;
        if(loggedInMember.getId()==fusionBean.getToUser().getId()){
            canAcceptOrDeny=true;
        }
        model.addAttribute("canAcceptOrDeny", canAcceptOrDeny);
        model.addAttribute("user", loggedInMember);
        model.addAttribute("fusionBean", fusionBean);
        return "/fusion/fusionRequest";
    }


    @PostMapping("/fusionRequest/api/new")
    public String fusionRequest(HttpServletRequest request, @Valid FusionRequestBean fusionRequestBean, BindingResult bindingResult,
            HttpServletResponse response, Model model, Member loggedInMember){
        if(bindingResult.hasErrors()){
            log.info("Error in the request");
        }
        if (loggedInMember == null) {
            return new AccessDeniedPage(null).toViewName(response);
        }
        String requestText;
        if(fusionRequestBean.getRequestText()==null){
            requestText="";
        }else{
            requestText=fusionRequestBean.getRequestText();
        }
        fusionClient=new FusionClient();
        FusionBean fusionBean=fusionClient.newFusionRequest(fusionRequestBean.getFromUserId(),
                fusionRequestBean.getToUserId(),fusionRequestBean.getFromProposalId(),
                fusionRequestBean.getToProposalId(), requestText);
        AlertMessage.success("Your merge request was sent").flash(request);
        String url= fusionBean.getToProposal().getProposalUrl();
        return "redirect:"+url;
    }

    @GetMapping("/fusionRequest/api/accept/{fusionID}")
    public String acceptFusion(HttpServletRequest request, BindingResult bindingResult,
            HttpServletResponse response, @PathVariable Long fusionId, Model model,
            Member loggedInMember){
        if (loggedInMember == null) {
            return new AccessDeniedPage(null).toViewName(response);
        }
        fusionClient= new FusionClient();
        FusionBean fusionBean= fusionClient.getFusionRequestById(fusionId);
        if(loggedInMember.getId()!=fusionBean.getToUser().getId()){
            return new AccessDeniedPage(null).toViewName(response);
        }
        fusionBean=fusionClient.acceptFusion(fusionId);
        AlertMessage.success("The merge request was accepted").flash(request);
        model.addAttribute("fusionBean", fusionBean);
        return "redirect:/fusionRequest/"+fusionId;
    }

    @GetMapping("/fusionRequest/api/reject/{fusionID}")
    public String rejectFusion(HttpServletRequest request, BindingResult bindingResult,
            HttpServletResponse response, @PathVariable Long fusionId, Model model,
            Member loggedInMember){
        if (loggedInMember == null) {
            return new AccessDeniedPage(null).toViewName(response);
        }
        fusionClient= new FusionClient();
        FusionBean fusionBean= fusionClient.getFusionRequestById(fusionId);
        if(loggedInMember.getId()!=fusionBean.getToUser().getId()){
            return new AccessDeniedPage(null).toViewName(response);
        }
        fusionBean=fusionClient.rejectFusion(fusionId);
        AlertMessage.warning("The merge request was rejected").flash(request);
        model.addAttribute("fusionBean", fusionBean);
        return "redirect:/fusionRequest/"+fusionId;

    }
}
