package org.xcolab.client.contest.pojo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.core.ParameterizedTypeReference;

import org.xcolab.util.http.client.types.TypeProvider;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContestFusion implements Serializable {

    private static final long serialVersionUID = -416641117;


    public static final TypeProvider<ContestFusion> TYPES =
            new TypeProvider<>(ContestFusion.class,
                    new ParameterizedTypeReference<List<ContestFusion>>() {
                    });

    private Long id_fusion;
    private Long id_contest_1;
    private Long id_contest_2;

    public ContestFusion() {}

    public ContestFusion(ContestFusion value) {   
        this.id_fusion = value.id_fusion;
        this.id_contest_1 = value.id_contest_1;
        this.id_contest_2 = value.id_contest_2;
    }

    public ContestFusion(Long id_fusion, Long id_contest_1, Long id_contest_2) {
        this.id_fusion = id_fusion;
        this.id_contest_1 = id_contest_1;
        this.id_contest_2 = id_contest_2;
    }


    public Long getIdFusion() {
        return id_fusion;
    }

    public Long getContestId_1() {
        return id_contest_1;
    }

    public Long getContestId_2() {
        return id_contest_2;
    }

    public void setIdFusion(Long id_fusion) {
        this.id_fusion = id_fusion;
    }

    public void setContestId_1(Long id_contest_1) {
        this.id_contest_1 = id_contest_1;
    }

    public void setContestId_2(Long id_contest_2) {
        this.id_contest_2 = id_contest_2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ContestFusion
                that = (ContestFusion) o;
        return Objects.equals(id_fusion, that.id_fusion) &&
                Objects.equals(id_contest_1, that.id_contest_1) &&
                Objects.equals(id_contest_2, that.id_contest_2);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(id_fusion, id_contest_1, id_contest_2);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id_fusion", id_fusion)
                .append("id_contest_1", id_contest_1)
                .append("id_contest_2", id_contest_2)
                .toString();
    }
}
