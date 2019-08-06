package classes;

import exceptions.ArticleException;

import java.util.ArrayList;
import java.util.List;

public class Storage {

    public List<Article> articleList = new ArrayList<Article>();

    public Storage () {
        if (articleList.size() == 0) {
            articleList.add(CreateArticle("article1", 1));
        }
    }

    public Article CreateArticle(String name, int level) {
        Article newArticle;
        newArticle = new Article(name, level);
        return newArticle;
    }

    public Article getArticle(int id){
        for (Article article : articleList) {
            if (article.id == id) {
                return article;
            }
        }
        return null;
    }
}
