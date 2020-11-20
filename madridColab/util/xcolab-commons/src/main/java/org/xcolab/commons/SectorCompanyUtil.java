package org.xcolab.commons;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import org.xcolab.commons.html.LabelLongValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class SectorCompanyUtil {
    private static final BidiMap<Long, String> codeToSectorMap = new DualHashBidiMap<>();
    private static final List<LabelLongValue> selectOptions;

    // Role list adapted from countries.csv by Google licensed under CC BY 3.0
    // Material: https://developers.google.com/public-data/docs/canonical/countries_csv
    // License: https://creativecommons.org/licenses/by/3.0/
    static {
        codeToSectorMap.put(0l, "Actividades profesionales");
        codeToSectorMap.put(1l, "Actividades administrativas y servicios auxiliares");
        codeToSectorMap.put(2l, "Agricultura y pesca");
        codeToSectorMap.put(3l, "Comercio");
        codeToSectorMap.put(4l, "Construcción");
        codeToSectorMap.put(5l, "Consultoría");
        codeToSectorMap.put(6l, "Educación");
        codeToSectorMap.put(7l, "Financiero y seguros");
        codeToSectorMap.put(8l, "Hostelería");
        codeToSectorMap.put(9l, "Industria y energía");
        codeToSectorMap.put(10l, "Información y comunicaciones");
        codeToSectorMap.put(11l, "Inmobiliario");
        codeToSectorMap.put(12l, "Resto servicios");
        codeToSectorMap.put(13l, "Salud");
        codeToSectorMap.put(14l, "Tecnología");
        codeToSectorMap.put(15l, "Transporte y almacenamiento");
        codeToSectorMap.put(16l, "Turismo");
        codeToSectorMap.put(17l, "Otro");

        List<LabelLongValue> rawSelectOptions = new ArrayList<>(
                LabelLongValue.fromMap(codeToSectorMap));
        //rawSelectOptions.sort(Comparator.comparing(LabelLongValue::getLable));
        selectOptions = Collections.unmodifiableList(rawSelectOptions);
    }

    private SectorCompanyUtil() { }

    public static String getSectorForCode(Long code) {
        if (codeToSectorMap.containsKey(code)) {
            return codeToSectorMap.get(code);
        }
        return "";
    }

    public static Long getCodeForSector(String sector) {
        if (codeToSectorMap.containsValue(sector)) {
            return codeToSectorMap.getKey(sector);
        }
        return 5l;
    }

    public static List<LabelLongValue> getSelectOptions() {
        return selectOptions;
    }
}


