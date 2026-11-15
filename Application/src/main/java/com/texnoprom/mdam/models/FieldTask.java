package com.texnoprom.mdam.models;

/** Задание на обход объекта. Хранится на телефоне у вошедшего пользователя. */
public class FieldTask {
    private String id;
    private String owner;
    private String objectName;
    private String deviceType;
    private String note;
    private boolean done;
    private long createdAt;

    public FieldTask() {
    }

    public FieldTask(String id, String owner, String objectName, String deviceType, String note, long createdAt) {
        this.id = id;
        this.owner = owner;
        this.objectName = objectName;
        this.deviceType = deviceType;
        this.note = note;
        this.done = false;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
