package com.kloc.unistore.di


import com.kloc.unistore.firestoredb.repository.EmployeeRepository
import com.kloc.unistore.firestoredb.repository.EmployeeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule
{

    @Binds
    abstract fun providesQuestionRepository(
        repo: EmployeeRepositoryImpl
    ): EmployeeRepository
}