package com.kaua.ecommerce.lib.infrastructure.constants;

public final class CommonMessageHeaders {

    private CommonMessageHeaders() {}

    public static final String COMMAND_ID = "command_id";
    public static final String COMMAND_TYPE = "command_type";
    public static final String COMMAND_OCCURRED_ON = "command_occurred_on";

    public static final String EVENT_ID = "event_id";
    public static final String EVENT_TYPE = "event_type";
    public static final String EVENT_OCCURRED_ON = "event_occurred_on";

    public static final String WHO = "who";
    public static final String TRACE_ID = "trace_id";
}
