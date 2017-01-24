import java.io.*;
import java.util.*;

public class HeavyArmorCalc {
   public static final int YOSHI_WEIGHT = 104;
   public static final int SIGNIFICANT_PERCENT = 200;
   public static final int DANGER_PERCENT = 90;
  
   public static void main(String[] args) throws FileNotFoundException {
      Scanner console = new Scanner(System.in);
      File character = new File("empty");
      System.out.println("Input a character's name, and the percent"); 
      System.out.println("at which Yoshi's double jump armor fails will be");
      System.out.println("displayed next to each of their attacks.");
      System.out.println();                      
      
      while (!character.exists()) {
         System.out.print("Type a character's name (no spaces): ");
         String charName = console.next();
         charName = charName.toLowerCase();
         if (charName.equals("diddy")) {
            charName = "diddykong";
         } else if (charName.equals("rosalina&luma") || charName.equals("rosalinaandluma")) {
            charName = "rosalina";
         } else if (charName.equals("bayo")) {
            charName = "bayonetta";
         } else if (charName.equals("captfalcon") || charName.equals("captainfalcon")) {
            charName = "falcon";
         } else if (charName.equals("zss") || charName.equals("zerosuit")) {
            charName = "zerosuitsamus";
         } else if (charName.equals("dk")) {
            charName = "donkeykong";
            System.out.print("DONKEY KONG");
         } else if (charName.equals("darkpit")) {
            charName = "pit";
            System.out.print("Dark Pit/Pit");
         } else if (charName.equals("girlmarth") || charName.equals("martha")) {
            charName = "lucina";
         }            
         charName += ".txt";
         
         character = new File(charName);
         if (!character.exists()) {
            System.out.println("File could not be found.");
         }   
      }
      
      //Prompts for whether or not to include rage - calculates multiplier if necessary   
      System.out.print("Account for rage? (Y or N) ");
      int rage = 0;
      double multiplier = 1.0;
      String answer = console.next();
      if (answer.toLowerCase().equals("y") || answer.toLowerCase().equals("yes")) {
         System.out.print("% of the attacker? (To determine rage) ");
         rage = console.nextInt();
         multiplier = rageMultiplier(rage);
         System.out.println();
         System.out.println("KB multiplier = " + multiplier);
      }   
      
      Scanner input = new Scanner(character);
      System.out.println();
      
      ArrayList<String> dangerMoves = new ArrayList<String>();
      Map<String, Integer> dangerPercent = new HashMap<String, Integer>();
      while (input.hasNextLine()) {
         String attack = input.nextLine();
         Scanner lineScan = new Scanner(attack);
         String name = lineScan.next();
         
         //test if it's a sweetspot move
         boolean hasSweetSpot = false;
         if (name.contains("+")) {
            name = name.substring(0, name.length() - 1);
            hasSweetSpot = true;
         }
         if (name.equals("aerials")) {
            attack = input.nextLine();
            lineScan = new Scanner(attack);
            name = lineScan.next();
            System.out.println();
            System.out.println("Aerials:");
         }
         if (name.contains("specials")) {
            attack = input.nextLine();
            lineScan = new Scanner(attack);
            name = lineScan.next();
            System.out.println();
            System.out.println("Specials:");
         }   
         //calculates % that armor breaks, adds to danger list if below DANGER_PERCENT  
         int yoshPercent = armorBreak(input, lineScan, multiplier);  
         if (yoshPercent <= DANGER_PERCENT) {
            dangerMoves.add(name);
            dangerPercent.put(name, yoshPercent);
         }    
         
         System.out.print(name + ": ");
         
         //for moves with sweetspots/sourspots
         if (hasSweetSpot) {
            if (yoshPercent < SIGNIFICANT_PERCENT) {
               System.out.print(yoshPercent + "(sweet");
            } else {
               System.out.print(">" + SIGNIFICANT_PERCENT + "(sweet");
            }
            String attack2 = input.nextLine();
            Scanner lineScan2 = new Scanner(attack2);
            String sour = lineScan2.next();
            
            int yoshPercent2 = armorBreak(input, lineScan2, multiplier);
            if (yoshPercent2 < SIGNIFICANT_PERCENT) {
               System.out.println("), " + yoshPercent2 + "(sour)");
            } else {
               System.out.println(" or sour)");   
            } 
              
         //for regular moves         
         } else {
            if (yoshPercent < SIGNIFICANT_PERCENT) {
               System.out.println(yoshPercent);
            } else {
               System.out.println(">" + SIGNIFICANT_PERCENT);   
            } 
         }           
      }
      System.out.println();
      System.out.println("Dangerous moves: (breaks armor at < " + DANGER_PERCENT + "%)");
      //for (String move : dangerPercent.keySet()) {
      //   System.out.println(move + " (breaks at " + dangerPercent.get(move) + ")");
      //}   
      for (int i = 0; i < dangerMoves.size(); i++) {
         String move = dangerMoves.get(i);
         System.out.println(move + " (breaks at " + dangerPercent.get(move) + "%)");
      }             
   }   
   
   //calculates knockback based on passed values:
   //p = Yoshi's percent
   //d = Attack damage
   //b = Base knockback
   //s = Knockback growth
   private static double calcKB(double p, double d, int b, int s, double r) {
      p = p + d;
      
      double KB = (p / 10.0) + ((double) p * d) / 20.0;
      KB = KB * (200.0 /(YOSHI_WEIGHT + 100)) * 1.4;
      KB = KB + 18;
      KB = KB * (s / 100.0);
      KB = KB + b;
      KB = KB * r;
      
      return KB;
   } 
   
   private static int armorBreak(Scanner input, Scanner lineScan, double r) {
      double d = lineScan.nextDouble();
      int b = lineScan.nextInt();
      int s = lineScan.nextInt();
      
      double KB = calcKB(0, d, b, s, r);
      int yoshPercent = 0;
      
      while (KB < 120.0) {
         yoshPercent++;
         KB = calcKB((double)yoshPercent, d, b, s, r);
      }
      return yoshPercent;    
   }
   
   private static double rageMultiplier(int rage) {
      if (rage >= 0 && rage < 35) {
         return 1.18;
      } else if (rage > 150) {
         rage = 150;
      }
      double multiplier = rage - 35.0;
      multiplier = multiplier * 0.15;
      multiplier = multiplier / 115;
      return multiplier + 1;
   }         
}

   