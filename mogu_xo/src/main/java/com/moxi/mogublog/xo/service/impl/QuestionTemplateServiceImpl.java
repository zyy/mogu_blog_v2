package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.QuestionTemplate;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.QuestionTemplateMapper;
import com.moxi.mogublog.xo.service.QuestionTemplateService;
import com.moxi.mogublog.xo.vo.QuestionTemplateVO;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 问答模板表 服务实现类
 *
 * @author 陌溪
 * @date 2021年6月9日08:08:05
 */
@Service
public class QuestionTemplateServiceImpl extends SuperServiceImpl<QuestionTemplateMapper, QuestionTemplate> implements QuestionTemplateService {

    @Autowired
    QuestionTemplateService questionTemplateService;

    @Override
    public IPage<QuestionTemplate> getPageList(QuestionTemplateVO questionTemplateVO) {
        QueryWrapper<QuestionTemplate> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(questionTemplateVO.getKeyword()) && !StringUtils.isEmpty(questionTemplateVO.getKeyword())) {
            queryWrapper.like(SQLConf.NAME, questionTemplateVO.getKeyword().trim());
        }

        Page<QuestionTemplate> page = new Page<>();
        page.setCurrent(questionTemplateVO.getCurrentPage());
        page.setSize(questionTemplateVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(questionTemplateVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(questionTemplateVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(questionTemplateVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(questionTemplateVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.SORT);
        }
        IPage<QuestionTemplate> pageList = questionTemplateService.page(page, queryWrapper);
        return pageList;
    }

    @Override
    public String addQuestionTemplate(QuestionTemplateVO questionTemplateVO) {
        QueryWrapper<QuestionTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.NAME, questionTemplateVO.getName());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        QuestionTemplate tempQuestionTemplate = questionTemplateService.getOne(queryWrapper);
        if (tempQuestionTemplate != null) {
            return ResultUtil.errorWithMessage(MessageConf.ENTITY_EXIST);
        }
        QuestionTemplate questionTemplate = new QuestionTemplate();
        BeanUtils.copyProperties(questionTemplateVO, questionTemplate, SysConf.STATUS);
        questionTemplate.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editQuestionTemplate(QuestionTemplateVO questionTemplateVO) {
        QuestionTemplate questionTemplate = questionTemplateService.getById(questionTemplateVO.getUid());
        if (questionTemplate != null && !questionTemplate.getName().equals(questionTemplateVO.getName())) {
            QueryWrapper<QuestionTemplate> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(SQLConf.NAME, questionTemplateVO.getName());
            queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            QuestionTemplate tempQuestionTemplate = questionTemplateService.getOne(queryWrapper);
            if (tempQuestionTemplate != null) {
                return ResultUtil.errorWithMessage(MessageConf.ENTITY_EXIST);
            }
        }
        BeanUtils.copyProperties(questionTemplateVO, questionTemplate, SysConf.STATUS, SysConf.UID);
        questionTemplate.setUpdateTime(new Date());
        questionTemplate.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchQuestionTemplate(List<QuestionTemplateVO> questionTemplateVOList) {
        if (questionTemplateVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        questionTemplateVOList.forEach(item -> {
            uids.add(item.getUid());
        });
        Collection<QuestionTemplate> QuestionTemplateList = questionTemplateService.listByIds(uids);
        QuestionTemplateList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });

        Boolean save = questionTemplateService.updateBatchById(QuestionTemplateList);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }
}
