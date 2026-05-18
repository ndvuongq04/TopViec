package com.topviec.topviec_be.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateUtils {

    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.of("Asia/Ho_Chi_Minh"));

    private DateUtils() {
    }

    public static String format(Instant instant) {
        return instant == null ? null : ISO_FORMATTER.format(instant);
    }
}
