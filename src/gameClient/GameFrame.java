package gameClient;
import javax.swing.*;
import java.awt.*;

/**
 * This class is responsible for showing the main graphical user interface for this game.
 */
public class GameFrame extends JFrame {

    private GamePanel gamePanel; // The main game panel
    private LoginPanel loginPanel; // The login panel
    private BackgroundPanel container; // The panel which hold the background.

    /**
     * Default constructor.
     * Login panel will be shown by default.
     */
    public GameFrame(){
        super();
        loginPanel = new LoginPanel(gamePanel,this);
        container = new BackgroundPanel();
        container.setLayout(new BorderLayout());

        /*
         * Here we are using Box layout for the login window.
         */
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

    /**
     * Initializtion constructor.
     * Used by direct commands from the command line.
     * @param id the specified user id (9 digits).
     * @param scenario the scenario number.
     */
    public GameFrame(int id, int scenario){
        Game game = new Game(id,scenario);
        Thread gameThread = new Thread(game);
        gameThread.start();
        gamePanel = new GamePanel(game.getData());
        add(gamePanel);
        setMinimumSize(new Dimension(300,250));
        initFrame();
        setVisible(true);
    }

    /**
     * Initializes this frame.
     */
    private void initFrame(){
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(dim.width/2+dim.width/4,dim.height/2+dim.height/4);
        this.setLocation(dim.width/2 - this.getSize().width/2,dim.height/2 - this.getSize().height/2);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Returns a pointer to the container panel (background)
     * @return the container panel.
     */
    public BackgroundPanel getContainer() {
        return container;
    }

    /**
     * Override of the default paint method.
     * @param g - the graphical object.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    /**
     * This class is responsible for showing the background panel (the image) which wraps the login panel.
     */
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
