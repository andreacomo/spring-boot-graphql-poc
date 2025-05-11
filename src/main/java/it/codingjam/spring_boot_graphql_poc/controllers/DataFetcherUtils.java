package it.codingjam.spring_boot_graphql_poc.controllers;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.SelectedField;

import java.util.Set;
import java.util.stream.Collectors;

public class DataFetcherUtils {

    public static Set<String> getSelectedFieldNames(DataFetchingEnvironment env) {
        return env.getSelectionSet().getFields().stream()
                .map(SelectedField::getFullyQualifiedName)
                .collect(Collectors.toSet());
    }
}
