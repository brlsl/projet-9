package com.openclassrooms.realestatemanager.controllers.activities;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.models.Agent;
import com.openclassrooms.realestatemanager.models.Image;
import com.openclassrooms.realestatemanager.models.Property;
import com.openclassrooms.realestatemanager.repositories.AgentDataRepository;
import com.openclassrooms.realestatemanager.repositories.ImageDataRepository;
import com.openclassrooms.realestatemanager.repositories.PropertyDataRepository;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

public class AddPropertyActivityViewModel extends ViewModel {

    private final PropertyDataRepository propertyDataRepository;
    private final ImageDataRepository imageDataRepository;
    private final Executor executor; // permits to realize asynchronous requests

    private MutableLiveData<Date> mDateSelected;

    private MutableLiveData<List<Bitmap>> mBitmapList;

    private MutableLiveData<Agent> mAgent;


    private MutableLiveData<List<String>> mPathList;


    public AddPropertyActivityViewModel(PropertyDataRepository propertyDataRepository, ImageDataRepository imageDataRepository, Executor executor) {

        this.propertyDataRepository = propertyDataRepository;
        this.imageDataRepository = imageDataRepository;
        this.executor = executor;
    }

    // ---------
    // PROPERTY
    // ---------

    public void createProperty(Property property){
        executor.execute(()->
                propertyDataRepository.createProperty(property));
    }

    public LiveData<List<Property>> getPropertyList(){
        return propertyDataRepository.getPropertyList();
    }

    // ---------
    // IMAGE
    // ---------

    public void createImage(Image image){
        executor.execute(() -> {
            imageDataRepository.createImage(image);
        });
    }

    // -------------------

    public MutableLiveData<Agent> getAgent(){
        if (mAgent == null) {
            mAgent = new MutableLiveData<>();
        }
        return mAgent;
    }


    public MutableLiveData<Date> getDateSelected(){
        if (mDateSelected == null) {
            mDateSelected = new MutableLiveData<>();
        }
        return mDateSelected;
    }


    public MutableLiveData<List<Bitmap>> getBitmapList() {
        if (mBitmapList == null){
            mBitmapList = new MutableLiveData<>();
        }
        return mBitmapList;
    }

    public MutableLiveData<List<String>> getPathList() {
        if (mPathList == null){
            mPathList = new MutableLiveData<>();
        }

        return mPathList;
    }

}
