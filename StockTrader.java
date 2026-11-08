/*
Part 2: Questions
1. For msft_2000_2010.txt, the highest price comes before the lowest price. 
What does that mean for someone who bought Microsoft stock in 2000?
	- Someone who bought Microsoft stock in 2000 would have experienced major losses 
	as the price declined and did not fully recover by 2010.
 
2. Look up what happened in the world around the lowest month for sbux_2007_2016.txt and sp500_1925_1945.txt. 
Does the timing make sense?
	-Starbucks (Nov 2008): The 2008 Financial Crisis reduced consumer spending, hurting Starbucks' sales and stock price.
	-S&P 500 (Jun 1932): The Great Depression caused widespread unemployment and a massive stock market decline.
 */

/*
Part 3: Questions
1. movingAverage performed the best on the most files. However, no strategy was best on every file.

2. The dip buyer performed poorly on rising.txt because the price never dropped two months in a row.
It never bought shares and finished with only $10,000.

3. The moving average beat buy and hold on Apple stock.
It likely avoided some major price declines by selling
during downward trends and buying when prices recovered.

4. My strategy beat buy and hold on 3 out of 10 files including falling, Amazon, and Microsoft.
However, it performed worse on the other 7 files. This shows that one strategy does not work well everywhere.
 */














import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class StockTrader {

    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(new File("msft_2000_2010.txt"));
        String symbol = in.next();
        int n = in.nextInt();

        // TODO: create the three arrays, each with n elements
		int[] years = new int[n];
		int[] months = new int[n];
		double[] prices = new double[n];
        // TODO: call loadData, then printData
        loadData(in, years, months, prices);
        printData(years,months,prices);
        System.out.println("lowest: $" + prices[indexOfLowest(prices)] + " " + monthName(months[indexOfLowest(prices)]) + " " + years[indexOfLowest(prices)]);
        System.out.println("highest: $" + prices[indexOfHighest(prices)] + " " + monthName(months[indexOfHighest(prices)]) + " " + years[indexOfHighest(prices)]);
        System.out.println("biggest drop: " + monthName(months[indexOfBiggestDrop(prices)]) + " " + years[indexOfBiggestDrop(prices)]);
		double result = simulate(years, months, prices);
		System.out.printf("Final value: $%,.2f%n", result);
    }
    
    public static void loadData(Scanner in, int[] years, int[] months, double[] prices) {
		for (int i = 0; i<years.length; i++) {
			years[i] = in.nextInt();
			months[i] = in.nextInt();
			prices[i] = in.nextDouble();
		}
	}	
	public static String monthName(int month) {
		String[] names = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
		return names[month-1];
	}
	public static void printData(int[] years, int[] months, double[] prices) {
		for (int i = 0; i<years.length; i++) {
			System.out.print(monthName(months[i]) + " " + years[i] + ": $");
			System.out.printf("%.2f", prices[i]);
			System.out.println();
		}
	}
	
	public static int indexOfLowest(double[] prices) {
		int lowest = 0;
		for (int i = 0; i<prices.length;i++) {
			if (prices[i] < prices[lowest]) {
				lowest = i;
			}
		}
		return lowest;
	}	
	public static int indexOfHighest(double[] prices) {
		int highest = 0;
		for (int i = 0; i<prices.length;i++) {
			if (prices[i] > prices[highest]) {
				highest = i;
			}
		}
		return highest;
	}	
	public static int indexOfBiggestDrop(double[] prices) {
		int index = 1;
		for (int i = 1; i<prices.length;i++) {
			if (prices[i]/prices[i-1] < prices[index]/prices[index-1]) {
				index = i;
			}
		}
	return index;	
	}	
	public static final double START_CASH = 10000.0;
	public static final double FEE = 10.0;

	public static double simulate(int[] years, int[] months, double[] prices) {
		double cash = START_CASH;
		int shares = 0;
		double boughtAt = 0.0;

		for (int i = 0; i < prices.length; i++) {
			double[] history = Arrays.copyOf(prices, i + 1);
			String action = decide(history, shares > 0, boughtAt);

			if (action.equals("BUY") && shares == 0) {
				int canBuy = (int) ((cash - FEE) / prices[i]);
				if (canBuy > 0) {
					shares = canBuy;
					cash = cash - shares * prices[i] - FEE;
					boughtAt = prices[i];
					System.out.printf("%s %d: BUY %d shares at $%.2f%n",
							monthName(months[i]), years[i], shares, prices[i]);
				}
			} else if (action.equals("SELL") && shares > 0) {
				cash = cash + shares * prices[i] - FEE;
				System.out.printf("%s %d: SELL %d shares at $%.2f%n",
						monthName(months[i]), years[i], shares, prices[i]);
				shares = 0;
				boughtAt = 0.0;
			}
		}
		return cash + shares * prices[prices.length - 1];
	}
	public static String decide(double[] history, boolean holding, double boughtAt) {
		return myStrategy(history, holding, boughtAt);  
	}
	public static String buyAndHold(double[] history, boolean holding, double boughtAt) {
		return "BUY";  
	}
	public static String dipBuyer(double[] history, boolean holding, double boughtAt) {
		if (history.length < 3) {
			return "SELL";
		}
		if (history[history.length - 1] < history[history.length - 2] && history[history.length - 2] < history[history.length - 3]) {
			return "BUY";
		}
		if (boughtAt < history[history.length-1] * 1.1) {
			return "SELL";
		}	
		if (holding) {
			return "BUY";
		} else {
			return "SELL";
		}
	}
	public static String movingAverage(double[] history, boolean holding, double boughtAt) {
		if (history.length < 12) {
			return "HOLD";
		}
		double average = 0;
		for (int i = history.length-12; i < history.length;i++){
			average += history[i];
		}
		average /= 12;
		
		if(holding == false && history[history.length - 1] > average) {
			return "BUY";
		}
		if (holding == true && history[history.length - 1] < average) {
			return "SELL";
		}
		return "HOLD";
	}
	/*
 My strategy buys a stock when its price has increased for two consecutive months, indicating an upward trend.
 If I own shares, I sell when the price increases by at least 15% or decreases by at least 10% from the purchase price.
 Otherwise, I hold the stock.
 */

	public static String myStrategy(double[] history, boolean holding, double boughtAt) {
		if (history.length < 3) {
			return "HOLD";
		}
		if (holding) {
			if (history[history.length - 1] >= boughtAt * 1.15 || history[history.length - 1] <= boughtAt * 0.90) {
				return "SELL";
			}
		} else {
			if (history[history.length - 1] > history[history.length - 2] && history[history.length - 2] > history[history.length - 3]) {
				return "BUY";
			}
		}
		return "HOLD";
	}
}
