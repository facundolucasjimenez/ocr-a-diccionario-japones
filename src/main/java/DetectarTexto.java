
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageContext;
import com.google.cloud.vision.v1.Vertex;
import com.google.protobuf.ByteString;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

public class DetectarTexto {
  private static Lectura lec;
  public static String completo;
  public static ArrayList<ArrayList<String>> matriz = new ArrayList<ArrayList<String>>();
  public static ArrayList<String> coordenadas = new ArrayList<String>();
  public static String[] separados;
  public static String url;
  
 //para especificar idioma del motor (opcional, no noté ningún cambio)
  public static ImageContext imageContext = ImageContext.newBuilder().addLanguageHints("ja").build(); // ja - japanese (no, no es jp)
  
  private static ArrayList<Vertice> ve;
  private static JFrame paraBrLocal;
  private static JFXPanel jfxLocal;
  private static ByteString imgBytes;
  private static Image img;
  private static Feature feat;
  private static AnnotateImageRequest request;
  private static BatchAnnotateImagesResponse response;
  private static List<AnnotateImageResponse> responses;
  private static JFXPanel jf;
  private static WebView navegador;
  private static WebEngine webEngine;
  private static Scene scene;
  static String urlUtf;
  
  public static String detectarTexto(String filePath) throws IOException, InterruptedException, Exception {
    int i = 0; //bandera, podría ser boolean
    ve = new ArrayList<Vertice>();
	
	List<AnnotateImageRequest> requests = new ArrayList<>();

    imgBytes = ByteString.readFrom(new FileInputStream(filePath));

    img = Image.newBuilder().setContent(imgBytes).build();
    feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
    request =
        AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).setImageContext(imageContext).build();
    requests.add(request);	// a�ade solicitud al array

    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources.
    try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
      response = client.batchAnnotateImages(requests);
      responses = response.getResponsesList();

      for (AnnotateImageResponse res : responses) {
        if (res.hasError()) {
          System.out.format("Error: %s%n", res.getError().getMessage());
          return null;
        }

        
        for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
        	if(i==0) {	// bandera para ver si es la primera salida
        		completo = annotation.getDescription();
        		i++;
        		System.out.println("completo:\n");
        		System.out.format("%s%n", annotation.getDescription());
        	}
        	else {
        		System.out.println("\n");
        		System.out.format("%s%n", annotation.getDescription());
        		//System.out.println(annotation.getBoundingPoly().getVerticesList());
        	}
        }
        
        i=0;
			
      }
        
    }
    
    System.out.println(":::::::::::::>>>>>>>>>>>>::::::::::::");
    System.out.println(":::::::::::::>>>>>>>>>>>>::::::::::::");
    System.out.println(" ");
    
    //byte[] completo2 = completo.getBytes("UTF-8");
    //System.out.println(completo2);
    url = Configuracion.urlBase+completo;
    url = url.replace("\n", ""); // saca los separadores de línea por las dudas que rompa algo en la url
    System.out.println(url);
    
    completo = completo.replace("+", "");
    
    String alClipboard = completo;
    StringSelection stringSelection = new StringSelection(alClipboard);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, null);
    
    String codificado2 = URLEncoder.encode(completo, "UTF-8");
    String urlCodificada = Configuracion.urlBase+codificado2;
    System.out.println(urlCodificada);
    
    String completoOrdenado="";
    for (Vertice ver : ve) {
    	completoOrdenado = completoOrdenado+ver.dameAnotacion().getDescription();
    }
    
    System.out.println(completoOrdenado);
    
    if (Configuracion.vertical) {
    	completo = completoOrdenado;
    	String codificado3 = URLEncoder.encode(completo, "UTF-8");
    	urlCodificada = Configuracion.urlBase+codificado3;
    	
    }
    
  //BufferedImage imgg = MenuPrincipal.darCurrentCompleta();
    BufferedImage imgg = null;
    ArrayList<Definicion> def = new ArrayList<Definicion>();
    
    lec = new Lectura ((int)Math.floor(Math.random()*(9999-1+1)+1), completo, imgg, def);
    
    paraBrLocal = MenuPrincipal.dameParaBr();
    
    return urlCodificada;
  }

  // tesseract
  /*Page segmentation modes:
	  0    Orientation and script detection (OSD) only.
	  1    Automatic page segmentation with OSD.
	  2    Automatic page segmentation, but no OSD, or OCR. (not implemented)
	  3    Fully automatic page segmentation, but no OSD. (Default)
	  4    Assume a single column of text of variable sizes.
	  5    Assume a single uniform block of vertically aligned text.
	  6    Assume a single uniform block of text.
	  7    Treat the image as a single text line.
	  8    Treat the image as a single word.
	  9    Treat the image as a single word in a circle.
	 10    Treat the image as a single character.
	 11    Sparse text. Find as much text as possible in no particular order.
	 12    Sparse text with OSD.
	 13    Raw line. Treat the image as a single text line,
	       bypassing hacks that are Tesseract-specific.

	OCR Engine modes:
	  0    Legacy engine only.
	  1    Neural nets LSTM engine only.
	  2    Legacy + LSTM engines.
	  3    Default, based on what is available.
  */
  public static String detectarTextoTesseract(String path) throws IOException{
	  Tesseract tesseract = new Tesseract();
      try {

      	tesseract.setLanguage(Configuracion.trainedData);
        tesseract.setDatapath("tessdata");
        tesseract.setPageSegMode(Configuracion.pageSeg);
        tesseract.setOcrEngineMode(Configuracion.ocrEngine);

        String texto = tesseract.doOCR(new File(path));

        System.out.println(texto);
        texto = texto.replace("\n", "");
        texto = texto.replace("+", "");
        String utf = URLEncoder.encode(texto, "UTF-8");
        urlUtf = Configuracion.urlBase+utf;
        urlUtf = urlUtf.replace("\n", "");
        urlUtf = urlUtf.replace("+", "");
        System.out.println(url);
        
        //BufferedImage imggg = MenuPrincipal.darCurrentCompleta();
        BufferedImage imggg = null;
        ArrayList<Definicion> deff = new ArrayList<Definicion>();
        
        lec = new Lectura ((int)Math.floor(Math.random()*(9999-1+1)+1), texto, imggg, deff);
        
        paraBrLocal = MenuPrincipal.dameParaBr();
        
        //jfxLocal = actualizarJFX(urlUtf);
      }
      catch (TesseractException e) {
          e.printStackTrace();
      }
      
      //return actualizarJFX(urlUtf);
      
      return urlUtf;
  }
  
  public static String detectarVertical(String path) throws IOException {
	  Tesseract tesseract = new Tesseract();
      try {

      	tesseract.setLanguage("jpn_vert");
        tesseract.setDatapath("tessdata");
        tesseract.setPageSegMode(3);
        tesseract.setOcrEngineMode(3);

        String texto = tesseract.doOCR(new File(path));

        System.out.println(texto);
        
        String utf = URLEncoder.encode(texto, "UTF-8");
        urlUtf = Configuracion.urlBase+utf;
        urlUtf = urlUtf.replace("\n", "");
        urlUtf = urlUtf.replace("\n", "+");
        System.out.println(url);
        
        //BufferedImage imggg = MenuPrincipal.darCurrentCompleta();
        BufferedImage imggg = null;
        ArrayList<Definicion> deff = new ArrayList<Definicion>();
        
        lec = new Lectura ((int)Math.floor(Math.random()*(9999-1+1)+1), texto, imggg, deff);
        
        paraBrLocal = MenuPrincipal.dameParaBr();
        
        //jfxLocal = actualizarJFX(urlUtf);
      }
      catch (TesseractException e) {
          e.printStackTrace();
      }
      
      //return actualizarJFX(urlUtf);
      
      return urlUtf;
  }
  
  public static WebView actualizarJFX(String url) {  	
  	jf = new JFXPanel();
	Platform.runLater(()->{
		  navegador = new WebView();
		  webEngine = navegador.getEngine();
		  navegador.setZoom(Configuracion.zoom);
		  webEngine.load(url);
		  scene = new Scene(navegador, 200, 200);
		  jf.setScene(scene);
		  
	});
		
	return navegador;
  }
  
  public static int sacarCentroX(List<Vertex> lista) {
	  return (lista.get(0).getX()+lista.get(1).getX())/2;
  }
  
  public static int sacarCentroY(List<Vertex> lista) {
	  return (lista.get(0).getY()+lista.get(3).getY())/2;
  }
  
  public static Lectura getLectura() {
	  return lec;
  }
  
}
