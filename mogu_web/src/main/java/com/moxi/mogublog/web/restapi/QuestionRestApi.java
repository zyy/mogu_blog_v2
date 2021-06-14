package com.moxi.mogublog.web.restapi;

import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.QuestionService;
import com.moxi.mogublog.xo.service.QuestionTagService;
import com.moxi.mogublog.xo.service.QuestionTemplateService;
import com.moxi.mogublog.xo.vo.QuestionTagVO;
import com.moxi.mogublog.xo.vo.QuestionTemplateVO;
import com.moxi.mogublog.xo.vo.QuestionVO;
import com.moxi.mougblog.base.enums.EContributeSource;
import com.moxi.mougblog.base.enums.EPublish;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.global.Constants;
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

import javax.servlet.http.HttpServletRequest;
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
    @Autowired
    private QuestionTemplateService questionTemplateService;

    @ApiOperation(value = "获取问答列表", notes = "获取问答列表", response = String.class)
    @PostMapping("/getList")
    public String getList(HttpServletRequest request, @Validated({GetList.class}) @RequestBody QuestionVO questionVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("获取问答列表");
        // 前端没有传递用户UID时，将查询在线用户的博客列表
        if (StringUtils.isEmpty(questionVO.getUserUid()) && request.getAttribute(SysConf.USER_UID) != null) {
            questionVO.setUserUid(request.getAttribute(SysConf.USER_UID).toString());
        }
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
        // 添加问答来源【用户投稿】
        questionVO.setQuestionSource(EContributeSource.USER_PUBLISH);
        log.info("增加问答");
        return questionService.addQuestion(questionVO);
    }


    @ApiOperation(value = "编辑问答", notes = "编辑问答", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody QuestionVO questionVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        questionVO.setQuestionSource(EContributeSource.USER_PUBLISH);
        log.info("编辑问答");
        return questionService.editQuestion(questionVO);
    }

    @ApiOperation(value = "获取标签列表", notes = "获取标签列表", response = String.class)
    @PostMapping("/getTagList")
    public String getTagList(@Validated({GetList.class}) @RequestBody QuestionTagVO questionTagVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取标签列表");
        return ResultUtil.result(SysConf.SUCCESS, questionTagService.getPageList(questionTagVO));
    }

    @ApiOperation(value = "获取问答模板列表", notes = "获取问答模板列表", response = String.class)
    @PostMapping("/getTemplateList")
    public String getTemplateList(@Validated({GetList.class}) @RequestBody QuestionTemplateVO questionTemplateVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取问答模板列表");
        return ResultUtil.result(SysConf.SUCCESS, questionTemplateService.getPageList(questionTemplateVO));
    }
}

