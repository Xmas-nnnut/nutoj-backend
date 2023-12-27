package com.xqj.nutoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xqj.nutoj.common.ErrorCode;
import com.xqj.nutoj.constant.CommonConstant;
import com.xqj.nutoj.exception.BusinessException;
import com.xqj.nutoj.exception.ThrowUtils;
import com.xqj.nutoj.mapper.RecordMapper;
import com.xqj.nutoj.model.dto.record.RecordQueryRequest;
import com.xqj.nutoj.model.entity.Record;
import com.xqj.nutoj.model.entity.User;
import com.xqj.nutoj.model.vo.LoginUserVO;
import com.xqj.nutoj.model.vo.RecordVO;
import com.xqj.nutoj.model.vo.UserVO;
import com.xqj.nutoj.service.RecordService;
import com.xqj.nutoj.service.UserService;
import com.xqj.nutoj.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author xuqingjian
* @description 针对表【record(记录表)】的数据库操作Service实现
* @createDate 2023-12-25 15:37:34
*/
@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record>
    implements RecordService {

    @Resource
    private UserService userService;

    @Override
    public void validRecord(Record record, boolean add) {
        if (record == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        String content = record.getContent();
//        // 创建时，参数不能为空
//        if (add) {
//            ThrowUtils.throwIf(StringUtils.isAnyBlank(content), ErrorCode.PARAMS_ERROR);
//        }
//        // 有参数则校验
//        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
//        }
    }

    @Override
    public Long initUserRecord(Long userId){
        Record record = new Record();
        record.setUserId(userId);
        record.setSubmitNum(0);
        record.setAcceptedNum(0);
        boolean save = this.save(record);
        if (!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        return userId;
    }

    /**
     * 获取查询包装类
     *
     * @param recordQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Record> getQueryWrapper(RecordQueryRequest recordQueryRequest) {
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        if (recordQueryRequest == null) {
            return queryWrapper;
        }
        String sortField = recordQueryRequest.getSortField();
        String sortOrder = recordQueryRequest.getSortOrder();
        Long id = recordQueryRequest.getId();
        Long userId = recordQueryRequest.getUserId();
        // 拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public RecordVO getRecordVO(Record record, HttpServletRequest request) {
        RecordVO recordVO = new RecordVO();
        BeanUtils.copyProperties(record, recordVO);
        long recordId = record.getId();
        // 关联查询用户信息
        Long userId = record.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        recordVO.setUser(userVO);
        return recordVO;
    }

    @Override
    public Page<RecordVO> getRecordVOPage(Page<Record> recordPage, HttpServletRequest request) {
        List<Record> recordList = recordPage.getRecords();
        Page<RecordVO> recordVOPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        if (CollectionUtils.isEmpty(recordList)) {
            return recordVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = recordList.stream().map(Record::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户
        User loginUser = userService.getLoginUserPermitNull(request);
        // 填充信息
        List<RecordVO> recordVOList = recordList.stream().map(record -> {
            RecordVO recordVO = new RecordVO();
            BeanUtils.copyProperties(record, recordVO);
            Long userId = record.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            recordVO.setUser(userService.getUserVO(user));
            return recordVO;
        }).collect(Collectors.toList());
        recordVOPage.setRecords(recordVOList);
        return recordVOPage;
    }

}




