package models;

public class UiDecorElement {

	public String image;
	public int x;
	public int y;
	public boolean background;

	public UiDecorElement(final DecorElement element) {
		this.image = element.image;
		this.x = element.x;
		this.y = element.y;
		this.background = element.background;
	}

	@Override
	public String toString() {
		return "UiDecorElement [image=" + image + ", x=" + x + ", y=" + y + "]";
	}

}
