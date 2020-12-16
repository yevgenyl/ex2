package gameClient;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.jar.JarEntry;

public class LoginPanel extends JPanel implements ActionListener {

    JButton playButton;
    GameFrame gameFrame;
    GamePanel gamePanel;
    LoginBackgroundPanel contentPane;
    JTextField idTextField, levelField;

    public LoginPanel(GamePanel gamePanel, GameFrame gameFrame){
        super();
        this.gameFrame = gameFrame;
        this.gamePanel = gamePanel;
        //this.setLayout(new BorderLayout());
        JPanel idAnLevelPanel = new JPanel( new BorderLayout());
        JPanel idPanel = new JPanel();
        JPanel levelPanel = new JPanel();
        JPanel playButtonPanel = new JPanel();
        JPanel loginLabelPanel = new JPanel();
        JLabel idLabel = new JLabel("ID");
        idTextField = new JTextField(9);
        idTextField.setFont(new Font("Serif",Font.BOLD,30));
        idLabel.setFont(new Font("Serif",Font.BOLD,30));
        idPanel.add(idLabel);
        idPanel.add(idTextField);
        idAnLevelPanel.add(idPanel,BorderLayout.NORTH);
        JLabel levelLabel = new JLabel("LEVEL");
        levelField = new JTextField(6);
        levelPanel.add(levelLabel);
        levelPanel.add(levelField);
        levelLabel.setFont(new Font("Serif",Font.BOLD,30));
        levelField.setFont(new Font("Serif",Font.BOLD,30));
        idAnLevelPanel.add(levelPanel,BorderLayout.SOUTH);
        contentPane = new LoginBackgroundPanel();
        this.setBackground(Color.darkGray);
        this.setMaximumSize(new Dimension(295,getHeight()));
        contentPane.add(idAnLevelPanel,BorderLayout.CENTER);
        playButton = new JButton("Play");
        playButton.addActionListener(this);
        playButton.setFont(new Font("Serif",Font.BOLD,30));
        playButtonPanel.add(playButton);
        contentPane.add(playButtonPanel,BorderLayout.SOUTH);
        JLabel loginLabel = new JLabel("Login:");
        loginLabelPanel.add(loginLabel);
        loginLabel.setFont(new Font("Serif",Font.BOLD,50));
        contentPane.add(loginLabelPanel,BorderLayout.NORTH);

        add(contentPane);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==playButton) {
            int id = 0, level = 0;
            try{
                id = Integer.parseInt(idTextField.getText());
            }catch (Exception ex){
                ex.printStackTrace();
            }
            try{
                level = Integer.parseInt(levelField.getText());
            }catch (Exception ex){
                ex.printStackTrace();
            }
            Game game = new Game(id,level);
            Thread gameThread = new Thread(game);
            gameThread.start();
            gamePanel = new GamePanel(game.getData());
            gameFrame.remove(this); // Remove this panel.
            this.remove(contentPane); // Remove content pane.
            gameFrame.remove(gameFrame.container); // Remove box layout.
            gameFrame.add(gamePanel);
            gameFrame.validate();
            gameFrame.repaint();
            //button.setEnabled(false);
            //textField.setEditable(false);
        }
    }

    private class LoginBackgroundPanel extends JPanel{
        GameResources backgroundsRes;
        public LoginBackgroundPanel(){
            backgroundsRes = new GameResources();
            this.setLayout(new BorderLayout());
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2D = (Graphics2D) g;
            g2D.drawImage(backgroundsRes.getLoginBackground(), 0, 0,getWidth(),getHeight(), null); // Background
        }
    }

}
