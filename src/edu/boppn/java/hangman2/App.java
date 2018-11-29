package edu.boppn.java.hangman2;

/**
 * Hangman 2.0
 * 
 * Objectives:
 * 	- Load a list of games
 * 	- Allow the user to play multiple games, back to back, without restarting the application
 * 	- Store game definitions in a file
 * 	- Write each game result out to a log file
 * 	- Make sure to use lists, organize your code, provide good names, comments, and follow the Java conventions
 * 
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class App
{
	
	List<Game> games = new ArrayList<Game>();
	
	// Reads the games file and puts the contents into the games array above
	public List<Game> loadGames() throws IOException
	{
		
		File f = new File("games.txt");
		
		/* Prints test output to the console
		System.out.println("Does the file exist? " + f.exists());
		*/
		
		if (f.exists())
		{
			BufferedReader br = new BufferedReader(new FileReader(f));
			
			String st;
			while ((st = br.readLine()) != null)
			{	
				// Splits the strings using a regular expression; separates by semicolons
				String[] splitGames = st.split(";");
				
				// Defines the game's attributes
				Game g = new Game();
				g.setWord(splitGames[0]);
				g.setHint(splitGames[1]);
				
				// Adds game to the array
				games.add(g);
			}
			
		}
		return games;
	}
	
	
	
	// The game results string; all text goes into this string...
	String results = "";
	
	// ...from this method, which also prints to the console.
	public void print(String p) {
		results+=p+"\n";
		System.out.println(p);
	}
	
	// This method then puts the results into a text file after the game is done.
	public void outputGame(String out) throws IOException
	{	
		// results+="\n\n=== END ===\n\n";
		
		// Makes sure that the linebreaks are output correctly
		results = results.replace("\n", System.getProperty("line.separator"));
		
		/*
		File r = new File("results.txt");
		if (r.exists())
		{
			BufferedReader priorResults = new BufferedReader(new FileReader(r));
			String pr = 
			while ((pr = r.readLine()) != null)
			resultsReader.close();
		}
		*/
		
	    BufferedWriter output = new BufferedWriter(new FileWriter("results.txt"));
	    try 
	    {
			output.write(results);
		} 
	    catch (IOException e) 
	    {
			throw e;
		}
	    finally
	    {
	    	output.close();
	    }   
	}
	
	
	
	// Generates the word's mask
	public String generateMask(String word, String guesses)
	{
		String mask = "";
		
		// Loop over word
		for (int i = 0; i < word.length(); i++)
		{
			// Look at each char in word
			String currentChar = String.valueOf(word.charAt(i));
			if (guesses.contains(currentChar.toLowerCase()) || guesses.contains(currentChar.toUpperCase()))
			{
				// If they did, append the char.
				mask += currentChar;
			}
			else
			{
				// If they did not, append a *.
				mask += "*";
			}
		}
		
		return mask;
	}

	
	
	// Returns whether or not the current word has been solved
	public boolean isUnsolved(String word, String userGuesses)
	{
		return generateMask(word, userGuesses).contains("*");
	}
	
	
	
	// Returns whether or not a guess is bad
	public boolean badGuess(String word, char userGuess) {
		return !word.contains(String.valueOf(userGuess).toLowerCase()) && !word.contains(String.valueOf(userGuess).toUpperCase());
	}
	
	
	
	// Control flow of game.
	public void playGame()
	{	
		
		Scanner sc = new Scanner(System.in); // Initialize the scanner, enabling this to read from the console
		
		int tries = 5; // Variable to track failed guesses
		
		for(Game game : games) // Repeat until all games in the games list have been solved
		{
			
			
			// These get the Word and Hint of the current game
			String word = game.getWord();
			String hint = game.getHint();
			
			// These reset the user guesses & tries for each game
			String userGuesses = "";
			tries = 5;
			
			
			while (isUnsolved(word, userGuesses)) // Game loop; runs until the player wins or loses
			{
				
				
				print("Guess a letter" + "\n" + "Hint: " + hint); // Prompts the user to guess, gives them the hint
				print("Mask: " + generateMask(word, userGuesses)); // Displays current mask & guesses
				
				if (userGuesses.length() > 0) // Outputs user guesses
				{
					print("Your guesses: " + userGuesses.toLowerCase());
				}
				
				String input = sc.nextLine(); // Collects user input
				results+=input+"\n";
				
				if (input.length() > 0) // Determines whether the user actually input anything; otherwise they'd get an error if they input nothing
				{
					char userGuess = input.charAt(0); // Takes only the first character
					
					if (userGuesses.contains(userGuess+"")) // Prevents the user from inputting the same letter multiple times & concatenates the char input into a String.
					{
						print("You have already guessed " + "\"" + userGuess + "\"");
					}
					else // Adds the guess to the list of guesses that have been made and removes a life if the guess is bad.
					{
						userGuesses += (userGuess+"");
						if (badGuess(word, userGuess))
						{
							tries--;
							print("Incorrect guess. You have " + tries + " tries remaining.");
						}
					}
				}
				else // Asks the user to guess a letter if they input nothing
				{
					print("Please guess a letter.");
				}
				
				if (tries == 0) // When the user runs out of lives, goes to Game Over
				{
					break;
				}
				
				
			} // WHILE LOOP ENDS HERE
			
			
			if (tries == 0) // Goes to Game Over
			{
				break;
			}
			
			// Success message
			print("\n" + word + " is correct!" + "\n");
			
			
		} // GAME END
		
		
		if (tries > 0) // User wins if all games have been completed
		{
			print("You win!");
		}
		else // User loses if they instead ran out of tries
		{
			print("\n\nGame over :(");
		}
		
		sc.close();
	}

	
	
	public static void main(String[] args) // Main Method
	{
		App rf = new App();
		try
		{
			List<Game> games = rf.loadGames();
			/* Prints test output to the console
			for(Game game : games) {
				System.out.println(game.getWord() + " : " + game.getHint());
			}
			*/
			rf.playGame();
			rf.outputGame(rf.results);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
