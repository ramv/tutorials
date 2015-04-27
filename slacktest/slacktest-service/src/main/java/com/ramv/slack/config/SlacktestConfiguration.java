package com.ramv.slack.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class SlacktestConfiguration extends Configuration {

    @NotNull
    @JsonProperty
    private String sampleProperty;

    @Valid
    @NotNull
    @JsonProperty
    private DataSourceFactory database = new DataSourceFactory();

    public String getSampleProperty() {
        return sampleProperty;
    }

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    public void setDataSourceFactory(DataSourceFactory database) {
        this.database = database;
    }

}
