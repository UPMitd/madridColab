package org.xcolab.client.fusion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.xcolab.client.contest.ContestClient;
import org.xcolab.client.contest.pojo.ContestFusion;
import org.xcolab.client.contest.resources.ContestResource;
import org.xcolab.client.fusion.beans.FusionBean;
import org.xcolab.client.fusion.pojo.ProposalFusionRequest;
import org.xcolab.client.fusion.utils.FusionStatus;
import org.xcolab.client.fusion.utils.MessagingFusion;
import org.xcolab.client.members.MembersClient;
import org.xcolab.client.members.exceptions.MemberNotFoundException;
import org.xcolab.client.proposals.ProposalClient;
import org.xcolab.util.http.client.RestResource1;
import org.xcolab.util.http.client.enums.ServiceNamespace;
import java.util.ArrayList;


public final class FusionClient {

    private static final ProposalClient proposalClient = ProposalClient.fromNamespace(
            ServiceNamespace.instance());
    private static final ContestClient contestClient= ContestClient.fromNamespace(
            ServiceNamespace.instance());

    private static final RestResource1<ContestFusion, Long> contestFusionRestResource =
            new RestResource1<>(ContestResource.CONTEST_FUSION, ContestFusion.TYPES);

    private static final Logger log = LoggerFactory.getLogger(FusionClient.class);

    public static FusionBean createFusionBean(ProposalFusionRequest fusionRequest) throws MemberNotFoundException {

    FusionBean fusionBean= new FusionBean();
    fusionBean.setId(fusionRequest.getId());
    fusionBean.setFromUser(MembersClient.getMember(fusionRequest.getFromUserId()));
    fusionBean.setToUser(MembersClient.getMember(fusionRequest.getToUserId()));
    fusionBean.setFromProposal(proposalClient.getProposal(fusionRequest.getFromProposalId()));
    fusionBean.setToProposal(proposalClient.getProposal(fusionRequest.getToProposalId()));
    fusionBean.setStatus(fusionRequest.getStatus());
    fusionBean.setRequestText(fusionRequest.getRequestText());
    fusionBean.setContestId(contestClient.getContest(fusionRequest.getContestId()));
    if(fusionRequest.getProposalId()!=null) {
        fusionBean.setProposal(proposalClient.getProposal(fusionRequest.getProposalId()));
    }
    fusionBean.setCommonText(fusionRequest.getCommonText());

    return fusionBean;
    }

    public static FusionBean newFusionRequest(ProposalFusionRequest proposalFusionRequest){
        FusionBean fusionBean=null;
        try {
            fusionBean=createFusionBean(proposalClient.createProposalFusionRequest(proposalFusionRequest));
            MessagingFusion messagingFusion= new MessagingFusion();
            messagingFusion.notifyFusionRequest(fusionBean);
        }catch (MemberNotFoundException e){
            fusionBean=null;
        }
       return fusionBean;
    }


    public static FusionBean getFusionRequestById(Long id){
        FusionBean fusionBean=null;
        try {
            fusionBean=createFusionBean(proposalClient.getFusionRequestById(id));
        }catch (MemberNotFoundException e){
            fusionBean=null;
        }
        return fusionBean;
    }

    public static ArrayList<FusionBean> listALlFusions(){
        ArrayList<ProposalFusionRequest> arrFusion=proposalClient.listAllFusions();
        ArrayList<FusionBean> arrFusionBean= new ArrayList<>();
        for(int i=0; i<arrFusion.size(); i++){
            FusionBean fusionBean=null;
            try {
                fusionBean=createFusionBean(arrFusion.get(i));
            }catch (MemberNotFoundException e){
                fusionBean=null;
            }
            if(fusionBean!=null){
                arrFusionBean.add(fusionBean);
            }
        }
        return arrFusionBean;
    }

    public static ArrayList<FusionBean> listByFromUserID(Long id){
        ArrayList<ProposalFusionRequest> arrFusion=proposalClient.listByFromUserID(id);
        ArrayList<FusionBean> arrFusionBean= new ArrayList<>();
        for(int i=0; i<arrFusion.size(); i++){
            FusionBean fusionBean=null;
            try {
                fusionBean=createFusionBean(arrFusion.get(i));
            }catch (MemberNotFoundException e){
                fusionBean=null;
            }
            if(fusionBean!=null){
                arrFusionBean.add(fusionBean);
            }
        }
        return arrFusionBean;
    }

    public static ArrayList<FusionBean> listByToUserID(Long id){
        ArrayList<ProposalFusionRequest> arrFusion=proposalClient.listByToUserID(id);
        ArrayList<FusionBean> arrFusionBean= new ArrayList<>();
        for(int i=0; i<arrFusion.size(); i++){
            FusionBean fusionBean=null;
            try {
                fusionBean=createFusionBean(arrFusion.get(i));
            }catch (MemberNotFoundException e){
                fusionBean=null;
            }
            if(fusionBean!=null){
                arrFusionBean.add(fusionBean);
            }
        }
        return arrFusionBean;
    }

    public static FusionBean acceptFusion(Long fusionId){
        FusionBean fusionBean=null;
        try {
            fusionBean=createFusionBean(proposalClient.acceptFusion(fusionId));
            MessagingFusion messagingFusion= new MessagingFusion();
            messagingFusion.notifyFusionAccept(fusionBean);
        }catch (MemberNotFoundException e){
            fusionBean=null;
        }
        return fusionBean;
    }

    public static FusionBean rejectFusion(Long fusionId){
        FusionBean fusionBean=null;
        try {
            fusionBean=createFusionBean(proposalClient.rejectFusion(fusionId));
            MessagingFusion messagingFusion= new MessagingFusion();
            messagingFusion.notifyFusionReject(fusionBean);
        }catch (MemberNotFoundException e){
            fusionBean=null;
        }
        return fusionBean;
    }



}
