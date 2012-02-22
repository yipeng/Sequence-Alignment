import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Align {
	private static String[] parsedStr1;
	private static String[] parsedStr2;

	public static void main(String[] args) {
		if (args.length != 4)
		{
			System.out.println("Call align as follows:\n\n" +
					"  java align <seq to align> <submatrix> <initiation penalty> " +
			"<extension penalty>");
			System.exit(0);
		}
		String[] seqList = prepareSequenceFromFile(args[0]);
		int[][] matrixList = prepareSubmatrix(args[1]);
		int initP = Integer.parseInt(args[2]);
		int extP = Integer.parseInt(args[3]);

		new SW3(seqList, matrixList, initP, extP, parsedStr1, parsedStr2);
		System.exit(0);
	}

	private static int[][] prepareSubmatrix(String args) {
		int[][] submatrix = null;
		try
		{	
			int lines = countLinesInFile(args.toString());
			FileInputStream fin = new FileInputStream (args.toString());
			//System.out.println();
			String currentLine = new DataInputStream(fin).readLine();
			
			parsedStr1 = currentLine.trim().split("  ");
			parsedStr2 = new String[lines-1];
			submatrix = new int[parsedStr1.length][lines-1];

			String[] stringRow =  null; 
			String remainder =  null;
			for(int j =0; j<lines-1; j++){
				currentLine = new DataInputStream(fin).readLine().trim();
				parsedStr2[j] = currentLine.substring(0, 1);
				remainder = currentLine.substring(1).replaceAll("  ", " +");
				stringRow =  remainder.trim().split(" ");

				int stringRowLength = stringRow.length;
				for(int i =0; i<stringRowLength; i++){
					if (stringRow[i].startsWith("+")) 
						stringRow[i] =  stringRow[i].substring(1);
				}
				for(int i =0; i<parsedStr1.length; i++)
					submatrix[i][j] = Integer.parseInt(stringRow[i]);
			}
			fin.close();
			args = null; fin = null; currentLine = null; stringRow = null; remainder = null;
			
			//printSubmatrix(submatrix, parsedStr1, parsedStr2);
		}
		catch (IOException e)
		{
			System.err.println ("Unable to read from submatrix file");
			System.exit(-1);
		}
		return submatrix;
	}

	private static void printSubmatrix(int[][] submatrix, String[] parsedStr1, String[] parsedStr2) {
		System.out.println("Printing blosum matrix...\n");
		System.out.format("%3s", "");
		for (int j=1; j<=parsedStr1.length;j++)
			System.out.format("%3s", parsedStr1[j-1]);
		System.out.println();
		StringBuffer result = new StringBuffer();
		String value;
		for (int i = 0; i < submatrix.length; ++i)
		{
			result.append(parsedStr2[i] + " [");
			for (int j = 0; j < submatrix[i].length; ++j){
				String.valueOf(submatrix[i][j]);
				value = String.format("%3s", submatrix[i][j]);
				result.append(value);
			}
			result.append("]\n");
		}
		System.out.println(result);
	}

	public static int countLinesInFile(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			while ((readChars = is.read(c)) != -1) {
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n')
						++count;
				}
			}
			return count;
		} finally {
			is.close();
		}
	}

	private static String[] prepareSequenceFromFile(String args) {
		String[] seqList = new String[2];
		try
		{
			FileInputStream fin = new FileInputStream (args.toString());
			//System.out.println("Opening file containing seqs to align...\n");
			//System.out.println("First Sequence:");
			// read first sequence
			seqList[0] = new DataInputStream(fin).readLine();
			//System.out.println(seqList[0]);
			// read empty line
			new DataInputStream(fin).readLine();
			//System.out.println(new DataInputStream(fin).readLine());
			// read second sequence
			//System.out.println("Second Sequence:");
			seqList[1] = new DataInputStream(fin).readLine();
			//System.out.println(seqList[1]);
			fin.close();		
		}
		catch (IOException e)
		{
			System.err.println ("Unable to read from sequence file!");
			System.exit(-1);
		}
		return seqList;
	}

}
