package com.example.solar.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "stations")
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StationPanel> panels;

    public Station(){}

    private Station(Builder builder) {
        this.name = builder.name;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.panels = builder.panels;
    }

    public static Builder builder(){return new Builder();}

    public static class Builder{
        private String name;
        private Double latitude;
        private Double longitude;
        private List<StationPanel> panels;
        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder latitude(Double latitude){
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(Double longitude){
            this.longitude = longitude;
            return this;
        }

        public Builder panels(List<StationPanel> panels){
            this.panels = panels;
            return this;
        }
        public Station build(){return new Station(this);}
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public List<StationPanel> getPanels() {
        return panels;
    }

    public void setPanels(List<StationPanel> panels) {
        this.panels = panels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(id, station.id) && Objects.equals(name, station.name) && Objects.equals(latitude, station.latitude) && Objects.equals(longitude, station.longitude) && Objects.equals(panels, station.panels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, latitude, longitude, panels);
    }
}
