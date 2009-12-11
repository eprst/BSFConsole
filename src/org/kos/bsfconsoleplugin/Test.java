package org.kos.bsfconsoleplugin;

import org.apache.bsf.BSFManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Console test
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public class Test {
	public static void main(final String[] args) {
		try {
			BSFManager.registerScriptingEngine("javascript", "org.apache.bsf.engines.javascript.JavaScriptEngine", null);
			BSFManager.registerScriptingEngine("beanshell", "bsh.util.BeanShellBSFEngine", null);
			BSFManager.registerScriptingEngine("xslt", "org.apache.bsf.engines.xslt.XSLTEngine", null);
			BSFManager.registerScriptingEngine("groovy", "org.codehaus.groovy.bsf.GroovyEngine", null);
			BSFManager.registerScriptingEngine("judoscript", "com.judoscript.BSFJudoEngine", null);
			BSFManager.registerScriptingEngine("netrexx", "org.apache.bsf.engines.netrexx.NetRexxEngine", null);
			BSFManager.registerScriptingEngine("ant", "org.kos.bsfconsoleplugin.languages.AntConsoleBSFEngine", null);

			final JFrame frame = new JFrame("test");
			frame.setBounds(100, 100, 500, 500);
			frame.getContentPane().setLayout(new BorderLayout());

			//final BSFConsolePanel panel = new BSFConsolePanel("ant", true);
			//final BSFConsolePanel panel = new BSFConsolePanel("javascript");
			//final BSFConsolePanel panel = new BSFConsolePanel("netrexx");
			//final BSFConsolePanel panel = new BSFConsolePanel("judoscript");
			final BSFConsolePanel panel = new BSFConsolePanel(null, new BSFLanguage("beanshell", "bsh.util.BeanShellBSFEngine"), true);
			
			//final BSFConsolePanel panel = new BSFConsolePanel("xslt% ", new BSFManager(), "xslt");
			//final BSFConsolePanel panel = new BSFConsolePanel(new BSFManager(), "groovy");
			frame.getContentPane().add(panel, BorderLayout.CENTER);
			frame.addWindowListener(
					new WindowAdapter() {
						@Override
						public void windowClosing(final WindowEvent e) {
							panel.getInterpreter().stop();
						}
					}
			);

			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			//new Thread(){public void run() {Thread.sleep(2000);System.out.println("haha");}}.start();

			frame.setVisible(true);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}