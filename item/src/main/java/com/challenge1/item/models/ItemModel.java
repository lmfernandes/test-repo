package com.challenge1.item.models;

import com.challenge1.item.validations.annotations.Identifier;
import com.challenge1.item.validations.annotations.Text;
import java.io.Serializable;

public class ItemModel implements Serializable {

    private static final long serialVersionUID = -1166042779400226011L;

    @Identifier(nullable = true, empty = true)
    private String id;
    @Text
    private String name;
    @Text
    private String description;
    @Identifier
    private String groupId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
