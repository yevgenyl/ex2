package gameClient;

/**
 * The main class of this game
 * The main method can deal with two run types:
 * 1) Login - opens the login GUI.
 * 2) Command line (with id and scenario). For example: java -jar Ex2.java 123456789 12
 */
public class Ex2{
    public static void main(String[] args){
        if(args.length == 2){
            int id = 0, scenario = 0;
            try {
                id = Integer.parseInt(args[0]);
                scenario = Integer.parseInt(args[1]);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            GameFrame directGUI = new GameFrame(id,scenario);
        }else if(args.length == 1){
            System.out.println("Wrong number of parameters. You should enter your id followed by scenario number.");
        }else {
            GameFrame loginGUI = new GameFrame();
        }
    }
}
