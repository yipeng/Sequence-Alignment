import java.util.ArrayList;
import java.util.Arrays;

public class SW3 {

	private int initP, extP;
	private String str1, str2;
	private String[] seqList;
	private int length1, length2;
	private int[][] blosum;
	private ArrayList<String> blosumRow, blosumCol;
	private Tracer[][] score;
	private Tracer[][] gIx;
	private Tracer[][] gIy;
	private static final int M_UP = 1;
	private static final int IX_UP = 2;
	private static final int M_DIAG = 8;
	private static final int IX_DIAG = 16;
	private static final int IY_DIAG = 32;
	private static final int M_LEFT = 64;
	private static final int IY_LEFT = 128;

	public SW3(String[] seqList, int[][] matrixList, int initP,
			int extP, String[] parsedStr1, String[] parsedStr2) {
		blosumRow = new ArrayList<String>(Arrays.asList(parsedStr2));
		blosumCol = new ArrayList<String>(Arrays.asList(parsedStr1));
		this.blosum = matrixList;
		this.initP = initP;
		this.extP = extP;
		this.seqList = seqList;
		str2 = seqList[0];
		str1 = seqList[1];
		this.length2 = seqList[0].length();
		this.length1 = seqList[1].length();
		score = new Tracer[length2+1][length1+1];

		scoreSequences();
		//printScoreMatrix();
		printAlignedSequences(findTraceStart());
	}

	private void scoreSequences() {
		int i, j;
		score[0][0] = new Tracer(0, 0, 0);
		Tracer[][] Ix = new Tracer[length2+1][length1+1];
		Tracer[][] Iy = new Tracer[length2+1][length1+1];
		Iy[0][0] = new Tracer(0, 0, 0);
		Ix[0][0] = new Tracer(0, 0, 0);

		for (int k = 0; k <= length2; k++) {
			for (int k2 = 0; k2 <= length1; k2++) {
				score[k][k2] = new Tracer(0, 0, 0);
			}
		}
		for (i = 1; i <= length2; i++) {
			score[i][0] = new Tracer(i, 0, 0);
			Ix[i][0] = new Tracer(i, 0);
			Iy[i][0] = new Tracer(i, 0);
		}
		for (j = 1; j <= length1; j++) {
			score[0][j] = new Tracer(0, j, 0);
			Ix[0][j] = new Tracer(0, j);
			Iy[0][j] = new Tracer(0, j);
		}
		
		for (i = 1; i <= length2; i++) {
			for (j = 1; j <= length1; j++) {
				// Score matrix Ix(i, j)
				int IxNewGap = score[i-1][j].getScore() - initP;
				int IxContGap = Ix[i-1][j].getScore() - extP;
				if (IxNewGap > IxContGap){
					Ix[i][j] = new Tracer(i, j, IxNewGap, M_UP);
				}else {
					Ix[i][j] = new Tracer(i, j, IxContGap, IX_UP);
				}
				// Score matrix Iy(i, j)
				int IyNewGap = score[i][j-1].getScore() - initP;
				int IyContGap = Iy[i][j-1].getScore() - extP;
				if (IyNewGap > IyContGap){
					Iy[i][j] = new Tracer(i, j, IyNewGap, M_LEFT);
				}else {
					Iy[i][j] = new Tracer(i, j, IyContGap, IY_LEFT);
				}

				// Score matrix M(i, j)
				int submaxScore = submaxScore(i, j);
				int MScore = score[i - 1][j - 1].getScore() + submaxScore;
				int IxScore = Ix[i-1][j-1].getScore() + submaxScore;		
				int IyScore = Iy[i-1][j-1].getScore() + submaxScore;
				int maxDiag = max(MScore, IxScore, IyScore, 0);

				if (IxScore==maxDiag){
					score[i][j] =  new Tracer(i, j, maxDiag, IX_DIAG);
				}else if (MScore==maxDiag){
					score[i][j] =  new Tracer(i, j, maxDiag, M_DIAG);
				}
				else if(IyScore==maxDiag){
					score[i][j] =  new Tracer(i, j, maxDiag, IY_DIAG);
				}
				else if(0==maxDiag){
					score[i][j] =  new Tracer(i, j, maxDiag, 0);
				}

				// Rescore matrix M(i, j) with Max
				if (Ix[i][j].getScore() >= score[i][j].getScore() && Ix[i][j].getScore() >= Iy[i][j].getScore() && Ix[i][j].getScore() >= 0){
					score[i][j] =  new Tracer(i, j, max(score[i][j].getScore(), Ix[i][j].getScore(), Iy[i][j].getScore()), Ix[i][j].getPrev());
				}else if (score[i][j].getScore() >= Ix[i][j].getScore() && score[i][j].getScore() >= Iy[i][j].getScore() && score[i][j].getScore() >= 0){
					score[i][j] =  new Tracer(i, j, max(score[i][j].getScore(), Ix[i][j].getScore(), Iy[i][j].getScore()), score[i][j].getPrev());
				}
				else if (Iy[i][j].getScore() >= score[i][j].getScore() && Iy[i][j].getScore() >= Ix[i][j].getScore() && Iy[i][j].getScore() >= 0){
					score[i][j] =  new Tracer(i, j, max(score[i][j].getScore(), Ix[i][j].getScore(), Iy[i][j].getScore()), Iy[i][j].getPrev());
				}
			}
		}
		gIx =  Ix;
		gIy =  Iy;
	}

	private int max(int x1, int x2, int x3) {
		return max(x1, max(x2, x3)); 
	}

	private int max(int x1, int x2, int x3, int x4){ 
		return max(max(x1, x2), max(x3, x4)); 
	}

	private int max(int x1, int x2) {
		return (x1 > x2 ? x1 : x2);
	}

	private int submaxScore(int i, int j) {
		return blosum[blosumCol.indexOf(String.valueOf(seqList[0].charAt(i-1)))][blosumRow.indexOf(String.valueOf(seqList[1].charAt(j-1)))]; 
	}

	public void printScoreMatrix()
	{
		System.out.println("\nPrinting M...\n");
		System.out.format("%6s", "");
		for (int j=1; j<=length1;j++)
			System.out.format("%5s", str1.charAt(j-1));
		System.out.println("\n");
		for (int i=0; i<=length2; i++)
		{
			if (i>0) System.out.print(str2.charAt(i-1));
			else System.out.print(" ");
			for (int j=0; j<=length1; j++)
				System.out.format("%5d", score[i][j].getScore());
			System.out.println();
		}
		System.out.println();
	}

	public int[] findTraceStart() {
		int maxScore = 0, x = 0, y = 0;
		for (int i = 1; i <= length2; i++) {
			for (int j = 1; j <= length1; j++) {
				if (score[i][j].getScore() >= maxScore) {
					maxScore = score[i][j].getScore();
					x = i; y = j;
				}
			}
		}
		int[] coords = {x, y};
		return coords;
	}

	private void printAlignedSequences(int[] coords) {
		int i = coords[0], j = coords[1];
		String al1 = "", al2 = "";
		Tracer lastTracer = score[i][j];

		while(lastTracer.getScore() != 0) {
			switch(lastTracer.getPrev()) {
			case M_UP:
				al1 = str2.charAt(i-1) + al1;
				al2 = '-' + al2;
				lastTracer = score[--i][j];
				break;
			case M_DIAG	:
				al1 = str2.charAt(i-1) + al1;
				al2 = str1.charAt(j-1) + al2;
				lastTracer = score[--i][--j];
				break;
			case M_LEFT:
				al1 = '-' + al1;
				al2 = str1.charAt(j-1) + al2;
				lastTracer = score[i][--j];
				break;
			case IX_UP:
				al1 = str2.charAt(i-1) + al1;
				al2 = '-' + al2;
				lastTracer = gIx[--i][j];
				break;
			case IX_DIAG:
				al1 = str2.charAt(i-1) + al1;
				al2 = str1.charAt(j-1) + al2;
				lastTracer = gIx[--i][--j];
				break;
			case IY_DIAG:
				al1 = str2.charAt(i-1) + al1;
				al2 = str1.charAt(j-1) + al2;
				lastTracer = gIy[--i][--j];
				break;
			case IY_LEFT:
				al1 = '-' + al1;
				al2 = str1.charAt(j-1) + al2;
				lastTracer = gIy[i][--j];
				break;
			}	
		}
		System.out.println(al1);
		System.out.println();
		System.out.println(al2);
	}
}
