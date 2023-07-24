package com.cpan252.tekkenreborn.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// jakarta == javax
// By using the @Table annotation, we are telling Spring Data to map the table
// name to the table

@Slf4j
@Entity
public class Fighter {
    public enum Anime {
        NARUTO("Naruto"), BLEACH("Bleach"), ONE_PIECE("One Piece"), TEKKEN("Tekken");

        @JsonValue
        @NotBlank
        private String title;

        private Anime(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    // Using JPA, we have to provide correct @Id annotation and use @GeneratedValue
    // to auto-generate the id, see the imports
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name;
    @Max(100)
    private int damagePerHit;
    @Min(1000)
    private int health;
    @DecimalMin(value = "0.1", inclusive = true)
    @DecimalMax(value = "10.0", inclusive = true)
    private BigDecimal resistance;

    private Anime animeFrom;

    @Column(nullable = true, length = 64)
    private String image;

    @Builder.Default
    private LocalDate createdAt = LocalDate.now();

    @Transient
    public String getPhotosImagePath() {
        if (image == null) return "";
        log.info("log path:  {}", "/fighter-photos/" + id + "/" + image);
        return "fighter-photos/" + id + "/" + image;
    }
}
