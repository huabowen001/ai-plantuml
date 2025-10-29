package net.sourceforge.plantuml.servlet.model;

import java.sql.Timestamp;

/**
 * 图表版本模型.
 */
public class DiagramVersion {
    private Long id;
    private Long historyId;
    private Integer versionNumber;
    private String plantumlCode;
    private String title;
    private String changeDescription;
    private Timestamp createdAt;
    private Long createdBy;

    public DiagramVersion() {
    }

    public DiagramVersion(Long histId, Integer verNum, String code) {
        this.historyId = histId;
        this.versionNumber = verNum;
        this.plantumlCode = code;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long newId) {
        this.id = newId;
    }

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long histId) {
        this.historyId = histId;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer verNum) {
        this.versionNumber = verNum;
    }

    public String getPlantumlCode() {
        return plantumlCode;
    }

    public void setPlantumlCode(String code) {
        this.plantumlCode = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public String getChangeDescription() {
        return changeDescription;
    }

    public void setChangeDescription(String desc) {
        this.changeDescription = desc;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp timestamp) {
        this.createdAt = timestamp;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long userId) {
        this.createdBy = userId;
    }
}

