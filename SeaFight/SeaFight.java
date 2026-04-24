package JavaProjects.SeaFight;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Scanner;

public class SeaFight {
    private ArrayList<ArrayList<String>> ships= new ArrayList<>();
    private int[][] locationCells = {};
    private int guessesCount = 0;
    
    private void generateFieldAndShips() {
        int fuildSize = 9;
        int[][] field = new int[fuildSize][fuildSize];
        int[] shipsSizes = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
        boolean success = false;
        SecureRandom rand = new SecureRandom();
        
        while (!success) {
            clearField(field);
            success = true;
            ships.clear();
            
            for (int shipLen : shipsSizes) {
                ArrayList<String> placedShips = new ArrayList<>();
                
                boolean placed = false;
                for (int attempt = 0; attempt < 30; attempt++) {
                    
                    int direction = rand.nextInt(2);
                    int row, col;

                    if (direction == 0) {
                        row = rand.nextInt(fuildSize);
                        col = rand.nextInt(fuildSize - shipLen + 1);
                    } 
                    else {
                        row = rand.nextInt(fuildSize - shipLen + 1);
                        col = rand.nextInt(fuildSize);
                    }

                    
                    if (canPlace(field, row, col, shipLen, direction)) {
                        for (int i = 0; i < shipLen; i++) {
                            if (direction == 0) {
                                field[row][col + i] = 1;
                                placedShips.add(Integer.toString(col+i)+Integer.toString(row));
                            } else {
                                field[row + i][col] = 1;
                                placedShips.add(Integer.toString(col)+Integer.toString(row+i));
                            }
                        }
                        
                        placed = true;
                        break;
                    }
                    
                }
                if (!placed) {
                    success = false;
                    break;
                }
                ships.add(placedShips);
            }
        }      
        /*for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                System.out.print(field[i][j]);
            }
            System.out.println();
        }*/
        locationCells = field;
        System.out.println();
    }

    private boolean canPlace(int[][] field, int row, int col, int shipLen, int direction) {
        int size = field.length;


        if (direction == 0 && col + shipLen > size) {
            return false;
        }
        if (direction == 1 && row + shipLen > size) {
            return false;
        }


        for (int i = -1; i <= shipLen; i++) {
            for (int j = -1; j <= 1; j++) {
                int r, c;
                if (direction == 0) {
                    r = row + j;
                    c = col + i;
                } else {
                    r = row + i;
                    c = col + j;
                }


                if (r < 0 || r >= size || c < 0 || c >= size) {
                    continue;
                }
                if (i >= 0 && i < shipLen && j == 0) {
                    if (field[r][c] != 0) {
                        return false;
                    }
                } 
                else {
                    if (field[r][c] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void printField() {
        for (int i = 0; i < locationCells.length; i++) {
            for (int j = 0; j < locationCells[i].length; j++) {
                if (locationCells[i][j] == 1) {
                System.out.print("0");
                continue;
                }
                else if (locationCells[i][j] == 2) {
                    System.out.print("X");
                    continue;
                }
                else if (locationCells[i][j] == -1) {
                    System.out.print("#");
                    continue;
                }
                System.out.print(locationCells[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    private void clearField(int[][] field) {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                field[i][j] = 0;
            }
        }
    }

    private boolean hasTargets() {
        for (int i = 0; i < locationCells.length; i++) {
            for (int j = 0; j < locationCells[i].length; j++) {
                if (locationCells[i][j] == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private String checkYourself(String userGuess) {
        try {
            char checkedLetter = Character.toLowerCase(userGuess.charAt(0));
            char checkedNum = userGuess.charAt(1);

            String letters = "abcdefghi";
            String nums = "123456789";

            int parsedNumIndex = 0;
            int parsedLetterIndex = 0;

            if ((checkedNum >= '1' && checkedNum <= '9') && (checkedLetter >= 'a' && checkedLetter <= 'i')) {
                for (int i = 0; i < nums.length(); i++) {
                    if (letters.charAt(i) == checkedLetter) {
                        parsedLetterIndex = i;
                    
                    }
                    if (nums.charAt(i) == checkedNum) {
                        parsedNumIndex = i;
                    }
                }
            }

            
            for (int shipIndex = 0; shipIndex < ships.size(); shipIndex++) {
                String coords = Integer.toString(parsedLetterIndex) + Integer.toString(parsedNumIndex);
                if (locationCells[parsedNumIndex][parsedLetterIndex] == 1 && ships.get(shipIndex).contains(coords)) {
                    guessesCount++;
                    locationCells[parsedNumIndex][parsedLetterIndex] = 2;
                    ships.get(shipIndex).remove(coords);
                    if (ships.get(shipIndex).isEmpty()) {
                        return "Потопил";
                    }
                    else {
                        return "Попал";
                    }
                }
                else if (locationCells[parsedNumIndex][parsedLetterIndex] == 2) {
                    return "Вы уже сделали такой ход!";
                }
            }
            locationCells[parsedNumIndex][parsedLetterIndex] = -1;
            guessesCount++;
            return "Мимо"; 
        }
        catch (StringIndexOutOfBoundsException exception) {
            return "Вы неправильно ввели координаты, повторите попытку!";
        }
    }

    private void getResult() {
        System.out.println("Игра завершена!");
        System.out.println("Количество ходов: " + guessesCount);
    }

    public void startGame() {
        SeaFight sea = new SeaFight();
        Scanner input = new Scanner(System.in);
        
        sea.generateFieldAndShips();
        while (sea.hasTargets()) {
            sea.printField();
            System.out.print("Сделайте ход:");
            String inputText = input.nextLine();
            System.out.println(sea.checkYourself(inputText));
        }
        sea.printField();
        sea.getResult();
    }
}