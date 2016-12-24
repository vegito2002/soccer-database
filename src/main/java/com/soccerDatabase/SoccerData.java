//-------------------------------------------------------------------------------------------------------------//
// Code based on a tutorial by Shekhar Gulati of SparkJava at
// https://blog.openshift.com/developing-single-page-web-applications-using-java-8-spark-mongodb-and-angularjs/
//-------------------------------------------------------------------------------------------------------------//

package com.soccerDatabase;

import java.util.Date;

public class SoccerData {

    private String id;
    private String title;
    private boolean done;
    private Date createdOn = new Date();

    public SoccerData(String id, String title, boolean done, Date createdOn) {
        this.id = id;
        this.title = title;
        this.done = done;
        this.createdOn = createdOn;
    }
    
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isDone() {
        return done;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SoccerData soccerData = (SoccerData) o;

        if (done != soccerData.done) return false;
        if (id != null ? !id.equals(soccerData.id) : soccerData.id != null) return false;
        if (title != null ? !title.equals(soccerData.title) : soccerData.title != null) return false;
        return !(createdOn != null ? !createdOn.equals(soccerData.createdOn) : soccerData.createdOn != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (done ? 1 : 0);
        result = 31 * result + (createdOn != null ? createdOn.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SoccerData{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", done=" + done +
                ", createdOn=" + createdOn +
                '}';
    }
}
