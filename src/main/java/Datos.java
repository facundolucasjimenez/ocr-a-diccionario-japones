import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Datos {

	public static boolean Guardar(Object o)
    {
        boolean ok = false;
        try
        {
        	FileOutputStream fileOut=new FileOutputStream("instanciaGestora.obj");
        	
        	ObjectOutputStream salida = new ObjectOutputStream(fileOut);
        	
        	salida.writeObject(o);
        	
        	salida.close();
        	
        	ok = true;
        }
        catch (Exception ex)
        {
            ok = false; // redundante
        }
        return ok;
    }
    public static Object Recuperar()
    {
        try
        {
        	FileInputStream fileIn=new FileInputStream("instanciaGestora.obj");
        	
        	ObjectInputStream entrada=new ObjectInputStream(fileIn);

        	Gestora g = (Gestora)entrada.readObject();
        	
        	entrada.close();
        	
        	return g;
        }
        catch (Exception ex)
        {
            return null;
        }
    }
	
}
