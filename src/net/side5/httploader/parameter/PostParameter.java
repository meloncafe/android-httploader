package net.side5.httploader.parameter;

import org.apache.http.message.BasicNameValuePair;

public class PostParameter {
    public static final int TYPE_FILE = 1001;
    public static final int TYPE_STRING = 1002;

    private BasicNameValuePair nameValuePair = null;

    private int type = TYPE_STRING;
    private String mimeType = "";

    public PostParameter(String name, String value) {
        nameValuePair = new BasicNameValuePair(name, value);
    }

    public PostParameter(String name, String value, int type) {
        nameValuePair = new BasicNameValuePair(name, value);
        this.type = type;
    }

    public PostParameter(String name, String value, int type, String mimeType) {
        nameValuePair = new BasicNameValuePair(name, value);
        this.type = type;
        this.mimeType = mimeType;
    }

    public String getName() {
        return nameValuePair.getName();
    }

    public String getValue() {
        return nameValuePair.getValue();
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}