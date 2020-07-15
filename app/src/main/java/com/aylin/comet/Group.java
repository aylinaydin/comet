package com.aylin.comet;

/**
 * Created by Aylin on 26.03.2018.
 */

public class Group {
    private String groupId;
    private String groupName;
    private String groupKey;
    private Member member;

    public Group(String groupId, String groupName, String groupKey) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupKey = groupKey;
    }

    public Group() {

    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
