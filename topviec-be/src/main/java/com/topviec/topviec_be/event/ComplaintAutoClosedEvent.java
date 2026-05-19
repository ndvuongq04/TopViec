package com.topviec.topviec_be.event;

public record ComplaintAutoClosedEvent(
        String employerEmail,
        String jobTitle,
        String complaintType,
        String reportCode) {
}
