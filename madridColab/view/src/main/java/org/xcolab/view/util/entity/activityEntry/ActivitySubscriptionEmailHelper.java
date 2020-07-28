package org.xcolab.view.util.entity.activityEntry;

import org.apache.commons.collections4.comparators.ComparatorChain;
import org.apache.commons.lang3.StringUtils;
import org.jooq.util.derby.sys.Sys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.xcolab.client.activities.ActivitiesClientUtil;
import org.xcolab.client.activities.pojo.ActivityEntry;
import org.xcolab.client.activities.pojo.ActivitySubscription;
import org.xcolab.client.admin.AdminClient;
import org.xcolab.client.admin.attributes.configuration.ConfigurationAttributeKey;
import org.xcolab.client.admin.attributes.platform.PlatformAttributeKey;
import org.xcolab.client.admin.exceptions.ConfigurationAttributeNotFoundException;
import org.xcolab.client.admin.pojo.ConfigurationAttribute;
import org.xcolab.client.comment.exceptions.CommentNotFoundException;
import org.xcolab.client.comment.pojo.Comment;
import org.xcolab.client.comment.util.CommentClientUtil;
import org.xcolab.client.contest.ContestClientUtil;
import org.xcolab.client.contest.pojo.Contest;
import org.xcolab.client.emails.EmailClient;
import org.xcolab.client.members.MembersClient;
import org.xcolab.client.members.MessagingClient;
import org.xcolab.client.members.exceptions.MemberNotFoundException;
import org.xcolab.client.members.pojo.Member;
import org.xcolab.client.members.pojo.MessagingUserPreference;
import org.xcolab.client.proposals.ProposalClientUtil;
import org.xcolab.client.proposals.exceptions.ProposalNotFoundException;
import org.xcolab.client.proposals.pojo.Proposal;
import org.xcolab.entity.utils.TemplateReplacementUtil;
import org.xcolab.util.activities.enums.ActivityCategory;
import org.xcolab.commons.html.HtmlUtil;
import org.xcolab.util.activities.enums.ContestActivityType;
import org.xcolab.util.activities.enums.ProposalActivityType;
import org.xcolab.view.activityentry.ActivityEntryHelper;
import org.xcolab.view.util.entity.NotificationUnregisterUtils;
import org.xcolab.view.util.entity.subscriptions.ActivitySubscriptionConstraint;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.InternetAddress;

@Component
public class ActivitySubscriptionEmailHelper {

    private static final Logger _log =
            LoggerFactory.getLogger(ActivitySubscriptionEmailHelper.class);

    private static final Object mutex = new Object();

    private static Instant lastEmailNotification = Instant.now();
    private static Instant lastDailyEmailNotification = Instant.now();
    private Instant lastWeeklyEmailNotification = Instant.now();

    private static final String FAQ_DIGEST_URL_PATH = "/faqs#digest";

    private static final String UNSUBSCRIBE_INSTANT_NOTIFICATION_TEXT =
            "You are receiving this message because you subscribed to a <contest/>, <proposal/> "
                    + "or discussion post on the <colab-name/>.  "
                    + "To receive all notifications in a daily digest, please click "
                    + "<a href='FAQ_DIGEST_LINK_PLACEHOLDER'>here</a> for instructions.  "
                    + "To stop receiving notifications from this <contest/>, <proposal/> or "
                    + "discussion post, please click "
                    + "<a href='UNSUBSCRIBE_SUBSCRIPTION_LINK_PLACEHOLDER'>here</a>.";

    private static final String FAQ_DIGEST_LINK_PLACEHOLDER = "FAQ_DIGEST_LINK_PLACEHOLDER";

    private static final String UNSUBSCRIBE_SUBSCRIPTION_LINK_PLACEHOLDER =
            "UNSUBSCRIBE_SUBSCRIPTION_LINK_PLACEHOLDER";


    private static final String MESSAGE_FOOTER_TEMPLATE = "<br /><br />\n<hr /><br />\n"
            + "To configure your notification preferences, visit your "
            + "<a href=\"USER_PROFILE_LINK\">profile</a> page";

    private static final String USER_PROFILE_LINK_PLACEHOLDER = "USER_PROFILE_LINK";

    private static final String USER_PROFILE_LINK_TEMPLATE =
            "DOMAIN_PLACEHOLDER/members/profile/USER_ID";

    private static final String USER_ID_PLACEHOLDER = "USER_ID";

    private static final String DOMAIN_PLACEHOLDER = "DOMAIN_PLACEHOLDER";

    private static final String DAILY_DIGEST_NOTIFICATION_SUBJECT_TEMPLATE =
            "<colab-name/> Activities – Daily Digest DATE";

    private static final String DAILY_DIGEST_NOTIFICATION_SUBJECT_DATE_PLACEHOLDER = "DATE";

    private static final String DAILY_DIGEST_ENTRY_TEXT = "<colab-name/> Digest for DATE";

    private static final String UNSUBSCRIBE_DAILY_DIGEST_NOTIFICATION_TEXT =
            "You are receiving this message because you subscribed to receiving a daily digest of"
                    + " activities on the <colab-name/>.  "
                    + "To stop receiving these notifications, please click "
                    + "<a href='UNSUBSCRIBE_SUBSCRIPTION_LINK_PLACEHOLDER'>here</a>.";

    private static final String PROTOTYPING_SPACE_CREATED=
            "<li>USER ha abierto el espacio de protipado PROTOTYPING_SPACE</li>";

    private static final String CONVERSATION_THREAD_CREATED=
            "<li>USER ha abierto el hilo de conversación CONVERSATION_THREAD</li>";

    private static final String COMMENT_ADDED_CONVERSATION_THREAD=
            "<li>USER ha publicado un comentario en el hilo de conversación CONVERSATION_THREAD</li>";

    private static final String COMMENT_ADDED_PROTOTYPING_SPACE=
            "<li>USER ha publicado un comentario en el espacio de prototipado PROTOTYPING_SPACE</li>";

    private static final String COMMENT_ADDED_PROPOSAL_PROTOTYPING_SPACE=
            "<li>USER ha publicado un comentario sobre la propuesta PROPOSAL del espacio de "
                    + "prototipado PROTOTYPING_SPACE</li>";

    private static final String PROPOSAL_CREATED_PROTOTYPING_SPACE=
            "<li>USER ha publicado la propuesta PROPOSAL en el espacio de prototipado "
                    + "PROTOTYPING_SPACE</li>";

    private static final String USER_EDIT_LINK="USER_EDIT_LINK";

    private static final String UNSUBSCRIBE_WEEKLY_DIGEST_NOTIFICATION_TEXT =
            "Gracias por ser parte de la comunidad <colab-name/>, \n<br />"
                    + "Si quiere dejar de recibir este resumen de actividad semanal, acceda a la "
                    + "configuración de <a href=\"USER_EDIT_LINK\"> notificaciones"
                    + " </a> y desactive las notificaciones semanales.\n<br><br />"
                    + "Atentamente, \n<br />"
                    + "El equipo de <colab-name/>.";

    private static final String WEEKLY_DIGEST_NOTIFICATION_SUBJECT_TEMPLATE =
            "<colab-name/> Resumen semanal de actividad para el DATE";

    private static final String WEEKLY_DIGEST_NOTIFICATION_SUBJECT_DATE_PLACEHOLDER = "DATE";

    private static final String WEEKLY_DIGEST_ENTRY_TEXT = "Querido usuario <colab-name/>, \n "
            + "Esto es lo que ha ocurrido en <colab-name/> durante la última semana:";



    private final ActivityEntryHelper activityEntryHelper;

    private static ConfigurationAttribute weeklyConfigurationAttribute;

    @Autowired
    public ActivitySubscriptionEmailHelper(ActivityEntryHelper activityEntryHelper) {
        this.activityEntryHelper = activityEntryHelper;
    }

    public void sendEmailNotifications() {
        // INSERT INTO `xcolab_ConfigurationAttribute` (`name`, `additionalId`, `numericValue`, `stringValue`, `realValue`) VALUES ('DAILY_DIGEST_LAST_EMAIL_NOTIFICATION', '0', '0', '2017-01-03 00:00:00', '0');
        String DAILY_DIGEST_LAST_EMAIL_NOTIFICATION =
                ConfigurationAttributeKey.DAILY_DIGEST_LAST_EMAIL_NOTIFICATION.get();

       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (!DAILY_DIGEST_LAST_EMAIL_NOTIFICATION.isEmpty()) {

            try {
                lastEmailNotification = sdf.parse(DAILY_DIGEST_LAST_EMAIL_NOTIFICATION).toInstant();
                lastDailyEmailNotification =
                        sdf.parse(DAILY_DIGEST_LAST_EMAIL_NOTIFICATION).toInstant();
            } catch (ParseException e) {
                lastEmailNotification = Instant.now();
            }
        }
        String WEEKLY_DIGEST_LAST_EMAIL_NOTIFICATION;
        try {
            weeklyConfigurationAttribute= AdminClient
                    .getConfigurationAttribute("WEEKLY_DIGEST_LAST_EMAIL_NOTIFICATION", "");
            WEEKLY_DIGEST_LAST_EMAIL_NOTIFICATION=weeklyConfigurationAttribute.getStringValue();
        }catch (ConfigurationAttributeNotFoundException exception){
            _log.error("Can't found configuration attribute");
            weeklyConfigurationAttribute= new ConfigurationAttribute();
            weeklyConfigurationAttribute.setName("WEEKLY_DIGEST_LAST_EMAIL_NOTIFICATION");
            Instant now= Instant.now();
            Date actualDate = Date.from(now);
            WEEKLY_DIGEST_LAST_EMAIL_NOTIFICATION= sdf.format(actualDate);
            weeklyConfigurationAttribute.setStringValue(WEEKLY_DIGEST_LAST_EMAIL_NOTIFICATION);
        }

        AdminClient.updateConfigurationAttribute(weeklyConfigurationAttribute);

        if(!WEEKLY_DIGEST_LAST_EMAIL_NOTIFICATION.isEmpty()){
            try {
                lastWeeklyEmailNotification =
                        sdf.parse(WEEKLY_DIGEST_LAST_EMAIL_NOTIFICATION).toInstant();

            } catch (ParseException e) {
                lastWeeklyEmailNotification = Instant.now();
            }
        }
        synchronized (mutex) {
            sendInstantNotifications();
            sendDailyNotifications();
            sendWeeklyNotifications();
        }
    }

    private void sendInstantNotifications() {
        List<ActivityEntry> res = getActivitiesAfter(lastEmailNotification);
        if (!res.isEmpty()) {
            _log.info("Sending instant notifications for {} activities", res.size());
        }
        for (ActivityEntry activity : res) {
            try {
                sendInstantNotifications(activity);
            } catch (Throwable e) {
                _log.error("Can't process activity when sending notifications ( {} )",
                        activity, e);
            }
        }
        lastEmailNotification = Instant.now();
    }

    private void sendDailyNotifications() {
        Instant now = Instant.now();
        final Long dailyDigestTriggerHour =
                ConfigurationAttributeKey.DAILY_DIGEST_TRIGGER_HOUR.get();

        // Send the daily digest at the predefined hour only
        if (now.minus(1, ChronoUnit.HOURS).isAfter(lastDailyEmailNotification)
                && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == dailyDigestTriggerHour) {
            List<ActivityEntry> res = getActivitiesAfter(lastDailyEmailNotification);
            sendDailyDigestNotifications(res);
            lastDailyEmailNotification = now;

        }
    }

    private void sendWeeklyNotifications(){
        //Instant now= Instant.now();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Instant now=Instant.now();
        LocalDateTime ldt = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
        final Long weeklyDigestTriggerHour=
                ConfigurationAttributeKey.WEEKLY_DIGEST_TRIGGER_HOUR.get();
        Instant dateToSend=lastWeeklyEmailNotification.plus(7, ChronoUnit.DAYS);
        int hourToSendUTC=dateToSend.atZone(ZoneOffset.UTC).getHour();
        int minToSendUTC=dateToSend.atZone(ZoneOffset.UTC).getMinute();
        int actualHourUTC=now.atZone(ZoneOffset.UTC).getHour();
        int hourDifference=ldt.getHour()-actualHourUTC;
        long hourAdjust=(weeklyDigestTriggerHour-hourDifference)-hourToSendUTC;
        dateToSend=dateToSend.plus(hourAdjust, ChronoUnit.HOURS);
        dateToSend=dateToSend.minus(minToSendUTC, ChronoUnit.MINUTES);
        if (now.isAfter(dateToSend)
                && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == weeklyDigestTriggerHour) {
            List<ActivityEntry> activityEntries = getAllActivitiesAfter(lastWeeklyEmailNotification);
            List<Contest> contestList= getContestsAfter(lastWeeklyEmailNotification);
            List<Member> subsMembers=getUsersForWeeklyDigest();
            int totalActivities=activityEntries.size()+contestList.size();
            lastWeeklyEmailNotification=Instant.now();
            if(totalActivities>0) {
                sendWeeklyDigestNotifications(activityEntries, contestList, subsMembers);
            }
            Date sentDate= Date.from(lastWeeklyEmailNotification);
            weeklyConfigurationAttribute.setStringValue(sdf.format(sentDate));
            AdminClient.updateConfigurationAttribute(weeklyConfigurationAttribute);
        }
    }

    private void sendDailyDigestNotifications(List<ActivityEntry> activities) {
        Map<Long, List<ActivityEntry>> userActivitiesDigestMap =
                getUserToActivityDigestMap(activities);

        String subject = StringUtils.replace(DAILY_DIGEST_NOTIFICATION_SUBJECT_TEMPLATE,
                DAILY_DIGEST_NOTIFICATION_SUBJECT_DATE_PLACEHOLDER,
                instantToFormattedString(lastDailyEmailNotification));
        // Send the digest to each user which is included in the set of subscriptions
        for (Map.Entry<Long, List<ActivityEntry>> entry : userActivitiesDigestMap.entrySet()) {
            try {
                final Member recipient = MembersClient.getMember(entry.getKey());
                final List<ActivityEntry> userDigestActivities = entry.getValue();
                String body = getDigestMessageBody(userDigestActivities, 
                        DAILY_DIGEST_NOTIFICATION_SUBJECT_DATE_PLACEHOLDER, lastDailyEmailNotification);
                String unsubscribeFooter = getUnsubscribeDailyDigestFooter(
                        NotificationUnregisterUtils.getActivityUnregisterLink(recipient));

                sendEmailMessage(recipient, subject, body, unsubscribeFooter,
                        PlatformAttributeKey.COLAB_URL
                                .get(), recipient.getId(), true);
            } catch (MemberNotFoundException ignored) {
                _log.error("sendDailyDigestNotifications: MemberNotFound : {}",
                        ignored.getMessage());
            }
        }
    }

    public void sendWeeklyDigestNotifications(List<ActivityEntry> activityEntries, List<Contest>
            contests, List<Member> subsMembers){

        String subject = StringUtils.replace(WEEKLY_DIGEST_NOTIFICATION_SUBJECT_TEMPLATE,
                WEEKLY_DIGEST_NOTIFICATION_SUBJECT_DATE_PLACEHOLDER,
                instantToFormattedString(lastWeeklyEmailNotification));
        // Send weekly report to each user subscribed
        String body = getWeeklyMessageBody(activityEntries, contests);
        for (Member member: subsMembers) {
            try {
                final Member recipient = MembersClient.getMember(member.getId());
                String unsubscribeFooter = getNotificationConfigurationWeeklyFooter(member);
                sendEmailMessage(recipient, subject, body, unsubscribeFooter,
                        PlatformAttributeKey.COLAB_URL
                                .get(), recipient.getId(), false);
            } catch (MemberNotFoundException ignored) {
                _log.error("sendDailyDigestNotifications: MemberNotFound : {}",
                        ignored.getMessage());
            }
        }

    }
    private String getUnsubscribeDailyDigestFooter(String unsubscribeUrl) {
        return StringUtils.replace(UNSUBSCRIBE_DAILY_DIGEST_NOTIFICATION_TEXT,
                UNSUBSCRIBE_SUBSCRIPTION_LINK_PLACEHOLDER, unsubscribeUrl);
    }

    private String getNotificationConfigurationWeeklyFooter(Member member){
     return StringUtils.replace(UNSUBSCRIBE_WEEKLY_DIGEST_NOTIFICATION_TEXT, USER_EDIT_LINK
     , PlatformAttributeKey.COLAB_URL.get()+member.getProfileEditUrl());
    }

    private String getWeeklyMessageBody(List<ActivityEntry> activities, List<Contest> contestList){
        Comparator<ActivityEntry> activityCategoryComparator =
                Comparator.comparing(ActivityEntry::getActivityCategory);
        Comparator<ActivityEntry> activityTypeComparator=
                Comparator.comparing(ActivityEntry::getActivityType);
        Comparator<ActivityEntry> socialActivityCreatedAtComparator =
                (o1, o2) -> (int) (o1.getCreatedAt().getTime() - o2.getCreatedAt().getTime());
        ComparatorChain comparatorChain = new ComparatorChain();
        comparatorChain.addComparator(activityCategoryComparator);
        comparatorChain.addComparator(activityTypeComparator);
        comparatorChain.addComparator(socialActivityCreatedAtComparator);
        StringBuilder body = new StringBuilder();
        body.append(WEEKLY_DIGEST_ENTRY_TEXT);
        body.append("<br/><br/><div><ul>");

        for (Contest contest: contestList){
          String title=contest.getTitle();
          String subTittle=title.substring(0, 4);
          Member user;
          if(subTittle.compareToIgnoreCase("E.P.")==0){
              try {
                  user = MembersClient.getMember(contest.getAuthorUserId());
              } catch (MemberNotFoundException e) {
                  user = null;
              }
              String prototypingSpaceCreated=
                      StringUtils.replace(PROTOTYPING_SPACE_CREATED, "USER",
                              getUserLink(user));
              prototypingSpaceCreated=StringUtils.replace(prototypingSpaceCreated,
                      "PROTOTYPING_SPACE", getContestLink(contest));
              body.append(prototypingSpaceCreated);
          }
        }
        try {
            activities.sort(comparatorChain);
            Contest contest;
            Proposal proposal;
            Member user;
            for(ActivityEntry activityEntry: activities){
                try {
                    user = MembersClient.getMember(activityEntry.getUserId());
                } catch (MemberNotFoundException e) {
                    user = null;
                }
                if(activityEntry.getActivityCategoryEnum()==ActivityCategory.CONTEST){
                    if(activityEntry.getActivityTypeEnum()==ContestActivityType.PROPOSAL_CREATED){
                        try {
                            proposal = ProposalClientUtil.getProposal(activityEntry.getAdditionalId(), true);
                        } catch (ProposalNotFoundException e) {
                            proposal=null;
                        }
                        contest=proposal.getContest();
                        if(isPrototypingSpace(contest)){
                            String proposalCreatedPE=StringUtils.replace(
                                    PROPOSAL_CREATED_PROTOTYPING_SPACE, "USER",
                                    getUserLink(user));
                            proposalCreatedPE=StringUtils.replace(proposalCreatedPE,
                                    "PROPOSAL", getProposalLink(proposal));
                            proposalCreatedPE=StringUtils.replace(proposalCreatedPE,
                                    "PROTOTYPING_SPACE", getContestLink(contest));
                            body.append(proposalCreatedPE);
                        }
                        if(isConversationThread(proposal)){
                            String conversationTread=StringUtils.replace(CONVERSATION_THREAD_CREATED,
                                    "USER", getUserLink(user));
                            conversationTread=StringUtils.replace(conversationTread,
                                    "CONVERSATION_THREAD", getProposalLink(proposal));
                            body.append(conversationTread);
                        }

                    }
                    if(activityEntry.getActivityTypeEnum()==ContestActivityType.COMMENT_ADDED){
                        try {
                            contest = ContestClientUtil.getContest(activityEntry.getCategoryId());
                        } catch (ProposalNotFoundException e) {
                            contest = null;
                        }
                        if(isPrototypingSpace(contest)){
                            String commentPrototypingSpace= StringUtils.replace(
                                    COMMENT_ADDED_PROTOTYPING_SPACE, "USER",
                                    getUserLink(user));
                            commentPrototypingSpace= StringUtils.replace(commentPrototypingSpace,
                                    "PROTOTYPING_SPACE", getContestLink(contest));
                            body.append(commentPrototypingSpace);
                        }
                    }

                }
                if(activityEntry.getActivityCategoryEnum()==ActivityCategory.PROPOSAL){
                    if(activityEntry.getActivityTypeEnum()==ProposalActivityType.COMMENT_ADDED){
                        try {
                            proposal = ProposalClientUtil.getProposal(activityEntry.getCategoryId(), true);
                        } catch (ProposalNotFoundException e) {
                            proposal=null;
                        }
                        contest=proposal.getContest();
                        if(isPrototypingSpace(contest)){
                            String commentProposalPE= StringUtils.replace(
                                    COMMENT_ADDED_PROPOSAL_PROTOTYPING_SPACE, "USER",
                                    getContestLink(contest));
                            commentProposalPE= StringUtils.replace(commentProposalPE,
                                    "PROPOSAL", getProposalLink(proposal));
                            commentProposalPE= StringUtils.replace(commentProposalPE,
                                    "PROTOTYPING_SPACE", getContestLink(contest));
                            body.append(commentProposalPE);
                        }
                        if(isConversationThread(proposal)){
                            String commentCT=StringUtils.replace(COMMENT_ADDED_CONVERSATION_THREAD,
                                    "USER", getUserLink(user));
                            commentCT= StringUtils.replace(commentCT, "CONVERSATION_THREAD",
                                    getProposalLink(proposal));
                            body.append(commentCT);
                        }
                    }
                }

            }

        }catch (IllegalArgumentException comparatorFailed) {
            _log.debug("Comparator failed {}", comparatorFailed.getLocalizedMessage());
        }
        body.append("</ul></div><br></br>");
        return body.toString();
    }

    private String getUserLink(Member member){
        return "<a href='" + member.getProfileLinkUrl()+ "'>" + member.getDisplayName()+ "</a>";
    }

    private String getContestLink(Contest contest){
        return "<a href='" + contest.getContestLinkUrl()+ "'>" + contest.getTitle()+ "</a>";
    }

    private String getProposalLink(Proposal proposal){
        return  "<a href='" + proposal.getProposalUrl() + "'>" + proposal.getName() + "</a>";
    }

    private boolean isConversationThread(Proposal proposal){
       if(proposal!=null) {
           String subProposal = proposal.getName().substring(0, 4);
           if (subProposal.compareToIgnoreCase("H.C.") == 0) {
               return true;
           } else {
               return false;
           }
       }else{
           return false;
       }
    }

    private boolean isPrototypingSpace(Contest contest){
        if(contest!=null) {
            String subContest = contest.getTitle().substring(0, 4);
            if (subContest.compareToIgnoreCase("E.P.") == 0) {
                return true;
            } else {
                return false;
            }
        }else{
            return false;
        }
    }

    private String getDigestMessageBody(List<ActivityEntry> userDigestActivities,
            String NOTIFICATION_SUBJECT_DATE_PLACEHOLDER, Instant date) {
        Comparator<ActivityEntry> activityCategoryComparator =
                Comparator.comparing(ActivityEntry::getActivityCategory);
        Comparator<ActivityEntry> socialActivityCreatedAtComparator =
                (o1, o2) -> (int) (o1.getCreatedAt().getTime() - o2.getCreatedAt().getTime());

        ComparatorChain comparatorChain = new ComparatorChain();
        comparatorChain.addComparator(activityCategoryComparator);
        comparatorChain.addComparator(socialActivityCreatedAtComparator);
        StringBuilder body = new StringBuilder();
        try {
            userDigestActivities.sort(comparatorChain);

            body.append(StringUtils.replace(DAILY_DIGEST_ENTRY_TEXT,
                    NOTIFICATION_SUBJECT_DATE_PLACEHOLDER,
                    instantToFormattedString(date)));
            body.append("<br/><br/>");

            for (ActivityEntry activityEntry : userDigestActivities) {
                //prevent null pointer exceptions which might happen at this point
                if (activityEntry == null) {
                    continue;
                }
                if (activityEntry.getActivityCategoryEnum() == ActivityCategory.DISCUSSION) {
                    try {
                        StringBuilder bodyWithComment = new StringBuilder();
                        bodyWithComment.append(activityEntryHelper.getActivityBody(activityEntry));
                        bodyWithComment.append("<br><br><div style='margin-left:20px;>");
                        bodyWithComment.append("<div style='margin-top:14pt;margin-bottom:14pt;'>");
                        Long commentId = activityEntry.getAdditionalId();
                        Comment comment = CommentClientUtil.getComment(commentId, true);
                        if (comment.getDeletedAt() != null) {
                            bodyWithComment.append("<b>COMMENT ALREADY DELETED</b>");
                        }
                        bodyWithComment.append(comment.getContent());
                        bodyWithComment.append("</div></div>");
                        body.append("<div style='margin-left: 10px'>")
                                .append(bodyWithComment.toString()).append("</div><br/><br/>");
                    } catch (CommentNotFoundException ex) {
                        body.append("<div style='margin-left: 10px'>").append(activityEntry)
                                .append("</div><br/><br/>");
                    }
                } else {
                    body.append("<div style='margin-left: 10px'>")
                            .append(activityEntryHelper.getActivityBody(activityEntry))
                            .append("</div><br/><br/>");
                }

            }
        } catch (IllegalArgumentException comparatorFailed) {
            _log.debug("Comparator failed {}", comparatorFailed.getLocalizedMessage());
        }
        return body.toString();
    }


    private Map<Long, List<ActivityEntry>> getUserToActivityDigestMap(
            List<ActivityEntry> activities) {
        Map<Long, List<ActivityEntry>> userDigestActivitiesMap = new HashMap<>();

        for (ActivityEntry activity : activities) {
            // Aggregate all activities for all users
            for (ActivitySubscription subscriptionObj : getActivitySubscribers(activity)) {

                Long recipientId = subscriptionObj.getReceiverUserId();

                if (subscriptionObj.getReceiverUserId() == activity.getUserId().longValue()) {
                    continue;
                }

                final MessagingUserPreference messagingPreferences =
                        MessagingClient.getMessagingPreferencesForMember(recipientId);
                if (messagingPreferences.getEmailOnActivity() && messagingPreferences
                        .getEmailActivityDailyDigest()) {

                    List<ActivityEntry> userDigestActivities = userDigestActivitiesMap
                            .computeIfAbsent(recipientId, k -> new ArrayList<>());
                    userDigestActivities.add(activity);
                }
            }
        }

        return userDigestActivitiesMap;
    }


    private List<Member> getUsersForWeeklyDigest(){
     List<Member> subscribedMembers= new ArrayList<>();
     List<Member> allMembers=MembersClient.listAllMembers();
     for(Member m: allMembers){
         final MessagingUserPreference messagingUserPreference=
                 MessagingClient.getMessagingPreferencesForMember(m.getId());
         if(messagingUserPreference.getId()!=null&&
                 messagingUserPreference.getEmailActivityWeeklyDigest()==true){
             subscribedMembers.add(m);
         }
     }
        return subscribedMembers;
    }

    private List<ActivityEntry> getAllActivitiesAfter(Instant minDate){
        List<ActivityEntry> activityObjects =
                ActivitiesClientUtil.getActivityEntriesAfter(Date.from(minDate));
        List<ActivityEntry> activityEntries= new ArrayList<>();
        for(ActivityEntry activityEntry: activityObjects){
            if(activityEntry.getActivityCategoryEnum()==ActivityCategory.CONTEST||
            activityEntry.getActivityCategoryEnum()==ActivityCategory.PROPOSAL){
                activityEntries.add(activityEntry);
            }
        }
        return activityEntries;
    }

    private List<ActivityEntry> getActivitiesAfter(Instant minDate) {

       List<ActivityEntry> activityObjects =
                ActivitiesClientUtil.getActivityEntriesAfter(Date.from(minDate));
       // ContestClientUtil

        // clean list of activities first in order not to send out activities concerning the same
        // proposal multiple times
        ActivityEntryMessageLimitationHelper h = new ActivityEntryMessageLimitationHelper(
                ActivityCategory.PROPOSAL);

        return h.process(activityObjects);
    }

    private List<Contest> getContestsAfter(Instant minDate){
        List<Contest> contests= ContestClientUtil.getContestAfter(Date.from(minDate));
        return contests;
    }

    private void sendInstantNotifications(ActivityEntry activity) {

        String subject = clearLinksInSubject(activityEntryHelper.getActivityTitle(activity)) + " ";
        String messageTemplate = activityEntryHelper.getActivityBody(activity);

        Set<Member> recipients = new HashSet<>();
        Map<Long, ActivitySubscription> subscriptionsPerUser = new HashMap<>();

        for (Object subscriptionObj : getActivitySubscribers(activity)) {
            ActivitySubscription subscription = (ActivitySubscription) subscriptionObj;

            if (subscription.getReceiverUserId() == activity.getUserId().longValue()) {
                continue;
            }
            try {
                Member member = MembersClient.getMember(subscription.getReceiverUserId());
                recipients.add(member);
                subscriptionsPerUser.put(member.getId(), subscription);
            } catch (MemberNotFoundException ignored) {
            }


            // map users to subscriptions for unregistration links

        }
        for (Member recipient : recipients) {
            final MessagingUserPreference messagingPreferences =
                    MessagingClient.getMessagingPreferencesForMember(recipient.getId());
            if (messagingPreferences.getEmailOnActivity() && !messagingPreferences
                    .getEmailActivityDailyDigest()) {
                _log.info("Sending activity notification to member {}.", recipient.getId());

                String unsubscribeFooter = getUnsubscribeIndividualSubscriptionFooter(
                        PlatformAttributeKey.COLAB_URL.get(), NotificationUnregisterUtils
                                .getUnregisterLink(
                                        subscriptionsPerUser.get(recipient.getId())));
                sendEmailMessage(recipient, subject, messageTemplate, unsubscribeFooter,
                        PlatformAttributeKey.COLAB_URL.get(), activity.getId(), true);
            }
        }
    }

    private String clearLinksInSubject(String activityEntryTitle) {
        if (activityEntryTitle != null) {
            return activityEntryTitle.replaceAll("\\<[^>]*>", "");
        }
        return "";
    }

    private void sendEmailMessage(Member recipient, String subject, String body,
            String unregisterFooter, String portalBaseUrl, Long referenceId, boolean defaultFooter) {

        try {
            InternetAddress fromEmail = TemplateReplacementUtil.getAdminFromEmailAddress();
            InternetAddress toEmail =
                    new InternetAddress(recipient.getEmailAddress(), recipient.getFullName());

            if(defaultFooter){body += MESSAGE_FOOTER_TEMPLATE;}

            body = HtmlUtil.makeRelativeLinksAbsolute(body, portalBaseUrl);
            body = body.replaceAll("\n", "\n<br />");
            String message = body.replace(USER_PROFILE_LINK_PLACEHOLDER,
                    getUserLink(recipient, portalBaseUrl));

            message = HtmlUtil.decodeHTMLEntitiesForEmail(message);

            // add link to unsubscribe
            message += "<br /><br />" + unregisterFooter;

            EmailClient
                    .sendEmail(fromEmail.getAddress(), ConfigurationAttributeKey.COLAB_NAME.get(),
                            toEmail.getAddress(),
                            TemplateReplacementUtil.replacePlatformConstants(subject),
                            TemplateReplacementUtil.replacePlatformConstants(message), true,
                            fromEmail.getAddress(), ConfigurationAttributeKey.COLAB_NAME.get(),
                            referenceId);

        } catch (UnsupportedEncodingException e) {
            _log.error("Can't send email notifications to users");
            _log.debug("Can't send email message", e);
        }
    }


    private List<ActivitySubscription> getActivitySubscribers(ActivityEntry activity) {

        List<ActivitySubscription> filteredResults = new ArrayList<>();

        List<ActivitySubscription> ret = ActivitiesClientUtil
                .getActivitySubscriptions(activity.getActivityCategoryEnum(),
                        activity.getCategoryId(),null);

        // Check for constraints which users should receive notifications
        ActivitySubscriptionConstraint subscriptionConstraint =
                new ActivitySubscriptionConstraint(activity.getActivityTypeEnum());
        if (subscriptionConstraint.areSubscribersConstrained()) {
            for (Long userId : subscriptionConstraint.getWhitelist(activity.getCategoryId())) {
                for (ActivitySubscription as : ret) {
                    if (as.getReceiverUserId() == userId.longValue()) {
                        filteredResults.add(as);
                    }
                }
            }
        } else {
            filteredResults.addAll(ret);
        }

        return filteredResults;
    }

    private String instantToFormattedString(Instant instant) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
        return formatter.format(Date.from(instant));
    }


    private String getUnsubscribeIndividualSubscriptionFooter(String portalBaseUrl,
            String unsubscribeUrl) {
        String faqUrl = portalBaseUrl + FAQ_DIGEST_URL_PATH;
        String footer = TemplateReplacementUtil.replaceContestTypeStrings(StringUtils
                .replace(UNSUBSCRIBE_INSTANT_NOTIFICATION_TEXT, FAQ_DIGEST_LINK_PLACEHOLDER,
                        faqUrl), null);
        footer = StringUtils
                .replace(footer, UNSUBSCRIBE_SUBSCRIPTION_LINK_PLACEHOLDER, unsubscribeUrl);
        return footer;
    }

    private String getUserLink(Member user, String portalBaseUrl) {
        return USER_PROFILE_LINK_TEMPLATE
                .replaceAll(USER_ID_PLACEHOLDER, String.valueOf(user.getId()))
                .replaceAll(DOMAIN_PLACEHOLDER, portalBaseUrl);
    }
}
