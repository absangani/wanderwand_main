package com.example.wanderwand.roompersistence;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import static androidx.lifecycle.ViewModelProvider.Factory;

/**
 * Factory for ViewModels
 */
public class ViewModelFactory implements Factory {
    private final ChecklistDataSource mDataSource;

    public ViewModelFactory(ChecklistDataSource dataSource) {
        mDataSource = dataSource;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ChecklistViewModel.class)) {
            return (T) new ChecklistViewModel(mDataSource);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

