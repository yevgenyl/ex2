package gameClient;
import api.*;
import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame implements game_listener {

    GameData data;
    GamePanel panel;

    public GameFrame(GameData data){
        super();
        this.data = data; // Init the game data for this GUI frame.
        initFrame();
        addPanel();
        this.setVisible(true);
        data.getManager().register(this);
    }

    public void update(){
        panel.update(data);
    }

    private void initFrame(){
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(dim.width/2,dim.height/2);
        this.setLocation(dim.width/2 - this.getSize().width/2,dim.height/2 - this.getSize().height/2);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // this.setUndecorated(true); allows not showing the default menu.
    }

    private void addPanel(){
        panel = new GamePanel();
        panel.update(data);
        this.add(panel);
    }

    private void addMenue(){
       MenuBar menuBar = new MenuBar();
       Menu file = new Menu();
    }

}
