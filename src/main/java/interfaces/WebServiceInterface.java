package interfaces;

import classes.Article;
import classes.Storage;

public interface WebServiceInterface {

    String isWebserviceOnline();
    Article getArticle(Integer id);
    void setRandomNumber(Integer id);

    String SERVER_IS_ONLINE = "SERVER IS ONLINE";
    String SERVER_IS_OFFLINE = "SERVER IS OFFLINE";
    Storage storage = new Storage();

    int randomNumber = 1;
}
