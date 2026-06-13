package com.example.solar.model;

import com.example.solar.SourceNames;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;
    @Enumerated(EnumType.STRING)
    private SourceNames sourceName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;

    public Task() {}

    private Task(Builder builder) {
        this.station = builder.station;
        this.sourceName = builder.sourceName;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.isActive = builder.isActive;
    }

    public static Builder builder(){return new Builder();}

    public static class Builder{
        private Station station;
        private SourceNames sourceName;
        private LocalDate startDate;
        private LocalDate endDate;
        private Boolean isActive;
        public Builder station(Station station){
            this.station = station;
            return this;
        }

        public Builder sourceName(SourceNames sourceName){
            this.sourceName = sourceName;
            return this;
        }

        public Builder startDate(LocalDate startDate){
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate){
            this.endDate = endDate;
            return this;
        }

        public Builder isActive(Boolean isActive){
            this.isActive = isActive;
            return this;
        }
        public Task build(){return new Task(this);}
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

    public SourceNames getSourceName() {
        return sourceName;
    }

    public void setSourceName(SourceNames sourceName) {
        this.sourceName = sourceName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(station, task.station) && Objects.equals(sourceName, task.sourceName) && Objects.equals(startDate, task.startDate) && Objects.equals(endDate, task.endDate) && Objects.equals(isActive, task.isActive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, station, sourceName, startDate, endDate, isActive);
    }
}
