
public class Tracer {
	private int i, j, score, prev;
	// SW top row left col
	public Tracer(int i, int j) {
		this.setI(i);
		this.setJ(j);
		score = Integer.MIN_VALUE/2;
		prev = 0;
	}
	// SW zeros
	public Tracer(int i, int j, int score) {
		this.setI(i);
		this.setJ(j);
		this.score = score;
		this.prev = 0;
	}
	// SW filled
	public Tracer(int i, int j, int score, int prev) {
		this.setI(i);
		this.setJ(j);
		this.score = score;
		this.prev = prev;
	}

	public int getScore() {
		return this.score;
	}
	public int getPrev() {
		return this.prev;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public void setPrev(int prev) {
		this.prev = prev;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getI() {
		return i;
	}

	public void setJ(int j) {
		this.j = j;
	}

	public int getJ() {
		return j;
	}
}
