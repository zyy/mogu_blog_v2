package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.admin.global.SysConf;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.QuestionTagService;
import com.moxi.mogublog.xo.service.QuestionTemplateService;
import com.moxi.mogublog.xo.vo.QuestionTemplateVO;
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
 * 问答模板表 RestApi
 *
 * @author 陌溪
 * @date 2021年6月9日08:10:18
 */
@Api(value = "问答模板相关接口", tags = {"问答模板相关接口"})
@RestController
@RequestMapping("/questionTemplate")
@Slf4j
public class QuestionTemplateRestApi {

    @Autowired
    private QuestionTemplateService questionTemplateService;

    @AuthorityVerify
    @ApiOperation(value = "获取模板列表", notes = "获取模板列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody QuestionTemplateVO questionTemplateVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取模板列表");
        return ResultUtil.result(SysConf.SUCCESS, questionTemplateService.getPageList(questionTemplateVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加模板")
    @ApiOperation(value = "增加模板", notes = "增加模板", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody QuestionTemplateVO questionTemplateVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加模板");
        return questionTemplateService.addQuestionTemplate(questionTemplateVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑模板")
    @ApiOperation(value = "编辑模板", notes = "编辑模板", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody QuestionTemplateVO questionTemplateVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑模板");
        return questionTemplateService.editQuestionTemplate(questionTemplateVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除模板")
    @ApiOperation(value = "批量删除模板", notes = "批量删除模板", response = String.class)
    @PostMapping("/deleteBatch")
    public String delete(@Validated({Delete.class}) @RequestBody List<QuestionTemplateVO> questionTemplateVOList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除模板");
        return questionTemplateService.deleteBatchQuestionTemplate(questionTemplateVOList);
    }
}

