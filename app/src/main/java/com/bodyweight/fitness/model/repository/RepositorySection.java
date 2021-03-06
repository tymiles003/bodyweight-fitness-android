package com.bodyweight.fitness.model.repository;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class RepositorySection extends RealmObject {
    @PrimaryKey @Required
    private String id;

    @Index
    private String sectionId;

    private String title;
    private String mode;

    private RepositoryRoutine routine;
    private RepositoryCategory category;

    private RealmList<RepositoryExercise> exercises = new RealmList<>();

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public void setRoutine(RepositoryRoutine routine) {
        this.routine = routine;
    }

    public RepositoryRoutine getRoutine() {
        return routine;
    }

    public void setCategory(RepositoryCategory category) {
        this.category = category;
    }

    public RepositoryCategory getCategory() {
        return category;
    }

    public void setExercises(RealmList<RepositoryExercise> exercises) {
        this.exercises = exercises;
    }

    public RealmList<RepositoryExercise> getExercises() {
        return exercises;
    }
}
