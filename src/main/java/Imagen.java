import java.io.Serializable;

public class Imagen implements Serializable {
	
	private byte[] imageData;
	private int idImagen;
	
	public Imagen(byte[] imageData, int idImagen) {
		super();
		this.imageData = imageData;
		this.idImagen = idImagen;
	}
	
	
}
