/*
File:   ScreenCap.java 
Copyright 2018, Kevin Medzorian

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and 
to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of 
the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO 
THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.MouseInfo;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;
import javax.swing.SwingUtilities;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.Timer;

public class MenuPrincipal extends JFrame implements NativeKeyListener, ActionListener, Runnable { 
    
	static String nuevaUrl;
	static Scene sc;
	public static int scrollY;
	static int animacion;
	static Image image;
	static ImageIcon imageIcon;
	static BufferedImage imagen, imagenNoClick, imgBuffer, imageTake;
	static Rectangle rect;
	static FrameArea cap;
	static JLabel jlII;
	static Gestora g;
	public static JFrame paraBr;
    static JFXPanel paraFX;
    static WebView navegador;
    static WebEngine webEngine;
    static CapturaCompleta cc;
	JButton btnMostrarXRecientes, btnAbrirEstatico, btnPracticarUltimos, zoom, mostrarCont, btnPracticarAleatorio;
	JComboBox comboBoxCantRecientes, comboBoxDic;
	JCheckBox chckbxVertical, chckbxTraerAlFrente, altTrainedData, chckbxDrag, chckbxNoMandarBandeja, exit, tesseractMode, clickDrag;
	JComboBox<String> comboBoxPracticarUltimos, comboBoxOcrEng, comboBoxPageSeg, comboBoxPracticarAleatorio;
    MenuItem exitItem, openItem;
    TrayIcon icon;
    JLabel jl;
    ImageIcon ii;
    JFrame paraMostrarCompleta;
    
    public MenuPrincipal(Gestora g) throws Exception {  
        Configuracion.Path = getClass().getProtectionDomain().getCodeSource().getLocation().toString().substring(6); // path donde está esta clase sin los primeros 6 caracteres ("file:/")
        Configuracion.Path = Configuracion.Path.substring(0, Configuracion.Path.length()-1); // saca la barra del final "/", porque beginIndex es 0 y endIndex es la longitud menos 1
        Configuracion.Path = Configuracion.Path.substring(0, Configuracion.Path.lastIndexOf("/")); // devuelve todo antes del ultimo "/", porque lastIndexOf devuelve el valor índice de la última ocurrencia de la cadena
        
        Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);
        
        if (Configuracion.os.equals("windows")) {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }

        Configuracion.leerConfig();
        OpenMenu();
        
        this.g = g;
        cc = new CapturaCompleta(); // todavía no saco captura pero instancio objeto
        paraBr = new JFrame();
	    paraFX = new JFXPanel();
	    
	    cap = new FrameArea(500,500);
		
	    //paraBr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); el default va a ser HIDE_ON_CLOSE
		paraBr.add(paraFX);
	    paraBr.pack();
	    paraBr.setBounds(Configuracion.posX,Configuracion.posY,Configuracion.ancho,Configuracion.alto);
	    //paraBr.setVisible(true);
	    
		Platform.runLater(()->{
			   	navegador = new WebView();
			   	webEngine = navegador.getEngine();
			   	navegador.setZoom(Configuracion.zoom);
			   	webEngine.load("https://jisho.org/search/null");
				Scene scene = new Scene(navegador, 200, 200);
			   	paraFX.setScene(scene);
		});
		
    }
    
    @Override
    public void run() {
    	
    }

    public void OpenTray() throws IOException {
    	//WindowListener[] wa = this.getListeners(WindowListener.class);
	    //System.out.println("TENGO "+wa.length+" LISTENERS");
        if (SystemTray.isSupported()) {

            PopupMenu popup = new PopupMenu();
            SystemTray st = SystemTray.getSystemTray();

            image = null;
            //if (SwingUtilities.isEventDispatchThread())
            	//System.out.println("EDT, open tray");
            //else
            	//System.out.println("NO EDT, open tray");

            try {
                image = ImageIO.read(new FileInputStream(new File("images/tray.gif")));
            } catch (NullPointerException e) {
                Configuracion.Log("/images/tray.gif not found - replacing with blank image");

                BufferedImage bi = new BufferedImage(1, 1, BufferedImage.OPAQUE);
                bi.setRGB(0, 0, 255);

                try {
                    ImageIO.write(bi, "GIF", new File("backup.gif"));
                } catch (IOException excep) {
                    Configuracion.Log("/images/tray.gif is an invalid path - closing");
                    System.exit(1);
                }

                try {
                    image = Toolkit.getDefaultToolkit().getImage("backup.gif");
                } catch (NullPointerException ee) {
                    Configuracion.Log("backup.gif not found - endless loop detected - closing");
                    System.exit(1);
                }
            }

            icon = new TrayIcon(image, "ocr-a-diccionario", popup);

            icon.setImageAutoSize(true);

            exitItem = new MenuItem("Exit");
            openItem = new MenuItem("Open");

            exitItem.addActionListener(this);
            openItem.addActionListener(this);

            popup.add(openItem);
            popup.add(exitItem);

            try {
                st.add(icon);
            } catch (Exception e) {
                System.out.println(e.toString());
            }

        }
    }

    /* sobre JFrame vs JPanel
    https://www.educba.com/jpanel-vs-jframe/
     */
    
    public void OpenMenu() throws IOException {
    	jlII = new JLabel();
    	File fileImagen = new File("images/"+Configuracion.pagina+".png");
    	try {
			imgBuffer = ImageIO.read(new FileInputStream(fileImagen));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if (SwingUtilities.isEventDispatchThread())
        	System.out.println("EDT, open menu");
        else
        	System.out.println("NO EDT, open menu");
        imageIcon = new ImageIcon(imgBuffer);
        jlII.removeAll();
        jlII.setIcon(imageIcon);
        jlII.setBounds(230, 245, 200, 200);
        add(jlII);
        jlII.setVisible(true);
    	
    	setTitle("ocr a diccionario");
		setIconImage(ImageIO.read(new FileInputStream(new File("images/icon.gif"))));
		setResizable(true);
		setBounds(100, 100, 411, 463);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		zoom = new JButton("zoom");
		zoom.setFont(new Font("Arial", Font.PLAIN, 10));
		zoom.setBounds(1, 1, 20, 20);
		getContentPane().add(zoom);
		
		btnMostrarXRecientes = new JButton("Show last:");
		btnMostrarXRecientes.setFont(new Font("Arial", Font.PLAIN, 18));
		btnMostrarXRecientes.setBounds(70, 95, 185, 25);
		getContentPane().add(btnMostrarXRecientes);
		
		comboBoxCantRecientes = new JComboBox();
		comboBoxCantRecientes.addItem("5");
		comboBoxCantRecientes.addItem("10");
		comboBoxCantRecientes.setFont(new Font("Arial", Font.PLAIN, 13));
		comboBoxCantRecientes.setBounds(262, 95, 45, 24);
		getContentPane().add(comboBoxCantRecientes);
		
		chckbxVertical = new JCheckBox("Vertical");
		chckbxVertical.setFont(new Font("Arial", Font.PLAIN, 14));
		chckbxVertical.setBounds(6, 313, 125, 25);
		getContentPane().add(chckbxVertical);
		
		JLabel lblOpcionTraductor = new JLabel("Dictionary/translator:");
		lblOpcionTraductor.setFont(new Font("Arial", Font.PLAIN, 14));
		lblOpcionTraductor.setBounds(10, 345, 146, 17);
		getContentPane().add(lblOpcionTraductor);
		
		comboBoxDic = new JComboBox();
		comboBoxDic.addItem("jisho");
		comboBoxDic.addItem("deepL");
		comboBoxDic.setBounds(10, 373, 135, 25);
		getContentPane().add(comboBoxDic);
		
		btnAbrirEstatico = new JButton("Show window");
		btnAbrirEstatico.setFont(new Font("Arial", Font.PLAIN, 18));
		btnAbrirEstatico.setBounds(70, 46, 185, 25);
		getContentPane().add(btnAbrirEstatico);
		
		btnPracticarUltimos = new JButton("Practice last:");
		btnPracticarUltimos.setFont(new Font("Arial", Font.PLAIN, 18));
		btnPracticarUltimos.setBounds(70, 144, 185, 24);
		getContentPane().add(btnPracticarUltimos);
		
		chckbxTraerAlFrente = new JCheckBox("Bring window to front");
		chckbxTraerAlFrente.setFont(new Font("Arial", Font.PLAIN, 14));
		chckbxTraerAlFrente.setBounds(6, 287, 171, 23);
		getContentPane().add(chckbxTraerAlFrente);
		
		comboBoxPracticarUltimos = new JComboBox<String>();
		comboBoxPracticarUltimos.addItem("5");
		comboBoxPracticarUltimos.addItem("10");
		comboBoxPracticarUltimos.setFont(new Font("Arial", Font.PLAIN, 13));
		comboBoxPracticarUltimos.setBounds(262, 147, 44, 22);
		getContentPane().add(comboBoxPracticarUltimos);
		
		chckbxDrag = new JCheckBox("click&drag mode");
		chckbxDrag.setFont(new Font("Arial", Font.PLAIN, 14));
		chckbxDrag.setBounds(6, 261, 162, 23);
		chckbxDrag.setSelected(true);
		getContentPane().add(chckbxDrag);
		
		
		chckbxNoMandarBandeja = new JCheckBox("Exit upon close");
		chckbxNoMandarBandeja.setFont(new Font("Arial", Font.PLAIN, 14));
		chckbxNoMandarBandeja.setBounds(6, 235, 171, 23);
		getContentPane().add(chckbxNoMandarBandeja);
		chckbxNoMandarBandeja.setSelected(Configuracion.ExitClose);
		SetExitOnClose(chckbxNoMandarBandeja);
		
		btnPracticarAleatorio = new JButton("Practice random:");
		btnPracticarAleatorio.setFont(new Font("Arial", Font.PLAIN, 18));
		btnPracticarAleatorio.setBounds(70, 192, 185, 25);
		getContentPane().add(btnPracticarAleatorio);
		
		comboBoxPracticarAleatorio = new JComboBox<String>();
		comboBoxPracticarAleatorio.addItem("5");
		comboBoxPracticarAleatorio.addItem("10");
		comboBoxPracticarAleatorio.setFont(new Font("Arial", Font.PLAIN, 13));
		comboBoxPracticarAleatorio.setBounds(262, 195, 45, 22);
		getContentPane().add(comboBoxPracticarAleatorio);
		
    	tesseractMode = new JCheckBox("use tesseract instead");
    	tesseractMode.setFont(new Font("Arial", Font.PLAIN, 14));
    	tesseractMode.setBounds(176, 235, 171, 23);
		getContentPane().add(tesseractMode);
		
		comboBoxOcrEng = new JComboBox<String>();
		comboBoxOcrEng.setFont(new Font("Arial", Font.PLAIN, 14));
		comboBoxOcrEng.addItem("0");
		comboBoxOcrEng.addItem("1");
		comboBoxOcrEng.addItem("2");
		comboBoxOcrEng.addItem("3");
		comboBoxOcrEng.setBounds(176, 263, 35, 25);
		getContentPane().add(comboBoxOcrEng);
		comboBoxOcrEng.setVisible(false);
		
		comboBoxPageSeg = new JComboBox<String>();
		comboBoxPageSeg.setFont(new Font("Arial", Font.PLAIN, 14));
		comboBoxPageSeg.addItem("0");
		comboBoxPageSeg.addItem("1");
		comboBoxPageSeg.addItem("2");
		comboBoxPageSeg.addItem("3");
		comboBoxPageSeg.addItem("4");
		comboBoxPageSeg.addItem("5");
		comboBoxPageSeg.addItem("6");
		comboBoxPageSeg.addItem("7");
		comboBoxPageSeg.addItem("8");
		comboBoxPageSeg.addItem("9");
		comboBoxPageSeg.addItem("10");
		comboBoxPageSeg.addItem("11");
		comboBoxPageSeg.addItem("12");
		comboBoxPageSeg.addItem("13");
		comboBoxPageSeg.setBounds(226, 263, 45, 25);
		getContentPane().add(comboBoxPageSeg);
		comboBoxPageSeg.setVisible(false);
		
		altTrainedData = new JCheckBox("alt. data");
		altTrainedData.setFont(new Font("Arial", Font.PLAIN, 11));
		altTrainedData.setBounds(176, 390, 171, 23);
		getContentPane().add(altTrainedData);
		altTrainedData.setVisible(false);
        
		//esto podría ser un for que recorra ArrayList<Object> pero igual usaría el mismo nro de sentencias en agregar cada uno al array
		//o podría usar getContentPane().getComponents
        zoom.addActionListener(this);
        tesseractMode.addActionListener(this);
        chckbxNoMandarBandeja.addActionListener(this);
        chckbxVertical.addActionListener(this);
        btnMostrarXRecientes.addActionListener(this);
        btnAbrirEstatico.addActionListener(this);
        chckbxTraerAlFrente.addActionListener(this);
        chckbxDrag.addActionListener(this);
        btnPracticarUltimos.addActionListener(this);
        btnPracticarAleatorio.addActionListener(this);
        comboBoxDic.addActionListener(this);
        comboBoxOcrEng.addActionListener(this);
        comboBoxPageSeg.addActionListener(this);
        altTrainedData.addActionListener(this);
        
        setVisible(true);
   
	    addWindowListener(new WindowAdapter() {
	            @Override
	            public void windowClosing(WindowEvent et) {
	                System.out.println("cerrando");
	
	                if (!Configuracion.ExitClose) {
	                    try {
							OpenTray();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                }
	                else {
		                if (Gestora.guardate(g))
		                	System.out.println("gestora guardada");
		                else
		                	System.out.println("error - no se guardó gestora");
	                }
	            }
	    });
	    
        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent e) {
                int length = Configuracion.keybinds.length; // keybinds es lista de ints, le pido cuantas teclas hay según Configuracion

                for (int i = 0; i < length; i++) { // recorre keybinds
                    if (i > 0 && Configuracion.keyActive[i - 1] && e.getKeyCode() == Configuracion.keybinds[i]) { // si no sos el primero y el anterior está activo y el evento registrado coincide con la tecla apretada ahora
                        Configuracion.keyActive[i] = true; // solo se consideran las teclas sucesivas si antes se apretó la primera
                    }
                    if (e.getKeyCode() == Configuracion.keybinds[i]) { // no hay otras restricciones si es primero
                        Configuracion.keyActive[i] = true;
                    }
                }

                if (Arrays.equals(Configuracion.keyActive, Configuracion.match)) {
                	if (SwingUtilities.isEventDispatchThread())
                    	System.out.println("EDT, CapturaCompleta.sacarCaptura");
                    else
                    	System.out.println("NO EDT, CapturaCompleta.sacarCaptura");
					SwingUtilities.invokeLater(new Runnable() {
					    public void run() {
					    	cc.sacarCaptura();
							try {
								StartCapture(500, 500);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					    }
					});
                }
                
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent e) {
                for (int i = 0; i < Configuracion.keybinds.length; i++) {
                    if (e.getKeyCode() == Configuracion.keybinds[i]) {
                        Configuracion.keyActive[i] = false;
                    }
                }
            }

            @Override
            public void nativeKeyTyped(NativeKeyEvent e) {
            }
            
        	
        });
    }

    public void StartCapture(int x, int y) throws IOException {
    	if (Configuracion.clickDrag) {
	    	if(imagen!=null)
	    		imagen.flush();
	    	imagen = ImageIO.read(new File("COMPLETA.PNG"));
			ii = new ImageIcon(imagen);
			if(jl!=null)
				jl.removeAll();
			jl = new JLabel(ii);
			
			paraMostrarCompleta = new JFrame();
			paraMostrarCompleta.setExtendedState(JFrame.MAXIMIZED_BOTH);
			paraMostrarCompleta.setUndecorated(true);
			paraMostrarCompleta.add(jl);
			if(SwingUtilities.isEventDispatchThread())
				System.out.println("SI ES EL EDT");
			else
				System.out.println("NO ES EL EDT");
			paraMostrarCompleta.setAlwaysOnTop(true);
			paraMostrarCompleta.setVisible(true);
	    	
	        //cap = new FrameArea(500, 500); las referencias a cada new FrameArea se mantenían (aunque no deberían???)
	    	//								 y la memoria que ocupaban no podía ser reclamada por el GC,
	    	//								 asi que instancié uno solo en el constructor de MenuPrincipal
	        cap.SetFrameSize(1, 1);
	        
	        GlobalScreen.addNativeMouseMotionListener(new NativeMouseMotionListener() {
	            @Override
	            public void nativeMouseDragged(NativeMouseEvent e) {
	                if (!cap.isVisible()) {
	                    GlobalScreen.removeNativeMouseMotionListener(this);
	                }
	
	                cap.SetFrameSize(e.getX() - cap.getX(), e.getY() - cap.getY());
	            }
	
	            @Override
	            public void nativeMouseMoved(NativeMouseEvent e) {
	            }
	        });
	
	        GlobalScreen.addNativeMouseListener(new NativeMouseListener() {
	
	            @Override
	            public void nativeMouseReleased(NativeMouseEvent e) {
	                cap.setVisible(false);
	                GlobalScreen.removeNativeMouseListener(this);
	
	                Rectangle rect = new Rectangle(cap.getX(), cap.getY(), cap.getWidth(), cap.getHeight());
	
	                TakeScreenshot(rect);
	                
	                paraMostrarCompleta.dispose();
	            }
	
	            @Override
	            public void nativeMousePressed(NativeMouseEvent e) {
	                cap.setVisible(true);
	                cap.SetFramePosition(e.getX(), e.getY());
	            }
	
	            @Override
	            public void nativeMouseClicked(NativeMouseEvent e) {
	            }
	        });
    	}
    	else {
			try {
				imagenNoClick = ImageIO.read(new File("COMPLETA.PNG"));
				ii = new ImageIcon(imagenNoClick);
				jl = new JLabel(ii);
				
				paraMostrarCompleta = new JFrame();
				paraMostrarCompleta.setExtendedState(JFrame.MAXIMIZED_BOTH);
				paraMostrarCompleta.setUndecorated(true);
				paraMostrarCompleta.add(jl);
				paraMostrarCompleta.setAlwaysOnTop(true);
				paraMostrarCompleta.setVisible(true);
				
				Thread.sleep(2000);
				
			    GlobalScreen.addNativeMouseMotionListener(mml);
			    
			    Thread t1 = new Thread(new Runnable() {
			        @Override
			        public void run() {
			        	//cap = new FrameArea(500, 500);
					    cap.SetFrameSize(1, 1);
					    cap.setVisible(true);
			            cap.SetFramePosition(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
					    
			        	try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					    GlobalScreen.removeNativeMouseMotionListener(mml);
					    rect = new Rectangle(cap.getX(), cap.getY(), cap.getWidth(), cap.getHeight());
					    cap.setVisible(false);
						TakeScreenshot(rect);
						paraMostrarCompleta.dispose();
			        }
			    });  
			    t1.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    NativeMouseMotionListener mml = new NativeMouseMotionListener() {
    	@Override
        public void nativeMouseDragged(NativeMouseEvent e) {     
        }
    	@Override
    	public void nativeMouseMoved(NativeMouseEvent e) {
            cap.SetFrameSize(e.getX() - cap.getX(), e.getY() - cap.getY());
        }
    };
    
    static NativeMouseMotionListener mml2 = new NativeMouseMotionListener() { // para esconder ventana sin hacer click, solo con mover mouse a esquina
    	@Override															  // útil aplicaciones exclusive fullscreen que no pueden perder focus
        public void nativeMouseDragged(NativeMouseEvent e) {     
        }
    	@Override
    	public void nativeMouseMoved(NativeMouseEvent e) {
    		if (e.getY()==0 && (e.getX()==0 || e.getX()==1366)) {
    			paraBr.setAlwaysOnTop(false);
        		paraBr.setVisible(false);
        		GlobalScreen.removeNativeMouseMotionListener(mml2);
    		}
        }
    };

    public void TakeScreenshot(Rectangle rect) {
        imageTake = null;

        try {
            imageTake = new Robot().createScreenCapture(rect);
        } catch (Exception e) {
            Configuracion.Log(e.toString() + "\n" + " dimensiones inválidas");
        }

        if (imageTake != null) {
            try {
            	SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
							actualizarPagina();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    }
                });
            	
            } catch (Exception e) {
                Configuracion.Log(e.toString() + "\n" + " error actualizarPagina");
            }
        }
    }

    public void SetExitOnClose(JCheckBox b) {
        setDefaultCloseOperation(b.isSelected() ? JFrame.EXIT_ON_CLOSE : JFrame.HIDE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        // no puedo usar switch con Object
        // "currently it seems the best way to switch over classes would be,
        // like @Davide pointed out, to use a uniquely identifieable, human-readable attribute, like their name/path."

        if (source == exitItem) { // del tray
        	if (Gestora.guardate(g))
            	System.out.println("gestora guardada");
            else
            	System.out.println("error - no se guardó gestora");
            System.exit(0);
        }

        if (source == openItem) { // del tray
            setVisible(true);
            SystemTray.getSystemTray().remove(icon);
        }

        if (source == chckbxNoMandarBandeja) {
            SetExitOnClose(chckbxNoMandarBandeja);
            Configuracion.cambiar(1, "" + chckbxNoMandarBandeja.isSelected()); // 1 es nro de línea de Exit on Close en config.txt
        }

        if (source == chckbxVertical) {
        	Configuracion.setVertical(chckbxVertical.isSelected());
        }
        
        if (source == btnMostrarXRecientes) {
        	
        	ArrayList<String> subRecientes = new ArrayList<String>();
        	for (int aa = g.getLecturas().size() ; aa>=g.getLecturas().size()-Integer.parseInt((String)comboBoxCantRecientes.getSelectedItem())+1; aa--) {
        		subRecientes.add(g.getLecturas().get(aa-1));
        	}
        	
        	JFrame jflecs = new JFrame();
        	JList<String> displayList = new JList<>(subRecientes.toArray(new String[0]));
            JScrollPane scrollPane = new JScrollPane(displayList);

            jflecs.getContentPane().add(scrollPane);
            //jflecs.setDefaultCloseOperation(EXIT_ON_CLOSE);
            jflecs.pack();
            jflecs.setVisible(true);

        }
        
        if (source == btnAbrirEstatico) {
        	if (!paraBr.isVisible())
        		paraBr.setVisible(true);
        	else
        		JOptionPane.showMessageDialog(this, "Browser's already visible");
        }
        
        if (source == chckbxTraerAlFrente) {
        	Configuracion.setFrente(chckbxTraerAlFrente.isSelected());
        }
        
        if (source == chckbxDrag) {
        	Configuracion.setClickDrag(chckbxDrag.isSelected());
        }
        
        if (source == btnPracticarUltimos) {
        	ArrayList<Lectura> l = g.dameListaLecturas();
        	ArrayList<Lectura> invertida = new ArrayList<Lectura>();
        	for (int indInvertida = l.size(); indInvertida>0; indInvertida--) {
        		invertida.add(l.get(indInvertida-1));
        	}
        	for (int ya = 0 ; ya<invertida.size(); ya++) {
        		System.out.println(invertida.get(ya).darTexto() + " - ");
        	}
        	try {
        		Examen.i = 0; // reiniciar i para después recorrer la sublista de X últimos
    			new Examen(invertida, Integer.parseInt((String)comboBoxPracticarUltimos.getSelectedItem())+1, g, false);
    		} catch (Exception nose) {
    			// TODO Auto-generated catch block
    			nose.printStackTrace();
    		}
        	
        }
        
        if(source == btnPracticarAleatorio) {
        	ArrayList<Lectura> l = g.dameListaLecturas();
        	ArrayList<Lectura> invertida = new ArrayList<Lectura>();
        	for (int indInvertida = l.size(); indInvertida>0; indInvertida--) {
        		invertida.add(l.get(indInvertida-1));
        	}
        	for (int ya = 0 ; ya<invertida.size(); ya++) {
        		System.out.println(invertida.get(ya).darTexto() + " - ");
        	}
        	try {
        		ExamenRE.i = 0; // no sé si hace falta acá
    			new ExamenRE(invertida, Integer.parseInt((String)comboBoxPracticarAleatorio.getSelectedItem())+1, g, false);
    		} catch (Exception nose) {
    			// TODO Auto-generated catch block
    			nose.printStackTrace();
    		}
        }
        
        if (source == comboBoxDic) {
        	animacion = 100;
        	Configuracion.setPagina((String)comboBoxDic.getSelectedItem());
        	Configuracion.setUrlBase((String)comboBoxDic.getSelectedItem());
        	SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                	Timer timer = new Timer(100, listenerAnimacion);
                    timer.start();
                }
            });
        }
        
        if (source == tesseractMode) {
        	Configuracion.setTesseract(tesseractMode.isSelected());
        	if (tesseractMode.isSelected()) {
        		altTrainedData.setVisible(true);
        		comboBoxOcrEng.setVisible(true);
        		comboBoxPageSeg.setVisible(true);
        	}
        	else {
        		altTrainedData.setVisible(false);
        		comboBoxOcrEng.setVisible(false);
        		comboBoxPageSeg.setVisible(false);
        	}
        }
        
        if (source == altTrainedData) {
        	if (altTrainedData.isSelected())
        		Configuracion.setTrainedData("bjpn");
        	else
        		Configuracion.setTrainedData("jpn");
        }
        
        if (source == comboBoxOcrEng) {
        	Configuracion.setOcrEng((int)comboBoxOcrEng.getSelectedItem());
        	if (tesseractMode.isSelected())
        		comboBoxOcrEng.setVisible(true);
        	else
        		comboBoxOcrEng.setVisible(false);
        }
        
        if (source == comboBoxPageSeg) {
        	Configuracion.setPageSeg((int)comboBoxPageSeg.getSelectedItem());
        	if (tesseractMode.isSelected())
        		comboBoxPageSeg.setVisible(true);
        	else
        		comboBoxPageSeg.setVisible(false);
        }
        
        if (source == zoom) {
        	paraBr.setAlwaysOnTop(true);
        	Configuracion.zoom = Double.parseDouble(JOptionPane.showInputDialog("cambiar zoom browser ej 0.5"));
        }

    }
    
    ActionListener listenerAnimacion = new ActionListener() {   
        @Override
        public void actionPerformed(ActionEvent event) {
        	try {
				imgBuffer = ImageIO.read(new FileInputStream(new File("images/"+(String)comboBoxDic.getSelectedItem()+".png")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            imageIcon = new ImageIcon(imgBuffer);
            if (SwingUtilities.isEventDispatchThread())
            	System.out.println("EDT, cambio imagen trad");
            else
            	System.out.println("NO EDT, cambio imagen trad");
            jlII.removeAll();
            jlII.setIcon(imageIcon);;
            animacion = animacion/2;
            jlII.setBounds((225+animacion), 245, 200, 200);
            add(jlII);
            jlII.setVisible(true);
            repaint();
        }
    };
    
    public static JFrame dameParaBr() {
    	return paraBr;
    }
	
	public static void actualizarPagina() throws InterruptedException, Exception {
		Platform.runLater(()->{
			scrollY = (Integer)webEngine.executeScript("document.body.scrollTop");
		});
		
		ImageIO.write(imageTake, "PNG", new File(Configuracion.Path + "\\screenshot.png"));
        if (Configuracion.vertical)
        	nuevaUrl = DetectarTexto.detectarVertical(Configuracion.Path + "\\screenshot.png");
        else {
        	if (Configuracion.esTesseract)
        		nuevaUrl = DetectarTexto.detectarTextoTesseract(Configuracion.Path + "\\screenshot.png"); // <-- oportunidad para polimorfismo
        	else
        		nuevaUrl = DetectarTexto.detectarTexto(Configuracion.Path + "\\screenshot.png");
        }
        
        paraFX = new JFXPanel();
        
        Platform.runLater(()->{
		   	navegador = new WebView();
		   	webEngine = navegador.getEngine();
		   	navegador.setZoom(Configuracion.zoom);
		   	webEngine.load(nuevaUrl);
			sc = new Scene(navegador, 200, 200);
		   	paraFX.setScene(sc);
        });
        
        g.agregarLectura(DetectarTexto.getLectura());
        if (!paraBr.isVisible()) {
        	paraBr.setVisible(true); // si la ventana está cerrada la abre de nuevo, no tiene nada que ver con mandarla al frente
        }
        
        paraBr.getContentPane().removeAll();
        paraBr.add(paraFX,0);
        paraBr.setVisible(true);
        
        GlobalScreen.addNativeMouseMotionListener(mml2);
        System.gc();
                
        Thread t2 = new Thread(new Runnable() { // thread con sleep 1.5 segs porque si mando window.scrollTo al toque antes que 
	        @Override							// termine de cargar toda la página, ignora el comando.
	        public void run() {				// Buscar forma de esperar a que termine de cargar todo "dinámicamente"
	        	try {						
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	Platform.runLater(()->{
	            	webEngine.executeScript("window.scrollTo(" + 0 + ", " + scrollY + ")");
	    		});
	        }
	    });  
	    t2.start();
        
        Platform.runLater(()->{
		    webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
	            if (newState == Worker.State.SUCCEEDED) {
	                // nueva página cargada, ahora
	                actualizarScroll();
	            }
	        });
        });
	    
	}
	
	private static void actualizarScroll() {
		Platform.runLater(()->{
        	webEngine.executeScript("window.scrollTo(" + 0 + ", " + scrollY + ")");
		});
	}
	
}