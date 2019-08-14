package classes;

public class WebServiceClass {

    public Storage storage;
    public int randomNumber = 1;
    public boolean serverOnline = true;

    public WebServiceClass() {
        storage = new Storage();
    }

    public String isWebServerOnline() {
        if (serverOnline) {
        return "SERVER IS ONLINE";
        } else return "SERVER IS OFFLINE";
    }

    public Article getArticle(int id){
        return storage.getArticle(id);
    }
}
