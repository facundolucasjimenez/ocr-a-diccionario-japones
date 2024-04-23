import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

public class Examen extends JFrame implements ActionListener {

	private JRadioButton radio1, radio2, radio3, radio4; // radio1 siempre va a ser la correcta, solo va a variar el orden de añadido
	private JButton verificar, salir;
	private String kanji;
	private ArrayList<Lectura> lecsAux;
	private static Lectura lecAux;
	private ArrayList<Lectura> lecturas;
	private static char[] unCaracter;
	private static ArrayList<Lectura> unaLectura;
	private static String opcion1, opcion2, opcion3, opcion4;
	private JLabel titulo, subtitulo;
	private static char[] correcta = {3,2,4,1,3,3,2,4,1,3,1,1,2,3,1,4,4,3,4,4,2,4,1,1,2,3,2,1,3,4,3,3}; // forma barata de cambiar posición de la opción correcta
	public static int i;
	private int j;
	private int cant;
	private int indiceUn;
	private Gestora g;
	private JButton btnNewButton;
	
	public Examen(ArrayList<Lectura> lecs, int cantt, Gestora g, boolean banderita) throws FileNotFoundException, IOException {
		
		// sobre los parámetros en java: los primitive types (int, byte, char) se pasan por valor y los Objects (String, ArrayList) por referencia
		this.g = g; // entonces hago referencia a la única instancia de gestora que debe existir a lo largo de la ejecución
		this.lecturas = lecs;
		//unCaracter = new ArrayList<String>(); 
		this.cant = cantt;
		setIconImage(ImageIO.read(new FileInputStream(new File("images/icon.gif"))));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setBounds(100, 100, 411, 463);
		setTitle("Last "+(cant-1));
		getContentPane().setLayout(null);
		
		if (!banderita) { // banderita es para que construya la sublista una sola vez por serie, por eso los componentes "unX" son static
			unCaracter = new char[cant];
			unaLectura = new ArrayList<Lectura>();
			indiceUn = 0;
			int k = 0;
			char ch = lecturas.get(0).darTexto().charAt(0);
			while (j<cant-1) {
				while (!esKanji(ch) && k<lecturas.get(j).darTexto().length()-1){
					System.out.println("K ES "+k);
					System.out.println("adentro del while, "+lecturas.get(j).darTexto().charAt(k)+" es kanji? "+esKanji(lecturas.get(j).darTexto().charAt(k)));
					k++;
					ch = lecturas.get(j).darTexto().charAt(k);
					System.out.println("lecturas.get(j).darTexto da "+ lecturas.get(j).darTexto()+" con j = "+j);
					
				}
				if (!esKanji(ch) && k == lecturas.get(j).darTexto().length()-1) {
					// si esta lectura no tuvo ningún kanji, no aumento el indiceUn para que no haya gaps en la sublista y me sigo fijando en la siguiente
					cant++; // además aumento en 1 cant para compensar por el ciclo perdido
					ch = lecturas.get(j+1).darTexto().charAt(0);
					j++;
					k=0;
				}
				else {
					if (j!=cant-2) { // pensé que era j!=cant-1 pero de la asignación en la última posi se encarga el else de abajo, estaba haciendo 1 de más
						unCaracter[indiceUn] = ch;
						unaLectura.add(lecturas.get(j));
						System.out.println("asigné a unCaracter["+j+"] el ch "+ch);
						indiceUn++;
						ch = lecturas.get(j+1).darTexto().charAt(0);
						j++;
						k=0;
					}
					else {
						unCaracter[indiceUn] = ch;
						System.out.println("asigné a unCaracter["+j+"] el ch "+ch);
						unaLectura.add(lecturas.get(j));
						j++;
					}
				}
				
			}
			
			//for (int w = 0; w<unCaracter.length-1; w++)
				//System.out.println(" en posi "+w+" un caracter "+unCaracter[w]);
			
		}
		
		subtitulo = new JLabel(Character.toString(unCaracter[i]));
		subtitulo.setHorizontalAlignment(SwingConstants.CENTER);
		subtitulo.setFont(new Font("Yu Gothic", Font.BOLD, 70));
		subtitulo.setBounds(99, 58, 185, 122);
		getContentPane().add(subtitulo);
		
		lecsAux = new ArrayList<Lectura>(); //acá quiero hacer una copia local para modificar y que no afecte al original, no me queda otra que copiar
		for (int a2 = 0 ; a2<lecturas.size(); a2++) { //los contenidos de uno a otro, si hago "lecsAux = lecturas" lecsAux referenciaria el mismo objeto
			lecsAux.add(lecturas.get(a2));
		}
		
		opcion1 = unaLectura.get(i).darSignKanji();
		lecsAux.remove(unaLectura.get(i));
		
		lecAux = darCualquiera(lecsAux);
		opcion2 = lecAux.darSignKanji();
		lecsAux.remove(lecAux);
		
		lecAux = darCualquiera(lecsAux);
		opcion3 = lecAux.darSignKanji();
		lecsAux.remove(lecAux);
		
		lecAux = darCualquiera(lecsAux);
		opcion4 = lecAux.darSignKanji();
		
		radio1 = new JRadioButton(opcion1);
		radio2 = new JRadioButton(opcion2);
		radio3 = new JRadioButton(opcion3);
		radio4 = new JRadioButton(opcion4);
		
		int opcion = correcta[g.darContCorrecta()]; // contador para recorrer la lista de posiciones "aleatorias" en las que va a estar la correcta
													// (de aleatorio no tiene nada, pero se entiende)
		switch(opcion){ // posiciona según el índice que de la gestora con darContCorrecta. Al guardarse en la gestora, se recuerda para próx ejec.
			case 1:{
				radio1.setBounds(20, 214, 350, 23);
				radio2.setBounds(20, 247, 350, 23);
				radio3.setBounds(20, 282, 350, 23);
				radio4.setBounds(20, 315, 350, 23);
			}
			break;
			case 2:{
				radio1.setBounds(20, 247, 350, 23);
				radio2.setBounds(20, 214, 350, 23);
				radio3.setBounds(20, 282, 350, 23);
				radio4.setBounds(20, 315, 350, 23);
			}
			break;
			case 3:{
				radio1.setBounds(20, 282, 350, 23);
				radio2.setBounds(20, 214, 350, 23);
				radio3.setBounds(20, 247, 350, 23);
				radio4.setBounds(20, 315, 350, 23);
			}
			break;
			case 4:{
				radio1.setBounds(20, 315, 350, 23);
				radio2.setBounds(20, 214, 350, 23);
				radio3.setBounds(20, 282, 350, 23);
				radio4.setBounds(20, 247, 350, 23);
			}
			break;
		}
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(radio1);
		bg.add(radio2);
		bg.add(radio3);
		bg.add(radio4);
		
		radio1.addActionListener(this);
		radio2.addActionListener(this);
		radio3.addActionListener(this);
		radio4.addActionListener(this);
		
		verificar = new JButton("Verify answer");
		verificar.setBounds(125,500,300,50);
		verificar.addActionListener(this);
		
		salir = new JButton("Exit");
		salir.setBounds(325,500,300,50);
		salir.addActionListener(this);
		
		//this.add(titulo);
		getContentPane().add(radio1);
		getContentPane().add(radio2);
		getContentPane().add(radio3);
		getContentPane().add(radio4);
		//this.add(verificar);
		//this.add(salir);
		
		JLabel lblNewLabel = new JLabel((i+1)+"/"+(cantt-1));
		lblNewLabel.setFont(new Font("Yu Gothic", Font.BOLD, 30));
		lblNewLabel.setBounds(168, 368, 92, 67);
		getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("2nd to last:");
		lblNewLabel_1.setFont(new Font("Yu Gothic", Font.BOLD, 14));
		lblNewLabel_1.setBounds(10, 11, 91, 23);
		getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel(Character.toString(unaLectura.get(i).darListaAciertos()[1]));
		if (unaLectura.get(i).darListaAciertos()[1] == 'O')
			lblNewLabel_2.setForeground(Color.BLUE);
		else
			if ((unaLectura.get(i).darListaAciertos()[1] == 'X'))
				lblNewLabel_2.setForeground(Color.RED);
			else
				lblNewLabel_2.setForeground(Color.BLACK);
		lblNewLabel_2.setFont(new Font("Arial", Font.BOLD, 30));
		lblNewLabel_2.setBounds(37, 31, 26, 36);
		getContentPane().add(lblNewLabel_2);
		
		JLabel lblNewLabel_1_1 = new JLabel("Last:");
		lblNewLabel_1_1.setFont(new Font("Yu Gothic", Font.BOLD, 14));
		lblNewLabel_1_1.setBounds(10, 67, 91, 30);
		getContentPane().add(lblNewLabel_1_1);
		
		JLabel lblNewLabel_2_1 = new JLabel(Character.toString(unaLectura.get(i).darListaAciertos()[0]));
		if (unaLectura.get(i).darListaAciertos()[0] == 'O')
			lblNewLabel_2_1.setForeground(Color.BLUE);
		else
			if ((unaLectura.get(i).darListaAciertos()[0] == 'X'))
				lblNewLabel_2_1.setForeground(Color.RED);
			else
				lblNewLabel_2_1.setForeground(Color.BLACK);
		lblNewLabel_2_1.setFont(new Font("Arial", Font.BOLD, 30));
		lblNewLabel_2_1.setBounds(37, 86, 26, 36);
		getContentPane().add(lblNewLabel_2_1);
		
		btnNewButton = new JButton("Context");
		btnNewButton.setBounds(275, 31, 89, 23);
		getContentPane().add(btnNewButton);
		btnNewButton.addActionListener(this);
		btnNewButton.setVisible(false);
		
		setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object source = e.getSource();
		if (source == btnNewButton) {
			//Image imag = unaLectura.get(i).darImagen();
			//ImageIcon Eii = new ImageIcon(imag);
			//JLabel Ejl = new JLabel(Eii);
			
			JFrame paraContexto = new JFrame();
			paraContexto.setBounds(800, 600, 200, 200);
			//paraContexto.setExtendedState(JFrame.MAXIMIZED_BOTH);
			//paraContexto.setUndecorated(true);
			//paraContexto.add(Ejl);
			paraContexto.setVisible(true);
		}
		else {
			
		
		/*if (source == verificar) {
			if (!radio1.isSelected() && !radio2.isSelected() && !radio3.isSelected() && !radio4.isSelected())
				JOptionPane.showMessageDialog(this, "NO HAY NINGUNA OPCION SELECCIONADA!");
			else {
				if (i<cant) {*/
						if (source == radio1) {
							unaLectura.get(i).setearProbabilidad(true);
							JOptionPane.showMessageDialog(this, "Correct!");
						}
						else {
							unaLectura.get(i).setearProbabilidad(false);
							JOptionPane.showMessageDialog(this, "Incorrect!");
						}
						g.incrementarCont();
						if (g.darContCorrecta()==correcta.length-1) {
							g.setCont(0);
						}
						i++;
						System.out.println("i es "+i);
						System.out.println("contCorrecta es "+g.darContCorrecta());
						if (i<cant) {
						if (i!=cant-1)
							try {
								new Examen(lecturas, cant, g, true);
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						this.dispose();
				/*}
				else
					this.dispose();
			}
		}
		else{
			g.incrementarCont();
			if (g.darContCorrecta()==correcta.length-1) {
				g.setCont(0);
			}
			System.out.println("contCorrecta es "+g.darContCorrecta());
			this.dispose();
		}*/
		}
	}
	
	public static boolean esKanji(final char c) { // para descartar caracteres que no sean kanji
	    if ((Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
	            || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)
	            || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B)
	            || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS)
	            || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS)
	            || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT)
	            || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION)
	            || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.ENCLOSED_CJK_LETTERS_AND_MONTHS)) {
	        return true;
	    }
	    return false;
	}
	
	private Lectura darCualquiera(ArrayList<Lectura> lecs) { // menos unaLectura[i] y cualquier lectura que haya salido elegida para otra opción en [i]
		int indice = new Random().nextInt(lecs.size());
		while (lecs.get(indice).darSignKanji() == "") {
			indice = new Random().nextInt(lecs.size());
		}
		return lecs.get(indice);
	}
	
}
