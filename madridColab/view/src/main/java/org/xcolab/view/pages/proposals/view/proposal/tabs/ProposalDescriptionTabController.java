package org.xcolab.view.pages.proposals.view.proposal.tabs;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.xcolab.client.admin.ContestTypeClient;
import org.xcolab.client.admin.attributes.configuration.ConfigurationAttributeKey;
import org.xcolab.client.admin.pojo.ContestType;
import org.xcolab.client.contest.ContestClientUtil;
import org.xcolab.client.contest.pojo.Contest;
import org.xcolab.client.contest.pojo.phases.ContestPhase;
import org.xcolab.client.contest.pojo.templates.ProposalTemplateSectionDefinition;
import org.xcolab.client.flagging.FlaggingClient;
import org.xcolab.client.fusion.FusionClient;
import org.xcolab.client.fusion.beans.FusionBean;
import org.xcolab.client.members.PlatformTeamsClient;
import org.xcolab.client.members.exceptions.MemberNotFoundException;
import org.xcolab.client.members.pojo.Member;
import org.xcolab.client.proposals.ProposalClient;
import org.xcolab.client.proposals.ProposalMoveClient;
import org.xcolab.client.proposals.pojo.ContestTypeProposal;
import org.xcolab.client.proposals.pojo.Proposal;
import org.xcolab.client.proposals.pojo.phases.ProposalMoveHistory;
import org.xcolab.commons.servlet.flash.AlertMessage;
import org.xcolab.util.enums.contest.ContestPhaseTypeValue;
import org.xcolab.util.enums.flagging.TargetType;
import org.xcolab.util.enums.proposal.MoveType;
import org.xcolab.util.enums.proposal.ProposalTemplateSectionType;
import org.xcolab.view.activityentry.ActivityEntryHelper;
import org.xcolab.view.errors.AccessDeniedPage;
import org.xcolab.view.pages.fusion.beans.FusionRequestBean;
import org.xcolab.view.pages.profile.wrappers.UserProfileWrapper;
import org.xcolab.view.pages.proposals.discussion.ProposalDiscussionPermissions;
import org.xcolab.view.pages.proposals.exceptions.ProposalsAuthorizationException;
import org.xcolab.view.pages.proposals.permissions.ProposalsPermissions;
import org.xcolab.view.pages.proposals.requests.JudgeProposalFeedbackBean;
import org.xcolab.view.pages.proposals.requests.UpdateProposalDetailsBean;
import org.xcolab.view.pages.proposals.tabs.ProposalTab;
import org.xcolab.view.pages.proposals.utils.context.ClientHelper;
import org.xcolab.view.pages.proposals.utils.context.ProposalContext;
import org.xcolab.view.pages.proposals.view.proposal.AddUpdateProposalControllerUtil;
import org.xcolab.view.pages.proposals.wrappers.ProposalJudgeWrapper;
import org.xcolab.view.taglibs.xcolab.jspTags.discussion.DiscussionPermissions;
import org.xcolab.view.util.entity.EntityGroupingUtil;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/contests/{contestYear}/{contestUrlName}")
public class ProposalDescriptionTabController extends BaseProposalTabController {

    @Autowired
    private final ActivityEntryHelper activityEntryHelper;

    public ProposalDescriptionTabController(
            ActivityEntryHelper activityEntryHelper) {
        this.activityEntryHelper = activityEntryHelper;
    }


    @GetMapping("c/{proposalUrlString}/{proposalId}")
    public String showProposalDetails(HttpServletRequest request, HttpServletResponse response,
            Model model, ProposalContext proposalContext, Member currentMember,
            @PathVariable Long contestYear,
            @PathVariable String contestUrlName, @PathVariable Long proposalId,
            @RequestParam(defaultValue = "false") boolean edit,
            @RequestParam(required = false) Integer version,
            @RequestParam(required = false) Long moveFromContestPhaseId,
            @RequestParam(required = false) String moveType,
            @Valid JudgeProposalFeedbackBean judgeProposalFeedbackBean,
            BindingResult bindingResult) {
        return showProposalDetails(request, response, model, proposalContext, currentMember, false,
                edit, moveFromContestPhaseId, moveType, proposalId);
    }

    public String showProposalDetails(HttpServletRequest request, HttpServletResponse response,
            Model model, ProposalContext proposalContext, Member currentMember,
            boolean voted, boolean edit, Long moveFromContestPhaseId, String moveType, Long proposalId) {

        final ProposalsPermissions permissions = proposalContext.getPermissions();
        if (!permissions.getCanView()) {
            return new AccessDeniedPage(currentMember).toViewName(response);
        }

        if(currentMember!=null){
            UserProfileWrapper currentUserProfile = null;
            try {
                currentUserProfile=new UserProfileWrapper(currentMember.getId(),
                        currentMember, activityEntryHelper);
            } catch (MemberNotFoundException e) {
                e.printStackTrace();
            }
            Collection<ContestTypeProposal> collectionProposal
                    =currentUserProfile.getContestTypeProposalWrappersByContestTypeId();
            Iterator<ContestTypeProposal> it=collectionProposal.iterator();
            List<Proposal> proposals=null;
            while (it.hasNext()){
                ContestTypeProposal contestTypeProposal=it.next();
                proposals=contestTypeProposal.getProposals();
            }

            ///check if some proposal is a merge
            List<Proposal> myProposals=new ArrayList<>();
            ArrayList<FusionBean> merged=FusionClient.listByFromUserID(currentMember.getId());
            for (int i=0; i< proposals.size(); i++){
                boolean exists=false;
                Proposal proposal= proposals.get(i);
                for (int j=0; j<merged.size(); j++){
                        FusionBean fusionBean= merged.get(j);
                        if(fusionBean.getProposal()!=null&&fusionBean.getProposal().getId()!=null){
                            if(fusionBean.getProposal().getId()==proposal.getId()){
                                exists=true;
                            }
                        }
                }
                if(exists==false){
                    myProposals.add(proposal);
                }
            }
            boolean mergeable=true;
            List<Contest> allContests = ContestClientUtil.getIntercommunityContests();
            for(int i=0; i<allContests.size(); i++){
                Contest contestFusion= allContests.get(i);
                Contest contest=proposalContext.getProposal().getContest();
                if(contestFusion.getId()==contest.getId()){
                    mergeable=false;
                }
            }

            ///check if it is already a merge
            ArrayList<FusionBean> fusionBeans=FusionClient.listALlFusions();
            for(FusionBean fusionBean: fusionBeans){
             if(fusionBean.getProposal()!=null&&fusionBean.getProposal().getId()!=null){
                 if(fusionBean.getProposal().getId()==proposalId){
                     mergeable=false;
                 }
             }
            }
            Member member=proposalContext.getProposal().getAuthor();
            if(member.getId()==currentMember.getId()){
                mergeable=false;
            }
            model.addAttribute("myProposals", myProposals);
            model.addAttribute("mergeable",mergeable);
            FusionRequestBean fusionRequestBean= new FusionRequestBean();
            model.addAttribute("fusionRequestBean", fusionRequestBean);

        }



        setCommonModelAndPageAttributes(request, model, proposalContext, ProposalTab.DESCRIPTION);

        boolean editValidated = false;
        final ProposalsPermissions proposalsPermissions = proposalContext.getPermissions();
        if (edit && proposalsPermissions.getCanEdit()) {
            editValidated = true;
        }
        model.addAttribute("edit", editValidated);
        model.addAttribute("voted", voted);
        model.addAttribute("reportTargets", FlaggingClient.listReportTargets(TargetType.PROPOSAL));
        model.addAttribute("showOpennessStatus",
            ConfigurationAttributeKey.CONTESTS_ALLOW_OPEN_PROPOSALS.get());
        model.addAttribute("member", currentMember);


        // make sure it's in the right contest,
        // which might not be the proposal's contests (e.g. when moving)
        model.addAttribute("saveUrl", proposalContext.getProposal().getProposalLinkUrl(proposalContext.getContest()));

        final Proposal proposal = proposalContext.getProposal();

        final ClientHelper clients = proposalContext.getClients();
        final ProposalClient proposalClient = clients.getProposalClient();
        final Contest baseContest = proposalClient
                .getCurrentContestForProposal(proposal.getId());

        if (voted) {
            setVotingDeadline(model, proposalContext, baseContest);
        }

        final boolean isMove = moveFromContestPhaseId != null && moveType != null;
        if (isMove) {
            // get base proposal from base contest
            ContestPhase baseContestPhase =
                    ContestClientUtil.getActivePhase(baseContest.getId());

            Proposal baseProposalWrapped = new Proposal(proposal, proposal.getCurrentVersion(),
                    baseContest, baseContestPhase, null);

            UpdateProposalDetailsBean updateProposalDetailsBean = new UpdateProposalDetailsBean(
                    proposal, baseProposalWrapped, true,
                    MoveType.valueOf(moveType, true));
            updateProposalDetailsBean.setMoveFromContestPhaseId(moveFromContestPhaseId);
            updateProposalDetailsBean.setMoveToContestId(proposalContext.getContest().getId());

            model.addAttribute("hasUnmappedSections",
                    hasUnmappedSections(proposalContext.getContest(), baseProposalWrapped));
            model.addAttribute("baseProposal", baseProposalWrapped);
            model.addAttribute("baseContest", baseContest);
            model.addAttribute("isMove", true);

            if (!model.containsAttribute("updateProposalDetailsBean")) {
                model.addAttribute("updateProposalDetailsBean", updateProposalDetailsBean);
            }
        } else if (!model.containsAttribute("updateProposalDetailsBean")) {
            model.addAttribute("updateProposalDetailsBean", new UpdateProposalDetailsBean(
                    proposal));
        }

        if (editValidated || isMove) {
            request.setAttribute("imageUploadServiceUrl",
                    ConfigurationAttributeKey.IMAGE_UPLOAD_EXTERNAL_SERVICE_URL.get());
            request.setAttribute("imageUploadHelpText",
                    ConfigurationAttributeKey.IMAGE_UPLOAD_HELP_TEXT.get());

            model.addAttribute("hasProposalPicker", hasProposalPicker(proposal));

            model.addAttribute("showProposalEditHelpText",
                    ConfigurationAttributeKey.CONTESTS_SHOW_PROPOSAL_EDIT_HELP_TEXT.get());
            model.addAttribute("showImageUpload",
                    ConfigurationAttributeKey.PROPOSALS_SHOW_IMAGE_UPLOAD.get());
            model.addAttribute("saveButtonText",
                    ConfigurationAttributeKey.PROPOSALS_SAVE_BUTTON_TEXT.get());
            model.addAttribute("saveHelpText",
                    ConfigurationAttributeKey.PROPOSALS_SAVE_HELP_TEXT.get());
            model.addAttribute("proposalPickerDefaultTabIsContests",
                    ConfigurationAttributeKey.PROPOSALS_PICKER_DEFAULT_TAB_CONTESTS.get());
            model.addAttribute("userTeams", PlatformTeamsClient.getTeams(currentMember));

            return "proposals/proposalDetails_edit";
        }

        if (proposalsPermissions.getCanJudgeActions()) {
            setJudgeProposalBean(model, proposalContext, currentMember);
        }

        ProposalDiscussionPermissions pdp = new ProposalDiscussionPermissions(request,
                proposalContext.getProposal());
        request.setAttribute(DiscussionPermissions.REQUEST_ATTRIBUTE_NAME, pdp);

        setLinkedProposals(model, proposalContext, proposal);
        final Contest contest = proposalContext.getContest();
        populateMoveHistory(model, proposalContext, proposal, contest);

        return "proposals/proposalDetails";
    }

    private boolean hasUnmappedSections(Contest moveToContest, Proposal baseProposalWrapped) {
        Set<Long> newContestSections = moveToContest.getSections().stream()
                .map(ProposalTemplateSectionDefinition::getSectionDefinitionId)
                .collect(Collectors.toSet());

        return baseProposalWrapped.getSections().stream()
                .filter(section -> StringUtils.isNotBlank(section.getContent()))
                .anyMatch(section -> !newContestSections.contains(section.getSectionDefinitionId()));
    }

    private boolean hasProposalPicker(Proposal proposal) {
        return proposal.getSections().stream()
                .map(ProposalTemplateSectionDefinition::getTypeEnum)
                .anyMatch(ProposalTemplateSectionType.PROPOSAL_PICKER_SECTION_TYPES::contains);
    }

    private void populateMoveHistory(Model model, ProposalContext proposalContext,
            Proposal proposal, Contest contest) {

        final ClientHelper clients = proposalContext.getClients();
        final ProposalMoveClient proposalMoveClient = clients.getProposalMoveClient();
        List<ProposalMoveHistory> sourceMoveHistories = proposalMoveClient
                        .getBySourceProposalIdContestId(proposal.getId(),
                                contest.getId());
        model.addAttribute("sourceMoveHistories", sourceMoveHistories);

        ProposalMoveHistory targetMoveHistory = proposalMoveClient
                        .getByTargetProposalIdContestId(proposal.getId(),
                                contest.getId());
        if (targetMoveHistory != null) {
            model.addAttribute("targetMoveHistory", targetMoveHistory);
        }
    }

    private void setLinkedProposals(Model model, ProposalContext proposalContext, Proposal proposal) {
        final ClientHelper clients = proposalContext.getClients();
        final ProposalClient proposalClient = clients.getProposalClient();
        List<Proposal> linkedProposals = proposalClient
                        .getSubproposals(proposal.getId(), true);
        Map<ContestType, Set<Proposal>> proposalsByContestType =
                EntityGroupingUtil.groupByContestType(linkedProposals);
        Map<Long, ContestTypeProposal> contestTypeProposalWrappersByContestTypeId = new HashMap<>();

        for (ContestType contestType : ContestTypeClient
                .getActiveContestTypes()) {
            contestTypeProposalWrappersByContestTypeId.put(contestType.getId(),
                    new ContestTypeProposal(contestType));
            final Set<Proposal> proposalsInContestType = proposalsByContestType.get(contestType);
            if (proposalsInContestType != null) {
                for (Proposal p : proposalsInContestType) {
                    contestTypeProposalWrappersByContestTypeId.get(contestType.getId())
                            .getProposals().add((p));
                }
            }
        }
        model.addAttribute("linkedProposalContestTypeProposalWrappersByContestTypeId",
                contestTypeProposalWrappersByContestTypeId);
    }

    private void setJudgeProposalBean(Model model, ProposalContext proposalContext, Member member) {
        final JudgeProposalFeedbackBean judgeProposalFeedbackBean =
                (JudgeProposalFeedbackBean) model.asMap().get("judgeProposalFeedbackBean");
        if (judgeProposalFeedbackBean == null
                || judgeProposalFeedbackBean.getContestPhaseId() == null) {

            final Proposal proposal = proposalContext.getProposal();
            final ContestPhase contestPhase = proposalContext.getContestPhase();

            ProposalJudgeWrapper proposalJudgeWrapper = new ProposalJudgeWrapper(proposal, member);
            JudgeProposalFeedbackBean judgeProposalBean =
                    new JudgeProposalFeedbackBean(proposalJudgeWrapper);
            judgeProposalBean.setContestPhaseId(contestPhase.getId());

            model.addAttribute("judgeProposalFeedbackBean", judgeProposalBean);
        }
    }

    private void setVotingDeadline(Model model, ProposalContext proposalContext, Contest baseContest) {
        Date votingDeadline = getVotingDeadline(proposalContext, baseContest);
        if (votingDeadline != null) {
            final DateFormat customDateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            model.addAttribute("votingDeadline", customDateFormat.format(votingDeadline));
        } else {
            model.addAttribute("votingDeadline", "");
        }
    }

    private Date getVotingDeadline(ProposalContext proposalContext, Contest contest) {
        List<ContestPhase> contestPhases = proposalContext.getClients().getContestClient()
                .getAllContestPhases(contest.getId());
        final ContestPhase activeVotingPhase = getActiveVotingPhase(contestPhases);
        if (activeVotingPhase != null) {
            return activeVotingPhase.getPhaseEndDate();
        }
        return null;
    }

    private ContestPhase getActiveVotingPhase(List<ContestPhase> contestPhases) {
        for (ContestPhase phase : contestPhases) {
            if (phase.getContestPhaseTypeId() == ContestPhaseTypeValue.SELECTION_OF_WINNERS.getTypeId() ||
                    phase.getContestPhaseTypeId() == ContestPhaseTypeValue.SELECTION_OF_WINNERS_NEW.getTypeId() ||
                    phase.getContestPhaseTypeId() == ContestPhaseTypeValue.WINNERS_SELECTION.getTypeId() ||
                    phase.getContestPhaseTypeId() == ContestPhaseTypeValue.VOTING_PHASE_SOLVE.getTypeId()
                    ) {
                if (phase.getPhaseActive()) {
                    return phase;
                }
            }
        }

        return null;
    }

    @PostMapping("c/{proposalUrlString}/{proposalId}")
    public String updateProposal(HttpServletRequest request, HttpServletResponse response,
            Model model, ProposalContext proposalContext, Member currentMember,
            @PathVariable String contestYear, @PathVariable String contestUrlName,
            @PathVariable String proposalUrlString, @PathVariable String proposalId,
            @Valid UpdateProposalDetailsBean updateProposalDetailsBean, BindingResult result)
            throws ProposalsAuthorizationException, IOException {

        Proposal proposal = proposalContext.getProposal();
        final ProposalsPermissions permissions = proposalContext.getPermissions();
        if (proposal != null && !permissions.getCanEdit()) {
            return new AccessDeniedPage(currentMember).toViewName(response);
        }

        if (result.hasErrors()) {
            AlertMessage.danger(
                    "Changes NOT saved. Please fix the errors before saving.")
                    .flash(request);
            return showProposalDetails(request, response, model, proposalContext, currentMember,
                    false, true, null, null, null);
        }

        return AddUpdateProposalControllerUtil
                .createOrUpdateProposal(request, updateProposalDetailsBean, proposal, proposalContext);
    }

}
