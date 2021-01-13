package org.xcolab.client.proposals;


import org.joda.time.DateTime;
import org.jooq.util.derby.sys.Sys;

import org.xcolab.client.activities.ActivitiesClient;
import org.xcolab.client.admin.ContestTypeClient;
import org.xcolab.client.admin.attributes.configuration.ConfigurationAttributeKey;
import org.xcolab.client.admin.pojo.ContestType;
import org.xcolab.client.contest.ContestClient;
import org.xcolab.client.contest.ContestClientUtil;
import org.xcolab.client.contest.ProposalTemplateClient;
import org.xcolab.client.contest.ProposalTemplateClientUtil;
import org.xcolab.client.contest.exceptions.ContestNotFoundException;
import org.xcolab.client.contest.pojo.Contest;
import org.xcolab.client.contest.pojo.ContestFusion;
import org.xcolab.client.contest.pojo.phases.ContestPhase;
import org.xcolab.client.contest.pojo.templates.ProposalTemplate;
import org.xcolab.client.contest.pojo.templates.ProposalTemplateSection;
import org.xcolab.client.contest.pojo.templates.ProposalTemplateSectionDefinition;
import org.xcolab.client.contest.resources.ProposalResource;
import org.xcolab.client.contest.util.ContestScheduleChangeHelper;
import org.xcolab.client.fusion.utils.FusionStatus;
import org.xcolab.client.members.pojo.Member;
import org.xcolab.client.proposals.enums.ProposalAttributeKeys;
import org.xcolab.client.proposals.exceptions.ProposalNotFoundException;
import org.xcolab.client.proposals.pojo.Proposal;
import org.xcolab.client.proposals.pojo.ProposalDto;
import org.xcolab.client.fusion.pojo.ProposalFusionRequest;
import org.xcolab.client.proposals.pojo.ProposalVersion;
import org.xcolab.client.proposals.pojo.ProposalVersionDto;
import org.xcolab.client.proposals.pojo.attributes.ProposalAttribute;
import org.xcolab.client.proposals.pojo.phases.Proposal2Phase;
import org.xcolab.client.proposals.pojo.tiers.ProposalReference;
import org.xcolab.client.proposals.pojo.tiers.ProposalReferenceDto;
import org.xcolab.commons.exceptions.ReferenceResolutionException;
import org.xcolab.util.activities.enums.ActivityCategory;
import org.xcolab.util.activities.enums.ProposalActivityType;
import org.xcolab.util.http.ServiceRequestUtils;
import org.xcolab.util.http.caching.CacheKeys;
import org.xcolab.util.http.caching.CacheName;
import org.xcolab.util.http.client.RestResource;
import org.xcolab.util.http.client.RestResource1;
import org.xcolab.util.http.client.enums.ServiceNamespace;
import org.xcolab.util.http.client.types.TypeProvider;
import org.xcolab.util.http.dto.DtoUtil;
import org.xcolab.util.http.exceptions.EntityNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ProposalClient {

    private static final Map<ServiceNamespace, ProposalClient> instances = new HashMap<>();

    private final ServiceNamespace serviceNamespace;

    private final RestResource<ProposalDto, Long> proposalResource;
    private final RestResource<Long, Long> proposalThreadIdResource;
    private final RestResource<Long, Long> proposalIdResource;
    private final RestResource1<ProposalVersionDto, Long> proposalVersionResource;
    private final RestResource1<ProposalReferenceDto, Long> proposalReferenceResource;

    private final RestResource1<ProposalFusionRequest, Long> proposalFusionRequestResource;

    //TODO COLAB-2600: methods that use this should be in the service!
    private final ContestClient contestClient;

    private final ActivitiesClient activitiesClient;

    private ProposalClient(ServiceNamespace serviceNamespace) {
        this.serviceNamespace = serviceNamespace;

        proposalResource = new RestResource1<>(ProposalResource.PROPOSAL, ProposalDto.TYPES,
                serviceNamespace);
        proposalIdResource = new RestResource1<>(ProposalResource.PROPOSAL_ID, TypeProvider.LONG,
                serviceNamespace);
        proposalThreadIdResource = new RestResource1<>(ProposalResource.PROPOSAL_THREAD_ID,
                TypeProvider.LONG, serviceNamespace);
        proposalVersionResource = new RestResource1<>(ProposalResource.PROPOSAL_VERSION,
                ProposalVersionDto.TYPES, serviceNamespace);
        proposalReferenceResource = new RestResource1<>(ProposalResource.PROPOSAL_REFERENCE,
                ProposalReferenceDto.TYPES, serviceNamespace);
        contestClient = ContestClient.fromNamespace(serviceNamespace);
        activitiesClient = ActivitiesClient.fromNamespace(serviceNamespace);
        proposalFusionRequestResource = new RestResource1<>(ProposalResource.PROPOSAL_FUSION_REQUEST,
                ProposalFusionRequest.TYPES, serviceNamespace);
       
    }

    public static ProposalClient fromNamespace(ServiceNamespace proposalService) {
        return instances
                .computeIfAbsent(proposalService, k -> new ProposalClient(proposalService));
    }

    public Proposal createProposal(Proposal proposal) {
        return proposalResource
                .create(new ProposalDto(proposal))
                .execute().toPojo(serviceNamespace);
    }

    public List<Proposal> listProposals(long contestId) {
        return listProposals(0, Integer.MAX_VALUE, contestId, null, null, null);
    }

    public List<Proposal> listProposals(int start, int limit, Long contestId, Boolean visible,
        Long contestPhaseId, Integer ribbon) {
        return listProposals(start, limit, null, contestId, null,
            null, visible, contestPhaseId, ribbon);
    }

    public List<Proposal> listProposalsInActiveContests() {
        return DtoUtil.toPojos(proposalResource.list()
                .addRange(0, Integer.MAX_VALUE)
                .queryParam("visible", true)
                .queryParam("contestPrivate", false)
                .queryParam("contestActive", true)
                .withCache(CacheName.PROPOSAL_LIST)
                .execute(), serviceNamespace);
    }

    public List<Proposal> listProposalsInCompletedContests(List<Integer> ribbons) {
        return DtoUtil.toPojos(proposalResource.list()
                .addRange(0, Integer.MAX_VALUE)
                .queryParam("visible", true)
                .queryParam("contestPrivate", false)
                .queryParam("contestActive", false)
                .optionalQueryParam("ribbon", ribbons)
                .withCache(CacheName.PROPOSAL_LIST_CLOSED)
                .execute(), serviceNamespace);
    }

    public List<Proposal> listProposals(int start, int limit, String filterText, Long contestId,
        List<Long> contestTypeIds, List<Long> contestTierIds, Boolean visible, Long contestPhaseId,
        Integer ribbon) {
        return DtoUtil.toPojos(proposalResource.list()
                .addRange(start, limit)
                .optionalQueryParam("filterText", filterText)
                .optionalQueryParam("contestIds", contestId)
                .optionalQueryParam("contestTypeIds", contestTypeIds)
                .optionalQueryParam("contestTierIds", contestTierIds)
                .optionalQueryParam("visible", visible)
                .optionalQueryParam("contestPhaseId", contestPhaseId)
                .optionalQueryParam("ribbon", ribbon)
                .execute(), serviceNamespace);
    }

    public List<Proposal> getProposalsInPublicContests(List<Long> contestTypeIds,
            List<Long> contestTierIds, String filterText) {

        return DtoUtil.toPojos(proposalResource.list()
                .addRange(0, Integer.MAX_VALUE)
                .optionalQueryParam("filterText", filterText)
                .optionalQueryParam("contestTypeIds", contestTypeIds)
                .optionalQueryParam("contestTierIds", contestTierIds)
                .queryParam("visible", true)
                .queryParam("contestPrivate", false)
                .withCache(CacheName.MISC_SHORT)
                .execute(), serviceNamespace);
    }

    public List<Long> listProposalIds(int start, int limit, Long contestId,
            Boolean visible, Long contestPhaseId, Integer ribbon) {
        return proposalIdResource.list()
                .addRange(start, limit)
                .optionalQueryParam("contestId", contestId)
                .optionalQueryParam("visible", visible)
                .optionalQueryParam("contestPhaseId", contestPhaseId)
                .optionalQueryParam("ribbon", ribbon)
                .execute();
    }

    public List<Long> listThreadIds(List<Long> proposalIds, Long contestId, Long contestPhaseId,
            Integer ribbon) {
        return proposalThreadIdResource.list()
                .optionalQueryParam("proposalIds", proposalIds)
                .optionalQueryParam("contestId", contestId)
                .optionalQueryParam("contestPhaseId", contestPhaseId)
                .optionalQueryParam("ribbon", ribbon)
                .execute();
    }

    public List<Proposal> getProposalsInContestPhase(Long contestPhaseId) {
        return listProposals(0, Integer.MAX_VALUE, null, true, contestPhaseId, null);
    }

    public List<Proposal> getAllProposals() {
        return listProposals(0, Integer.MAX_VALUE, null, true, null, null);
    }

    public List<Proposal> getProposalsInContest(Long contestId) {
        ContestPhase cp = contestClient.getActivePhase(contestId);

        return listProposals(0, Integer.MAX_VALUE, null, true, cp.getId(), null);
    }

    //TODO: move to proposals/{proposalId}/teamMembers endpoint
    public List<Member> getProposalMembers(Long proposalId) {
        return proposalResource.elementService(proposalId, "allMembers", Member.TYPES.getTypeReference())
                .getList();
    }

    //TODO: move to proposals/{proposalId}/teamMembers endpoint
    public void removeMemberFromProposalTeam(Long proposalId, Long userId) {
        proposalResource.elementService(proposalId, "removeMemberFromProposalTeam", Boolean.class)
                .queryParam("userId", userId)
                .delete();

        ActivitiesClient activityClient = ActivitiesClient.fromNamespace(serviceNamespace);
        activityClient.createActivityEntry(ProposalActivityType.MEMBER_REMOVED, userId, proposalId);
    }

    public void promoteMemberToProposalOwner(Long proposalId, Long userId) {
        proposalResource.elementService(proposalId, "promoteMemberToProposalOwner", Boolean.class)
                .queryParam("userId", userId)
                .post();
    }

    public Boolean isUserInProposalTeam(Long proposalId, Long memberUserId) {
        return proposalResource.elementService(proposalId, "isUserInProposalTeam", Boolean.class)
                .queryParam("memberUserId", memberUserId)
                .get();
    }

    public List<Proposal> getActiveProposalsInContestPhase(Long contestPhaseId, CacheName cacheName) {
        return DtoUtil.toPojos(proposalResource.list()
                .addRange(0, Integer.MAX_VALUE)
                .optionalQueryParam("visible", true)
                .optionalQueryParam("contestPhaseId", contestPhaseId)
                .withCache(CacheKeys.withClass(ProposalDto.class)
                        .withParameter("contestPhaseId", contestPhaseId).asList(), cacheName)
                .execute(), serviceNamespace);
    }

    public List<Proposal> getActiveProposalsInContestPhase(Long contestPhaseId) {
        return getActiveProposalsInContestPhase(contestPhaseId, CacheName.PROPOSAL_LIST);
    }

    public Proposal createProposal(long authorUserId, long contestPhaseId, boolean publishActivity) {
        return proposalResource.collectionService("createProposal", ProposalDto.class)
                .queryParam("authorUserId", authorUserId)
                .queryParam("contestPhaseId", contestPhaseId)
                .queryParam("publishActivity", publishActivity)
                .post().toPojo(serviceNamespace);
    }

    public List<Proposal> getContestIntegrationRelevantSubproposals(Long proposalId) {
        return DtoUtil.toPojos(proposalResource.elementService(proposalId, "contestIntegrationRelevantSubproposal",
                ProposalDto.TYPES.getTypeReference()).getList(), serviceNamespace);
    }

    public List<Proposal> getLinkingProposalsForUser(long userId) {
        final List<Proposal> userProposals = getMemberProposals(userId);
        List<Proposal> linkingProposals = new ArrayList<>();
        for (Proposal proposal : userProposals) {
            linkingProposals.addAll(getLinkingProposals(proposal.getId()));
        }
        return linkingProposals;
    }

    public List<Proposal> getMemberProposals(Long userId) {
        return DtoUtil.toPojos(proposalResource.collectionService("getMemberProposals", ProposalDto.TYPES.getTypeReference())
                .queryParam("userId", userId)
                .getList(), serviceNamespace);
    }

    public List<Proposal> getLinkingProposals(long proposalId) {
        List<ProposalReference> proposalReferences = getProposalReference(null, proposalId);
        List<Proposal> linkingProposals = new ArrayList<>();
        for (ProposalReference proposalReference : proposalReferences) {
            try {
                final Proposal proposal = getProposal(proposalReference.getProposalId());
                if (!linkingProposals.contains(proposal)) {
                    linkingProposals.add(proposal);
                }
            } catch (ProposalNotFoundException ignored) {

            }
        }
        return linkingProposals;
    }

    public Proposal getProposalByThreadId(long threadId) {
        final ProposalDto proposal = proposalResource.list()
                .queryParam("threadId", threadId)
                .executeWithResult()
                .getOneIfExists();
        if (proposal != null) {
            return proposal.toPojo(serviceNamespace);
        }
        throw new ProposalNotFoundException("No proposal with threadId = " + threadId);
    }

    public Proposal getProposal(long proposalId) throws ProposalNotFoundException {
        return getProposal(proposalId, false);
    }

    public void invalidateProposalCache(long proposalId) {
        ServiceRequestUtils.invalidateCache(CacheKeys.withClass(ProposalDto.class)
                .withParameter("proposalId", proposalId)
                .withParameter("includeDeleted", false).build(), CacheName.MISC_REQUEST);
    }

    public Proposal getProposal(long proposalId, boolean includeDeleted)
            throws ProposalNotFoundException {
        try {
            return proposalResource.get(proposalId)
                    .queryParam("includeDeleted", includeDeleted)
                    .withCache(CacheKeys.withClass(ProposalDto.class)
                                    .withParameter("proposalId", proposalId)
                                    .withParameter("includeDeleted", includeDeleted).build(),
                            CacheName.MISC_REQUEST)
                    .executeChecked().toPojo(serviceNamespace);
        } catch (EntityNotFoundException e) {
            throw new ProposalNotFoundException(proposalId);
        }
    }

    public List<ProposalReference> getProposalReference(Long proposalId, Long subProposalId) {
        return DtoUtil.toPojos(proposalReferenceResource.list()
                .optionalQueryParam("proposalId", proposalId)
                .optionalQueryParam("subProposalId", subProposalId)
                .execute(), serviceNamespace);
    }

    public List<Proposal> getSubproposals(Long proposalId, Boolean includeProposalsInSameContest) {
        return DtoUtil.toPojos(proposalResource
                .elementService(proposalId, "getSubproposals", ProposalDto.TYPES.getTypeReference())
                .queryParam("includeProposalsInSameContest", includeProposalsInSameContest)
                .getList(), serviceNamespace);
    }


    public List<Proposal> getLinkingProposalsForProposalId(Long proposalId) {
        return DtoUtil.toPojos(proposalResource.elementService(proposalId, "listProposalLinks", ProposalDto.TYPES.getTypeReference())
                .getList(), serviceNamespace);
    }

    public Integer getProposalMaterializedPoints(Long proposalId) {
        return proposalResource.elementService(proposalId, "materializedPoints", Integer.class).get();
    }


    public Integer getNumberOfProposalsAlreadyJudgedForJudge(Long userId, Long contestPhaseId) {
        return proposalResource.collectionService("numberOfProposalsAlreadyJudgedForJudge", Integer.class)
                .queryParam("userId", userId)
                .queryParam("contestPhaseId", contestPhaseId)
                .get();

    }
    public Integer getNumberOfProposalsForJudge(Long userId, Long contestPhaseId) {
        return proposalResource.collectionService("numberOfProposalsForJudge", Integer.class)
                .queryParam("userId", userId)
                .queryParam("contestPhaseId", contestPhaseId)
                .get();

    }

    public ProposalReference getProposalReferenceByProposalIdSubProposalId(Long proposalId,
            Long subProposalId) {
        List<ProposalReference> list = DtoUtil.toPojos(proposalReferenceResource.list()
                .optionalQueryParam("proposalId", proposalId)
                .optionalQueryParam("subProposalId", subProposalId)
                .execute(), serviceNamespace);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public boolean updateProposal(Proposal proposal) {
        return proposalResource
                .update(new ProposalDto(proposal), proposal.getId())
                .cacheKey(CacheKeys.withClass(ProposalDto.class)
                                .withParameter("proposalId", proposal.getId())
                                .withParameter("includeDeleted", false).build())
                .execute();
    }

    public boolean deleteProposal(long proposalId) {
        return proposalResource.delete(proposalId).execute();
    }

    public ProposalVersion getProposalVersionByProposalIdVersion(Long proposalId, Integer version) {
        return proposalVersionResource.collectionService("getByProposalIdVersion", ProposalVersionDto.class)
                .queryParam("proposalId", proposalId)
                .queryParam("version", version)
                .get()
                .toPojo(serviceNamespace);
    }
    public List<ProposalVersion> getProposalVersionsGroupedVersionsByContest(Long proposalId, Long contestId, int start, int end) {
        return DtoUtil.toPojos(proposalVersionResource.collectionService("getGroupedVersionsByContest", ProposalVersionDto.TYPES.getTypeReference())
                .queryParam("proposalId", proposalId)
                .queryParam("contestId", contestId)
                .queryParam("start", start)
                .queryParam("end", end)
                .getList(), serviceNamespace);

    }
    public Integer getMaxVersion(long proposalId) {
        return proposalResource.elementService(proposalId, "maxVersion", Integer.class)
                .get();
    }
    public Integer countProposalVersionsGroupedVersionsByContest(Long proposalId, Long contestId) {
        return proposalVersionResource.collectionService("countGroupedVersionsByContest", Integer.class)
                .queryParam("proposalId", proposalId)
                .queryParam("contestId", contestId)
                .get();
    }
    public Integer countProposalVersions(Long proposalId) {
        return proposalVersionResource.collectionService("count", Integer.class)
                .optionalQueryParam("proposalId", proposalId)
                .get();
    }

    public ProposalVersion getProposalVersionByProposal(Long proposalId) {
        return proposalVersionResource.collectionService("getByProposalIdVersion", ProposalVersionDto.class)
                .queryParam("proposalId", proposalId)
                .get()
                .toPojo(serviceNamespace);
    }

    public List<ProposalVersion> getAllProposalVersions(Long proposalId) {
        return DtoUtil.toPojos(proposalVersionResource.list()
                .optionalQueryParam("proposalId", proposalId)
                .execute(), serviceNamespace);
    }

    public ContestType getContestTypeFromProposalId(Long proposalId) {
        try {
            Contest contest = getCurrentContestForProposal(proposalId);
            return ContestTypeClient.getContestType(contest.getContestTypeId());
        } catch (ContestNotFoundException e) {
            throw ReferenceResolutionException.toObject(Contest.class, "")
                    .fromObject(Proposal.class, proposalId);
        }
    }

    public Contest getCurrentContestForProposal(Long proposalId) throws ContestNotFoundException {
        Long contestPhaseId = getLatestContestPhaseIdInProposal(proposalId);
        ContestPhase contestPhase = contestClient.getContestPhase(contestPhaseId);
        return contestClient.getContest(contestPhase.getContestId());

    }

    public Long getLatestContestPhaseIdInProposal(Long proposalId) {
        return proposalResource.elementService(proposalId, "getLatestContestPhaseIdInProposal", Long.class)
                .get();
    }

    public Contest getLatestContestInProposal(Long proposalId) throws ContestNotFoundException {
        return contestClient
                .getContest(getLatestContestPhaseInProposal(proposalId).getContestId());
    }


    public ContestPhase getLatestContestPhaseInProposal(Long proposalId) {
        Long contestPhaseId = getLatestContestPhaseIdInProposal(proposalId);
        return contestClient.getContestPhase(contestPhaseId);
    }


    public void populateTableWithProposal(long proposalId) {
        proposalReferenceResource.collectionService("populateTableWithProposal", Boolean.class)
                .queryParam("proposalId", proposalId)
                .get();

    }

    public boolean isMemberSubscribedToProposal(long proposalId, long userId) {
        return activitiesClient.isSubscribedToActivity(userId,
                ActivityCategory.PROPOSAL, proposalId);
    }

    public void subscribeMemberToProposal(long proposalId, long userId) {

        subscribeMemberToProposal(proposalId, userId, false);
    }

    private void subscribeMemberToProposal(long proposalId, long userId, boolean automatic) {
        activitiesClient.addSubscription(userId, ActivityCategory.PROPOSAL, proposalId, null);
    }

    public void unsubscribeMemberFromProposal(long proposalId, long userId) {
        unsubscribeMemberFromProposal(proposalId, userId, false);

    }

    private void unsubscribeMemberFromProposal(long proposalId, long userId, boolean automatic) {
        activitiesClient.deleteSubscription(userId, ActivityCategory.PROPOSAL, proposalId);
    }

    private static String convertListToGetParameter(List<Long> list, String parameterName) {
        if(list.isEmpty()){
            return "";
        }
        String parameterList = "";
        for(int i=0; i<list.size()-2; i++) {
            parameterList += list.get(i) + "&" + parameterName + "=";
        }
        parameterList += list.get(list.size()-1);
        return parameterList;
    }

    public ProposalFusionRequest createProposalFusionRequest(ProposalFusionRequest data) {
        Long fromContestId = ProposalClientUtil.getProposal(data.getFromProposalId()).getcontestId();
        Long toContestId = ProposalClientUtil.getProposal(data.getToProposalId()).getcontestId();

        Long contestId = ContestClientUtil.getContestFusion(fromContestId, toContestId);
        Contest contest = null;

        if(contestId == null) {
            String templateName=ContestClientUtil.getContest(fromContestId).getTitle()+" "
                    +ContestClientUtil.getContest(toContestId).getTitle();
            ProposalTemplate proposalTemplate=createFusionTemplate(data, templateName);
            contest = createContest("Fusion " +
                    templateName, 1, proposalTemplate.getId());
            ContestClientUtil.createContestFusion(new ContestFusion(contest.getId(), fromContestId, toContestId));
            contestId = contest.getId();

        }else{
            contest = ContestClientUtil.getContest(contestId);
        }
        data.setContestId(contestId);
        data.setStatus(FusionStatus.PENDING.getValue());
        return proposalFusionRequestResource.create(data).execute();
    }


    private ProposalTemplate createFusionTemplate(ProposalFusionRequest proposalFusionRequest, String name){
        Proposal proposalFrom = getProposal(proposalFusionRequest.getFromProposalId());
        Proposal proposalTo = getProposal(proposalFusionRequest.getToProposalId());
        Contest contestFrom= proposalFrom.getContest();
        Contest contestTo= proposalTo.getContest();
        ProposalTemplate originTemplate=ProposalTemplateClientUtil.getProposalTemplate(contestFrom.getProposalTemplateId());
        ProposalTemplate destTemplate=ProposalTemplateClientUtil.getProposalTemplate(contestTo.getProposalTemplateId());
        originTemplate.setName(name);
        ProposalTemplate newTemplate= ProposalTemplateClientUtil.createProposalTemplate(originTemplate);

        List<ProposalTemplateSection> oldSections=ProposalTemplateClientUtil.getProposalTemplateSectionsByTemplateId(originTemplate.getId());
        for(int i=0; i< oldSections.size(); i++){
            createSectionTemplate(oldSections.get(i), newTemplate, i);
        }
        List<ProposalTemplateSection> sections=ProposalTemplateClientUtil.getProposalTemplateSectionsByTemplateId(newTemplate.getId());

        List<ProposalTemplateSection> newSections=ProposalTemplateClientUtil.getProposalTemplateSectionsByTemplateId(destTemplate.getId());

        ProposalTemplateSectionDefinition sectionDefinition;
        ProposalTemplateSectionDefinition newSectionDefinition;
        int weight=sections.size();

        for(int i=0; i<newSections.size(); i++){
            ProposalTemplateSection newSection= newSections.get(i);
            newSectionDefinition =ProposalTemplateClientUtil.getProposalTemplateSectionDefinition(newSection.getSectionDefinitionId());
            String newTitle=newSectionDefinition.getTitle();
            boolean exist=false;
            for(int j=0; j<sections.size(); j++){
                ProposalTemplateSection section= sections.get(j);
                sectionDefinition = ProposalTemplateClientUtil.getProposalTemplateSectionDefinition(section.getSectionDefinitionId());
                String title=sectionDefinition.getTitle();
                if(title.compareToIgnoreCase(newTitle)==0){
                    exist=true;
                }
            }
            if(exist==false){
                weight++;
                createSectionTemplate(newSection, newTemplate, weight);
            }
        }
        return newTemplate;
    }

    private void createSectionTemplate(ProposalTemplateSection section, ProposalTemplate template, int weight){
        ProposalTemplateSection pts = new ProposalTemplateSection();
        pts.setProposalTemplateId(template.getId());
        ProposalTemplateSectionDefinition ptsd =
                ProposalTemplateClientUtil.getProposalTemplateSectionDefinition(section.getSectionDefinitionId());
        ptsd.setId(null);
        ptsd.setCharacterLimit(ptsd.getCharacterLimit()*3);
        ptsd = ProposalTemplateClientUtil.createProposalTemplateSectionDefinition(ptsd);
        pts.setSectionDefinitionId(ptsd.getId());
        pts.setWeight(weight);
        ProposalTemplateClientUtil.createProposalTemplateSection(pts);
    }

    private void generateAttributes(ProposalFusionRequest proposalFusionRequest) {

        Proposal proposal1 = getProposal(proposalFusionRequest.getFromProposalId());
        Proposal proposal2 = getProposal(proposalFusionRequest.getToProposalId());
        Integer proposalAttributeVersion = null;

        final ProposalAttributeClient proposalAttributeClient =
                ProposalAttributeClientUtil.getClient();
        for (ProposalAttribute attribute : proposalAttributeClient
                .getAllProposalAttributes(proposal1.getId())) {
            ProposalAttribute attribute2;
            String value = "";
            attribute2 = proposalAttributeClient
                    .getProposalAttribute(proposal2.getId(), attribute.getName(), (long) 0);
            switch (attribute.getName()) {
                case ProposalAttributeKeys.NAME:
                    value = attribute.getStringValue() + " -- " + attribute2.getStringValue();
                    proposalAttributeClient.setProposalAttribute(proposal1.getAuthorUserId(),
                            proposalFusionRequest.getProposalId(),
                            attribute.getName(), attribute.getAdditionalId(),
                            value, attribute.getNumericValue(),
                            attribute.getRealValue(), proposalAttributeVersion);
                    break;
                case ProposalAttributeKeys.PITCH:
                    value = attribute.getStringValue() + " && " + attribute2.getStringValue();
                    proposalAttributeClient.setProposalAttribute(proposal1.getAuthorUserId(),
                            proposalFusionRequest.getProposalId(),
                            attribute.getName(), attribute.getAdditionalId(),
                            value, attribute.getNumericValue(),
                            attribute.getRealValue(), proposalAttributeVersion);
                    break;
                case ProposalAttributeKeys.DESCRIPTION:
                    value = "<p>Merge Result of:</p>";
                    value+="<ul><li><a href='"+proposal1.getProposalUrl()+"'>"+proposal1.getName();
                    value+="</a></li><li><a href='"+proposal2.getProposalUrl()+"'>"+proposal2.getName();
                    value+="</a></li><ul><br><br>";
                    proposalAttributeClient.setProposalAttribute(proposal1.getAuthorUserId(),
                            proposalFusionRequest.getProposalId(),
                            attribute.getName(), attribute.getAdditionalId(),
                            value, attribute.getNumericValue(),
                            attribute.getRealValue(), proposalAttributeVersion);
                    break;
                case ProposalAttributeKeys.TEAM:
                    String teamA="";
                    String teamB="";
                    if(attribute!=null){
                        teamA=attribute.getStringValue();
                    }
                    if(attribute2!=null){
                        teamB=attribute2.getStringValue();
                    }
                    value = teamA + " & " + teamB;
                    proposalAttributeClient.setProposalAttribute(proposal1.getAuthorUserId(),
                            proposalFusionRequest.getProposalId(),
                            attribute.getName(), attribute.getAdditionalId(),
                            value, attribute.getNumericValue(),
                            attribute.getRealValue(), proposalAttributeVersion);
                    break;
            }

        }
        List<ProposalAttribute> atrFrom=proposalAttributeClient.getAllProposalAttributes(proposal1.getId());
        List<ProposalAttribute> atrTo=proposalAttributeClient.getAllProposalAttributes(proposal2.getId());

        Proposal mergeProposal = getProposal(proposalFusionRequest.getProposalId());
        Contest mergeContest= mergeProposal.getContest();
        List<ProposalTemplateSection> sections=ProposalTemplateClientUtil
                .getProposalTemplateSectionsByTemplateId(mergeContest.getProposalTemplateId());
        ProposalAttribute proposalFusionAttribute=new ProposalAttribute();
        for(int i=0; i<sections.size(); i++){
            ProposalTemplateSection section= sections.get(i);
            ProposalTemplateSectionDefinition psdFusion=ProposalTemplateClientUtil
                    .getProposalTemplateSectionDefinition(section.getSectionDefinitionId());
            String value="";
            for(int j=0; j<atrFrom.size(); j++){
                ProposalAttribute attribute=atrFrom.get(j);
                    long id=attribute.getAdditionalId();
                    if(id!=0) {
                        ProposalTemplateSectionDefinition psdProposal =
                                ProposalTemplateClientUtil.getProposalTemplateSectionDefinition(id);
                        if (psdProposal.getTitle().compareToIgnoreCase(psdFusion.getTitle()) == 0) {
                            value = value + attribute.getStringValue() + "</hr>";
                        }
                    }

            }
            for(int k=0; k<atrTo.size(); k++){
                ProposalAttribute attribute=atrTo.get(k);
                long id = attribute.getAdditionalId();
                if(id!=0) {
                    ProposalTemplateSectionDefinition psdProposal =
                            ProposalTemplateClientUtil.getProposalTemplateSectionDefinition(id);
                    if (psdProposal.getTitle().compareToIgnoreCase(psdFusion.getTitle()) == 0) {
                        value = value + attribute.getStringValue() ;
                    }
                }

            }
            proposalFusionAttribute.setProposalId(proposalFusionRequest.getProposalId());
            proposalFusionAttribute.setVersion(1);
            proposalFusionAttribute.setName(ProposalAttributeKeys.SECTION);
            proposalFusionAttribute.setAdditionalId(section.getSectionDefinitionId());
            proposalFusionAttribute.setNumericValue(0l);
            proposalFusionAttribute.setRealValue(0d);
            proposalFusionAttribute.setStringValue(value);
            addProposalAttribute(proposalFusionRequest.getFromUserId(), proposalFusionAttribute);
        }

    }

    private void addProposalAttribute(Long userId, ProposalAttribute attribute){
        final ProposalAttributeClient proposalAttributeClient =
                ProposalAttributeClientUtil.getClient();
        proposalAttributeClient.setProposalAttribute(userId,
                attribute.getProposalId(),
                attribute.getName(), attribute.getAdditionalId(),
                attribute.getStringValue(), attribute.getNumericValue(),
                attribute.getRealValue(), attribute.getVersion());
    }


    public ProposalFusionRequest getFusionRequestById(Long id){
        return proposalFusionRequestResource.get(id).execute();
    }

    public ArrayList<ProposalFusionRequest> listAllFusions(){
        List<ProposalFusionRequest> lq = proposalFusionRequestResource.list().execute();

        return new ArrayList<ProposalFusionRequest>(lq);
    }

    public ArrayList<ProposalFusionRequest> listByFromUserID(Long from_user_id){
        List<ProposalFusionRequest> lq = proposalFusionRequestResource.list()
                .queryParam("from_user_id", from_user_id).execute();

        return new ArrayList<ProposalFusionRequest>(lq);
    }

    public ArrayList<ProposalFusionRequest> listByToUserID(Long to_user_id){
        List<ProposalFusionRequest> lq = proposalFusionRequestResource.list()
                .queryParam("to_user_id", to_user_id).execute();

        return new ArrayList<ProposalFusionRequest>(lq);
    }

    public ProposalFusionRequest acceptFusion(Long fusionId){
        ProposalFusionRequest proposalFusionRequest = getFusionRequestById(fusionId);
        if(proposalFusionRequest != null) {
            proposalFusionRequest.setStatus(FusionStatus.ACCEPTED.getValue());
            Long proposalId = createProposal(proposalFusionRequest.getFromUserId(),
                    ContestClientUtil.getActivePhase(proposalFusionRequest.getContestId()).getId(), true).getId();
            MembershipClientUtil.addUserToProposalTeam(proposalFusionRequest.getToUserId(), getProposal(proposalId));
            proposalFusionRequest.setProposalId(proposalId);
            generateAttributes(proposalFusionRequest);
            proposalFusionRequestResource.update(proposalFusionRequest, fusionId).execute();
        }
        return proposalFusionRequest;
    }

    public ProposalFusionRequest rejectFusion(Long fusionId){
        ProposalFusionRequest proposalFusionRequest = getFusionRequestById(fusionId);

        if(proposalFusionRequest != null) {
            proposalFusionRequest.setStatus(FusionStatus.REJECTED.getValue());
            proposalFusionRequestResource.update(proposalFusionRequest, fusionId).execute();
        }

        return proposalFusionRequest;
    }


    private Contest createContest(String title, long authorUserId, long templateId) {
        Contest contest = ContestClientUtil.createContest(authorUserId, title);
        contest.setContestYear((long) DateTime.now().getYear());
        contest.setContestPrivate(false);
        contest.setShowInTileView(true);
        contest.setShowInListView(true);
        contest.setShowInOutlineView(true);
        contest.setProposalTemplateId(templateId);
        final Long contestScheduleId = ConfigurationAttributeKey
                .DEFAULT_CONTEST_SCHEDULE_ID.get();
        contest.setContestScheduleId(contestScheduleId);
        contest.setContestTypeId(0l);
        ContestClientUtil.updateContest(contest);
        ContestScheduleChangeHelper
                changeHelper = new ContestScheduleChangeHelper(contest.getId(), contestScheduleId);
        changeHelper.changeScheduleForBlankContest();
        return contest;
    }

}
