import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Year;
import java.util.InputMismatchException;
import java.util.Scanner;

class FootballManager {

    public static String line() {
        return new Scanner(System.in).nextLine();
    }

    public static void println(String arg) {
        System.out.println(arg);
    }

    public static String collectTeams() {
        File folder = new File(".\\project");
        String[] folderList = folder.list();
        String result = "";
        if (folderList != null) {
            for (int i = 0; i < folderList.length; i++) {
                result = result.concat((i > 0 ? "\n" : "") + folderList[i]);
            }
        }
        return result;
    }

    public static void newTeam() {
        println("Enter the name of new team: ");
        String newTeam = line();
        boolean mkdirs = new File("project\\" + newTeam).mkdirs();
        File infoPath = new File("project\\" + newTeam + "\\info.txt");
        try {
            FileWriter info = new FileWriter(infoPath);
            if (mkdirs) {
                println("City: ");
                info.write(line() + "\n");
                println("Country: ");
                info.write(line() + "\n");
                println("Balance(in $): ");
                info.write(line() + "\n");
                println("Transfer fee(in percent from 0 to 10): ");
                info.write(line() + "\n");
                println("Team created successfully!");
                info.close();
            } else {
                println("Team already exists!");
            }
        } catch (IOException e) {
            println("Some error happened.");
        }
    }

    public static void newPlayer() {
        println("Enter the name of new player: ");
        String newPlayer = line();
        println("Choose the player team:\n" + collectTeams());
        String playerTeam = line();
        while (!collectTeams().contains(playerTeam) || playerTeam.equals("")) {
            println("This team does not exist. Try again.");
            playerTeam = line();
        }
        File playerPath = new File("project\\" + playerTeam + "\\" + newPlayer + ".txt");
        try {
            FileWriter player = new FileWriter(playerPath);
            println("Career start year: ");
            player.write(line() + " ");
            println("Age: ");
            player.write(line() + " ");
            println("Player added successfully!");
            player.close();
        } catch (IOException e) {
            println("Some error happened.");
        }
    }

    public static boolean deleteFolder(File teamPath) {
        File[] allContents = teamPath.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteFolder(file);
            }
        }
        return teamPath.delete();
    }

    public static void transfer(String team, int startYear, int age,
                                File teamPath, String player) throws IOException {
        println(collectTeams() + "\nWhich team will receive this player?");
        String receiverTeam = line();
        while (!collectTeams().contains(receiverTeam) || receiverTeam.equals("")) {
            println("This team does not exist. Try again.");
            receiverTeam = line();
        }

        Scanner receiver = new Scanner(new File("project\\" + receiverTeam + "\\info.txt"));
        String receiverCity = receiver.nextLine();
        String receiverCountry = receiver.nextLine();
        int receiverBalance = receiver.nextInt();
        int receiverFee = receiver.nextInt();

        Scanner seller = new Scanner(new File("project\\" + team + "\\info.txt"));
        String sellerCity = seller.nextLine();
        String sellerCountry = seller.nextLine();
        int sellerBalance = seller.nextInt();
        int sellerFee = seller.nextInt();

        int formula = ((Year.now().getValue() - startYear) * 12 * 100000 / age);
        int fullPrice = formula + (formula * sellerFee / 100);
        println("Transfer price: " + fullPrice + "$");
        if (receiverBalance >= fullPrice) {
            println("The receiver's team balance is " + receiverBalance + "$." + " Are you sure? y/n");
            if (line().equals("y")) {
                try {
                    receiverBalance -= fullPrice;
                    sellerBalance += fullPrice;

                    FileWriter rWrite = new FileWriter("project\\" + receiverTeam + "\\info.txt");
                    rWrite.write(receiverCity + "\n" + receiverCountry + "\n"
                            + receiverBalance + "\n" + receiverFee + "\n");
                    rWrite.close();

                    FileWriter sWrite = new FileWriter(teamPath + "\\info.txt");
                    sWrite.write(sellerCity + "\n" + sellerCountry + "\n"
                            + sellerBalance + "\n" + sellerFee + "\n");
                    sWrite.close();

                    File sellerFile = new File(teamPath + "\\" + player + ".txt");
                    File receiverFile = new File("project\\" + receiverTeam + "\\" + player + ".txt");
                    Files.copy(sellerFile.toPath(), receiverFile.toPath());
                    println(sellerFile.delete() ? "seller file deleted"
                            : "seller file not deleted");
                    println("Transfer successful!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            println("The receiver's team doesn't have enough money.");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            int input = 0;
            println("1. Show the teams or players" +
                    "\n2. Create a new team or player" +
                    "\n3. Delete a player or a team" +
                    "\n0. Exit");
            try {
                input = scanner.nextInt();
            } catch (InputMismatchException e) {
                println("Incorrect input.");
            }
            if (input == 1) {
                println("All available teams:\n" + collectTeams() + "\nShow players? y/n");
                if (line().equals("y")) {
                    println("Which team?");
                    String team = line();
                    while (!collectTeams().contains(team) || team.equals("")) {
                        println("This team does not exist. Try again.");
                        team = line();
                    }
                    File teamPath = new File("project\\" + team);
                    Scanner teamScan = null;
                    try {
                        teamScan = new Scanner(new File(teamPath + "\\info.txt"));
                    } catch (FileNotFoundException e) {
                        println("Caught some error.");
                    }
                    if (teamScan != null) {
                        println("City: " + teamScan.next() +
                                "\nCountry: " + teamScan.next() +
                                "\nBalance: " + teamScan.nextInt() + "$" +
                                "\nTransfer fee: " + teamScan.nextInt() + "%\n\nPlayers:");
                    }
                    String[] players = teamPath.list();
                    if (players != null && players.length > 1) {
                        for (String s : players) {
                            if (!s.equals("info.txt")) {
                                println(s.replace(".txt", ""));
                            }
                        }
                        println("Show player's info? y/n");
                        if (line().equals("y")) {
                            println("Which player?");
                            try {
                                String player = line();
                                File playerPath = new File(teamPath + "\\" + player + ".txt");
                                Scanner playerScan = new Scanner(playerPath);
                                int startYear = playerScan.nextInt();
                                int age = playerScan.nextInt();
                                println("Career start year: " + startYear
                                        + "\nAge: " + age
                                        + "\nTransfer this player? y/n");
                                if (line().equals("y")) {
                                    try {
                                        transfer(team, startYear, age, teamPath, player);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        println("Team is empty!");
                    }
                    println("");
                }
            } else if (input == 2) {
                println("1. New team\n2. New player\n0. Back");
                try {
                    input = scanner.nextInt();
                } catch (InputMismatchException e) {
                    println("Incorrect input.");
                    break;
                }
                if (input == 1) {
                    newTeam();
                } else if (input == 2) {
                    newPlayer();
                }
            } else if (input == 3) {
                println(collectTeams() + "\nWhich team?");
                String team = line();
                File teamPath = new File("project\\" + team + "\\");
                println("Delete this team or a player in it? t/p");
                String tp = line();
                if (tp.equals("t")) {
                    println(deleteFolder(teamPath) ? "Team deleted successfully!" : "Failed to delete the team!");
                } else if (tp.equals("p")) {
                    String[] teams = teamPath.list();
                    if (teams != null && teams.length > 1) {
                        println("Which player?");
                        for (String s : teams) {
                            if (!s.equals("info.txt")) {
                                println(s.replace(".txt", ""));
                            }
                        }
                        String player = line();
                        println(new File(teamPath + player + ".txt").delete()
                                ? "Player deleted successfully!"
                                : "Failed to delete the player!");
                    } else {
                        println("Team is empty!");
                    }
                }
            } else if (input != 0) {
                println("Incorrect input.");
            }
            if (input == 0) {
                break;
            }
        }
    }
}