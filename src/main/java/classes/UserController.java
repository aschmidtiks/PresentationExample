package classes;

import exceptions.ArticleException;

import java.util.ArrayList;
import java.util.List;

public class UserController {

    public List<User> userList = new ArrayList<User>();

    public UserController() {
       if (userList.size() == 0) {
           userList.add(CreateUser("user1", 1, 1));
       }
    }

    public User CreateUser(String name, int id, int level) {
        User newUser;
        newUser = new User(name, id, level);
        return newUser;
    }

    public boolean refund(WebServiceClass webServiceClass, String name, int id){
        Article article = webServiceClass.getArticle(id);
        if (article != null) {
            return true;
        } else throw new ArticleException("Article with ID: " + id + " not found!");
    }
}
