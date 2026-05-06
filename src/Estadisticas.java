import java.util.ArrayList;

public class Estadisticas {
    static public Double promedioCalcular(ArrayList<Double> kilometros, ArrayList<Double> litros) {
        double promedio = 0.0;
        int kilometrosLen = kilometros.size();
        for (int i = 0; i < kilometrosLen; i++) {
            Double km = kilometros.get(i);
            Double lts = litros.get(i);
            promedio += km / lts;
        }
        return promedio / kilometrosLen;
    }

    static public String extremosCalcular(ArrayList<String> patente, ArrayList<Double> kilometros, ArrayList<Double> litros) {
        double rendimientoMinimo = kilometros.get(0) / litros.get(0);
        double rendimientoMaximo = kilometros.get(0) / litros.get(0);
        double rendimiento;
        int indiceMaximo = 0;
        int indiceMinimo = 0;
        int kilometrosLen = kilometros.size();
        for (int i = 0; i < kilometrosLen; i++) {
            rendimiento = kilometros.get(i) / litros.get(i);
            if (rendimiento > rendimientoMaximo) {
                rendimientoMaximo = rendimiento;
                indiceMaximo = i;
            } else if (rendimiento < rendimientoMinimo) {
                rendimientoMinimo = rendimiento;
                indiceMinimo = i;
            }
        }

        return "Patente " + patente.get(indiceMaximo) + " tuvo el rendimiento maximo con "
                + String.format("%.2f", rendimientoMaximo) + " km/L\n"
                + "Patente " + patente.get(indiceMinimo) + " tuvo el rendimiento minimo con "
                + String.format("%.2f", rendimientoMinimo) + " km/L";
    }

}


