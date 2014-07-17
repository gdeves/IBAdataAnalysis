package workspace.neuronJ;

import neuronJgui.TracingToolbar;
import imagejProxies.Proxy;

public class TracingToolbarWorkspace {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Proxy proxy = new Proxy();
		TracingToolbar trace = (TracingToolbar) proxy.newObject(TracingToolbar.class);
		System.out.println(trace.getBounds());
	}

}
