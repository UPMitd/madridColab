package org.xcolab.client.flagging.exceptions;

import org.xcolab.util.enums.flagging.TargetType;

public class ReportTargetNotFoundException extends Exception {

    public ReportTargetNotFoundException(long reportTargetId) {
        super("ReportTarget with id " + reportTargetId + " not found");
    }

    public ReportTargetNotFoundException(TargetType type, String reason) {
        super("ReportTarget of type " + type.name() + " and reason " + reason + " not found");
    }
}
