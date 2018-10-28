import java.util.*;
import java.lang.*;
import java.io.*; 
public class HangmanGame {

    ArrayList<String> words = new ArrayList<>(); 
    ArrayList<String> guesses; 
    ArrayList<String> wrong_guesses;
    StringBuilder current; 
    String answer; 
    int guess_count; 
    HashMap<String, Integer> alpha = new HashMap<>(); 


    HangmanGame() {
        words = generateList(); 
        answer = generateWord().toLowerCase();
        current = generateBlank(); 
        wrong_guesses = new ArrayList<>(); 
        guess_count = 3; 
        guesses = new ArrayList<>(); 
        for (char ch = 'a'; ch <= 'z'; ++ch) 
            alpha.put(String.valueOf(ch), 0);
    }
    ArrayList<String> generateList() {
        File file; 
        Scanner scan; 
        try {
            file = new File("words.txt"); 
            scan = new Scanner(file); 
            while (scan.hasNextLine()) {
                words.add(scan.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
        }
        return words; 
    }
    String generateWord() {
        Random generator = new Random();
        int index = generator.nextInt(words.size());
        return words.get(index); 
    }
    StringBuilder generateBlank() {
        StringBuilder temp = new StringBuilder(); 
        int length = answer.length(); 
        for (int i = 0; i < length; i++) {
            temp.append("_"); 
        }
        return temp; 
    }
    String getCurrent() {return "The word is currently: " + current.toString();}
    String getAnswer() {return answer;}
    String getCount() {return "You have " + guess_count + " guesses remaining"; }; 
    void guesses() {for (String alpha : guesses) {System.out.print(alpha + " ");}}

}