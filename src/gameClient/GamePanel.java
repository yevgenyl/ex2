package gameClient;
import api.node_data;
import api.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GamePanel extends JPanel {

    GameData data;
    double minX, minY, maxX, maxY;

    public GamePanel(){
        super();
    }

    /**
     * Updates this panel
     * @param data
     */
    public void update(GameData data){
        this.data = data;
        this.setBackground(Color.gray);
        minX = getMinX();
        minY = getMinY();
        maxX = getMaxX();
        maxY = getMaxY();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGraph(g);
        drawPokemons(g);
        drawAgents(g);
    }

    private void drawGraph(Graphics g){
        for(node_data node : data.getGraph().getV()){
            drawNode(g,node);
            for(edge_data edge : data.getGraph().getE(node.getKey())){
                drawEdge(g,edge);
            }
        }
    }

    private void drawNode(Graphics g, node_data n){
        geo_location oldLocation = n.getLocation();
        double x = scale(oldLocation.x(),minX,maxX,10,getWidth()-30);
        double y = scale(oldLocation.y(),minY,maxY,10,getHeight()-70);
        g.setColor(Color.BLUE);
        g.fillOval((int)(x),(int)(Math.abs(y-(getHeight()-30))),15,15);
        g.setColor(Color.WHITE);
        g.drawString(n.toString(),(int)(x),(int)(Math.abs(y-(getHeight()-30))));
    }

    private void drawEdge(Graphics g, edge_data edge){
        g.setColor(Color.BLUE);
        geo_location locationSrc = data.getGraph().getNode(edge.getSrc()).getLocation();
        geo_location locationDest = data.getGraph().getNode(edge.getDest()).getLocation();
        double xSrc = scale(locationSrc.x(),minX,maxX,10,getWidth()-30);
        double ySrc = scale(locationSrc.y(),minY,maxY,10,getHeight()-70);
        double xDest = scale(locationDest.x(),minX,maxX,10,getWidth()-30);
        double yDest = scale(locationDest.y(),minY,maxY,10,getHeight()-70);
        g.drawLine((int)(xSrc+5),(int)(Math.abs(ySrc-(getHeight()-30+5))),(int)(xDest+5),(int)(Math.abs(yDest-(getHeight()-30+5))));
    }

    private void drawPokemons(Graphics g){
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
            g.fillOval((int)(x),(int)(Math.abs(y-(getHeight()-30))),10,10);
        }
    }

    private void drawAgents(Graphics g){
        ArrayList<GameAgent> agents = data.getAgents();
        for(GameAgent agent : agents){
            geo_location location = agent.location;
            double x = scale(location.x(),minX,maxX,10,getWidth()-30);
            double y = scale(location.y(),minY,maxY,10,getHeight()-70);
            g.setColor(Color.GREEN);
            g.fillOval((int)(x),(int)(Math.abs(y-(getHeight()-30))),10,10);
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

}
