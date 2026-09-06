import java.util.*;    

// Custom Exception handling for invalidinputs
class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}

interface PlayerStatistics {
    void addRuns(int runs);
    void faceBall();
    void gotOut();
}
 
// Player class to keep track of individual player scores for player status
class Player implements PlayerStatistics {
    String name;
    int runs;
    int ballsFaced;
    boolean out;
    public Player(String name) {
        this.name = name;
        this.runs = 0;
        this.ballsFaced = 0;
        this.out = false;
    }
    public void addRuns(int runs) {
        this.runs += runs;
    }
    public void faceBall() {
        this.ballsFaced++;
    }
    public void gotOut() {
        this.out = true;
    }
    public String toString() {
        return name + " - Runs: " + runs + ", Balls: " + ballsFaced + ", Out: " + (out ? "Yes" : "No");
    }  
}

// Abstract class for the basic structure of a team
abstract class Team {
    public String name;
    public int totalScore;
    public int wickets;
    public int oversPlayed;
    public int ballsPlayed;
    public int total4s;
    public int total6s;
    public int wideBalls;
    public int noBalls;
    public int lastball;
    public int totalBalls;
    public Team(String name) {
        this.name = name;
        this.totalScore = 0;
        this.wickets = 0;
        this.oversPlayed = 0;
        this.ballsPlayed = 0;
        this.total4s = 0;
        this.total6s = 0;
        this.wideBalls = 0;
        this.noBalls = 0;
        this.lastball = 0;
        this.totalBalls = 0;
    }
    // Abstract method to be implemented by childclasses
    abstract void addRuns(int runs, boolean isWide, boolean isNoBall, boolean isWicket);
}

class CricketTeam extends Team {
    ArrayList<Player> players;
    int currentPlayerIndex;

    public CricketTeam(String name, int numPlayers) {
        super(name);
        this.players = new ArrayList<>();
        for (int i = 1; i <= numPlayers; i++) {
            this.players.add(new Player("Player " + i));
        }
        this.currentPlayerIndex = 0; // First player starts
    }

    void addRuns(int runs, boolean isWide, boolean isNoBall, boolean isWicket) {
        if (isWide) {
            this.totalScore += 1; // add 1 run 
            this.wideBalls++;
            this.totalBalls++;
            return; // Wide ball doesn't count as a valid delivery
        }

        if (isNoBall) {
            this.totalScore += 1; // No ball adds 1 run
            this.noBalls++;
            this.totalBalls++;
            return; // No ball doesn't count asvalid delivery
        }

        if (isWicket) {
            this.players.get(currentPlayerIndex).gotOut();
            this.wickets++;
            this.currentPlayerIndex++; // Move to next player
        } 
        else {
            this.players.get(currentPlayerIndex).addRuns(runs);
            this.totalScore += runs;
            if (runs == 4) total4s++;
            if (runs == 6) total6s++;
        }

        this.players.get(currentPlayerIndex).faceBall();
        this.ballsPlayed++;
        if (this.ballsPlayed % 6 == 0) {
            this.oversPlayed++; // Over is completed after 6 balls
        }
    }
    public void displayPlayerScores() {
        for (Player player : players) {
            System.out.println(player);
        }
    }
}

public class Cricket_Match {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try{
            System.out.println("Enter type of match (ODI/T20/or type 'CUSTOM' for custom overs): ");
            String matchType = sc.nextLine();
            int overs = 0;
            switch (matchType.toLowerCase()) {
                case "odi":
                    overs = 50;
                    break;
                case "t20":
                    overs = 20;
                    break;
                case "custom":
                    System.out.println("Enter number of overs: ");
                    overs = sc.nextInt();
                    break;
                default:
                    System.out.println("Invalid Input");
                    return;
            }
            System.out.println("Enter the name of Team 1: ");
            String team1Name = sc.next();
            CricketTeam team1 = new CricketTeam(team1Name, 11);
        
            System.out.println("Enter the name of Team 2: ");
            String team2Name = sc.next();
            CricketTeam team2 = new CricketTeam(team2Name, 11);

            System.out.println("Number of overs are: " + overs);
        
            // System.out.println("Enter number of overs: ");
            // int overs = sc.nextInt();
        
            System.out.println(team1Name + " starts batting.");
            for (int i = 1; i <= overs; i++) {
                System.out.println("Over Number: " + i);
                team1.totalBalls=6;
                for (int j = 1; j <= team1.totalBalls; j++) {
                    System.out.println("Enter runs of ball " + j + " (or write ('wicket') for wicket, ('wide') for wide, ('noball') for no-ball): ");
                    String result = sc.next();
            
                    boolean isWide = result.equals("wide");
                    boolean isNoBall = result.equals("noball");
                    boolean isWicket = result.equals("wicket");
                    int runs = 0;
            
                    if (!isWide && !isNoBall && !isWicket) {
                        try {
                            runs = Integer.parseInt(result);
                            if (runs < 0 || runs >= 7) {
                                throw new InvalidInputException("Runs cannot be negative or greater then equal to 7 is not possible");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input! Please enter a valid number of runs or a valid keyword (wicket, wide, noball)");
                            j--; // Repeat the input for the same ball
                            continue; // Skip the rest of the loop and prompt again
                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                            j--;
                            continue;
                        }
                    }
            
                    team1.addRuns(runs, isWide, isNoBall, isWicket);
                    if (team1.wickets == 10) {
                        System.out.println(team1Name + " is All-Out");
                        break;
                    }     
                    //delcare ball last number
                    if (j>=6){
                        team1.lastball=0;
                    }
                    else{
                        team1.lastball=j;
                    }
                }
                System.out.println("--------Team Scored--------");
                System.out.println(team1Name + " scored " + team1.totalScore + "/" + team1.wickets + " in " + team1.oversPlayed + "." + team1.lastball + " overs.");
                System.out.println("Average Runs per Over: " + ((float)team1.totalScore / (team1.oversPlayed)));
                System.out.println("Fours: " + team1.total4s + " | Sixes: " + team1.total6s + 
                                " | Wide Balls: " + team1.wideBalls + " | No Balls: " + team1.noBalls);
            }
            System.out.println("--------"+team1Name+" Team Player Status--------");
            team1.displayPlayerScores();
        
            //Team 2 innings
            System.out.println("");
            System.out.println(team2Name + " starts batting.");
            for (int i = 1; i <= overs; i++){
                System.out.println("Over Number: " + i);
                team2.totalBalls=6;
                for (int j = 1; j <= team2.totalBalls; j++) {
                    System.out.println("Enter runs of ball " + j + " (or write ('wicket') for wicket, ('wide') for wide, ('noball') for no-ball): ");
                    String result = sc.next().toLowerCase();
            
                    boolean isWide = result.equals("wide");
                    boolean isNoBall = result.equals("noball");
                    boolean isWicket = result.equals("wicket");
                    int runs = 0;
            
                    if (!isWide && !isNoBall && !isWicket) {
                        try {
                            runs = Integer.parseInt(result);
                            if (runs < 0 || runs >= 7) {
                                throw new InvalidInputException("(*_*)Runs cannot be negative or greater then equal to 7 is not possible(*_*)");  
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("(*_*)Invalid input! Please enter a valid number of runs or a valid keyword (wicket, wide, noball).(*_*)");
                            j--; // Repeat the input for the same ball
                            continue; // Skip the rest of the loop and prompt again
                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                            j--;
                            continue;
                        }
                    }
            
                    team2.addRuns(runs, isWide, isNoBall, isWicket);
                    if (team2.wickets == 10) {
                        System.out.println(team2Name + " is All-Out");
                        break;
                    }      
                    //delcare ball last number
                    if (j>=6){
                        team2.lastball=0;
                    }
                    else{
                        team2.lastball=j;
                    }     
                    //this if condition will break (total ball for loop) if second team will score more run then first team
                    if (team2.totalScore > team1.totalScore)
                    {
                        break;
                    }
                }
                //this if condition will break (over for loop) if second team will score more run then first team
                if (team2.totalScore > team1.totalScore)
                {
                    break;
                }
                System.out.println("--------Team Scored--------");
                System.out.println(team2Name + " scored " + team2.totalScore + "/" + team2.wickets + " in " + team2.oversPlayed + "." + team2.lastball + " overs.");
                System.out.println("Average Runs per Over: " + ((float)team2.totalScore / (team2.oversPlayed)));
                System.out.println("Fours: " + team2.total4s + " | Sixes: " + team2.total6s + 
                                " | Wide Balls: " + team2.wideBalls + " | No Balls: " + team2.noBalls);
            }
            System.out.println("--------"+team2Name+" Team Player Status--------");
            team2.displayPlayerScores();
            
            // Delcare the winner
            System.out.println("--------Announcement of winner--------");
            if (team1.totalScore > team2.totalScore) {
                System.out.println(team1Name + " wins!");
            } 
            else if (team1.totalScore < team2.totalScore) {
                System.out.println(team2Name + " wins!");
            } 
            else {
                System.out.println("The match is a tie!");
            }

            System.out.println("--------Final Score of " + matchType + " World Cup--------");
            System.out.println("Team 1:");
            System.out.println("Team: " + team1Name + " | Total Runs: " + team1.totalScore + 
                                " | Wickets: " + team1.wickets + " | Total Over: " +  team1.oversPlayed + "." + team1.lastball);
            System.out.println("Fours: " + team1.total4s + " | Sixes: " + team1.total6s + 
                                " | Wide Balls: " + team1.wideBalls + " | No Balls: " + team1.noBalls);
            System.out.println("Team 2:");
            System.out.println("Team: " + team2Name + " | Total Runs: " + team2.totalScore + 
                                " | Wickets: " + team2.wickets + " | Total Over: " + team2.oversPlayed + "." + team2.lastball);
            System.out.println("Fours: " + team2.total4s + " | Sixes: " + team2.total6s + 
                                " | Wide Balls: " + team2.wideBalls + " | No Balls: " + team2.noBalls);
        }
        catch (InputMismatchException e) {
            System.out.println("Invalid input! Please enter the correct type.");
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }
}
