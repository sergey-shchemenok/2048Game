
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by Caelestis on 12.10.2017.
 */
public class Controller extends KeyAdapter {
    private Model model;
    private View view;
    private static final int WINNING_TILE = 64;

    public Controller(Model model) {
        this.model = model;
        this.view = new View(this);
    }

    public void resetGame(){
        model.score = 0;
        model.maxTile = 0;
        view.isGameLost = false;
        view.isGameWon = false;
        model.previousScores.clear();
        model.previousStates.clear();
        model.resetGameTiles();
    }

    public Tile[][] getGameTiles(){
        return model.getGameTiles();
    }

    public int getScore(){
        return model.score;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            resetGame();
        }
        else {
            if (!model.canMove()) {
                view.isGameLost = true;
            }
            else {
                if (!view.isGameLost && !view.isGameWon) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP : model.up(); break;
                        case KeyEvent.VK_W : model.up(); break;
                        case KeyEvent.VK_DOWN : model.down(); break;
                        case KeyEvent.VK_S : model.down(); break;
                        case KeyEvent.VK_LEFT : model.left(); break;
                        case KeyEvent.VK_A : model.left(); break;
                        case KeyEvent.VK_RIGHT : model.right(); break;
                        case KeyEvent.VK_D : model.left(); break;
                        case KeyEvent.VK_Q : model.rollback(); break;
                        case KeyEvent.VK_R : model.randomMove(); break;
                        case KeyEvent.VK_T : model.autoMove(); break;
                        case KeyEvent.VK_Y : model.intelligentMove(); break;
                        case KeyEvent.VK_U : model.superMove(); break;
                        case KeyEvent.VK_I : model.hyperMove(); break;


                        /*case KeyEvent.VK_V : {
                            for (int i = 0; i < 100; i++)
                                model.autoMove();

                        } break;*/
                    }
                }
                if (model.maxTile == WINNING_TILE) {
                    view.isGameWon = true;
                }
            }
        }
        if (!view.isGameLost && !view.isGameWon ){
            view.repaint();
        } else  {
            view.repaint();
            view.winPaint();
        }
    }

    /*public void MoveToTheEnd() throws InterruptedException {
        while(view.isGameLost == false || view.isGameWon == false){
        //for (int i = 0; i < 100; i++){
            model.autoMove();
            Thread.sleep(200);
        }
    }*/

    public View getView() {
        return view;
    }
}
