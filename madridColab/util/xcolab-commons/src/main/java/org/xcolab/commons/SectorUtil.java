package org.xcolab.commons;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import org.xcolab.commons.html.LabelLongValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class SectorUtil {
    private static final BidiMap<Long, String> codeToSectorMap = new DualHashBidiMap<>();
    private static final List<LabelLongValue> selectOptions;

    // Role list adapted from countries.csv by Google licensed under CC BY 3.0
    // Material: https://developers.google.com/public-data/docs/canonical/countries_csv
    // License: https://creativecommons.org/licenses/by/3.0/
    static {
        codeToSectorMap.put(0l, "Académico");
        codeToSectorMap.put(1l, "Empresa");
        codeToSectorMap.put(2l, "Gobierno");
        codeToSectorMap.put(3l, "Autónomo");
        codeToSectorMap.put(4l, "Start-up (o PYME)");
        codeToSectorMap.put(5l, "Tercer sector (o ONG/Fundación)");
        codeToSectorMap.put(6l, "Otro");

        List<LabelLongValue> rawSelectOptions = new ArrayList<>(
                LabelLongValue.fromMap(codeToSectorMap));
        rawSelectOptions.sort(Comparator.comparing(LabelLongValue::getLable));
        selectOptions = Collections.unmodifiableList(rawSelectOptions);
    }

    private SectorUtil() { }

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


