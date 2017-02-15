package com.atguigu.imgjw.modle.bean;

/**
 * Created by 皇 上 on 2017/2/15.
 */
public class GroupInfo {

    //群名称
    private String groupName;
    //群号
    private String groupId;
    //群主
    private String invitePerson;

    public GroupInfo() {
    }

    public GroupInfo(String groupName, String groupId, String invitePerson) {
        this.groupName = groupName;
        this.groupId = groupId;
        this.invitePerson = invitePerson;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getInvitePerson() {
        return invitePerson;
    }

    public void setInvitePerson(String invitePerson) {
        this.invitePerson = invitePerson;
    }
}
