package itp341.wang.xinghan.reedforneed.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Xinghan on 5/4/16.
 */
public class Record implements Serializable{
    private String originLanguage;
    private String destinationLanguage;
    private String contentOriginLanguage;
    private String contentDestinationLanguage;
    private Date creationDate;
    private String imgString;

    public Record() {
        destinationLanguage = "English";         //default destinationLanguage
    }

    public String getImgString() {
        return imgString;
    }

    public void setImgString(String imgString) {
        this.imgString = imgString;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getOriginLanguage() {
        return originLanguage;
    }

    public void setOriginLanguage(String originLanguage) {
        this.originLanguage = originLanguage;
    }

    public String getDestinationLanguage() {
        return destinationLanguage;
    }

    public void setDestinationLanguage(String destinationLanguage) {
        this.destinationLanguage = destinationLanguage;
    }

    public String getContentOriginLanguage() {
        return contentOriginLanguage;
    }

    public void setContentOriginLanguage(String contentOriginLanguage) {
        this.contentOriginLanguage = contentOriginLanguage;
    }

    public String getContentDestinationLanguage() {
        return contentDestinationLanguage;
    }

    public void setContentDestinationLanguage(String contentDestinationLanguage) {
        this.contentDestinationLanguage = contentDestinationLanguage;
    }
}
