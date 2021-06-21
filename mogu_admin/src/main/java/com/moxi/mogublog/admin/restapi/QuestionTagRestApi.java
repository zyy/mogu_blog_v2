package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.admin.global.SysConf;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.QuestionTagService;
import com.moxi.mogublog.xo.vo.QuestionTagVO;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.validator.group.Delete;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 问答标签表 RestApi
 *
 * @author 陌溪
 * @date 2021年4月26日22:59:09
 */
@Api(value = "问答标签相关接口", tags = {"问答标签相关接口"})
@RestController
@RequestMapping("/questionTag")
@Slf4j
public class QuestionTagRestApi {

    @Autowired
    private QuestionTagService questionTagService;

    @AuthorityVerify
    @ApiOperation(value = "获取标签列表", notes = "获取标签列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody QuestionTagVO questionTagVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取标签列表");
        return ResultUtil.result(SysConf.SUCCESS, questionTagService.getPageList(questionTagVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加标签")
    @ApiOperation(value = "增加标签", notes = "增加标签", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody QuestionTagVO questionTagVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加标签");
        return questionTagService.addQuestionTag(questionTagVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑标签")
    @ApiOperation(value = "编辑标签", notes = "编辑标签", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody QuestionTagVO questionTagVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑标签");
        return questionTagService.editQuestionTag(questionTagVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除标签")
    @ApiOperation(value = "批量删除标签", notes = "批量删除标签", response = String.class)
    @PostMapping("/deleteBatch")
    public String delete(@Validated({Delete.class}) @RequestBody List<QuestionTagVO> questionTagVOList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除标签");
        return questionTagService.deleteBatchQuestionTag(questionTagVOList);
    }
}

