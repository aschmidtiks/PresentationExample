package interfaces;

import classes.Article;
import classes.Storage;

public interface WebServiceInterface {

    String isWebserviceOnline();

    String SERVER_IS_ONLINE = "SERVER IS ONLINE";
    String SERVER_IS_OFFLINE = "SERVER IS OFFLINE";
    Storage storage = new Storage();

    Article getArticle(Integer id);

    int randomNumber = 1;
}
