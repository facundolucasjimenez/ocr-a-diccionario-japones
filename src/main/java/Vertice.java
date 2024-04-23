import com.google.cloud.vision.v1.EntityAnnotation;

public class Vertice {
	
	private int x;
	private int y;
	private EntityAnnotation anotacion;
	
	public Vertice(int x, int y, EntityAnnotation anotacion) {
		this.x=x;
		this.y=y;
		this.anotacion=anotacion;
	}
	
	public int dameX() {
		return x;
	}
	
	public int dameY() {
		return y;
	}
	
	public EntityAnnotation dameAnotacion() {
		return anotacion;
	}
	
	public void setX(int unX) {
		x = unX;
	}
	
	public void setY(int unY) {
		y = unY;
	}
	
	public void setAnotacion(EntityAnnotation unaAnotacion) {
		anotacion = unaAnotacion;
	}
	
	public void truncarEjes() {
		x = x/10;
		y = y/10;
	}
	
	public void redondearEjes() {
		x = 100*(Math.round(x/100));
		y = 5*(Math.round(y/5));
	}
}
