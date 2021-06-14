package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 用户关注表
 * @author 陌溪
 * @date 2021年6月13日17:10:34
 */
@Data
@TableName("t_user_watch")
public class UserWatch extends SuperEntity<UserWatch> {

    private static final long serialVersionUID = 1L;

    /**
     * 关注人UID
     */
    private String userUid;

    /**
     * 被关注人UID
     */
    private String toUserUid;

    /**
     * 是否是管理员：0否，1是
     */
    private String isAdmin;

    /**
     * 关注的管理员信息
     */
    @TableField(exist = false)
    private Admin admin;

    /**
     * 关注的用户信息
     */
    @TableField(exist = false)
    private User user;
}
