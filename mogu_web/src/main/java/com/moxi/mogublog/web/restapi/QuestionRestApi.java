package com.moxi.mogublog.web.restapi;

import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.QuestionService;
import com.moxi.mogublog.xo.service.QuestionTagService;
import com.moxi.mogublog.xo.vo.QuestionTagVO;
import com.moxi.mogublog.xo.vo.QuestionVO;
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
 * 问答相关 RestApi
 *
 * @author 陌溪
 * @date 2021年5月5日18:13:52
 */
@Api(value = "问答相关接口", tags = {"问答相关接口"})
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionRestApi {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionTagService questionTagService;

    @ApiOperation(value = "获取问答列表", notes = "获取问答列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody QuestionVO questionVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("获取问答列表");
        return ResultUtil.result(SysConf.SUCCESS, questionService.getPageList(questionVO));
    }

    @ApiOperation(value = "获取问答详情", notes = "获取问答详情")
    @PostMapping("/getQuestion")
    public String getQuestion(@RequestBody QuestionVO questionVO) {
        return questionService.getQuestion(questionVO);
    }

    @ApiOperation(value = "增加问答", notes = "增加问答", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody QuestionVO questionVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        log.info("增加问答");
        return questionService.addQuestion(questionVO);
    }


    @ApiOperation(value = "编辑问答", notes = "编辑问答", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody QuestionVO questionVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        log.info("编辑问答");
        return questionService.editQuestion(questionVO);
    }

    @ApiOperation(value = "批量删除问答", notes = "批量删除问答", response = String.class)
    @PostMapping("/deleteBatch")
    public String delete(@Validated({Delete.class}) @RequestBody List<QuestionVO> questionVOList, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除问答");
        return questionService.deleteBatchQuestion(questionVOList);
    }

    @ApiOperation(value = "获取标签列表", notes = "获取标签列表", response = String.class)
    @PostMapping("/getTagList")
    public String getTagList(@Validated({GetList.class}) @RequestBody QuestionTagVO questionTagVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取标签列表");
        return ResultUtil.result(SysConf.SUCCESS, questionTagService.getPageList(questionTagVO));
    }
}

