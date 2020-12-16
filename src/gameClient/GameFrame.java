package gameClient;
import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    GameData data;
    GamePanel gamePanel;
    LoginPanel loginPanel;
    BackgroundPanel container;

    public GameFrame(){
        super();
        //this.data = data; // Init the game data for this GUI frame.
        //addPanel(data);

        loginPanel = new LoginPanel(gamePanel,this);

        container = new BackgroundPanel();
        container.setLayout(new BorderLayout());

        //this.add(loginPanel);
        Box box = new Box(BoxLayout.Y_AXIS);
        box.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        box.add(Box.createVerticalGlue());
        box.add(loginPanel);
        box.add(Box.createVerticalGlue());
        container.add(box, BorderLayout.CENTER);
        container.setPreferredSize(new Dimension(getWidth(),getHeight()));
        add(container);
        setMinimumSize(new Dimension(300,250));
        initFrame();
        setVisible(true);
    }

    private void initFrame(){
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(dim.width/2+dim.width/4,dim.height/2+dim.height/4);
        this.setLocation(dim.width/2 - this.getSize().width/2,dim.height/2 - this.getSize().height/2);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //this.setUndecorated(true); //allows not showing the default menu.
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    private void addPanel(GameData data){
        gamePanel = new GamePanel();
        gamePanel.update(data);
        this.add(gamePanel);
    }

    private class BackgroundPanel extends JPanel{
        GameResources backgroundsRes;
        public BackgroundPanel(){
            backgroundsRes = new GameResources();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2D = (Graphics2D) g;
            g2D.drawImage(backgroundsRes.getLoginBackground(), 0, 0,getWidth(),getHeight(), null); // Background
        }
    }
}
