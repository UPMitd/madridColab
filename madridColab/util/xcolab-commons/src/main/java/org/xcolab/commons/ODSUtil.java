package org.xcolab.commons;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import org.xcolab.commons.html.LabelLongValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class ODSUtil {
    private static final BidiMap<Long, String> codeToODSMap = new DualHashBidiMap<>();
    private static final List<LabelLongValue> selectOptions;

    // Role list adapted from countries.csv by Google licensed under CC BY 3.0
    // Material: https://developers.google.com/public-data/docs/canonical/countries_csv
    // License: https://creativecommons.org/licenses/by/3.0/
    static {
        codeToODSMap.put(0l, "1. Fin de la pobreza");
        codeToODSMap.put(1l, "2. Hambre cero");
        codeToODSMap.put(2l, "3. Salud y bienestar");
        codeToODSMap.put(3l, "4. Educación de calidad");
        codeToODSMap.put(4l, "5. Igualdad de género");
        codeToODSMap.put(5l, "6. Agua limpia y saneamiento");
        codeToODSMap.put(6l, "7. Energía asequible y no contaminante");
        codeToODSMap.put(7l, "8. Trabajo decente y crecimiento económico");
        codeToODSMap.put(8l, "9. Industria, innovación e infraestructura");
        codeToODSMap.put(9l, "10. Reducción de las desigualdades");
        codeToODSMap.put(10l, "11. Ciudades y comunidades sostenibles");
        codeToODSMap.put(11l, "12. Producción y consumo responsables");
        codeToODSMap.put(12l, "13. Acción por el clima");
        codeToODSMap.put(13l, "14. Vida submarina");
        codeToODSMap.put(14l, "15. Vida de ecosistemas terrestres");
        codeToODSMap.put(15l, "16. Paz, justicia e instituciones sólidas");
        codeToODSMap.put(16l, "17. Alianzas para lograr los objetivos");

        List<LabelLongValue> rawSelectOptions = new ArrayList<>(
                LabelLongValue.fromMap(codeToODSMap));
        rawSelectOptions.sort(Comparator.comparing(LabelLongValue::getValue));
        selectOptions = Collections.unmodifiableList(rawSelectOptions);
    }

    private ODSUtil() { }

    public static String getODSForCode(Long code) {
        if (codeToODSMap.containsKey(code)) {
            return codeToODSMap.get(code);
        }
        return "";
    }

    public static Long getCodeForODS(String ODS) {
        if (codeToODSMap.containsValue(ODS)) {
            return codeToODSMap.getKey(ODS);
        }
        return 5l;
    }

    public static List<LabelLongValue> getSelectOptions() {
        return selectOptions;
    }
}


