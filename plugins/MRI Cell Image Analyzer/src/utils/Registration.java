package utils;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

public class Registration extends Object {
	protected int translationX;
	protected int translationY;
	protected int rotation;
	protected double scalingX;
	protected double scalingY;
	protected float quality;
	
	public Registration() {
		super();
		translationX = translationY = rotation = 0;
		scalingX = scalingY = 1.0f;
	}

	public Registration(int x, int y, int rot, double scaleX, double scaleY) {
		super();
		this.translationX = x;
		this.translationY = y;
		this.rotation = rot;
		this.scalingX = scaleX;
		this.scalingY = scaleY;
	}
	
	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public double getScalingX() {
		return scalingX;
	}

	public void setScalingX(float scaling) {
		this.scalingX = scaling;
	}

	public int getTranslationX() {
		return translationX;
	}

	public void setTranslationX(int translationX) {
		this.translationX = translationX;
	}

	public int getTranslationY() {
		return translationY;
	}

	public void setTranslationY(int translationY) {
		this.translationY = translationY;
	}
	
	public Area getRectangle(int width, int height) {
		Area result;
		Shape shape;
		Rectangle myRect = new Rectangle(this.translationX, this.translationY, width, height);
		AffineTransform at = AffineTransform.getRotateInstance((Math.PI/180)*this.rotation,myRect.getCenterX(),myRect.getCenterY());
		shape = at.createTransformedShape(myRect);
		at = AffineTransform.getScaleInstance(this.getScalingX(), this.getScalingY());
		shape = at.createTransformedShape(myRect);
		result = new Area(shape);
		return result;
	}
	
	public Area overlappingArea(Registration other, int width, int height) {
		Area myRect = this.getRectangle(width, height);
		Area otherRect = other.getRectangle(width, height);
		myRect.intersect(otherRect);
		return myRect;
	}

	public double getScalingY() {
		return scalingY;
	}

	public void setScalingY(double scalingY) {
		this.scalingY = scalingY;
	}

	public float getQuality() {
		return quality;
	}

	public void setQuality(float quality) {
		this.quality = quality;
	}

	public void setScalingX(double scalingX) {
		this.scalingX = scalingX;
	}
}
