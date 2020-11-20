package org.xcolab.commons.html;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class LabelLongValue {

    private Long value;
    private String lable;

    public LabelLongValue(Long value, String lable) {
        this.value = value;
        this.lable = lable;
    }

    public static Collection<LabelLongValue> fromMap(Map<Long, String> labelToValueMap) {
        return labelToValueMap.entrySet().stream()
                .map(entry -> new LabelLongValue(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }


    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }
}
