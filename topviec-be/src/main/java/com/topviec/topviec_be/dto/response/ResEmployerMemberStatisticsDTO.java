package com.topviec.topviec_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResEmployerMemberStatisticsDTO {

    /** Tổng số thành viên trong công ty */
    private long totalMembers;

    /** Tổng số thành viên đang hoạt động (active) */
    private long activeMembers;

    /** Tổng số thành viên chờ xác nhận (pending) */
    private long pendingMembers;

    /** Tổng số thành viên đang bị khóa (deactivated) */
    private long lockedMembers;
}
