package classes;

public class WebServiceClass {

    private Storage storage = new Storage();
    public int randomNumber = 1;

    public String isWebServerOnline() {
        return "SERVER IS ONLINE";
    }

    public Article getArticle(int id){
        return storage.getArticle(id);
    }
}
