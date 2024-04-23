import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
 
public class CapturaCompleta {
	
	static String direccion = Configuracion.Path+"COMPLETA.PNG";
	
	public static Robot robot; // no hace falta ni public (el método está para eso) ni static (creo una instancia)
	private static String format;
	private static String fileName;
	public static Rectangle screenRect;
	private static BufferedImage screenFullImage;
	
	public CapturaCompleta() throws AWTException {
		robot = new Robot();
        screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        format = "PNG";
        fileName = "COMPLETA."+format;
	}
 
    public void sacarCaptura() {
        try {
        	if (SwingUtilities.isEventDispatchThread())
            	System.out.println("EDT, en CapturaCompleta");
            else
            	System.out.println("NO EDT, en CapturaCompleta");
        	
            Configuracion.incrementarIndiceCaptura();
            
            if (screenFullImage!=null)
            	screenFullImage.flush();
            screenFullImage = robot.createScreenCapture(screenRect);
            
            ImageIO.write(screenFullImage, format, new File(fileName));
             
            System.out.println("Guardada");
            
            System.gc();
        } catch (IOException ex) {
            System.err.println(ex);
        }
           
    }
    
}