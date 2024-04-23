import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
    import java.net.URLConnection;
import java.net.URLEncoder;
    
    
    public class Pagina {
    	public static String aParsear;
    	public static String sub2;
    	public Pagina(String c) throws IOException {
            URL url = null;
			try {
				url = new URL("https://jisho.org/search/"+c);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            
            URLConnection con = url.openConnection();
            InputStream is = con.getInputStream();
                      
            
            try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line = null;
                aParsear = null;
            
                while ((line = br.readLine()) != null) {
                    aParsear = aParsear+line;
                }
                //System.out.println(aParsear);
                String sub = aParsear.substring(aParsear.indexOf("meaning-meaning") + 17);
                String sub2 = sub.substring(0, sub.indexOf("<"));
                System.out.println(sub2);
            }
    	}
            
        public static String darSignificado(String c) throws IOException {
        	try {
    			c = URLEncoder.encode(c, "UTF-8");
    		} catch (UnsupportedEncodingException e2) {
    			// TODO Auto-generated catch block
    			e2.printStackTrace();
    		}
        	URL url = null;
			try {
				url = new URL("https://jisho.org/search/"+c);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            
            URLConnection con = url.openConnection();
            InputStream is = con.getInputStream();
                      
            
            try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line = null;
                aParsear = null;
            
                while ((line = br.readLine()) != null) {
                    aParsear = aParsear+line;
                }
                //System.out.println(aParsear);
                String sub = aParsear.substring(aParsear.indexOf("meaning-meaning") + 17);
                sub2 = sub.substring(0, sub.indexOf("<"));
                System.out.println(sub2);
            }
        	return sub2;
        }
    }