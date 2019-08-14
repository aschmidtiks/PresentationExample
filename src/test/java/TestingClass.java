import classes.*;
import exceptions.ArticleException;
import interfaces.WebServiceInterface;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static interfaces.WebServiceInterface.SERVER_IS_OFFLINE;
import static interfaces.WebServiceInterface.SERVER_IS_ONLINE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestingClass {

    @Mock
    WebServiceInterface webServiceInterface;

    UserController userController;

    /*
    Mocks und spies können extra Namen gegeben werden welche bei Fehlermeldungen genutzt werden
    */
    @Mock (name = "Injected Storage")
    Storage storage;
    @InjectMocks
    WebServiceClass webServiceClass;

    @Captor
    ArgumentCaptor<List<Article>> captor;

    /*
    Vor jeden Test wird für den Test eine Instanz von UserController angelegt.
    */
    @BeforeEach
    public void setupTests() {
        userController = new UserController();
        //webServiceInterface = mock(WebServiceInterface.class);    -> nicht nötig da durch die Annotation automatisch initialisiert wird
    }

    /*
    In diesem Test wird mehrfach stubbing getestet und 2 Testfunktionen aufgerüfen welche prüfen ob die Listen die sie beinhalten Listen gefüllt sind.
    Da stubs final sind bleibt der letzte Stub für alle nachfolgenden Aufrufe bestehen.
    Bei der Verifikation wird zusätzlich geprüft ob die Methode isWebServiceOnline() 3 mal ausgeführt wurde.
    */
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

    /*
    Es wird geprüft ob die User Liste der Klasse UserController gefüllt ist
    */
    @Test
    public void testDoesUserExists() {
        assertEquals(1, userController.userList.size(), "test if a user exists");
    }

    /*
    Es wird geprüft ob die Artikel Liste der Klasse storage gefüllt ist
    */
    @Test
    public void testArticleListNotEmpty() {
        assertEquals(1, webServiceInterface.storage.articleList.size(), "test if an article exists in the storage");
    }

    /*
     In diesem Test wird die Methode getArticle des Interfaces auf die Rückgabe eines dummy Artikels bei Annahme irgendeines Integers gestubbed.
    */
    @Test
    public void testGetArticleAndReturnDummyWithInterface() {
        Article dummy = mock(Article.class);
        when(webServiceInterface.getArticle(anyInt())).thenReturn(dummy);
        webServiceInterface.getArticle(anyInt());
        verify(webServiceInterface).getArticle(anyInt());
    }

    /*
    In diesem Test wird das in webServiceClass injizierte mock "storage" auf die Rückgabe eines dummy bei Annahme irgendeines Integers Artikels gestubbed.
    Anschließend wird eine Methode in webServiceClass aufgerufen welche einen weiteren Aufruf in storage startet.
    In der Verifikation wird der Aufruf der getArticle Methode in dem storage Objekt geprüft.
    Dies ist gültig da storage ein mock ist welches von webServiceClass genutzt wird / in webServiceClass injiziert wurde
    */
    @Test
    public void testGetArticleAndReturnDummyWithInjectedMocks() {
        Article dummy = mock(Article.class);
        when(storage.getArticle(anyInt())).thenReturn(dummy);
        webServiceClass.getArticle(anyInt());
        verify(storage).getArticle(anyInt());
    }

    /*
    In diesem Test wird gezeigt dass ein spy wir eine normale Instanz und gleichzeitig wie ein mock genutzt werden kann.
    Die Isntanz wird erstellt, die Methode isWebServerOnline auf den Rückgabewert "SERVER IS ONLINE" gestubbed
    */
    @Test
    public void testIsWebServiceOnlineWithSpy() {
        WebServiceClass serviceclass = spy(new WebServiceClass());
        when(serviceclass.isWebServerOnline()).thenReturn("SERVER IS ONLINE");
        assertEquals(SERVER_IS_ONLINE, serviceclass.isWebServerOnline());
    }

    /*
    In diesem Test wird durch übergabe einer ungültigen ID eine exception geworfen
    In der Methode refund von UserController prüft den Namen und die ID eines Artikels, stimmen diese nicht überein wird eine Exception geworden
    assertThrows gillt als passed wenn eine Exception geworfen wurde
    */
    @Test
    public void testGetArticleException() {
        assertThrows(ArticleException.class, () -> {
            userController.refund(webServiceClass, "article1", 2);
        });
    }

    /*
    In diesem Test soll die getter Methode des Interfaces getestet werden.
    Da diese einen Artikel zurück gibt wird ein Dummy als Argument genutzt.
    Anschließend wird die argThat Methode genutzt um das Argument für die Verifikation gültig zu machen.
        - in diesem Fall wird geprüft ob eine 1 als ID übergeben wurde wodurch für das Argument eni true zurückgegeben wird
            was der Verifikation reicht
    Zuerst die normale Schreibweise und danach die verkürzte Lambda Version
    */
    @Test
    public void testArgThat() {
        Article dummy = mock(Article.class);

        when(webServiceInterface.getArticle(1)).thenReturn(dummy);
        webServiceInterface.getArticle(1);

        verify(webServiceInterface).getArticle(argThat(new ArgumentMatcher<Integer>() {
            @Override
            public boolean matches(Integer i) {
                if (i == 1) {
                    return true;
                }
                return false;
            }
        }));

        //verify(webServiceInterface).getArticle(argThat(Integer -> Integer == 1));
    }

    /*
    Mock erstellt eine "leere" Kopie des Objekt bei welcher alle Werte auf 0 gesetzt werden
        - Mock einer Instanz behält die initialisierten Werte bei
    Spy erstellt eine voll funktionsfähige Instanz
    In diesem Test:
        - wird die "randomNumber" welche mit dem Wert 1 initialisiert wurde um 1 addiert.
        - wird die Klasse WebServiceClass gemockt, wodurch alle Werte darin auf 0 gesetzt werden, dementsprechend ist das Ergebnis von radnomNumber + 1 = 1
        - wird das Interface WebServiceInterface gemockt, alle Werte werden beibehalten, dementsprechen ist das Ergbnis von randomNumber + 1 = 2
        - wird ein Spion der Klasse WebServiceClass erstellt, alle Werte werden beibehalten, dementsprechend ist das Ergebnis von randomNumber + 1 = 2
    */
    @Test
    public void testIncrementNumberOnSpyAndOnMock(){
        WebServiceClass WebServiceClass = mock(WebServiceClass.class);
        WebServiceInterface webServiceInterface = mock(WebServiceInterface.class);
        WebServiceClass WebServiceClassSpy = spy(new WebServiceClass());

        assertEquals(1, WebServiceClass.randomNumber + 1);
        assertEquals(2, webServiceInterface.randomNumber + 1);
        assertEquals(2, WebServiceClassSpy.randomNumber + 1);
    }

    /*
    In diesem Test wird ein Spion für die Klasse "storage" angelegt. Spy wird genutzt da die Liste in der Klasse über die Add-Methode gefüllt werden soll was mit einem mock nicht geht.
    Dann werden zwei neue Artikel angelegt und "storage" hinzugefügt.
    Danach werden alle Artikel in "storage" in eine gesonderte Liste gespeichert, dies wird genutzt um die dabei übergebenen Argument über den ArgumentCaptor
    abzufangen und abgezuspeicher um diese anschließend zu prüfen.

    Mann könnte eine Liste direkt mocken (siehe mockedList) und diese mit Add befüllen was aber der falsche Ansatz wäre, man kann auch eine Schraube mit nem Hammer festmachen aber man tut es einfach nicht.
    */
    @Test
    public void testArgumentCaptor() {
        Storage spyStorage = spy(new Storage());
        List<Article> spiedList = spy(new ArrayList<>());
        //List<Article> mockedList = mock(List.class);

        Article article2 = new Article("article2", 2);
        Article article3 = new Article("article3", 3);

        spyStorage.articleList.add(spyStorage.CreateArticle(article2.name, article2.id));
        spyStorage.articleList.add(spyStorage.CreateArticle(article3.name, article3.id));

        spiedList.addAll(spyStorage.articleList);
        verify(spiedList).addAll(captor.capture());

        List<Article> capturedArgument = captor.getValue();

        assertEquals(article2.name, capturedArgument.get(1).name);
    }

    /*
    In diesem Test wird eine Antwort erstellt, in diesem Fall ein einfaches Return, welche ausgeführt werden soll sobald über das Interface die Methode
    isWebServiceOnline() aufgerufen wird.
    Der erste Part des Tests ist die normale/lange Version einer Antwort und die letzten beiden sind die Kurzform durch Lambda.
    */
    @Test
    public void testAnswer() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock){
                //Hier kann man allerlei Gedöns machen
                Object[] args = invocationOnMock.getArguments();
                Object mock = invocationOnMock.getMock();
                return "SERVER IS UNKNOWN";
            }
        }).when(webServiceInterface).isWebserviceOnline();
        assertEquals("SERVER IS UNKNOWN", webServiceInterface.isWebserviceOnline());

        /*doAnswer(invocationOnMock -> "SERVER IS UNKNOWN").when(webServiceInterface).isWebserviceOnline();
        assertEquals("SERVER IS UNKNOWN", webServiceInterface.isWebserviceOnline());*/
    }

    /*
    Bei Void-Methoden muss zuerst das Verhalten beschrieben werden und dann die Bedingung.
    Außerdem muss null zurückgegeben werden.
    In diesem Test wird eine Setter Methode aufgerufen, die Testantwort prüft welcher Wert als Argument übergeben wurde
    */
    @Test
    public void testVoidMethods() {
        doAnswer((args) -> {
            assertEquals(5, (int)args.getArgument(0));
            return null;
        }).when(webServiceInterface).setRandomNumber(anyInt());
        webServiceInterface.setRandomNumber(5);
    }


}
