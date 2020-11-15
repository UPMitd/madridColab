package org.xcolab.client.fusion;

import org.xcolab.client.contest.ContestClient;
import org.xcolab.client.fusion.beans.FusionBean;
import org.xcolab.client.fusion.utils.FusionStatus;
import org.xcolab.client.members.MembersClient;
import org.xcolab.client.members.exceptions.MemberNotFoundException;
import org.xcolab.client.proposals.ProposalClient;
import org.xcolab.util.http.client.enums.ServiceNamespace;

public class FusionClient {

    private static final ProposalClient proposalClient = ProposalClient.fromNamespace(
            ServiceNamespace.instance());
    private static final ContestClient contestClient= ContestClient.fromNamespace(
            ServiceNamespace.instance());

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

}
