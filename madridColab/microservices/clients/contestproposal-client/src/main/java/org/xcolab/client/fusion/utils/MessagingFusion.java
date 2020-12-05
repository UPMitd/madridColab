package org.xcolab.client.fusion.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.xcolab.client.admin.attributes.configuration.ConfigurationAttributeKey;
import org.xcolab.client.emails.EmailClient;
import org.xcolab.client.fusion.beans.FusionBean;
import org.xcolab.client.members.MessagingClient;
import org.xcolab.client.members.messaging.MessageLimitExceededException;
import org.xcolab.client.members.pojo.Member;
import org.xcolab.client.proposals.pojo.Proposal;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.internet.InternetAddress;

public class MessagingFusion {

    private static final String REQUEST_TITLE="New Fusion Request";
    private static final String ACCEPT_TITLE="Fusion Request Accepted";
    private static final String REJECT_TITLE="Fusion Request Rejected";

    private static final String MESSAGE_REQUEST="The user USER_B_PLACEHOLDER, has sent you a new merge request to "
            + "merge his proposal PROPOSAL_B_PLACEHOLDER with your proposal PROPOSAL_A_PLACEHOLDER"
            + ", to accept or reject the merge request, <a href='FUSION_REQUEST_PLACEHOLDER'> click here.</a>";

    private static final String MESSAGE_ACCEPT_REQUEST="Your merge request, to merge your proposal PROPOSAL_B_PLACEHOLDER with the proposal"
            + " PROPOSAL_A_PLACEHOLDER, has been accepted. To see the status of all merge requests, "
            + "<a href='MY_FUSION_REQUESTS_PLACEHOLDER'> click here.</a>";

    private static final String MESSAGE_REJECT_REQUEST="Your merge request, to merge your proposal PROPOSAL_B_PLACEHOLDER with the proposal"
            + " PROPOSAL_A_PLACEHOLDER, has been rejected. To see the status of all merge requests, "
            + "<a href='MY_FUSION_REQUESTS_PLACEHOLDER'> click here.</a>";;

    private static final String MESSAGE_FOOTER_TEMPLATE = "<br /><br />\n<hr /><br />\n"
            + "Sincerely, the COLAB_NAME_PLACEHOLDER team";

    private static final String USER_B_PLACEHOLDER="USER_B_PLACEHOLDER";

    private static final String PROPOSAL_B_PLACEHOLDER="PROPOSAL_B_PLACEHOLDER";

    private static final String PROPOSAL_A_PLACEHOLDER= "PROPOSAL_A_PLACEHOLDER";

    private static final String FUSION_REQUEST_PLACEHOLDER="FUSION_REQUEST_PLACEHOLDER";

    private static final String MY_FUSION_REQUESTS_PLACEHOLDER= "MY_FUSION_REQUESTS_PLACEHOLDER";

    private static final String COLAB_NAME_PLACEHOLDER= "COLAB_NAME_PLACEHOLDER";

    private static final String baseUrl = ConfigurationAttributeKey.COLAB_URL_PRODUCTION.get();

    private static final Logger log = LoggerFactory.getLogger(MessagingFusion.class);

    public void notifyFusionRequest(FusionBean fusionBean){
        StringBuilder body= new StringBuilder();
        body.append("<br></br><p>");
        String userB=getUserUrl(fusionBean.getFromUser());
        String proposalB=getProposalUrl(fusionBean.getFromProposal());
        String proposalA=getProposalUrl(fusionBean.getToProposal());
        String linkFusionRequest=baseUrl+"/fusionRequest/"+fusionBean.getId();
        String compose= StringUtils.replace(MESSAGE_REQUEST, USER_B_PLACEHOLDER, userB);
        compose=StringUtils.replace(compose, PROPOSAL_B_PLACEHOLDER, proposalB);
        compose=StringUtils.replace(compose, PROPOSAL_A_PLACEHOLDER, proposalA);
        compose=StringUtils.replace(compose, FUSION_REQUEST_PLACEHOLDER, linkFusionRequest);
        body.append(compose);
        body.append("</p>");
        body.append(getFooter());
        sendNotification(REQUEST_TITLE, body.toString(), fusionBean.getFromUser().getId(),
                fusionBean.getToUser().getId());
        sendEmailMessage(fusionBean.getToUser(), REQUEST_TITLE, body.toString(), fusionBean.getFromUser().getId());

    }

    public void notifyFusionAccept(FusionBean fusionBean){
        StringBuilder body= new StringBuilder();
        body.append("<br></br><p>");
        String proposalB=getProposalUrl(fusionBean.getFromProposal());
        String proposalA=getProposalUrl(fusionBean.getToProposal());
        String linkMyFusionRequests=baseUrl+"/fusionRequests/sent";
        String compose= StringUtils.replace(MESSAGE_ACCEPT_REQUEST, PROPOSAL_B_PLACEHOLDER, proposalB);
        compose=StringUtils.replace(compose, PROPOSAL_A_PLACEHOLDER, proposalA);
        compose=StringUtils.replace(compose, MY_FUSION_REQUESTS_PLACEHOLDER, linkMyFusionRequests);
        body.append(compose);
        body.append("</p>");
        body.append(getFooter());
        List<Long> recipients= new ArrayList<>();
        recipients.add(fusionBean.getFromUser().getId());
        sendNotification(ACCEPT_TITLE, body.toString(), fusionBean.getToUser().getId(),
                    fusionBean.getFromUser().getId());
        sendEmailMessage(fusionBean.getFromUser(), ACCEPT_TITLE, body.toString(), fusionBean.getToUser().getId());
    }

    public void notifyFusionReject(FusionBean fusionBean){
        StringBuilder body= new StringBuilder();
        body.append("<br></br><p>");
        String proposalB="<a href='"+fusionBean.getFromProposal().getProposalUrl()+"'>"+fusionBean.getFromProposal().getName()+"</a>";
        String proposalA="<a href='"+fusionBean.getToProposal().getProposalUrl()+"'>"+fusionBean.getToProposal().getName()+"</a>";
        String linkMyFusionRequests=baseUrl+"/fusionRequests/sent";
        String compose= StringUtils.replace(MESSAGE_REJECT_REQUEST, PROPOSAL_B_PLACEHOLDER, proposalB);
        compose=StringUtils.replace(compose, PROPOSAL_A_PLACEHOLDER, proposalA);
        compose=StringUtils.replace(compose, MY_FUSION_REQUESTS_PLACEHOLDER, linkMyFusionRequests);
        body.append(compose);
        body.append("</p>");
        body.append(StringUtils.replace(MESSAGE_FOOTER_TEMPLATE, COLAB_NAME_PLACEHOLDER, ConfigurationAttributeKey.COLAB_NAME.get()));
        List<Long> recipients= new ArrayList<>();
        recipients.add(fusionBean.getFromUser().getId());
        sendNotification(REJECT_TITLE, body.toString(), fusionBean.getToUser().getId(),
                fusionBean.getFromUser().getId());
        sendEmailMessage(fusionBean.getFromUser(), REJECT_TITLE, body.toString(), fusionBean.getToUser().getId());
    }

    private String getUserUrl(Member member){
        String userUrl="<a href='"+baseUrl+member.getProfileLinkUrl()+"'>"+member.getDisplayName()+"</a>";
        return  userUrl;
    }

    private String getProposalUrl(Proposal proposal){
        String proposalB="<a href='"+baseUrl+proposal.getProposalUrl()+"'>"+proposal.getName()+"</a>";
        return proposalB;
    }

    private String getFooter(){
       String footer= StringUtils.replace(MESSAGE_FOOTER_TEMPLATE, COLAB_NAME_PLACEHOLDER, ConfigurationAttributeKey.COLAB_NAME.get());
       return footer;
    }

    private void sendNotification(String subject, String content, Long fromUserId, Long toUserId){
        List<Long> recipients= new ArrayList<>();
        recipients.add(toUserId);
        try {
            MessagingClient.checkLimitAndSendMessage(subject, content, fromUserId, "", recipients);
        } catch (MessageLimitExceededException e) {
            e.printStackTrace();
            log.warn(e.getMessage());
        }

    }

    private void sendEmailMessage(Member recipient, String subject, String body, Long referenceId) {
        Date date= new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String d=formatter.format(date);
        subject= subject+" "+d;
        try {
            InternetAddress fromEmail = TemplateReplacementUtil.getAdminFromEmailAddress();
            InternetAddress toEmail =
                    new InternetAddress(recipient.getEmailAddress(), recipient.getFullName());
            EmailClient.sendEmail(fromEmail.getAddress(), ConfigurationAttributeKey.COLAB_NAME.get(),
                    toEmail.getAddress(),
                    TemplateReplacementUtil.replacePlatformConstants(subject),
                    TemplateReplacementUtil.replacePlatformConstants(body), true,
                    fromEmail.getAddress(), ConfigurationAttributeKey.COLAB_NAME.get(),
                    referenceId);
        } catch (UnsupportedEncodingException e) {

        }
    }


}
