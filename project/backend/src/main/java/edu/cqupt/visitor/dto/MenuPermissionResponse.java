package edu.cqupt.visitor.dto;

import java.util.ArrayList;
import java.util.List;

public class MenuPermissionResponse {

    private Long id;
    private Long parentId;
    private String permissionCode;
    private String permissionName;
    private String routePath;
    private String componentPath;
    private Integer sortOrder;
    private List<MenuPermissionResponse> children = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getRoutePath() {
        return routePath;
    }

    public void setRoutePath(String routePath) {
        this.routePath = routePath;
    }

    public String getComponentPath() {
        return componentPath;
    }

    public void setComponentPath(String componentPath) {
        this.componentPath = componentPath;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public List<MenuPermissionResponse> getChildren() {
        return children;
    }

    public void setChildren(List<MenuPermissionResponse> children) {
        this.children = children;
    }
}