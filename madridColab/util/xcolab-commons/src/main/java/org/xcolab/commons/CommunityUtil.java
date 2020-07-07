package org.xcolab.commons;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import org.xcolab.commons.html.LabelLongValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class CommunityUtil {
    private static final BidiMap<Long, String> codeToCommunityMap = new DualHashBidiMap<>();
    private static final List<LabelLongValue> selectOptions;

    // Role list adapted from countries.csv by Google licensed under CC BY 3.0
    // Material: https://developers.google.com/public-data/docs/canonical/countries_csv
    // License: https://creativecommons.org/licenses/by/3.0/
    static {
        codeToCommunityMap.put(0l, "Alumno");
        codeToCommunityMap.put(1l, "Personal Docente Investigador (PDI)");
        codeToCommunityMap.put(2l, "Personal de Administración y Servicios (PAS)");
        codeToCommunityMap.put(3l, "UPM, ninguno de los anteriores");
        codeToCommunityMap.put(4l, "No UPM");

        List<LabelLongValue> rawSelectOptions = new ArrayList<>(
                LabelLongValue.fromMap(codeToCommunityMap));
        rawSelectOptions.sort(Comparator.comparing(LabelLongValue::getLable));
        selectOptions = Collections.unmodifiableList(rawSelectOptions);
    }

    private CommunityUtil() { }

    public static String getCommunityForCode(Long code) {
        if (codeToCommunityMap.containsKey(code)) {
            return codeToCommunityMap.get(code);
        }
        return "";
    }

    public static Long getCodeForCommunity(String community) {
        if (codeToCommunityMap.containsValue(community)) {
            return codeToCommunityMap.getKey(community);
        }
        return 5l;
    }

    public static List<LabelLongValue> getSelectOptions() {
        return selectOptions;
    }
}

