import java.io.File;
import java.io.IOException;
import java.util.Scanner;

// Creating a Path class to hold the current gold and pathway during exhaustive algorithm
class Path{
	int minStep, currentStep;
	int maxGold, currentGold;
	StringBuilder maxPath, currentPath;
	public Path() {
		maxGold = 0;
		currentGold = 0;
		maxPath = new StringBuilder();
		currentPath = new StringBuilder();
		minStep = 0;
		currentStep = 0;
	}
}

public class GreedyGnomesDynamic {
	int maxGold = 0;
	int rowLocation = 0;
	int colLocation = 0;
	boolean validMap;
	int row, col;
	String[][] map;
	int[][] DPTable; // Table contain maximum value of gold collected at each coordinate
	int[][] compareTable; // Table to compare for tracing back
	
	// Processing the map
	public GreedyGnomesDynamic(String mapName) {
		validMap = true;
		Scanner sc = null;
		row = 0; col = 0;
		try {
			sc = new Scanner(new File(mapName));
			//sc.useDelimiter(" ");	
			row = sc.nextInt();
			col = sc.nextInt();
			//check if row or column exceeds limitation
			if (row > 27 || col > 27) {
				validMap = false;
				sc.close();
				return;
			}
			map = new String[row][col];
			DPTable = new int[row][col];
			compareTable = new int[row][col];
			
			// Storing the map data into a 2D array instance
			for(int i = 0; i < row; i ++) {
				sc.nextLine();
				for (int j = 0; j < col; j++) {
					map[i][j] = sc.next();
					DPTable[i][j] = -1;

					//Checking if the map has any unusual characters
					if (!map[i][j].contains("X") && !map[i][j].contains(".") && !parsable(map[i][j])) {
						validMap = false;
						sc.close();
						return;
					}
				}
			}
			sc.close();

			// Display the 2D array
			System.out.println(row +" "+ col);
			for(int i = 0; i < row; i++) {
				for(int j = 0; j < col; j++) {
					System.out.print(map[i][j] + " ");
				}
				System.out.println();
			}
			System.out.println();
			
			// set the table value for the start point
			if (this.parsable(this.map[0][0])) {
				this.DPTable[0][0] = Integer.parseInt(this.map[0][0]);
			}
			else {
				this.DPTable[0][0] = 0;
			}
			
			//Making the compare table
			for(int i = 0; i < row; i++) {
				for(int j = 0; j < col; j++) {
					if (parsable(this.map[i][j])) {
						this.compareTable[i][j] = Integer.parseInt(this.map[i][j]);
					} else if (this.map[i][j].contains("X")) {
						this.compareTable[i][j] = -1;
					} else {
						this.compareTable[i][j] = 0;
					}
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// This method will recursively go through the cells and fill a table
	// with the maximum amount of gold that can be collected up to each table cell
	private void dynamicTableFill(int row, int col) {
		int nextGold = 0;
		
		if(col != this.col - 1) { // If the cell to the right is not out of bound
			if(!this.map[row][col + 1].contains("X")) { // If the cell to the right is not an obstacle
				if (parsable(this.map[row][col + 1])) { // If the cell has gold
					nextGold = Integer.parseInt(this.map[row][col + 1]); // save that amount of gold
				}
				// If the table cell to the right has less gold than 
				// the sum of the current table cell and the cell to the right
				if (DPTable[row][col + 1] < (DPTable[row][col] + nextGold)) {
					DPTable[row][col + 1] = DPTable[row][col] + nextGold;// Replace it with the new sum
				}
				dynamicTableFill(row, col + 1);
			}
		}
		nextGold = 0;
		if(row != this.row - 1) { // If the cell below is not out of bound
			if(!this.map[row + 1][col].contains("X")) { // If the cell below is not an obstacle
				if (parsable(this.map[row + 1][col])) { // If the cell has gold
					nextGold = Integer.parseInt(this.map[row + 1][col]); // save that amount of gold
				}
				// If the table cell below has less gold than 
				// the sum of the current table cell and the cell below
				if (DPTable[row + 1][col] < (DPTable[row][col] + nextGold)) {
					DPTable[row + 1][col] = DPTable[row][col] + nextGold;// Replace it with the new sum
				}
				dynamicTableFill(row + 1, col);
			}
		}
	}
	
	// Find the table coordinate with the most gold and least traverse steps
	private void findEndPoint() {
		int[][] mostGoldLocation = new int[2][this.col]; // a 2D array to store max gold of each column and its row location
		
		//Getting the max gold on each column
		for (int i = 0; i <= this.col - 1; i++) { //Traversing each column first
			for (int j = 0; j <= this.row - 1; j++) {
				if (this.DPTable[j][i] > mostGoldLocation[0][i]) { //
					mostGoldLocation[0][i] = this.DPTable[j][i]; //save the max gold on this column
					mostGoldLocation[1][i] = j; // save the location of the row
				}
			}
		}
		
		// Finding the true max gold and its location
		for (int i = 0; i <= this.col - 1; i++) { // traversing through the 2D array
			if (this.maxGold < mostGoldLocation[0][i]) {
				this.maxGold = mostGoldLocation[0][i]; // save the new max gold
				this.rowLocation = mostGoldLocation[1][i]; //save its row
				this.colLocation = i; // save its column
			} else if (this.maxGold == mostGoldLocation[0][i]) {
				if ((this.rowLocation + this.colLocation) > (mostGoldLocation[1][i] + i)) { // return the location that uses less steps
					this.maxGold = mostGoldLocation[0][i];
					this.rowLocation = mostGoldLocation[1][i];
					this.colLocation = i;
				}
			}
		}
	}
	
	// From the end point find the path to the starting point
	private void traceback() {
		StringBuilder str = new StringBuilder();
		int rowLocation = this.rowLocation;
		int colLocation = this.colLocation;
		int maxGold = this.maxGold;
		
		
		while (rowLocation > 0 || colLocation > 0) { // From the max gold location, traverse back to the row 0 column 0
			maxGold = this.DPTable[rowLocation][colLocation] - this.compareTable[rowLocation][colLocation]; 
			if (colLocation == 0) {
				rowLocation--;
				str.insert(0,'D'); // append to the start of the string
			} else if (rowLocation == 0) {
				colLocation--;
				str.insert(0,'R');
			} else {
				if (maxGold == this.DPTable[rowLocation - 1][colLocation]) {
					rowLocation--;
					str.insert(0, 'D');
				} else {
					colLocation--;
					str.insert(0, 'R');
				}
			}
		}
		
		System.out.println("The Path to mine max gold: " + str);
		System.out.println("Number of steps: " + str.length());
	}
	
	public boolean parsable(String intToConvert) {
		try {
			// If this string is a number
			Integer.parseInt(intToConvert);
			return true;
		} catch (NumberFormatException e) {
			// If it is not, return false
			return false;
		}
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long startTime = System.nanoTime(); // Indicating start time
		GreedyGnomesDynamic gnomeMap = new GreedyGnomesDynamic(args[0]);
		if (!gnomeMap.validMap) {
			System.out.println("This map cannot be processed! Please try again with a different map.");
		} else {
			gnomeMap.dynamicTableFill(0, 0); // call the dynamic algorithm that fills the table
			// print table, remove when finished
			// Remove comment if want to see table
			
//			for(int i = 0; i < gnomeMap.row; i++) {
//				for(int j = 0; j < gnomeMap.col; j++) {
//					System.out.print(gnomeMap.DPTable[i][j] + " ");
//				}
//				System.out.println();
//			}
//			System.out.println();
			
			gnomeMap.findEndPoint();
			gnomeMap.traceback();
			System.out.println("Max Gold: " + gnomeMap.maxGold);
			System.out.println("Time elapsed: " + ((System.nanoTime() - startTime) / 1000000) + " milliseconds");// Measuring total time of algorithm
		}
	}
}
