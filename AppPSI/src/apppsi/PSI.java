
package psifx;

import org.problets.lib.comm.rmi.*;
import java.rmi.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.net.*;
import java.io.*;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;



/**
 * Practice Student Interface Minimalist text-based interface to display
 * ParsonsPuzzles and evaluate performance of a user on them. Used mostly for
 * debugging.
 * 
 * Original version implemented in Paul Burton's IT Senior Project. Updated when
 * we switched the RMI relation between the ITS side and the Broker so that the
 * ITS would systematically initiate the dialog.
 **/
public class PSI
{

	/**
	 * Main method; just building a new PSI object and getting it started
	 **/
	public static void main(String args[])
	{
		String brokerHostname = System.getenv("BROKER_HOSTNAME");
		String brokerPortText = System.getenv("BROKER_PORT");
		String relayHostname = System.getenv("RELAY_HOSTNAME");
		String relayPortText = System.getenv("RELAY_PORT");
	
		if (args.length > 0) {
			brokerHostname = args[0];
		}

		if (args.length > 1) {
			brokerPortText = args[1];
		}

		if (args.length > 2) {
			relayHostname = args[2];
		}

		if (args.length > 3) {
			relayPortText = args[3];
		}

		if (brokerHostname == null) {
			brokerHostname = "spell.forest.usf.edu";
		}

		int brokerPort = 1235;
		if (brokerPortText != null) {
			try {
				brokerPort = Integer.parseInt(brokerPortText);
			} catch (NumberFormatException e) {
				System.out.println("NumberFormatException when parsing Broker port: '" + brokerPortText + "'. Use default port '" + brokerPort + "' instead.");
			}
		}
		
		if (relayHostname == null) {
			relayHostname = "";
		}

		int relayPort = 0;
		if (relayPortText != null) {
			try {
				relayPort = Integer.parseInt(relayPortText);
			} catch (NumberFormatException e) {
				System.out.println("NumberFormatException when parsing Relay port: '" + relayPortText + "'. Use default port '" + relayPort + "' instead.");
			}
		}
		
		PSI psi = new PSI(brokerHostname, brokerPort, relayHostname, relayPort);
		psi.start();
	}

	/**
	 * Please note that when we build the proxy, it will initiate the RMI link
	 * to the broker
	 **/
	public PSI(String brokerHostname, int brokerPort, String relayHostname, int relayPort)
	{
		userInput = new Scanner(System.in);
		proxy = new ParsonsBrokerProxy(brokerHostname, brokerPort, relayHostname, relayPort);
	}

	/**
	 * This method does all the work a PSI object is supposed to do
	 **/
	public void start()
	{

		String studentLogin;

		// reading student's login name

		System.out.println("Please enter your student Name: ");
		studentLogin = userInput.nextLine();
		proxy.setStudentID(studentLogin);
		log("Sent student login" + studentLogin);

		// Main loop;
		// keep presenting new ParsonsPuzzles to student until they leave
		// TODO - add a hook so that in case of timeout or SIGKILL
		// we let the Broker know the evaluation is lost

		while (true)
		{

			ParsonsPuzzle puzzle = null;

			// Requesting new ParsonsPuzzle from broker
			puzzle = proxy.getParsonsPuzzle();

			log("Got ParsonsPuzzle");

			// Evaluating the ParsonsPuzzle with the user
//			evaluate(puzzle);

		}
	}

	// ---------------------------------------------------------------------------------
	// PRIVATE stuff
	// ---------------------------------------------------------------------------------

	/**
	 * Our main interface with the user
	 **/
	private Scanner userInput;

	/**
	 * This is our proxy to the broker, aggregated in any ITS we are using; here
	 * PSI
	 **/
	private ParsonsBrokerProxy proxy;

	/**
	 * Temporary debugging method
	 **/
	private void log(String msg)
	{
		System.out.println("[PSI]\t" + msg);
	}

	/**
	 * Utility - apply AND operator to an array of booleans
	 * 
	 * @param e
	 *            Array of Booleans
	 * @return Logical AND of all elements in the array
	 **/
	private boolean areAllTrue(boolean[] e)
	{
		for (int i = 0; i < e.length; i++)
			if (!e[i])
				return false;
		return true;
	}

	/**
	 * Process a ParsonsPuzzle we got from the broker; i.e. present it to the
	 * user, get the user solution, evaluate it When done, we use our proxy to
	 * send the evaluation data back to the Broker
	 * 
	 * @param o
	 *            The ParsonsPuzzle object to be used; provided by the Broker
	 **/
	private void evaluate(ParsonsPuzzle o)
	{

		String tmpString;
		
		String[] title = o.getTitle();
		String[] description = o.getDescription();
		List<Fragment> fragments = o.getPuzzleFragments();
		String[] distractors = o.getDistractorStrings(); //DONE *****Rename to getDistractorStrings

		

		//DONE *****Rename class "Line" to "Fragment"
		//DONE *****Replace the lines code above with ParsonsPuzzle.getPuzzleFragments
		// *****Given a line #, determine if fragment belongs to the line : boolean
		
		int count = 0;

		for (int i = 0; i < fragments.size(); i++)
		{

			if (fragments.get(i).getLineNum() != null)
			{
				count++;
			}
		}

		int attempts = 0;
		long startTime;
		long endTime;
		boolean giveUp = false;
		Date elapsed;
		long elapsedMillis;

		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		DateFormat timeFormat = new SimpleDateFormat("mm:ss");

		startTime = System.currentTimeMillis();

		System.out.println(String.format("Evaluation start time: %s", dateFormat.format(startTime)));

		boolean[] isCorrect = new boolean[count];

		for (boolean b : isCorrect)
		{
			b = false;
		}

		while (!areAllTrue(isCorrect))
		{

			int answerLength = 0;

			if (attempts > 0)
			{
				System.out.println("\n\nWrong, try again");
			}

			displayParsonsPuzzle(title, description, fragments, distractors,count);			
			// Debugging order printout

	/*		System.out.println("\n Solution = ");
			for (int i = 0; i < count; i++)
			{
				for (int orderCheck = 0; orderCheck < fragments.size(); orderCheck++)
				{
					if (fragments.get(orderCheck).getOriginalOrder() == i + 1)
					{
						System.out.print((orderCheck + 1) + " ");
					}
				}
			}

			// User input validation
			System.out.println("\n\nEnter Your Solution: ");
			int[] userAttempt = new int[answerLength];
			int numAnswers = 0;
			int position = 0;

			while (answerLength != count)
			{
				tmpString = userInput.nextLine();

				if (tmpString.trim().equals("-1"))
				{
					giveUp = true;
					break;
				}
				else
				{
					StringTokenizer strToken = new StringTokenizer(tmpString);
					answerLength = strToken.countTokens();
					if (answerLength != count)
					{
						System.out.println("Check answer length, it doesn't match the question!");
					}
					else
					{
						userAttempt = new int[answerLength];

						for (int i = 0; i < answerLength; i++)
						{
							try
							{
								userAttempt[i] = Integer.parseInt((String) strToken.nextElement());
							}
							catch (NumberFormatException e)
							{
								System.out.println("Please only enter integers!");
								answerLength = 0;
							}
						}
					}
				}
			}

			if (giveUp)
			{
				break;
			}

			// Checking for distractors guessed and correct order
			List<Integer> distractorsGuessed = new ArrayList<Integer>();
			for (int j = 0; j < answerLength; j++)
			{
				position = j + 1;

				if (!(userAttempt[j] - 1 > fragments.size() || userAttempt[j] - 1 < 0))
				{
					if (fragments.get(userAttempt[j] - 1).getLineNum() == null)
					{
						numAnswers = 0;
						distractorsGuessed.add(userAttempt[j]);
					}
					else
					{
						numAnswers = fragments.get(userAttempt[j] - 1).getLineNum().size();
						for (int k = 0; k < numAnswers; k++)
						{
							if (position == (fragments.get(userAttempt[j] - 1).getLineNum().get(k)))
							{
								isCorrect[j] = true;
							}
						}
					}
				}
			}

			// Feedback to the user

			System.out.println("You guessed this order: ");
			for (int i : userAttempt)
			{
				System.out.print(i + " ");
			}
			System.out.println();

			if (!distractorsGuessed.isEmpty())
			{
				System.out.print("You used the following erroneous lines : ");
				for (int i : distractorsGuessed)
					System.out.print(i + " ");
			}
			System.out.println();

			attempts++;
		}

		if (giveUp)
		{
			System.out.println("\nBetter luck next time.\n\n\n\n");
		}
		else
		{
			System.out.println("\nThat's correct!\n\n\n\n");
		}

		*/endTime = System.currentTimeMillis();
		elapsedMillis = endTime - startTime;
		elapsed = new Date(elapsedMillis);

		// elapsed.setTimeInMillis();

		System.out.println(String.format("\nEvaluation end time: %s", dateFormat.format(endTime)));
		System.out.println(String.format("\nEvaluation elapsed time: %s", timeFormat.format(elapsed)));

		// sending back evaluation data
		proxy.setParsonsEvaluation((double) attempts, elapsedMillis, giveUp);

		// FIXME - provide more data than this; i.e. aligned with what
		// the real ParsonsProblet will provide

		log("Sent evaluation data");
	}
}
	/**
	 * Utility method to display {@link ParsonsPuzzle} to user
	 * 
	 * @param title
	 *            Title string array extracted from {@link ParsonsPuzzle}
	 * @param description
	 *            Description string array extracted from {@link ParsonsPuzzle}
	 * @param body
	 *            {@link List} of Line objects representing the body of the
	 *            programs
	 **/
	private void displayParsonsPuzzle(String[] title, String[] description, List<Fragment> body, String[] transforms,int count)
	{
		List<String> list = new ArrayList<String>();
		List<Integer> order = new ArrayList<Integer>();

		for (int i = 0; i < count; i++)
		{
			for (int orderCheck = 0; orderCheck < body.size(); orderCheck++)
			{
				if (body.get(orderCheck).getOriginalOrder() == i + 1)
				{
					order.add(orderCheck + 1);
					System.out.print((orderCheck + 1) + " ");
				}
			}
		}

		System.out.println("\n\n");

		for (String s : title)
			System.out.println(s);

		for (String s : description)
			System.out.println(s);

		int b = 1;
		for (Fragment s : body) {
			list.add(s.getLine().toString());
			System.out.println("List Added successfully "+list.get(0));


			System.out.println(b++ + "\t" + s.getLine());
		}
		
		

	}

}
