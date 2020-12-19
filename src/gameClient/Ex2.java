package gameClient;

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
