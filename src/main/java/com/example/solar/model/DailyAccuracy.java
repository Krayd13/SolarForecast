package com.example.solar.model;

import com.example.solar.SourceNames;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "forecast_accuracy")
public class DailyAccuracy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long stationId;
    private SourceNames sourceName;
    private LocalDate date;
    private Double mape;
    private Double rmse;

    public DailyAccuracy() {
    }

    public static Builder builder() {
        return new Builder();
    }

    private DailyAccuracy(Builder builder) {
        this.stationId = builder.stationId;
        this.sourceName = builder.sourceName;
        this.date = builder.date;
        this.mape = builder.mape;
        this.rmse = builder.rmse;
    }

    public static class Builder {
        private Long stationId;
        private SourceNames sourceName;
        private LocalDate date;
        private Double mape;
        private Double rmse;

        public Builder stationId(Long stationId) {
            this.stationId = stationId;
            return this;
        }

        public Builder sourceName(SourceNames sourceName) {
            this.sourceName = sourceName;
            return this;
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder mape(Double mape) {
            this.mape = mape;
            return this;
        }

        public Builder rmse(Double rmse) {
            this.rmse = rmse;
            return this;
        }

        public DailyAccuracy build() {
            return new DailyAccuracy(this);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public SourceNames getSourceName() {
        return sourceName;
    }

    public void setSourceName(SourceNames sourceName) {
        this.sourceName = sourceName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getMape() {
        return mape;
    }

    public void setMape(Double mape) {
        this.mape = mape;
    }

    public Double getRmse() {
        return rmse;
    }

    public void setRmse(Double rmse) {
        this.rmse = rmse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyAccuracy that = (DailyAccuracy) o;
        return Objects.equals(id, that.id) && Objects.equals(stationId, that.stationId) && sourceName == that.sourceName && Objects.equals(date, that.date) && Objects.equals(mape, that.mape) && Objects.equals(rmse, that.rmse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stationId, sourceName, date, mape, rmse);
    }
}
