package tools.magicWand;

public interface MagicWandBackstoreImageCreator extends Runnable {

	abstract public void run();

	abstract public void showOptions();

	abstract public String name();
}