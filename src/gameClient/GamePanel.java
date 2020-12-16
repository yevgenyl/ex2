package gameClient;
import api.node_data;
import api.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GamePanel extends JPanel implements ActionListener{

    GameData data;
    GameResources resources;
    ArrayList<Image> pokemonsRes;
    ArrayList<Image> agentsRes;
    ArrayList<Image> backgroundsRes;
    ArrayList<Image> nodesRes;
    long seed;
    double minX, minY, maxX, maxY;
    int widthMargin , heightMargin;
    Timer timer;

    public GamePanel(){
        super();
        resources = new GameResources();
        pokemonsRes = resources.getPokemonResources();
        agentsRes = resources.getAgentResources();
        backgroundsRes = resources.getBackgroundsResources();
        nodesRes = resources.getNodesResources();
        seed = (long) (Math.random()*30000);
        timer = new Timer(60, this);
        timer.start();
    }

    public GamePanel(GameData data){
        super();
        this.data = data;
        resources = new GameResources();
        pokemonsRes = resources.getPokemonResources();
        agentsRes = resources.getAgentResources();
        backgroundsRes = resources.getBackgroundsResources();
        nodesRes = resources.getNodesResources();
        seed = (long) (Math.random()*30000);
        timer = new Timer(60, this);
        timer.start();
    }

    /**
     * Updates this panel
     * @param data
     */
    public void update(GameData data){
        this.data = data;
        minX = getMinX();
        minY = getMinY();
        maxX = getMaxX();
        maxY = getMaxY();
        widthMargin = getWidth()/5;
        heightMargin = getHeight()/5;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Random random = new Random();
        random.setSeed(seed);
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(backgroundsRes.get(random.nextInt(backgroundsRes.size())), 0, 0,getWidth(),getHeight(), null); // Background
        drawGraph(g);
        drawPokemons(g);
        drawAgents(g);
        drawTimer(g);
        //drawScore(g);
    }

    private void drawGraph(Graphics g){
        for(node_data node : data.getGraph().getV()){
            for(edge_data edge : data.getGraph().getE(node.getKey())){
                drawEdge(g,edge);
            }
        }
        for(node_data node : data.getGraph().getV()){
            drawNode(g,node);
        }
    }

    private void drawNode(Graphics g, node_data n){
        geo_location oldLocation = n.getLocation();
        double x = scale(oldLocation.x(),minX,maxX,10,getWidth()-30);
        double y = scale(oldLocation.y(),minY,maxY,10,getHeight()-70);
        //g.setColor(Color.BLUE);
        //g.fillOval((int)(x),(int)(Math.abs(y-(getHeight()-30))),15,15);
        //g.setColor(Color.MAGENTA);
        //g.drawString(n.toString(),(int)(x),(int)(Math.abs(y-(getHeight()-30))));
        g.drawImage(nodesRes.get(0),(int)(x-10),(int) (Math.abs(y-(getHeight()-30-20))),40,40,null);
    }

    private void drawEdge(Graphics g, edge_data edge){
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(new Color(86,61,45));
        geo_location locationSrc = data.getGraph().getNode(edge.getSrc()).getLocation();
        geo_location locationDest = data.getGraph().getNode(edge.getDest()).getLocation();
        double xSrc = scale(locationSrc.x(),minX,maxX,10,getWidth()-30);
        double ySrc = scale(locationSrc.y(),minY,maxY,10,getHeight()-70);
        double xDest = scale(locationDest.x(),minX,maxX,10,getWidth()-30);
        double yDest = scale(locationDest.y(),minY,maxY,10,getHeight()-70);
        g2d.setStroke(new BasicStroke(10));
        g2d.drawLine((int)(xSrc+5),(int)(Math.abs(ySrc-(getHeight()-30+5))),(int)(xDest+5),(int)(Math.abs(yDest-(getHeight()-30+5))));
    }

    private void drawPokemons(Graphics g){
        Random random = new Random();
        random.setSeed(seed);
        ArrayList<GamePokemon> pokemons = data.getPokemons();
        for(GamePokemon pokemon : pokemons){
            geo_location location = pokemon.getLocation();
            double x = scale(location.x(),minX,maxX,10,getWidth()-30);
            double y = scale(location.y(),minY,maxY,10,getHeight()-70);
            if(pokemon.getType() == 1) {
                g.setColor(Color.orange); // Positive movement.
            }else {
                g.setColor(Color.red); // Negative movement.
            }
            //g.fillOval((int)(x),(int)(Math.abs(y-(getHeight()-30))),10,10);
            Graphics2D g2D = (Graphics2D) g;
            g2D.drawImage(pokemonsRes.get(random.nextInt(pokemonsRes.size())), (int)(x), (int)(Math.abs(y-(getHeight()-30-20))),50,50, null);
        }
    }

    private void drawAgents(Graphics g){
        ArrayList<GameAgent> agents = data.getAgents();
        for(GameAgent agent : agents){
            geo_location location = agent.getLocation();
            double x = scale(location.x(),minX,maxX,10,getWidth()-30);
            double y = scale(location.y(),minY,maxY,10,getHeight()-70);
            g.setColor(Color.GREEN);
            //g.fillOval((int)(x),(int)(Math.abs(y-(getHeight()-30))),10,10);
            g.drawImage(resources.getAgentResources().get(0),(int)(x-10),(int)(Math.abs(y-(getHeight()-50))),50,50,null);
        }
    }

    private void drawTimer(Graphics g){
        g.setColor(Color.BLACK);
        g.setFont(new Font("TimesRoman",Font.BOLD,((getWidth()*getHeight())/25000)));
        long miliseconds = data.getServer().timeToEnd();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(miliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(miliseconds);
        String time = seconds < 10 ? "00:0"+seconds : "00:"+seconds;
        g.drawString(time, getWidth()-(g.getFont().getSize()*4),getHeight()/10);
    }

    private void drawScore(Graphics g){
        int space = widthMargin;
        for(int i = 0; i < data.getAgents().size(); i++) {
            g.drawImage(resources.getPokaball(),getWidth()-space,20,40,40,null);
            space += widthMargin/2.25;
        }
    }

    /**
     *
     * @param data denote some data to be scaled
     * @param r_min the minimum of the range of your data
     * @param r_max the maximum of the range of your data
     * @param t_min the minimum of the range of your desired target scaling
     * @param t_max the maximum of the range of your desired target scaling
     * @return
     */
    private double scale(double data, double r_min, double r_max,
                         double t_min, double t_max)
    {
        double res = ((data - r_min) / (r_max-r_min)) * (t_max - t_min) + t_min;
        return res;
    }

    private double getMinX(){
        directed_weighted_graph graph = data.getGraph();
        int n = graph.nodeSize();
        double minimum = Double.MAX_VALUE;
        for(int i = 0; i < n; i++)
            if(graph.getNode(i).getLocation().x() < minimum) {
                minimum = graph.getNode(i).getLocation().x();
            }
        return minimum;
    }

    private double getMinY(){
        directed_weighted_graph graph = data.getGraph();
        int n = graph.nodeSize();
        double minimum = Double.MAX_VALUE;
        for(int i = 0; i < n; i++)
            if(graph.getNode(i).getLocation().y() < minimum) {
                minimum = graph.getNode(i).getLocation().y();
            }
        return minimum;
    }

    private double getMaxX(){
        directed_weighted_graph graph = data.getGraph();
        int n = graph.nodeSize();
        double maximum = Double.MIN_VALUE;
        for(int i = 0; i < n; i++)
            if(graph.getNode(i).getLocation().x() > maximum) {
                maximum = graph.getNode(i).getLocation().x();
            }
        return maximum;
    }

    private double getMaxY(){
        directed_weighted_graph graph = data.getGraph();
        int n = graph.nodeSize();
        double maximum = Double.MIN_VALUE;
        for(int i = 0; i < n; i++)
            if(graph.getNode(i).getLocation().y() > maximum) {
                maximum = graph.getNode(i).getLocation().y();
            }
        return maximum;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update(data);
    }

}
