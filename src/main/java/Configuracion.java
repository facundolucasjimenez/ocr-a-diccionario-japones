
/*
File:   Settings.java 
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
//import org.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

// por encapsulamiento los campos deberían ser privados, ya tengo los set faltarían los get

public class Configuracion {
	
	public static double zoom = 1;
	
	public static int ocrEngine = 3;
	
	public static int pageSeg = 3;
	
	public static String trainedData = "jpn";
	
	public static boolean esTesseract = false;
	
	public static String pagina = "jisho";
	
	public static String urlBase = "https://www.jisho.org/search/";
	
	public static ArrayList<String> lecturas;
	
	public static int indiceCaptura = 0;
	
	public static boolean clickDrag = true;
	
	public static boolean vertical = false;
	
	public static boolean alFrente = false;
	
	public static int posX = 100;
	
	public static int posY = 100;
	
	public static int ancho = 500;
	
	public static int alto = 600;

    public static String os = "Windows";

    public static String settings = "Keybind: CONTROL+PRINTSCREEN\nExit on Close: true";

    public static String DebugText = "";

    public static String Path = "";
    
    public static boolean ExitClose = true;

    public static int[] keybinds = {
    		NativeKeyEvent.VC_SHIFT,
            NativeKeyEvent.VC_CONTROL
    };
    
    public static boolean[] keyActive = {
        false,
        false,
        false
    };
    public static boolean[] match = {
        true,
        true,
        true
    };

    public static boolean hasConfig() {
        return new File("config.txt").exists();
    }

    public static void cambiar(int line, String value) {
        String[] lines = settings.split("\n");
        lines[line] = lines[line].substring(0, lines[line].indexOf(": ")) + ": " + value;
        settings = Arrays.toString(lines).substring(1, Arrays.toString(lines).length() - 1).replaceAll(", ", "\n");
        try {
            escribirConfig();
        } catch (Exception e) {
            Log("error al cambiar la config y reescribiendo - no se hizo nada");
        }
        
        try{
            leerConfig();
        }catch(Exception e){
            Log("error al leer la configuración nueva");
        }
    }

    public static void escribirConfig() throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("config.txt")));

        for (String s : settings.split("\n")) {
            bw.write(s);
            bw.newLine();
        }

        bw.close();
        bw = null;

        System.out.println("config escrito");
    }

    public static void Log(String text) {
        System.out.println(text);
        DebugText += text + "\n";

        File debug = new File("debug.log");

        try {
            debug.createNewFile();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            BufferedWriter bw = new BufferedWriter(new FileWriter(debug, true));           
            
            bw.write(dateFormat.format(date) + " | " + text);
            bw.newLine();
            bw.close();

        } catch (IOException e) {
            System.out.println("error");
            System.exit(1);
        }
    }

    @SuppressWarnings("resource")
	public static int leerConfig() throws Exception {
        if (!hasConfig()) {
            escribirConfig();
        }

        Scanner file = new Scanner(new File("config.txt"));
        String lines = "";

        while (file.hasNextLine()) {
            lines += file.nextLine() + "\n";
        }

        file = new Scanner(lines);

        for (int i = 0; i < settings.split("\n").length; i++) {
            String config;
            try {
                config = file.nextLine().split(": ")[1];
            } catch (Exception e) {
                new File("config.txt").delete();
                Log("error en la configuracion - reescribiendo");
                escribirConfig();
                leerConfig();
                return -1;
            }

            switch (i) {
                case 0:
                    String[] keys = config.trim().split("\\+");

                    keybinds = null;
                    keybinds = new int[keys.length];

                    for (int j = 0; j < keybinds.length; j++) {
                        try {
                            Field f = NativeKeyEvent.class.getField("VC_" + keys[j].toUpperCase());
                            f.setAccessible(true);

                            keybinds[j] = f.getInt(f);

                        } catch (Exception e) {
                            Log(e.toString() + "\n" + "no se reconocieron los keybinds, reescribiendo");
                            new File("settings.txt").delete();
                            escribirConfig();
                            leerConfig();
                            return -1;
                        }
                    }
                    break;
                case 1:
                    ExitClose = Boolean.parseBoolean(config);
                    break;
            }

        }
        settings = lines;
        keyActive = new boolean[keybinds.length];
        match = new boolean[keybinds.length];
        Arrays.fill(match, true);

        Log("Config leída");
        return 0;
    }
    
    public static void setVertical(boolean e) {
    	vertical = e;
    }
    
    public static void incrementarIndiceCaptura() {
    	indiceCaptura++;
    }
    
    public static void setFrente(boolean e) {
    	alFrente = e;
    }
    
    public static void cambiarTam(int ancho, int alto) {
    	Configuracion.ancho = ancho;
    	Configuracion.alto = alto;
    }
    
    public static void cambiarPosXY(int x, int y) {
    	Configuracion.posX = x;
    	Configuracion.posY = y;
    }
    
    public static void setClickDrag(boolean e) {
    	clickDrag = e;
    }
    
    public static void setPagina(String s) {
    	pagina = s;
    }
    
    public static void setUrlBase(String nuevo) { // agregar más diccionarios/traductores
    	switch(nuevo){
    	case"jisho":{
    		urlBase = "https://www.jisho.org/search/";
    	}
    	break;
    	case"deepL":{
    		urlBase = "https://www.deepl.com/translator#ja/es/";
    	}
    	}
    }
    
    public static void setTesseract(boolean e) {
    	esTesseract = e;
    }
    
    public static void setOcrEng(int mode) {
    	ocrEngine = mode;
    }
    
    public static void setPageSeg(int mode) {
    	pageSeg = mode;
    }
    
    public static void setTrainedData(String otro) {
    	trainedData = otro;
    }
    
}
