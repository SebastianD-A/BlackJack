import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.util.InputMismatchException;

public class Project {
    public static void main(String[] args) {
        int roundCount = 1;
        boolean isRunning = true;
        Dealer dealer = new Dealer();
        Game casino = new Game("Casino");
        ArrayList<String> userRecord = new ArrayList<>();
        CardCounter cardCount;

        System.out.println("""
                Welcome to the Sebastian Special Black jack casino!
                This version of blackjack allows you to Double Down your bet mid hand and go all in!!
                Careful tho since doubling down or going all in would mean that you get dealt one more card
                To get you started, we'll give you $1000 on the house!! (spend it wisely)
                Heres some basic info:
                 The initial bet for each hand is $150
                 You will win 1.10x the amount of money in the pot
                 If you can't afford the minimum for each hand, you lose!!
                 """);
        System.out.println("Hi my name is "+dealer.getName()+", Before we begin, whats your name?");
        Player user = new Player(In.nextLine());
        while (isRunning){
            String userChoice = "";
            boolean userTurn = true;
            boolean userLose=false;
            //Game Start
            System.out.println("Round " + roundCount);
            casino.clearPot();
            casino.addPot(150);
            //start game
            user.start();
            dealer.start();
            //initialize card counter here to ensure the arrayList is filled
            cardCount = new CardCounter(user, dealer);

            while (userTurn){
            System.out.println("_______________________");
            System.out.println("Total pot: $" + casino.getPot());
            System.out.println(dealer);
            System.out.println();
            System.out.println(user);
            System.out.println(cardCount);
            System.out.println("Balance: "+user.getBalance());
            System.out.println("_______________________");
            System.out.println("""
                    Options (enter the number):
                    1. Hit
                    2. Fold
                    3. Double Down
                    4. All in
                    5. Stay
                    Enter Here:""");

            userChoice = In.nextLine();

                if (userChoice.equals("1")) {
                    user.hit();
                    if (user.checkOver21()) {
                        System.out.println(user);
                        System.out.println("You went over 21!");
                        user.fold();
                        userLose = true;
                        userTurn = false;
                    }
                } 
                else if (userChoice.equals("2")) {
                    user.fold();
                    userLose = true;
                    userTurn = false;
                } 
                else if (userChoice.equals("3")) {
                    double userDoubleDown = user.doubleDown();
                    casino.addPot(userDoubleDown);
                    System.out.println("You have chosen to DoubleDown by $" + userDoubleDown);
                    if (user.getBalance()==0){
                        System.out.println("You basically went all in!!");
                        userTurn=false;
                    }
                    if (user.checkOver21()) {
                        System.out.println(user);
                        System.out.println("You went over 21!");
                        user.fold();
                        System.out.println("You lost $ "+casino.getPot());
                        userLose = true;
                        userTurn = false;
                    }
                } 
                else if (userChoice.equals("4")) {
                    casino.addPot(user.getBalance());
                    user.allIn();
                    System.out.println("ALL IN!!!!");
                    user.hit();
                    System.out.println(user);
                    if (user.checkOver21()){
                        userLose=true;
                    }
                    userTurn = false;
                } 
                else if (userChoice.equals("5")) {
                    System.out.println("You have chosen to stay");
                    userTurn = false;
                } 
                else {
                    System.out.println("Invalid input, please try again\n");
                }
            }
            //decide the outcome of the game if the player didnt lose already
            if (userTurn==false && userLose==false){
                System.out.println("____________________________________________");
                dealer.play();
                System.out.println(dealer.getName()+" (Dealer) hand: "+dealer.getHand()+"\n Hand Total: "+dealer.getHandTotal());
                System.out.println(user);

                if (dealer.checkOver21()){
                    System.out.println("Dealer went over 21!! You Won!! Good Job");
                    dealer.addLoss();
                    user.win(casino.getPot()*1.1);
                }
                else{
                    if (user.getHandTotal()>dealer.getHandTotal()){
                        System.out.println("You beat the dealer's hand!! Good job!!");
                        dealer.addLoss();
                        user.win(casino.getPot()*1.1);
                    }

                    else if (user.getHandTotal()<dealer.getHandTotal()){
                        System.out.println("You lost to the dealer's hand");
                        System.out.println("You could've won $"+(casino.getPot()*1.1)+" but oh well, nice try");
                    }
                    else {
                        user.win(casino.getPot());
                        System.out.println("Its a tie, you will get your the pot back");
                        }
                    }
                }
            if (userLose) {
                userRecord.add("Loss");
            }
            else{
                userRecord.add("Win");
            }                
            roundCount+=1;
            //check first if the user can even go to a next round
            if (user.getBalance()<=149){
                System.out.println("You can't afford the minimum buy in for each hand, sadly you lose :<");
                isRunning=false;
                break;
            }
            boolean validAnswer = false;
            while (!validAnswer){
                System.out.println("_______________________________________");
                System.out.println("""
                    Would you like to do another round? (enter number only, no other characters)
                    1. Yes
                    2. No
                    Enter Here:""");
                    try {
                        int userContinue = In.nextInt();
                        if (userContinue==1){
                            validAnswer=true;
                        }
                        else if (userContinue==2) {
                            validAnswer=true;
                            isRunning=false;
                        }
                        else{
                            System.out.println("Invalid input, Please pick 1 or 2");
                        }
                    } 
                    catch (InputMismatchException e) {
                        System.out.println("Invalid input, Please pick 1 or 2");
                        In.nextLine();
                    }
            }
        }
        System.out.println("Thank you for playing");
        System.out.println("Here is your record for this session" + userRecord);
        int winCount=0;
        int lossCount=0;
        for (String i:userRecord){
            if (i.equals("Win")){
                winCount+=1;
            }
            else{
                lossCount+=1;
            }
        }
        System.out.println("Wins: "+winCount+"\nLoss: "+ lossCount);
    }
}

class Game{
    
    protected ArrayList<String> hand;
    private String[] cardOptions; 
    private HashMap<String, Integer> cardValues;
    protected int handTotal;
    protected int aceInHand;
    private double totalPot;
    protected String role;
    public Random random;

    Game(String role){
        this.hand = new ArrayList<>();
        this.cardOptions = new String[13];
        this.cardValues = new HashMap<String, Integer>();
        this.random = new Random();
        this.role=role;
        this.totalPot = 0;
        cardSetup();
    }

    private void cardSetup(){
        //used to create fill the cardOptions array like this [2, 3, 4, 5, 6, 7, 8, 9, 10, Jack, Queen, King, Ace]
        cardOptions[0] = "2";
        cardOptions[1] = "3";
        cardOptions[2] = "4";
        cardOptions[3] = "5";
        cardOptions[4] = "6";
        cardOptions[5] = "7";
        cardOptions[6] = "8";
        cardOptions[7] = "9";
        cardOptions[8] = "10";
        cardOptions[9] = "Jack";
        cardOptions[10] = "Queen";
        cardOptions[11] = "King";
        cardOptions[12] = "Ace";

        //fill the hashmap that assigns values
            for (int i=2; i<11;i++){
                cardValues.put(cardOptions[i-2], i);
            }
            cardValues.put(cardOptions[9], 10);
            cardValues.put(cardOptions[10], 10);
            cardValues.put(cardOptions[11], 10);
            cardValues.put(cardOptions[12], 11);
    
    }
    public ArrayList<String> getHand(){
        return this.hand;
    }
    public void addPot(double bet){
        this.totalPot+=bet;
    }
    public double getPot(){
        return this.totalPot;
    }
    public void clearPot(){
        this.totalPot=0;
    }
    public double getHandTotal(){
        return this.handTotal;
    }

    public void hit(){
        int randomValue = random.nextInt(13);
        if (cardOptions[randomValue].equals("Ace")){
            this.aceInHand+=1;
        }

        hand.add(cardOptions[randomValue]);
        handTotal+=cardValues.get(cardOptions[randomValue]);

        if (handTotal>21 && aceInHand>=1){
            handTotal-=10; 
            this.aceInHand-=1;
        }
    }
    public boolean checkOver21(){
        if(this.handTotal>21){
            return true;
        }
        else{
            return false;
        }
    }
    public void fold(){
        this.handTotal = 0;
        this.hand.clear();
    }
    public void start(){
        this.hand.clear();
        this.totalPot=0;
        this.handTotal=0;
        this.aceInHand=0;
        hit();
        hit();
    }
    public String toString(){
        return "Total pot: "+this.totalPot;
    }
}

class Player extends Game {
    private double balance;
    private String name;
    Player(String name){
        super("Player");
        this.balance = 1000;
        this.name=name;
    }
    @Override
    public void start(){
        this.balance-=150;
        if (this.balance<0){
            this.balance=0;
        }
        super.start();
    }
    //money theme methods
    public double getBalance(){
        return this.balance;
    }
    public String getName(){
        return this.name;
    }
    public double doubleDown(){
    double userBet=0;
        do{
            try{
                System.out.println("Please enter your bet:");
                userBet = In.nextDouble();
                if (userBet<1){
                    System.out.println("Your bet has to be bigger than $1");
                }
                else if (this.getBalance()-userBet<0){
                    System.out.println("Your bet exceeds your balance, try again");
                }
            }
            catch(InputMismatchException e){
                System.out.println("Invalid Input, please try again");
                In.nextLine();
            }
        } while (userBet<1 || this.getBalance()-userBet<0);
        super.hit();
        this.balance-=userBet;
        return userBet;
    }

    public double allIn(){
        double amount = this.balance;
        this.balance = 0;
        return amount;
    }

    public void win(double amount){
        this.balance+=(amount);
        System.out.println("$"+amount+" was added to your balance");
    }
    @Override
    public String toString(){
        return this.name+" ("+this.role+") "+"hand: "+ this.hand+"\n Hand total: " + this.handTotal;
    }
}

class Dealer extends Game{
    String[] possibleNames;
    int dealerLossCount;
    private String name;
    Dealer(){
        super("Dealer");
        this.name="Bob";
        this.possibleNames=new String[]{"Bob","Max", "Richie", "Pewaldo", "Darvell","Gewaldo", "Toaster","Panasonic", "RichMillion", "Joe", "Sonic"};
    }
    @Override
    //ensure dealer does not immediately get black jack
    public void start() {
        if (dealerLossCount>3){
            this.name=possibleNames[random.nextInt(this.possibleNames.length)];
            dealerLossCount=0;
            System.out.println("Sorry about this but your previous dealer was fired for losing too much money");
            System.out.println("But its okay, I'm your new dealer! My name is "+this.name);
        }
        super.start();
        while (handTotal==21){
            this.hand.clear();
            super.start();
        }
    }
    public void play(){
        while (this.handTotal<=17){
            super.hit();
    }
    }
    public String getFirstCard(){
        return this.hand.get(0);
    }
    public String getName(){
        return this.name;
    }
    public void addLoss(){
        this.dealerLossCount+=1;
    }
    @Override
    public String toString(){
        return this.name+" (Dealer) Hand: [" + this.getFirstCard()+", X]";
    }
}


class CardCounter{
    private int counter;
    private ArrayList<String> playerHand;
    private ArrayList<String> unifiedHand;
    
    
    CardCounter(Player player, Dealer dealer){
        this.counter = 0;
        this.unifiedHand = new ArrayList<>();
        this.playerHand = player.getHand();
        for (String a:this.playerHand){
            unifiedHand.add(a);
        }
        unifiedHand.add(dealer.getFirstCard());
    }
    private int cardCountingValue(String card){
        switch (card) {
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            return 1;

            case "7":
            case "8":
            case "9":
            return 0;

            case "10":
            case "Jack":
            case "Queen":
            case "King":
            case "Ace":
            return (-1);
        
            default:
                return 0;
        }
    }
    private void count(){
        counter = 0;
        for (String card: unifiedHand){
            counter+=cardCountingValue(card);
        }
    }
    public String toString(){
        this.count();
        return "Hi-Lo Card count: "+this.counter;
    }
}