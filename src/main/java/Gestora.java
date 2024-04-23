import java.io.Serializable;
import java.util.ArrayList;

public class Gestora implements Serializable {
	ArrayList<Lectura> lecturas;
	//ArrayList<Imagen> imagenes;
	public int contCorrecta; // se usa en clases de Examen, forma barata de mezclar respuestas del multiple choice
	//private static java.awt.Image currentCompleta;
	public Gestora() {
		lecturas = new ArrayList<Lectura>();
		//imagenes = new ArrayList<Imagen>();
		//currentCompleta = null;
	}

	
	public static Gestora Recuperar() {
		Gestora g = (Gestora)Datos.Recuperar();
		if (g==null)
			g = new Gestora();
		return g;
	}
	
	public static boolean guardate(Gestora g) {
		return Datos.Guardar(g);
	}
	
	public void agregarLectura(Lectura l) {
		lecturas.add(l);
	}
	
	public ArrayList<String> getLecturas(){
		ArrayList<String> lecs = new ArrayList<String>();
		if (lecturas!=null) {
			for (int i = 0; i < lecturas.size(); i++) {
				lecs.add(lecturas.get(i).darTexto());
			}
		}
		return lecs;
	}
	
	public ArrayList<Lectura> dameListaLecturas(){
		return lecturas;
	}
	
	public int darContCorrecta() {
		return contCorrecta;
	}
	
	public void incrementarCont() {
		contCorrecta++;
	}
	
	public void setCont(int c) {
		contCorrecta = c;
	}
	
	

}
