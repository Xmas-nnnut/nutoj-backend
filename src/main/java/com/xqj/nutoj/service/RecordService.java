package com.xqj.nutoj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xqj.nutoj.model.dto.record.RecordQueryRequest;
import com.xqj.nutoj.model.entity.Record;
import com.xqj.nutoj.model.vo.LoginUserVO;
import com.xqj.nutoj.model.vo.RecordVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author xuqingjian
* @description 针对表【record(记录表)】的数据库操作Service
* @createDate 2023-12-25 15:37:34
*/
public interface RecordService extends IService<Record> {
    
    /**
     * 校验
     *
     * @param record
     * @param add
     */
    void validRecord(Record record, boolean add);

    /**
     * 用户记录初始化
     * @param userId
     * @return
     */
    Long initUserRecord(Long userId);


    /**
     * 获取查询条件
     *
     * @param recordQueryRequest
     * @return
     */
    QueryWrapper<Record> getQueryWrapper(RecordQueryRequest recordQueryRequest);

    /**
     * 获取题目评论封装
     *
     * @param record
     * @param request
     * @return
     */
    RecordVO getRecordVO(Record record, HttpServletRequest request);

    /**
     * 分页获取题目评论封装
     *
     * @param recordPage
     * @param request
     * @return
     */
    Page<RecordVO> getRecordVOPage(Page<Record> recordPage, HttpServletRequest request);

}
