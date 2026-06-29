package divar.aut.frontend.ui;
import javafx.scene.Parent;
import divar.aut.frontend.DivarApplication;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
public class ViewManager
{
    private final StackPane root;
    private final DivarApplication mainApp;
    public ViewManager(StackPane root, DivarApplication mainApp)
    {
        this.root = root;
        this.mainApp = mainApp;
    }
    public void show(Parent newView)
    {
        if(!root.getChildren().isEmpty())
            root.getChildren().removeFirst();
        root.getChildren().add(newView);
    }
    public void toWelcome() { show(new WelcomeScreen(this).getView()); }
    public void toLogin() { show(new LoginScreen(this).getView()); }
    public void toRegister() { show(new RegisterScreen(this).getView()); }
    public void toMain(){
        MainView mainView = new MainView(this);
        show(mainView.getView());
    }
}
