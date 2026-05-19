package com.topviec.topviec_be.config;

import com.topviec.topviec_be.entity.ActionItem;
import com.topviec.topviec_be.entity.AdminUser;
import com.topviec.topviec_be.entity.RoleDefault;
import com.topviec.topviec_be.entity.User;
import com.topviec.topviec_be.enums.adminUsers.AdminRoleConstants;
import com.topviec.topviec_be.enums.companyMember.MemberRole;
import com.topviec.topviec_be.enums.users.UserStatus;
import com.topviec.topviec_be.enums.users.UserType;
import com.topviec.topviec_be.repository.AdminUserRepository;
import com.topviec.topviec_be.repository.RoleDefaultRepository;
import com.topviec.topviec_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AdminUserRepository adminUserRepository;
    private final RoleDefaultRepository roleDefaultRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initRoleDefaults();

        createAdminIfNotExists("superadmin@topviec.vn", "Super Admin", AdminRoleConstants.SUPER_ADMIN, "Management");
        createAdminIfNotExists("moderator@topviec.vn", "Content Moderator", AdminRoleConstants.CONTENT_MODERATOR,
                "Content Team");
        createAdminIfNotExists("support@topviec.vn", "Support Admin", AdminRoleConstants.SUPPORT_ADMIN,
                "Customer Support");
        createAdminIfNotExists("finance@topviec.vn", "Finance Admin", AdminRoleConstants.FINANCE_ADMIN, "Finance");
    }

    private void initRoleDefaults() {
        createRoleDefaultIfNotExists(MemberRole.OWNER, createOwnerActions());
        createRoleDefaultIfNotExists(MemberRole.MANAGER, createManagerActions());
        createRoleDefaultIfNotExists(MemberRole.RECRUITER, createRecruiterActions());
        createRoleDefaultIfNotExists(MemberRole.VIEWER, createViewerActions());
    }

    private void createRoleDefaultIfNotExists(MemberRole role, List<ActionItem> actions) {
        if (roleDefaultRepository.existsByRoleName(role)) {
            log.info("Role Default [{}] đã tồn tại, bỏ qua khởi tạo", role);
            return;
        }

        RoleDefault roleDefault = RoleDefault.builder()
                .roleName(role)
                .actions(actions)
                .build();

        roleDefaultRepository.save(roleDefault);
        log.info(">>>>>>>>: Đã tạo Role Default [{}]", role);
    }

    private static ActionItem a(String name, String code, boolean enabled) {
        return ActionItem.builder().name(name).code(code).enabled(enabled).build();
    }

    private List<ActionItem> createOwnerActions() {
        return List.of(
            a("Chỉnh sửa thông tin công ty",   "company:edit",           true),
            a("Xóa công ty",                    "company:delete",         true),
            a("Xác thực công ty",               "company:verify",         true),
            a("Thêm thành viên",                "member:add",             true),
            a("Xóa thành viên",                 "member:delete",          true),
            a("Phân quyền thành viên",          "member:permission",      true),
            a("Xem hoạt động thành viên",       "member:view_activity",   true),
            a("Tạo tin tuyển dụng",             "job:create",             true),
            a("Sửa tin của mình",               "job:edit_own",           true),
            a("Sửa tin của người khác",         "job:edit_other",         true),
            a("Xóa tin của mình",               "job:delete_own",         true),
            a("Xóa tin của người khác",         "job:delete_other",       true),
            a("Bật/tắt tin của mình",           "job:toggle_own",         true),
            a("Bật/tắt tin của người khác",     "job:toggle_other",       true),
            a("Giao tin tuyển dụng",            "job:assign",             true),
            a("Quản lý phân công tin",           "job_assignment:manage",  true),
            a("Nhận phân công tin",              "job_assignment:receive", true),
            a("Xem tất cả tin tuyển dụng",      "job:view_all",           true),
            a("Xem CV được phân công",          "cv:view_assigned",       true),
            a("Xem CV chưa phân công",          "cv:view_unassigned",     true),
            a("Phân loại CV",                   "cv:classify",            true),
            a("Mời phỏng vấn",                  "cv:invite_interview",    true),
            a("Ghi nhận kết quả phỏng vấn",     "cv:record_interview",    true),
            a("Duyệt ứng viên",                 "cv:approve",             true),
            a("Từ chối ứng viên",               "cv:reject",              true),
            a("Tìm kiếm ứng viên",              "talent:search",          true),
            a("Xem hồ sơ ứng viên",             "talent:view_profile",    true),
            a("Mời ứng viên ứng tuyển",         "talent:invite",          true),
            a("Lưu vào talent pool",            "talent:save_pool",       true),
            a("Mua dịch vụ",                    "service:purchase",       true),
            a("Xem lịch sử dịch vụ",            "service:view_history",   true),
            a("Xem hóa đơn",                    "service:invoice",        true),
            a("Xem dashboard báo cáo",          "report:dashboard",       true),
            a("Xuất báo cáo",                   "report:export",          true),
            a("Xem báo cáo toàn công ty",       "report:view_company",    true)
        );
    }

    private List<ActionItem> createManagerActions() {
        return List.of(
            a("Chỉnh sửa thông tin công ty",   "company:edit",           true),
            a("Xóa công ty",                    "company:delete",         false),
            a("Xác thực công ty",               "company:verify",         false),
            a("Thêm thành viên",                "member:add",             true),
            a("Xóa thành viên",                 "member:delete",          true),
            a("Phân quyền thành viên",          "member:permission",      true),
            a("Xem hoạt động thành viên",       "member:view_activity",   true),
            a("Tạo tin tuyển dụng",             "job:create",             true),
            a("Sửa tin của mình",               "job:edit_own",           true),
            a("Sửa tin của người khác",         "job:edit_other",         true),
            a("Xóa tin của mình",               "job:delete_own",         true),
            a("Xóa tin của người khác",         "job:delete_other",       true),
            a("Bật/tắt tin của mình",           "job:toggle_own",         true),
            a("Bật/tắt tin của người khác",     "job:toggle_other",       true),
            a("Giao tin tuyển dụng",            "job:assign",             true),
            a("Quản lý phân công tin",           "job_assignment:manage",  true),
            a("Nhận phân công tin",              "job_assignment:receive", true),
            a("Xem tất cả tin tuyển dụng",      "job:view_all",           true),
            a("Xem CV được phân công",          "cv:view_assigned",       true),
            a("Xem CV chưa phân công",          "cv:view_unassigned",     true),
            a("Phân loại CV",                   "cv:classify",            true),
            a("Mời phỏng vấn",                  "cv:invite_interview",    true),
            a("Ghi nhận kết quả phỏng vấn",     "cv:record_interview",    true),
            a("Duyệt ứng viên",                 "cv:approve",             true),
            a("Từ chối ứng viên",               "cv:reject",              true),
            a("Tìm kiếm ứng viên",              "talent:search",          true),
            a("Xem hồ sơ ứng viên",             "talent:view_profile",    true),
            a("Mời ứng viên ứng tuyển",         "talent:invite",          true),
            a("Lưu vào talent pool",            "talent:save_pool",       true),
            a("Mua dịch vụ",                    "service:purchase",       false),
            a("Xem lịch sử dịch vụ",            "service:view_history",   true),
            a("Xem hóa đơn",                    "service:invoice",        false),
            a("Xem dashboard báo cáo",          "report:dashboard",       true),
            a("Xuất báo cáo",                   "report:export",          true),
            a("Xem báo cáo toàn công ty",       "report:view_company",    true)
        );
    }

    private List<ActionItem> createRecruiterActions() {
        return List.of(
            a("Chỉnh sửa thông tin công ty",   "company:edit",           false),
            a("Xóa công ty",                    "company:delete",         false),
            a("Xác thực công ty",               "company:verify",         false),
            a("Thêm thành viên",                "member:add",             false),
            a("Xóa thành viên",                 "member:delete",          false),
            a("Phân quyền thành viên",          "member:permission",      false),
            a("Xem hoạt động thành viên",       "member:view_activity",   false),
            a("Tạo tin tuyển dụng",             "job:create",             true),
            a("Sửa tin của mình",               "job:edit_own",           true),
            a("Sửa tin của người khác",         "job:edit_other",         false),
            a("Xóa tin của mình",               "job:delete_own",         true),
            a("Xóa tin của người khác",         "job:delete_other",       false),
            a("Bật/tắt tin của mình",           "job:toggle_own",         true),
            a("Bật/tắt tin của người khác",     "job:toggle_other",       false),
            a("Giao tin tuyển dụng",            "job:assign",             false),
            a("Quản lý phân công tin",           "job_assignment:manage",  false),
            a("Nhận phân công tin",              "job_assignment:receive", true),
            a("Xem tất cả tin tuyển dụng",      "job:view_all",           true),
            a("Xem CV được phân công",          "cv:view_assigned",       true),
            a("Xem CV chưa phân công",          "cv:view_unassigned",     false),
            a("Phân loại CV",                   "cv:classify",            true),
            a("Mời phỏng vấn",                  "cv:invite_interview",    true),
            a("Ghi nhận kết quả phỏng vấn",     "cv:record_interview",    true),
            a("Duyệt ứng viên",                 "cv:approve",             true),
            a("Từ chối ứng viên",               "cv:reject",              true),
            a("Tìm kiếm ứng viên",              "talent:search",          false),
            a("Xem hồ sơ ứng viên",             "talent:view_profile",    false),
            a("Mời ứng viên ứng tuyển",         "talent:invite",          false),
            a("Lưu vào talent pool",            "talent:save_pool",       true),
            a("Mua dịch vụ",                    "service:purchase",       false),
            a("Xem lịch sử dịch vụ",            "service:view_history",   false),
            a("Xem hóa đơn",                    "service:invoice",        false),
            a("Xem dashboard báo cáo",          "report:dashboard",       false),
            a("Xuất báo cáo",                   "report:export",          false),
            a("Xem báo cáo toàn công ty",       "report:view_company",    false)
        );
    }

    private List<ActionItem> createViewerActions() {
        return List.of(
            a("Chỉnh sửa thông tin công ty",   "company:edit",           false),
            a("Xóa công ty",                    "company:delete",         false),
            a("Xác thực công ty",               "company:verify",         false),
            a("Thêm thành viên",                "member:add",             false),
            a("Xóa thành viên",                 "member:delete",          false),
            a("Phân quyền thành viên",          "member:permission",      false),
            a("Xem hoạt động thành viên",       "member:view_activity",   false),
            a("Tạo tin tuyển dụng",             "job:create",             false),
            a("Sửa tin của mình",               "job:edit_own",           false),
            a("Sửa tin của người khác",         "job:edit_other",         false),
            a("Xóa tin của mình",               "job:delete_own",         false),
            a("Xóa tin của người khác",         "job:delete_other",       false),
            a("Bật/tắt tin của mình",           "job:toggle_own",         false),
            a("Bật/tắt tin của người khác",     "job:toggle_other",       false),
            a("Giao tin tuyển dụng",            "job:assign",             false),
            a("Quản lý phân công tin",           "job_assignment:manage",  false),
            a("Nhận phân công tin",              "job_assignment:receive", false),
            a("Xem tất cả tin tuyển dụng",      "job:view_all",           true),
            a("Xem CV được phân công",          "cv:view_assigned",       true),
            a("Xem CV chưa phân công",          "cv:view_unassigned",     false),
            a("Phân loại CV",                   "cv:classify",            false),
            a("Mời phỏng vấn",                  "cv:invite_interview",    false),
            a("Ghi nhận kết quả phỏng vấn",     "cv:record_interview",    false),
            a("Duyệt ứng viên",                 "cv:approve",             false),
            a("Từ chối ứng viên",               "cv:reject",              false),
            a("Tìm kiếm ứng viên",              "talent:search",          false),
            a("Xem hồ sơ ứng viên",             "talent:view_profile",    false),
            a("Mời ứng viên ứng tuyển",         "talent:invite",          false),
            a("Lưu vào talent pool",            "talent:save_pool",       false),
            a("Mua dịch vụ",                    "service:purchase",       false),
            a("Xem lịch sử dịch vụ",            "service:view_history",   false),
            a("Xem hóa đơn",                    "service:invoice",        false),
            a("Xem dashboard báo cáo",          "report:dashboard",       true),
            a("Xuất báo cáo",                   "report:export",          false),
            a("Xem báo cáo toàn công ty",       "report:view_company",    false)
        );
    }

    private void createAdminIfNotExists(String email, String fullName, String adminRole, String department) {
        if (userRepository.existsByEmail(email)) {
            log.info("Admin [{}] đã tồn tại, bỏ qua khởi tạo", email);
            return;
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode("123456"))
                .userType(UserType.ADMIN)
                .status(UserStatus.ACTIVE)
                .twoFactorEnabled(false)
                .build();

        User savedUser = userRepository.save(user);

        AdminUser adminUser = AdminUser.builder()
                .user(savedUser)
                .adminRole(adminRole)
                .fullName(fullName)
                .department(department)
                .createdBy(savedUser.getId())
                .build();

        adminUserRepository.save(adminUser);
        log.info(">>>>>>>>: Đã tạo tài khoản [{}] - {} ({})", adminRole, fullName, email);
    }
}
