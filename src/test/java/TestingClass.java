import classes.*;
import exceptions.ArticleException;
import interfaces.WebServiceInterface;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static interfaces.WebServiceInterface.SERVER_IS_OFFLINE;
import static interfaces.WebServiceInterface.SERVER_IS_ONLINE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestingClass {

    @Mock
    WebServiceInterface webServiceInterface;

    UserController userController;

    @Mock
    Storage storage;
    @InjectMocks
    WebServiceClass webServiceClass;


    @BeforeEach
    public void setupTests() {
        userController = new UserController();
    }

    @Test
    public void testSetup() {
        //when(webServiceInterface.isWebserviceOnline()).thenReturn("SERVER IS ONLINE", "SERVER IS OFFLINE");
        when(webServiceInterface.isWebserviceOnline()).thenReturn("SERVER IS ONLINE").thenReturn("SERVER IS OFFLINE");

        assertEquals(SERVER_IS_ONLINE, webServiceInterface.isWebserviceOnline(), "test if server is online");
        assertEquals(SERVER_IS_OFFLINE, webServiceInterface.isWebserviceOnline(), "test if server is offline (second invocation");
        assertEquals(SERVER_IS_OFFLINE, webServiceInterface.isWebserviceOnline(), "test if server is offline (second invocation");

        testDoesUserExists();
        testArticleListNotEmpty();

        verify(webServiceInterface, times(3)).isWebserviceOnline();
    }

    @Test
    public void testDoesUserExists() {
        assertEquals(1, userController.userList.size(), "test if a user exists");
    }

    @Test
    public void testArticleListNotEmpty() {
        assertEquals(1, webServiceInterface.storage.articleList.size(), "test if an article exists in the storage");
    }

    @Test
    public void testGetArticleAndReturnDummyWithInterface() {
        Article dummy = mock(Article.class);
        when(webServiceInterface.getArticle(anyInt())).thenReturn(dummy);
        webServiceInterface.getArticle(anyInt());
        verify(webServiceInterface).getArticle(anyInt());
    }

    /*
    Der injizierte Mock (storage) wird gestubbed und geprüft aber der Funktionsaufruf kommt über das Objekt webServiceClass
     */
    @Test
    public void testGetArticleAndReturnDummyWithInjectedMocks() {
        //argThat for dummy?
        Article dummy = mock(Article.class);
        when(storage.getArticle(anyInt())).thenReturn(dummy);
        webServiceClass.getArticle(anyInt());
        verify(storage).getArticle(anyInt());
    }

    /*
    Es wird eine funktionsfähige Instanz von WebServiceClass erstellt
    Methoden können normal genutzt werden aber auch gestubbed
     */
    @Test
    public void testIsWebServiceOnlineWithSpy() {
        WebServiceClass serviceclass = spy(new WebServiceClass());
        when(serviceclass.isWebServerOnline()).thenReturn("SERVER IS ONLINE");
        assertEquals(SERVER_IS_ONLINE, serviceclass.isWebServerOnline());
    }

    @Test
    public void testGetArticleException() {
        assertThrows(ArticleException.class, () -> {
            userController.refund(webServiceClass, "article1", 2);
        });
    }

    @Test
    public void testArgThat() {
        Article dummy = mock(Article.class);

        when(webServiceInterface.getArticle(1)).thenReturn(dummy);
        webServiceInterface.getArticle(1);

        /*verify(webServiceInterface).getArticle(argThat(new ArgumentMatcher<Integer>() {
            @Override
            public boolean matches(Integer i) {
                if (i == 1) {
                    return true;
                }
                return false;
            }
        }));*/

        verify(webServiceInterface).getArticle(argThat(Integer -> Integer == 1));
    }

    /*
        Mock erstellt eine "leere" Kopie des Objekt bei welcher alle Werte auf 0 gesetzt werden
            - Mock einer Instanz behält die initialisierten Werte bei
        Spy erstellt eine voll funktionsfähige Instanz
     */
    @Test
    public void testIncrementNumberOnSpyAndOnMock(){
        WebServiceClass WebServiceClass = mock(WebServiceClass.class);
        WebServiceInterface webServiceInterface = mock(WebServiceInterface.class);
        WebServiceClass WebServiceClassSpy = spy(new WebServiceClass());

        assertEquals(1, WebServiceClass.randomNumber + 1);
        assertEquals(3, webServiceInterface.randomNumber + 2);
        assertEquals(2, WebServiceClassSpy.randomNumber + 1);
    }

}
