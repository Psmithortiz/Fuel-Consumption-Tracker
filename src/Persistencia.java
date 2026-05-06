import java.io.*;
import java.util.ArrayList;


public class Persistencia {

    private static final String NOMBRE_ARCHIVO = "registros.txt";

    static public void cargar(ArrayList<String> patentes, ArrayList<Double> kilometros, ArrayList<Double> litros) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(NOMBRE_ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] atributos = linea.split(";");
                patentes.add(atributos[0]);
                kilometros.add(Double.parseDouble(atributos[1]));
                litros.add(Double.parseDouble(atributos[2]));
            }
        }
    }

    static public void guardar(ArrayList<String> patentes, ArrayList<Double> kilometros, ArrayList<Double> litros) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(NOMBRE_ARCHIVO))) {

            for (int i = 0; i < patentes.size(); i++) {
                bw.write(patentes.get(i) + ";" + kilometros.get(i) + ";" + litros.get(i) + ";" + (kilometros.get(i) / litros.get(i)));
                bw.newLine();
            }
        }
    }
}

