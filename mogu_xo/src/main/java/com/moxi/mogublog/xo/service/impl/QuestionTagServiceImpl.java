package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.QuestionTag;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.mapper.QuestionTagMapper;
import com.moxi.mogublog.xo.service.QuestionTagService;
import com.moxi.mogublog.xo.vo.QuestionTagVO;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 问答标签表 服务实现类
 *
 * @author 陌溪
 * @date 2021年4月26日22:56:25
 */
@Service
public class QuestionTagServiceImpl extends SuperServiceImpl<QuestionTagMapper, QuestionTag> implements QuestionTagService {

    @Autowired
    QuestionTagService questionTagService;

    @Override
    public IPage<QuestionTag> getPageList(QuestionTagVO questionTagVO) {
        QueryWrapper<QuestionTag> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(questionTagVO.getKeyword()) && !StringUtils.isEmpty(questionTagVO.getKeyword())) {
            queryWrapper.like(SQLConf.NAME, questionTagVO.getKeyword().trim());
        }

        Page<QuestionTag> page = new Page<>();
        page.setCurrent(questionTagVO.getCurrentPage());
        page.setSize(questionTagVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(questionTagVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(questionTagVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(questionTagVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(questionTagVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.SORT);
        }
        IPage<QuestionTag> pageList = questionTagService.page(page, queryWrapper);
        return pageList;

    }

    @Override
    public List<QuestionTag> getList() {
        return null;
    }

    @Override
    public String addQuestionTag(QuestionTagVO questionTagVO) {
        QueryWrapper<QuestionTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.NAME, questionTagVO.getName());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        QuestionTag tempTag = questionTagService.getOne(queryWrapper);
        if (tempTag != null) {
            return ResultUtil.errorWithMessage(MessageConf.ENTITY_EXIST);
        }
        QuestionTag tag = new QuestionTag();
        tag.setName(questionTagVO.getName());
        tag.setSummary(questionTagVO.getSummary());
        tag.setClickCount(0);
        tag.setStatus(EStatus.ENABLE);
        tag.setSort(questionTagVO.getSort());
        tag.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editQuestionTag(QuestionTagVO questionTagVO) {
        QuestionTag tag = questionTagService.getById(questionTagVO.getUid());

        if (tag != null && !tag.getName().equals(questionTagVO.getName())) {
            QueryWrapper<QuestionTag> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(SQLConf.NAME, questionTagVO.getName());
            queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            QuestionTag tempTag = questionTagService.getOne(queryWrapper);
            if (tempTag != null) {
                return ResultUtil.errorWithMessage(MessageConf.ENTITY_EXIST);
            }
        }

        tag.setName(questionTagVO.getName());
        tag.setSummary(questionTagVO.getSummary());
        tag.setStatus(EStatus.ENABLE);
        tag.setSort(questionTagVO.getSort());
        tag.setUpdateTime(new Date());
        tag.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchQuestionTag(List<QuestionTagVO> questionTagVOList) {
        if (questionTagVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        questionTagVOList.forEach(item -> {
            uids.add(item.getUid());
        });

        // 判断要删除的分类，是否有问答
//        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
//        blogQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
//        blogQueryWrapper.in(SQLConf.TAG_UID, uids);
//        Integer blogCount = blogService.count(blogQueryWrapper);
//        if (blogCount > 0) {
//            return ResultUtil.errorWithMessage(MessageConf.BLOG_UNDER_THIS_TAG);
//        }

        Collection<QuestionTag> tagList = questionTagService.listByIds(uids);

        tagList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });

        Boolean save = questionTagService.updateBatchById(tagList);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }
}
