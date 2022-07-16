package com.wkclz.gen.bean;

public class GenTaskInfo {


    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 模板ID
     */
    private Long tempId;

    /**
     * 任务变量
     */
    private String taskName;

    /**
     * 是否生成
     */
    private Integer needCreate;

    /**
     * 是否删除(本地模式有效)
     */
    private Integer needDelete;

    /**
     * 生产的文件后缀
     */
    private String fileSubfix;

    /**
     * 任务项目基本路径
     */
    private String projectBasePath;

    /**
     * 任务包路径
     */
    private String packagePath;

    /**
     * 任务变量
     */
    private String variables;

    /**
     * 任务描述
     */
    private String taskDesc;


    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getTempId() {
        return tempId;
    }

    public void setTempId(Long tempId) {
        this.tempId = tempId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getNeedCreate() {
        return needCreate;
    }

    public void setNeedCreate(Integer needCreate) {
        this.needCreate = needCreate;
    }

    public Integer getNeedDelete() {
        return needDelete;
    }

    public void setNeedDelete(Integer needDelete) {
        this.needDelete = needDelete;
    }

    public String getFileSubfix() {
        return fileSubfix;
    }

    public void setFileSubfix(String fileSubfix) {
        this.fileSubfix = fileSubfix;
    }

    public String getProjectBasePath() {
        return projectBasePath;
    }

    public void setProjectBasePath(String projectBasePath) {
        this.projectBasePath = projectBasePath;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public String getVariables() {
        return variables;
    }

    public void setVariables(String variables) {
        this.variables = variables;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }
}
