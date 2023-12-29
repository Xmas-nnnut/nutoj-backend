package com.xqj.nutoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xqj.nutoj.annotation.AuthCheck;
import com.xqj.nutoj.common.BaseResponse;
import com.xqj.nutoj.common.DeleteRequest;
import com.xqj.nutoj.common.ErrorCode;
import com.xqj.nutoj.common.ResultUtils;
import com.xqj.nutoj.constant.UserConstant;
import com.xqj.nutoj.exception.BusinessException;
import com.xqj.nutoj.exception.ThrowUtils;
import com.xqj.nutoj.model.dto.record.RecordAddRequest;
import com.xqj.nutoj.model.dto.record.RecordEditRequest;
import com.xqj.nutoj.model.dto.record.RecordQueryRequest;
import com.xqj.nutoj.model.dto.record.RecordUpdateRequest;
import com.xqj.nutoj.model.entity.Record;
import com.xqj.nutoj.model.entity.User;
import com.xqj.nutoj.model.vo.RecordVO;
import com.xqj.nutoj.service.RecordService;
import com.xqj.nutoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 记录接口
 *
 */
@RestController
@RequestMapping("/record")
@Slf4j
public class RecordController {

    @Resource
    private RecordService recordService;

    @Resource
    private UserService userService;

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<RecordVO> getRecordVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Record record = recordService.getById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(recordService.getRecordVO(record, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param recordQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<RecordVO>> listRecordVOByPage(@RequestBody RecordQueryRequest recordQueryRequest,
            HttpServletRequest request) {
        long current = recordQueryRequest.getCurrent();
        long size = recordQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Record> recordPage = recordService.page(new Page<>(current, size),
                recordService.getQueryWrapper(recordQueryRequest));
        return ResultUtils.success(recordService.getRecordVOPage(recordPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param recordQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<RecordVO>> listMyRecordVOByPage(@RequestBody RecordQueryRequest recordQueryRequest,
            HttpServletRequest request) {
        if (recordQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        recordQueryRequest.setUserId(loginUser.getId());
        long current = recordQueryRequest.getCurrent();
        long size = recordQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Record> recordPage = recordService.page(new Page<>(current, size),
                recordService.getQueryWrapper(recordQueryRequest));
        return ResultUtils.success(recordService.getRecordVOPage(recordPage, request));
    }

}
