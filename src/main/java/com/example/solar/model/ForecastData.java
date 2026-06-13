package com.example.solar.model;

import com.example.solar.SourceNames;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "forecasts")
public class ForecastData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;
    private LocalDateTime timestamp;
    @Enumerated(EnumType.STRING)
    private SourceNames sourceName;
    private Double value;

    public ForecastData(){}

    private ForecastData(Builder builder){
        this.station = builder.station;
        this.timestamp = builder.timestamp;
        this.sourceName = builder.sourceName;
        this.value = builder.value;
    }

    public static Builder builder(){return new Builder();}

    public static class Builder{
        private Station station;
        private LocalDateTime timestamp;
        private SourceNames sourceName;
        private Double value;

        public Builder station(Station station){
            this.station = station;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp){
            this.timestamp = timestamp;
            return this;
        }

        public Builder sourceName(SourceNames sourceName){
            this.sourceName = sourceName;
            return this;
        }

        public Builder value(Double value){
            this.value = value;
            return this;
        }

        public ForecastData build(){return new ForecastData(this);}
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public SourceNames getSourceName() {
        return sourceName;
    }

    public void setSourceName(SourceNames sourceName) {
        this.sourceName = sourceName;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForecastData that = (ForecastData) o;
        return Objects.equals(id, that.id) && Objects.equals(station, that.station) && Objects.equals(timestamp, that.timestamp) && sourceName == that.sourceName && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, station, timestamp, sourceName, value);
    }
}
