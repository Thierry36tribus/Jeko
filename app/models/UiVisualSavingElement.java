package models;

public class UiVisualSavingElement {

	public UiVisualSavingElement(final int x, final int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public int x;
	public int y;

	@Override
	public String toString() {
		return "UiVisualSavingElement [x=" + x + ", y=" + y + "]";
	}

}
