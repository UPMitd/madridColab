package org.xcolab.client.fusion;

import org.xcolab.client.contest.ContestClient;
import org.xcolab.client.contest.pojo.ContestFusion;
import org.xcolab.client.contest.resources.ContestResource;
import org.xcolab.client.contest.resources.ProposalResource;
import org.xcolab.client.fusion.beans.FusionBean;
import org.xcolab.client.fusion.pojo.Fusion;
import org.xcolab.client.fusion.utils.FusionStatus;
import org.xcolab.client.fusion.utils.MessagingFusion;
import org.xcolab.client.members.MembersClient;
import org.xcolab.client.members.exceptions.MemberNotFoundException;
import org.xcolab.client.proposals.ProposalClient;
import org.xcolab.util.http.client.RestResource1;
import org.xcolab.util.http.client.enums.ServiceNamespace;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FusionClient {

    private static final ProposalClient proposalClient = ProposalClient.fromNamespace(
            ServiceNamespace.instance());
    private static final ContestClient contestClient= ContestClient.fromNamespace(
            ServiceNamespace.instance());

   private static final RestResource1<Fusion, Long> fusionRestResource =
            new RestResource1<>(ProposalResource.PROPOSAL_FUSION_REQUEST, Fusion.TYPES);

    private static final RestResource1<ContestFusion, Long> contestFusionRestResource =
            new RestResource1<>(ContestResource.CONTEST_FUSION, ContestFusion.TYPES);

    public FusionClient(){

    }

    public FusionBean createFusionBean(Long id, Long toUserId, Long fromUserId, Long toProposalId,
            Long fromProposalId, FusionStatus status, Long newContestId, Long newProposalId,
            String requestText, String commonText) throws MemberNotFoundException {

    FusionBean fusionBean= new FusionBean();
    fusionBean.setId(id);
    fusionBean.setFromUser(MembersClient.getMember(fromUserId));
    fusionBean.setToUser(MembersClient.getMember(toUserId));
    fusionBean.setFromProposal(proposalClient.getProposal(fromProposalId));
    fusionBean.setToProposal(proposalClient.getProposal(toProposalId));
    fusionBean.setStatus(status);
    fusionBean.setRequestText(requestText);
    if(newContestId!=null){
        fusionBean.setNewContest(contestClient.getContest(newContestId));
    }
    if(newProposalId!=null){
        fusionBean.setNewProposal(proposalClient.getProposal(newProposalId));
    }
    if(commonText!=null){
        fusionBean.setCommonText(commonText);
    }
    return fusionBean;
    }

    public FusionBean getFusionRequestById(Long id){
        return null;
    }

    public ArrayList<FusionBean> listALlFusions(){
        return null;
    }

    public ArrayList<FusionBean> listByFromUserID(Long id){
        return null;
    }

    public ArrayList<FusionBean> listByToUserID(Long id){
        return null;
    }

    public FusionBean newFusionRequest(String fromUserId, String toUserId,
            String fromProposalId, String toProposalId, String requestText){
        FusionBean fusionBean;
        try {
            fusionBean=createFusionBean((long)1, (long)10148,(long)10146, (long)8, (long)9,
                    FusionStatus.PENDING, null, null, "", "");
        }catch (MemberNotFoundException e){
            fusionBean=null;
        }
        return fusionBean;
    }

    public FusionBean acceptFusion(Long fusionId){
        return null;
    }

    public FusionBean rejectFusion(Long fusionID){
        return null;
    }



}
