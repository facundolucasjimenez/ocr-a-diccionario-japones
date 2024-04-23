import java.util.Scanner;
import javax.swing.SwingUtilities;
import com.github.kwhat.jnativehook.GlobalScreen;

public class Main {
	
	private static Gestora g = new Gestora(); // objeto Gestora guarda listas de lecturas

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		Configuracion.os = new Scanner(System.getProperty("os.name")).next().toLowerCase();
		
        GlobalScreen.registerNativeHook();

        g = Gestora.Recuperar();
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                crearUI();
            }
        });
		
	}
	
	private static void crearUI() {
		try {
			GlobalScreen.addNativeKeyListener(new MenuPrincipal(g));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


