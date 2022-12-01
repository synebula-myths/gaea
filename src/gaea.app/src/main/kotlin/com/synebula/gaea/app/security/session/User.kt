package com.synebula.gaea.app.security.session

import com.synebula.gaea.data.permission.AuthorityType
import com.synebula.gaea.data.permission.PermissionType

open class User {
    /**
     * 权限类型, 根据类型来动态应用[permissions]中的权限信息. 通常配置在角色中
     */
    var permissionType = PermissionType.Minimum

    /**
     * 用户的权限信息. 通常通过角色配置
     */
    var permissions = mapOf<String, AuthorityType>()
}