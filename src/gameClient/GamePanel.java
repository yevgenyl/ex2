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

/**
 * This class is responsible for showing a visual form of the game graph (map) with all it's objects (pokemons, agents..).
 */

public class GamePanel extends JPanel implements ActionListener{

    GameData data; // The data if the game.
    GameResources resources; // The game resources class (holds images).
    ArrayList<Image> pokemonsRes, agentsRes, backgroundsRes, nodesRes;
    long seed; // desired seed for Random class usages.
    double minX, minY, maxX, maxY; // Min/Max values of the geo location which represents the graph nodes.
    int widthMargin , heightMargin; // The desired width/height margins.
    Timer timer; // A timer used to display this panel at update rate of 60 milliseconds.

    /**
     * Initialization constructor based on the game data (graph, agents, pokemons.. etc).
     * @param data - the data of the current scenario.
     */
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
     * @param data - the data of the current scenario.
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

    /**
     * Override of the default painComponent method.
     * @param g - the graphics object.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Random random = new Random();
        random.setSeed(seed); // Used for generating random background.
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(backgroundsRes.get(random.nextInt(backgroundsRes.size())), 0, 0,getWidth(),getHeight(), null); // Draw background.
        drawGraph(g); // Draws the game graph.
        drawPokemons(g); // Draws the game pokemons.
        drawAgents(g); // Draws the game agents.
        drawTimer(g); // Draws the game Timer.
        drawScore(g); // Draws the game current score.
        //drawMoves(g); // Draws the game current number of moves.
    }

    /**
     * Draws the graph based on the game data.
     * @param g the graphics object.
     */
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
    /**
     *
     * Draws a single node in a graph based on the game data.
     * @param g the graphics object.
     */
    private void drawNode(Graphics g, node_data n){
        geo_location oldLocation = n.getLocation();
        double x = scale(oldLocation.x(),minX,maxX,10,getWidth()-30);
        double y = scale(oldLocation.y(),minY,maxY,10,getHeight()-70);
        g.drawImage(nodesRes.get(0),(int)(x-10),(int) (Math.abs(y-(getHeight()-30-20))),40,40,null);
    }

    /**
     * Draws a single edge in a graph based on the game data.
     * @param g the graphics object.
     */
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

    /**
     * Draws the game pokemons based on the game data.
     * @param g the graphics object.
     */
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
            //g.setColor(Color.BLUE);
            //g.setFont(new Font("TimesRoman",Font.BOLD,20));
            //g2D.drawString(pokemon.getValue()+"",(int)(x),(int)(Math.abs(y-(getHeight()-30-20))));
        }
    }

    /**
     * Draws the game agents based on the game data.
     * @param g the graphics object.
     */
    private void drawAgents(Graphics g){
        ArrayList<GameAgent> agents = data.getAgents();
        for(GameAgent agent : agents){
            geo_location location = agent.getLocation();
            double x = scale(location.x(),minX,maxX,10,getWidth()-30);
            double y = scale(location.y(),minY,maxY,10,getHeight()-70);
            g.setColor(Color.GREEN);
            //g.fillOval((int)(x),(int)(Math.abs(y-(getHeight()-30))),10,10);
            g.drawImage(resources.getAgentResources().get(0),(int)(x-10),(int)(Math.abs(y-(getHeight()-50))),50,50,null);
            g.setColor(Color.red);
            g.setFont(new Font("TimesRoman",Font.BOLD,20));
            g.drawString(agent.getValue()+"",(int)(x-10),(int)(Math.abs(y-(getHeight()-50))));
        }
    }

    /**
     * Draws the game timer based on the game data.
     * @param g the graphics object.
     */
    private void drawTimer(Graphics g){
        g.setColor(Color.BLACK);
        //g.setFont(new Font("TimesRoman",Font.BOLD,((getWidth()*getHeight())/25000)));
        g.setFont(new Font("TimesRoman",Font.BOLD,25));
        long miliseconds = data.getServer().timeToEnd();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(miliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(miliseconds);
        String time = seconds < 10 ? "00:0"+seconds : "00:"+seconds;
        g.drawString("TIME: "+time, getWidth()-(g.getFont().getSize()*6),getHeight()/17);
    }

    /**
     * Draws the game score based on the game data.
     * @param g the graphics object.
     */
    private void drawScore(Graphics g){
        g.setColor(Color.black);
        g.setFont(new Font("TimesRoman",Font.BOLD,25));
        g.drawString("SCORE: "+data.getScore(), getWidth()-(g.getFont().getSize()*13),getHeight()/17);
    }

    /**
     * Draws the game moves based on the game data.
     * @param g the graphics object.
     */
    private void drawMoves(Graphics g){
        g.setColor(Color.black);
        g.setFont(new Font("TimesRoman",Font.BOLD,25));
        g.drawString("MOVES: "+data.getMoves(), getWidth()-(g.getFont().getSize()*13),getHeight()/9);
    }

    /**
     *
     * @param data denotes some data to be scaled
     * @param r_min the minimum of the range of your data
     * @param r_max the maximum of the range of your data
     * @param t_min the minimum of the range of your desired target scaling
     * @param t_max the maximum of the range of your desired target scaling
     * @return the new parameter x or y after scaling.
     */
    private double scale(double data, double r_min, double r_max,
                         double t_min, double t_max)
    {
        double res = ((data - r_min) / (r_max-r_min)) * (t_max - t_min) + t_min;
        return res;
    }

    /**
     * Returns the minimum x (geo location x) value compared to all nodes.
     * @return the minimum x value.
     */
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

    /**
     * Returns the minimum y (geo location y) value compared to all nodes.
     * @return the minimum y value of all nodes.
     */
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

    /**
     * Returns the maximum x (geo location x) value compared to all nodes.
     * @return the maximum x value of all nodes.
     */
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

    /**
     * Returns the maximum y (geo location y) value compared to all nodes.
     * @return the maximum y value of all nodes.
     */
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

    /**
     * Override of the actionPerforms method.
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        update(data); // Repaints the screen (each 60 milliseconds).
    }

}
