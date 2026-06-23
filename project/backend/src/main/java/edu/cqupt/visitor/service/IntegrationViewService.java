package edu.cqupt.visitor.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.entity.AccessRecord;
import edu.cqupt.visitor.entity.Blacklist;
import edu.cqupt.visitor.entity.CampusGate;
import edu.cqupt.visitor.entity.Department;
import edu.cqupt.visitor.entity.PassCode;
import edu.cqupt.visitor.entity.SysUser;
import edu.cqupt.visitor.entity.VisitApply;
import edu.cqupt.visitor.entity.Visitor;
import edu.cqupt.visitor.entity.VisitorVehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IntegrationViewService {

    private final VisitorService visitorService;
    private final VisitApplyService visitApplyService;
    private final DepartmentService departmentService;
    private final SysUserService sysUserService;
    private final VisitorVehicleService visitorVehicleService;
    private final CampusGateService campusGateService;
    private final PassCodeService passCodeService;

    public Page<VisitApply> enrichVisitApplyPage(Page<VisitApply> page) {
        if (page == null || page.getRecords() == null) {
            return page;
        }
        page.getRecords().forEach(this::enrichVisitApply);
        return page;
    }

    public VisitApply enrichVisitApply(VisitApply apply) {
        if (apply == null) {
            return null;
        }
        Visitor visitor = apply.getVisitorId() == null ? null : visitorService.getById(apply.getVisitorId());
        if (visitor != null) {
            apply.setVisitorName(visitor.getVisitorName());
            apply.setPhone(visitor.getPhone());
            apply.setIdNumber(visitor.getIdNumber());
        }
        SysUser host = apply.getHostUserId() == null ? null : sysUserService.getById(apply.getHostUserId());
        if (host != null) {
            apply.setHostName(host.getRealName());
        }
        Department department = apply.getDepartmentId() == null ? null : departmentService.getById(apply.getDepartmentId());
        if (department != null) {
            apply.setDepartmentName(department.getDeptName());
        }
        VisitorVehicle vehicle = apply.getVehicleId() == null ? null : visitorVehicleService.getById(apply.getVehicleId());
        if (vehicle != null) {
            apply.setVehiclePlateNo(vehicle.getPlateNo());
        }
        return apply;
    }

    public Page<AccessRecord> enrichAccessRecordPage(Page<AccessRecord> page) {
        if (page == null || page.getRecords() == null) {
            return page;
        }
        page.getRecords().forEach(this::enrichAccessRecord);
        return page;
    }

    public AccessRecord enrichAccessRecord(AccessRecord record) {
        if (record == null) {
            return null;
        }
        VisitApply apply = record.getApplyId() == null ? null : visitApplyService.getById(record.getApplyId());
        if (apply != null) {
            record.setApplyNo(apply.getApplyNo());
            if (record.getVisitorId() == null) {
                record.setVisitorId(apply.getVisitorId());
            }
        }
        Visitor visitor = record.getVisitorId() == null ? null : visitorService.getById(record.getVisitorId());
        if (visitor != null) {
            record.setVisitorName(visitor.getVisitorName());
            record.setPhone(visitor.getPhone());
        }
        CampusGate entryGate = record.getEntryGateId() == null ? null : campusGateService.getById(record.getEntryGateId());
        if (entryGate != null) {
            record.setEntryGateName(entryGate.getGateName());
        }
        CampusGate exitGate = record.getExitGateId() == null ? null : campusGateService.getById(record.getExitGateId());
        if (exitGate != null) {
            record.setExitGateName(exitGate.getGateName());
        }
        SysUser entryGuard = record.getEntryGuardId() == null ? null : sysUserService.getById(record.getEntryGuardId());
        if (entryGuard != null) {
            record.setEntryGuardName(entryGuard.getRealName());
        }
        SysUser exitGuard = record.getExitGuardId() == null ? null : sysUserService.getById(record.getExitGuardId());
        if (exitGuard != null) {
            record.setExitGuardName(exitGuard.getRealName());
        }
        PassCode passCode = record.getPassCodeId() == null ? null : passCodeService.getById(record.getPassCodeId());
        if (passCode != null) {
            record.setPassCode(passCode.getPassCode());
        }
        return record;
    }

    public Page<Blacklist> enrichBlacklistPage(Page<Blacklist> page) {
        if (page == null || page.getRecords() == null) {
            return page;
        }
        page.getRecords().forEach(this::enrichBlacklist);
        return page;
    }

    public Blacklist enrichBlacklist(Blacklist item) {
        if (item == null) {
            return null;
        }
        Visitor visitor = item.getVisitorId() == null ? null : visitorService.getById(item.getVisitorId());
        if (visitor != null) {
            item.setVisitorName(visitor.getVisitorName());
        }
        SysUser operator = item.getOperatorUserId() == null ? null : sysUserService.getById(item.getOperatorUserId());
        if (operator != null) {
            item.setOperatorName(operator.getRealName());
        }
        return item;
    }

    public Page<SysUser> enrichSysUserPage(Page<SysUser> page) {
        if (page == null || page.getRecords() == null) {
            return page;
        }
        page.getRecords().forEach(this::enrichSysUser);
        return page;
    }

    public SysUser enrichSysUser(SysUser user) {
        if (user == null) {
            return null;
        }
        Department department = user.getDepartmentId() == null ? null : departmentService.getById(user.getDepartmentId());
        if (department != null) {
            user.setDepartmentName(department.getDeptName());
        }
        user.setPasswordHash(null);
        return user;
    }
}