package com.moxi.mogublog.web.restapi;

import com.moxi.mogublog.commons.entity.Admin;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.web.annotion.log.BussinessLog;
import com.moxi.mogublog.web.global.MessageConf;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.vo.BlogVO;
import com.moxi.mogublog.xo.vo.QuestionVO;
import com.moxi.mougblog.base.enums.EBehavior;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.validator.group.GetList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 关于我 RestApi
 *
 * @author 陌溪
 * @date 2018年11月12日14:51:54
 */
@RestController
@RequestMapping("/about")
@Api(value = "关于我相关接口", tags = {"关于我相关接口"})
@Slf4j
public class AboutMeRestApi {

    @Autowired
    private AdminService adminService;
    @Autowired
    private WebConfigService webConfigService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private UserService userService;

    /**
     * 获取关于我的信息
     */
    @BussinessLog(value = "关于我", behavior = EBehavior.VISIT_PAGE)
    @ApiOperation(value = "关于我", notes = "关于我")
    @GetMapping("/getMe")
    public String getMe() {

        log.info("获取关于我的信息");
        return ResultUtil.result(SysConf.SUCCESS, adminService.getAdminByUser(SysConf.ADMIN));
    }

    @ApiOperation(value = "获取联系方式", notes = "获取联系方式")
    @GetMapping("/getContact")
    public String getContact() {
        log.info("获取联系方式");
        return ResultUtil.result(SysConf.SUCCESS, webConfigService.getWebConfigByShowList());
    }

    @ApiOperation(value = "通过ID获取用户【包含用户和管理员】", notes = "通过ID获取用户")
    @GetMapping("/getUserByUid")
    public String getUserByUid(@ApiParam(name = "adminUid", value = "管理员uid", required = false) @RequestParam(name = "adminUid", required = false, defaultValue = "") String adminUid,
                               @ApiParam(name = "userUid", value = "用户uid", required = false) @RequestParam(name = "userUid", required = false, defaultValue = "") String userUid) {
        if(StringUtils.isNotEmpty(adminUid)) {
            Admin admin = adminService.getAdminByUid(adminUid);
            Admin tempAdmin = new Admin();
            // 数据脱敏
            tempAdmin.setPhotoUrl(admin.getPhotoUrl());
            tempAdmin.setSummary(admin.getSummary());
            tempAdmin.setNickName(admin.getNickName());
            tempAdmin.setGender(admin.getGender());
            tempAdmin.setOccupation(admin.getOccupation());
            return ResultUtil.successWithData(tempAdmin);
        } else if(StringUtils.isNotEmpty(userUid)) {
            List<String> userUidList = new ArrayList<>();
            userUidList.add(userUid);
            List<User> userList = userService.getUserListAndAvatarByIds(userUidList);
            if (userList.size() > 0) {
                User user = userList.get(0);
                // 数据脱敏
                User tempUser = new User();
                tempUser.setPhotoUrl(user.getPhotoUrl());
                tempUser.setSummary(user.getSummary());
                tempUser.setNickName(user.getNickName());
                tempUser.setGender(user.getGender());
                tempUser.setOccupation(user.getOccupation());
                return ResultUtil.successWithData(tempUser);
            } else {
                return ResultUtil.errorWithMessage("该用户不存在");
            }
        }
        log.info("通过用户获取博客列表");
        return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
    }

    @ApiOperation(value = "通过用户获取博客列表", notes = "通过用户获取博客列表")
    @PostMapping("/getBlogListByUser")
    public String getBlogListByUser(@Validated({GetList.class}) @RequestBody BlogVO blogVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("通过用户获取博客列表");
        return ResultUtil.result(SysConf.SUCCESS, blogService.getPageList(blogVO));
    }

    @ApiOperation(value = "通过用户获取问答列表", notes = "通过用户获取问答列表")
    @PostMapping("/getQuestionListByUser")
    public String getQuestionListByUser(@Validated({GetList.class}) @RequestBody QuestionVO questionVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("通过用户获取问答列表");
        return ResultUtil.result(SysConf.SUCCESS, questionService.getPageList(questionVO));
    }

}

