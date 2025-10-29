package net.sourceforge.plantuml.servlet.model;

import java.sql.Timestamp;

/**
 * 图表历史记录实体类.
 */
public class DiagramHistory {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private String originalText;
    private String plantumlCode;
    private String diagramType;
    private String imageUrl;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Boolean isFavorite;
    private Boolean isDeleted;

    public DiagramHistory() {
    }

    public DiagramHistory(final Long newUserId, final String newOriginalText, final String newPlantumlCode) {
        this.userId = newUserId;
        this.originalText = newOriginalText;
        this.plantumlCode = newPlantumlCode;
        this.isFavorite = false;
        this.isDeleted = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(final Long newId) {
        this.id = newId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long newUserId) {
        this.userId = newUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String newTitle) {
        this.title = newTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String newDescription) {
        this.description = newDescription;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(final String newOriginalText) {
        this.originalText = newOriginalText;
    }

    public String getPlantumlCode() {
        return plantumlCode;
    }

    public void setPlantumlCode(final String newPlantumlCode) {
        this.plantumlCode = newPlantumlCode;
    }

    public String getDiagramType() {
        return diagramType;
    }

    public void setDiagramType(final String newDiagramType) {
        this.diagramType = newDiagramType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(final String newImageUrl) {
        this.imageUrl = newImageUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Timestamp newCreatedAt) {
        this.createdAt = newCreatedAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Timestamp newUpdatedAt) {
        this.updatedAt = newUpdatedAt;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(final Boolean newIsFavorite) {
        this.isFavorite = newIsFavorite;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(final Boolean newIsDeleted) {
        this.isDeleted = newIsDeleted;
    }
}

