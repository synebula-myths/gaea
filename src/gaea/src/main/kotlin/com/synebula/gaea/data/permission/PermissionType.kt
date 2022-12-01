package com.synebula.gaea.data.permission

/**
 * 角色权限类型
 */
enum class PermissionType {
    /**
     * 拥有所有权限
     */
    All,

    /**
     * 拥有最大权限
     * 不配置无权则有权限
     */
    Maximum,

    /**
     * 最小权限
     * 不配置有权则无权限
     */
    Minimum,

    /**
     * 没有任何权限
     */
    None
}