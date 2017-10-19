import javax.swing.*;

/**
 * Created by Caelestis on 12.10.2017.
 */
public class Main {
    public static void main(String[] args) {
        //java -jar ./target/2048-1.0.1.jar
        Model model = new Model();
        Controller controller = new Controller(model);
        JFrame game = new JFrame();
        game.setTitle("2048");
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game.setSize(450, 500);
        game.setResizable(false);
        game.add(controller.getView());
        game.setLocationRelativeTo(null);
        game.setVisible(true);

    }
}
