package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.entity.Notice;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "通知消息管理")
@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "分页查询通知消息")
    @GetMapping
    public ApiResponse<Page<Notice>> list(@RequestParam(defaultValue = "1") long current,
                                          @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(noticeService.page(new Page<>(current, size)));
    }

    @Operation(summary = "查询")
    @GetMapping("/{id}")
    public ApiResponse<Notice> detail(@PathVariable Long id) {
        Notice entity = noticeService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "");
        }
        return ApiResponse.ok(entity);
    }

    @Operation(summary = "新增通知消息")
    @PostMapping
    public ApiResponse<Boolean> create(@Valid @RequestBody Notice entity) {
        return ApiResponse.ok(noticeService.save(entity));
    }

    @Operation(summary = "修改通知消息")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody Notice entity) {
        entity.setId(id);
        return ApiResponse.ok(noticeService.updateById(entity));
    }

    @Operation(summary = "删除通知消息")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(noticeService.removeById(id));
    }
}
