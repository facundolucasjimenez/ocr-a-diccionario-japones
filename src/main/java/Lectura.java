import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Lectura implements Serializable {
	
	private int idLectura;
	private String texto;
	//transient private BufferedImage imagen;
	private ArrayList<Definicion> definiciones;
	private String defPrimerKanji;
	private char[] aciertos;
	
	public Lectura(int unIdLectura, String unTexto, BufferedImage unaImagen, ArrayList<Definicion> unaLista) throws IOException {
		idLectura = unIdLectura;
		texto = unTexto;
		//imagen = unaImagen;
		definiciones = unaLista;
		
		char ch = texto.charAt(0);
		int k = 0;
		while (!Examen.esKanji(ch) && k<texto.length()-1)
		{
			k++;
			ch = texto.charAt(k);
		}
		if (!Examen.esKanji(ch) && k == texto.length()-1)
			defPrimerKanji = "";
		else
			defPrimerKanji = Pagina.darSignificado(Character.toString(ch));
		
		aciertos = new char[2];
		aciertos[0] = 'N';
		aciertos[1] = 'N';
		
	}
	
	public String darTexto() {
		return texto;
	}
	
	public String darSignKanji() {
		return defPrimerKanji;
	}
	
	public int devolverProbabilidad() { // 3x3 = 9 posibilidades - 2 = 7 porque de la forma que actualizamos, nunca va a haber [0]=N y [1]==OoX
		int valor = 0;
		if ((aciertos[0]=='N' && aciertos[1]=='N') || (aciertos[0]=='X' && aciertos[1]=='N') || (aciertos[0]=='O' && aciertos[1]=='N')
				|| (aciertos[0]=='X' && aciertos[1]=='O') || (aciertos[0]=='O' && aciertos[1]=='X'))
			valor = 3;
		else
			if (aciertos[0]=='X' && aciertos[1]=='X')
				valor = 30;
			else
				if (aciertos[0]=='O' && aciertos[1]=='O')
					valor = 1;
		
		return valor;
			
	}
	
	public char[] darListaAciertos() {
		return aciertos;
	}
	
	public void setearProbabilidad(boolean acerto) {
		aciertos[1] = aciertos[0];
		if (acerto)
			aciertos[0] = 'O';
		else
			aciertos[0] = 'X';
	}
	
	/*public Image darImagen() {
		return imagen;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(1); // how many images are serialized?
        
        ImageIO.write(imagen, "png", out); // png is lossless
        
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        final int imageCount = in.readInt();
        imagen = ImageIO.read(in); //new BufferedImage(imageCount);
    }*/
}
