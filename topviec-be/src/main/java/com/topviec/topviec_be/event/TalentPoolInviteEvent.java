package com.topviec.topviec_be.event;

public record TalentPoolInviteEvent(
        String candidateEmail,
        String candidateName,
        String companyName,
        String jobTitle,
        String jobLink) {
}
