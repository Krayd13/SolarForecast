package com.example.solar.model;

import jakarta.persistence.*;

import java.util.Objects;


@Entity
@Table(name = "station_panels")
public class StationPanel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;
    private Integer azimuth;
    private Integer tilt;
    private Integer capacity;

    public StationPanel(){}

    private StationPanel(Builder builder){
        this.station = builder.station;
        this.azimuth = builder.azimuth;
        this.tilt = builder.tilt;
        this.capacity = builder.capacity;
    }

    public static Builder builder(){return new Builder();}

    public static class Builder{
        private Station station;
        private Integer azimuth;
        private Integer tilt;
        private Integer capacity;
        public Builder station(Station station){
            this.station = station;
            return this;
        }

        public Builder azimuth(Integer azimuth){
            this.azimuth = azimuth;
            return this;
        }

        public Builder tilt(Integer tilt){
            this.tilt = tilt;
            return this;
        }

        public Builder capacity(Integer capacity){
            this.capacity = capacity;
            return this;
        }
        public StationPanel build(){return new StationPanel(this);}
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

    public Integer getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(Integer azimuth) {
        this.azimuth = azimuth;
    }

    public Integer getTilt() {
        return tilt;
    }

    public void setTilt(Integer tilt) {
        this.tilt = tilt;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationPanel that = (StationPanel) o;
        return Objects.equals(id, that.id) && Objects.equals(station, that.station) && Objects.equals(azimuth, that.azimuth) && Objects.equals(tilt, that.tilt) && Objects.equals(capacity, that.capacity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, station, azimuth, tilt, capacity);
    }
}
