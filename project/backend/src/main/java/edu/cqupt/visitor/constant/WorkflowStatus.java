package edu.cqupt.visitor.constant;

import java.util.Set;

public final class WorkflowStatus {

    private WorkflowStatus() {
    }

    public static final String APPLY_PENDING_HOST = "PENDING_HOST";
    public static final String APPLY_HOST_CONFIRMED = "HOST_CONFIRMED";
    public static final String APPLY_HOST_REJECTED = "HOST_REJECTED";
    public static final String APPLY_PENDING_DEPT = "PENDING_DEPT";
    public static final String APPLY_APPROVED = "APPROVED";
    public static final String APPLY_REJECTED = "REJECTED";
    public static final String APPLY_CANCELED = "CANCELED";
    public static final String APPLY_REJECTED_BLACKLIST = "REJECTED_BLACKLIST";

    public static final String ACCESS_NOT_ENTERED = "NOT_ENTERED";
    public static final String ACCESS_ENTERED = "ENTERED";
    public static final String ACCESS_EXITED = "EXITED";
    public static final String ACCESS_OVERTIME = "OVERTIME";
    public static final String ACCESS_ABNORMAL = "ABNORMAL";

    public static final String PASS_VALID = "VALID";
    public static final String PASS_USED = "USED";
    public static final String PASS_EXPIRED = "EXPIRED";
    public static final String PASS_DISABLED = "DISABLED";

    public static final String APPROVAL_STEP_HOST = "HOST_CONFIRM";
    public static final String APPROVAL_STEP_DEPARTMENT = "DEPT_APPROVAL";
    public static final String APPROVAL_RESULT_APPROVED = "APPROVED";
    public static final String APPROVAL_RESULT_REJECTED = "REJECTED";

    public static final String BLACKLIST_ACTIVE = "ACTIVE";

    public static final Set<String> EDITABLE_APPLY_STATUSES = Set.of(
            APPLY_PENDING_HOST, APPLY_HOST_CONFIRMED, APPLY_PENDING_DEPT
    );

    public static final Set<String> DEPARTMENT_PENDING_STATUSES = Set.of(
            APPLY_HOST_CONFIRMED, APPLY_PENDING_DEPT
    );

    public static final Set<String> CANCELABLE_APPLY_STATUSES = Set.of(
            APPLY_PENDING_HOST, APPLY_HOST_CONFIRMED, APPLY_PENDING_DEPT
    );
}