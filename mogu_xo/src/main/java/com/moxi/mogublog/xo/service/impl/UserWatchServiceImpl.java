package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.Admin;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.entity.UserWatch;
import com.moxi.mogublog.commons.entity.WebNavbar;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.UserWatchMapper;
import com.moxi.mogublog.xo.mapper.WebNavbarMapper;
import com.moxi.mogublog.xo.service.AdminService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mogublog.xo.service.UserWatchService;
import com.moxi.mogublog.xo.service.WebNavbarService;
import com.moxi.mogublog.xo.vo.UserWatchVO;
import com.moxi.mogublog.xo.vo.WebNavbarVO;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 用户关注 服务实现类
 *
 * @author 陌溪
 * @date 2021年6月13日18:01:06
 */
@Service
public class UserWatchServiceImpl extends SuperServiceImpl<UserWatchMapper, UserWatch> implements UserWatchService {

    @Autowired
    private UserWatchService userWatchService;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;

    @Override
    public IPage<UserWatch> getPageList(UserWatchVO userWatchVO) {
        QueryWrapper<UserWatch> queryWrapper = new QueryWrapper<>();
        // 判断关注者和被关注者是否为空
        if(StringUtils.isEmpty(userWatchVO.getUserUid()) && StringUtils.isEmpty(userWatchVO.getToUserUid())) {
            throw new QueryException(MessageConf.PARAM_INCORRECT);
        }
        // 状态标志位，判断是否获取关注者 还是 被关注者【主要是通过userUid和toUserUid来进行判断】
        Boolean isWatch = false;
        // 获取TA关注了谁
        if(StringUtils.isNotEmpty(userWatchVO.getUserUid())) {
            isWatch = true;
            queryWrapper.eq(SQLConf.USER_UID, userWatchVO.getUserUid());
        }
        // 获取TA的粉丝
        if(StringUtils.isNotEmpty(userWatchVO.getToUserUid())) {
            isWatch = false;
            queryWrapper.eq(SQLConf.TO_USER_UID, userWatchVO.getToUserUid());
        }
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        Page<UserWatch> page = new Page<>();
        page.setCurrent(userWatchVO.getCurrentPage());
        page.setSize(userWatchVO.getPageSize());

        IPage<UserWatch> pageList = userWatchService.page(page, queryWrapper);
        List<UserWatch> userWatchList = pageList.getRecords();
        List<String> userIdList = new ArrayList<>();
        List<String> adminIdList = new ArrayList<>();
        Boolean finalIsWatch = isWatch;
        userWatchList.forEach(item -> {
            if(finalIsWatch) {
                // 获取他关注了谁
                if(Constants.STR_ONE.equals(item.getIsAdmin())) {
                    adminIdList.add(item.getToUserUid());
                } else {
                    userIdList.add(item.getToUserUid());
                }
            } else {
                // 获取谁关注了他【管理员无法主动关注用户】
                userIdList.add(item.getUserUid());
            }
        });
        List<User> userList = new ArrayList<>();
        List<Admin> adminList = new ArrayList<>();
        if(userIdList.size() > 0) {
            userList = userService.getUserListAndAvatarByIds(userIdList);
        }
        if(adminIdList.size() > 0) {
            adminList = adminService.getAdminListByUid(adminIdList);
        }
        Map<String, User> userMap = new HashMap<>();
        Map<String, Admin> adminMap = new HashMap<>();
        userList.forEach(item -> {
            userMap.put(item.getUid(), item);
        });
        adminList.forEach(item -> {
            adminMap.put(item.getUid(), item);
        });
        userWatchList.forEach(item -> {
            // 判断是关注者还是被关注者
            if(finalIsWatch) {
                // 获取用户的粉丝
                if(Constants.STR_ONE.equals(item.getIsAdmin())) {
                    item.setAdmin(adminMap.get(item.getToUserUid()));
                } else {
                    item.setUser(userMap.get(item.getToUserUid()));
                }
            } else {
                // 获取用户的粉丝
                item.setUser(userMap.get(item.getUserUid()));
            }
        });
        return pageList.setRecords(userWatchList);
    }

    @Override
    public String addUserWatch(UserWatchVO userWatchVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        Object userUid = request.getAttribute(SysConf.USER_UID);
        if(userUid == null) {
            return ResultUtil.errorWithMessage("用户未登录");
        }
        if (userUid.equals(userWatchVO.getToUserUid())) {
            return ResultUtil.errorWithMessage("您无法关注自己");
        }

        // 判断该用户是否被关注过
        QueryWrapper<UserWatch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, userUid.toString());
        queryWrapper.eq(SQLConf.TO_USER_UID, userWatchVO.getToUserUid());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        int count = userWatchService.count(queryWrapper);
        if(count > 0) {
            return ResultUtil.errorWithMessage("您已关注过该用户");
        }

        UserWatch userWatch = new UserWatch();
        userWatch.setUserUid(userUid.toString());
        userWatch.setToUserUid(userWatchVO.getToUserUid());
        userWatch.setIsAdmin(userWatchVO.getIsAdmin());
        userWatch.insert();
        return ResultUtil.successWithMessage("关注成功");
    }

    @Override
    public String deleteUserWatch(UserWatchVO userWatchVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        Object userUid = request.getAttribute(SysConf.USER_UID);
        if(userUid == null) {
            return ResultUtil.errorWithMessage("用户未登录");
        }
        QueryWrapper<UserWatch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, userUid.toString());
        queryWrapper.eq(SQLConf.TO_USER_UID, userWatchVO.getToUserUid());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.last(SysConf.LIMIT_ONE);
        UserWatch userWatch = userWatchService.getOne(queryWrapper);
        if(userWatch == null) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        } else {
            userWatch.setStatus(EStatus.DISABLED);
            userWatch.updateById();
            return ResultUtil.successWithMessage("取消关注成功");
        }
    }

    @Override
    public Boolean checkUserWatch(UserWatchVO userWatchVO) {
        QueryWrapper<UserWatch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, userWatchVO.getUserUid());
        queryWrapper.eq(SQLConf.TO_USER_UID, userWatchVO.getToUserUid());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        int count = userWatchService.count(queryWrapper);
        return count > 0;
    }
}
