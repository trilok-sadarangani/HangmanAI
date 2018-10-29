import java.util.*; 
import java.lang.*; 
import java.io.*; 
import java.util.regex.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.io.InputStreamReader;

public class Hangman {
    // static void UserPlay(HangmanGame game) {
    //     while (game.guess_count > 0) {
    //         if (game.current.toString().equals(game.answer)) {
    //             System.out.println("Well Done."  + game.answer + " is the final word"); 
    //             return; 
    //         }
    //         Scanner input = new Scanner(System.in); 
    //         System.out.println("This is the current word: "); 
    //         for (int i = 0; i < game.current.length(); i++) {
    //             System.out.print(game.current.charAt(i) + " "); 
    //         }
    //         System.out.println("Remaining Guesses: "  + game.guess_count);
           
    //         boolean duplicate = false; 
    //         String move; 
    //         do {
    //             System.out.print("GUESS: "); 
    //             move = input.next().toLowerCase(); 
    //             if (!game.guesses.contains(move) && move.length() == 1) 
    //                 duplicate = true; 
    //             else 
    //                 System.out.println("You have already guessed "  + move + ". Or, your guess is not a single letter"); 
    //         }
    //         while (!duplicate); 
            
            
    //         if (game.answer.indexOf(move) == -1) {
    //             System.out.println(move + " DOESN'T EXIST"); 
    //             game.guess_count--; 
    //         }
    //         else {
    //             int count = 0; 
    //             int index = game.answer.indexOf(move); 
    //             while (index >= 0) {
    //                 game.current.replace(index, index+1, move); 
    //                 index = game.answer.indexOf(move, index + 1);
    //                 count++; 
    //             }
    //             System.out.println("CORRECT"); 
    //         }
    //         game.guesses.add(move);   
    //         System.out.println();  
    //     }   
    //     System.out.println("GAME OVER. THE WORD IS " + game.answer); 
    //     return; 
    // }
    public static Prisoner getInfo(String info, Prisoner prisoner) {

        Pattern p = Pattern.compile("(ALIVE|DEAD|FREE)"); // STATUS
        Matcher m = p.matcher(info);

        Pattern p1 = Pattern.compile("(\\d+)"); // TOKEN
        Matcher m1 = p1.matcher(info);

        Pattern p2 = Pattern.compile("(\\d)(,)"); // GUESSES
        Matcher m2 = p2.matcher(info);

        Pattern p3 = Pattern.compile("([A-Z_'\\s]+)(\"})"); // STATE
        Matcher m3 = p3.matcher(info);

        if (m.find() && m1.find() && m2.find() && m3.find()) {
            prisoner.setStatus(m.group()); 
            prisoner.setToken(m1.group()); 
            prisoner.setRemaining(m2.group(1)); 
            prisoner.setState(m3.group(1)); 
        }
        return prisoner; 
    }
    static void AIPlay(HangmanGame game, Prisoner prisoner) {
        ArrayList<String> potential = new ArrayList<>(); 
        potential = create_size(game, potential); // FILTER BY SIZE 
        game.alpha = alpha_freq(game, potential); // CREATE FREQUENCY OF LETTERS IN THE POTENTIAL LIST 
        String move = make_guess(game).toLowerCase(); // GUESS THE FIRST MOVE

        while (game.guess_count > 0) {
            if (game.current.toString().equals(game.answer)) {  // IF THE ANSWER IS GUESSED CORRECTLY 
                System.out.println("CONGRATULATIONS. "  + game.answer.toUpperCase() + " IS THE FINAL WORD. The prisoner is FREE."); 
                return; 
            }
            System.out.println("THIS IS THE CURRENT WORD: "); // PRINT OUT BLANKS AND CURRENT WORD
            for (int i = 0; i < game.current.length(); i++) {
                System.out.print(game.current.charAt(i) + " "); 
            }
            System.out.println(); 
            System.out.println("REMAINING GUESSES: "  + game.guess_count);
            System.out.println("GUESS: " + move); 
            
            
            if (!game.answer.contains(move)) { 
                System.out.println(move + " DOESN'T EXIST"); 
                game.guess_count--; // DECREASE AMOUNT OF TRIES 
                game.wrong_guesses.add(move); // ADD THE MOVE TO THE WRONG ANSWERS LIST 
            }
            else {
                int index = game.answer.indexOf(move);  
                while (index >= 0) { // CHANGE CURRENT 
                    game.current.replace(index, index+1, move); 
                    index = game.answer.indexOf(move, index + 1);
                } 
                System.out.println("CORRECT: "  + move + " EXISTS"); 
            }
            game.guesses.add(move); // ADD THE MOVE TO THE ENTIRE GUESSES LIST 
            potential = trim(potential, game); // CHANGE THE POTENTIAL AMOUNT OF WORDS BASED ON CURRENT PATTERN AND WRONG GUESSES 
            game.alpha = alpha_freq(game, potential); // GET THE NEW FREQUENCY OF LETTERS
            move = make_guess(game).toLowerCase(); // FIND THE MAX FREQUENCY LETTER 
            
            System.out.println(); 
        }    
        System.out.println("THE PRISONER WAS NOT FREED");    
        
        return; 
    }   
    static String make_guess(HangmanGame game) {
        int max = Integer.MIN_VALUE; 
        String ans = ""; 
        for (Map.Entry<String, Integer> entry : game.alpha.entrySet()) {
            if (entry.getValue() > max) {
                if (!game.guesses.contains(entry.getKey())) {
                    ans = entry.getKey(); 
                    max = entry.getValue(); 
                }
            }
        }
        return ans; 
    }
    static ArrayList<String> trim(ArrayList<String> potential, HangmanGame game) {
        String x = "[a-z]"; 
        StringBuilder regex = new StringBuilder(); 
        for (int i = 0; i < game.current.length(); i++) {
            if (game.current.charAt(i) == '_') {
                regex.append(x); 
            }
            else {
                regex.append(game.current.charAt(i) + ""); 
            }
        }
        String pattern = regex.toString(); 
        String[] wrong = new String[game.wrong_guesses.size()]; 
        wrong = game.wrong_guesses.toArray(wrong); 
        for (Iterator<String> iterator = potential.iterator(); iterator.hasNext(); ) {
            String value = iterator.next();
            if (!value.matches(pattern) ) {
                iterator.remove();
            }
            if (Arrays.stream(wrong).parallel().anyMatch(value::contains)) {
                iterator.remove();
            }
        }
        return potential; 
    }
    static HashMap<String, Integer> alpha_freq(HangmanGame game, ArrayList<String> potential) {
        HashMap<String, Integer> copy = new HashMap<>(); 
        for (String s : potential) {
            String[] temp = s.toLowerCase().split(""); 
            for (String t : temp) {
                if (copy.containsKey(t)) {
                    copy.put(t.toLowerCase(), copy.get(t)+1); 
                }
                else {
                    copy.put(t, 1); 
                }
            }
        }
        return copy;         
    }
    static ArrayList<String> create_size(HangmanGame game, ArrayList<String> potential) {
        for (String s : game.words) {
            if (s.length() == game.answer.length()) {
                potential.add(s); 
            }
        }
        return potential; 
    }
    public static void main(String[] args) {
        HangmanGame game = new HangmanGame(); 
        String gameUrl = "http://gallows.hulu.com/play?code=trilok.sadarangani@duke.edu"; 
        Prisoner prisoner = new Prisoner(); 
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL(gameUrl).openStream()));
            String info = in.readLine();
            prisoner = getInfo(info, prisoner);  
        }catch(IOException e){
            System.err.println(e);
        }
        AIPlay(game, prisoner); 
        //String call = "http://gallows.hulu.com/play?code=yuebin.patrick@gmail.com" + String.format("&token=%s&guess=%s", token, "a");

    }

    
}
