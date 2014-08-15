package net.side5.httploader.data;

public class IndexesListData {
    private int no = 0;
    private String title = "";
    private String name = "";
    private String url = "";
    private String lastModify = "";
    private String size = "";
    private boolean dir = false;
    public int getNo() {
        return no;
    }
    public void setNo(int no) {
        this.no = no;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getLastModify() {
        return lastModify;
    }
    public void setLastModify(String lastModify) {
        this.lastModify = lastModify;
    }
    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public boolean isDir() {
        return dir;
    }
    public void setDir(boolean dir) {
        this.dir = dir;
    }
}