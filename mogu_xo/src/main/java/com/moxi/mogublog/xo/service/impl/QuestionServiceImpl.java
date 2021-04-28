package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.Question;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.QuestionMapper;
import com.moxi.mogublog.xo.service.QuestionService;
import com.moxi.mogublog.xo.vo.QuestionVO;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 问答表 服务实现类
 *
 * @author 陌溪
 * @date 2021年4月26日22:56:25
 */
@Service
public class QuestionServiceImpl extends SuperServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Autowired
    QuestionService questionService;

    @Override
    public IPage<Question> getPageList(QuestionVO questionVO) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        // 构建搜索条件
        if (StringUtils.isNotEmpty(questionVO.getKeyword()) && !StringUtils.isEmpty(questionVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.TITLE, questionVO.getKeyword().trim());
        }
        if (!StringUtils.isEmpty(questionVO.getQuestionTagUid())) {
            queryWrapper.like(SQLConf.TAG_UID, questionVO.getQuestionTagUid());
        }
        if (!StringUtils.isEmpty(questionVO.getIsPublish())) {
            queryWrapper.eq(SQLConf.IS_PUBLISH, questionVO.getIsPublish());
        }
        if (!StringUtils.isEmpty(questionVO.getUserUid())) {
            queryWrapper.eq(SQLConf.USER_UID, questionVO.getUserUid());
        }

        //分页
        Page<Question> page = new Page<>();
        page.setCurrent(questionVO.getCurrentPage());
        page.setSize(questionVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);

        if (StringUtils.isNotEmpty(questionVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(questionVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(questionVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(questionVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            // 使用，默认按sort值大小倒序
            queryWrapper.orderByDesc(SQLConf.SORT);
        }

        IPage<Question> pageList = questionService.page(page, queryWrapper);
        return pageList;
    }

    @Override
    public String addQuestion(QuestionVO questionVO) {
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question, SysConf.STATUS);
        question.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editQuestion(QuestionVO questionVO) {
        Question question = questionService.getById(questionVO.getUid());
        BeanUtils.copyProperties(questionVO, question, SysConf.STATUS, SysConf.UID);
        question.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchQuestion(List<QuestionVO> questionVOList) {
        if (questionVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uidList = new ArrayList<>();
        questionVOList.forEach(item -> {
            uidList.add(item.getUid());
        });
        Collection<Question> questionList = questionService.listByIds(uidList);
        questionList.forEach(item -> {
            item.setStatus(EStatus.DISABLED);
        });
        questionService.updateBatchById(questionList);
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }
}
